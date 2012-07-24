package com.untzuntz.ustack.main;

import java.lang.reflect.Method;
import java.util.List;

import nextapp.echo.app.Component;
import nextapp.echo.app.event.ActionEvent;
import nextapp.echo.app.event.ActionListener;

import org.apache.log4j.Logger;

import com.untzuntz.ustack.data.UserAccount;
import com.untzuntz.ustack.uisupport.UEntryError;

/**
 * Base UPlugin class to support minimal functionality
 * 
 * @author jdanner
 *
 */
abstract public class UPlugin implements UPluginInt,ActionListener {
	
	private static Logger logger = Logger.getLogger(UPlugin.class);
	private static final long serialVersionUID = 1L;
	private UControllerInt controller;
	private UserAccount currentUser;

	public UControllerInt getController() { 
		return controller; 
	}
	
	public UserAccount getUser() {
		return currentUser;
	}
	
	public void setUser(UserAccount actor)
	{
		currentUser = actor;
	}

	public void setController(UControllerInt ctrl) {
		controller = ctrl;
		controller.registerPlugin(this);
	}

	public void actionPerformed(ActionEvent e) {
		
		String action = e.getActionCommand();
		if (action.startsWith("loadPlugin."))
		{
			controller.actionPerformed(e);
			return;
		}

		boolean found = false;
		try {
			
			Class[] partypes = new Class[]{Component.class};
			Method m = getClass().getMethod(action, partypes);
			
			Object[] arglist = new Object[]{ (Component)e.getSource() };
			m.invoke(this, arglist);
			found = true;
				
		} catch (NoSuchMethodException err) {
			//logger.warn(getClass().getName() + " -> No Method for '" + e.getActionCommand() + "(Component)'");
		} catch (Exception err) {
			logger.warn(getClass().getName() + " -> Error while executing '" + e.getActionCommand() + "(Component)'", err);
			found = true;
		}
		
		if (!found)
		{
			try {
				Class[] partypes = new Class[]{};
				Method m = getClass().getMethod(action, partypes);
				m.invoke(this);
				found = true;
			} catch (NoSuchMethodException err) {
			} catch (Exception err) {
				logger.warn(getClass().getName() + " -> Error while executing '" + e.getActionCommand() + "()'", err);
			}
		}		
		
		if (!found)
			controller.actionPerformed(e);

		
	}
	
	public List<UEntryError> validate() {
		return UEntryError.getEmptyList();
	}
	
	public void save() {
		// stub
	}
	
}
