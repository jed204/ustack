package com.untzuntz.ustack.data;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.WriteResult;
import com.untzuntz.ustack.main.UOpts;

/**
 * Basic MongoDB analytics tracking
 * 
 * @author jdanner
 *
 */
public class Analytics {

	private static Logger logger = Logger.getLogger(Analytics.class);

	public static void track(String searchGroup, String objectName, String event, String actor)
	{
		track(searchGroup, objectName, event, actor, null, null, null);
	}
	
	public static void track(String searchGroup, String objectName, String event, String actor, String location, DBObject extras)
	{
		track(searchGroup, objectName, event, actor, location, extras, null);
	}
	
	public static void trackFlat(String searchGroup, String objectName, String event, String actor)
	{
		DBCollection coll = getCollection();

		DBObject obj = new BasicDBObject("name", objectName).append("sg", searchGroup);
		
		if (actor != null)
			obj.put("actor", actor);
		else
			obj.put("actor", "Unknown");

		obj.put("time", new Date());
		obj.put("event", event);
		
		coll.save(obj);
		logger.info("Track [" + objectName + " -> " + event + " / " + actor + "] => " + obj);
	}
	
	public static void track(String searchGroup, String objectName, String event, String actor, String location, DBObject extras, Date time)
	{
		DBCollection coll = getCollection();
		DBObject obj = coll.findOne(new BasicDBObject("name", objectName).append("sg", searchGroup));
		if (obj == null)
			obj = new BasicDBObject("name", objectName).append("sg", searchGroup);
		
		BasicDBList actions = (BasicDBList)obj.get("acts");
		if (actions == null)
			actions = new BasicDBList();
		
		if (time == null)
			time = new Date();
		
		DBObject newAct = new BasicDBObject("event", event);
		if (actor != null)
			newAct.put("actor", actor);
		else
			newAct.put("actor", "Unknown");
			
		newAct.put("time", time);
		if (location != null)
		{
			newAct.put("loc", location);
			if (obj.get("loc") == null)
				obj.put("loc", location);
		}
		if (extras != null)
			newAct.putAll(extras);
		
		actions.add(newAct);
		
		obj.put("acts", actions);
		
		coll.save(obj);
		logger.info("Track [" + objectName + " -> " + event + " / " + actor + "] => " + obj);
	}

	/**
	 * Get the tracking object
	 * @param objectName
	 * @return
	 */
	public static TrackingObject getTracking(String searchGroup, String objectName)
	{
		DBCollection coll = getCollection();
		DBObject obj = coll.findOne(new BasicDBObject("name", objectName).append("sg", searchGroup));
		if (obj == null)
			return null;
		
		return new TrackingObject(obj);
	}
	
	public static void updateMetrics(String userName, String action, int count)
	{
		Calendar cal = Calendar.getInstance();
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH);
		int weekOfMonth = cal.get(Calendar.WEEK_OF_MONTH);
		int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
		int hourOfDay = cal.get(Calendar.HOUR_OF_DAY);

		//Monthly
		updateMetrics("M" + year + "_" + month, userName, action, count);

		//Weekly
		updateMetrics("W" + month + "_" + weekOfMonth, userName, action, count);
		
		//Hourly
		updateMetrics("H" + dayOfWeek + "_" + hourOfDay, userName, action, count);
	}
	
	public static List<DBObject> getAll()
	{
		List<DBObject> ret = new Vector<DBObject>();
		DBCollection coll = getCollection();
		DBCursor cur = coll.find();
		
		while (cur.hasNext())
			ret.add(cur.next());
		
		return ret;
	}
	
	public static DBCollection getCollection()
	{
		return MongoDB.getCollection(UOpts.getAppName(), "userAnalytics");
	}
	
	public static void updateMetrics(String name, String userName, String action, int count)
	{		
		try {
			//Create search queries
			DBObject q = new BasicDBObject();
	        q.put("name", name);
	        q.put("userName", userName);
	        q.put("action", action);
	
			//Create incrementor
			DBObject inc = new BasicDBObject("count", count);
			DBObject incQuery = new BasicDBObject("$inc", inc);
	
			DBCollection coll = getCollection();
			WriteResult wr = coll.update(q, incQuery, true, false);
			
		} catch (Exception er) {
			// TODO: Do something!
		}
	}

}
