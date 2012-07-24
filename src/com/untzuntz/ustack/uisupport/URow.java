package com.untzuntz.ustack.uisupport;

import com.untzuntz.ustack.aaa.Authorization;
import com.untzuntz.ustack.aaa.UStackPermissionEnum;

import nextapp.echo.app.Component;
import nextapp.echo.app.Extent;
import nextapp.echo.app.LayoutData;
import nextapp.echo.app.Row;

public class URow extends Row {

	private static final long serialVersionUID = 6044320317963575725L;
	
	public URow(Component comp)
	{
		super();
		add(comp);
	}

	public URow(LayoutData layout)
	{
		super();
		setCellSpacing(UStackStatics.EX_10);
		setLayoutData(layout);
	}

	public URow(Component comp, LayoutData layout)
	{
		super();
		add(comp);
		setLayoutData(layout);
	}

	public URow()
	{
		super();
		setCellSpacing(UStackStatics.EX_10);
	}
	
	public URow(Extent sp)
	{
		setCellSpacing(sp);
	}
	
	public URow(Extent sp, LayoutData layout)
	{
		setCellSpacing(sp);
		setLayoutData(layout);
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
