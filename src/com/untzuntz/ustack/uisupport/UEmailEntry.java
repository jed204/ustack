package com.untzuntz.ustack.uisupport;

import java.util.List;

import nextapp.echo.app.Color;

import org.informagen.echo.app.RegExTextField;

public class UEmailEntry extends RegExTextField {

	private static final long serialVersionUID = 1L;
	private boolean required;
	private String fieldName;
	
	public UEmailEntry()
	{
		setup();
	}
	
	public UEmailEntry(boolean req)
	{
		required = req;
		setup();
	}
	
	private void setup()
	{
		fieldName = "Email Address";
		setWidth(UStackStatics.EX_280);
		setMaximumLength(255);   
		setRegEx("^\\s*[\\w\\-\\+_]+(\\.[\\w\\-\\+_]+)*\\@[\\w\\-\\+_]+\\.[\\w\\-\\+_]+(\\.[\\w\\-\\+_]+)*\\s*$");
		setMessage("ex: user@domain.com");
	}
	
	public List<UEntryError> validateEmail()
	{
		if (required)
		{
			return handleDisplay(UEntryValidation.validateEmailAddress(fieldName, getText()));
		}
		else if (getText().length() > 0)
		{
			return handleDisplay(UEntryValidation.validateEmailAddress(fieldName, getText()));
		}
		
		return handleDisplay(UEntryError.getEmptyList());
	}
	
	public String getEmailAddress()
	{
		if (getText().length() > 0)
		{
			if (UEntryValidation.validateEmailAddress(fieldName, getText()).size() == 0)
				return getText();
		}
		return null;
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
