package com.untzuntz.ustack.main.setup;

import java.util.List;

import nextapp.echo.app.Button;
import nextapp.echo.app.Column;
import nextapp.echo.app.Grid;
import nextapp.echo.app.Label;
import nextapp.echo.filetransfer.app.UploadSelect;
import nextapp.echo.filetransfer.app.event.UploadListener;

import com.mongodb.BasicDBList;
import com.mongodb.DBObject;
import com.untzuntz.ustack.aaa.ResourceDefinition;
import com.untzuntz.ustack.aaa.RoleDefinition;
import com.untzuntz.ustack.data.TermsConditions;
import com.untzuntz.ustack.data.UntzDBObjectTemplate;
import com.untzuntz.ustack.main.ApplicationInstance;
import com.untzuntz.ustack.main.Msg;
import com.untzuntz.ustack.main.UControllerInt;
import com.untzuntz.ustack.uisupport.UButton;
import com.untzuntz.ustack.uisupport.UCenterCol;
import com.untzuntz.ustack.uisupport.UColumn;
import com.untzuntz.ustack.uisupport.UEntryError;
import com.untzuntz.ustack.uisupport.UErrorColumn;
import com.untzuntz.ustack.uisupport.UGrid;
import com.untzuntz.ustack.uisupport.ULabel;
import com.untzuntz.ustack.uisupport.URow;
import com.untzuntz.ustack.uisupport.USelectField;
import com.untzuntz.ustack.uisupport.UStackStatics;
import com.untzuntz.ustack.uisupport.UTextArea;
import com.untzuntz.ustack.uisupport.UTextField;
import com.untzuntz.ustack.uisupport.UViewColumn;

import echopoint.Strut;

public class ResourceDefinitionsView extends UViewColumn {

	private static final long serialVersionUID = 1L;

	public ResourceDefinitionsView(UControllerInt ctrl) { super(ctrl); }
	
	private UTextField resourceDefName;
	private USelectField resourceDefType;
	private UTextField roleName;
	private UTextField permissionName;
	private UTextField pluginName;
	private UTextField pluginParent;
	private UTextField linkActionClass;
	private UTextField roleOrder;
	private UTextField roleLinkActionClass;
	private UTextField managedBy;
	private UTextField canManage;
	private UTextField internalName;
	private UTextField supportEmail;
	private UTextField event;
	private UTextField method;
	private UTextField var;
	private UTextArea val;
	private UTextField resName;
	private UTextField role;
	private USelectField tosName;
	private USelectField userTemplate;
	private Column roleEditCol;
	private UErrorColumn errors;
	private UErrorColumn permErrors;
	private UErrorColumn pluginErrors;
	
	public void setError(List<UEntryError> errList) { errors.setErrorList(errList); }
	public void setPermissionError(List<UEntryError> errList) { permErrors.setErrorList(errList); }
	public void setPluginError(List<UEntryError> errList) { pluginErrors.setErrorList(errList); }
	
	@Override
	public void setup() {

		removeAll();
		
		UCenterCol body = new UCenterCol();
		body.setInsets(UStackStatics.IN_15);
		body.setCellSpacing(UStackStatics.EX_5);
		add(body);

		Grid header = new Grid(2);
		header.setWidth(UStackStatics.EX_800);
		header.setColumnWidth(0, UStackStatics.EX_50P);
		body.add(header);

		body.add(new ULabel(Msg.getString("ResourceDefinitions"), UStackStatics.FONT_BOLD_LARGE));
		body.add(new Strut(800, 15));

		
		Grid resItems = new Grid(4);
		resItems.setInsets(UStackStatics.IN_5);
		resItems.setWidth(UStackStatics.EX_800);
		resItems.setColumnWidth(0, UStackStatics.EX_100);
		resItems.setColumnWidth(1, UStackStatics.EX_100);
		resItems.setColumnWidth(3, UStackStatics.EX_200);
		body.add(resItems);
		
		resItems.add(new ULabel(Msg.getString("Name"), UStackStatics.FONT_BOLD_MED));
		resItems.add(new ULabel(Msg.getString("Type"), UStackStatics.FONT_BOLD_MED));
		resItems.add(new ULabel(Msg.getString("Roles"), UStackStatics.FONT_BOLD_MED));
		resItems.add(new ULabel("", UStackStatics.FONT_NORMAL_BOLD));
		
		List<ResourceDefinition> items = ResourceDefinition.getAll();
		for (ResourceDefinition item : items)
		{
			// export
			Button export = new UButton(Msg.getString("Export"), UStackStatics.WEB_BUTTON, this, "exportResourceDefinition");
			export.set("name", item.getName());
			// edit
			Button edit = new UButton(Msg.getString("Edit"), UStackStatics.WEB_BUTTON, this, "editResourceDefinition");
			edit.set("name", item.getName());
			// duplicate
			Button dup = new UButton(Msg.getString("Duplicate"), UStackStatics.WEB_BUTTON, this, "duplicateResourceDefinition");
			dup.set("name", item.getName());
			// remove (delete)
			Button rem = new UButton(Msg.getString("Remove"), UStackStatics.WEB_BUTTON, this, "deleteResourceDefinintion");
			rem.set("name", item.getName());
			
			URow row = new URow();
			row.add(export);
			row.add(edit);
			row.add(dup);
			row.add(new Strut(10, 0));
			row.add(rem);
			
			resItems.add(new ULabel(item.getName(), 50));
			resItems.add(new ULabel(item.getType(), 50));
			resItems.add(new ULabel(item.getRoleListString(), 50));
			resItems.add(row);
		}

		resourceDefName = new UTextField(Msg.getString("ResourceDefinition"));
		resourceDefType = new USelectField(Msg.getString("ResourceType"), ApplicationInstance.getResourceTypes());
		
		resItems.add(resourceDefName);
		resItems.add(resourceDefType);
		resItems.add(new UButton(Msg.getString("Save"), UStackStatics.WEB_BUTTON, this, "saveResourceDef"));
		
		body.add(new UButton(Msg.getString("Import"), UStackStatics.WEB_BUTTON, this, "importResourceDef"));

		body.add(errors = new UErrorColumn());
	}

	public void prepareResourceImport()
	{
		removeAll();
		
		UCenterCol body = new UCenterCol();
		body.setInsets(UStackStatics.IN_15);
		body.setCellSpacing(UStackStatics.EX_5);
		add(body);

		Grid header = new Grid(2);
		header.setWidth(UStackStatics.EX_800);
		header.setColumnWidth(0, UStackStatics.EX_50P);
		body.add(header);

		body.add(new ULabel(Msg.getString("Import") + " " + Msg.getString("ResourceDefinitions"), UStackStatics.FONT_BOLD_LARGE));
		body.add(new Strut(800, 15));

		body.add(new ULabel(Msg.getString("FileSelect"), UStackStatics.FONT_BOLD_MED));
		
		UploadSelect uploadSelect = new UploadSelect();
        uploadSelect.addUploadListener((UploadListener)getController());
        body.add(uploadSelect);
        
        body.add(new Strut(0, 20));
        
        body.add(new UButton(Msg.getString("Cancel"), UStackStatics.WEB_BUTTON, this, "cancelResourceDefinitionEdit"));
		
		body.add(errors = new UErrorColumn());
	}

	
	/** Edit a ResourceDefinition (Add/Edit/Remove Roles) */
	public void editResourceDef(ResourceDefinition res)
	{
		removeAll();
		
		UCenterCol body = new UCenterCol();
		body.setInsets(UStackStatics.IN_15);
		body.setCellSpacing(UStackStatics.EX_5);
		add(body);

		Grid header = new Grid(2);
		header.setWidth(UStackStatics.EX_800);
		header.setColumnWidth(0, UStackStatics.EX_50P);
		body.add(header);

		if (res != null)
			header.add(new ULabel(res.getName() + " " + Msg.getString("Resource"), UStackStatics.FONT_BOLD_LARGE));
		header.add(new URow(new UButton(Msg.getString("Close"), UStackStatics.WEB_BUTTON, this, "cancelResourceDefinitionEdit"), UStackStatics.GRID_RIGHT));
		body.add(new Strut(800, 15));

		body.add(new UColumn(new ULabel(Msg.getString("BasicSettings"), UStackStatics.FONT_BOLD_LARGE), UStackStatics.BDR_SIMPLE, UStackStatics.LIGHT_GRAY));
		body.add(new Strut(800, 5));

		UGrid opts = new UGrid(2, UStackStatics.IN_5);
		body.add(opts);
		
		opts.add(Msg.getString("LinkActionClass") + ":");
		opts.add(linkActionClass = new UTextField(Msg.getString("LinkActionClass")));
		opts.add(Msg.getString("ManagedBy") + ":");
		opts.add(managedBy = new UTextField(Msg.getString("ManagedBy")));
		opts.add(Msg.getString("CanManage") + ":");
		opts.add(canManage = new UTextField(Msg.getString("CanManage")));
		opts.add(Msg.getString("supportEmail") + ":");
		opts.add(supportEmail = new UTextField(Msg.getString("supportEmail")));
		opts.add(Msg.getString("internalName") + ":");
		opts.add(internalName = new UTextField(Msg.getString("internalName")));
		opts.add("");
		opts.add(new URow(new UButton(Msg.getString("Save"), UStackStatics.WEB_BUTTON, this, "updateResourceDef")));

		linkActionClass.setWidth(UStackStatics.EX_325);
		linkActionClass.setText(res.getLinkActionClass());
		managedBy.setWidth(UStackStatics.EX_200);
		managedBy.setText(res.getManagedByListString());
		canManage.setWidth(UStackStatics.EX_200);
		canManage.setText(res.getCanManageListString());
		supportEmail.setWidth(UStackStatics.EX_200);
		supportEmail.setText(res.getSupportEmail());
		internalName.setText(res.getInternalName());
		internalName.setWidth(UStackStatics.EX_200);

		/*
		 * Roles
		 */
		body.add(new UColumn(new ULabel(Msg.getString("Roles"), UStackStatics.FONT_BOLD_LARGE), UStackStatics.BDR_SIMPLE, UStackStatics.LIGHT_GRAY));
		body.add(new Strut(800, 5));
		
		Grid roleGrid = new Grid(3);
		roleGrid.setInsets(UStackStatics.IN_5);
		roleGrid.setWidth(UStackStatics.EX_800);
		roleGrid.setColumnWidth(0, UStackStatics.EX_100);
		roleGrid.setColumnWidth(2, UStackStatics.EX_50);
		body.add(roleGrid);

		roleGrid.add(new ULabel(Msg.getString("Role"), UStackStatics.FONT_BOLD_MED));
		roleGrid.add(new ULabel(Msg.getString("Permissions"), UStackStatics.FONT_BOLD_MED));
		roleGrid.add(new ULabel("", UStackStatics.FONT_NORMAL_BOLD));

		BasicDBList roleList = res.getRoleList();
		for (int i = 0; i < roleList.size(); i++)
		{
			RoleDefinition role = new RoleDefinition((DBObject)roleList.get(i));
			
			Button edit = new UButton(Msg.getString("Edit"), UStackStatics.WEB_BUTTON, this, "editRole");
			edit.set("name", role.getName());
			Button dup = new UButton(Msg.getString("Duplicate"), UStackStatics.WEB_BUTTON, this, "duplicateRole");
			dup.set("name", role.getName());
			Button rem = new UButton(Msg.getString("Remove"), UStackStatics.WEB_BUTTON, this, "deleteRole");
			rem.set("name", role.getName());
			
			URow row = new URow();
			row.add(edit);
			row.add(new Strut(10,0));
			row.add(dup);
			row.add(rem);
			
			roleGrid.add(new ULabel(role.getName(), 50));
			roleGrid.add(new ULabel(role.getPermissionListString(), 50));
			roleGrid.add(row);
		}
		
		roleName = new UTextField(Msg.getString("Role"));
		roleName.addActionListener(this);
		roleName.setActionCommand("saveRole");
		
		roleGrid.add(roleName);
		roleGrid.add(new UButton(Msg.getString("Save"), UStackStatics.WEB_BUTTON, this, "saveRole"));

		body.add(errors = new UErrorColumn());
		
		roleEditCol = new UColumn();
		body.add(new Strut(800, 12));
		body.add(roleEditCol);

	}

	/** Edit a Role (Permissions of the role) */
	public void editRole(RoleDefinition roleDef)
	{
		roleEditCol.removeAll();
		
		roleEditCol.add(new UColumn(new ULabel(roleDef.getName() + " " + Msg.getString("Role"), UStackStatics.FONT_BOLD_LARGE), UStackStatics.BDR_SIMPLE, UStackStatics.LIGHT_GRAY));
		roleEditCol.add(new Strut(800, 5));
		
		URow mainRow = new URow();
		roleEditCol.add(mainRow);

		Column left = new UColumn();
		mainRow.add(left);
		/*
		 * Left Side (Permissions)
		 */
		Grid permGrid = new Grid(2);
		permGrid.setInsets(UStackStatics.IN_5);
		permGrid.setWidth(UStackStatics.EX_395);
		permGrid.setColumnWidth(1, UStackStatics.EX_25);
		permGrid.setLayoutData(UStackStatics.ROW_TOP);
		left.add(permGrid);

		permGrid.add(new ULabel(Msg.getString("Permission"), UStackStatics.FONT_BOLD_MED));
		permGrid.add(new ULabel("", UStackStatics.FONT_NORMAL_BOLD));

		BasicDBList permList = roleDef.getPermissionList();
		for (int i = 0; i < permList.size(); i++)
		{
			DBObject permission = (DBObject)permList.get(i);
			String permName = (String)permission.get("name");
			
			Button rem = new UButton(Msg.getString("Remove"), UStackStatics.WEB_BUTTON, this, "deletePermission");
			rem.set("name", permName);
			
			URow row = new URow();
			row.add(rem);
			
			permGrid.add(new Label(permName));
			permGrid.add(row);
		}
		
		permissionName = new UTextField(Msg.getString("Role"));
		permissionName.addActionListener(this);
		permissionName.setActionCommand("savePermission");
		
		permGrid.add(permissionName);
		permGrid.add(new UButton(Msg.getString("Save"), UStackStatics.WEB_BUTTON, this, "savePermission"));
		
		permErrors = new UErrorColumn();
		permErrors.setLayoutData(UStackStatics.GRID_SPAN2);
		roleEditCol.add(permErrors);

		
		/*
		 * TOS
		 */
		Grid tosGrid = new Grid(2);
		tosGrid.setInsets(UStackStatics.IN_5);
		tosGrid.setWidth(UStackStatics.EX_395);
		tosGrid.setColumnWidth(1, UStackStatics.EX_50);
		left.add(tosGrid);

		tosGrid.add(new ULabel(Msg.getString("Name"), UStackStatics.FONT_BOLD_MED));
		tosGrid.add(new ULabel("", UStackStatics.FONT_NORMAL_BOLD));

		BasicDBList tosList = roleDef.getTOSList();
		for (int i = 0; i < tosList.size(); i++)
		{
			String tos = (String)tosList.get(i);
			
			Button rem = new UButton(Msg.getString("Remove"), UStackStatics.WEB_BUTTON, this, "deleteTosFromRole");
			rem.set("name", tos);
			
			tosGrid.add(new Label(tos));
			tosGrid.add(rem);
		}

		tosName = new USelectField(Msg.getString("TOS Name"), TermsConditions.getAll());
		tosName.addActionListener(this);
		tosName.setActionCommand("addTosToRole");
		
		tosGrid.add(tosName);
		tosGrid.add(new UButton(Msg.getString("Save"), UStackStatics.WEB_BUTTON, this, "addTosToRole"));
		
		/*
		 * Other Stuff
		 */
		
		UGrid rg = new UGrid(2, UStackStatics.IN_5);
		left.add(rg);
		
		List<UntzDBObjectTemplate> templateList = UntzDBObjectTemplate.getAll();
		
		rg.add(Msg.getString("RoleOrder"));
		rg.add(roleOrder = new UTextField("Role Order"));
		rg.add(Msg.getString("LinkActionClass"));
		rg.add(roleLinkActionClass = new UTextField("Link Action Class"));
		rg.add(Msg.getString("UserTemplate"));
		rg.add(userTemplate = new USelectField("User Template", templateList));
		rg.add("");
		rg.add(new UButton(Msg.getString("Save"), UStackStatics.WEB_BUTTON, this, "saveRoleOrder"));
		
		roleOrder.setText( roleDef.getRoleOrder() + "" );
		roleOrder.setWidth(UStackStatics.EX_25);
		roleLinkActionClass.setText( roleDef.getLinkActionClass() );
		roleLinkActionClass.setWidth(UStackStatics.EX_200);
		
		String objTempl = roleDef.getObjectTemplate();
		if (objTempl != null)
		{
			for (int i = 0; i < templateList.size(); i++)
			{
				if (objTempl.equalsIgnoreCase(templateList.get(i).getTemplateName()))
					userTemplate.setSelectedIndex(i + 1);
			}
		}
		
		/*
		 * Right Side (Plugins)
		 */
		Grid pluginGrid = new Grid(3);
		pluginGrid.setInsets(UStackStatics.IN_5);
		pluginGrid.setWidth(UStackStatics.EX_395);
		pluginGrid.setColumnWidth(2, UStackStatics.EX_25);
		pluginGrid.setLayoutData(UStackStatics.ROW_TOP);
		mainRow.add(pluginGrid);

		pluginGrid.add(new ULabel(Msg.getString("Plugins"), UStackStatics.FONT_BOLD_MED));
		pluginGrid.add(new ULabel(Msg.getString("Parent"), UStackStatics.FONT_BOLD_MED));
		pluginGrid.add(new ULabel("", UStackStatics.FONT_NORMAL_BOLD));

		BasicDBList pluginList = roleDef.getPluginList();
		for (int i = 0; i < pluginList.size(); i++)
		{
			DBObject plugin = (DBObject)pluginList.get(i);
			String name = (String)plugin.get("name");
			String parent = (String)plugin.get("parent");
			
			URow row = new URow();
			
			Button rem = new UButton(Msg.getString("Remove"), UStackStatics.WEB_BUTTON, this, "deletePlugin");
			rem.set("name", name);
			rem.set("parent", parent);
			row.add(rem);

			Button up = new UButton("up", UStackStatics.WEB_BUTTON, this, "moveUp");
			up.set("idx", i);
			Button down = new UButton("down", UStackStatics.WEB_BUTTON, this, "moveDown");
			down.set("idx", i);
			
			row.add(up);
			row.add(down);

			pluginGrid.add(new Label(name));
			pluginGrid.add(new Label(parent));
			pluginGrid.add(row);
		}
		
		pluginName = new UTextField(Msg.getString("Plugin"));
		pluginName.addActionListener(this);
		pluginName.setActionCommand("savePlugin");
		
		pluginParent = new UTextField(Msg.getString("Parent"));
		pluginParent.addActionListener(this);
		pluginParent.setActionCommand("savePlugin");
		
		pluginGrid.add(pluginName);
		pluginGrid.add(pluginParent);
		pluginGrid.add(new UButton(Msg.getString("Save"), UStackStatics.WEB_BUTTON, this, "savePlugin"));

		pluginGrid.add(new ULabel(Msg.getString("Resource"), UStackStatics.FONT_BOLD_MED));
		pluginGrid.add(new ULabel(Msg.getString("Role"), UStackStatics.FONT_BOLD_MED));
		pluginGrid.add(new ULabel("", UStackStatics.FONT_NORMAL_BOLD));

		/*
		 * Additional Resources/Roles
		 */
		BasicDBList addResRolesList = roleDef.getAdditionalResRolesList();
		for (int i = 0; i < addResRolesList.size(); i++)
		{
			DBObject resRole = (DBObject)addResRolesList.get(i);
			String resource = (String)resRole.get("resName");
			String role = (String)resRole.get("role");
			
			URow row = new URow();
			
			Button rem = new UButton(Msg.getString("Remove"), UStackStatics.WEB_BUTTON, this, "deleteAddtResRole");
			rem.set("resName", resource);
			rem.set("role", role);
			row.add(rem);

			pluginGrid.add(new Label(resource));
			pluginGrid.add(new Label(role));
			pluginGrid.add(row);
		}

		resName = new UTextField("Resource Name");
		resName.addActionListener(this);
		resName.setActionCommand("saveAddtResRole");
		
		role = new UTextField("Role");
		role.addActionListener(this);
		role.setActionCommand("saveAddtResRole");
		
		pluginGrid.add(resName);
		pluginGrid.add(role);
		pluginGrid.add(new UButton(Msg.getString("Save"), UStackStatics.WEB_BUTTON, this, "saveAddtResRole"));

		
		pluginGrid.add(new ULabel(Msg.getString("Event"), UStackStatics.FONT_BOLD_MED));
		pluginGrid.add(new ULabel(Msg.getString("Method"), UStackStatics.FONT_BOLD_MED));
		pluginGrid.add(new ULabel("", UStackStatics.FONT_NORMAL_BOLD));
		/*
		 * Auto-Subscriptions
		 */
		BasicDBList subList = roleDef.getAutoSubscriptionList();
		for (int i = 0; i < subList.size(); i++)
		{
			DBObject sub = (DBObject)subList.get(i);
			String event = (String)sub.get("event");
			String method = (String)sub.get("method");
			
			URow row = new URow();
			
			Button rem = new UButton(Msg.getString("Remove"), UStackStatics.WEB_BUTTON, this, "deleteAutoSub");
			rem.set("event", event);
			rem.set("method", method);
			row.add(rem);

			pluginGrid.add(new Label(event));
			pluginGrid.add(new Label(method));
			pluginGrid.add(row);
		}
		
		event = new UTextField("Event");
		event.addActionListener(this);
		event.setActionCommand("saveAutoSub");
		
		method = new UTextField("Role");
		method.addActionListener(this);
		method.setActionCommand("saveAutoSub");
		
		pluginGrid.add(event);
		pluginGrid.add(method);
		pluginGrid.add(new UButton(Msg.getString("Save"), UStackStatics.WEB_BUTTON, this, "saveAutoSub"));
		
		
		pluginGrid.add(new ULabel(Msg.getString("Variable"), UStackStatics.FONT_BOLD_MED));
		pluginGrid.add(new ULabel(Msg.getString("Value"), UStackStatics.FONT_BOLD_MED));
		pluginGrid.add(new ULabel("", UStackStatics.FONT_NORMAL_BOLD));
		
		/*
		 * Variables
		 */
		BasicDBList varList = roleDef.getVariableList();
		for (int i = 0; i < varList.size(); i++)
		{
			DBObject sub = (DBObject)varList.get(i);
			String var = (String)sub.get("var");
			DBObject valObj = (DBObject)sub.get("val");
			String val = valObj + "";
			
			URow row = new URow();
			
			Button rem = new UButton(Msg.getString("Remove"), UStackStatics.WEB_BUTTON, this, "deleteVariable");
			rem.set("var", var);
			row.add(rem);
			
			if (val != null && val.length() > 16)
				val = val.substring(0, 16) + "...";

			pluginGrid.add(new Label(var));
			pluginGrid.add(new Label(val));
			pluginGrid.add(row);
		}
		
		var = new UTextField("Variable Name");
		var.addActionListener(this);
		var.setActionCommand("saveVariable");
		
		val = new UTextArea("Value", UStackStatics.EX_100, UStackStatics.EX_30);
		val.addActionListener(this);
		val.setMaximumLength(-1);
		val.setActionCommand("saveVariable");
		
		pluginGrid.add(var);
		pluginGrid.add(val);
		pluginGrid.add(new UButton(Msg.getString("Save"), UStackStatics.WEB_BUTTON, this, "saveVariable"));
		
		pluginErrors = new UErrorColumn();
		pluginErrors.setLayoutData(UStackStatics.GRID_SPAN2);
		roleEditCol.add(pluginErrors);
	}

	public UTextField getVariableField() { return var; }
	public UTextArea getValueField() { return val; }
	public UTextField getEventField() { return event; }
	public UTextField getMethodField() { return method; }
	public UTextField getResNameField() { return resName; }
	public UTextField getRoleField() { return role; }
	public UTextField getResourceDefNameField() { return resourceDefName; }
	public USelectField getResourceDefTypeField() { return resourceDefType; }
	public UTextField getRoleNameField() { return roleName; }
	public UTextField getPermissionNameField() { return permissionName; }
	public UTextField getPluginNameField() { return pluginName; }
	public UTextField getPluginParentField() { return pluginParent; }
	public UTextField getLinkActionClassField() { return linkActionClass; }
	public UTextField getRoleOrderField() { return roleOrder; }
	public UTextField getRoleLinkActionClassField() { return roleLinkActionClass; }
	public UTextField getManagedByField() { return managedBy; }
	public UTextField getCanManageField() { return canManage; }
	public UTextField getSupportEmailField() { return supportEmail; }
	public UTextField getInternalName() { return internalName; }
	public USelectField getTosNameField() { return tosName; }
	public USelectField getUserTemplateField() { return userTemplate; }
}
