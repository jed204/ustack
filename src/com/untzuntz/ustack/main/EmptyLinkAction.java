package com.untzuntz.ustack.main;

import nextapp.echo.app.event.ActionEvent;
import nextapp.echo.app.event.ActionListener;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.untzuntz.ustack.aaa.LinkActionInterface;
import com.untzuntz.ustack.aaa.ResourceLink;
import com.untzuntz.ustack.data.UntzDBObject;
import com.untzuntz.ustack.uisupport.LinkUIComponent;

public class EmptyLinkAction extends LinkUIComponent implements LinkActionInterface,ActionListener {

	private static final long serialVersionUID = 1L;
	
	public LinkUIComponent getLinkSelectUI() {
		return null;
	}

	public void actionPerformed(ActionEvent e) {
		
		
	}

	public DBObject getResourceLinkExtras(ResourceLink link) {
		return new BasicDBObject();
	}

	public void linkCreated(UntzDBObject user, ResourceLink link) {
	}

	public void linkRemoved(UntzDBObject user, ResourceLink link) {
	}
	
}
