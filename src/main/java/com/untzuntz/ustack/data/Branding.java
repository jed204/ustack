package com.untzuntz.ustack.data;

import java.net.URL;
import java.util.Date;

import org.apache.log4j.Logger;
import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.untzuntz.ustack.exceptions.AccountExistsException;
import com.untzuntz.ustack.main.UOpts;

/**
 * Object to support a branding per URL / URI
 * 
 * @author jdanner
 *
 */
public class Branding extends UntzDBObject {

	private static final long serialVersionUID = 1L;
	@SuppressWarnings("unused")
	private static Logger logger = Logger.getLogger(Branding.class);

	public String getCollectionName() { return "branding"; }
	
	private Branding() {
		put("created", new Date());
	}
	
	public Branding(DBObject obj)
	{
		putAll(obj);
	}
	
	public String getId() {
		return get("_id") + "";
	}
	
	/** Gets the DB Collection for the UserAccount object */
	public static DBCollection getDBCollection() {
		return new Branding().getCollection();
	}
	
	public static Branding createBranding(String appName, String host, String file) throws AccountExistsException
	{
		String[] fileArray = file.split(",");
		for (String fileItem : fileArray)
		{
			DBObject search = new BasicDBObject("host", host).append("file", fileItem);
			
			DBCollection col = new Branding().getCollection();
			DBObject obj = col.findOne( search );
			if (obj != null)
				throw new AccountExistsException("URL");
		}
		
		Branding branding = new Branding();
		branding.put("applicationName", appName);
		branding.put("host", host);
		branding.put("file", fileArray);
		
		return branding;
	}
	
	public static Branding getById(String id)
	{
		if (id == null)
			return null;
		
		DBObject book = new Branding().getCollection().findOne(BasicDBObjectBuilder.start("_id", new ObjectId(id)).get());
		
		if (book == null)
			return null;
		
		return new Branding(book);
	}
	
	public static Branding getByURL(URL url)
	{
//		String key = url.toString();
		
		if (UOpts.getCacheEnabled())
		{
			//Branding curCache = (Branding)UDataCache.getInstance().get(key);
			//if (curCache != null)
			//	return curCache;
		}
		
		DBObject search = new BasicDBObject("host", url.getHost()).append("file", url.getFile());
		
		DBCollection col = new Branding().getCollection();
		DBObject obj = col.findOne( search );
		
		if (obj != null)
		{
			Branding ret = new Branding(obj);
			
			//if (UOpts.getCacheEnabled())
			//	UDataCache.getInstance().set(key, 900, ret);
			return ret;
		}
		
		return null;
	}


}
