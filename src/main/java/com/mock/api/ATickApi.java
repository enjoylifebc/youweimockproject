package com.mock.api;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.InvalidProtocolBufferException;
import com.mock.trading.cassandra.CassandraManager;
import com.mock.trading.cassandra.TimeWithSequenceSerializer;
import com.mock.trading.domain.enumeration.BAR_TYPE;
import com.mock.trading.pb.TradePb.bar_pb;
import com.mock.trading.pb.TradePb.tick_pb;
import com.mock.trading.util.T;
import com.netflix.astyanax.Keyspace;
import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;
import com.netflix.astyanax.model.Column;
import com.netflix.astyanax.model.ColumnFamily;
import com.netflix.astyanax.model.ColumnList;

/***
 * provide tick api to search cassandra database by min, timestamp, timestamp range, datetime rage
 * 
 * provide client filtering for ticks object: filter by time, sec, timestamp, by hour, by datetime 
 *  
 * Given a list of ticks object, convert it into min, 5min, hour bar object.  (using OHLC)
 * 
 * @author Youwei
 * 
 */
public class ATickApi {

	private final static Logger logger = LoggerFactory.getLogger(ATickApi.class);
	static ColumnFamily<String, TimeWithSequenceSerializer> tickCf = CassandraManager
			.getTickCf();
	static Keyspace ks = CassandraManager.getTradeKS();

	public ATickApi() {
	}

	public static List<tick_pb> getTickByMin(String symbol, String start,
			String end) {
		long[] range = new long[2];
		range[0] = df.parseDateTime(start).getMillis();
		range[1] = df.parseDateTime(end).getMillis();
		return getTick(symbol, range[0], range[1]);
	}

	public static List<tick_pb> getTickBySec(String symbol, long ts) {
		List<tick_pb> out = new ArrayList<tick_pb>();

		long[] range = new long[2];
		range[0] = ts - 60;
		range[1] = ts;
		List<tick_pb> ticks = getTick(symbol, range[0], range[1]);

		boolean trade = false;
		boolean quote = false;

		if (!ticks.isEmpty()) {
			for (int i = ticks.size() - 1; i > 0; i--) {
				if ("T".equals(ticks.get(i).getType()) && trade == false) {
					out.add(ticks.get(i));
					trade = true;
				}
				if ("Q".equals(ticks.get(i).getType()) && quote == false) {
					out.add(ticks.get(i));
					quote = true;
				}
				if (quote && trade)
					break;
			}
			return out;
		}
		return out;
	}

	/**
	 * Return ticks from start + secs
	 */
	public static List<tick_pb> getTickBySecs(String symbol, long start,
			int secs) {
		if (secs > 0) {
			return getTick(symbol, start, start + secs);
		}

		// else get 2 hours of data, good for now
		return getTick(symbol, start, start + 60 * 60 * 2);
	}

	public static List<tick_pb> getTickByDate(String symbol, int date) {
		long[] range = T.getDayRange(date, 1);
		return getTick(symbol, range[0], range[1]);
	}

	public static List<tick_pb> getTickByDate(String symbol, int date,
			boolean filterQuote) {
		long[] range = T.getDayRange(date, 1);
		return getTick(symbol, range[0], range[1], filterQuote);
	}

	private static List<tick_pb> getTick(String symbol, long start_, long end) {
		return getTick(symbol, start_, end, true);
	}

	private static List<tick_pb> getTick(String symbol, long start_, long end_,
			boolean filterQuote) {
		TimeWithSequenceSerializer start = new TimeWithSequenceSerializer();
		start.setTs(T.dbMilli(start_));
		TimeWithSequenceSerializer end = new TimeWithSequenceSerializer();
		end.setTs(T.dbMilli(end_));
		List<tick_pb> ticks = new ArrayList<tick_pb>();
		ColumnList<TimeWithSequenceSerializer> columns;
		try {
			columns = ks.prepareQuery(tickCf).getKey(symbol)
					.withColumnRange(start, end, false, 1000000).execute()
					.getResult();
			for (Column<TimeWithSequenceSerializer> tickColumn : columns) {
				tick_pb tick = tick_pb
						.parseFrom(tickColumn.getByteArrayValue());

				if ("Q".equals(tick.getType())) {
					if (filterQuote)
						continue;
				}

				ticks.add(tick);
			}
		} catch (ConnectionException | InvalidProtocolBufferException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return ticks;
	}

	protected static DateTimeFormatter df = DateTimeFormat
			.forPattern("yyyyMMdd HH:mm");
	protected static DateTimeFormatter secondDF = DateTimeFormat
			.forPattern("HHmmss");


	public static List<bar_pb> getBar(List<tick_pb> ticks, BAR_TYPE barType) {

		int lastsec = 0;
		bar_pb.Builder current_bar = null;
		List<bar_pb> bars = new ArrayList<bar_pb>();
		double lastValue = 0.0;

		int i = 0;
		for (tick_pb tick : ticks) {

			DateTime dt = new DateTime(T.milli(tick.getTs()));
			// dt.isAfter(openTime.getMillis()) &&
			// dt.isBefore(closeTime.getMillis()) &&
			if ("T".equals(tick.getType())) {

				int currentTime = 0;

				if (barType == BAR_TYPE.MINUTE) {
					currentTime = new Integer(dt.getHourOfDay() + ""
							+ dt.getMinuteOfHour());
				} else if (barType == BAR_TYPE.SECOND) {
					currentTime = new Integer(secondDF.print(dt));
				} else if (barType == BAR_TYPE.FIVE_SECOND) {
					DateTime t = dt.minusSeconds(dt.getSecondOfMinute() % 5);
					currentTime = new Integer(secondDF.print(t));
				}

				// a new bar
				if (currentTime != lastsec) {
					// close old bar
					if (current_bar != null) {
						current_bar.setClose(lastValue);
						bars.add(current_bar.build());
					}

					// create new bar
					current_bar = bar_pb.newBuilder();
					current_bar.setLow(Double.MAX_VALUE);
					lastsec = currentTime;
					// current_bar.setTs(dt.withSecondOfMinute(0).withMillis(0).getMillis()/1000);
					current_bar.setTs(tick.getTs());
					current_bar.setOpen(tick.getBid());
					// insert open of new bar
				}

				lastValue = tick.getBid();
				current_bar.setHigh(Math.max(current_bar.getHigh(),
						tick.getBid()));
				current_bar
						.setLow(Math.min(current_bar.getLow(), tick.getBid()));
				current_bar.setVolume(current_bar.getVolume()
						+ tick.getBidsize());

				if (i == ticks.size() - 1) {
					current_bar.setClose(lastValue);
					bars.add(current_bar.build());
				}
			}
			i++;
		}

		return bars;
	}

	public static List<tick_pb> filterBySec(List<tick_pb> ticks, long start,
			int secs) {
		List<tick_pb> filtered = new ArrayList<tick_pb>();
		for (tick_pb tick : ticks) {
			if (tick.getTs() > start) {
				filtered.add(tick);
			}
		}
		return filtered;
	}

	public static List<tick_pb> filterByTime(List<tick_pb> ticks, long start,
			long end) {
		List<tick_pb> filtered = new ArrayList<tick_pb>();

		for (tick_pb tick : ticks) {
			if (tick.getTs() >= start && tick.getTs() <= end) {
				filtered.add(tick);
			}
		}
		return filtered;
	}

	public static List<tick_pb> filterByTime(List<tick_pb> ticks,
			DateTime start, DateTime end) {
		List<tick_pb> filtered = new ArrayList<tick_pb>();

		for (tick_pb tick : ticks) {
			if (tick.getTs() * 1000 > start.getMillis()
					&& tick.getTs() * 1000 < end.getMillis()) {
				filtered.add(tick);
			}
		}
		return filtered;
	}

	public static List<tick_pb> filterByTime(long ts, List<tick_pb> ticks) {
		List<tick_pb> filtered = new ArrayList<tick_pb>();

		DateTime start = new DateTime(T.milli(ts)).minusMinutes(30);
		DateTime end = new DateTime(T.milli(ts)).plusMinutes(30);

		for (tick_pb tick : ticks) {
			if (tick.getTs() * 1000 > start.getMillis()
					&& tick.getTs() * 1000 < end.getMillis()) {
				filtered.add(tick);
			}
		}
		return filtered;
	}

	public static List<tick_pb> filterByHour(List<tick_pb> ticks, int hhStart,
			int hhEnd) {

		if (ticks.isEmpty())
			return null;

		List<tick_pb> filtered = new ArrayList<tick_pb>();

		GregorianCalendar cal = new GregorianCalendar();
		cal.setTimeInMillis(ticks.get(0).getTs() * 1000);
		cal.set(Calendar.HOUR, new Integer(hhStart));
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);

		long tsStart = cal.getTimeInMillis() / 1000;
		cal.set(Calendar.HOUR, new Integer(hhEnd));
		long tsEnd = cal.getTimeInMillis() / 1000;

		for (tick_pb tick : ticks) {
			if (tick.getTs() > tsStart && tick.getTs() < tsEnd) {
				filtered.add(tick);
			}
		}
		return filtered;
	}

}
