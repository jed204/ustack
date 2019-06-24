package com.textrecruit.ustack.data;

import java.util.Date;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.textrecruit.ustack.main.UOpts;

public class AuditLog {

	/**
	 * Create an audit log entry
	 * 
	 * @param app
	 * @param actor
	 * @param action
	 * @param logData
	 */
	public static void log(String app, String actor, String action, DBObject logData)
	{
		DBCollection coll = MongoDB.getCollection(getDatabaseName(), "auditLog");
		
		DBObject entry = new BasicDBObject("app", app);
		entry.put("ts", new Date());
		entry.put("act", action);
		entry.put("actor", actor);
		entry.put("data", logData);
		coll.save(entry);
	}

	/**
	 * Responds if the object has changed
	 * 
	 * @param src
	 * @param tgt
	 * @return
	 */
	public static boolean changed(Object src, Object tgt)
	{
		if (src != null && src.equals(tgt))
			return false;
		
		if (src == null && tgt == null)
			return false;
		
		if (src == null && tgt != null)
			return false;
		
		return true;
	}

	public static String getDatabaseName()
	{
		return UOpts.getAppName();
	}

}
