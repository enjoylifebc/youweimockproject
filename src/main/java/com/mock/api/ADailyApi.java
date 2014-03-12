package com.mock.api;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.InvalidProtocolBufferException;
import com.mock.trading.cassandra.CassandraManager;
import com.mock.trading.pb.TradePb.daily_pb;
import com.netflix.astyanax.Keyspace;
import com.netflix.astyanax.MutationBatch;
import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;
import com.netflix.astyanax.model.Column;
import com.netflix.astyanax.model.ColumnFamily;
import com.netflix.astyanax.model.ColumnList;

/***
 * daily view of market data, provide api to get/insert OHLC data
 * @author Youwei
 *
 */

@SuppressWarnings("boxing")
public class ADailyApi
{			      				    
	final static Logger logger = LoggerFactory.getLogger(ADailyApi.class);
	static Keyspace ks = CassandraManager.getTradeKS(); 
	static ColumnFamily<String, Integer> dailyOhlcCf = CassandraManager.getDailyOhlcCf();
	
	public ADailyApi(){
	}

	public static daily_pb get(String symbol, int date){		
		daily_pb out = null;		
		List<daily_pb> daily = get(symbol, date, date);
		if (!daily.isEmpty())
			out = daily.get(0);		
		return out;
	}
		
	public static void insert(List<daily_pb> dailies){
		MutationBatch m = ks.prepareMutationBatch();
		for (daily_pb daily : dailies){
			m.withRow(dailyOhlcCf, daily.getSymbol())
			  .putColumn(daily.getDate(), daily.toByteArray());
			try {
				  m.execute();
			} catch (ConnectionException e) {
				logger.error(e.getMessage());
			}											
		}
		logger.info("finished inserting " + dailies.size());		
	}
	
	public static List<daily_pb> get(String symbol, int start, int end){
		List<daily_pb> out = new ArrayList<daily_pb>();
		ColumnList<?> data;
		try {
			data = ks.prepareQuery(dailyOhlcCf)
				    .getKey(symbol)
				    .withColumnRange(start, end, false, 100000)
				    .execute().getResult();			
			for (Column<?> c : data){
				out.add(daily_pb.parseFrom(c.getByteArrayValue()));
			}			
		} catch (ConnectionException | InvalidProtocolBufferException e) {
			logger.error(e.getMessage());
		}		
		return out;
	}
		
	public static void main(String[] args) {		
	}
}
