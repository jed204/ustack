package com.textrecruit.ustack.testcases;

import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBObject;
import com.textrecruit.ustack.data.NotificationInst;
import com.textrecruit.ustack.data.NotificationTemplate;
import com.textrecruit.ustack.data.SiteAccount;
import com.textrecruit.ustack.main.UNotificationSvc;
import org.apache.log4j.Logger;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class NotificationTest extends UStackTestCaseBase {
	
	protected static Logger logger = Logger.getLogger(NotificationTest.class);

	public NotificationTest()
	{
		super();
	}
	
	@Test public void testSubscription()
	{
		SiteAccount site = null;
		NotificationTemplate templ = null;
		try {
			templ = NotificationTemplate.createTemplate("testEvent" + runId);
			assertNotNull(templ);
			
			DBObject emailType = BasicDBObjectBuilder.start("templateText", "This is the test email template! ${siteId} -- ${site.siteName}").get();
			emailType.put("fromName", "Test From Untz");
			emailType.put("fromAddress", "testfrom@textrecruit.com");
			emailType.put("subject", "This is my test subject! ${REPLACEME}");
			emailType.put("dummyExtra", "true");
			
			templ.addType("sms", BasicDBObjectBuilder.start("templateText", "This is the test sms template! ${siteId} -- ${site.siteName}").get());
			templ.addType("email", emailType);
			
			templ.save("TestCase");
			
			site = SiteAccount.createSite("Test Site Name" + runId);
			
		} catch (Exception err) { fail(); } 

		NotificationInst ni1 = NotificationInst.subscribe(templ, "testEvent" + runId, "dummyuser@textrecruit.com");
		ni1.addType("sms", "5135551212"); // add some types
		ni1.addType("email", "testuser1@textrecruit");
		ni1.put("siteId", "SITE1");
		ni1.save("TestCase");
		
		NotificationInst ni2 = NotificationInst.subscribe(templ, "testEvent" + runId, "dummyuser@textrecruit.com");
		ni2.addType("email", "testuser2@textrecruit");
		ni2.put("siteId", "SITE2");
		ni2.save("TestCase");
		
		// verify notification types
		assertNotNull(ni1.getType("sms"));
		assertNotNull(ni1.getType("email"));
		assertNull(ni1.getType("bad"));

		// test we have it and the other guy doesn't
		assertNotNull(NotificationInst.getNotification("testEvent" + runId, "siteId", "SITE1"));
		assertNull(NotificationInst.getNotification("testEvent" + runId, "userName", "USER"));

		// general notification search
		List<NotificationInst> noti = NotificationInst.getNotifications("testEvent" + runId, "dummyuser@textrecruit.com");
		assertEquals(2, noti.size());
		
		// advanced notification search
		noti = NotificationInst.getNotifications("testEvent" + runId, BasicDBObjectBuilder.start("siteId", "SITE1").get());
		assertEquals(1, noti.size());
		
		// actually simulate some alerts
		UNotificationSvc notif = new UNotificationSvc();
		notif.setData("site", site);
		notif.setTestMode(true);
		assertEquals(2, notif.notify("testEvent" + runId, BasicDBObjectBuilder.start("siteId", "SITE1").get())); // email + sms 
		assertEquals(0, notif.notify("testEvent" + runId, BasicDBObjectBuilder.start("siteId", "SITEBAD").get())); // none
		assertEquals(1, notif.notify("testEvent" + runId, BasicDBObjectBuilder.start("siteId", "SITE2").get())); // sms
	}


}
