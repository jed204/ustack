package com.untzuntz.ustack.uisupport.widgets;

import java.util.List;
import java.util.Vector;

import nextapp.echo.app.Button;
import nextapp.echo.app.Color;
import nextapp.echo.app.Column;
import nextapp.echo.app.Grid;
import nextapp.echo.app.event.ActionEvent;
import nextapp.echo.app.event.ActionListener;
import nextapp.echo.app.layout.ColumnLayoutData;

import org.apache.log4j.Logger;

import com.mongodb.BasicDBList;
import com.mongodb.DBObject;
import com.untzuntz.ustack.aaa.Authorization;
import com.untzuntz.ustack.aaa.LinkActionHelper;
import com.untzuntz.ustack.aaa.LinkActionInterface;
import com.untzuntz.ustack.aaa.ResourceDefinition;
import com.untzuntz.ustack.aaa.ResourceLink;
import com.untzuntz.ustack.aaa.RoleDefinition;
import com.untzuntz.ustack.aaa.UBasePermission;
import com.untzuntz.ustack.data.UserAccount;
import com.untzuntz.ustack.main.Msg;
import com.untzuntz.ustack.uisupport.LinkUIComponent;
import com.untzuntz.ustack.uisupport.UButton;
import com.untzuntz.ustack.uisupport.UColumn;
import com.untzuntz.ustack.uisupport.UEntryError;
import com.untzuntz.ustack.uisupport.UGrid;
import com.untzuntz.ustack.uisupport.ULabel;
import com.untzuntz.ustack.uisupport.UStackStatics;

public class UResourceRoleSelect extends Column implements ActionListener {

	static Logger       logger               = Logger.getLogger(UResourceRoleSelect.class);
	private static final long serialVersionUID = 1L;

	private Column resourcesCol;
	private Column rolesCol;
	private List<ResourceDefinition> resources;
	private List<ActionListener> actions;
	private String actionCommand;
	private static final ColumnLayoutData cld = new ColumnLayoutData();
	private String selectedResource;
	private String selectedRole;
	private LinkActionInterface linkAction;
	private UserAccount user;
	
	public UResourceRoleSelect(UserAccount user)
	{
		this.user = user;
		reset();
	}
	
	private void reset()
	{
		removeAll();
		
		Grid inGrid = new UGrid(2, UStackStatics.IN_5);
		inGrid.setColumnWidth(0, UStackStatics.EX_395);
		inGrid.setColumnWidth(1, UStackStatics.EX_395);
		add(inGrid);
		
		cld.setBackground(UStackStatics.DARK_BLUE);

		actions = new Vector<ActionListener>();
		actionCommand = "roleSelected";
		
		resourcesCol = new UColumn(UStackStatics.EX_2);
		resourcesCol.setLayoutData(UStackStatics.GRID_TOP);
		rolesCol = new UColumn(UStackStatics.EX_2);
		rolesCol.setLayoutData(UStackStatics.GRID_TOP);
		
		inGrid.add(resourcesCol);
		inGrid.add(rolesCol);
		
		resources = new Vector<ResourceDefinition>();
	}
	
	public void setActionCommand(String cmd) {
		actionCommand = cmd;
	}
	
	public void addActionListener(ActionListener al)
	{
		actions.add(al);
	}
	
	public void loadResources(List<ResourceDefinition> resList)
	{
		resources.clear();
		resources.addAll(resList);
		
		resourcesCol.removeAll();
		resourcesCol.add(new ULabel("Select a resource:", UStackStatics.FONT_NORMAL_BOLD));
		
		for (ResourceDefinition res : resList)
		{
			Button btn = new UButton(res.getName(), UStackStatics.WEB_BUTTON, this, "selectResource");
			btn.setInsets(UStackStatics.IN_5);
			btn.set("name", res.getName());
			resourcesCol.add(btn);
		}
	}
	
	private void loadRoles(String resourceName)
	{
		for (ResourceDefinition res : resources)
		{
			if (resourceName.equalsIgnoreCase(res.getName()))
				loadRoles(res);
		}
	}

	private String allowDefinitionType;
	public void setAllowDefinitionType(String t) {
		allowDefinitionType = t;
		logger.info("allowDefinitionType => '" + allowDefinitionType + "'");
	}
	
	private void loadRoles(ResourceDefinition res)
	{
		rolesCol.removeAll();
		rolesCol.add(new ULabel("Select a role:", UStackStatics.FONT_NORMAL_BOLD));

		logger.info("Presenting roles: " + allowDefinitionType);
		List<RoleDefinition> roles = res.getVisibleRoles(user, allowDefinitionType);
		for (RoleDefinition roleDef : roles)
		{
			Button btn = new UButton(roleDef.getName(), UStackStatics.WEB_BUTTON, this, "selectRole");
			btn.setInsets(UStackStatics.IN_5);
			btn.set("name", roleDef.getName());
			rolesCol.add(btn);
		}
		
//		int myLevel = 0;
//		boolean permitted = false;
//		if (ResourceDefinition.TYPE_SITEPROFILE.equalsIgnoreCase(res.getType()))
//			permitted = true;
//		else if (allowDefinitionType != null && allowDefinitionType.equalsIgnoreCase(res.getType()))
//			permitted = true;
//		else
//		{		
//			if (Authorization.authorizeUserBool(user, res.getInternalName(), UBasePermission.ManageRoles)) // check for this specific app
//				permitted = true;
//			
//			if (!permitted)
//			{
//				if (Authorization.authorizeUserBool(user, res.getName(), UBasePermission.ManageRoles)) // check for this specific app
//					permitted = true;
//			}
//			
//			if (!permitted)
//				permitted = Authorization.authorizeUserBool(user, "Setup App", UBasePermission.ManageRoles); // check for the over setup app
//			
//			if (!permitted)
//				myLevel = user.getRoleLevel(res); // still nothing so prep the role levels
//		}
//	
//		logger.info("User Role Level: " + myLevel);
//		BasicDBList roles = res.getRoleList();
//		for (int i = 0; i < roles.size(); i++)
//		{
//			DBObject obj = (DBObject)roles.get(i);
//			RoleDefinition roleDef = new RoleDefinition(obj);
//
//			logger.info("Role Def [" + roleDef.getName() + "] => Order: " + roleDef.getRoleOrder());
//			if (permitted || roleDef.getRoleOrder() >= myLevel)
//			{
//				Button btn = new UButton(roleDef.getName(), UStackStatics.WEB_BUTTON, this, "selectRole");
//				btn.setInsets(UStackStatics.IN_5);
//				btn.set("name", roleDef.getName());
//				rolesCol.add(btn);
//			}
//		}
	}

	public void actionPerformed(ActionEvent e) {
		
		String action = e.getActionCommand();
		
		if ("selectResource".equalsIgnoreCase(action))
		{
			for (int i = 0; i < resourcesCol.getComponentCount(); i++)
			{
				resourcesCol.getComponent(i).setLayoutData(null);
				resourcesCol.getComponent(i).setForeground(null);
			}
			
			Button btn = (Button)e.getSource();
			btn.setLayoutData(cld);
			btn.setForeground(Color.WHITE);
			selectedResource = (String)btn.get("name");
			
			loadRoles(selectedResource);
		}
		else if ("selectRole".equalsIgnoreCase(action))
		{
			for (int i = 0; i < rolesCol.getComponentCount(); i++)
			{
				rolesCol.getComponent(i).setLayoutData(null);
				rolesCol.getComponent(i).setForeground(null);
			}
			
			Button btn = (Button)e.getSource();
			btn.setLayoutData(cld);
			btn.setForeground(Color.WHITE);
			
			selectedRole = (String)btn.get("name");
			
			loadLinkSelect();
		}
		else if ("saveLink".equalsIgnoreCase(action))
		{
			removeAll();
			runActions();
		}
	}
	
	private void loadLinkSelect()
	{
		ResourceDefinition def = ResourceDefinition.getByName(selectedResource);
		RoleDefinition roleDef = def.getRoleByName(selectedRole);
		if (roleDef.getLinkActionClass() != null)
			linkAction = LinkActionHelper.getLinkAction(roleDef.getLinkActionClass());
		else
			linkAction = LinkActionHelper.getLinkAction(def.getLinkActionClass());
		
		if (linkAction == null)
		{
			runActions();
			return;
		}
		// setup the user
		linkAction.setUser(user);
		
		// get the ui
		LinkUIComponent ui = linkAction.getLinkSelectUI();
		if (ui == null)
		{
			runActions();
			return;
		}

		ui.setActionCommand("saveLink");
		ui.addActionListener(this);
		
		removeAll();
		add(ui);
	}
	
	private void runActions()
	{
		ActionEvent ae = new ActionEvent(this, actionCommand);
		for (ActionListener al : actions)
			al.actionPerformed(ae);
	}

	public List<UEntryError> validateEntries()
	{
		List<UEntryError> ret = new Vector<UEntryError>();
	
		if (getResourceLink() == null || (linkAction != null && linkAction.getResourceLinkExtras(null) == null))
			ret.add(UEntryError.getInstance("ResourceSelect", Msg.getString("EntryError-ResourceSelect", "Resource Role Select")));
		
		return ret;
	}
	
	public ResourceLink getResourceLink()
	{
		if (selectedResource == null)
			return null;
		if (selectedRole == null)
			return null;
		
		ResourceDefinition def = ResourceDefinition.getByName(selectedResource);
		ResourceLink ret = new ResourceLink(def, selectedRole);
		
		if (linkAction != null)
			ret.putAll(linkAction.getResourceLinkExtras(ret));
		
		return ret;
	}

}
