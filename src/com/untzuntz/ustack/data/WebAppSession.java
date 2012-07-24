package com.untzuntz.ustack.data;

import java.net.URL;
import java.util.Calendar;
import java.util.Date;

import org.apache.log4j.Logger;
import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.untzuntz.ustack.main.ApplicationInstance;

/**
 * A session (for long term tracking)
 * 
 * @author jdanner
 *
 */
public class WebAppSession extends UntzDBObject {

	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(WebAppSession.class);

	public String getCollectionName() { return "webSession"; }
	
	private WebAppSession() {
		put("created", new Date());
	}
	
	public WebAppSession(DBObject obj)
	{
		putAll(obj);
	}
	
	public String getId() {
		return get("_id") + "";
	}
	
	/** Gets the DB Collection for the UserAccount object */
	public static DBCollection getDBCollection() {
		return new WebAppSession().getCollection();
	}
	
	public static WebAppSession createSession(String appName, ApplicationInstance ai)
	{
		String appServer = System.getProperty("ApplicationServer.Name", "PrimaryServer");
		return createSession(appServer, ai.getHTTPSessionID(), ai.getUserName(), appName, ai.getClientIP(), ai.getClientCountry(), ai.getURL());
	}
	
	public static WebAppSession createSession(String appServer, String sessionId, String userName, String appName, String sourceIP, String sourceCountry, URL url)
	{
		if (appServer == null)
			appServer = System.getProperty("ApplicationServer.Name", "PrimaryServer");

		if (appServer == null)
			logger.warn("Unknown App Server? Session ID => " + sessionId);
		
		WebAppSession session = new WebAppSession();
		session.put("userName", userName);
		session.put("appServer", appServer);
		session.put("sessionId", sessionId);
		session.put("applicationName", appName);
		session.put("host", url.getHost());
		session.put("path", url.getFile());
		session.put("sourceIP", sourceIP);
		session.put("sourceCountry", sourceCountry);
		session.put("startTime", new Date());
		session.save(userName);
		
		return session;
	}
	
	public static void endSession(ApplicationInstance ai, String endReason, String actor) {
		endSession(ai.getHTTPSessionID(), endReason, actor);
	}

	public static void endSession(String sessionId, String endReason, String actor) {
		endSession(null, sessionId, endReason, actor);
	}
	
	public static void endSession(String appServer, String sessionId, String endReason, String actor) {
		
		if (appServer == null)
			appServer = System.getProperty("ApplicationServer.Name", "PrimaryServer");

		WebAppSession session = WebAppSession.getRecentSessionByServerSessionIdUser(appServer, sessionId);
		if (session != null)
		{
			endSession(session, endReason);
			session.save(actor);
		}
		else
			logger.info("Session not found in session database - too old? : " + appServer + "/" + sessionId);
		
	}
	
	private static void endSession(DBObject session, String endReason) {
		
		if (session.get("endTime") != null)
		{
			logger.info("Session already ended (" + session.get("endTime") + ") : => " + session.get("_id"));
			return;
		}
		
		Date now = new Date();
		Date start = (Date)session.get("startTime");
		long durMs = now.getTime() - start.getTime();
		
		session.put("endTime", now);
		session.put("duration", (long)((float)durMs / 1000.0f / 60.0f));
		session.put("reason", endReason);
		
	}
	
	public static void endSessionById(String id, String endReason, String actor) {
		
		WebAppSession session = getById(id);
		if (session != null)
		{
			endSession(session, endReason);
			session.save(actor);
		}
		
	}
	
	public static void closeOutstandingSessions(String appServer)
	{
		if (appServer == null)
			appServer = System.getProperty("ApplicationServer.Name", "PrimaryServer");

		Calendar c = Calendar.getInstance();
		c.add(Calendar.DATE, -1);
		
		BasicDBObject sort = new BasicDBObject("startTime", -1);
		BasicDBObject search = new BasicDBObject("appServer", appServer).append("startTime", new BasicDBObject("$gte", c.getTime())).append("endTime", new BasicDBObject("$exists", false));
	
		logger.info("Close-out Session Search: " + search);

		DBCollection col = new WebAppSession().getCollection();
		DBCursor cur = col.find(search).sort(sort);
		int idx = 0;
		long st = System.currentTimeMillis();
		logger.info("Close-out Session -> " + cur.count() + " sessions to close-out");
		while (cur.hasNext())
		{
			DBObject session = (DBObject)cur.next();
			endSession(session, "Closed Outstanding");
			col.save(session);
			idx++;
			if (idx % 10 == 0)
				logger.info("Close-out Session -> " + (cur.count() - idx) + " remaining to close-out");
		}

		logger.info("Close-out Session -> DONE! Took: " + (System.currentTimeMillis() - st) + " ms");

	}
	
	public static WebAppSession getRecentSessionByServerSessionIdUser(String appServer, String sessionId) {
		
		if (appServer == null || sessionId == null)
			return null;

		
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DATE, -1);
		
		BasicDBObject sort = new BasicDBObject("startTime", -1);
		BasicDBObject search = new BasicDBObject("appServer", appServer).append("sessionId", sessionId).append("startTime", new BasicDBObject("$gte", c.getTime()));
	
		logger.info("Session Search: " + search);
		
		DBCursor cur = new WebAppSession().getCollection().find(search).sort(sort);
		if (cur != null && cur.hasNext())
			return new WebAppSession(cur.next());
		
		return null;
		
	}
	
	public static WebAppSession getById(String id)
	{
		if (id == null)
			return null;
		
		DBObject book = new WebAppSession().getCollection().findOne(BasicDBObjectBuilder.start("_id", new ObjectId(id)).get());
		
		if (book == null)
			return null;
		
		return new WebAppSession(book);
	}
	
	

}
