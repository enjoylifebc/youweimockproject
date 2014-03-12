package com.mock.trading.indicator;

import com.tictactec.ta.lib.Core;
import com.tictactec.ta.lib.MInteger;

public class RSI {

	public static double[] get(int period, double[] inReal) {
    	Core core = new Core();
    	if (inReal.length == 0) {
            return null;
        }

        int lookback = core.rsiLookback(period);
        if (inReal.length < lookback) {
            return null;
        }

        int startIdx = 0;
        int endIdx = inReal.length - 1;

        MInteger outBegIdx = new MInteger();
        MInteger outNbElement = new MInteger();
        double[] outReal = new double[inReal.length - lookback];

        core.rsi(startIdx, endIdx, inReal, period, outBegIdx, outNbElement, outReal);
        return outReal;
    }

	public static void main(String[] args) throws Exception
	{
	}
}
