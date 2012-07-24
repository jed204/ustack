package com.untzuntz.ustack.testcases;

import nextapp.echo.app.Label;
import nextapp.echo.app.event.ActionEvent;
import nextapp.echo.app.event.ActionListener;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.untzuntz.ustack.aaa.LinkActionInterface;
import com.untzuntz.ustack.aaa.ResourceLink;
import com.untzuntz.ustack.data.UntzDBObject;
import com.untzuntz.ustack.main.Msg;
import com.untzuntz.ustack.uisupport.LinkUIComponent;
import com.untzuntz.ustack.uisupport.UButton;
import com.untzuntz.ustack.uisupport.URow;
import com.untzuntz.ustack.uisupport.UStackStatics;
import com.untzuntz.ustack.uisupport.UTextField;

public class LinkActionSample extends LinkUIComponent implements LinkActionInterface,ActionListener {

	private static final long serialVersionUID = 1L;
	private UTextField sample;
	
	public LinkUIComponent getLinkSelectUI() {
		
		URow row = new URow();
		
		row.add(new Label(Msg.getString("SampleValue") + ":"));
		row.add(sample = new UTextField(Msg.getString("None")));
		row.add(new UButton(Msg.getString("Save"), UStackStatics.WEB_BUTTON, this, "saveLink"));
		
		add(row);
		
		return this;
	}

	public void actionPerformed(ActionEvent e) {
		
		if ("saveLink".equalsIgnoreCase(e.getActionCommand()))
			runActions();
		
	}

	public DBObject getResourceLinkExtras(ResourceLink link) {
		DBObject ret = new BasicDBObject();
		ret.put("linkText", sample.getText());
		return ret;
	}

	public void linkCreated(UntzDBObject user, ResourceLink link) {
	}

	public void linkRemoved(UntzDBObject user, ResourceLink link) {
	}
	
}
