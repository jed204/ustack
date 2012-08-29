package com.untzuntz.ustack.main;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public class Duration {

	static final long SECOND = 1000;
	static final long MINUTE = 60 * SECOND;
	static final long HOUR = 60 * MINUTE;
	static final long DAY = 24 * HOUR;
	static final long WEEK = 7 * DAY;

	static {
	}

	static Map<String, Integer> calendarFields = new HashMap<String, Integer>();

	static {
		Integer millisecondField = new Integer(Calendar.MILLISECOND);
		calendarFields.put("millisecond", millisecondField);
		calendarFields.put("milliseconds", millisecondField);

		Integer secondField = new Integer(Calendar.SECOND);
		calendarFields.put("second", secondField);
		calendarFields.put("seconds", secondField);

		Integer minuteField = new Integer(Calendar.MINUTE);
		calendarFields.put("minute", minuteField);
		calendarFields.put("minutes", minuteField);

		Integer hourField = new Integer(Calendar.HOUR);
		calendarFields.put("hour", hourField);
		calendarFields.put("hours", hourField);

		Integer dayField = new Integer(Calendar.DAY_OF_MONTH);
		calendarFields.put("day", dayField);
		calendarFields.put("days", dayField);

		Integer weekField = new Integer(Calendar.WEEK_OF_MONTH);
		calendarFields.put("week", weekField);
		calendarFields.put("weeks", weekField);

		Integer monthField = new Integer(Calendar.MONTH);
		calendarFields.put("month", monthField);
		calendarFields.put("months", monthField);

		Integer yearField = new Integer(Calendar.YEAR);
		calendarFields.put("year", yearField);
		calendarFields.put("years", yearField);
		
		
		calendarFields.put("daily", 21001);
		calendarFields.put("weekly", 21002);
		calendarFields.put("monthly", 21003);
		calendarFields.put("yearly", 21004);
	}

	private int field;
	private int amount;
	private String unitText;
	private String hhmmss;

	Duration() {
	}

	public Duration(int milliseconds) {
		amount = milliseconds;
		field = Calendar.MILLISECOND;
	}

	public Duration(Duration duration) {
		field = duration.field;
		amount = duration.amount;
	}

	/**
	 * creates a duration from a textual description. syntax: {number} space
	 * {unit} where number is parsable to a java.lang.Number and unit is one of
	 * <ul>
	 * <li>second</li>
	 * <li>seconds</li>
	 * <li>minute</li>
	 * <li>minutes</li>
	 * <li>hour</li>
	 * <li>hours</li>
	 * <li>day</li>
	 * <li>days</li>
	 * <li>week</li>
	 * <li>weeks</li>
	 * <li>month</li>
	 * <li>months</li>
	 * <li>year</li>
	 * <li>years</li>
	 * </ul>
	 */
	public Duration(String duration) {
		if (duration == null) {
			amount = 0;
			return;
		}

		duration = duration.toLowerCase();
		String[] durItems = duration.split(" ");

		try {
			
			// 1 week
			// 5 years
			
			Integer quantity = Integer.valueOf(durItems[0]);
			
			String unitText = durItems[1];
			Integer unit = (Integer) calendarFields.get(unitText);
		      if (unit == null)
		        throw new IllegalArgumentException("improper format of duration '" + duration + "'");

		    unitText = null;
	        field = unit.intValue();
	        amount = quantity;
	        hhmmss = null;
	        
		} catch (NumberFormatException e) {

			hhmmss = "000000";
			// weekly on friday at 1900
			// monthly on the first day
			// monthly on the last day
			// monthly on the 15th
			
			// daily at 1600
			
			unitText = durItems[0];
			Integer unit = (Integer) calendarFields.get(unitText);
		      if (unit == null)
		        throw new IllegalArgumentException("improper format of duration '" + duration + "'");
			
		      amount = 1;
			  field = Calendar.DAY_OF_MONTH;
		      subField = 0;
	    	  int tfIdx = 2;
		      if (!"at".equalsIgnoreCase(durItems[1]))
		      {
		    	  // on XXX
		    	  // on the XXX
		    	  String timeFrame = null;
		    	  if ("the".equalsIgnoreCase(durItems[2]))
		    		  tfIdx++;

	    		  timeFrame = durItems[tfIdx];
	    		  if ("first".equalsIgnoreCase(timeFrame))
	    		  {
	    			  if ("weekly".equalsIgnoreCase( unitText))
	    			  {
	    				  field = Calendar.DAY_OF_WEEK;
	    				  amount = Calendar.MONDAY;
	    			  }
	    		  }
	    		  else if ("last".equalsIgnoreCase(timeFrame))
	    		  {
	    			  if ("weekly".equalsIgnoreCase( unitText))
	    			  {
	    				  field = Calendar.DAY_OF_WEEK;
	    				  amount = Calendar.FRIDAY;
	    			  }
	    			  else if ("monthly".equalsIgnoreCase(unitText))
	    			  {
	    				  field = Calendar.MONTH;
	    				  subField = Calendar.DAY_OF_MONTH;
	    				  amount = -1;
	    			  }
	    			  else if ("yearly".equalsIgnoreCase(unitText))
	    			  {
	    				  field = Calendar.YEAR;
	    				  subField = Calendar.DAY_OF_MONTH;
	    				  amount = -1;
	    			  }
	    		  }
	    		  else
	    		  {
	    			  if (timeFrame.indexOf("th") > -1 || timeFrame.indexOf("st") > -1 || timeFrame.indexOf("nd") > -1 || timeFrame.indexOf("rd") > -1)
	    			  {
	    				  amount = Integer.valueOf( timeFrame.substring(0, 2) );
	    			  }
	    			  else
	    			  {
	    				  field = Calendar.DAY_OF_WEEK;
	    				  if ("sunday".equalsIgnoreCase(timeFrame))
	    					  amount = Calendar.SUNDAY;
	    				  else if ("monday".equalsIgnoreCase(timeFrame))
	    					  amount = Calendar.MONDAY;
	    				  else if ("tuesday".equalsIgnoreCase(timeFrame))
	    					  amount = Calendar.TUESDAY;
	    				  else if ("wednesday".equalsIgnoreCase(timeFrame))
	    					  amount = Calendar.WEDNESDAY;
	    				  else if ("thursday".equalsIgnoreCase(timeFrame))
	    					  amount = Calendar.THURSDAY;
	    				  else if ("friday".equalsIgnoreCase(timeFrame))
	    					  amount = Calendar.FRIDAY;
	    				  else if ("saturday".equalsIgnoreCase(timeFrame))
	    					  amount = Calendar.SATURDAY;
	    				  else
	    				      throw new IllegalArgumentException("improper format of duration '" + duration + "'");
	    			  }
	    		  }
	    		  
	    		  if (durItems.length > (tfIdx + 1) && "day".equalsIgnoreCase(durItems[tfIdx + 1]))
	    			  tfIdx++;

	    		  if (durItems.length > (tfIdx + 1))
	    		  {
		    		  if ("at".equalsIgnoreCase(durItems[tfIdx + 1]))
		    			  tfIdx++;
		    		  
		    		  hhmmss = durItems[tfIdx + 1];
	    		  }
		      }
		      else
		      {
		    	  amount = 0;
		    	  // for weeks assume Monday
    			  if ("weekly".equalsIgnoreCase( unitText))
    			  {
    				  field = Calendar.DAY_OF_WEEK;
    				  amount = Calendar.MONDAY;
    			  }
    			  
    			  hhmmss = durItems[2];
		      }
		      
		      if (hhmmss.length() == 2)
		    	  hhmmss += "0000";
		      else if (hhmmss.length() == 4)
		    	  hhmmss += "00";
		      	
		      //System.out.println(duration + " | Date => " + amount + " || hhmmss => " + hhmmss);
			
		} catch (IllegalArgumentException iae) {
			throw iae;
		}
		
	}
	private int subField;
	
	public Date getDate(Date date) {

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		
		if (unitText == null)
			calendar.add(field, (int) amount);
		else if (hhmmss != null)
		{
			if (amount == -1) // monthly or yearly
			{
				calendar.add(field, 1);
				calendar.add(subField, -1);
				amount = 0;
			}
			
			if (amount != 0)
				calendar.set(field, amount);
			
			calendar.set(Calendar.HOUR_OF_DAY, Integer.valueOf(hhmmss.substring(0, 2)));
			calendar.set(Calendar.MINUTE, Integer.valueOf(hhmmss.substring(2, 4)));
			calendar.set(Calendar.SECOND, Integer.valueOf(hhmmss.substring(4, 6)));

			if ("daily".equalsIgnoreCase(unitText))
			{
				// daily
				System.out.println(calendar.getTime() + " before " + date);
				if (calendar.getTime().before(date))
					calendar.add(Calendar.DATE, 1);
			}
			else if ("weekly".equalsIgnoreCase(unitText))
			{
				// weekly
				System.out.println("pre => " + calendar.getTime() + " before " + date);
				if (calendar.getTime().before(date))
					calendar.add(Calendar.WEEK_OF_MONTH, 1);
				System.out.println("pos => " + calendar.getTime() + " before " + date);
			}
			else if ("monthly".equalsIgnoreCase(unitText))
			{
				// monthly
				if (calendar.getTime().before(date))
					calendar.add(Calendar.MONTH, 1);
			}
			else if ("yearly".equalsIgnoreCase(unitText))
			{
				// weekly
				if (calendar.getTime().before(date))
					calendar.add(Calendar.YEAR, 1);
			}
			
			if (amount != 0)
				calendar.set(field, (int) amount);
		}
		
		return calendar.getTime();
	}

	public long getMilliseconds() {
		switch (field) {
		case Calendar.MILLISECOND:
			return amount;
		case Calendar.SECOND:
			return amount * SECOND;
		case Calendar.MINUTE:
			return amount * MINUTE;
		case Calendar.HOUR:
			return amount * HOUR;
		case Calendar.DAY_OF_MONTH:
			return amount * DAY;
		case Calendar.WEEK_OF_MONTH:
			return amount * WEEK;
		default:
			throw new UnsupportedOperationException("calendar field '" + field
					+ "' does not have a fixed duration");
		}
	}

}
