package com.textrecruit.ustack.data;

import org.apache.log4j.Logger;

import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

/**
 * Keep track of geo lookups for addresses to minimize API usage
 * 
 * @author jdanner
 *
 */
public class GeoLookupCache {

	private static Logger logger = Logger.getLogger(GeoLookupCache.class);

	/** Ensure we have a capped collection */
	public static void generateCollection()
	{
		int maxEntries = 5000;
		
		DB db = MongoDB.getMongo().getDB(UntzDBObject.getDatabaseName());
		if (db.getCollection("geoLookups") == null)
		{
			logger.info("Creating 'geoLookups' collection...");
			DBObject cc = BasicDBObjectBuilder.start("capped", true).add("max", maxEntries).get();
			db.createCollection("geoLookups", cc);
		}
	}

	/** Save our successful search results */
	public static void saveLookup(String lookup, DBObject obj)
	{
		logger.info("Saving [" + lookup + "] => " + obj);
		
		DBObject saver = new BasicDBObject();
		saver.putAll(obj);
		saver.put("lookup", lookup);
		
		generateCollection();

		DBCollection col = MongoDB.getCollection(UntzDBObject.getDatabaseName(), "geoLookups");
		col.save(saver);
	}

	/** Check if we have done this search before */
	public static DBObject getLookup(String lookup)
	{
		DBCollection col = MongoDB.getCollection(UntzDBObject.getDatabaseName(), "geoLookups");
		DBObject search = BasicDBObjectBuilder.start("lookup", lookup).get();
		logger.debug("Looking for: " + search);
		DBObject find = col.findOne( search );
		if (find == null)
			return null;
		
		DBObject loc = new BasicDBObject();
		loc.put("lat", (Double)find.get("lat"));
		loc.put("lng", (Double)find.get("lng"));
		return loc;
	}
	
}
