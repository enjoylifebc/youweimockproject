package com.mock.trading.chart;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.CandlestickRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.ohlc.OHLCSeries;
import org.jfree.data.time.ohlc.OHLCSeriesCollection;
import org.jfree.data.xy.OHLCDataset;
import org.jfree.util.ShapeUtilities;
import org.joda.time.DateTime;

import com.mock.api.ATickApi;
import com.mock.trading.domain.enumeration.BAR_TYPE;
import com.mock.trading.pb.TradePb.bar_pb;
import com.mock.trading.pb.TradePb.tick_pb;
import com.mock.trading.pb.TradePb.trade_pb;
import com.mock.trading.util.T;

/**
 * A demo showing a time series with OHLC data for tick data.
 * 
 * Transaction (trade) data can be overlayed to this chart. 
 * 
 * Author: Youwei Chen
 * 
 */
@SuppressWarnings("serial")
public class TickCandleChart extends JFrame {

	OHLCDataset ohlcDataSet;
	OHLCSeries ohlcSeries = new OHLCSeries("");;

	OHLCSeriesCollection ohlcCollection = new OHLCSeriesCollection();

	TimeSeries buySerie = new TimeSeries("buy");
	TimeSeries sellSerie = new TimeSeries("sell");

	final ChartPanel chartPanel;
	Date start;
	Date end;
	double min = Double.MAX_VALUE;
	double max = Double.MIN_VALUE;

	DateAxis domainAxis = new DateAxis("Date");
	NumberAxis rangeAxis = new NumberAxis("Price");

	public void setData(List<bar_pb> bars, List<trade_pb> trades) {

		if (bars.isEmpty())
			return;

		min = Double.MAX_VALUE;
		max = Double.MIN_VALUE;

		ohlcSeries.clear();

		start = new DateTime(bars.get(0).getTs() * 1000).toDate();
		end = new DateTime(bars.get(bars.size() - 1).getTs() * 1000).toDate();

		Date[] date = new Date[bars.size()];
		double[] open = new double[bars.size()];
		double[] close = new double[bars.size()];
		double[] high = new double[bars.size()];
		double[] low = new double[bars.size()];
		double[] volume = new double[bars.size()];
		int i = 0;

		for (bar_pb bar : bars) {
			date[i] = new Date(bar.getTs() * 1000);
			open[i] = bar.getOpen();
			close[i] = bar.getClose();
			high[i] = bar.getHigh();
			low[i] = bar.getLow();
			volume[i] = bar.getVolume();

			min = Math.min(min, low[i]);
			max = Math.max(max, high[i]);

			Calendar d = Calendar.getInstance();
			d.setTime(date[i]);
			Second second = new Second(d.get(Calendar.SECOND),
					d.get(Calendar.MINUTE), d.get(Calendar.HOUR_OF_DAY),
					d.get(Calendar.DAY_OF_MONTH), d.get(Calendar.MONTH) + 1,
					d.get(Calendar.YEAR));

			ohlcSeries.add(second, open[i], high[i], low[i], close[i]);
			i++;
		}
		ohlcCollection.addSeries(this.ohlcSeries);
		this.ohlcDataSet = ohlcCollection;

		buySerie.clear();
		sellSerie.clear();

//		for (trade_pb t : trades) {
//			if (T.isBuy(t.getSide())) {
//				buySerie.addOrUpdate(new Second(new Date(t.getTs() * 1000)),
//						new Double(t.getPrice()));
//			} else {
//				sellSerie.addOrUpdate(new Second(new Date(t.getTs() * 1000)),
//						new Double(t.getPrice()));
//			}
//		}

		domainAxis.setRange(start, end);
		rangeAxis.setRange(min, max);

	}

	public TickCandleChart(List<bar_pb> bars, List<trade_pb> trades) {

		super("Tick Candle Chart");

		setData(bars, trades);

		CandlestickRenderer renderer1 = new CandlestickRenderer();
		renderer1.setUpPaint(Color.WHITE);
		renderer1.setDownPaint(Color.BLACK);
		renderer1.setUseOutlinePaint(true);

		XYPlot plot = new XYPlot(ohlcDataSet, domainAxis, rangeAxis, renderer1);

		TimeSeriesCollection buyCollection = new TimeSeriesCollection();
		TimeSeriesCollection sellCollection = new TimeSeriesCollection();

		buyCollection.addSeries(buySerie);
		XYLineAndShapeRenderer r = new XYLineAndShapeRenderer();
		r.setSeriesPaint(1, Color.GREEN);
		r.setSeriesShape(1, ShapeUtilities.createDiamond(10)); // set second
		r.setSeriesShapesVisible(1, true);
		r.setSeriesLinesVisible(1, false);
		plot.setRenderer(1, r);
		plot.setDataset(1, buyCollection);

		sellCollection.addSeries(sellSerie);
		XYLineAndShapeRenderer sell = new XYLineAndShapeRenderer();
		sell.setSeriesPaint(2, Color.RED);
		sell.setSeriesShape(2, ShapeUtilities.createDiamond(10)); // set second
		sell.setSeriesShapesVisible(2, true);
		sell.setSeriesLinesVisible(2, false);
		plot.setRenderer(2, sell);
		plot.setDataset(2, sellCollection);

		CombinedDomainXYPlot cplot = new CombinedDomainXYPlot(domainAxis);
		cplot.add(plot, 3);
		cplot.setGap(8.0);
		cplot.setDomainGridlinePaint(Color.white);
		cplot.setDomainGridlinesVisible(true);
		cplot.setDomainPannable(true);

		JFreeChart chart = new JFreeChart("Tick chart",
				JFreeChart.DEFAULT_TITLE_FONT, cplot, false);

		chartPanel = new ChartPanel(chart);
		chartPanel.setPreferredSize(new java.awt.Dimension(1200, 600));
		chartPanel.setMouseZoomable(true, true);
		setContentPane(chartPanel);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		// setDefaultCloseOperation(EXIT_ON_CLOSE);
	}

	public JPanel getChart() {
		return chartPanel;
	}

	public void view() {
		this.setVisible(true);
		this.pack();
	}

	public static void main(final String[] args) {

		List<tick_pb> ticks = ATickApi.getTickByDate("JPM", 20130801);
		ticks = ATickApi.filterByTime(ticks, new DateTime(2013, 8, 1, 10, 30),
				new DateTime(2013, 8, 3, 11, 30));

		List<trade_pb> filtered = new ArrayList<>();
		List<bar_pb> bars = ATickApi.getBar(ticks, BAR_TYPE.SECOND);

		TickCandleChart chart = new TickCandleChart(bars, filtered);
		chart.view();

	}

}