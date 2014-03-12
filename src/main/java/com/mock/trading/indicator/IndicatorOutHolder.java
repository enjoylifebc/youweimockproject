package com.mock.trading.indicator;

public class IndicatorOutHolder {

	public double[] out1;
	public double[] out2;
	public int lookback;

	public IndicatorOutHolder(int lookback, double[] k) {
		this.lookback = lookback;
		this.out1 = k;
	}
		
	public IndicatorOutHolder(int lookback, double[] k, double[] d) {
		this.lookback = lookback;
		this.out1 = k;
		this.out2 = d;
	}

}
