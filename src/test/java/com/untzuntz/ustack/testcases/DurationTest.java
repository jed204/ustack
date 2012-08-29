package com.untzuntz.ustack.testcases;

import static org.junit.Assert.assertEquals;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.untzuntz.ustack.main.Duration;

public class DurationTest extends UStackTestCaseBase {

	protected static Logger logger = Logger.getLogger(DurationTest.class);

	@Test public void testDuration()
	{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		long timeZoneOffset = (-1 * TimeZone.getDefault().getRawOffset());
		
		Date now = new Date(timeZoneOffset + 0L);
		Date in5Minutes = new Date(timeZoneOffset + 5 * 60 * 1000L);
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(now);
		
		assertEquals("19700101000000", sdf.format(new Date(timeZoneOffset + 0L)));
		assertEquals("19700101000000", sdf.format(new Duration("0 days").getDate(now)));
		assertEquals("19700108000000", sdf.format(new Duration("1 week").getDate(now)));
		assertEquals("19700205000000", sdf.format(new Duration("5 weeks").getDate(now)));
		assertEquals("19700103000000", sdf.format(new Duration("2 days").getDate(now)));
		assertEquals("19700301000000", sdf.format(new Duration("2 months").getDate(now)));
		assertEquals("19720101000000", sdf.format(new Duration("2 years").getDate(now)));
		assertEquals("19700102190000", sdf.format(new Duration("weekly on friday at 1900").getDate(now)));
		assertEquals("19700101000000", sdf.format(new Duration("monthly on the first day").getDate(now)));
		assertEquals("19700131000000", sdf.format(new Duration("monthly on the last day").getDate(now)));
		assertEquals("19700101150000", sdf.format(new Duration("monthly on the first day at 1500").getDate(now)));
		assertEquals("19700131193000", sdf.format(new Duration("monthly on the last day at 1930").getDate(now)));
		assertEquals("19700115000000", sdf.format(new Duration("monthly on the 15th").getDate(now)));
		assertEquals("19700115165012", sdf.format(new Duration("monthly on the 15th at 165012").getDate(now)));
		assertEquals("19700101235900", sdf.format(new Duration("daily at 2359").getDate(now)));
		assertEquals("19700101000100", sdf.format(new Duration("daily at 0001").getDate(now)));
		assertEquals("19700102000100", sdf.format(new Duration("daily at 0001").getDate(in5Minutes)));
		assertEquals("19700105000100", sdf.format(new Duration("weekly at 0001").getDate(now)));
		assertEquals("19700105000100", sdf.format(new Duration("weekly at 0001").getDate(in5Minutes)));
		assertEquals("19700101000100", sdf.format(new Duration("monthly at 0001").getDate(now)));
		assertEquals("19700201000100", sdf.format(new Duration("monthly at 0001").getDate(in5Minutes)));
	}
	
	public Date getAdded(Calendar cal, int field, int amount)
	{
		cal.add(field, amount);
		Date ret = cal.getTime();
		cal.add(field, -1 * amount);
		return ret;
	}

}
