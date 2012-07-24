package com.untzuntz.ustack.uisupport;

import nextapp.echo.app.Button;
import nextapp.echo.app.Font;
import nextapp.echo.app.ImageReference;
import nextapp.echo.app.Style;
import nextapp.echo.app.event.ActionListener;

public class UButton extends Button {

	private static final long serialVersionUID = -9220779864616616581L;
	private String ipc;
	
	public String getIPC() { return ipc; }
	
	private UButton() {}
	
	public static UButton getIPCButton(String ipc)
	{
		UButton n = new UButton();
		n.ipc = ipc;
		return n;
	}

	public UButton(String txt, ActionListener act, String actionCmd)
	{
		super(txt);
		
		addActionListener(act);
		setActionCommand(actionCmd);
	}
	
	public UButton(String txt, Font font, ActionListener act, String actionCmd)
	{
		super(txt);
		
		setFont(font);
		addActionListener(act);
		setActionCommand(actionCmd);
	}
	
	public UButton(String txt, Style style, ActionListener act, String actionCmd)
	{
		super(txt);
		
		setStyle(style);
		addActionListener(act);
		setActionCommand(actionCmd);
	}
	
	public UButton(String txt, Style style, ActionListener act, String actionCmd, ImageReference icn)
	{
		super(txt);
		
		setStyle(style);
		addActionListener(act);
		setActionCommand(actionCmd);
		setIcon(icn);
	}
	
	public UButton(String txt, Style style, ActionListener act, String actionCmd, String ipc)
	{
		super(txt);
		this.ipc = ipc;
		
		setStyle(style);
		addActionListener(act);
		setActionCommand(actionCmd);
	}
	
}
