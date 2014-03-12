package com.mock.trading.ui;

import java.awt.Component;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

import net.miginfocom.swing.MigLayout;

import com.google.common.collect.Lists;
import com.jidesoft.grid.FilterableTableModel;
import com.jidesoft.grid.SortableTable;
import com.mock.api.ATickApi;
import com.mock.trading.chart.TickCandleChart;
import com.mock.trading.domain.enumeration.BAR_TYPE;
import com.mock.trading.pb.TradePb.tick_pb;
import com.mock.trading.pb.TradePb.trade_pb;
import com.mock.trading.util.T;
import com.mock.ui.MockUi;

@SuppressWarnings("serial")
public class UITick extends JPanel implements ActionListener {
	static JTextField symbol;
	static JTextField date;
	JTextField hourStart;
	JTextField hourEnd;
	static TickTableModel tableModel;
	JScrollPane scrollPane;
	static TickCandleChart tickTradeChart;
	static Map<String, List<tick_pb>> cache = new HashMap<String, List<tick_pb>>();
	SortableTable sortableTable;
	static private Map<Long, Double> tradesMap = new HashMap<>();

	public static void setTrade(List<trade_pb> trades) {
		tradesMap.clear();
		for (trade_pb t : trades) {
			tradesMap.put(t.getTs(), t.getPrice());
		}
	}

	class CustomTable extends SortableTable {

		public CustomTable(TableModel model) {
			super(model);
		}

		@Override
		public Component prepareRenderer(TableCellRenderer renderer, int row,
				int column) {
			Component c = super.prepareRenderer(renderer, row, column);

			// if (!isRowSelected(row))
			// c.setBackground(row % 2 == 0 ? getBackground() : Color.RED);
			// tick_pb tick = tableModel.getRow(row);
			// if (tradesMap.get(tick.getTs()) != null ||
			// tradesMap.get(tick.getTs()+1) != null) {
			// c.setFont(c.getFont().deriveFont(Font.BOLD));
			// double p = tradesMap.get(tick.getTs());
			// if (p == 0)
			// p = tradesMap.get(tick.getTs()+1);
			// if (T.d2(tick.getBid()) == p){
			// //c.setBackground(Color.RED);
			// }
			// }else{
			// c.setFont(c.getFont().deriveFont(Font.PLAIN));
			// //c.setBackground(Color.WHITE);
			// }
			return c;
		}
	}

	@SuppressWarnings("unqualified-field-access")
	public UITick() {

		setLayout(new MigLayout("", "", ""));

		tableModel = new TickTableModel();
		FilterableTableModel filteredModel = new FilterableTableModel(
				tableModel);

		sortableTable = new SortableTable(filteredModel);
		sortableTable.changeSelection(0, 0, false, false);
		sortableTable.setPreferredScrollableViewportSize(Toolkit
				.getDefaultToolkit().getScreenSize());
		scrollPane = new JScrollPane(sortableTable);

		JPanel cmdPanel = new JPanel(new MigLayout());

		symbol = new JTextField(10);
		date = new JTextField(10);
		hourStart = new JTextField(3);
		hourEnd = new JTextField(3);
		JButton btnActivate = new JButton("Show Tick");

		hourStart.setText("930");
		hourEnd.setText("16");
		date.setText("20130802");
		symbol.setText("JPM");

		cmdPanel.add(btnActivate);
		cmdPanel.add(new JLabel("Symbol:"));
		cmdPanel.add(symbol);
		cmdPanel.add(new JLabel("Date:"));
		cmdPanel.add(date);
		cmdPanel.add(new JLabel("Hours:"));
		cmdPanel.add(hourStart);
		cmdPanel.add(new JLabel("-"));
		cmdPanel.add(hourEnd);

		btnActivate.addActionListener(this);

		add(cmdPanel, "wrap");
		add(scrollPane, "span");
	}

	public void init() {
	}

	public static void showTick(String symbol, String dateStr, Integer start, Integer end, boolean showGraph) {
		List<tick_pb> ticks = null;
		String sym = symbol;
		int date = new Integer(dateStr).intValue();
			ticks = ATickApi.getTickByDate(sym, new Integer(date), false);
			if (ticks.isEmpty()) {
				return;
		}
			

		List<tick_pb> filter = new ArrayList<tick_pb>();
		for (tick_pb t : ticks){
			if (T.isMarketHour(t.getTs(), false)) {
				filter.add(t);
			}
		}
		
		tableModel.setTicks(filter);

		MockUi.getToolWindowManager().getContentManager().getContent("Tick")
				.setSelected(true);

		if (showGraph) {
			List<trade_pb> trades = Lists.newArrayList();
			if (tickTradeChart == null) {
				tickTradeChart = new TickCandleChart(ATickApi.getBar(filter,
						BAR_TYPE.SECOND), trades);
			} else {
				tickTradeChart.setData(
						ATickApi.getBar(filter, BAR_TYPE.FIVE_SECOND), trades);
			}
			tickTradeChart.view();
		}
	}

	@SuppressWarnings({ "unqualified-field-access", "boxing" })
	@Override
	public void actionPerformed(ActionEvent e) {
		showTick(symbol.getText().toUpperCase(), date.getText(), new
				Integer(hourStart.getText()), new Integer(hourEnd.getText()), true);
	}
}
