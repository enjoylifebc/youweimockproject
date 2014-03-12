package com.mock.trading.domain;

import java.util.ArrayList;
import java.util.List;

import com.google.common.primitives.Doubles;

public class OHLCHolder {

	List<Ohlcs> ohlcs = new ArrayList<>();

	List<Double> high = new ArrayList<>();
	List<Double> low = new ArrayList<>();
	List<Double> open = new ArrayList<>();
	List<Double> close = new ArrayList<>();

	public void add(Ohlcs ohlc) {
		this.ohlcs.add(ohlc);
		this.high.add(ohlc.high);
		this.low.add(ohlc.low);
		this.open.add(ohlc.open);
		this.close.add(ohlc.close);
	}

	public double[] open() {
		return Doubles.toArray(this.open);
	}

	public double[] close() {
		return Doubles.toArray(this.close);
	}

	public double[] high() {
		return Doubles.toArray(this.high);
	}

	public double[] low() {
		return Doubles.toArray(this.low);
	}
}