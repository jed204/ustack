package com.textrecruit.ustack.data;

import java.awt.Color;
import java.net.URL;
import java.util.Date;

import com.textrecruit.ustack.exceptions.AccountExistsException;
import org.apache.log4j.Logger;
import org.bson.BasicBSONObject;
import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.textrecruit.ustack.main.UOpts;

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
	
	public BrandingObject getBrandingByType(Class name)
	{
		if (name == null)
			throw new IllegalArgumentException("You must provide a branding type");
		
		DBObject o = (DBObject)get(name.getSimpleName());
		if (o == null)
			return new BrandingObject();
		
		return new BrandingObject(o);
	}
	
	public static interface ColorBranding {
		public int getForegroundRGB();
		public int getBackgroundRGB();
	}
	
	/**
	 * A section of the application branding
	 * 
	 * @author jdanner
	 *
	 */
	public static class BrandingObject
	{
		private BasicBSONObject data;

		public BrandingObject() {
			data = new BasicBSONObject();
		}
		
		public String toString() {
			return data.toString();
		}
		
		public BrandingObject(DBObject o) {
			this();
			data.putAll(o);
		}
		
		public int getInt(Enum itemName) {
			return data.getInt(itemName.name(), 0);
		}
		
		public String getText(Enum itemName) {
			return data.getString(itemName.name());
		}
		
		public boolean isHidden(Enum itemName) {
			return data.getBoolean(itemName.name(), false);
		}
		
		public boolean isVisible(Enum itemName) {
			return data.getBoolean(itemName.name(), true);
		}
		
		public String getImageUrl(Enum itemName) {
			return data.getString(itemName.name());
		}
		
		public Color getColor(Enum itemName) {

			String value = data.getString(itemName.name());
			if (value == null)
				return null;
			
			int r = 0;
			int g = 0;
			int b = 0;
			
			String[] rgb = value.split(",");
			try {
				r = Integer.valueOf(rgb[0]);
				g = Integer.valueOf(rgb[1]);
				b = Integer.valueOf(rgb[2]);
			} catch (NumberFormatException nfe) {
				return null;
			}
			
			System.out.println(String.format("r,g,b = %d,%d,%d", r, g, b));
			
			return new Color(r, g, b);
		}
		
		
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
	
	public static Branding getByAppName(String name) {
		if (name == null)
			return null;
		
		DBObject book = new Branding().getCollection().findOne(BasicDBObjectBuilder.start("applicationName", name).get());
		
		if (book == null)
			return null;
		
		return new Branding(book);
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
