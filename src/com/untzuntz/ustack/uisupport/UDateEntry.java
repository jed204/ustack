package com.untzuntz.ustack.uisupport;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import nextapp.echo.app.Color;

import org.informagen.echo.app.RegExTextField;

public class UDateEntry extends RegExTextField {

	private static final long serialVersionUID = 1L;
	private boolean required;
	private String fieldName;
	private String format;
	private String message;
	
	public UDateEntry(String fn)
	{
		fieldName = fn;
		setup();
	}
	
	public UDateEntry(String fn, boolean req)
	{
		fieldName = fn;
		required = req;
		setup();
	}
	
	public void setFormat(String fmt) {
		format = fmt;
		if (message == null)
			message = fmt;
	}
	
	public void setMessage(String msg) {
		message = msg;
	}
	
	private void setup()
	{
		if (format == null)
			format = "MM/dd/yyyy";

		if (fieldName == null)
			fieldName = "Date";
		
		setWidth(UStackStatics.EX_280);
		setMaximumLength(255);   
		
		if ("MM/dd/yyyy".equalsIgnoreCase(format))
			setRegEx("^\\d{1,2}\\/\\d{1,2}\\/\\d{4}$");
		else if ("dd-MMM-yyyy".equalsIgnoreCase(format))
			setRegEx("^([01]?\\d|2[0-8])-([Ff][Ee][bB])|(([012]?\\d|3[01])-([Jj][Aa][Nn]|[Mm][Aa][Rr]|[Aa][Pp][Rr]|[Mm][Aa][Yy]|[Jj][Uu][Nn]|[Jj][Uu][Ll]|[aA][Uu][gG]|[Ss][eE][pP]|[oO][Cc][Tt]|[Nn][oO][Vv]|[Dd][Ee][Cc]))-(19|20)\\d\\d$");
		else if ("dd/MMM/yyyy".equalsIgnoreCase(format))
			setRegEx("^([01]?\\d|2[0-8])-([Ff][Ee][bB])|(([012]?\\d|3[01])\\/([Jj][Aa][Nn]|[Mm][Aa][Rr]|[Aa][Pp][Rr]|[Mm][Aa][Yy]|[Jj][Uu][Nn]|[Jj][uU][lL]|[aA][Uu][gG]|[Ss][eE][pP]|[oO][Cc][Tt]|[Nn][oO][Vv]|[Dd][Ee][Cc]))\\/(19|20)\\d\\d$");

		setMessage(message);
	}
	
	public List<UEntryError> validateDate()
	{
		if (required)
		{
			return UEntryValidation.validateDate(fieldName, format, getText());
		}
		else if (getText().length() > 0)
		{
			return UEntryValidation.validateDate(fieldName, format, getText());
		}
		
		return handleDisplay(UEntryError.getEmptyList());
	}
	
	public void setDate(Date ts)
	{
		if (ts == null)
			return;
		
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		try {
			setText(sdf.format(ts));
		} catch (Exception err) {}
	}
	
	public Date getDate()
	{
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		Date dater = null; 
		try {
			dater = sdf.parse(getText());
		} catch (Exception err) {}

		return dater;
	}
	
	/** Mark the field in error */
	public void error()
	{
		setBackground(Color.RED);
		setForeground(Color.WHITE);
	}
	
	/** Mark the field as passed */
	public void pass()
	{
		setBackground(null);
		setForeground(null);
	}

	/** called to check on pass or fail of this field */
	private List<UEntryError> handleDisplay(List<UEntryError> list)
	{
		if (list.size() > 0)
			error();
		else
			pass(); // clears previous errors
		
		return list;
	}

}
