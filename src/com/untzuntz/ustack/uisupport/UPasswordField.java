package com.untzuntz.ustack.uisupport;

import java.util.List;

import nextapp.echo.app.Color;
import nextapp.echo.app.Extent;
import nextapp.echo.app.Font;
import nextapp.echo.app.PasswordField;
import nextapp.echo.app.event.ActionListener;

public class UPasswordField extends PasswordField {

	private static final long serialVersionUID = -2538144124765761983L;

	protected String fieldName;
	
	public String getFieldName() { return fieldName; }

	public UPasswordField(String fieldName, Extent size)
	{
		super();
		this.fieldName = fieldName;
		setWidth(size);
	}

	public UPasswordField(String fieldName, Extent size, Extent height, Font font)
	{
		super();
		this.fieldName = fieldName;
		setWidth(size);
		setHeight(height);
		setInsets(UStackStatics.IN_2);
		
		if (font != null)
			setFont(font);
	}
	
	public UPasswordField(String fieldName, Extent size, ActionListener al, String cmd)
	{
		super();
		this.fieldName = fieldName;
		setWidth(size);
		addActionListener(al);
		setActionCommand(cmd);
	}


	public List<UEntryError> validatePassword(UPasswordField other)
	{
		List<UEntryError> list = UEntryError.getEmptyList(); // no errors found
		
		list.addAll(com.untzuntz.ustack.aaa.Authentication.verifyPasswordRequirements(getText(), other.getText()));
		
		return handleDisplay(other, list);
	}

	/** Mark the field in error */
	public void error(UPasswordField me)
	{
		me.setBackground(Color.RED);
		me.setForeground(Color.WHITE);
	}
	
	/** Mark the field as passed */
	public void pass(UPasswordField me)
	{
		me.setBackground(null);
		me.setForeground(null);
	}
	
	/** verifies the string is at least minLength in characters */
	public List<UEntryError> validateEntry()
	{
		return handleDisplay(UEntryValidation.validateString(getFieldName(), getText(), 1, -1));
	}

	/** called to check on pass or fail of this field */
	private List<UEntryError> handleDisplay(List<UEntryError> list)
	{
		if (list.size() > 0)
			error(this);
		else
			pass(this); // clears previous errors
		
		return list;
	}
	

	/** called to check on pass or fail of this field */
	private List<UEntryError> handleDisplay(UPasswordField other, List<UEntryError> list)
	{
		if (list.size() > 0)
		{
			error(this);
			error(other);
		}
		else
		{
			pass(this); // clears previous errors
			pass(other);
		}
		
		return list;
	}
}
