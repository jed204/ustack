package com.untzuntz.ustack.data;

import java.util.Date;

import org.bson.types.ObjectId;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.untzuntz.ustack.main.UOpts;

/**
 * Used to interact with external OpenID systems
 * 
 * @author jdanner
 *
 */
public class OpenIDTracker extends UntzDBObject {

	private static final long serialVersionUID = 1L;

	@Override
	public String getCollectionName() { return "openIdTracker"; }

	private OpenIDTracker()
	{
		// setup basic values on account
		put("created", new Date());
	}
	
	/**
	 * Generate a OpenIDTracker object from the MongoDB object
	 * @param user
	 */
	public OpenIDTracker(DBObject object) {
		super(object);
	}
	
	public String getId() {
		return get("_id") + "";
	}

	/** Gets the DB Collection for the v object */
	public static DBCollection getDBCollection() {
		return new OpenIDTracker().getCollection();
	}

	/** Return the name of the database that houses the 'openIdTracker' collection */
	public static final String getDatabaseName() {
		return UOpts.getAppName();
	}
	
	public void setRedirectUrl(String url) {
		put("redirectUrl", url);
	}
	
	public String getRedirectUrl() { 
		return getString("redirectUrl");
	}
	
	public BasicDBList getParameterList() { 
		BasicDBList ret = (BasicDBList)get("paramList");
		if (ret == null)
			ret = new BasicDBList();
		return ret;
	}
	
	public void setParameterList(BasicDBList ret) {
		put("paramList", ret);
	}
	
	public void addParameter(String key, String val) {
		BasicDBList params = getParameterList();
		params.add(new BasicDBObject("key", key).append("val", val));
		setParameterList(params);
	}

	public static OpenIDTracker create(String redirectUrl) {
		
		OpenIDTracker ret = new OpenIDTracker();
		ret.setRedirectUrl(redirectUrl);
		
		return ret;
		
	}
	
	public static OpenIDTracker getById(String id) {
		
		if (id == null)
			return null;
		
		DBObject book = new OpenIDTracker().getCollection().findOne(BasicDBObjectBuilder.start("_id", new ObjectId(id)).get());
		
		if (book == null)
			return null;
		
		return new OpenIDTracker(book);

	}

}
