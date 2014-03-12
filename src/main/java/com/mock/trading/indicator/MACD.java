package com.mock.trading.indicator;

import java.util.List;

import com.mock.trading.domain.BarHolder;
import com.mock.trading.pb.TradePb.bar_pb;
import com.mock.trading.pb.TradePb.macd_cross_pbe;
import com.mock.trading.pb.TradePb.macd_pb;
import com.tictactec.ta.lib.Core;
import com.tictactec.ta.lib.MAType;
import com.tictactec.ta.lib.MInteger;

public class MACD {

	// default: fast=12, slow=26, signal=9
	// alt: fast=3, slow=10, signal=16

	Core core = new Core();
	private int fastLength;
	private int slowLength;
	private int signalLength;
	public int lookback;

	public enum CROSS {
		CROSS_ABOVE, CROSS_BELOW, NONE
	};

	public BarHolder barHolder = new BarHolder();

	public double[] macd;
	public double[] signal;
	public double[] macdhist;
	public int len = 0;

	public MACD(List<bar_pb> pbs) {
		this(12, 26, 9, pbs);
	}

	public MACD() {
		this(12, 26, 9, null);
	}

	public MACD(int fast, int slow, int signal, List<bar_pb> pbs) {
		this.lookback = this.core.macdExtLookback(fast, MAType.Ema, slow,
				MAType.Ema, signal, MAType.Ema);
		this.fastLength = fast;
		this.slowLength = slow;
		this.signalLength = signal;
		if (pbs != null) {
			this.barHolder.add(pbs);
			this._calc();
			this.len = pbs.size();
		}
	}

	public MACD(int fast, int slow, int signal) {
		this(fast, slow, signal, null);
	}

	public void add(bar_pb pb) {
		this.barHolder.add(pb);
	}

	public void add(List<bar_pb> pbs) {
		this.barHolder.add(pbs);
	}

	public void addCalc(bar_pb pb) {
		add(pb);
		if (dataSize() >= lookback)
			_calc();
	}

	public void addCalc(List<bar_pb> pbs) {
		this.barHolder.add(pbs);
		if (dataSize() >= lookback)
			_calc();
	}

	private void _calc() {
		get(MAType.Ema, this.fastLength, MAType.Ema, this.slowLength,
				MAType.Ema, this.signalLength, this.barHolder.close());
	}

	public macd_cross_pbe crossing() {
		return crossing(this.len);
	}

	public macd_cross_pbe crossing(int l) {
		if (this.macd.length < 2 || l < 2)
			return macd_cross_pbe.none;

		// cross above - macd over signal
		if (this.macd[l] > this.signal[l]
				&& this.macd[l - 1] < this.signal[l - 1]) {
			return macd_cross_pbe.cross_above;
		} else if (this.macd[l] < this.signal[l]
				&& this.macd[l - 1] > this.signal[l - 1]) {
			return macd_cross_pbe.cross_below;
		}
		return macd_cross_pbe.none;
	}

	public macd_pb getPb(int index) {
		if (index > this.macd.length)
			return null;

		macd_pb.Builder macd_b = macd_pb.newBuilder();
		macd_b.setMacd(this.macd[index]);
		macd_b.setSignal(this.signal[index]);
		macd_b.setMacdhist(this.macdhist[index]);
		macd_b.setCross(crossing(index));
		return macd_b.build();
	}

	public int dataSize() {
		return barHolder.size();
	}

	public int size() {
		if (macd != null)
			return macd.length;
		return 0;
	}

	public void get(MAType fastMaType, int fastPeriod, MAType slowMaType,
			int slowPeriod, MAType signalMaType, int signalPeriod,
			double[] inReal) {
		if (inReal.length < lookback) {
			return;
		}

		int startIdx = 0;
		int endIdx = inReal.length - 1;

		MInteger outBegIdx = new MInteger();
		MInteger outNbElement = new MInteger();
		double[] outMACD = new double[inReal.length - lookback];
		double[] outSignal = new double[inReal.length - lookback];
		double[] outMACDHist = new double[inReal.length - lookback];

		this.core.macdExt(startIdx, endIdx, inReal, fastPeriod, fastMaType,
				slowPeriod, slowMaType, signalPeriod, signalMaType, outBegIdx,
				outNbElement, outMACD, outSignal, outMACDHist);
		this.macd = outMACD;
		this.signal = outSignal;
		this.macdhist = outMACDHist;
	}

	public static void main(String[] args) throws Exception {
	}

}
