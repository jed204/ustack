package com.untzuntz.ustack.uisupport;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.untzuntz.ustack.main.Msg;

public class UEntryValidation {
	
	private static Logger logger = Logger.getLogger(UEntryValidation.class);

	private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";
	private static final String CODE_PATTERN = "[a-fA-F0-9]{3}[0-9]-[a-fA-F0-9]{3}[0-9]-[a-fA-F0-9]{3}[0-9]";

	public static List<UEntryError> validateCode(String fieldName, String value) {
		
		if (Pattern.compile(CODE_PATTERN).matcher(value).matches()) {
			return UEntryError.getEmptyList(); // no errors found
		}
		return UEntryError.getListInstance(fieldName, Msg.getString("EntryError-CodeFormat", fieldName));
		
	}
	
	/** Verify the format of a date in text  */
	public static List<UEntryError> validateDate(String fieldName, String format, String dateText) {
		
		Date date = null;

		// test date string matches format structure using regex
		// - weed out illegal characters and enforce 4-digit year
		// - create the regex based on the local format string
		String reFormat = Pattern.compile("d+|M+").matcher(Matcher.quoteReplacement(format)).replaceAll("\\\\d{1,2}");
		reFormat = Pattern.compile("y+").matcher(reFormat).replaceAll("\\\\d{4}");
		if (Pattern.compile(reFormat).matcher(dateText).matches()) {

			// date string matches format structure,
			// - now test it can be converted to a valid date
			SimpleDateFormat sdf = (SimpleDateFormat)DateFormat.getDateInstance();
			sdf.applyPattern(format);
			sdf.setLenient(false);
			try {
				date = sdf.parse(dateText);
			} catch (ParseException e) {}
		}
		
		if (date == null)
			return UEntryError.getListInstance(fieldName, Msg.getString("EntryError-DateFormat", fieldName, format));
		else
			return UEntryError.getEmptyList(); // no errors found
	}
	
	/** Verify the format of an email address */
	public static List<UEntryError> validateEmailAddress(String fieldName, String text)
	{	
		Pattern pattern = Pattern.compile(EMAIL_PATTERN);
	 
		if (text == null || text.length() == 0) // low hanging fruit
		{
			logger.debug("UEntryVal : Email -- ERR [NO TEXT]");
			return UEntryError.getListInstance(fieldName, Msg.getString("EntryError-EmailFormat", fieldName));
		}

		try {
			javax.mail.internet.InternetAddress.parse( text, true );
			Matcher matcher = pattern.matcher(text);
			if (!matcher.matches())
				throw new Exception("Invalid Email Address Format");
			
		} catch (Exception er) {
			logger.debug("UEntryVal : Email -- ERR [" + er + "]");
			return UEntryError.getListInstance(fieldName, Msg.getString("EntryError-EmailFormat", fieldName));
		}
		logger.debug("UEntryVal : Email -- no errors [" + text + "]");
		return UEntryError.getEmptyList(); // no errors found
	}

	/** validate the text length is at least or at most or both */
	public static List<UEntryError> validateString(String fieldName, String text, int minLength, int maxLength)
	{
		// Note: though possible to do, failing both min and max would be silly so we just will report one
		int length = text.length();
		if (minLength != -1 && length < minLength)
			return UEntryError.getListInstance(fieldName, Msg.getString("EntryError-MinLength", fieldName, minLength));
		
		if (maxLength != -1 && length > maxLength)
			return UEntryError.getListInstance(fieldName, Msg.getString("EntryError-MaxLength", fieldName, minLength));
		
		return UEntryError.getEmptyList(); // no errors found
	}

	/** validate the text can be parsed to a Long and is between the range of min and max */
	public static List<UEntryError> validateLongRange(String fieldName, String text, long min, long max)
	{
		long val = 0;
		try { val = Long.valueOf(text); } catch (Exception err) { 
			return UEntryError.getListInstance(fieldName, Msg.getString("EntryError-NumberFormat", fieldName, Msg.getString("Number")));
		}
		
		if (val < min)
			return UEntryError.getListInstance(fieldName, Msg.getString("EntryError-MinValueLong", fieldName, min));
		
		if (val > max)
			return UEntryError.getListInstance(fieldName, Msg.getString("EntryError-MaxValueLong", fieldName, max));
		
		return UEntryError.getEmptyList(); // no errors found
	}

	/** validate the text can be parsed to a long and is at least min */
	public static List<UEntryError> validateLongMin(String fieldName, String text, long min)
	{
		long val = 0;
		try { val = Long.valueOf(text); } catch (Exception err) { 
			return UEntryError.getListInstance(fieldName, Msg.getString("EntryError-NumberFormat", fieldName, Msg.getString("Number")));
		}
		
		if (val < min)
			return UEntryError.getListInstance(fieldName, Msg.getString("EntryError-MinValueLong", fieldName, min));
		
		return UEntryError.getEmptyList(); // no errors found
	}
	
	/** validate the text can be parsed to a long and is at least min */
	public static List<UEntryError> validateLongMax(String fieldName, String text, long max)
	{
		long val = 0;
		try { val = Long.valueOf(text); } catch (Exception err) { 
			return UEntryError.getListInstance(fieldName, Msg.getString("EntryError-NumberFormat", fieldName, Msg.getString("Number")));
		}
		
		if (val > max)
			return UEntryError.getListInstance(fieldName, Msg.getString("EntryError-MaxValueLong", fieldName, max));
		
		return UEntryError.getEmptyList(); // no errors found
	}

	/** validate the text can be parsed to a Int and is between the range of min and max */
	public static List<UEntryError> validateIntRange(String fieldName, String text, int min, int max)
	{
		int val = 0;
		try { val = Integer.valueOf(text); } catch (Exception err) { 
			return UEntryError.getListInstance(fieldName, Msg.getString("EntryError-NumberFormat", fieldName, Msg.getString("Number")));
		}
		
		if (val < min)
			return UEntryError.getListInstance(fieldName, Msg.getString("EntryError-MinValueInt", fieldName, min));
		
		if (val > max)
			return UEntryError.getListInstance(fieldName, Msg.getString("EntryError-MaxValueInt", fieldName, max));
		
		return UEntryError.getEmptyList(); // no errors found
	}

	/** validate the text can be parsed to a int and is at least min */
	public static List<UEntryError> validateIntMin(String fieldName, String text, int min)
	{
		int val = 0;
		try { val = Integer.valueOf(text); } catch (Exception err) { 
			return UEntryError.getListInstance(fieldName, Msg.getString("EntryError-NumberFormat", fieldName, Msg.getString("Number")));
		}
		
		if (val < min)
			return UEntryError.getListInstance(fieldName, Msg.getString("EntryError-MinValueInt", fieldName, min));
		
		return UEntryError.getEmptyList(); // no errors found
	}
	
	/** validate the text can be parsed to a int and is at least min */
	public static List<UEntryError> validateIntMax(String fieldName, String text, int max)
	{
		int val = 0;
		try { val = Integer.valueOf(text); } catch (Exception err) { 
			return UEntryError.getListInstance(fieldName, Msg.getString("EntryError-NumberFormat", fieldName, Msg.getString("Number")));
		}
		
		if (val > max)
			return UEntryError.getListInstance(fieldName, Msg.getString("EntryError-MaxValueInt", fieldName, max));
		
		return UEntryError.getEmptyList(); // no errors found
	}
	
	/** validate the text can be parsed to a Float and is between the range of min and max */
	public static List<UEntryError> validateFloatRange(String fieldName, String text, float min, float max)
	{
		float val = 0;
		try { val = Float.valueOf(text); } catch (Exception err) { 
			return UEntryError.getListInstance(fieldName, Msg.getString("EntryError-NumberFormat", fieldName, Msg.getString("Number")));
		}
		
		if (val < min)
			return UEntryError.getListInstance(fieldName, Msg.getString("EntryError-MinValueFloat", fieldName, min));
		
		if (val > max)
			return UEntryError.getListInstance(fieldName, Msg.getString("EntryError-MaxValueFloat", fieldName, max));
		
		return UEntryError.getEmptyList(); // no errors found
	}

	/** validate the text can be parsed to a float and is at least min */
	public static List<UEntryError> validateFloatMin(String fieldName, String text, float min)
	{
		float val = 0;
		try { val = Float.valueOf(text); } catch (Exception err) { 
			return UEntryError.getListInstance(fieldName, Msg.getString("EntryError-NumberFormat", fieldName, Msg.getString("Number")));
		}
		
		if (val < min)
			return UEntryError.getListInstance(fieldName, Msg.getString("EntryError-MinValueFloat", fieldName, min));
		
		return UEntryError.getEmptyList(); // no errors found
	}
	
	/** validate the text can be parsed to a float and is at least min */
	public static List<UEntryError> validateFloatMax(String fieldName, String text, float max)
	{
		float val = 0;
		try { val = Float.valueOf(text); } catch (Exception err) { 
			return UEntryError.getListInstance(fieldName, Msg.getString("EntryError-NumberFormat", fieldName, Msg.getString("Number")));
		}
		
		if (val > max)
			return UEntryError.getListInstance(fieldName, Msg.getString("EntryError-MaxValueFloat", fieldName, max));
		
		return UEntryError.getEmptyList(); // no errors found
	}
	

	
}
