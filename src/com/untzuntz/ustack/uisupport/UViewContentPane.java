package com.untzuntz.ustack.uisupport;

import nextapp.echo.app.ContentPane;
import nextapp.echo.app.event.ActionEvent;
import nextapp.echo.app.event.ActionListener;

import org.apache.log4j.Logger;

import com.untzuntz.ustack.main.UControllerInt;

public abstract class UViewContentPane extends ContentPane implements ActionListener {

	protected static Logger   	logger           = Logger.getLogger(UViewContentPane.class);
	private static final long 	serialVersionUID = 6044020317933575725L;

private UControllerInt controller;
	
	protected UControllerInt getController() { return controller; }

	public UViewContentPane(UControllerInt ctrl)
	{
		super();
		controller = ctrl;
		internalSetup();
	}
	
	public UViewContentPane(UControllerInt ctrl, boolean setup)
	{
		super();
		controller = ctrl;
		if (setup)
			internalSetup();
	}
	
	protected UViewContentPane() {}
	
	protected void internalSetup()
	{
		setup();
	}
	
	public abstract void setup();
	
	public void actionPerformed(ActionEvent e) {

		getController().actionPerformed(e);
		
	}

	
}
