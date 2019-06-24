package com.textrecruit.ustack.data;

import java.util.Date;
import java.util.UUID;

import org.apache.log4j.Logger;

import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.textrecruit.ustack.exceptions.AccountExistsException;
import com.textrecruit.ustack.exceptions.InvalidUserAccountName;
import com.textrecruit.ustack.exceptions.PasswordException;
import com.textrecruit.ustack.exceptions.PasswordLengthException;
import com.textrecruit.ustack.main.Msg;
import com.textrecruit.ustack.main.UAppCfg;
import com.textrecruit.ustack.main.UOpts;

/**
 * Unique reference / UID for use in emails or links
 * 
 * @author jdanner
 *
 */
public class UniqueReference extends UntzDBObject {
	
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(UniqueReference.class);

	public String getCollectionName() { return "uniqReferences"; }
	
	private UniqueReference()
	{
		// setup basic values on account
		put("created", new Date());
	}
	
	public String getId() {
		return get("_id") + "";
	}
	
	private void setActionName(String name)
	{
		put("actionName", name);
	}

	public String getProductUid() {
		return getString("actionName");
	}
	
	/** Gets the DB Collection for the UniqueReference object */
	public static DBCollection getDBCollection() {
		return new UniqueReference().getCollection();
	}

	/** Return the name of the database that houses the 'uniqReferences' collection */
	public static final String getDatabaseName() {
		
		if (UOpts.getString(UAppCfg.DATABASE_PRODUCT_COL) != null)
			return UOpts.getString(UAppCfg.DATABASE_PRODUCT_COL);
		
		return UOpts.getAppName();
		
	}

	/**
	 * Generate a UniqueReference object from the MongoDB object
	 * @param user
	 */
	public UniqueReference(DBObject acct) {
		super(acct);
	}
	
	public void setDescription(String desc)
	{
		put("description", desc);
	}
	
	public String getDescription()
	{
		return getString("description");
	}

	/** Sets the uid */
	private void setUid(String uid)
	{
		put("uid", uid);
	}

	/** Returns the uid */
	public String getUid()
	{
		return getString("uid");
	}
	
	/**
	 * Create a new UniqueRefernece Action
	 * 
	 * @param actionName
	 * @return
	 * @throws AccountExistsException
	 * @throws PasswordLengthException
	 */
	public static UniqueReference createAction(String actionName) throws AccountExistsException,PasswordException
	{
		if (actionName == null || actionName.length() == 0)
			throw new InvalidUserAccountName(Msg.getString("Invalid-Action"));
		
		UniqueReference ref = getByAction(actionName);
		if (ref != null) // already exists
			throw new AccountExistsException("Action");
		
		// create the actual product
		ref = new UniqueReference();
		ref.setActionName(actionName);
		logger.info("Creating new uniq reference action '" + actionName + "'");
		
		return ref;
	}
	
	/**
	 * Get by a field on the unique reference and verify expiration
	 * 
	 * @param name
	 * @return
	 */
	public static UniqueReference getByFieldCheckExpiration(String field, String val)
	{
		if (val == null || val.length() == 0 || field == null || field.length() == 0)
			return null;
		
		Date expDate = new Date();
		
		DBObject ref = null;
		try {
			DBObject locQ = BasicDBObjectBuilder.start(field, val).append("expires", new BasicDBObject("$gt", expDate)).get();
			ref = new UniqueReference().getCollection().findOne(locQ);
		} catch (Exception exp) { 
			return null;
		}
		
		if (ref == null)
			return null;
		
		return new UniqueReference(ref);
	}

	/**
	 * Get a uniq ref by uid
	 * 
	 * @param name
	 * @return
	 */
	public static UniqueReference getByUID(String uid)
	{
		if (uid == null || uid.length() == 0)
			return null;
		
		uid = uid.toLowerCase();
		
		DBObject ref = null;
		try {
			ref = new UniqueReference().getCollection().findOne(BasicDBObjectBuilder.start("uid", uid).get());
		} catch (Exception exp) { 
			return null;
		}
		
		if (ref == null)
			return null;
		
		return new UniqueReference(ref);
	}

	/**
	 * Get a uniq ref by action
	 * 
	 * @param name
	 * @return
	 */
	public static UniqueReference getByAction(String action)
	{
		if (action == null || action.length() == 0)
			return null;
		
		action = action.toLowerCase();
		
		DBObject ref = null;
		try {
			ref = new UniqueReference().getCollection().findOne(BasicDBObjectBuilder.start("actionName", action).append("uid", BasicDBObjectBuilder.start("$exists", false).get()).get());
		} catch (Exception exp) { 
			return null;
		}
		
		if (ref == null)
			return null;
		
		return new UniqueReference(ref);
	}

	/**
	 * Create a new unique
	 * 
	 * @param action
	 * @return
	 * @throws AccountExistsException
	 * @throws PasswordLengthException
	 */
	public static UniqueReference createUniqRef(String action)
	{
		UniqueReference act = UniqueReference.getByAction(action);
		if (act == null)
			return null;

		// create the actual uniq ref
		UniqueReference ref = new UniqueReference(act);
		ref.removeField("_id");
		
		boolean valid = false;
		while (!valid)
		{
			UUID nUid = UUID.randomUUID();
			String uidStr = nUid.toString();
			if (getByUID(uidStr) == null)
			{
				uidStr = uidStr.toLowerCase();
				ref.setUid(uidStr);
				valid = true;
			}
		}		

		return ref;
	}
	
	/**
	 * Returns the status of a unique reference URL (see @UniqueLinkStatus)
	 * 
	 * @param linkId
	 * @return
	 */
	public static UniqueLinkStatus getLinkStatus(String linkId)
	{
		UniqueReference ref = UniqueReference.getByUID(linkId);
		return getLinkStatus(ref);
	}
	
	/**
	 * Returns the status of a unique reference URL (see @UniqueLinkStatus)
	 * 
	 * @param ref
	 * @return
	 */
	public static UniqueLinkStatus getLinkStatus(UniqueReference ref)
	{
		UniqueLinkStatus ret = UniqueLinkStatus.invalid;
		if (ref != null)
		{
			Date d = (Date)ref.getDate("expires");
			if (d != null && d.before(new Date()))
				ret = UniqueLinkStatus.expired;
			else if ("true".equals(ref.getString("used")))
				ret = UniqueLinkStatus.used;
			else
				ret = UniqueLinkStatus.active;
		}
		return ret;
	}
	
	public static enum UniqueLinkStatus {

		invalid,
		active,
		used,
		expired;
		
	}


}
