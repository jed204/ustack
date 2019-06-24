package com.textrecruit.ustack.data;

import java.util.Date;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.textrecruit.ustack.main.UOpts;

public class Unsubscribe extends UntzDBObject {

	private static final long serialVersionUID = 1L;

	@Override
	public String getCollectionName() { return "unsubscribe"; }

	private Unsubscribe()
	{
		// setup basic values on account
		put("created", new Date());
	}

	public void setEmail(String email) {
		put("email", email);
	}

	/** Gets the DB Collection for the Unsubscribe object */
	public static DBCollection getDBCollection() {
		return new Unsubscribe().getCollection();
	}

	/** Return the name of the database that houses the 'Unsubscribe' collection */
	public static final String getDatabaseName() {
		return UOpts.getAppName();
	}
	
	public static void deleteByEmail(String email)
	{
		DBCollection col = new Unsubscribe().getCollection();
		col.remove(new BasicDBObject("email", email));
	}
	
	public static boolean getByEmail(String email)
	{
		DBCollection col = new Unsubscribe().getCollection();
		DBObject u = col.findOne(new BasicDBObject("email", email));
		return u != null;
	}
	
	public static void unsubscribeNow(String email)
	{
		if (!getByEmail(email))
		{
			Unsubscribe un = new Unsubscribe();
			un.setEmail(email);
			Unsubscribe.save(un, "na");
		}
	}

}
