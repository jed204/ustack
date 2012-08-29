package com.untzuntz.ustack.data;

import java.util.Date;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.bson.types.ObjectId;

import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.untzuntz.ustack.exceptions.ObjectExistsException;
import com.untzuntz.ustack.main.UOpts;
import com.untzuntz.ustack.main.UAppCfg;
import com.untzuntz.ustack.main.UOpts;

/**
 * Terms + Conditions you may want a user to agree to (now and on a moving forward basis)
 * 
 * @author jdanner
 *
 */
public class TermsConditions extends UntzDBObject {

	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(TermsConditions.class);

	@Override
	public String getCollectionName() { return "tos"; }

	private TermsConditions()
	{
		// setup basic values on account
		put("created", new Date());
	}
	
	public String toString() {
		return getName() + " (" + getDisplayName() + ")";
	}

	public String getTOSId() {
		return get("_id") + "";
	}

	/** Gets the DB Collection for the TermsConditions object */
	public static DBCollection getDBCollection() {
		return new TermsConditions().getCollection();
	}

	/** Return the name of the database that houses the 'tos' collection */
	public static final String getDatabaseName() {
		
		if (UOpts.getString(UAppCfg.DATABASE_TOS_COL) != null)
			return UOpts.getString(UAppCfg.DATABASE_TOS_COL);
		
		return UOpts.getAppName();
	}

	/**
	 * Generate a TermsConditions object from the MongoDB object
	 * @param user
	 */
	public TermsConditions(DBObject tos) {
		super(tos);
	}

	/** Sets the name */
	private void setName(String name)
	{
		put("name", name);
	}

	/** Returns the name */
	public String getName()
	{
		return getString("name");
	}

	/** Sets the display name */
	public void setDisplayName(String name)
	{
		if (name == null || name.length() == 0)
			name = getName();
		
		put("displayName", name);
	}

	/** Returns the display name */
	public String getDisplayName()
	{
		return getString("displayName");
	}
	
	/** Sets the display name */
	public void setText(String text)
	{
		put("text", text);
	}

	/** Returns the display name */
	public String getText()
	{
		return getString("text");
	}
	
	/** Sets the number of days until this agreement should be renewed */
	public void setRenewalDays(int days)
	{
		put("renewalDays", days);
	}
	
	/** Returns the number of days until this agreement should be renewed */
	public int getRenewalDays()
	{
		return getInt("renewalDays", -1);
	}

	/**
	 * Create a new TermsConditions
	 * 
	 * @param name
	 * @return
	 * @throws ObjectExistsException
	 */
	public static TermsConditions createTOS(String name) throws ObjectExistsException
	{
		if (name == null || name.length() == 0)
			return null;
		
		TermsConditions user = getTOS(name);
		if (user != null) // already exists
			throw new ObjectExistsException();
		
		// create the actual account
		user = new TermsConditions();
		user.setName(name);
		logger.info("Creating tos '" + name + "'");
		
		return user;
	}

	/**
	 * Get a tos object by name
	 * 
	 * @param name
	 * @return
	 */
	public static TermsConditions getTOS(String name)
	{
		if (name == null || name.length() == 0)
			return null;
		
		DBObject obj = null;
		try {
			obj = new TermsConditions().getCollection().findOne(BasicDBObjectBuilder.start("name", name).get());
		} catch (Exception exp) { 
			return null;
		}
		
		if (obj == null)
			return null;
		
		return new TermsConditions(obj);
	}

	/** Returns the T&Cs by ID */
	public static TermsConditions getByGUID(String guid)
	{
		if (guid == null || guid.length() == 0)
			return null;
		
		DBObject obj = null;
		try {
			obj = new TermsConditions().getCollection().findOne(BasicDBObjectBuilder.start("_id", new ObjectId(guid)).get());
		} catch (Exception exp) { 
			return null;
		}
		
		if (obj == null)
			return null;
		
		return new TermsConditions(obj);
	}

	/** Returns all TCs */
	public static List<TermsConditions> getAll()
	{
		List<TermsConditions> ret = new Vector<TermsConditions>();
		DBCollection col = getDBCollection();
		DBCursor cur = col.find();
		
		while (cur.hasNext())
		{
			TermsConditions obj = new TermsConditions(cur.next());
			ret.add(obj);
		}
		
		return ret;
	}


}
