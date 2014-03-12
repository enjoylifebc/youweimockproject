package com.mock.trading.storm.cas;

import com.mock.trading.cassandra.CassandraManager;
import com.mock.trading.cassandra.TimeWithSequenceSerializer;
import com.mock.trading.pb.TradePb.tick_pb;
import com.netflix.astyanax.Keyspace;
import com.netflix.astyanax.MutationBatch;
import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;
import com.netflix.astyanax.model.ColumnFamily;

public class CasTickUtil {
	
	    static Keyspace keyspace = CassandraManager.getTradeKS();
	    static MutationBatch m = keyspace.prepareMutationBatch();
		static ColumnFamily<String, TimeWithSequenceSerializer> tickCf =CassandraManager.getTickCf();

		public static void save(tick_pb tick ){
			TimeWithSequenceSerializer ser = new TimeWithSequenceSerializer();
			ser.setTs(tick.getTs());
			ser.setId(tick.getIndex());		
			
			m.withRow(tickCf, tick.getSymbol())
			  .putColumn(ser, tick.toByteArray());
			try {
				  m.execute();
			} catch (ConnectionException e) {
				e.printStackTrace();
			}
		}
}