package com.untzuntz.ustack.data;

import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.Vector;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.untzuntz.ustack.aaa.LinkActionHelper;
import com.untzuntz.ustack.aaa.ResourceDefinition;
import com.untzuntz.ustack.aaa.ResourceLink;
import com.untzuntz.ustack.aaa.RoleDefinition;
import com.untzuntz.ustack.exceptions.ObjectExistsException;
import com.untzuntz.ustack.main.UFile;
import com.untzuntz.ustack.main.UOpts;
import com.untzuntz.ustack.uisupport.FieldValueMap;
import com.untzuntz.ustack.uisupport.SearchTableHeaderInt;
import com.untzuntz.ustack.uisupport.TablePagerInt;

/**
 * Base Object
 * 
 * @author jdanner
 *
 */
abstract public class UntzDBObject extends BasicDBObject implements DBObject {

	private static Logger logger = Logger.getLogger(UntzDBObject.class);
	private static final long serialVersionUID = 1L;
	abstract public String getCollectionName();
	public static final String TYPE_CSV = "csv";
	
	protected UntzDBObject() {}

	/**
	 * Base Constructor
	 * 
	 * @param obj
	 */
	public UntzDBObject(DBObject obj)
	{
		super();
		putAll(obj);
	}
	
	public void update(String actor, DBObject updates)
	{
		DBCollection coll = getCollection();
		
		DBObject partial = new BasicDBObject();
		partial.putAll(updates);
		partial.put("updatedBy", actor);
		partial.put("updated", new Date());
		
		BasicDBObject updateObj = new BasicDBObject();
		updateObj.put("$set", partial);
		
		logger.info("Updating [" + get("_id") + "] => " + partial);
		
		coll.update(new BasicDBObject("_id", get("_id")), updateObj);
	}

	/**
	 * Save an object, provide username or system component that updated the object
	 * 
	 * @param actor
	 */
	public void save(String actor)
	{
		DBCollection coll = getCollection();
		put("updatedBy", actor);
		put("updated", new Date());
		coll.save(this);
	}

	/**
	 * Save an object
	 * 
	 * @param actor
	 */
	protected void save()
	{
		getCollection().save(this);
	}

	/**
	 * Delete an object
	 */
	public void delete()
	{
		getCollection().remove(this);
	}

	/**
	 * Returns the MongoDB collection for this class
	 * @return
	 */
	public DBCollection getCollection()
	{
		DBCollection coll = MongoDB.getCollection(getDatabaseName(), getCollectionName());
		return coll;
	}
	
	/** Remove a resource link by index */
	public void removeResourceLinkIdx(int idx)
	{
		BasicDBList list = getResourceLinkList();
		LinkActionHelper.handleLinkRemoveAction(new ResourceLink((DBObject)list.get(idx)), this);
		list.remove(idx);
		setResourceLinkList(list);
		calculateManageLists();
	}

	/** Returns the list of resource links */
	public BasicDBList getResourceLinkList()
	{
		BasicDBList ret = (BasicDBList)get("resourceLinkList");
		if (ret == null)
			ret = new BasicDBList();
		
		return ret;
	}
	
	private void setResourceLinkList(BasicDBList list)
	{
		put("resourceLinkList", list);
	}
	
	public void removeResourceLinks(ResourceDefinition resDef, String role)
	{
		BasicDBList list = getResourceLinkList();
		for (int i = 0; i < list.size(); i++)
		{
			DBObject resLink = (DBObject)list.get(i);
			String linkResDefId = (String)resLink.get("resDefId");
			String resDefId = resDef.get("_id") + "";
			
			if (linkResDefId.equalsIgnoreCase(resDefId))
			{
				if (role.equalsIgnoreCase( (String)resLink.get("role") ))
				{
					LinkActionHelper.handleLinkRemoveAction(new ResourceLink(resLink), this);
					list.remove(i);
					i--;
				}
			}
		}
		setResourceLinkList(list);
		calculateManageLists();
	}
	
	public void removeResourceLinks(ResourceDefinition resDef)
	{
		BasicDBList list = getResourceLinkList();
		for (int i = 0; i < list.size(); i++)
		{
			DBObject resLink = (DBObject)list.get(i);
			String linkResDefId = (String)resLink.get("resDefId");
			String resDefId = resDef.get("_id") + "";
			
			if (linkResDefId.equalsIgnoreCase(resDefId))
			{
				LinkActionHelper.handleLinkRemoveAction(new ResourceLink(resLink), this);
				list.remove(i);
				i--;
			}
		}
		setResourceLinkList(list);
		calculateManageLists();
	}

	/**
	 * Adds a resourceLink to the current definition if the object doesn't have a matching resource link already
	 * @param resourceLink
	 */
	public void addResourceLinkIfNeeded(ResourceLink resourceLink) {
		
		if (hasResourceLink(resourceLink))
			return;

		logger.debug("Adding ResourceLink => " + ((DBObject)resourceLink).toString());
		addResourceLink(resourceLink);
	}
	
	public boolean hasResourceLink(ResourceLink resourceLink) {
		
		String resDefId = resourceLink.getString("resDefId");
		String role = resourceLink.getString("role");
		String siteId = resourceLink.getString("siteId");
		String userName = resourceLink.getString("userName");
		
		BasicDBList resList = getResourceLinkList();
		for (int i = 0; i < resList.size(); i++)
		{
			DBObject obj = (DBObject)resList.get(i);
			
			String linkResDefId = (String)obj.get("resDefId");
			if (linkResDefId.equalsIgnoreCase(resDefId))
			{
				if (role.equalsIgnoreCase( (String)obj.get("role") ))
				{
					if (siteId != null && siteId.equalsIgnoreCase( (String)obj.get("siteId") ))
					{
						logger.debug("\t- Matched [" + obj + "  ==>  " + ((DBObject)resourceLink).toString());
						return true;
					}
					else if (userName != null && userName.equalsIgnoreCase( (String)obj.get("userName") ))
					{
						logger.debug("\t- Matched [" + obj + "  ==>  " + ((DBObject)resourceLink).toString());
						return true;
					}
					else
					{
						// compare all items on the incoming resource link object to the existing object -- match?
						Iterator<String> it = resourceLink.keySet().iterator();
						while (it.hasNext())
						{
							String key = it.next();
							if (!"created".equalsIgnoreCase(key))
							{
								Object val1 = resourceLink.get(key);
								Object val2 = obj.get(key);
								if (!val1.equals(val2))
								{
									logger.info("\t- MIS-MATCH [" + val1 + " ==> " + val2 + "]");
									return false;
								}
								
								logger.info("\t- Match [" + val1 + " ==> " + val2 + "]");
							}
							else
								logger.info("\t- Skipping 'created'");
						}

						logger.info("\t- Match [ALL ITEMS]");

						return true;
					}
				}
			}
		}

		return false;
	}
	
	/**
	 * Adds a resourceLink to the current definition
	 * 
	 * @param resourceLink
	 * @throws ObjectExistsException
	 */
	public void addResourceLink(ResourceLink resourceLink) {

		LinkActionHelper.handleLinkAddAction(resourceLink, this);

		BasicDBList resourceLinkList = getResourceLinkList();
		resourceLinkList.add(resourceLink);
		setResourceLinkList(resourceLinkList);

		calculateManageLists();
	}
	
	public void clearManagedBy()
	{
		removeField("managedByList");
	}

	/** Returns a comma-separated string of roles */
	public String getManagedByListString()
	{
		StringBuffer res = new StringBuffer();
		
		BasicDBList mgdByList = getManagedByList();
		for (int i = 0; i < mgdByList.size(); i++)
		{
			String mb = (String)mgdByList.get(i);
			res.append(mb);
			if ((i + 1) < mgdByList.size())
				res.append(",");
		}		
		
		return res.toString();
	}
	
	/** Resets and rebuilds the manage and can manage lists */
	protected void calculateManageLists()
	{
		setCanManageList(new BasicDBList());
		setManagedByList(new BasicDBList());
		
		BasicDBList resourceLinkList = getResourceLinkList();
		for (int q = 0; q < resourceLinkList.size(); q++)
		{
			ResourceLink resourceLink = new ResourceLink( (DBObject)resourceLinkList.get(q) );
			
			ResourceDefinition rdef = resourceLink.getResourceDefinition();
			if (rdef != null)
			{
				BasicDBList managedByList = rdef.getManagedByList();
				for (int i = 0; i < managedByList.size(); i++)
					addManagedBy( (String)managedByList.get(i) );

				BasicDBList resLinkMgBy = (BasicDBList)resourceLink.get("managedBy");
				for (int i = 0; resLinkMgBy != null && i < resLinkMgBy.size(); i++)
				{
					String mgmtBy = (String)resLinkMgBy.get(i);
					addManagedBy( mgmtBy );
				}

				BasicDBList canManageList = rdef.getCanManageList();
				for (int i = 0; i < canManageList.size(); i++)
					addCanManage( (String)canManageList.get(i) );
			}			
		}
	}

	/** Check if this object can be 'managedBy' the provided String */
	public boolean hasManagedBy(String managedBy)
	{
		BasicDBList mgdByList = getCanManageList();
		for (int i = 0; i < mgdByList.size(); i++)
			if (managedBy.equalsIgnoreCase( (String)mgdByList.get(i) ))
				return true;
			
		return false;
	}

	/** Removes a managed by item */
	public void removeManagedBy(String managedBy)
	{
		BasicDBList mgdByList = getCanManageList();
		for (int i = 0; i < mgdByList.size(); i++)
			if (managedBy.equalsIgnoreCase( (String)mgdByList.get(i) ))
			{
				mgdByList.remove(i);
				i--;
			}
	}
	
	/** Add a managedBy entry */
	public void addManagedBy(String managedBy)
	{
		if (managedBy == null)
			return;
		
		managedBy = managedBy.trim();
		
		BasicDBList mgdByList = getManagedByList();
		for (int i = 0; i < mgdByList.size(); i++)
		{
			String mb = (String)mgdByList.get(i);
			if (mb.equalsIgnoreCase(managedBy))
				return;
		}
		
		mgdByList.add(managedBy);
		setManagedByList(mgdByList);
	}

	/** Get the managedBy list */
	public BasicDBList getManagedByList()
	{
		BasicDBList ret = (BasicDBList)get("managedByList");
		if (ret == null)
			ret = new BasicDBList();
		
		return ret;
	}

	/** Set the managedBy list */
	protected void setManagedByList(BasicDBList list)
	{
		put("managedByList", list);
	}

	/** Get the canManage list as a $in mongodb search object */
	public DBObject getCanManageSearch()
	{
		return BasicDBObjectBuilder.start("managedByList", BasicDBObjectBuilder.start("$in", getCanManageArray()).get()).get();
	}

	/** Add a canManage entry */
	public void addCanManage(String canManage)
	{
		if (canManage == null || canManage.length() == 0)
			return;
		
		canManage = canManage.trim();
		
		BasicDBList canMgList = getCanManageList();
		for (int i = 0; i < canMgList.size(); i++)
		{
			String mb = (String)canMgList.get(i);
			if (mb.equalsIgnoreCase(canManage))
				return;
		}
		
		canMgList.add(canManage);
		setCanManageList(canMgList);
	}

	/** Get the canManage list as an array */
	public String[] getCanManageArray()
	{
		BasicDBList canMgList = getCanManageList();
		String[] ret = new String[canMgList.size()];

		for (int i = 0; i < canMgList.size(); i++)
			ret[i] = (String)canMgList.get(i);
		
		return ret;
	}

	/** Returns a comma-separated string of roles */
	public String getCanManageListString()
	{
		StringBuffer res = new StringBuffer();
		
		BasicDBList canMgList = getCanManageList();
		for (int i = 0; i < canMgList.size(); i++)
		{
			String mb = (String)canMgList.get(i);
			res.append(mb);
			if ((i + 1) < canMgList.size())
				res.append(",");
		}		
		
		return res.toString();
	}

	/** Determines if this use can manage based on the list of managedby items sent in */
	public boolean hasCanManage(BasicDBList list)
	{
		if (list == null)
			return true;

		BasicDBList canMgList = getCanManageList();
		for (int j = 0; j < list.size(); j++)
		{
			String canManage = (String)list.get(j);
			for (int i = 0; i < canMgList.size(); i++)
				if (canManage.equalsIgnoreCase( (String)canMgList.get(i) ))
					return true;
		}		
		return false;
	}
	
	/** Determines if this use can manage based on the string */
	public boolean hasCanManage(String canManage)
	{
		BasicDBList canMgList = getCanManageList();
		for (int i = 0; i < canMgList.size(); i++)
			if (canManage.equalsIgnoreCase( (String)canMgList.get(i) ))
				return true;
			
		return false;
	}

	/** Returns the list of Strings that the user 'can manage' */
	public BasicDBList getCanManageList()
	{
		BasicDBList ret = (BasicDBList)get("canManageList");
		if (ret == null)
			ret = new BasicDBList();
		
		return ret;
	}

	/** Sets the canManage list */
	private void setCanManageList(BasicDBList list)
	{
		put("canManageList", list);
	}

	/**
	 * Determines if the resourceLink (by name) already exists for this resource
	 * @param resourceLinkName
	 * @return
	 */
	public boolean hasResourceLink(String resourceLinkName) {
		
		if (getResourceLinkByName(resourceLinkName) != null)
			return true;
		
		return false;
	}

	/** Determines if the user has the provided resource link (full name) */
	public boolean hasResourceLinkFullName(String resourceLinkName)
	{
		BasicDBList resourceLinkList = getResourceLinkList();
		for (int i = 0; i < resourceLinkList.size(); i++)
		{
			DBObject resourceLink = (DBObject)resourceLinkList.get(i);
			String rn = (String)resourceLink.get("name");
			if (rn.equalsIgnoreCase(resourceLinkName))
				return true;
		}
		return false;
	}


	
	/** Returns the list of tos and approvals */
	public BasicDBList getTermsConditions()
	{
		BasicDBList ret = (BasicDBList)get("tosList");
		if (ret == null)
			ret = new BasicDBList();
		
		return ret;
	}
	
	public int getRoleLevel(ResourceDefinition def)
	{
		int curMax = 10000;
		BasicDBList resourceLinkList = getResourceLinkList();
		for (int i = 0; i < resourceLinkList.size(); i++)
		{
			DBObject resourceLink = (DBObject)resourceLinkList.get(i);
			//String rn = (String)resourceLink.get("internalName");
			//if (rn.equalsIgnoreCase(def.getInternalName()))
			String rn = (String)resourceLink.get("name");
			if (rn.equalsIgnoreCase(def.getName()))
			{
				RoleDefinition role = def.getRoleByName( (String)resourceLink.get("role") );
				if (role != null && role.getRoleOrder() < curMax)
					curMax = role.getRoleOrder();
			}
//			rn = (String)resourceLink.get("name");
//			if (rn.equalsIgnoreCase(def.getName()))
//			{
//				RoleDefinition role = def.getRoleByName( (String)resourceLink.get("role") );
//				if (role != null && role.getRoleOrder() < curMax)
//					curMax = role.getRoleOrder();
//			}
		}
		return curMax;
	}

	/** Determines if the user has the provided role within the specified resource (internal name) */
	public boolean hasRole(String resourceLinkName, String role)
	{
		BasicDBList resourceLinkList = getResourceLinkList();
		for (int i = 0; i < resourceLinkList.size(); i++)
		{
			DBObject resourceLink = (DBObject)resourceLinkList.get(i);
			String rn = (String)resourceLink.get("internalName");
			if (rn.equalsIgnoreCase(resourceLinkName))
			{
				if (role.equalsIgnoreCase( (String)resourceLink.get("role") ))
					return true;
			}
		}
		return false;
	}

	/** Determines if the user has the provided role within the specified resource (full name) */
	public boolean hasRoleFullName(String resourceLinkName, String role)
	{
		BasicDBList resourceLinkList = getResourceLinkList();
		for (int i = 0; i < resourceLinkList.size(); i++)
		{
			DBObject resourceLink = (DBObject)resourceLinkList.get(i);
			String rn = (String)resourceLink.get("name");
			if (rn.equalsIgnoreCase(resourceLinkName))
			{
				if (role.equalsIgnoreCase( (String)resourceLink.get("role") ))
					return true;
			}
		}
		return false;
	}

	/**
	 * Removes ALL resource links that match the context
	 * @param context
	 */
	public void removeResourceLinksByContext(DBObject context)
	{
		BasicDBList resourceLinkList = getResourceLinkList();
		for (int i = 0; i < resourceLinkList.size(); i++)
		{
			DBObject resourceLink = (DBObject)resourceLinkList.get(i);
			if (checkContext(resourceLink, context))
			{
				logger.info("Removing Resource Link: " + resourceLink);
				resourceLinkList.remove(i);
				i--;
			}
		}		
		setResourceLinkList(resourceLinkList);
	}

	/**
	 * Removes the resource link by internal name and a matching context
	 * @param resourceLinkName
	 * @param context
	 */
	public void removeResourceLinksByName(String resourceLinkName, DBObject context)
	{
		BasicDBList resourceLinkList = getResourceLinkList();
		for (int i = 0; i < resourceLinkList.size(); i++)
		{
			DBObject resourceLink = (DBObject)resourceLinkList.get(i);
			//logger.info(i + " -> " + resourceLink);
			String rn = (String)resourceLink.get("internalName");
			if (rn.equalsIgnoreCase(resourceLinkName))
			{
				if (checkContext(resourceLink, context))
				{
					logger.info("Removing Resource Link: " + resourceLink);
					resourceLinkList.remove(i);
					i--;
				}
			}
		}
		setResourceLinkList(resourceLinkList);
	}

	/** Returns a list of all ResourceLinks by name and optionally context */
	public List<ResourceLink> getResourceLinksByFullName(String resourceLinkName, DBObject context)
	{
		List<ResourceLink> ret = new Vector<ResourceLink>();
		
		BasicDBList resourceLinkList = getResourceLinkList();
		for (int i = 0; i < resourceLinkList.size(); i++)
		{
			DBObject resourceLink = (DBObject)resourceLinkList.get(i);
			String rn = (String)resourceLink.get("name");
			if (rn.equalsIgnoreCase(resourceLinkName))
			{
				if (checkContext(resourceLink, context))
					ret.add(new ResourceLink(resourceLink));
			}
		}
		
		return ret;
	}
	
	/** Returns a list of all ResourceLinks by (internal) name and optionally context */
	public List<ResourceLink> getResourceLinksByName(String resourceLinkName, DBObject context)
	{
		List<ResourceLink> ret = new Vector<ResourceLink>();
		
		BasicDBList resourceLinkList = getResourceLinkList();
		for (int i = 0; i < resourceLinkList.size(); i++)
		{
			DBObject resourceLink = (DBObject)resourceLinkList.get(i);
			//logger.info(i + " [internalName:" + resourceLinkName + "] -> " + resourceLink);
			String rn = (String)resourceLink.get("internalName");
			if (rn.equalsIgnoreCase(resourceLinkName) || "*".equals(resourceLinkName))
			{
				if (checkContext(resourceLink, context))
					ret.add(new ResourceLink(resourceLink));
			}
		}
		
		return ret;
	}
	
	private boolean checkContext(DBObject resourceLink, DBObject context)
	{
		@SuppressWarnings("unused")
		String errKey = null;
		boolean match = true;
		if (context != null)
		{
			//logger.info(i + " ->\n\n" + resourceLink + "\n\n" + context + "\n\n");
			Iterator<String> it = context.keySet().iterator();
			while (match && it.hasNext())
			{
				String key = it.next();
				String contextVal = (String)context.get(key);
				boolean check = false;
				if (contextVal != null)
				{
					String linkVal = (String)resourceLink.get(key);
					check = !contextVal.equalsIgnoreCase(linkVal);
					//logger.debug(key + " -> '" + contextVal + "' vs '" + linkVal + "' ==> Result: " + check);
				}
				else
					logger.warn("contextValue is null for key [" + key + "] (Context: " + context + ")");
				
				if (check)
				{
					errKey = key;
					match = false;
				}
			}
		}
		return match;
	}
	
	/**
	 * Returns a ResourceLinkDefinition object for the requested resourceLink
	 * 
	 * @param resourceLinkName
	 * @return
	 */
	public ResourceLink getResourceLinkByName(String resourceLinkName)
	{
		BasicDBList resourceLinkList = getResourceLinkList();
		for (int i = 0; i < resourceLinkList.size(); i++)
		{
			DBObject resourceLink = (DBObject)resourceLinkList.get(i);
			String rn = (String)resourceLink.get("internalName");
			if (rn.equalsIgnoreCase(resourceLinkName))
				return new ResourceLink(resourceLink);
		}
		return null;
	}
	
	/**
	 * Updates an existing resourceLink definition
	 * @param newResourceLink
	 */
	public void setResourceLink(ResourceLink newResourceLink)
	{
		boolean found = false;
		BasicDBList resourceLinkList = getResourceLinkList();
		for (int i = 0; i < resourceLinkList.size(); i++)
		{
			DBObject resourceLink = (DBObject)resourceLinkList.get(i);
			String rn = (String)resourceLink.get("internalName");
			if (rn.equalsIgnoreCase(newResourceLink.getName()))
			{
				found = true;
				resourceLinkList.set(i, newResourceLink);
			}
		}
		
		if (!found)
			resourceLinkList.add(newResourceLink);
		
		setResourceLinkList(resourceLinkList);
	}
	
	/**
	 * Save an object, provide username or system component that updated the object
	 * 
	 * @param obj
	 * @param actor
	 */
	public static void save(UntzDBObject obj, String actor)
	{
		if ("true".equalsIgnoreCase(obj.getString("doNotSave")))
			return;
		
		DBCollection coll = MongoDB.getCollection(getDatabaseName(), obj.getCollectionName());
		obj.put("updatedBy", actor);
		obj.put("updated", new Date());
		coll.save(obj);
	}

	public static String getDatabaseName()
	{
		return UOpts.getAppName();
	}

	/**
	 * Returns a list of objects that match the resource link
	 * @param col
	 * @param internalName
	 * @param additionalSearch
	 * @return
	 */
	public static List<DBObject> getByResourceLink(DBCollection col, String internalName, DBObject additionalSearch)
	{
		BasicDBObject lookup = new BasicDBObject("internalName", internalName);
		
		if (additionalSearch != null)
			lookup.putAll(additionalSearch);
		
		BasicDBObject foo = new BasicDBObject();
		foo.put("$elemMatch", lookup);
		
		BasicDBObject query = new BasicDBObject();
		query.put("resourceLinkList", foo);

		logger.info("Resource Link Search: " + query);
		DBCursor cur = col.find(query);
		
		List<DBObject> ret = new Vector<DBObject>();
		while (cur.hasNext())
			ret.add(cur.next());
		
		return ret;
	}
	
	public static List<DBObject> search(SearchTableHeaderInt sth, TablePagerInt pager, DBCollection col, DBObject additionalSearch)
	{
		return search(sth, pager, col, additionalSearch, null);
	}
	
	public static List<DBObject> search(SearchTableHeaderInt sth, TablePagerInt pager, DBCollection col, DBObject additionalSearch, DBObject sorter)
	{
		BasicDBObject lookup = new BasicDBObject();
		
		if (additionalSearch != null)
			lookup.putAll(additionalSearch);

		if (sth != null && sth.getFields() != null)
		{
			Hashtable<String,FieldValueMap> fields = sth.getFields();
			Enumeration<String> enu = fields.keys();
			while (enu.hasMoreElements())
			{
				String field = enu.nextElement();
				Object value = fields.get(field).value;
				if (value instanceof String)
				{
					if (((String)value).length() > 0)
					{
						if ("\"\"".equalsIgnoreCase((String)value))
							lookup.put(field, "");
						else
							lookup.put(field, Pattern.compile(".*" + ((String)value) + ".*", Pattern.CASE_INSENSITIVE));
					}
				}
				else if (value instanceof Date)
				{
					Date d1 = (Date)value;
					if (d1 != null)
					{
						Date d2 = new Date( d1.getTime() + 86400000L );
						lookup.put(field, new BasicDBObject("$gte",d1).append("$lt", d2));
						
						logger.info("Lookup: " + lookup);
					}
				}
			}
		}		
		
		if (sorter == null)
			sorter = new BasicDBObject();
		
		if (sth != null && sth.getSortField() != null)
			sorter.put(sth.getSortField(), sth.getSortDirectionInt());

		logger.debug("Search Query:" + lookup);
		
		DBCursor cur = col.find(lookup).sort(sorter);
		if (pager != null)
		{
			cur.skip(pager.getPage() * pager.getMaxResults());
			cur.limit(pager.getMaxResults());
		}
		
		List<DBObject> ret = new Vector<DBObject>();
		
		while (cur.hasNext())
			ret.add(cur.next());
		
		if (pager != null)
			pager.setResultCount(cur.count());

		return ret;
	}
	
	public static DBObject getPhoneObject(String countryCode, String phoneNumber)
	{
		if (countryCode == null || phoneNumber == null || countryCode.length() == 0 || phoneNumber.length() == 0)
			return null;
		
		DBObject phone = new BasicDBObject();
		phone.put("countryCode", countryCode);
		if ("1".equalsIgnoreCase(countryCode))
		{
			if (phoneNumber.indexOf("-") == -1 && phoneNumber.length() == 10)
			{
				// try to split
				phoneNumber = phoneNumber.substring(0, 3) + "-" + phoneNumber.substring(3, 6) + "-" + phoneNumber.substring(6);
			}
		}
		
		phone.put("phoneNumber", phoneNumber);
		return phone;
	}

	/** Export data from the DBObject list to a file given the provided file format */
	public static int export(String format, List<DBObject> toExport, UFile outputFile, String ... fields) throws Exception
	{
		OutputStream out = outputFile.getOutputStream();
		
		int count = 0;
		int fieldLen = fields.length;

		SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
		
		/*
		 * Output CSV Header
		 */
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < fieldLen; i++)
		{
			String field = fields[i];
			buf.append("\"").append(field).append("\"");
			if ((i + 1) < fieldLen)
				buf.append(",");
		}
		buf.append("\n");
		out.write(buf.toString().getBytes());
		
		try {
			for (DBObject object : toExport)
			{
				/* Output Each Object based on the format */
				if (TYPE_CSV.equalsIgnoreCase(format))
				{
					buf = new StringBuffer();
					for (int i = 0; i < fieldLen; i++)
					{
						String field = fields[i];
						String[] actionFields = field.split("\\.");
						
						boolean noOutput = false;
						Object res = object.get(actionFields[0]);
						if (res instanceof BasicDBList)
						{
							String myField = actionFields[1];
							String[] limiter = null;
							if (myField.indexOf("[") > -1)
							{
								limiter = myField.substring(myField.indexOf("[") + 1, myField.length() - 1).split("=");
								myField = myField.substring(0, myField.indexOf("["));
							}
							
							noOutput = true;
							boolean hasWritten = false;
							BasicDBList list = (BasicDBList)res;
							for (int q = 0; q < list.size(); q++)
							{
								DBObject o = (DBObject)list.get(q);
								if (limiter != null)
								{
									if (limiter[1].equalsIgnoreCase( (String)o.get(limiter[0]) ))
									{
										if (hasWritten)
											buf.append(",");

										outputValue(o.get(myField), buf, sdf);
										hasWritten = true;
									}
								}
								else 
								{
									outputValue(o.get(myField), buf, sdf);
									if ((q + 1) < list.size())
										buf.append(",");
								}
							}
						}	
						else if (res instanceof DBObject)
						{ 
							// handle certain internal object types
							DBObject dbO = (DBObject)res;
							if ("loc".equalsIgnoreCase(field))
								res = dbO.get("lat") + "," + dbO.get("lng");
							else if (dbO.get("countryCode") != null && dbO.get("phoneNumber") != null)
								res = "+" + dbO.get("countryCode") + " " + dbO.get("phoneNumber");
						}

						if (!noOutput)
							outputValue(res, buf, sdf);

						
						if ((i + 1) < fieldLen)
							buf.append(",");
					}
					buf.append("\n");
					out.write(buf.toString().getBytes());
				}
				count++;
			}
			out.flush();
		} catch (Exception err) {
			logger.warn("Failed during export", err);
		} finally {
			if (out != null)
				try { out.close(); } catch (Exception e) {}
		}
		
		return count;
	}
	
	private static void outputValue(Object res, StringBuffer buf, SimpleDateFormat sdf)
	{
		if (res == null)
			res = "";
		
		buf.append("\"");
		if (res instanceof Date)
			buf.append(sdf.format( (Date)res ));
		else if (res instanceof String)
		{
			String val = (String)res;
			if ("true".equalsIgnoreCase(val))
				buf.append(1);
			else if ("false".equalsIgnoreCase(val))
				buf.append(0);
			else
				buf.append(res);
		}
		else
			buf.append(res);
			
		buf.append("\"");

		
	}
	
	/**
	 * Returns a list of plugins based on the provided container name
	 * @param containerName
	 * @return
	 */
	public List<String> getPluginsByContainer(String containerName)
	{
		List<String> ret = new Vector<String>();
		BasicDBList resourceLinkList = getResourceLinkList();
		for (int i = 0; i < resourceLinkList.size(); i++)
		{
			ResourceLink link = new ResourceLink((DBObject)resourceLinkList.get(i));
			ResourceDefinition def = ResourceDefinition.getByName( link.getName() );
			if (def != null)
			{
				RoleDefinition role = def.getRoleByName(link.getRoleName());
	
				if (role != null)
				{
					List<String> pls = role.getPluginsByContainer(containerName);
					logger.debug("Loading Plugins for [" + get("_id") + "] ==> " + def.getName() + " // " + role.getName() + " // Container: " + containerName + " ===> Count: " + pls.size());
					ret.addAll(pls);
				}
				else
					logger.warn("Uknown Role: " + link.getName() + "/" + link.getRoleName());
			}
			else
				logger.warn("Unknown Resource: " + link.getName());
		}
		return ret;
	}

	/**
	 * Returns a list of plugins based on the provided container name - removes potential dupes
	 * @param containerName
	 * @return
	 */
	public List<String> getPluginsByContainerMerged(String containerName)
	{
		List<String> ret = new Vector<String>();
		List<String> pluginClassList = getPluginsByContainer(containerName);
		Hashtable<String,String> track = new Hashtable<String,String>();

		for (int i = 0; i <  pluginClassList.size(); i++)
		{
			String pluginClass = pluginClassList.get(i);
			if (track.get(pluginClass) == null) // make sure we only load each plugin once
			{
				ret.add(pluginClass);
				track.put(pluginClass, "T");
			}
		}
		
		return ret;
	}
	
	/** Returns the list of Strings that the user 'can manage' */
	public BasicDBList getAPIMappingList()
	{
		BasicDBList ret = (BasicDBList)get("apiMappingList");
		if (ret == null)
			ret = new BasicDBList();
		
		return ret;
	}

	/** Sets the apiMappingList */
	public void setAPIMappingList(BasicDBList list)
	{
		put("apiMappingList", list);
	}
	
	/** Returns the list of devices */
	public BasicDBList getDeviceList()
	{
		BasicDBList ret = (BasicDBList)get("deviceList");
		if (ret == null)
			ret = new BasicDBList();
		
		return ret;
	}

	/** Sets the deviceList */
	public void setDeviceList(BasicDBList list)
	{
		put("deviceList", list);
	}
	
	public void addDeviceID(String deviceType, String deviceId) {
		if (deviceType == null || deviceId == null)
			return;
		
		BasicDBList list = getDeviceList();
		DBObject existing = getDeviceId(list, deviceType, deviceId);
		if (existing != null)
			return;
		
		list.add(new BasicDBObject("deviceType", deviceType).append("deviceId", deviceId));
		setDeviceList(list);
	}
	
	public List<DBObject> getDevices(String deviceType) {

		List<DBObject> ret = new ArrayList<DBObject>();
		
		if (deviceType == null)
			return ret;
		
		BasicDBList list = getDeviceList();
		for (int i = 0; i < list.size(); i++)
		{
			DBObject obj = (DBObject)list.get(i);
			if (deviceType.equalsIgnoreCase( (String)obj.get("deviceType") ))
				ret.add(obj);
		}
		
		return ret;
		
	}
	
	private DBObject getDeviceId(BasicDBList list, String deviceType, String deviceId) {
		
		if (list == null || deviceType == null || deviceId == null)
			return null;
		
		for (int i = 0; i < list.size(); i++)
		{
			DBObject chk = (DBObject)list.get(i);
			if (deviceType.equalsIgnoreCase( (String)chk.get("deviceType") ) && deviceId.equalsIgnoreCase( (String)chk.get("deviceId") ))
				return chk;
		}
		
		return null;
	}
	
	public void removeDeviceId(String deviceType, String deviceId) {
		if (deviceType == null || deviceId == null)
			return;

		BasicDBList list = getDeviceList();
		DBObject existing = getDeviceId(list, deviceType, deviceId);
		if (existing == null)
			return;
		
		list.remove(existing);
		setDeviceList(list);
	}
	
	public void removeAPIMapping(String apiName) {
		
		if (apiName == null)
			return;
		
		BasicDBList list = getAPIMappingList();
		for (int i = 0; i < list.size(); i++)
		{
			DBObject chk = (DBObject)list.get(i);
			if (apiName.equalsIgnoreCase( (String)chk.get("name") ))
			{
				list.remove(i);
				i--;
			}
		}
		setAPIMappingList(list);
	}
	
	public void addAPIMapping(DBObject obj)
	{
		DBObject chk = getAPIMapping( (String)obj.get("name") );
		if (chk != null)
			chk.putAll(obj);
		else
		{
			BasicDBList list = getAPIMappingList();
			list.add(obj);
			setAPIMappingList(list);
		}
	}

	public APIMapping getAPIMappingGen(String apiName) {
		
		if (apiName == null)
			return null;
		
		APIMapping ret = getAPIMapping(apiName);
		if (ret == null)
		{
			ret = APIMapping.createMapping(apiName);
			ret.setAccessInfo(UUID.randomUUID().toString(), UUID.randomUUID().toString());
			addAPIMapping(ret);
			save(); // save new mapping automatically
		}
		
		return ret;
	}
	
	public APIMapping getAPIMapping(String apiName)
	{
		if (apiName == null)
			return null;
		
		BasicDBList list = getAPIMappingList();
		for (int i = 0; i < list.size(); i++)
		{
			DBObject chk = (DBObject)list.get(i);
			if (apiName.equalsIgnoreCase( (String)chk.get("name") ))
				return new APIMapping(chk);
		}
		
		return null;
	}

	public int getObjectInt(String objName, String field, int def)
	{
		int ret = def;
		
		DBObject obj = (DBObject)get(objName);
		if (obj != null)
		{
			if (obj.get(field) instanceof Double)
			{
				Double i = (Double)obj.get(field);
				if (i != null)
					ret = i.intValue();
			}
			else if (obj.get(field) instanceof Integer)
			{
				Integer i = (Integer)obj.get(field);
				if (i != null)
					ret = i.intValue();
			}
				
		}
		
		return ret;
	}

	public void setObjectInt(String objName, String field, int value)
	{
		DBObject obj = (DBObject)get(objName);
		if (obj == null)
			obj = new BasicDBObject();
		
		obj.put(field, value);
		put(objName, obj);
	}
	
	protected BasicDBList getList(String name)
	{
		BasicDBList ret = (BasicDBList)get(name);
		if (ret == null)
			ret = new BasicDBList();
		
		return ret;
	}
	
	protected void setList(String name, BasicDBList obj)
	{
		put(name, obj);
	}

}
