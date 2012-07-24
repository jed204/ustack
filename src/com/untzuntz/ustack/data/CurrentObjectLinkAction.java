package com.untzuntz.ustack.data;

import nextapp.echo.app.event.ActionEvent;
import nextapp.echo.app.event.ActionListener;

import org.apache.log4j.Logger;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.untzuntz.ustack.aaa.LinkActionInterface;
import com.untzuntz.ustack.aaa.ResourceLink;
import com.untzuntz.ustack.uisupport.LinkUIComponent;

/**
 * Links a user or site to the current object
 * 
 * @author jdanner
 *
 */
public class CurrentObjectLinkAction extends LinkUIComponent implements LinkActionInterface,ActionListener {

	private static Logger logger = Logger.getLogger(CurrentObjectLinkAction.class);
	
	private static final long serialVersionUID = 1L;
		
	public LinkUIComponent getLinkSelectUI() {
		return null;
	}
	
	public DBObject getResourceLinkExtras(ResourceLink link) {
		DBObject ret = new BasicDBObject();
		return ret;
	}

	public void linkCreated(UntzDBObject curObj, ResourceLink link) {
		
		logger.info("Link called ==> Incoming Object: " + curObj);
		if (curObj.get("userName") != null)
		{
			logger.info("Link created --- using current user: " + curObj.get("userName"));
			link.put("userName", curObj.get("userName"));
			link.put("linkText", curObj.get("userName"));
		}
		else if (curObj.get("siteId") != null)
		{
			logger.info("Link created --- using current site: " + curObj.get("siteId"));
			link.put("siteId", curObj.get("siteId"));
			link.put("linkText", curObj.get("siteName"));
		}
	}

	public void linkRemoved(UntzDBObject user, ResourceLink link) {
	}

	public void actionPerformed(ActionEvent e) {
	}
	
}
