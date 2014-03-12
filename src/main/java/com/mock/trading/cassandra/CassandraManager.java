package com.mock.trading.cassandra;

import com.netflix.astyanax.AstyanaxContext;
import com.netflix.astyanax.Keyspace;
import com.netflix.astyanax.connectionpool.NodeDiscoveryType;
import com.netflix.astyanax.connectionpool.impl.ConnectionPoolConfigurationImpl;
import com.netflix.astyanax.impl.AstyanaxConfigurationImpl;
import com.netflix.astyanax.model.ColumnFamily;
import com.netflix.astyanax.serializers.AnnotatedCompositeSerializer;
import com.netflix.astyanax.serializers.IntegerSerializer;
import com.netflix.astyanax.serializers.LongSerializer;
import com.netflix.astyanax.serializers.StringSerializer;
import com.netflix.astyanax.thrift.ThriftFamilyFactory;

public class CassandraManager
{		
	public static String hosts = "localhost:9160";
	public static String localhost = "localhost:9160";
	public static Keyspace ks;
	
	// placeholder for environment based initialization
	static{
		String mode = System.getProperty("mode");
		if ("local".equals(mode)){
			hosts = localhost;
		}
	}

	
	public static ColumnFamily<String, TimeWithSequenceSerializer> getTickCf(){
		AnnotatedCompositeSerializer<TimeWithSequenceSerializer> tradeSerializer
	      = new AnnotatedCompositeSerializer<TimeWithSequenceSerializer>(TimeWithSequenceSerializer.class);
		ColumnFamily<String, TimeWithSequenceSerializer> tick = new ColumnFamily<String, TimeWithSequenceSerializer>(
			    "tick",
			    StringSerializer.get(),   
			    tradeSerializer);  	
		return tick;
	}	

	public static ColumnFamily<String, Integer> getDailyOhlcCf(){
		ColumnFamily<String, Integer> daily = new ColumnFamily<String, Integer>(
			    "daily_ohlc",
			    StringSerializer.get(),   
			    IntegerSerializer.get());  	
		return daily;
	}	

	
	public static Keyspace getTradeKS(String hosts_) {		
		AstyanaxContext<Keyspace> context = new AstyanaxContext.Builder().				
				forKeyspace("trade").
				withAstyanaxConfiguration(
						new AstyanaxConfigurationImpl().setDiscoveryType(NodeDiscoveryType.RING_DESCRIBE))
				.withConnectionPoolConfiguration(new ConnectionPoolConfigurationImpl("trade").setPort(9160).
						setMaxConnsPerHost(5).setSeeds(hosts_))
					.withAstyanaxConfiguration(
						new AstyanaxConfigurationImpl().
						setCqlVersion("3.0.0").
						setTargetCassandraVersion("1.2")).						
						buildKeyspace(ThriftFamilyFactory.getInstance());
		
		context.start();
		Keyspace ks_ = context.getClient();
		return ks_;
	}		
	
	public static Keyspace setKS(String hosts){
		return getTradeKS(hosts);
	}
	
	public static Keyspace getTradeKS(){
		return getTradeKS(hosts);
	}
	
}
