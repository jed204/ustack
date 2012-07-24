package com.untzuntz.ustack.testcases;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import junit.framework.TestCase;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.untzuntz.ustack.data.MongoDB;
import com.untzuntz.ustack.data.NotificationInst;
import com.untzuntz.ustack.data.NotificationTemplate;
import com.untzuntz.ustack.data.SiteAccount;
import com.untzuntz.ustack.main.ApplicationInstance;
import com.untzuntz.ustack.main.UNotificationSvc;

public class NotificationTests extends TestCase {
	
	protected static Logger logger = Logger.getLogger(NotificationTests.class);

	public NotificationTests()
	{
		System.setProperty("TestCase", "true");
	}
	
	public void testInit()
	{
		BasicConfigurator.configure();
		
		Mongo m = MongoDB.getMongo();
		if (!m.getAddress().sameHost("localhost")) // verify we are connectected locally
		{
			logger.error("Database not hosted locally - stopping test");
			System.exit(0);
		}
		
		// output some info about the database
		DBCollection col = MongoDB.getCollection(ApplicationInstance.getAppName(), "users");
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

	public void testSubscription()
	{
		SiteAccount site = null;
		NotificationTemplate templ = null;
		try {
			templ = NotificationTemplate.createTemplate("testEvent");
			assertNotNull(templ);
			
			DBObject emailType = BasicDBObjectBuilder.start("templateText", "This is the test email template! ${siteId} -- ${site.siteName}").get();
			emailType.put("fromName", "Test From Untz");
			emailType.put("fromAddress", "testfrom@untzuntz.com");
			emailType.put("subject", "This is my test subject! ${REPLACEME}");
			emailType.put("dummyExtra", "true");
			
			templ.addType("sms", BasicDBObjectBuilder.start("templateText", "This is the test sms template! ${siteId} -- ${site.siteName}").get());
			templ.addType("email", emailType);
			
			templ.save("TestCase");
			
			site = SiteAccount.createSite("Test Site Name");
			
		} catch (Exception err) { fail(); } 

		NotificationInst ni1 = NotificationInst.subscribe(templ, "testEvent", "dummyuser@untzuntz.com");
		ni1.addType("sms", "5135551212"); // add some types
		ni1.addType("email", "testuser1@untzuntz");
		ni1.put("siteId", "SITE1");
		ni1.save("TestCase");
		
		NotificationInst ni2 = NotificationInst.subscribe(templ, "testEvent", "dummyuser@untzuntz.com");
		ni2.addType("email", "testuser2@untzuntz");
		ni2.put("siteId", "SITE2");
		ni2.save("TestCase");
		
		// verify notification types
		assertNotNull(ni1.getType("sms"));
		assertNotNull(ni1.getType("email"));
		assertNull(ni1.getType("bad"));

		// test we have it and the other guy doesn't
		assertNotNull(NotificationInst.getNotification("testEvent", "siteId", "SITE1"));
		assertNull(NotificationInst.getNotification("testEvent", "userName", "USER"));

		// general notification search
		List<NotificationInst> noti = NotificationInst.getNotifications("testEvent", "dummyuser@untzuntz.com");
		assertEquals(2, noti.size());
		
		// advanced notification search
		noti = NotificationInst.getNotifications("testEvent", BasicDBObjectBuilder.start("siteId", "SITE1").get());
		assertEquals(1, noti.size());
		
		// actually simulate some alerts
		UNotificationSvc notif = new UNotificationSvc();
		notif.setData("site", site);
		notif.setTestMode(true);
		assertEquals(2, notif.notify("testEvent", BasicDBObjectBuilder.start("siteId", "SITE1").get())); // email + sms 
		assertEquals(0, notif.notify("testEvent", BasicDBObjectBuilder.start("siteId", "SITEBAD").get())); // none
		assertEquals(1, notif.notify("testEvent", BasicDBObjectBuilder.start("siteId", "SITE2").get())); // sms
	}


}
