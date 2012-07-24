package com.untzuntz.ustack.uisupport;

import nextapp.echo.app.Grid;
import nextapp.echo.app.Insets;
import nextapp.echo.app.Label;

public class UGrid extends Grid {

	private static final long serialVersionUID = -1673340057515431903L;

	public UGrid(int size, Insets insets)
	{
		super(size);
		setInsets(insets);
	}
	
	public void add(String txt)
	{
		add(new Label(txt));
	}
	
}
