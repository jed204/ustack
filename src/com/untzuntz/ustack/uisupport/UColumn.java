package com.untzuntz.ustack.uisupport;

import com.untzuntz.ustack.aaa.Authorization;
import com.untzuntz.ustack.aaa.UStackPermissionEnum;

import nextapp.echo.app.Border;
import nextapp.echo.app.Color;
import nextapp.echo.app.Column;
import nextapp.echo.app.Component;
import nextapp.echo.app.Extent;
import nextapp.echo.app.LayoutData;

public class UColumn extends Column {

	private static final long serialVersionUID = 6044020317963575725L;
	
	public UColumn()
	{
		super();
		setCellSpacing(UStackStatics.EX_10);
	}
	
	public UColumn(LayoutData ld)
	{
		super();
		setCellSpacing(UStackStatics.EX_10);
		setLayoutData(ld);
	}
	
	public UColumn(Extent sp)
	{
		setCellSpacing(sp);
	}
	
	public UColumn(Component comp, Border bdr, Color bgColor)
	{
		add(comp);
		setBackground(bgColor);
		setInsets(UStackStatics.IN_5);
		setBorder(bdr);
	}
	
	public void addIfAllow(UStackPermissionEnum perm, Component comp)
	{
		if (Authorization.authUserApp(perm))
			add(comp);
	}
	
	public void addIfAllow(String resource, UStackPermissionEnum perm, Component comp)
	{
		if (Authorization.authorizeCurrentUser(resource, perm))
			add(comp);
	}

}
