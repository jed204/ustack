package com.untzuntz.ustack.uisupport;

import java.util.List;

import nextapp.echo.app.Color;

import com.untzuntz.components.app.ClientTextField;

public class UClientTextField extends ClientTextField {

	private static final long serialVersionUID = -2538141124765761983L;

	private String fieldName;

	/** sets up the fieldname value and puts a limit on data entry to 255 characters */
	private void setup(String fieldName)
	{
		this.fieldName = fieldName;
		this.setMaximumLength(255);
	}
	
	protected String getFieldName() { return fieldName; }
	
	public UClientTextField(String renderId, String fieldName)
	{
		super(renderId);
		setup(fieldName);
	}
	
	public void setEditable(boolean en)
	{
		super.setEditable(en);
		if (en)
			setBackground(null);
		else
			setBackground(UStackStatics.LIGHT_GRAY);
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


	/** verifies the string is at least minLength in characters */
	public List<UEntryError> validateStringMinLen(int minLength)
	{
		return handleDisplay(UEntryValidation.validateIntMin(getFieldName(), getTextLength() + "", minLength));
	}
	
	/** verifies the string is at least maxLength in characters */
	public List<UEntryError> validateStringMaxLen(int maxLength)
	{
		return handleDisplay(UEntryValidation.validateIntMax(getFieldName(), getTextLength() + "", maxLength));
	}
	
	/** verifies the string is at least minLength but no more than maxLength in characters */
	public List<UEntryError> validateStringLen(int minLength, int maxLength)
	{
		return handleDisplay(UEntryValidation.validateIntRange(getFieldName(), getTextLength() + "", minLength, maxLength));
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
