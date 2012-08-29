package com.untzuntz.ustack.testcases;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.Mongo;
import com.untzuntz.ustack.data.Analytics;
import com.untzuntz.ustack.data.MongoDB;
import com.untzuntz.ustack.data.TrackingObject;

public class AnalyticsTest extends UStackTestCaseBase {

	protected static Logger logger = Logger.getLogger(AccountingTest.class);

	public AnalyticsTest()
	{
		super();
	}
	
	@Test public void testTracking() throws Exception
	{
		Analytics.track("itm", "1.2.3.4.5.6" + runId, "OpenFile", "test@guy.com" + runId, null, null);
		Analytics.track("itm", "1.2.3.4.5.6" + runId, "CloseFile", "test@guy.com" + runId, null, null);
		Analytics.track("itm", "1.2.3.4.5.7" + runId, "OpenFile", "test@guy.com" + runId, null, null);
		Analytics.track("itm", "1.2.3.4.5.7" + runId, "OpenFile", "another@guy.com" + runId, null, null);
		Analytics.track("itm", "1.2.3.4.5.7" + runId, "OpenFile", "final@guy.com" + runId, null, new BasicDBObject("test", "true"));
		
		// 1st Event
		TrackingObject track = Analytics.getTracking("itm", "1.2.3.4.5.6" + runId);
		assertNotNull(track);
		
		BasicDBList events = (BasicDBList)track.getEvents();
		assertEquals(2, events.size());
		
		assertTrue(track.hasEvent("OpenFile"));
		assertTrue(track.hasEvent("CloseFile"));
		assertFalse(track.hasEvent("NonExistantEvent"));
		
		// 2nd Event
		track = Analytics.getTracking("itm", "1.2.3.4.5.7" + runId);
		assertNotNull(track);
		
		events = (BasicDBList)track.getEvents();
		assertEquals(3, events.size());

		assertTrue(track.hasEvent("OpenFile"));
		assertFalse(track.hasEvent("NonExistantEvent"));

		assertTrue(track.hasActor("test@guy.com" + runId));
		assertTrue(track.hasActor("another@guy.com" + runId));
		assertTrue(track.hasActor("final@guy.com" + runId));
		assertFalse(track.hasActor("nonexistant@guy.com" + runId));
	}

}
