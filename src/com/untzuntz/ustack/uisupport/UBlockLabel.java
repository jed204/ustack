package com.untzuntz.ustack.uisupport;

import nextapp.echo.app.Extent;
import nextapp.echo.app.Font;
import nextapp.echo.app.Insets;
import nextapp.echo.app.Label;
import echopoint.ContainerEx;

public class UBlockLabel extends ContainerEx {

	private static final long serialVersionUID = 1L;

	public UBlockLabel(String txt, Extent width)
	{
		setWidth(width);
		add(new Label(txt));
	}
	
	public UBlockLabel(String txt, Extent width, Insets inset)
	{
		setWidth(width);
		setInsets(inset);
		add(new Label(txt));
	}
	
	public UBlockLabel(String txt, Font font, Extent width)
	{
		setWidth(width);
		add(new ULabel(txt, font));
	}
	
	public UBlockLabel(String txt, Font font, Extent width, Insets inset)
	{
		setWidth(width);
		setInsets(inset);
		add(new ULabel(txt, font));
	}
	
}
