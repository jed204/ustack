package com.untzuntz.ustack.main.setup;

import java.util.List;

import nextapp.echo.app.Button;
import nextapp.echo.app.Grid;

import com.mongodb.DBObject;
import com.untzuntz.ustack.data.NotificationTemplate;
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
import com.untzuntz.ustack.uisupport.UStackStatics;
import com.untzuntz.ustack.uisupport.UTextArea;
import com.untzuntz.ustack.uisupport.UTextField;
import com.untzuntz.ustack.uisupport.UViewColumn;

import echopoint.Strut;

public class NotificationTemplView extends UViewColumn {

	private static final long serialVersionUID = 1L;

	public NotificationTemplView(UControllerInt ctrl) { super(ctrl); }
	
	private UTextField eventName;
	private UTextField linkActionClass;
	private UTextField managedBy;
	private UErrorColumn errors;
	
	public void setError(List<UEntryError> errList) { errors.setErrorList(errList); }
	
	@Override
	public void setup() {

		removeAll();
		
		UCenterCol body = new UCenterCol();
		body.setInsets(UStackStatics.IN_15);
		body.setCellSpacing(UStackStatics.EX_5);
		add(body);

		body.add(new ULabel(Msg.getString("Notifications"), UStackStatics.FONT_BOLD_LARGE));
		body.add(new Strut(800, 15));

		Grid configItems = new Grid(2);
		configItems.setInsets(UStackStatics.IN_5);
		configItems.setWidth(UStackStatics.EX_800);
		configItems.setColumnWidth(1, UStackStatics.EX_125);
		body.add(configItems);
		
		configItems.add(new ULabel(Msg.getString("EventName"), UStackStatics.FONT_BOLD_MED));
		configItems.add(new ULabel("", UStackStatics.FONT_NORMAL_BOLD));
		
		List<NotificationTemplate> items = NotificationTemplate.getAll();
		for (NotificationTemplate item : items)
		{
			Button edit = new UButton(Msg.getString("Edit"), UStackStatics.WEB_BUTTON, this, "editNotifTempl");
			edit.set("name", item.getEventName());
			Button rem = new UButton(Msg.getString("Remove"), UStackStatics.WEB_BUTTON, this, "deleteNotifTempl");
			rem.set("name", item.getEventName());
			
			configItems.add(new ULabel(item.getEventName(), 100));
			
			URow row = new URow();
			row.add(edit);
			row.add(rem);
			configItems.add(row);
		}
		
		eventName = new UTextField(Msg.getString("Event Name"));
		
		configItems.add(eventName);
		configItems.add(new UButton(Msg.getString("Save"), UStackStatics.WEB_BUTTON, this, "createNotifTempl"));
		
		body.add(errors = new UErrorColumn());
	}

	public UTextField getEventNameField() {
		return eventName;
	}
	
	public void editTemplate(NotificationTemplate templ)
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

		header.add(new ULabel(templ.getEventName() + " " + Msg.getString("Template"), UStackStatics.FONT_BOLD_LARGE));
		header.add(new URow(new UButton(Msg.getString("Close"), UStackStatics.WEB_BUTTON, this, "cancelNotifTemplEdit"), UStackStatics.GRID_RIGHT));
		body.add(new Strut(800, 15));

		/*
		 * General
		 */
		body.add(new UColumn(new ULabel(Msg.getString("General"), UStackStatics.FONT_BOLD_LARGE), UStackStatics.BDR_SIMPLE, UStackStatics.LIGHT_GRAY));

		UGrid genopts = new UGrid(2, UStackStatics.IN_5);
		body.add(genopts);

		genopts.add(Msg.getString("LinkActionClass") + ":");
		genopts.add(linkActionClass = new UTextField(Msg.getString("LinkActionClass")));
		genopts.add(Msg.getString("ManagedBy") + ":");
		genopts.add(managedBy = new UTextField(Msg.getString("ManagedBy")));
		
		linkActionClass.setWidth(UStackStatics.EX_325);
		linkActionClass.setText(templ.getLinkActionClass());
		managedBy.setWidth(UStackStatics.EX_200);
		managedBy.setText(templ.getManagedByListString());
		
		/*
		 * Email
		 */
		body.add(new UColumn(new ULabel(Msg.getString("Email"), UStackStatics.FONT_BOLD_LARGE), UStackStatics.BDR_SIMPLE, UStackStatics.LIGHT_GRAY));
		body.add(new Strut(800, 5));

		UGrid emailopts = new UGrid(2, UStackStatics.IN_5);
		body.add(emailopts);
		
		emailopts.add(Msg.getString("fromAddress") + ":");
		emailopts.add(fromAddress = new UTextField(Msg.getString("From Address")));
		
		emailopts.add(Msg.getString("fromName") + ":");
		emailopts.add(fromName = new UTextField(Msg.getString("From Name")));

		emailopts.add(Msg.getString("subject") + ":");
		emailopts.add(subject = new UTextField(Msg.getString("Subject")));

		emailopts.add(new ULabel(Msg.getString("Template"), UStackStatics.GRID_TOP));
		emailopts.add(emailTemplText = new UTextArea("Text", UStackStatics.EX_550, UStackStatics.EX_230));
		emailopts.add(new ULabel("HTML Template", UStackStatics.GRID_TOP));
		emailopts.add(htmlEmailTemplText = new UTextArea("Text", UStackStatics.EX_550, UStackStatics.EX_230));

		emailopts.add("");
		emailopts.add(new UButton(Msg.getString("Save"), UStackStatics.WEB_BUTTON, this, "updateNotifTempl"));

		/*
		 * SMS
		 */
		body.add(new UColumn(new ULabel(Msg.getString("SMS"), UStackStatics.FONT_BOLD_LARGE), UStackStatics.BDR_SIMPLE, UStackStatics.LIGHT_GRAY));
		body.add(new Strut(800, 5));

		UGrid smsopts = new UGrid(2, UStackStatics.IN_5);
		body.add(smsopts);
		
		smsopts.add(new ULabel(Msg.getString("Template"), UStackStatics.GRID_TOP));
		smsopts.add(smsTemplText = new UTextArea("Text", UStackStatics.EX_550, UStackStatics.EX_125));

		smsopts.add("");
		smsopts.add(new UButton(Msg.getString("Save"), UStackStatics.WEB_BUTTON, this, "updateNotifTempl"));

		
		/*
		 * IOS
		 */
		body.add(new UColumn(new ULabel("iOS", UStackStatics.FONT_BOLD_LARGE), UStackStatics.BDR_SIMPLE, UStackStatics.LIGHT_GRAY));
		body.add(new Strut(800, 5));

		UGrid iosopts = new UGrid(2, UStackStatics.IN_5);
		body.add(iosopts);
		
		iosopts.add("iOS Push Queue Name:");
		iosopts.add(iosPushQueueName = new UTextField(Msg.getString("iOS Queue Name")));

		iosopts.add(new ULabel(Msg.getString("Template"), UStackStatics.GRID_TOP));
		iosopts.add(iosTemplText = new UTextArea("Text", UStackStatics.EX_550, UStackStatics.EX_125));

		iosopts.add("");
		iosopts.add(new UButton(Msg.getString("Save"), UStackStatics.WEB_BUTTON, this, "updateNotifTempl"));

		/*
		 * Facebook
		 */
		body.add(new UColumn(new ULabel("Facebook", UStackStatics.FONT_BOLD_LARGE), UStackStatics.BDR_SIMPLE, UStackStatics.LIGHT_GRAY));
		body.add(new Strut(800, 5));

		UGrid fbopts = new UGrid(2, UStackStatics.IN_5);
		body.add(fbopts);
		
		fbopts.add("Post To:");
		fbopts.add(fbPostTo = new UTextField(Msg.getString("Facebook Post To")));
		
		fbopts.add("Link Name:");
		fbopts.add(fbLinkName = new UTextField(Msg.getString("Facebook Link Name")));
		
		fbopts.add("Caption:");
		fbopts.add(fbCaption = new UTextField(Msg.getString("Facebook Caption")));
		
		fbopts.add("Description:");
		fbopts.add(fbDescription = new UTextField(Msg.getString("Facebook Description")));
		
		fbopts.add("Link URL:");
		fbopts.add(fbLink = new UTextField(Msg.getString("Facebook Link URL")));
		
		fbopts.add("Icon/Picture");
		fbopts.add(fbPicture = new UTextField(Msg.getString("Facebook Icon/Picture")));

		fbopts.add(new ULabel(Msg.getString("Template"), UStackStatics.GRID_TOP));
		fbopts.add(fbTemplText = new UTextArea("Text", UStackStatics.EX_550, UStackStatics.EX_125));

		fbopts.add("");
		fbopts.add(new UButton(Msg.getString("Save"), UStackStatics.WEB_BUTTON, this, "updateNotifTempl"));

		/*
		 * Load
		 */
		DBObject email = templ.getType("email");
		if (email != null)
		{
			fromAddress.setText((String)email.get("fromAddress"));
			fromName.setText((String)email.get("fromName"));
			subject.setText((String)email.get("subject"));
			emailTemplText.setText((String)email.get("templateText"));
			htmlEmailTemplText.setText((String)email.get("htmlTemplateText"));
		}
		
		DBObject sms = templ.getType("sms");
		if (sms != null)
			smsTemplText.setText((String)sms.get("templateText"));
		
		DBObject ios = templ.getType("ios-push");
		if (ios != null)
		{
			iosPushQueueName.setText((String)ios.get("iosPushQueueName"));
			iosTemplText.setText((String)ios.get("templateText"));
		}
		
		DBObject facebook = templ.getType("facebook");
		if (facebook != null)
		{
			fbPostTo.setText((String)facebook.get("postTo"));
			fbLinkName.setText((String)facebook.get("linkName"));
			fbCaption.setText((String)facebook.get("caption"));
			fbDescription.setText((String)facebook.get("description"));
			fbLink.setText((String)facebook.get("link"));
			fbPicture.setText((String)facebook.get("picture"));
			fbTemplText.setText((String)facebook.get("templateText"));
		}
	}

	private UTextField fromAddress;
	private UTextField fromName;
	private UTextField subject;
	private UTextArea emailTemplText;
	private UTextArea htmlEmailTemplText;
	private UTextArea smsTemplText;
	private UTextField iosPushQueueName;
	private UTextArea iosTemplText;
	private UTextField fbPostTo;
	private UTextField fbLinkName;
	private UTextField fbCaption;
	private UTextField fbDescription;
	private UTextField fbLink;
	private UTextField fbPicture;
	private UTextArea fbTemplText;

	public UTextField getFromAddrField() { return fromAddress; }
	public UTextField getFromNameField() { return fromName; }
	public UTextField getSubjectField() { return subject; }
	public UTextArea getEmailTemplField() { return emailTemplText; }
	public UTextArea getHTMLEmailTemplField() { return htmlEmailTemplText; }
	public UTextArea getSMSTemplField() { return smsTemplText; }
	public UTextField getLinkActionClassField() { return linkActionClass; }
	public UTextField getManagedByField() { return managedBy; }

	public UTextField getIOSPushQueueName() { return iosPushQueueName; }
	public UTextArea getIOSTemplField() { return iosTemplText; }
	public UTextField getFBPostToField() { return fbPostTo; }
	public UTextField getFBLinkNameField() { return fbLinkName; }
	public UTextField getFBCaptionField() { return fbCaption; }
	public UTextField getFBDescriptionField() { return fbDescription; }
	public UTextField getFBLinkField() { return fbLink; }
	public UTextField getFBPictureField() { return fbPicture; }
	public UTextArea getFBTemplField() { return fbTemplText; }

}
