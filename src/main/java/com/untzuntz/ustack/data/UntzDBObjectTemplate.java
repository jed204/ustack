package com.untzuntz.ustack.data;

import java.util.Date;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.untzuntz.ustack.exceptions.ObjectExistsException;
import com.untzuntz.ustack.main.UAppCfg;
import com.untzuntz.ustack.main.UOpts;

/**
 * Template for loading new objects with existing data
 * 
 * @author jdanner
 *
 */
public class UntzDBObjectTemplate extends UntzDBObject {

	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(UntzDBObjectTemplate.class);

	@Override
	public String getCollectionName() { return "objectTemplates"; }

	private UntzDBObjectTemplate()
	{
		// setup basic values on account
		put("created", new Date());
	}
	
	public String toString() {
		return getTemplateName();
	}

	public String getTemplateId() {
		return get("_id") + "";
	}

	/** Gets the DB Collection for the NotificationTemplate object */
	public static DBCollection getDBCollection() {
		return new UntzDBObjectTemplate().getCollection();
	}

	/** Return the name of the database that houses the 'objectTemplates' collection */
	public static final String getDatabaseName() {
		
		if (UOpts.getString(UAppCfg.DATABASE_OBJ_TEMPL_COL) != null)
			return UOpts.getString(UAppCfg.DATABASE_OBJ_TEMPL_COL);
		
		return UOpts.getAppName();
	}

	/**
	 * Generate a NotificationTemplate object from the MongoDB object
	 * @param user
	 */
	public UntzDBObjectTemplate(DBObject templ) {
		super(templ);
	}

	/** Sets the templateName */
	private void setTemplateName(String templateName)
	{
		put("templateName", templateName);
	}

	/** Returns the templateName */
	public String getTemplateName()
	{
		return getString("templateName");
	}

	/** Returns a BasicDBList of objects that are part of the template */
	public BasicDBList getTemplateObjectList()
	{
		BasicDBList list = (BasicDBList)get("templateObjectList");
		if (list == null)
			list = new BasicDBList();
		
		return list;
	}

	/** Sets the list of template objects */
	public void setTemplateObjectList(BasicDBList list) 
	{
		put("templateObjectList", list);
	}
	
	/**
	 * Create a new UntzDBObjectTemplate for an templateName
	 * 
	 * @param name
	 * @return
	 * @throws ObjectExistsException
	 */
	public static UntzDBObjectTemplate createTemplate(String templateName) throws ObjectExistsException
	{
		if (templateName == null || templateName.length() == 0)
			return null;
		
		UntzDBObjectTemplate templ = getTemplate(templateName);
		if (templ != null) // already exists
			throw new ObjectExistsException();

		// create the actual account
		templ = new UntzDBObjectTemplate();
		templ.setTemplateName(templateName);
		logger.info("Creating object template '" + templateName + "'");
		
		return templ;
	}

	/**
	 * Get a template object by templateName
	 * 
	 * @param name
	 * @return
	 */
	public static UntzDBObjectTemplate getTemplate(String templateName)
	{
		if (templateName == null || templateName.length() == 0)
			return null;
		
		DBObject obj = null;
		try {
			obj = new UntzDBObjectTemplate().getCollection().findOne(BasicDBObjectBuilder.start("templateName", templateName).get());
		} catch (Exception exp) { 
			return null;
		}
		
		if (obj == null)
			return null;
		
		return new UntzDBObjectTemplate(obj);
	}

	/** Returns all Templates */
	public static List<UntzDBObjectTemplate> getAll()
	{
		List<UntzDBObjectTemplate> ret = new Vector<UntzDBObjectTemplate>();
		DBCollection col = getDBCollection();
		DBCursor cur = col.find();
		
		while (cur.hasNext())
			ret.add(new UntzDBObjectTemplate(cur.next()));
		
		return ret;
	}
	
	/**
	 * Returns all Templates
	 * @return
	 */
	public static List<UntzDBObjectTemplate> getAll(BasicDBList managedByList)
	{
		DBObject search = new BasicDBObject();
		if (managedByList != null)
		{
			String[] mbArray = new String[managedByList.size()];
			for (int i = 0; i < managedByList.size(); i++)
				mbArray[i] = (String)managedByList.get(i);
			
			search.put("managedByList", BasicDBObjectBuilder.start("$in", mbArray).get());
		}
		
		List<UntzDBObjectTemplate> ret = new Vector<UntzDBObjectTemplate>();
		DBCursor cur = new UntzDBObjectTemplate().getCollection().find(search).sort( BasicDBObjectBuilder.start("templateName", 1).get() );
		while (cur.hasNext())
			ret.add(new UntzDBObjectTemplate(cur.next()));
		
		return ret;
	}
	


}
