package com.mock.trading.domain;

import java.util.ArrayList;
import java.util.List;

import com.google.common.primitives.Doubles;
import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;
import com.mock.trading.pb.TradePb.bar_pb;

@SuppressWarnings("boxing")
public class BarHolder {
		
	public List<Double> close = new ArrayList<Double>();
	public List<Double> open = new ArrayList<Double>();
	public List<Double> high = new ArrayList<Double>();
	public List<Double> low = new ArrayList<Double>();
	public List<Integer> volume = new ArrayList<Integer>();
	public List<Long> time = new ArrayList<Long>();
	public List<bar_pb> bars = new ArrayList<bar_pb>();
	
	public BarHolder(){	
	}
	
	public BarHolder(List<bar_pb> bars){
		this.add(bars);
		this.bars.addAll(bars);
	}
	
	public void add(List<bar_pb> bars){
		for (bar_pb bar: bars)
			add(bar);
		
		this.bars.addAll(bars);
		
	}
	
	public void add(bar_pb bar){
		this.close.add(new Double(""+bar.getClose()));
		this.open.add(new Double(""+bar.getOpen()));
		this.high.add(new Double(""+bar.getHigh()));
		this.low.add(new Double(""+bar.getLow()));
		this.volume.add(bar.getVolume());
		this.time.add(bar.getTs());
	}
	
	public int size(){
		return this.open().length;
	}
	
	public double[] open(){
		return Doubles.toArray(this.open);
	}

	public double[] close(){
		return Doubles.toArray(this.close);
	}

	public double[] high(){
		return Doubles.toArray(this.high);
	}

	public double[] low(){
		return Doubles.toArray(this.low);
	}

	public int[] volume(){
		return Ints.toArray(this.volume);
	}
	
	public long[] time(){
		return Longs.toArray(this.time);
	}	
	
	public List<bar_pb> bars(){
		return this.bars;
	}
	
	
	public boolean empty(){
		return this.open.isEmpty();
	}
}