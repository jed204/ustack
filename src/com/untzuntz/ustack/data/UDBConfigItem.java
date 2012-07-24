package com.untzuntz.ustack.data;

import java.util.List;
import java.util.Vector;

import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.untzuntz.ustack.main.UOpts;

/**
 * Configuration for the system stored in the database
 * 
 * @author jdanner
 *
 */
public class UDBConfigItem extends UntzDBObject {

	private static final long serialVersionUID = 1L;

	public String getCollectionName() { return "config"; }
	
	public static List<UDBConfigItem> getAll()
	{
		List<UDBConfigItem> ret = new Vector<UDBConfigItem>();
		DBCollection col = MongoDB.getCollection(UOpts.getAppName(), "config");
		DBCursor cur = col.find();
		
		while (cur.hasNext())
		{
			UDBConfigItem obj = new UDBConfigItem(cur.next());
			ret.add(obj);
		}
		
		return ret;
	}

	public static UDBConfigItem getByPropertyName(String propertyName)
	{
		DBCollection col = MongoDB.getCollection(UOpts.getAppName(), "config");
		if (col == null)
			return null;
		DBObject obj = col.findOne(BasicDBObjectBuilder.start("property", propertyName).get());
		if (obj == null)
			return null;

		return new UDBConfigItem(obj);
	}
	
	public UDBConfigItem(String propertyName)
	{
		setPropertyName(propertyName);
	}
	
	public UDBConfigItem(DBObject obj)
	{
		putAll(obj);
	}
	
	public void setPropertyName(String propName)
	{
		put("property", propName);
	}
	
	public void setValue(String value)
	{
		put("value", value);
	}
	
	public String getPropertyName()
	{
		return getString("property");
	}
	
	public String getValue()
	{
		return getString("value");
	}
	
}
