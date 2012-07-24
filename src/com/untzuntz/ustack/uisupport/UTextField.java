package com.untzuntz.ustack.uisupport;

import java.util.List;

import nextapp.echo.app.Color;
import nextapp.echo.app.Extent;
import nextapp.echo.app.Font;
import nextapp.echo.app.TextField;
import nextapp.echo.app.event.ActionListener;

public class UTextField extends TextField {

	private static final long serialVersionUID = -2538141124765761983L;

	private String fieldName;

	/** sets up the fieldname value and puts a limit on data entry to 255 characters */
	private void setup(String fieldName)
	{
		this.fieldName = fieldName;
		this.setMaximumLength(255);
	}
	
	protected String getFieldName() { return fieldName; }
	
	public UTextField(String fieldName)
	{
		super();
		setup(fieldName);
	}
	
	public UTextField(String fieldName, Extent size)
	{
		super();
		setup(fieldName);
		setWidth(size);
	}
	
	public UTextField(String fieldName, Extent size, Extent height, Font font)
	{
		super();
		setup(fieldName);
		setWidth(size);
		setHeight(height);
		setInsets(UStackStatics.IN_2);
		
		if (font != null)
			setFont(font);
	}
	
	public UTextField(String fieldName, Extent size, ActionListener al, String cmd)
	{
		super();
		setup(fieldName);
		setWidth(size);
		addActionListener(al);
		setActionCommand(cmd);
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
	
	public List<UEntryError> validateEmailAddress()
	{
		return handleDisplay(UEntryValidation.validateEmailAddress(getFieldName(), getText()));
	}

	/** verifies the string is at least minLength in characters */
	public List<UEntryError> validateStringMinLen(int minLength)
	{
		return handleDisplay(UEntryValidation.validateString(getFieldName(), getText(), minLength, -1));
	}
	
	/** verifies the string is at least maxLength in characters */
	public List<UEntryError> validateStringMaxLen(int maxLength)
	{
		return handleDisplay(UEntryValidation.validateString(getFieldName(), getText(), -1, maxLength));
	}
	
	/** verifies the string is at least minLength but no more than maxLength in characters */
	public List<UEntryError> validateStringLen(int minLength, int maxLength)
	{
		return handleDisplay(UEntryValidation.validateString(getFieldName(), getText(), minLength, maxLength));
	}
	
	/** verifies the number is a long and between min and max */
	public List<UEntryError> validateLongRange(long min, long max)
	{
		return handleDisplay(UEntryValidation.validateLongRange(getFieldName(), getText(), min, max));
	}

	/** verifies the number is a long and at least min */
	public List<UEntryError> validateLongMin(long min)
	{
		return handleDisplay(UEntryValidation.validateLongMin(getFieldName(), getText(), min));
	}

	/** verifies the number is a long and at least min */
	public List<UEntryError> validateLongMax(long max)
	{
		return handleDisplay(UEntryValidation.validateLongMax(getFieldName(), getText(), max));
	}

	/** verifies the number is a int and between min and max */
	public List<UEntryError> validateIntRange(int min, int max)
	{
		return handleDisplay(UEntryValidation.validateIntRange(getFieldName(), getText(), min, max));
	}

	/** verifies the number is a int and at least min */
	public List<UEntryError> validateLongMin(int min)
	{
		return handleDisplay(UEntryValidation.validateIntMin(getFieldName(), getText(), min));
	}

	/** verifies the number is a int and at least min */
	public List<UEntryError> validateIntMax(int max)
	{
		return handleDisplay(UEntryValidation.validateIntMax(getFieldName(), getText(), max));
	}

	/** verifies the number is a float and between min and max */
	public List<UEntryError> validateFloatRange(float min, float max)
	{
		return handleDisplay(UEntryValidation.validateFloatRange(getFieldName(), getText(), min, max));
	}

	/** verifies the number is a float and at least min */
	public List<UEntryError> validateFloatMin(float min)
	{
		return handleDisplay(UEntryValidation.validateFloatMin(getFieldName(), getText(), min));
	}

	/** verifies the number is a float and at least min */
	public List<UEntryError> validatFloatMax(float max)
	{
		return handleDisplay(UEntryValidation.validateFloatMax(getFieldName(), getText(), max));
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
