package com.untzuntz.ustack.testcases;

import java.util.Iterator;
import java.util.Set;

import junit.framework.TestCase;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.Mongo;
import com.untzuntz.ustack.data.Analytics;
import com.untzuntz.ustack.data.MongoDB;
import com.untzuntz.ustack.data.TrackingObject;
import com.untzuntz.ustack.main.ApplicationInstance;

public class AnalyticsTests extends TestCase {

	protected static Logger logger = Logger.getLogger(AccountingTests.class);

	public AnalyticsTests()
	{
		System.setProperty("TestCase", "true");
	}
	
	public void testInit()
	{
		BasicConfigurator.configure();

		Mongo m = MongoDB.getMongo();
		if (!m.getAddress().sameHost("localhost")) // verify we are connected locally
		{
			logger.error("Database not hosted locally - stopping test");
			System.exit(0);
		}
		
		// output some info about the database
		DBCollection col = MongoDB.getCollection(ApplicationInstance.getAppName(), "userAnalytics");
		DB db = col.getDB();
		
		logger.info("Database Name: " + db.getName() + " (" + db.getCollectionNames().size() + " collections)");
		
		if (!"testCase".equalsIgnoreCase(db.getName()))
		{
			logger.error("Invalid database selected : please verify TestCase variable : " + db.getName());
			System.exit(0);
		}
		
		Set<String> colls = db.getCollectionNames();
		Iterator<String> it = colls.iterator();
		
		while (it.hasNext())
			logger.info("   - Collection: " + it.next());

		if (colls.size() > 0)
			db.dropDatabase();
	}
	
	public void testTracking() throws Exception
	{
		Analytics.track("itm", "1.2.3.4.5.6", "OpenFile", "test@guy.com", null, null);
		Analytics.track("itm", "1.2.3.4.5.6", "CloseFile", "test@guy.com", null, null);
		Analytics.track("itm", "1.2.3.4.5.7", "OpenFile", "test@guy.com", null, null);
		Analytics.track("itm", "1.2.3.4.5.7", "OpenFile", "another@guy.com", null, null);
		Analytics.track("itm", "1.2.3.4.5.7", "OpenFile", "final@guy.com", null, new BasicDBObject("test", "true"));
		
		// 1st Event
		TrackingObject track = Analytics.getTracking("itm", "1.2.3.4.5.6");
		assertNotNull(track);
		
		BasicDBList events = (BasicDBList)track.getEvents();
		assertEquals(2, events.size());
		
		assertTrue(track.hasEvent("OpenFile"));
		assertTrue(track.hasEvent("CloseFile"));
		assertFalse(track.hasEvent("NonExistantEvent"));
		
		// 2nd Event
		track = Analytics.getTracking("itm", "1.2.3.4.5.7");
		assertNotNull(track);
		
		events = (BasicDBList)track.getEvents();
		assertEquals(3, events.size());

		assertTrue(track.hasEvent("OpenFile"));
		assertFalse(track.hasEvent("NonExistantEvent"));

		assertTrue(track.hasActor("test@guy.com"));
		assertTrue(track.hasActor("another@guy.com"));
		assertTrue(track.hasActor("final@guy.com"));
		assertFalse(track.hasActor("nonexistant@guy.com"));
	}

}
