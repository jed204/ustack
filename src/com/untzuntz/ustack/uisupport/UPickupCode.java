package com.untzuntz.ustack.uisupport;

import java.util.List;

import nextapp.echo.app.Color;

import com.untzuntz.components.app.CodeTextField;

public class UPickupCode extends CodeTextField {

	private static final long serialVersionUID = 1L;
	private boolean required;
	private String fieldName;
	
	public UPickupCode(String fieldName)
	{
		this.fieldName = fieldName;
		setup();
	}
	
	public UPickupCode(String fieldName, boolean req)
	{
		this.fieldName = fieldName;
		this.required = req;
		setup();
	}
	
	private void setup()
	{
		setWidth(UStackStatics.EX_280);
		setMaximumLength(255);   
	}
	
	public List<UEntryError> validateCode()
	{
		if (required)
		{
			return handleDisplay(UEntryValidation.validateCode(fieldName, getText()));
		}
		else if (getText().length() > 0)
		{
			return handleDisplay(UEntryValidation.validateCode(fieldName, getText()));
		}
		
		return handleDisplay(UEntryError.getEmptyList());
	}
	
	public String getCode()
	{
		if (getText().length() > 0)
		{
			if (UEntryValidation.validateCode(fieldName, getText()).size() == 0)
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
