package com.untzuntz.ustack.uisupport;

import nextapp.echo.app.Border;
import nextapp.echo.app.Extent;
import echopoint.Strut;

public class ULine extends Strut {

	private static final long serialVersionUID = 1L;

	public ULine()
	{
		setHeight(UStackStatics.EX_0);
		setWidth(UStackStatics.EX_100P);
		setBorder(UStackStatics.BDR_SIMPLE);
	}

	public ULine(Border bdr)
	{
		setHeight(UStackStatics.EX_0);
		setWidth(UStackStatics.EX_100P);
		setBorder(bdr);
	}

	public ULine(int ex)
	{
		setHeight(UStackStatics.EX_0);
		setWidth(new Extent(ex));
		setBorder(UStackStatics.BDR_SIMPLE);
	}

}
