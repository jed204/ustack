package com.untzuntz.ustack.uisupport;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import nextapp.echo.app.Column;
import nextapp.echo.app.Component;
import nextapp.echo.app.event.ActionEvent;
import nextapp.echo.app.event.ActionListener;

import org.apache.log4j.Logger;

import com.untzuntz.ustack.main.ApplicationInstance;
import com.untzuntz.ustack.main.UControllerInt;

public abstract class UViewColumn extends Column implements ActionListener {

	protected static Logger   	logger           = Logger.getLogger(UViewColumn.class);
	private static final long 	serialVersionUID = 6044020317933575725L;
	private UControllerInt controller;
	
	protected UControllerInt getController() { return controller; }

	public UViewColumn(UControllerInt ctrl)
	{
		super();
		controller = ctrl;
		internalSetup();
	}
	
	public UViewColumn(UControllerInt ctrl, boolean setup)
	{
		super();
		controller = ctrl;
		if (setup)
			internalSetup();
	}
	
	protected void setController(UControllerInt ctrl) {
		controller = ctrl;
	}
	
	protected UViewColumn() {}
	
	protected void internalSetup()
	{
		setCellSpacing(UStackStatics.EX_10);
		setup();
	}
	
	public abstract void setup();
	
	public void actionPerformed(ActionEvent e) {

		String cmd = e.getActionCommand();
		if (cmd.startsWith("focusOn-"))
		{
			try {
				Field f = this.getClass().getDeclaredField(cmd.substring(8));
				f.setAccessible(true);
				
				Object obj = null;
				if (f != null && (obj = f.get(this)) instanceof Component)
				{
					Component c = (Component)obj;
					ApplicationInstance.getActive().setFocusedComponent(c);
				}
			} catch (Exception err) {
				logger.warn("Failed to focus on: " + cmd, err);
			}
			return;
		}
		

		getController().actionPerformed(e);
		
	}
	
	/**
	 * Takes an action event and processes the command by looking at methods on the 'src' class. 
	 * 
	 * If 'actionCommand(Component comp)' is found we use that, if 'actionCommand()' is found we use that, otherwise do nothing
	 * 
	 * Example:
	 * 
	 * You create a button with an action command of 'testThisOut'
	 * 
	 * We look at the class you send it for the method 'testThisOut(Component comp)', if found we call that method.
	 * If not found we look for 'testThisOut()', if found we call that
	 * 
	 * 
	 * @param src
	 * @param e
	 */
	public static void processClassCommand(Object src, ActionEvent e)
	{
		boolean found = false;
		String cmd = null;
		try {
			cmd = e.getActionCommand();
			
			@SuppressWarnings("rawtypes") Class[] partypes = new Class[]{Component.class};
			Method m = src.getClass().getMethod(cmd, partypes);
			
			Object[] arglist = new Object[]{ (Component)e.getSource() };
			m.invoke(src, arglist);
			found = true;
				
		} catch (NoSuchMethodException err) {
			logger.debug(src.getClass().getName() + " -> No Method for '" + e.getActionCommand() + "(Component)'");
		} catch (Exception err) {
			logger.error("Failed to execute method: " + e.getActionCommand() + "(Component)", err);
		}
		
		if (!found)
		{
			try {
				@SuppressWarnings("rawtypes") Class[] partypes = new Class[]{};
				Method m = src.getClass().getMethod(cmd, partypes);
				m.invoke(src);
				found = true;
			} catch (NoSuchMethodException err) {
				logger.debug(src.getClass().getName() + " -> No Method for '" + e.getActionCommand() + "()'");
			} catch (Exception err) {
				logger.error("Failed to execute method: " + e.getActionCommand() + "()", err);
			}
		}
	}

	
}
