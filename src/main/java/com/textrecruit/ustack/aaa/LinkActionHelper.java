package com.textrecruit.ustack.aaa;

import java.util.Iterator;

import org.apache.log4j.Logger;

import com.mongodb.BasicDBList;
import com.mongodb.DBObject;
import com.textrecruit.ustack.data.NotificationInst;
import com.textrecruit.ustack.data.NotificationTemplate;
import com.textrecruit.ustack.data.SiteAccount;
import com.textrecruit.ustack.data.UntzDBObject;
import com.textrecruit.ustack.data.UntzDBObjectTemplate;
import com.textrecruit.ustack.data.UserAccount;

public class LinkActionHelper {

	private static Logger logger = Logger.getLogger(LinkActionHelper.class);

	/**
	 * Loads the LinkActionInterface based on a class name
	 * 
	 * @param className
	 * @return
	 */
	public static LinkActionInterface getLinkAction(String className)
	{
		if (className == null)
			return null;
		
		String type = null;
		String role = null;
		if (className.indexOf("[") > -1)
		{
			String tr = className.substring( className.indexOf("[") + 1, className.length() - 1 );
			if (tr.indexOf("/") > -1)
			{
				type = tr.substring(0, tr.indexOf("/"));
				role = tr.substring(tr.indexOf("/") + 1);
			}
			else
				type = tr;
			
			className = className.substring(0, className.indexOf("["));
		}

		LinkActionInterface ret = null;
		
		try {
			ret = (LinkActionInterface)LinkActionHelper.class.getClassLoader().loadClass(className).newInstance();
			ret.setType(type);
			ret.setRole(role);
		} catch (Exception err) {
			logger.error("Failed to load LinkActionInterface", err);
		}

		return ret;
	}

	/**
	 * Called when a link is being requested to be added
	 * @param resourceLink
	 * @param user
	 */
	public static void handleLinkAddAction(String actor,ResourceLink resourceLink, UntzDBObject obj)
	{
		ResourceDefinition def = ResourceDefinition.getByName( resourceLink.getName() );
		if (def == null) {
			logger.error("Cannot find resource link: " + resourceLink.getName());
		}
		
		LinkActionInterface linkAction = getLinkAction(def.getLinkActionClass());
		if (linkAction != null)
			linkAction.linkCreated(obj, resourceLink);
		
		RoleDefinition roleDef = def.getRoleByName(resourceLink.getRoleName());
		if (roleDef.getLinkActionClass() != null)
		{
			linkAction = LinkActionHelper.getLinkAction(roleDef.getLinkActionClass());
			if (linkAction != null)
				linkAction.linkCreated(obj, resourceLink);
		}
		
		if (roleDef.getObjectTemplate() != null)
		{
			UntzDBObjectTemplate templ = UntzDBObjectTemplate.getTemplate(roleDef.getObjectTemplate());
			if (templ != null)
				applyTemplate(actor, resourceLink, templ, obj);
		}
		
		BasicDBList list = roleDef.getAdditionalResRolesList();
		for (int i = 0; i < list.size(); i++)
		{
			DBObject linkData = (DBObject)list.get(i);
			String resName = (String)linkData.get("resName");
			String role = (String)linkData.get("role");
			
			ResourceDefinition rdef = ResourceDefinition.getByName( resName );

			ResourceLink newLink = new ResourceLink(resourceLink);
			newLink.loadData(rdef, role);
			
			logger.info("Attempting to add new link [" + resName + "/" + role + "]");
			obj.addResourceLinkIfNeeded(actor, newLink);
		}

		if (obj instanceof UserAccount)
		{
			SiteAccount site = null;
			if (resourceLink.get("siteId") != null)
				site = SiteAccount.getSiteById(resourceLink.getString("siteId"));
			
			UserAccount user = (UserAccount)obj;
			BasicDBList sublist = roleDef.getAutoSubscriptionList();
			for (int i = 0; i < sublist.size(); i++)
			{
				DBObject sub = (DBObject)sublist.get(i);
				
				String event = (String)sub.get("event");
				String method = (String)sub.get("method");
				
				NotificationTemplate notifTempl = NotificationTemplate.getNotificationTemplate(event);
				if (notifTempl != null)
				{
					NotificationInst ni1 = NotificationInst.subscribe(notifTempl, notifTempl.getEventName(), user.getUserName());
					if ("sms".equalsIgnoreCase(method))
						ni1.addType("sms", user.getPrimaryTelephoneString()); 
					if ("email".equalsIgnoreCase(method))
						ni1.addType("email", user.getPrimaryEmail());
					
					if (site != null)
					{
						ni1.put("siteId", site.getSiteId());
						ni1.put("linkText", site.getSiteName());
					}
					
					ni1.save(user.getUserName());
				}
				else
					logger.error("Failed to locate template by event name '" + event + "'");
			}		
		}
	}
	
	public static void applyTemplate(String actor, ResourceLink resourceLink, UntzDBObjectTemplate templ, UntzDBObject target)
	{
		BasicDBList tObjectList = templ.getTemplateObjectList();
		for (int i = 0; i < tObjectList.size(); i++)
		{
			DBObject obj = (DBObject)tObjectList.get(i);
			
			String field = (String)obj.get("field");
			String op = (String)obj.get("op");
			Object val = obj.get("value");
			
			if ("add/replace".equalsIgnoreCase(op))
			{
				// try to replace site id's and usernames if we can...
				if ("resourceLinkList".equalsIgnoreCase(field) && val instanceof BasicDBList)
				{
					BasicDBList list = (BasicDBList)val;
					for (i = 0; i < list.size(); i++)
					{
						DBObject item = (DBObject)list.get(i);
						
						Iterator<String> it = item.keySet().iterator();
						while (it.hasNext())
						{
							String key = it.next();
							if (item.get(key) instanceof String)
							{
								String iVal = (String)item.get(key);
								if (iVal.startsWith("${REPLACE-"))
								{
									String rField = iVal.substring(10, iVal.length() - 1);
									item.put(rField, resourceLink.get(rField));
								}
							}
						}
						
						target.addResourceLink(actor, new ResourceLink(item));
					}
				}
				else
					target.put(field, val);
			}
		}
	}

	/**
	 * Called when a link is being requested to be removed
	 * @param resourceLink
	 * @param user
	 */
	public static void handleLinkRemoveAction(ResourceLink resourceLink, UntzDBObject obj)
	{
		ResourceDefinition def = ResourceDefinition.getByName( resourceLink.getName() );

		if (def == null)
			return;
		
		LinkActionInterface linkAction = getLinkAction(def.getLinkActionClass());
		if (linkAction != null)
			linkAction.linkRemoved(obj, resourceLink);
		
		RoleDefinition roleDef = def.getRoleByName(resourceLink.getRoleName());
		if (roleDef.getLinkActionClass() != null)
		{
			linkAction = LinkActionHelper.getLinkAction(roleDef.getLinkActionClass());
			if (linkAction != null)
				linkAction.linkRemoved(obj, resourceLink);
		}
	}
	
}
