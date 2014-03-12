package com.mock.trading.indicator;

import com.tictactec.ta.lib.Core;
import com.tictactec.ta.lib.MAType;
import com.tictactec.ta.lib.MInteger;

public class MA {

    public static double[] ma(double[] inReal, int period, MAType type) {    
    	
    	Core core = new Core();
    	int lookback = core.movingAverageLookback(period, type);
    	if (inReal.length < lookback) {
            return null;
        }    	       

        int startIdx = 0;
        int endIdx = inReal.length - 1;

        MInteger outBegIdx = new MInteger();
        MInteger outNbElement = new MInteger();
        double[] outReal = new double[inReal.length - lookback];

        core.movingAverage(startIdx, endIdx, inReal, period, type, outBegIdx, outNbElement, outReal);
        return outReal;
    }  
}
