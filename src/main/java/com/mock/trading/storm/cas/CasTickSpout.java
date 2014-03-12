package com.mock.trading.storm.cas;

import java.util.List;
import java.util.Map;

import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;

import com.mock.api.ATickApi;
import com.mock.trading.pb.TradePb.tick_pb;

public class CasTickSpout extends BaseRichSpout {

	private static final long serialVersionUID = -8832129289160738851L;
	SpoutOutputCollector _collector;        
    List<tick_pb> ticks;
    int tupleIndex;

    String symbol;
    int date;
    
    public CasTickSpout(String symbol, int date){
    	this.symbol = symbol;
    	this.date = date;    	
    }
    
    @Override
    public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
        _collector = collector;
        ticks = ATickApi.getTickByDate(symbol, date);
    }

    @Override
    public void nextTuple() {    	
    	if (tupleIndex<ticks.size()){
    		tick_pb tick = ticks.get(tupleIndex);
            _collector.emit(new Values(
            		tick.getSymbol(),
            		tick.getTs(),
            		tick.getIndex(),
            		tick.getType(),
            		new Double(tick.getBid()),
            		tick.getAsk(),
            		tick.getBidsize(),
            		tick.getAsksize(),
            		tick.getExchange(),
            		tick.getChange()
            		));
            tupleIndex++;
        }
        else{
            try {            	            	
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void ack(Object id) {
    }

    @Override
    public void fail(Object id) {
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("symbol","ts","index","type", "bid", "ask", "bidsize", "asksize", "exchange", "change"));
    }
        
    public static void main(String[] args) {	
	}    
}