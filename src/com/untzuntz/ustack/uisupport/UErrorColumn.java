package com.untzuntz.ustack.uisupport;

import java.util.List;

import nextapp.echo.app.Color;
import nextapp.echo.app.Column;

import com.untzuntz.ustack.main.Msg;

public class UErrorColumn extends Column {

	private static final long serialVersionUID = 604402337263575725L;
	
	public UErrorColumn()
	{
		setCellSpacing(UStackStatics.EX_2);
		setForeground(Color.RED);
	}

	/** Sets the list of errors to be displayed by this column */
	public void setErrorList(List<UEntryError> errors)
	{
		removeAll();
		
		if (errors != null && errors.size() > 0)
		{
			for (UEntryError err : errors)
				if (err.isTopLevel())
				{
					add(new ULabel(err.getErrorMessage(), UStackStatics.FONT_NORMAL_BOLD));
					return;
				}
			
			add(new ULabel(Msg.getString("EntryErrors") + ":", UStackStatics.FONT_NORMAL_BOLD));
			
			for (UEntryError err : errors)
				add(new ULabel(" - " + err.getErrorMessage(), UStackStatics.FONT_NORMAL_BOLD));
		}
	}

}
