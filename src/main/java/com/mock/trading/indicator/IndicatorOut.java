package com.mock.trading.indicator;

import java.util.Map;

import com.mock.trading.indicator.MACD.CROSS;
import com.mock.trading.pb.TradePb.bar_pb;

public class IndicatorOut
{
	public double macd;
	public double signal;
	public double macdhist;
	public double time;
	public double rsi;
	public double price;
//	public double ma2;
//	public double ma4;
//	public double ma;
//	public double ma2;
//	public double ma2;
//	public double ma2;
	public CROSS macdCross;	
	public Map<Integer, bar_pb> bars;
	
	public double bidRsi;
	public double askRsi;
	public double tradeRsi;
	
	public IndicatorOut()
	{
	}

}