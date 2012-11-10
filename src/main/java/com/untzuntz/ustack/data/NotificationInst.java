package com.untzuntz.ustack.data;

import java.util.Date;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.bson.types.ObjectId;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.untzuntz.ustack.exceptions.ObjectExistsException;
import com.untzuntz.ustack.main.UAppCfg;
import com.untzuntz.ustack.main.UOpts;

/**
 * A notification instance is a subscription for a user to be notified against
 * 
 * 
 * 
 * @author jdanner
 *
 */
public class NotificationInst extends UntzDBObject {

	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(NotificationInst.class);

	@Override
	public String getCollectionName() { return "notifications"; }

	public NotificationInst(boolean testMode) {
		put("created", new Date());
	}
	
	private NotificationInst()
	{
		// setup basic values on account
		put("created", new Date());
	}
	
	public String toString() {
		return getNotificationId() + " - " + getEventName();
	}

	public String getNotificationId() {
		return get("_id") + "";
	}

	/** Gets the DB Collection for the NotificationInst object */
	public static DBCollection getDBCollection() {
		return new NotificationInst().getCollection();
	}

	/** Return the name of the database that houses the 'notifications' collection */
	public static final String getDatabaseName() {
		
		if (UOpts.getString(UAppCfg.DATABASE_NOTIFICATION_COL) != null)
			return UOpts.getString(UAppCfg.DATABASE_NOTIFICATION_COL);
		
		return UOpts.getAppName();
	}

	/**
	 * Generate a NotificationInst object from the MongoDB object
	 * @param user
	 */
	public NotificationInst(DBObject not) {
		super(not);
	}
	
	public void disable(String reason)
	{
    	put("invalid", reason);
    	save("Error Response");
    	
        logger.warn("Disabling Notification ID [" + getNotificationId() + "] => " + reason);
	}
	
	private void setTemplateId(String id)
	{
		put("templateId", id);
	}
	
	public String getTemplateId()
	{
		return getString("templateId");
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

	/** Sets the userName */
	private void setUserName(String userName)
	{
		put("userName", userName);
	}

	/** Returns the userName */
	public String getUserName()
	{
		return getString("userName");
	}

	/** Add a notification type */
	public void addType(String typeName, String destination)
	{
		BasicDBList list = getTypeList();
		DBObject curType = new BasicDBObject();
		curType.put("name", typeName);
		if (destination != null)
			curType.put("destination", destination);
		
		list.add(curType);
		setTypeList(list);
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
	public BasicDBList getTypeList() {
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
	
	public static NotificationInst subscribe(NotificationTemplate template, String eventName) 
	{
		if (template == null)
			return null;
		if (eventName == null || eventName.length() == 0)
			return null;
		
		// create the actual account
		NotificationInst notif = new NotificationInst();
		notif.setTemplateId(template.getTemplateId());
		notif.setEventName(eventName);
		
		BasicDBList mgdBy = template.getManagedByList();
		if (mgdBy != null)
			notif.put("managedBy", mgdBy);

		logger.info("Creating notification for event '" + template.getEventName() + "'");
		
		return notif;
	}
	
	/**
	 * Subscribe to a notification - this is used to subscribe users to event notifications
	 * 
	 * @param template
	 * @param eventName
	 * @param userName
	 * @return
	 * @throws ObjectExistsException
	 */
	public static NotificationInst subscribe(NotificationTemplate template, String eventName, String userName) 
	{
		NotificationInst notif = subscribe(template, eventName);
		notif.setUserName(userName);
		return notif;
	}
	
	/**
	 * Subscribe an email address to an event with extra data
	 * @param eventName
	 * @param emailAddr
	 * @param extras
	 * @param actor
	 * @return
	 */
	public static NotificationInst subscribeEmail(String eventName, String emailAddr, DBObject extras, String actor)
	{
		NotificationTemplate ntemplate = NotificationTemplate.getNotificationTemplate(eventName);
		NotificationInst myInstance = NotificationInst.subscribe(ntemplate, eventName);
		if (myInstance != null)
		{
			if (extras != null)
				myInstance.putAll(extras);
			
			myInstance.put("eventName", eventName);
			myInstance.addType("email", emailAddr);
			myInstance.save(actor);
		}

		return myInstance;
	}

	/**
	 * Get a notification object by name and user
	 * 
	 * @param eventName
	 * @param userName
	 * @return
	 */
	public static NotificationInst getNotification(String eventName, String srchField, String value)
	{
		if (eventName == null || eventName.length() == 0)
			return null;
		if (srchField == null || srchField.length() == 0)
			return null;
		if (value == null || value.length() == 0)
			return null;
		
		DBObject obj = null;
		try {
			obj = new NotificationInst().getCollection().findOne(BasicDBObjectBuilder.start("eventName", eventName).add(srchField, value).get());
		} catch (Exception exp) { 
			return null;
		}
		
		if (obj == null)
			return null;
		
		return new NotificationInst(obj);
	}

	public static List<NotificationInst> getNotification(String type, String destination)
	{
		DBObject elem = new BasicDBObject();
		elem.put("name", type);
		elem.put("destination", destination);
		
		DBObject typeListMatch  = new BasicDBObject("$elemMatch", elem);
		DBObject search = BasicDBObjectBuilder.start("typeList", typeListMatch).get();
		
		DBCollection col = new NotificationInst().getCollection();
		DBCursor cur = col.find(search);
		
		List<NotificationInst> ret = new Vector<NotificationInst>();
		while (cur.hasNext())
			ret.add(new NotificationInst(cur.next()));
		
		return ret;
	}
	

	public static List<NotificationInst> getNotification(String userName, String eventName, String type, String destination)
	{
		DBObject elem = new BasicDBObject();
		elem.put("name", type);
		elem.put("destination", destination);
		
		DBObject typeListMatch  = new BasicDBObject("$elemMatch", elem);
		DBObject search = BasicDBObjectBuilder.start("typeList", typeListMatch).get();
		search.put("userName", userName);
		search.put("eventName", eventName);
		
		DBCollection col = new NotificationInst().getCollection();
		DBCursor cur = col.find(search);
		
		List<NotificationInst> ret = new Vector<NotificationInst>();
		while (cur.hasNext())
			ret.add(new NotificationInst(cur.next()));
		
		return ret;
	}
	
	public static List<NotificationInst> getNotificationByUser(String userName, String type, String destination)
	{
		DBObject elem = new BasicDBObject();
		elem.put("name", type);
		elem.put("destination", destination);

		DBObject typeListMatch  = new BasicDBObject("$elemMatch", elem);
		DBObject search = BasicDBObjectBuilder.start("typeList", typeListMatch).get();
		search.put("userName", userName);
		
		DBCollection col = new NotificationInst().getCollection();
		DBCursor cur = col.find(search);
		
		List<NotificationInst> ret = new Vector<NotificationInst>();
		while (cur.hasNext())
			ret.add(new NotificationInst(cur.next()));
		
		return ret;
	}
	

	/**
	 * Returns instances of the notification based on the eventName and optional extra search parameters within the typeList
	 * 
	 * @param eventName
	 * @param extraSearch
	 * @return
	 */
	public static List<NotificationInst> getNotifications(String eventName, DBObject extraSearch)
	{
		DBObject search = BasicDBObjectBuilder.start("eventName", eventName).get();
		if (extraSearch != null)
			search.putAll(extraSearch);
		
		DBCollection col = new NotificationInst().getCollection();
		DBCursor cur = col.find(search);
		
		List<NotificationInst> ret = new Vector<NotificationInst>();
		while (cur.hasNext())
			ret.add(new NotificationInst(cur.next()));
		
		return ret;
	}

	/** Returns the NotificationInst by ID */
	public static NotificationInst getById(String guid)
	{
		if (guid == null || guid.length() == 0)
			return null;
		
		DBObject obj = null;
		try {
			obj = new NotificationInst().getCollection().findOne(BasicDBObjectBuilder.start("_id", new ObjectId(guid)).get());
		} catch (Exception exp) { 
			return null;
		}
		
		if (obj == null)
			return null;
		
		return new NotificationInst(obj);
	}

	/**
	 * Returns instances of the notification based on the eventName and user subscribed
	 * 
	 * @param eventName
	 * @param userName
	 * @return
	 */
	public static List<NotificationInst> getNotifications(String eventName, String userName)
	{
		DBObject search = BasicDBObjectBuilder.start("eventName", eventName).get();
		search.put("userName", userName);
		
		DBCollection col = new NotificationInst().getCollection();
		DBCursor cur = col.find(search);
		
		List<NotificationInst> ret = new Vector<NotificationInst>();
		while (cur.hasNext())
			ret.add(new NotificationInst(cur.next()));
		
		return ret;
	}

	public static List<NotificationInst> getSiteNotifications(String siteId)
	{
		DBObject search = BasicDBObjectBuilder.start("siteId", siteId).get();
		
		DBCollection col = new NotificationInst().getCollection();
		DBCursor cur = col.find(search);
		
		List<NotificationInst> ret = new Vector<NotificationInst>();
		while (cur.hasNext())
			ret.add(new NotificationInst(cur.next()));
		
		return ret;
	}
	
	/**
	 * Returns instances of the notification based on the user subscribed
	 * 
	 * @param userName
	 * @return
	 */
	public static List<NotificationInst> getNotifications(String userName)
	{
		DBObject search = BasicDBObjectBuilder.start("userName", userName).get();
		
		DBCollection col = new NotificationInst().getCollection();
		DBCursor cur = col.find(search);
		
		List<NotificationInst> ret = new Vector<NotificationInst>();
		while (cur.hasNext())
			ret.add(new NotificationInst(cur.next()));
		
		return ret;
	}

}
