package com.textrecruit.ustack.data;

import java.util.Date;
import java.util.List;
import java.util.Vector;

import com.textrecruit.ustack.main.UAppCfg;
import org.apache.log4j.Logger;
import org.bson.types.ObjectId;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.textrecruit.ustack.exceptions.ObjectExistsException;
import com.textrecruit.ustack.main.UOpts;

/**
 * A email/sms/ios/etc template for sending data to users based on notifications
 * 
 * @author jdanner
 *
 */
public class NotificationTemplate extends UntzDBObject {

	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(NotificationTemplate.class);

	@Override
	public String getCollectionName() { return "notificationTempl"; }

	public NotificationTemplate(boolean testMode) {
		put("created", new Date());
	}

	private NotificationTemplate()
	{
		// setup basic values on account
		put("created", new Date());
	}
	
	public String toString() {
		return getEventName();
	}

	public String getTemplateId() {
		return get("_id") + "";
	}

	/** Gets the DB Collection for the NotificationTemplate object */
	public static DBCollection getDBCollection() {
		return new NotificationTemplate().getCollection();
	}

	/** Return the name of the database that houses the 'notificationTempl' collection */
	public static final String getDatabaseName() {
		
		if (UOpts.getString(UAppCfg.DATABASE_NOTIFICATION_TEMPL_COL) != null)
			return UOpts.getString(UAppCfg.DATABASE_NOTIFICATION_TEMPL_COL);
		
		return UOpts.getAppName();
	}

	/**
	 * Generate a NotificationTemplate object from the MongoDB object
	 * @param user
	 */
	public NotificationTemplate(DBObject templ) {
		super(templ);
	}

	/** Sets the eventName */
	private void setEventName(String eventName)
	{
		put("eventName", eventName);
	}

	/** Returns the eventName */
	public String getEventName()
	{
		return getString("eventName");
	}
	
	/** Add a notification type */
	public void addType(String typeName, DBObject notiData)
	{
		DBObject curType = getType(typeName);
		
		BasicDBList list = getTypeList();
		if (curType != null)
			list.remove(curType);
		
		curType = new BasicDBObject();
		curType.put("name", typeName);
		curType.putAll(notiData);
		list.add(curType);
		setTypeList(list);
	}
	
	/** Remove a type by name */
	public void removeType(String typeName)
	{
		BasicDBList list = getTypeList();
		for (int i = 0; i < list.size(); i++)
		{
			DBObject obj = (DBObject)list.get(i);
			if (typeName.equalsIgnoreCase( (String)obj.get("name") ))
			{
				list.remove(i);
				i--;
			}
		}
	}

	/** Returns a notification type by name */
	public DBObject getType(String typeName)
	{
		BasicDBList list = getTypeList();
		for (int i = 0; i < list.size(); i++)
		{
			DBObject obj = (DBObject)list.get(i);
			if (typeName.equalsIgnoreCase( (String)obj.get("name") ))
				return obj;
		}
		
		return null;
	}

	/**
	 * Returns the type list (or types of notifications to send). May include additional information about when to use certain notifications
	 * @return
	 */
	private BasicDBList getTypeList() {
		BasicDBList ret = (BasicDBList)get("typeList");
		if (ret == null)
			ret = new BasicDBList();
		
		return ret;
	}

	/**
	 * Set the type list for determining which types of alerts to send - should match up against the notificationtemplate
	 * @param list
	 */
	private void setTypeList(BasicDBList list)
	{
		put("typeList", list);
	}
	
	/**
	 * Action class that can optionally support:
	 * 
	 * 1. Link selection support
	 * 2. New link action
	 * 3. Remove link action
	 * 
	 * @param actionClass
	 */
	public void setLinkActionClass(String actionClass)
	{
		if (actionClass == null || actionClass.length() == 0)
			removeField("linkActionClass");
		else
			put("linkActionClass", actionClass);
	}

	/**
	 * Returns the class responsible for link activities
	 * 
	 * @return
	 */
	public String getLinkActionClass()
	{
		return getString("linkActionClass");
	}
	
	
	
	/**
	 * Create a new NotificationTemplate for an eventName
	 * 
	 * @param name
	 * @return
	 * @throws ObjectExistsException
	 */
	public static NotificationTemplate createTemplate(String eventName) throws ObjectExistsException
	{
		if (eventName == null || eventName.length() == 0)
			return null;
		
		NotificationTemplate templ = getNotificationTemplate(eventName);
		if (templ != null) // already exists
			throw new ObjectExistsException();
		
		// create the actual account
		templ = new NotificationTemplate();
		templ.setEventName(eventName);
		logger.info("Creating notification template '" + eventName + "'");
		
		return templ;
	}

	/**
	 * Get a notification template object by eventName
	 * 
	 * @param name
	 * @return
	 */
	public static NotificationTemplate getNotificationTemplate(String eventName)
	{
		if (eventName == null || eventName.length() == 0)
			return null;
		
		DBObject obj = null;
		try {
			obj = new NotificationTemplate().getCollection().findOne(BasicDBObjectBuilder.start("eventName", eventName).get());
		} catch (Exception exp) { 
			return null;
		}
		
		if (obj == null)
			return null;
		
		return new NotificationTemplate(obj);
	}

	/** Returns the T&Cs by ID */
	public static NotificationTemplate getByGUID(String guid)
	{
		if (guid == null || guid.length() == 0)
			return null;
		
		DBObject obj = null;
		try {
			obj = new NotificationTemplate().getCollection().findOne(BasicDBObjectBuilder.start("_id", new ObjectId(guid)).get());
		} catch (Exception exp) { 
			return null;
		}
		
		if (obj == null)
			return null;
		
		return new NotificationTemplate(obj);
	}

	/** Returns all Templates */
	public static List<NotificationTemplate> getAll()
	{
		List<NotificationTemplate> ret = new Vector<NotificationTemplate>();
		DBCollection col = getDBCollection();
		DBCursor cur = col.find().sort(new BasicDBObject("eventName", 1));
		
		while (cur.hasNext())
			ret.add(new NotificationTemplate(cur.next()));
		
		return ret;
	}
	
	/**
	 * Returns all NotificationTemplates
	 * @return
	 */
	public static List<NotificationTemplate> getAll(BasicDBList managedByList)
	{
		DBObject search = new BasicDBObject();
		if (managedByList != null)
		{
			String[] mbArray = new String[managedByList.size()];
			for (int i = 0; i < managedByList.size(); i++)
				mbArray[i] = (String)managedByList.get(i);
			
			search.put("managedByList", BasicDBObjectBuilder.start("$in", mbArray).get());
		}
		
		List<NotificationTemplate> ret = new Vector<NotificationTemplate>();
		DBCursor cur = new NotificationTemplate().getCollection().find(search).sort( BasicDBObjectBuilder.start("eventName", 1).get() );
		while (cur.hasNext())
			ret.add(new NotificationTemplate(cur.next()));
		
		return ret;
	}
	


}
