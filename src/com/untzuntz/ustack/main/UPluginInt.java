package com.untzuntz.ustack.main;

import java.util.List;

import com.untzuntz.ustack.data.UserAccount;
import com.untzuntz.ustack.uisupport.UEntryError;

import nextapp.echo.app.Component;


public interface UPluginInt {

	public void setController(UControllerInt ctrl);
	public Component getComponent();
	public String getDisplayName();
	public String getVersionId();
	public void setUser(UserAccount actor);
	public void save();
	public List<UEntryError> validate();
	
}
