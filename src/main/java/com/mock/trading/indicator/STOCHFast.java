package com.mock.trading.indicator;

import com.tictactec.ta.lib.Core;
import com.tictactec.ta.lib.MAType;
import com.tictactec.ta.lib.MInteger;

public class STOCHFast {

	public static IndicatorOutHolder get(double[] inHigh, double[] inLow, double[] inClose) {
		return get(14, 3, inHigh, inLow, inClose, MAType.Sma);
	}

	/**
	 * size returned: total array size, minus length for fast stock, minus 1 (0
	 * index), for 14,3, it will return starting at 15th index, or 16th element.
	 * 14-1+3-1 = 15th. or 16th element
	 */
	public static IndicatorOutHolder get(int kFastPeriod, int kSlowPeriod,
			double[] inHigh, double[] inLow, double[] inClose, MAType maType) {

		if (inHigh == null) {
			return null;
		}

		Core core = new Core();

		int lookback = core.stochFLookback(kFastPeriod, kSlowPeriod, maType);
		if (inHigh.length < lookback) {
			return null;
		}

		int startIdx = 0;
		int endIdx = inHigh.length - 1;

		MInteger outBegIdx = new MInteger();
		MInteger outNbElement = new MInteger();
		double[] outK = new double[inHigh.length - lookback];
		double[] outD = new double[inHigh.length - lookback];

		core.stochF(startIdx, endIdx, inHigh, inLow, inClose, kFastPeriod,
				kSlowPeriod, MAType.Sma, outBegIdx, outNbElement, outK, outD);
		return new IndicatorOutHolder(lookback, outK, outD);
	}

}
