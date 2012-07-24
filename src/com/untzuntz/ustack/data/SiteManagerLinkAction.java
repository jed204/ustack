package com.untzuntz.ustack.data;

import nextapp.echo.app.event.ActionListener;

import com.mongodb.BasicDBList;
import com.mongodb.DBObject;
import com.untzuntz.ustack.aaa.LinkActionInterface;
import com.untzuntz.ustack.aaa.ResourceLink;

/**
 * Marks a user as the manager of a site record
 * 
 * @author jdanner
 *
 */
public class SiteManagerLinkAction extends UserLinkAction implements LinkActionInterface,ActionListener {

	private static final long serialVersionUID = 1L;

	public DBObject getResourceLinkExtras(ResourceLink link) {
		DBObject ret = super.getResourceLinkExtras(link);
		
		ret.put("linkText", "Managed by: " + searchTable.getSelectedObject().get("userName"));
		
		BasicDBList managedBy = (BasicDBList)link.get("managedBy");
		if (managedBy == null)
			managedBy = new BasicDBList();
		managedBy.add(searchTable.getSelectedObject().get("userName"));
		link.put("managedBy", managedBy);

		return ret;
	}

}
