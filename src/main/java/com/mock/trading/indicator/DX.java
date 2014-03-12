package com.mock.trading.indicator;

import com.tictactec.ta.lib.Core;
import com.tictactec.ta.lib.MInteger;

public class DX
{

	public DX()
	{
	}

	// period = 7;
	public double[] dx(double[] inHigh, double[] inLow, double[] inClose, int period)
	{
		if (inHigh == null)
		{
			return null;
		}

		Core core = new Core();

		int lookback = core.dxLookback(period);
		if (inHigh.length < lookback)
		{
			return null;
		}

		int startIdx = 0;
		int endIdx = inHigh.length - 1;

		MInteger outBegIdx = new MInteger();
		MInteger outNbElement = new MInteger();
		double[] outReal = new double[inHigh.length - lookback];

		core.dx(startIdx, endIdx, inHigh, inLow, inClose, period, outBegIdx, outNbElement, outReal);
		return outReal;
	}

}
