package com.mock.trading.util;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.joda.time.DateTime;


@SuppressWarnings("boxing")
/**
 * time related utility
 * @author Youwei
 *
 */
public class T {
	private static SimpleDateFormat yyyymmddFormat = new SimpleDateFormat(
			"yyyyMMdd");

	public static long milli(long ts) {
		if (ts < 9999999999l)
			return ts * 1000;

		return ts;
	}

	public static long dbMilli(long ts) {
		if (ts > 9999999999l)
			return ts / 1000;

		return ts;
	}

	public static long[] getDayRange(int date, int dayInterval) {
		long[] datesTs = new long[2];
		long ts = T.dateToTs(date, true);
		datesTs[0] = ts;
		GregorianCalendar cal_t = new GregorianCalendar();
		cal_t.setTimeInMillis(ts);
		cal_t.add(Calendar.DATE, dayInterval);
		datesTs[1] = cal_t.getTimeInMillis();
		return datesTs;
	}

	public static Long dateToTs(int date) {
		return dateToTs(date, false);
	}
	

	public static double d2(double value)
	{
		return new BigDecimal(value).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
	}
	
	// assume 9:30-16:00
	public static boolean isMarketHour(long ts, boolean addTz)
	{
		DateTime dt = new DateTime(milli(ts));
		return dt.getMinuteOfDay() >= 570 && dt.getMinuteOfDay() <= 960;

	}
	public static Long dateToTs(int date, boolean millisecond) {
		return dateToTs("" + date, millisecond);
	}

	public static Long dateToTs(String date, boolean millisecond) {
		try {
			synchronized (T.yyyymmddFormat) {
				Long time = yyyymmddFormat.parse(date).getTime();
				if (millisecond)
					return time;

				return time / 1000;
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static void main(String[] args) {
	}
}
