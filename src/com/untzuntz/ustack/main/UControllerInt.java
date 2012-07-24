package com.untzuntz.ustack.main;

import java.util.Hashtable;

import nextapp.echo.app.Component;
import nextapp.echo.app.event.ActionEvent;

import com.untzuntz.ustack.data.UserAccount;

/**
 * Interface for the UController
 * @author jdanner
 *
 */
public interface UControllerInt {

	public Component getDefaultView();
	public void loadView(Component comp);
	public UserAccount getUser();
	public void actionPerformed(ActionEvent e);
	
	// Plugin Interface
	public void registerPlugin(UPluginInt plugin);
	public Hashtable<String,Object> getIPC();
	public void loadPlugin(UPluginInt plugin);
	public UPluginInt getPluginById(String id);
	
}
