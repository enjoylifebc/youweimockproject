package com.mock.trading.indicator;

import com.tictactec.ta.lib.Core;
import com.tictactec.ta.lib.MInteger;

public class ADX {

	public static double[] get(int period, double[] inHigh, double[] inLow,
			double[] inClose) {

		return getDefault(period, inHigh, inLow, inClose).out1;
	}

	/**
	 *  adx lookback = period * 2 - 1 
	 */
	public static IndicatorOutHolder getDefault(int period, double[] inHigh,
			double[] inLow, double[] inClose) {
		Core core = new Core();

		int lookback = core.adxLookback(period);
		if (inHigh.length < lookback) {
			return null;
		}

		int startIdx = 0;
		int endIdx = inHigh.length - 1;

		MInteger outBegIdx = new MInteger();
		MInteger outNbElement = new MInteger();
		double[] outReal = new double[inHigh.length - lookback];
		core.adx(startIdx, endIdx, inHigh, inLow, inClose, period, outBegIdx,
				outNbElement, outReal);
		return new IndicatorOutHolder(lookback, outReal);
	}
}
