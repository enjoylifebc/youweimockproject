package com.mock.trading.indicator;

import com.tictactec.ta.lib.Core;
import com.tictactec.ta.lib.MAType;
import com.tictactec.ta.lib.MInteger;

public class STOCH {

	public static IndicatorOutHolder get(double[] inHigh, double[] inLow, double[] inClose) {
		return get(14, 3, 3, inHigh, inLow, inClose, MAType.Sma);
	}

	public static IndicatorOutHolder get833(double[] inHigh, double[] inLow,
			double[] inClose) {
		return get(8, 3, 3, inHigh, inLow, inClose, MAType.Sma);
	}

	// kFastPeriod kSlowPeriod; dPeriod=14
	public static IndicatorOutHolder get(int kFastPeriod, int kSlowPeriod, int dPeriod,
			double[] inHigh, double[] inLow, double[] inClose, MAType maType) {

		if (inHigh == null) {
			return null;
		}

		Core core = new Core();

		int lookback = core.stochLookback(kFastPeriod, kSlowPeriod, maType,
				dPeriod, maType);
		if (inHigh.length < lookback) {
			return null;
		}

		int startIdx = 0;
		int endIdx = inHigh.length - 1;

		MInteger outBegIdx = new MInteger();
		MInteger outNbElement = new MInteger();
		double[] outK = new double[inHigh.length - lookback];
		double[] outD = new double[inHigh.length - lookback];

		core.stoch(startIdx, endIdx, inHigh, inLow, inClose, kFastPeriod,
				kSlowPeriod, maType, dPeriod, maType, outBegIdx, outNbElement,
				outK, outD);

		return new IndicatorOutHolder(lookback, outK, outD);
	}

}
