package com.untzuntz.ustack.aaa;

import java.util.Date;

import com.mongodb.BasicDBList;
import com.mongodb.DBObject;
import com.untzuntz.ustack.data.UntzDBObject;

public class ResourceLink extends UntzDBObject {

	private static final long serialVersionUID = 1L;
	public String getCollectionName() { return "resourceLinks"; }
	private ResourceDefinition resDef;
	
	@SuppressWarnings("unused")
	private ResourceLink() {}
	
	public ResourceLink(ResourceDefinition def, String roleName)
	{
		loadData(def, roleName);
	}
	
	public void loadData(ResourceDefinition def, String roleName)
	{
		setInternalName(def.getInternalName());
		setName(def.getName());
		put("resDefId", def.get("_id") + "");
		put("role", roleName);
		put("created", new Date());
		
		BasicDBList mgdBy = def.getManagedByList();
		if (mgdBy != null)
			put("managedBy", mgdBy);
		resDef = def;
	}
	
	public ResourceDefinition getResourceDefinition()
	{
		if (resDef == null)
			return ResourceDefinition.getById( (String)get("resDefId") );
		
		return resDef;
	}

	/**
	 * Generate a RoleDefinition object from the MongoDB object
	 * @param res
	 */
	public ResourceLink(DBObject res) {
		putAll(res);
	}
	
	private void setName(String name)
	{
		put("name", name);
	}
	
	public String getName()
	{
		return getString("name");
	}
	
	private void setInternalName(String name)
	{
		put("internalName", name);
	}
	
	public String getInternalName()
	{
		return getString("internalName");
	}
	
	public String getRoleName()
	{
		return getString("role");
	}
	
	public String toString() 
	{
		return getLinkText();
	}
	
	public String getLinkText()
	{
		return getString("linkText");
	}

	public void setLinkText(String lt) 
	{
		put("linkText", lt);
	}

}
