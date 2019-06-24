package com.textrecruit.ustack.aaa;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import com.textrecruit.ustack.data.UntzDBObject;
import com.textrecruit.ustack.data.UserAccount;
import com.textrecruit.ustack.exceptions.AccountExistsException;
import com.textrecruit.ustack.exceptions.ObjectExistsException;
import com.textrecruit.ustack.exceptions.PasswordLengthException;
import com.textrecruit.ustack.main.UAppCfg;
import org.apache.log4j.Logger;
import org.bson.types.ObjectId;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.textrecruit.ustack.data.UDataCache;
import com.textrecruit.ustack.main.UOpts;

public class ResourceDefinition extends UntzDBObject {

	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(ResourceDefinition.class);
	
	public static final String STATUS_DISABLED = "Disabled";
	public static final String STATUS_ACTIVE = "Active";

	public static final String TYPE_APIACCESS = "APIAccess";
	public static final String TYPE_USERACCESS = "UserAccess";
	public static final String TYPE_SITEPROFILE = "SiteProfile";

	public String getCollectionName() { return "resources"; }
	
	private ResourceDefinition()
	{
		// setup basic values on account
		put("created", new Date());
	}

	/** Return the name of the database that houses the 'users' collection */
	public static final String getDatabaseName() {
		
		if (UOpts.getString(UAppCfg.DATABASE_RESOURCE_COL) != null)
			return UOpts.getString(UAppCfg.DATABASE_RESOURCE_COL);
		
		return UOpts.getAppName();

	}

	/**
	 * Generate a ResourceDefinition object from the MongoDB object
	 * @param res
	 */
	public ResourceDefinition(DBObject res) {
		super(res);
	}
	
	public String toString() {
		return getName();
	}
	
	private void setName(String name)
	{
		put("name", name);
	}

	/** Returns name of the ResourceDefinition */
	public String getName()
	{
		return (String)get("name");
	}
	
	public void setInternalName(String name)
	{
		if (name == null || name.length() == 0)
		{
			removeField("internalName");
			return;
		}
		
		put("internalName", name);
	}

	/** Returns internal name (what is used in the application code) of the ResourceDefinition */
	public String getInternalName()
	{
		if (get("internalName") == null)
			return getName();
		
		return (String)get("internalName");
	}
	
	public void setPartners(String p)
	{
		if (p == null || p.length() == 0)
		{
			removeField("partner");
			return;
		}
	
		BasicDBList ret = new BasicDBList();
		String[] prts = p.split(",");
		for (int i = 0; i < prts.length; i++)
			ret.add(prts[i]);
		
		put("partner", ret);
	}
	
	public String getPartnersCsv() {
		
		BasicDBList partners = getPartners();
		if (partners == null)
			return null;
		
		StringBuffer ret = new StringBuffer();
		for (int i = 0; i < partners.size(); i++)
		{
			String p = (String)partners.get(i);
			ret.append(p);
			if ((i + 1) < partners.size())
				ret.append(",");
		}
		return ret.toString();		
	}

	/** Returns the 'partner' value to restrict loading */
	public BasicDBList getPartners()
	{
		BasicDBList ret = (BasicDBList)get("partner");
		if (ret == null)
			ret = new BasicDBList();
		
		return ret;
	}

	
	/**
	 * True if:
	 *    - no partner provided
	 *    - no partners defined
	 *    - partner matches what is in the list
	 *    
	 *    
	 * False if:
	 *    - partner is not defined in the list
	 * 
	 * 
	 * 
	 * @param p
	 * @return
	 */
	public boolean partnerMatch(String p) {
		
		if (p == null)
			return true;
		
		BasicDBList partners = getPartners();
		if (partners == null || partners.size() == 0)
			return true;
		
		List<String> prts = new ArrayList<String>();
		for (int i = 0; i < partners.size(); i++)
			prts.add( (String)partners.get(i) );
		
		return prts.contains(p);
	}
	
	private void setType(String type)
	{
		put("type", type);
	}

	/** Returns type of the ResourceDefinition */
	public String getType()
	{
		return (String)get("type");
	}
	
	public void copyFrom(final ResourceDefinition def) {
		def.remove("_id");
		def.remove("name");
		def.remove("created");
		putAll((DBObject)def);
	}

	/** Returns a comma-separated string of roles */
	public String getRoleListString()
	{
		StringBuffer res = new StringBuffer();
		
		BasicDBList roleList = getRoleList();
		for (int i = 0; i < roleList.size(); i++)
		{
			DBObject role = (DBObject)roleList.get(i);
			res.append((String)role.get("name"));
			if ((i + 1) < roleList.size())
				res.append(", ");
		}		
		
		return res.toString();
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
	
	public void clearCanManage()
	{
		removeField("canManageList");
	}
	
	public void clearManagedBy()
	{
		removeField("managedByList");
	}
	
	public BasicDBList getManagedByList()
	{
		BasicDBList ret = (BasicDBList)get("managedByList");
		if (ret == null)
			ret = new BasicDBList();
		
		return ret;
	}
	
	public void setSupportEmail(String email)
	{
		if (email == null || email.length() == 0)
			removeField("supportEmail");
		else
			put("supportEmail", email);
	}
	
	public String getSupportEmail() 
	{
		return getString("supportEmail");
	}

	/**
	 * Returns a list of roles for this resource definition that are visible to this user based on user privilege
	 * @param user
	 * @return
	 */
	public List<RoleDefinition> getVisibleRoles(UserAccount user)
	{
		return getVisibleRoles(user, null);
	}
	
	/**
	 * Returns a list of visible roles based on a user account AND optionally the allowDefinitionType (if provided, all roles will be visible for this type)
	 * @param user
	 * @param allowDefinitionType
	 * @return
	 */
	public List<RoleDefinition> getVisibleRoles(UserAccount user, String allowDefinitionType)
	{
		List<RoleDefinition> ret = new ArrayList<RoleDefinition>();
		int myLevel = 0;
		boolean permitted = false;
		if (ResourceDefinition.TYPE_SITEPROFILE.equalsIgnoreCase(getType()))
			permitted = true;
		else if (allowDefinitionType != null && allowDefinitionType.equalsIgnoreCase(getType()))
			permitted = true;
		else
		{
			if (Authorization.authorizeUserBool(user, getInternalName(), UBasePermission.ManageRoles)) // check for this specific app
				permitted = true;
			
			if (!permitted)
			{
				if (Authorization.authorizeUserBool(user, getName(), UBasePermission.ManageRoles)) // check for this specific app
					permitted = true;
			}
			
			if (!permitted)
				permitted = Authorization.authorizeUserBool(user, "Setup App", UBasePermission.ManageRoles); // check for the over setup app
			
			if (!permitted)
				myLevel = user.getRoleLevel(this); // still nothing so prep the role levels
		}
	
		BasicDBList roles = getRoleList();
		for (int i = 0; i < roles.size(); i++)
		{
			DBObject obj = (DBObject)roles.get(i);
			RoleDefinition roleDef = new RoleDefinition(obj);

			logger.info("Role Def [" + roleDef.getName() + "] => Order: " + roleDef.getRoleOrder());
			if (permitted || roleDef.getRoleOrder() >= myLevel)
				ret.add(roleDef);
		}
		
		return ret;
	}
	
	public BasicDBList getRoleList()
	{
		BasicDBList ret = (BasicDBList)get("roleList");
		if (ret == null)
			ret = new BasicDBList();
		
		return ret;
	}
	
	private void setRoleList(BasicDBList list)
	{
		put("roleList", list);
	}

	/**
	 * Adds a role to the current definition
	 * 
	 * @param role
	 * @throws ObjectExistsException
	 */
	public void addRole(RoleDefinition role) throws ObjectExistsException {

		if (hasRole(role.getName()))
			throw new ObjectExistsException();
		
		BasicDBList roleList = getRoleList();
		roleList.add(role);
		setRoleList(roleList);
	}
	
	/**
	 * Determines if the role (by name) already exists for this resource
	 * @param roleName
	 * @return
	 */
	public boolean hasRole(String roleName) {
		
		if (getRoleByName(roleName) != null)
			return true;
		
		return false;
	}
	
	/**
	 * Returns a RoleDefinition object for the requested role
	 * 
	 * @param roleName
	 * @return
	 */
	public RoleDefinition getRoleByName(String roleName)
	{
		BasicDBList roleList = getRoleList();
		for (int i = 0; i < roleList.size(); i++)
		{
			DBObject role = (DBObject)roleList.get(i);
			String rn = (String)role.get("name");
			if (rn.equalsIgnoreCase(roleName))
				return new RoleDefinition(role);
		}
		return null;
	}
	
	public void deleteRole(RoleDefinition tgtRole)
	{
		BasicDBList roleList = getRoleList();
		for (int i = 0; i < roleList.size(); i++)
		{
			DBObject role = (DBObject)roleList.get(i);
			String rn = (String)role.get("name");
			if (rn.equalsIgnoreCase(tgtRole.getName()))
			{
				roleList.remove(i);
				i--;
			}
		}
		setRoleList(roleList);
	}
	
	/**
	 * Updates an existing role definition
	 * @param newRole
	 */
	public void setRole(RoleDefinition newRole)
	{
		boolean found = false;
		BasicDBList roleList = getRoleList();
		for (int i = 0; i < roleList.size(); i++)
		{
			DBObject role = (DBObject)roleList.get(i);
			String rn = (String)role.get("name");
			if (rn.equalsIgnoreCase(newRole.getName()))
			{
				found = true;
				roleList.set(i, newRole);
			}
		}
		
		if (!found)
			roleList.add(newRole);
		
		setRoleList(roleList);
	}
	
	/**
	 * Action class that can optionally support:
	 * 
	 * 1. Link selection support
	 * 2. New link action
	 * 3. Remove link action
	 * 
	 * @param actionClass
	 */
	public void setLinkActionClass(String actionClass)
	{
		if (actionClass == null || actionClass.length() == 0)
			removeField("linkActionClass");
		else
			put("linkActionClass", actionClass);
	}

	/**
	 * Returns the class responsible for link activities
	 * 
	 * @return
	 */
	public String getLinkActionClass()
	{
		return getString("linkActionClass");
	}

	/**
	 * Create a new user account
	 * 
	 * @param userName
	 * @param password
	 * @return
	 * @throws AccountExistsException
	 * @throws PasswordLengthException
	 */
	public static ResourceDefinition createResource(String name, String type) throws ObjectExistsException
	{
		ResourceDefinition res = getByName(name);
		if (res != null) // already exists
			throw new ObjectExistsException();
		
		// create the actual account
		res = new ResourceDefinition();
		res.setName(name);
		res.setInternalName(name);
		res.setType(type);
		logger.info("Creating resource '" + name + "'");
		
		return res;
	}

	/**
	 * Get a resource by internal name
	 * 
	 * @param name
	 * @return
	 */
	public static ResourceDefinition getByInternalName(String name)
	{
		if (UOpts.getCacheEnabled())
		{
			ResourceDefinition rd = (ResourceDefinition)UDataCache.getInstance().get("urdi-" + name.replaceAll(" ", "_"));
			if (rd != null)
				return rd;
		}
		
		DBObject res = new ResourceDefinition().getCollection().findOne(BasicDBObjectBuilder.start("internalName", name).get());
		
		if (res == null)
			return null;
		
		ResourceDefinition rd = new ResourceDefinition(res);
		rd.cache();
		return rd;
	}

	/**
	 * Get a resource by name
	 * 
	 * @param name
	 * @return
	 */
	public static ResourceDefinition getByName(String name)
	{
		logger.debug("Getting resource from database or cache [" + name + "]");
		if (UOpts.getCacheEnabled())
		{
			String key = "urd-" + name.replaceAll(" ", "_");
			logger.debug("Trying to find in cache [" + key + "]");
			ResourceDefinition rd = (ResourceDefinition)UDataCache.getInstance().get(key);
			if (rd != null && rd.get("_id") != null)
				return rd;
		}
			
		DBObject res = new ResourceDefinition().getCollection().findOne(BasicDBObjectBuilder.start("name", name).get());
		
		if (res == null)
			return null;
		
		ResourceDefinition rd = new ResourceDefinition(res);
		rd.cache();
		return rd;
	}
	
	/**
	 * Get a resource by id
	 * 
	 * @param id
	 * @return
	 */
	public static ResourceDefinition getById(String id)
	{
		if (UDataCache.getInstance() != null)
		{
			ResourceDefinition rd = (ResourceDefinition)UDataCache.getInstance().get("urdid-" + id);
			if (rd != null)
				return rd;
		}
		
		if (id == null || "null".equalsIgnoreCase(id))
			return null;

		DBObject res = new ResourceDefinition().getCollection().findOne(BasicDBObjectBuilder.start("_id", new ObjectId(id)).get());
		
		if (res == null)
			return null;
		
		ResourceDefinition rd = new ResourceDefinition(res);
		rd.cache();
		return rd;
	}
	
	protected void decache()
	{
		decache("urdi-" + getString("internalName").replaceAll(" ", "_"));
		decache("urd-" + getString("name").replaceAll(" ", "_"));
		decache("urdid-" + get("_id"));
	}
	
	protected void cache()
	{
		cache("urdi-" + getString("internalName").replaceAll(" ", "_"));
		cache("urd-" + getString("name").replaceAll(" ", "_"));
		cache("urdid-" + get("_id"));
	}
	
	/**
	 * Returns all ResourceDefinitions of 'type' in the system database
	 * @return
	 */
	public static List<ResourceDefinition> getAll(String type, BasicDBList managedByList)
	{
		DBObject search = BasicDBObjectBuilder.start("type", type).get();
		if (managedByList != null)
		{
			String[] mbArray = new String[managedByList.size()];
			for (int i = 0; i < managedByList.size(); i++)
				mbArray[i] = (String)managedByList.get(i);
			
			search.put("managedByList", BasicDBObjectBuilder.start("$in", mbArray).get());
		}
		
		List<ResourceDefinition> ret = new Vector<ResourceDefinition>();
		DBCursor cur = new ResourceDefinition().getCollection().find(search).sort( BasicDBObjectBuilder.start("name", 1).get() );
		while (cur.hasNext())
			ret.add(new ResourceDefinition(cur.next()));
		
		return ret;
	}

	/**
	 * Returns all ResourceDefinitions in the system database
	 * @return
	 */
	public static List<ResourceDefinition> getAll()
	{
		List<ResourceDefinition> ret = new Vector<ResourceDefinition>();
		DBCursor cur = new ResourceDefinition().getCollection().find().sort( BasicDBObjectBuilder.start("name", 1).get() );
		while (cur.hasNext())
			ret.add(new ResourceDefinition(cur.next()));
		
		return ret;
	}

}
