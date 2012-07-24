package com.untzuntz.ustack.uisupport;

import java.util.List;
import java.util.Vector;

import com.untzuntz.ustack.aaa.LinkActionInterface;
import com.untzuntz.ustack.data.UserAccount;

import nextapp.echo.app.Column;
import nextapp.echo.app.event.ActionEvent;
import nextapp.echo.app.event.ActionListener;

abstract public class LinkUIComponent extends Column implements LinkActionInterface {

	private static final long serialVersionUID = 1L;

	private List<ActionListener> actions;
	private String actionCommand;
	private UserAccount user;
	private String type;
	private String role;
	
	public String getType() {
		return type;
	}
	
	public String getRole() {
		return role;
	}
	
	public void setRole(String role) {
		this.role = role;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void addActionListener(ActionListener al)
	{
		if (actions == null)
			actions = new Vector<ActionListener>();
		
		actions.add(al);
	}
	
	public void setActionCommand(String cmd) {
		actionCommand = cmd;
	}
	
	public void setUser(UserAccount user) 
	{
		this.user = user;
	}
	
	public UserAccount getUser()
	{
		return user;
	}

	protected void runActions()
	{
		if (actions == null)
			return;
		
		ActionEvent ae = new ActionEvent(this, actionCommand);
		for (ActionListener al : actions)
			al.actionPerformed(ae);
	}
	
}
