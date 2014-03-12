package com.mock.trading.indicator;

import com.tictactec.ta.lib.Core;
import com.tictactec.ta.lib.MInteger;

public class ATR {
    
    // period = 7;    
    public static double[] get(double[] inHigh, double[] inLow, double[] inClose, int period) {
        if (inHigh == null) {
            return null;
        }
       
        Core core =  new Core();
        int lookback = core.atrLookback(period);
        if (inHigh.length < lookback) {
            return null;
        }

        int startIdx = 0;
        int endIdx = inHigh.length - 1;

        MInteger outBegIdx = new MInteger();
        MInteger outNbElement = new MInteger();
        double[] outReal = new double[ - lookback];

        core.atr(startIdx, endIdx, inHigh, inLow, inClose, period, outBegIdx, outNbElement, outReal);

        return outReal;
    }

 }
