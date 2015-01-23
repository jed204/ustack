package com.untzuntz.ustack.data;

import java.util.Date;

import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.untzuntz.ustack.main.UOpts;

public class EmailLog extends UntzDBObject {

	private static final long serialVersionUID = 1L;

	@Override
	public String getCollectionName() { return "emailLog"; }

	private EmailLog()
	{
		// setup basic values on account
		put("created", new Date());
	}
	
	public EmailLog(DBObject not) {
		super(not);
	}
	
	public static EmailLog logEmail(String toEmail, String fromName, String fromEmail, String subject)
	{
		EmailLog log = new EmailLog();
		log.put("toEmail", toEmail);
		log.put("fromName", fromName);
		log.put("fromEmail", fromEmail);
		log.put("subject", subject);
		EmailLog.save(log, "NotificationSvc");
		
		return log;
	}

	public void setEmail(String email) {
		put("email", email);
	}

	/** Gets the DB Collection for the Unsubscribe object */
	public static DBCollection getDBCollection() {
		return new EmailLog().getCollection();
	}

	/** Return the name of the database that houses the 'Unsubscribe' collection */
	public static final String getDatabaseName() {
		return UOpts.getAppName();
	}
	
	public static void deleteByEmail(String email)
	{
		DBCollection col = new EmailLog().getCollection();
		col.remove(new BasicDBObject("email", email));
	}
	
	public static EmailLog getById(String id)
	{
		DBCollection col = new EmailLog().getCollection();
		DBObject ret =  col.findOne(new BasicDBObject("_id", new ObjectId(id)));
		if (ret == null)
			return null;
		
		return new EmailLog(ret);
	}


}
