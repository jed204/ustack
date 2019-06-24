package com.textrecruit.ustack.data;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class TrackingObject extends BasicDBObject
{
	private static final long serialVersionUID = 1L;

	protected TrackingObject(DBObject obj)
	{
		putAll(obj);
	}
	
	public BasicDBList getEvents()
	{
		BasicDBList ret = (BasicDBList)get("acts");
		if (ret == null)
			ret = new BasicDBList();
		
		return ret;
	}
	
	public boolean hasEvent(String evn)
	{
		if (evn == null)
			return false;

		BasicDBList acts = getEvents();
		for (int i = 0; i < acts.size(); i++)
		{
			DBObject eventObj = (DBObject)acts.get(i);
			if (evn.equalsIgnoreCase( (String)eventObj.get("event") ))
				return true;
		}
		
		return false;
	}
	
	public boolean hasActor(String actor)
	{
		if (actor == null)
			return false;

		BasicDBList acts = getEvents();
		for (int i = 0; i < acts.size(); i++)
		{
			DBObject eventObj = (DBObject)acts.get(i);
			if (actor.equalsIgnoreCase( (String)eventObj.get("actor") ))
				return true;
		}
		
		return false;
	}
}
