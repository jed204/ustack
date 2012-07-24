package com.untzuntz.ustack.data;

import java.util.List;

import nextapp.echo.app.event.ActionEvent;
import nextapp.echo.app.event.ActionListener;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.untzuntz.ustack.aaa.LinkActionInterface;
import com.untzuntz.ustack.aaa.ResourceDefinition;
import com.untzuntz.ustack.aaa.ResourceLink;
import com.untzuntz.ustack.main.Msg;
import com.untzuntz.ustack.uisupport.LinkUIComponent;
import com.untzuntz.ustack.uisupport.UButton;
import com.untzuntz.ustack.uisupport.UEntryError;
import com.untzuntz.ustack.uisupport.UGrid;
import com.untzuntz.ustack.uisupport.USelectField;
import com.untzuntz.ustack.uisupport.UStackStatics;

/**
 * Links a user to a site
 * 
 * @author jdanner
 *
 */
public class SiteProfileLink extends LinkUIComponent implements LinkActionInterface,ActionListener {

	private static final long serialVersionUID = 1L;
	private USelectField siteProfile;
	
	public LinkUIComponent getLinkSelectUI() {

		
		UGrid row = new UGrid(2, UStackStatics.IN_5);
		add(row);
		
		row.add("Site Profile:");
		row.add(siteProfile = new USelectField("Site Profile", ResourceDefinition.getAll(ResourceDefinition.TYPE_SITEPROFILE, null)));
		
		row.add(new UButton(Msg.getString("Save"), UStackStatics.WEB_BUTTON, this, "save"));
		
		return this;
	}

	public void actionPerformed(ActionEvent e) {
		
		if ("save".equalsIgnoreCase(e.getActionCommand()))
		{
			List<UEntryError> errors = UEntryError.getEmptyList();
			errors.addAll( siteProfile.validateMinSelectionIndex(1) );
			
			if (errors.size() > 0)
				return;
			
			runActions();
		}
		
	}

	public DBObject getResourceLinkExtras(ResourceLink link) {
		DBObject ret = new BasicDBObject();
		ResourceDefinition def = (ResourceDefinition)siteProfile.getSelectedObject();
		ret.put("targetResource", def.getName());
		ret.put("linkText", def.getName());
		return ret;
	}

	public void linkCreated(UntzDBObject user, ResourceLink link) {
	}

	public void linkRemoved(UntzDBObject user, ResourceLink link) {
	}

}
