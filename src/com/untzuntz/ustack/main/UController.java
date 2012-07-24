package com.untzuntz.ustack.main;

import java.lang.reflect.Method;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;

import nextapp.echo.app.Component;
import nextapp.echo.app.event.ActionEvent;

import com.untzuntz.ustack.data.UserAccount;
import com.untzuntz.ustack.uisupport.UButton;

/**
 * Base UController as a starter for classes using the UControllerInt interface
 * @author jdanner
 *
 */
abstract public class UController implements UControllerInt {

	private static Logger logger = Logger.getLogger(UController.class);

	abstract public Component getDefaultView();
	protected List<UPluginInt> loadedPlugins;
	protected Hashtable<String,Object> ipc;
	
	public void registerPlugin(UPluginInt plugin) {
		if (loadedPlugins == null)
			loadedPlugins = new Vector<UPluginInt>();
		
		loadedPlugins.add(plugin);
	}
	
	
	public Hashtable<String,Object> getIPC() {
		if (ipc == null)
			ipc = new Hashtable<String,Object>();
		return ipc;
	}
	
	public UPluginInt getPluginById(String id)
	{
		if (loadedPlugins == null)
			return null;
		
		for (UPluginInt plugin : loadedPlugins)
		{
			if (id.equalsIgnoreCase(plugin.getVersionId()))
				return plugin;
		}
		return null;
	}

	public UserAccount getUser() {
		return ((UControllerInt)ApplicationInstance.getActive()).getUser();
	}
	
	abstract public void loadPlugin(UPluginInt plugin);
	
	public void actionPerformed(ActionEvent e) {
		
		String action = e.getActionCommand();
		if (action.startsWith("loadPlugin."))
		{
			if (e.getSource() instanceof UButton)
			{
				String ipc = ((UButton)e.getSource()).getIPC();
				if (ipc != null)
					getIPC().put("default", ipc );
				else
					getIPC().clear();
			}
			
			String[] split = action.split("\\.");
			UPluginInt plugin = getPluginById(split[1]);
			if (plugin != null)
				loadPlugin(plugin);
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
			((UControllerInt)ApplicationInstance.getActive()).actionPerformed(e);
		
	}
	
	protected void executeAction(String name, Object obj)
	{
		ActionEvent ae = new ActionEvent(obj, name);
		((UControllerInt)ApplicationInstance.getActive()).actionPerformed(ae);
	}

}
