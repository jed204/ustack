package com.untzuntz.ustack.main.setup;

import java.util.Iterator;
import java.util.List;

import nextapp.echo.app.Button;
import nextapp.echo.app.Color;
import nextapp.echo.app.Column;
import nextapp.echo.app.Grid;
import nextapp.echo.app.Row;
import nextapp.echo.filetransfer.app.UploadSelect;
import nextapp.echo.filetransfer.app.event.UploadListener;

import com.mongodb.BasicDBList;
import com.mongodb.DBObject;
import com.untzuntz.ustack.data.UntzDBObjectTemplate;
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
import com.untzuntz.ustack.uisupport.UTextField;
import com.untzuntz.ustack.uisupport.UViewColumn;

import echopoint.Strut;

public class ObjectTemplatesView extends UViewColumn {

	private static final long serialVersionUID = 1L;

	public ObjectTemplatesView(UControllerInt ctrl) { super(ctrl); }
	
	private UTextField templName;
	private UErrorColumn errors;
	
	public void setError(List<UEntryError> errList) { errors.setErrorList(errList); }
	
	@Override
	public void setup() {

		removeAll();
		
		UCenterCol body = new UCenterCol();
		body.setInsets(UStackStatics.IN_15);
		body.setCellSpacing(UStackStatics.EX_5);
		add(body);

		body.add(new ULabel(Msg.getString("ObjectTemplates"), UStackStatics.FONT_BOLD_LARGE));
		body.add(new Strut(800, 15));

		Grid configItems = new Grid(2);
		configItems.setInsets(UStackStatics.IN_5);
		configItems.setWidth(UStackStatics.EX_800);
		body.add(configItems);
		
		configItems.add(new ULabel(Msg.getString("Name"), UStackStatics.FONT_BOLD_MED));
		configItems.add(new ULabel("", UStackStatics.FONT_NORMAL_BOLD));
		
		List<UntzDBObjectTemplate> items = UntzDBObjectTemplate.getAll();
		for (UntzDBObjectTemplate item : items)
		{
			Button exp = new UButton(Msg.getString("Export"), UStackStatics.WEB_BUTTON, this, "exportObjTempl");
			exp.set("name", item.getTemplateName());
			Button edit = new UButton(Msg.getString("Edit"), UStackStatics.WEB_BUTTON, this, "editObjTempl");
			edit.set("name", item.getTemplateName());
			Button rem = new UButton(Msg.getString("Remove"), UStackStatics.WEB_BUTTON, this, "deleteObjTempl");
			rem.set("name", item.getTemplateName());
			
			configItems.add(new ULabel(item.getTemplateName(), 100));
			
			URow row = new URow();
			row.add(exp);
			row.add(edit);
			row.add(rem);
			configItems.add(row);
		}
		
		templName = new UTextField(Msg.getString("Name"));
		
		configItems.add(templName);
		configItems.add(new UButton(Msg.getString("Save"), UStackStatics.WEB_BUTTON, this, "createObjTemplate"));
		
		body.add(errors = new UErrorColumn());
		
		body.add(new UButton(Msg.getString("Import"), UStackStatics.WEB_BUTTON, this, "importObjTemplate"));

	}

	public void prepareObjTemplImport()
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

		body.add(new ULabel(Msg.getString("Import") + " " + Msg.getString("ObjectTemplates"), UStackStatics.FONT_BOLD_LARGE));
		body.add(new Strut(800, 15));

		body.add(new ULabel(Msg.getString("FileSelect"), UStackStatics.FONT_BOLD_MED));
		
		UploadSelect uploadSelect = new UploadSelect();
        uploadSelect.addUploadListener((UploadListener)getController());
        body.add(uploadSelect);
        
        body.add(new Strut(0, 20));
        
        body.add(new UButton(Msg.getString("Cancel"), UStackStatics.WEB_BUTTON, this, "cancelNotifTemplEdit"));
		
		body.add(errors = new UErrorColumn());
	}

	public void editObjTempl(UntzDBObjectTemplate templ)
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

		header.add(new ULabel(templ.getTemplateName(), UStackStatics.FONT_BOLD_LARGE));
		header.add(new URow(new UButton(Msg.getString("Close"), UStackStatics.WEB_BUTTON, this, "cancelObjTemplEdit"), UStackStatics.GRID_RIGHT));
		body.add(new Strut(800, 15));

		body.add(new UColumn(new ULabel(Msg.getString("BasicSettings"), UStackStatics.FONT_BOLD_LARGE), UStackStatics.BDR_SIMPLE, UStackStatics.LIGHT_GRAY));
		body.add(new Strut(800, 5));

		UGrid opts = new UGrid(2, UStackStatics.IN_5);
		body.add(opts);
		
		opts.add("");
		opts.add(new UButton(Msg.getString("Save"), UStackStatics.WEB_BUTTON, this, "updateObjTempl"));
		
		body.add(new UColumn(new ULabel(Msg.getString("TemplateItems"), UStackStatics.FONT_BOLD_LARGE), UStackStatics.BDR_SIMPLE, UStackStatics.LIGHT_GRAY));
		body.add(new Strut(800, 5));

		UGrid items = new UGrid(5, UStackStatics.IN_5);
		body.add(items);

		BasicDBList tObjectList = templ.getTemplateObjectList();
		for (int i = 0; i < tObjectList.size(); i++)
		{
			DBObject obj = (DBObject)tObjectList.get(i);
			
			String field = (String)obj.get("field");
			String op = (String)obj.get("op");
			Object val = obj.get("value");
			
			String type = "Unknown";
			String valStr = "(cannot view)";
			if (val instanceof String)
			{
				type = "String";
				valStr = (String)val;
			}
			else if (val instanceof BasicDBList)
			{
				type = "List";
				valStr = "TODO: Implement View of List";
			}
			else if (val instanceof Long)
			{
				type = "Long";
				valStr = "" + val;
			}
			else if (val instanceof Integer)
			{
				type = "Integer";
				valStr = "" + val;
			}

			items.add(field);
			items.add(op);
			items.add(type);
			items.add(valStr);
			
			Button btn = new UButton("Remove", UStackStatics.WEB_BUTTON, this, "removeObjTemplItem");
			btn.set("idx", i);
			
			Row cmds = new URow();
			cmds.add(btn);
			items.add(cmds);
			
		}

		Row otherActs = new URow();
		body.add(otherActs);
		
		otherActs.add(new UButton("Add String", UStackStatics.WEB_BUTTON, this, "addObjTemplItem", "String"));
		otherActs.add(new UButton("Add Integer", UStackStatics.WEB_BUTTON, this, "addObjTemplItem", "Integer"));
		otherActs.add(new UButton("Add Long", UStackStatics.WEB_BUTTON, this, "addObjTemplItem", "Long"));
		otherActs.add(new UButton("Grab from another object", UStackStatics.WEB_BUTTON, this, "importObjTemplItem"));
		
	}
	
	public void importObjTemplItem()
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

		header.add(new ULabel("Grab From Another Object", UStackStatics.FONT_BOLD_LARGE));
		header.add(new URow(new UButton(Msg.getString("Close"), UStackStatics.WEB_BUTTON, this, "editCurrentObjTempl"), UStackStatics.GRID_RIGHT));
		body.add(new Strut(800, 15));

		UGrid r = new UGrid(2, UStackStatics.IN_5);
		body.add(r);
		
		r.add("Object Type:");
		r.add(objType = new USelectField("Object Type", "User", "Site"));
		r.add("Object ID:");
		r.add(objectId = new UTextField("Object ID"));
		
		r.add("");
		r.add(new UButton("Load Object", UStackStatics.WEB_BUTTON, this, "loadObjTemplSource"));

		loadedObj = new UColumn();
		body.add(loadedObj);
	}
	
	public boolean isLegalField(String key)
	{
		if ("_id".equalsIgnoreCase(key) || "salt".equalsIgnoreCase(key) || "password".equalsIgnoreCase(key) || 
				"created".equalsIgnoreCase(key) || "updated".equalsIgnoreCase(key) || "updatedBy".equalsIgnoreCase(key) || 
				"userName".equalsIgnoreCase(key) || "siteName".equalsIgnoreCase(key))
			return false;

		return true;
	}
	
	
	public void loadObject(DBObject obj)
	{
		loadedObj.removeAll();
		if (obj == null)
		{
			loadedObj.add(new ULabel("Invalid/Uknown Object", UStackStatics.FONT_NORMAL_BOLD, Color.RED));
			return;
		}

		loadedObj.add(new UColumn(new ULabel(Msg.getString("BasicSettings"), UStackStatics.FONT_BOLD_LARGE), UStackStatics.BDR_SIMPLE, UStackStatics.LIGHT_GRAY));
		loadedObj.add(new Strut(800, 5));

		Iterator<String> keys = obj.keySet().iterator();
		
		UGrid items = new UGrid(4, UStackStatics.IN_5);
		loadedObj.add(items);
		
		while (keys.hasNext())
		{
			String key = keys.next();
				
			if (!isLegalField(key))
				continue;
			
			Object val = obj.get(key);
			String type = "Unknown";
			String valStr = "(cannot view)";
			if (val instanceof String)
			{
				type = "String";
				valStr = (String)val;
			}
			else if (val instanceof BasicDBList)
			{
				type = "List";
				valStr = "TODO: Implement View of List";
			}
			else if (val instanceof Long)
			{
				type = "Long";
				valStr = "" + val;
			}
			else if (val instanceof Integer)
			{
				type = "Integer";
				valStr = "" + val;
			}

			items.add(key);
			items.add(type);
			items.add(valStr);
			
			Button btn = new UButton("Import", UStackStatics.WEB_BUTTON, this, "importObjTemplItemNow");
			btn.set("name", key);
			items.add(btn);
			
		}
		
	}
	
	private Column loadedObj;	
	private UTextField objectId;
	private USelectField objType;
	private UTextField field;
	private UTextField value;
	public void addObjTemplItem(String type)
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

		header.add(new ULabel("Add a " + type, UStackStatics.FONT_BOLD_LARGE));
		header.add(new URow(new UButton(Msg.getString("Close"), UStackStatics.WEB_BUTTON, this, "editCurrentObjTempl"), UStackStatics.GRID_RIGHT));
		body.add(new Strut(800, 15));

		UGrid r = new UGrid(2, UStackStatics.IN_5);
		body.add(r);
		
		r.add("Field Name:");
		r.add(field = new UTextField("Field"));
		r.add("Value:");
		r.add(value = new UTextField("Value"));
		r.add("");
		r.add(new UButton("Save", UStackStatics.WEB_BUTTON, this, "saveObjTemplItem"));
		
	}

	public UTextField getTemplNameField() {
		return templName;
	}

	public UTextField getField() {
		return field;
	}
	
	public UTextField getValue() {
		return value;
	}

	public USelectField getObjectType() {
		return objType;
	}
	
	public UTextField getObjectId() {
		return objectId;
	}
	


}
