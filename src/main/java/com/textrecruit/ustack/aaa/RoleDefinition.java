package com.textrecruit.ustack.aaa;

import java.util.Date;
import java.util.List;
import java.util.Vector;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class RoleDefinition extends BasicDBObject implements DBObject {

	private static final long serialVersionUID = 1L;
	
	public static final String STATUS_DISABLED = "Disabled";
	public static final String STATUS_ACTIVE = "Active";
	
	@SuppressWarnings("unused")
	private RoleDefinition() {}
	
	public RoleDefinition(String name)
	{
		setName(name);
		put("created", new Date());
	}

	public RoleDefinition(RoleDefinition current, String name)
	{
		setName(name);
		// permissions
		BasicDBList newList = new BasicDBList();
		newList.addAll(current.getPermissionList());
		setPermissionList(newList);
		// plugins
		BasicDBList newPluginList = new BasicDBList();
		newPluginList.addAll(current.getPluginList());
		setPluginList(newPluginList);
		// setup
		put("created", new Date());
	}

	/**
	 * Generate a RoleDefinition object from the MongoDB object
	 * @param res
	 */
	public RoleDefinition(DBObject res) {
		putAll(res);
	}
	
	private void setName(String name)
	{
		put("name", name);
	}
	
	public String getName()
	{
		return (String)get("name");
	}
	
	public void setObjectTemplate(String name)
	{
		if (name != null && name.length() > 0)
			put("objTemplateName", name);
		else
			removeField("objTemplateName");
	}
	
	public String getObjectTemplate()
	{
		return getString("objTemplateName");
	}
	
	public String getLinkActionClass()
	{
		return getString("linkActionClass");
	}
	
	public void setLinkActionClass(String actionClass)
	{
		if (actionClass == null || actionClass.length() == 0)
			removeField("linkActionClass");
		else
			put("linkActionClass", actionClass);
	}
	
	public void setRoleOrder(int order)
	{
		put("order", new Integer(order));
	}
	
	public int getRoleOrder() 
	{
		Integer val = (Integer)get("order");
		if (val == null)
			return 0;
		
		return val;
	}
	
	/** Returns a comma-separated string of roles */
	public String getPermissionListString()
	{
		StringBuffer res = new StringBuffer();
		
		BasicDBList permList = getPermissionList();
		for (int i = 0; i < permList.size(); i++)
		{
			DBObject role = (DBObject)permList.get(i);
			res.append((String)role.get("name"));
			if ((i + 1) < permList.size())
				res.append(", ");
		}
		
		return res.toString();
	}
	
	/** Delete a permission by name */
	public void deletePermission(String permissionName)
	{
		BasicDBList permList = getPermissionList();
		for (int i = 0; i < permList.size(); i++)
		{
			DBObject role = (DBObject)permList.get(i);
			String rn = (String)role.get("name");
			if (rn.equalsIgnoreCase(permissionName))
			{
				permList.remove(i);
				i--;
			}
		}
		setPermissionList(permList);
	}
	
	/** Add a permission by name */
	public void addPermission(String permissionName)
	{
		BasicDBList list = getPermissionList();
		for (int i = 0; i < list.size(); i++)
		{
			DBObject perm = (DBObject)list.get(i);
			if (permissionName.equalsIgnoreCase( (String)perm.get("name") ))
				return;
		}
		
		DBObject perm = new BasicDBObject();
		perm.put("name", permissionName);
		perm.put("created", new Date());
		list.add(perm);
		setPermissionList(list);
	}

	/**
	 * Returns a list of plugins based on the provided container name
	 * @param container
	 * @return
	 */
	public List<String> getPluginsByContainer(String container)
	{
		List<String> ret = new Vector<String>();
		BasicDBList pliginList = getPluginList();
		for (int i = 0; i < pliginList.size(); i++)
		{
			DBObject plugin = (DBObject)pliginList.get(i);
			if (container == null || container.equalsIgnoreCase( (String)plugin.get("parent") ))
				ret.add( (String)plugin.get("name") );
		}
		return ret;
	}

	/**
	 * Determines if the permission is defined for this
	 * @param roleName
	 * @return
	 */
	public boolean hasPermission(String permissionName) {
		
		BasicDBList list = getPermissionList();
		for (int i = 0; i < list.size(); i++)
		{
			DBObject perm = (DBObject)list.get(i);
			if (permissionName.equalsIgnoreCase( (String)perm.get("name") ))
				return true;
		}
		
		return false;
	}

	public BasicDBList getTOSList()
	{
		BasicDBList ret = (BasicDBList)get("tosList");
		if (ret == null)
			ret = new BasicDBList();
		
		return ret;
	}
	
	public void addTOS(String name)
	{
		BasicDBList list = getTOSList();
		for (int i = 0; i < list.size(); i++)
		{
			String tos = (String)list.get(i);
			if (tos.equalsIgnoreCase( name ))
				return;
		}
		
		list.add(name);
		setTOSList(list);
	}
	
	public void removeTOS(String name)
	{
		BasicDBList list = getTOSList();
		for (int i = 0; i < list.size(); i++)
		{
			String tos = (String)list.get(i);
			if (tos.equalsIgnoreCase( name ))
			{
				list.remove(i);
				i--;
			}
		}
		
		setTOSList(list);
	}
	
	private void setTOSList(BasicDBList list)
	{
		put("tosList", list);
	}
	
	public BasicDBList getPermissionList()
	{
		BasicDBList ret = (BasicDBList)get("permList");
		if (ret == null)
			ret = new BasicDBList();
		
		return ret;
	}
	
	private void setPermissionList(BasicDBList list)
	{
		put("permList", list);
	}
	
	public BasicDBList getAdditionalResRolesList()
	{
		BasicDBList ret = (BasicDBList)get("addtResRoleList");
		if (ret == null)
			ret = new BasicDBList();
		
		return ret;
	}
	
	public void setAdditionalResRolesList(BasicDBList list)
	{
		put("addtResRoleList", list);
	}
	
	public BasicDBList getAutoSubscriptionList()
	{
		BasicDBList ret = (BasicDBList)get("autoSubList");
		if (ret == null)
			ret = new BasicDBList();
		
		return ret;
	}
	
	public void setAutoSubscriptionList(BasicDBList list)
	{
		put("autoSubList", list);
	}
	
	public BasicDBList getVariableList()
	{
		BasicDBList ret = (BasicDBList)get("variableList");
		if (ret == null)
			ret = new BasicDBList();
		
		return ret;
	}
	
	public void setVariableList(BasicDBList list)
	{
		put("variableList", list);
	}

	public BasicDBList getPluginList()
	{
		BasicDBList ret = (BasicDBList)get("pluginList");
		if (ret == null)
			ret = new BasicDBList();
		
		return ret;
	}
	
	public void setPluginList(BasicDBList list)
	{
		put("pluginList", list);
	}

	/** Returns a comma-separated string of roles */
	public String getPluginListString()
	{
		StringBuffer res = new StringBuffer();
		
		BasicDBList pliginList = getPluginList();
		for (int i = 0; i < pliginList.size(); i++)
		{
			DBObject role = (DBObject)pliginList.get(i);
			res.append((String)role.get("name"));
			if ((i + 1) < pliginList.size())
				res.append(", ");
		}
		
		return res.toString();
	}
	
	public void deleteAutoSub(String event, String method)
	{
		BasicDBList autoSubList = getAutoSubscriptionList();
		for (int i = 0; i < autoSubList.size(); i++)
		{
			DBObject plugin = (DBObject)autoSubList.get(i);
			if (event.equalsIgnoreCase((String)plugin.get("event")) && method.equalsIgnoreCase((String)plugin.get("method")))
			{
				autoSubList.remove(i);
				i--;
			}
		}
		setAutoSubscriptionList(autoSubList);
	}
		
	public void deleteVariable(String var)
	{
		BasicDBList varList = getVariableList();
		for (int i = 0; i < varList.size(); i++)
		{
			DBObject obj = (DBObject)varList.get(i);
			if (var.equalsIgnoreCase((String)obj.get("var")))
			{
				varList.remove(i);
				i--;
			}
		}
		setVariableList(varList);
	}
	
	public DBObject getVariableValue(String var)
	{
		BasicDBList varList = getVariableList();
		for (int i = 0; i < varList.size(); i++)
		{
			DBObject obj = (DBObject)varList.get(i);
			if (var.equalsIgnoreCase((String)obj.get("var")))
				return (DBObject)obj.get("val");
		}
		return null;
	}
		
	public void deleteAddtResRole(String resName, String role)
	{
		BasicDBList addtResRoleList = getAdditionalResRolesList();
		for (int i = 0; i < addtResRoleList.size(); i++)
		{
			DBObject plugin = (DBObject)addtResRoleList.get(i);
			if (resName.equalsIgnoreCase((String)plugin.get("resName")) && role.equalsIgnoreCase((String)plugin.get("role")))
			{
				addtResRoleList.remove(i);
				i--;
			}
		}
		setAdditionalResRolesList(addtResRoleList);
	}
		
	/** Delete a plugin by name */
	public void deletePlugin(String pluginName, String pluginParent)
	{
		BasicDBList pliginList = getPluginList();
		for (int i = 0; i < pliginList.size(); i++)
		{
			DBObject plugin = (DBObject)pliginList.get(i);
			if (pluginName.equalsIgnoreCase((String)plugin.get("name")) && pluginParent.equalsIgnoreCase((String)plugin.get("parent")))
			{
				pliginList.remove(i);
				i--;
			}
		}
		setPluginList(pliginList);
	}
	
	/** Add an additional resource role */
	public void addAdditionalResourceRole(String resName, String role)
	{
		BasicDBList list = getAdditionalResRolesList();
		for (int i = 0; i < list.size(); i++)
		{
			DBObject perm = (DBObject)list.get(i);
			if (resName.equalsIgnoreCase( (String)perm.get("resName") ) && role.equalsIgnoreCase((String)perm.get("role")))
				return;
		}
		
		DBObject perm = new BasicDBObject();
		perm.put("resName", resName);
		perm.put("role", role);
		perm.put("created", new Date());
		list.add(perm);
		setAdditionalResRolesList(list);
	}
	
	/** Add a variable to the role */
	public void addVariable(String var, DBObject val)
	{
		DBObject perm = null;
		BasicDBList list = getVariableList();
		for (int i = 0; perm == null && i < list.size(); i++)
		{
			DBObject obj = (DBObject)list.get(i);
			if (var.equalsIgnoreCase( (String)obj.get("var") ))
				perm = obj;
		}
		
		if (perm == null)
		{
			perm = new BasicDBObject();
			perm.put("created", new Date());
			list.add(perm);
		}
		
		perm.put("var", var);
		perm.put("val", val);
		
		setVariableList(list);
	}
	
	/** Add an auto-subscription */
	public void addAutoSubscription(String event, String method)
	{
		BasicDBList list = getAutoSubscriptionList();
		for (int i = 0; i < list.size(); i++)
		{
			DBObject perm = (DBObject)list.get(i);
			if (event.equalsIgnoreCase( (String)perm.get("event") ) && method.equalsIgnoreCase((String)perm.get("method")))
				return;
		}
		
		DBObject perm = new BasicDBObject();
		perm.put("event", event);
		perm.put("method", method);
		perm.put("created", new Date());
		list.add(perm);
		setAutoSubscriptionList(list);
	}
	
	/** Add a plugin by name */
	public void addPlugin(String pluginName, String pluginParent)
	{
		BasicDBList list = getPluginList();
		for (int i = 0; i < list.size(); i++)
		{
			DBObject perm = (DBObject)list.get(i);
			if (pluginName.equalsIgnoreCase( (String)perm.get("name") ) && pluginParent.equalsIgnoreCase((String)perm.get("parent")))
				return;
		}
		
		DBObject perm = new BasicDBObject();
		perm.put("name", pluginName);
		perm.put("parent", pluginParent);
		perm.put("created", new Date());
		list.add(perm);
		setPluginList(list);
	}
	
}
