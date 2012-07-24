package com.untzuntz.ustack.uisupport;

import nextapp.echo.app.RadioButton;
import nextapp.echo.app.button.ButtonGroup;

public class URadioButton extends RadioButton {

	private static final long serialVersionUID = 1L;

	public URadioButton(String displayText, ButtonGroup bg, String id)
	{
		super(displayText);
		setId(id);
		setGroup(bg);
	}
	
	public URadioButton(String displayText, ButtonGroup bg, String id, boolean selected)
	{
		super(displayText);
		setId(id);
		setGroup(bg);
		setSelected(selected);
	}
	
	public static String getSelectedButtonId(ButtonGroup bg)
	{
		for (RadioButton btn : bg.getButtons())
			if (btn.isSelected())
				return btn.getId();
		
		return null;
	}
	
}
