package com.untzuntz.ustack.main.setup;

import java.util.Date;
import java.util.List;

import javax.mail.internet.AddressException;

import nextapp.echo.app.Button;
import nextapp.echo.app.Color;
import nextapp.echo.app.Component;
import nextapp.echo.app.ContentPane;
import nextapp.echo.app.Extent;
import nextapp.echo.app.SplitPane;
import nextapp.echo.app.event.ActionListener;
import nextapp.echo.extras.app.TabPane;
import nextapp.echo.filetransfer.app.event.UploadEvent;
import nextapp.echo.filetransfer.app.event.UploadListener;
import nextapp.echo.filetransfer.model.Upload;

import org.apache.log4j.Logger;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import com.untzuntz.ustack.aaa.Authentication;
import com.untzuntz.ustack.aaa.ResourceDefinition;
import com.untzuntz.ustack.aaa.ResourceLink;
import com.untzuntz.ustack.aaa.RoleDefinition;
import com.untzuntz.ustack.data.NotificationInst;
import com.untzuntz.ustack.data.NotificationTemplate;
import com.untzuntz.ustack.data.SiteAccount;
import com.untzuntz.ustack.data.TermsConditions;
import com.untzuntz.ustack.data.UDBConfigItem;
import com.untzuntz.ustack.data.UDataMgr;
import com.untzuntz.ustack.data.UntzDBObjectTemplate;
import com.untzuntz.ustack.data.UserAccount;
import com.untzuntz.ustack.exceptions.InvalidUserAccountName;
import com.untzuntz.ustack.exceptions.ObjectExistsException;
import com.untzuntz.ustack.exceptions.PasswordMismatchException;
import com.untzuntz.ustack.exceptions.RequiredAccountDataMissingException;
import com.untzuntz.ustack.main.ApplicationInstance;
import com.untzuntz.ustack.main.Msg;
import com.untzuntz.ustack.main.UController;
import com.untzuntz.ustack.main.UControllerInt;
import com.untzuntz.ustack.main.UFile;
import com.untzuntz.ustack.main.UPluginInt;
import com.untzuntz.ustack.uisupport.UButton;
import com.untzuntz.ustack.uisupport.UDownloadCmd;
import com.untzuntz.ustack.uisupport.UEntryError;
import com.untzuntz.ustack.uisupport.UStackStatics;
import com.untzuntz.ustack.uisupport.UTabPane;
import com.untzuntz.ustack.uisupport.UTextArea;
import com.untzuntz.ustack.uisupport.UTextField;
import com.untzuntz.ustack.uisupport.widgets.LoginSettingsPane;
import com.untzuntz.ustack.uisupport.widgets.UNotificationSelect;
import com.untzuntz.ustack.uisupport.widgets.UResourceRoleSelect;
import com.untzuntz.ustack.uisupport.widgets.UserDetailsEntry;

public class SetupAppController extends UController implements UControllerInt,ActionListener,UploadListener {

	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(SetupAppController.class);
	private ContentPane mainPane;
	private ConfigItemsView hv;
	private UserAccountView uav;
	private SiteAccountView sav;
	private ResourceDefinitionsView rdv;
	private TermsConditionsView tos;
	private NotificationTemplView templ;
	private ObjectTemplatesView objTempl;
	private ResourceDefinition selectedResourceDef;
	private RoleDefinition selectedRoleDef;
	private UserAccount selectedUserAccount;
	private SiteAccount selectedSiteAccount;
	private TermsConditions selectedTos;
	private NotificationTemplate selectedTempl;
	private UntzDBObjectTemplate selectedObjTempl;
	
	/** default view */
	public Component getDefaultView()
	{
		SplitPane split = new SplitPane(SplitPane.ORIENTATION_VERTICAL_BOTTOM_TOP, UStackStatics.EX_30);
		split.add(new LoginSettingsPane(this, ApplicationInstance.getAppName() + " " + Msg.getString("ApplicationSetup") + " - " + ApplicationInstance.getHostName()));
		split.add(mainPane = new ContentPane());
		
		buildUI();
		
		return split;
	}
	
	public void loadView(Component comp)
	{
		mainPane.setBackground(Color.WHITE);
		mainPane.removeAll();
		mainPane.add(comp);
	}

	/** builds the supporting UI for this controller */
	private void buildUI()
	{
		mainPane.removeAll();
		
		if ("admin".equalsIgnoreCase(getUser().getUserName()))
			mainPane.add(new CreateAccountView(this));
		else if (getUser().isPasswordExpired())
			mainPane.add(new ResetPasswordView(this));
		else
		{
			mainPane.setBackground(UStackStatics.DARK_BLUE);
			
			/*
			 * Tab Pane
			 */
			UTabPane tabPane = new UTabPane();
	        tabPane.setTabCloseEnabled(true);
	        tabPane.setBorderType(TabPane.BORDER_TYPE_ADJACENT_TO_TABS);
	        tabPane.setImageBorder(UStackStatics.FIB1_SURROUND);
	        tabPane.setTabSpacing(new Extent(-22));
	        tabPane.setTabRolloverBackground(Color.LIGHTGRAY);
	        tabPane.setTabRolloverEnabled(true);
	        tabPane.setTabActiveBackground(Color.WHITE);
	        tabPane.setTabActiveBackgroundInsets(UStackStatics.FIB1_TOP_INSETS);
	        tabPane.setTabActiveImageBorder(UStackStatics.FIB1_TOP);
	        tabPane.setTabActiveInsets(UStackStatics.IN_VWIDE);
	        tabPane.setTabInactiveBackground(Color.DARKGRAY);
	        tabPane.setTabInactiveBackgroundInsets(UStackStatics.FIB1_TOP_INSETS);
	        tabPane.setTabInactiveForeground(Color.WHITE);
	        tabPane.setTabInactiveInsets(UStackStatics.IN_VWIDE);
	        tabPane.setTabInactiveImageBorder(UStackStatics.FIB1_TOP);
	        mainPane.add(tabPane);
	        
	        tabPane.createTab(Msg.getString("ConfigItems")).add(hv = new ConfigItemsView(this));
	        tabPane.createTab(Msg.getString("ResourceDefinitions")).add(rdv = new ResourceDefinitionsView(this));
	        tabPane.createTab(Msg.getString("UserAccounts")).add(uav = new UserAccountView(this));
	        tabPane.createTab(Msg.getString("SiteAccounts")).add(sav = new SiteAccountView(this));
	        tabPane.createTab(Msg.getString("TermsOfService")).add(tos = new TermsConditionsView(this));
	        tabPane.createTab(Msg.getString("Notifications")).add(templ = new NotificationTemplView(this));
	        tabPane.createTab(Msg.getString("ObjectTemplates")).add(objTempl = new ObjectTemplatesView(this));
		}
	}

	/** Create and save a new ResourceDefinition */
	public void saveResourceDef()
	{
		List<UEntryError> errors = UEntryError.getEmptyList();
		errors.addAll( rdv.getResourceDefNameField().validateStringLen(1, 255) );
		errors.addAll( rdv.getResourceDefTypeField().validateMinSelectionIndex(1) );
		
		if (errors.size() > 0)
		{
			rdv.setError(errors);
			return;
		}
		
		try {
			
			ResourceDefinition rd = ResourceDefinition.createResource(rdv.getResourceDefNameField().getText(), rdv.getResourceDefTypeField().getString());
			rd.save(getUser().getUserName());
			rdv.setup();
			
		} catch (ObjectExistsException e) {
			rdv.setError(UEntryError.getTopLevelError(Msg.getString("Object-Exists")));
		}
		
	}
	
	/** Copies an existing ResourceDefinition to a new one */
	public void duplicateResourceDefinition(Component comp)
	{
		List<UEntryError> errors = UEntryError.getEmptyList();
		errors.addAll( rdv.getResourceDefNameField().validateStringLen(1, 255) );
		errors.addAll( rdv.getResourceDefTypeField().validateMinSelectionIndex(1) );
		
		if (errors.size() > 0)
		{
			rdv.setError(errors);
			return;
		}

		Button btn = (Button)comp;
		ResourceDefinition item = ResourceDefinition.getByName((String)btn.get("name"));

		try {
			
			ResourceDefinition rd = ResourceDefinition.createResource(rdv.getResourceDefNameField().getText(), rdv.getResourceDefTypeField().getString());
			rd.copyFrom(item);
			rd.save(getUser().getUserName());
			rdv.setup();
			
		} catch (ObjectExistsException e) {
			rdv.setError(UEntryError.getTopLevelError(Msg.getString("Object-Exists")));
		}
	}

	/** Exports (via download to user) the selected resource */
	public void exportResourceDefinition(Component comp)
	{
		Button btn = (Button)comp;

		ResourceDefinition item = ResourceDefinition.getByName((String)btn.get("name"));
		if (item != null)
		{
			UFile tempFile = UDataMgr.writeDBObjectToFile(item);
			if (tempFile != null)
				UDownloadCmd.sendFileToUser(tempFile, "application/binary", item.getName() + "-UStackResource.usr", true);
		}		
	}

	private String importSource;
	/** Update UI for file selection */
	public void importResourceDef()
	{
		importSource = "resdef";
		rdv.prepareResourceImport();
	}

	/** Handles upload of resource definition */
	public void uploadComplete(UploadEvent e) {

		// NOTE: Only one file upload is being used on this controller, so we aren't checking the source -- could be a problem in the future
		
        Upload upload = e.getUpload();
        if (upload.getStatus() != Upload.STATUS_COMPLETE)
        {
        	if ("resdef".equalsIgnoreCase(importSource))
        		rdv.setError(UEntryError.getListInstance("Upload", "Failed to import, status = " + upload.getStatus()));
        	else if ("objtempl".equalsIgnoreCase(importSource))
        		objTempl.setError(UEntryError.getListInstance("Upload", "Failed to import, status = " + upload.getStatus()));
        	return;
        }

        try {
        	UFile tmpFile = UFile.getTempFile(e.getUpload().getInputStream());
        	logger.info("Upload Complete: " + e.getUpload() + " stored in '" + tmpFile + "'");
        	
        	DBObject res = UDataMgr.readDBObjectFromFile(tmpFile);
    		res.removeField("_id"); // remove these since we're probably crossing databases
    		res.removeField("created"); // remove these since we're probably crossing databases

    		if ("resdef".equalsIgnoreCase(importSource))
    		{
	        	ResourceDefinition def = ResourceDefinition.getByName((String)res.get("name"));
	        	if (def == null) // not in our system right now...
	        		def = ResourceDefinition.createResource((String)res.get("name"), (String)res.get("type")); // create it
	        	
	    		def.putAll(res);
	        	def.save(getUser().getUserName());
	        	
	        	rdv.setup();
    		}
    		else if ("objtempl".equalsIgnoreCase(importSource))
    		{
    			UntzDBObjectTemplate t = UntzDBObjectTemplate.getTemplate((String)res.get("templateName"));
	        	if (t == null) // not in our system right now...
	        		t = UntzDBObjectTemplate.createTemplate(objTempl.getTemplNameField().getText());
	        	
    			t.putAll(res);
    			t.save(getUser().getUserName());
    			
    			objTempl.setup();
    		}
    		
        } catch (Exception err) {
        	logger.error("Failed to process upload", err);
        	String msg = err.getMessage();
        	if (msg == null) msg = "General Error";
        	rdv.setError(UEntryError.getListInstance("Upload", Msg.getString("FileProcessingError", msg, new Date())));
        }
	}

	/** Edit a ResourceDefinition object */
	public void editResourceDefinition(Component comp)
	{
		Button btn = (Button)comp;

		ResourceDefinition item = ResourceDefinition.getByName((String)btn.get("name"));
		if (item != null)
		{
			selectedResourceDef = item;
			rdv.editResourceDef(item);
		}
	}

	/** Update a resource definition */
	public void updateResourceDef()
	{
		if (rdv.getLinkActionClassField().getText().length() == 0)
			selectedResourceDef.setLinkActionClass(null);
		else
			selectedResourceDef.setLinkActionClass(rdv.getLinkActionClassField().getText());

		selectedResourceDef.clearManagedBy();
		String[] managedByArray = rdv.getManagedByField().getText().split(",");
		for (String managedBy : managedByArray)
			selectedResourceDef.addManagedBy(managedBy);
		
		selectedResourceDef.clearCanManage();
		String[] canManageArray = rdv.getCanManageField().getText().split(",");
		for (String canManage : canManageArray)
			selectedResourceDef.addCanManage(canManage);

		selectedResourceDef.setSupportEmail(rdv.getSupportEmailField().getText());
		selectedResourceDef.setInternalName(rdv.getInternalName().getText());
		selectedResourceDef.save(getUser().getUserName());
	}
	
	/** Delete a ResourceDefinition object */
	public void deleteResourceDefinintion(Component comp)
	{
		Button btn = (Button)comp;

		ResourceDefinition item = ResourceDefinition.getByName((String)btn.get("name"));
		if (item != null)
		{
			item.delete();
			rdv.setup();
		}
	}

	String selectedType;
	/** Directs UI to add a new item for an Object Template */
	public void addObjTemplItem(Component c)
	{
		UButton btn = (UButton)c;
		selectedType = btn.getIPC();
		
		objTempl.addObjTemplItem(selectedType);
	}
	
	public void saveObjTemplItem()
	{
		if (!objTempl.isLegalField(objTempl.getField().getText().trim()))
		{
			objTempl.getField().error();
			return;
		}
		
		BasicDBList list = selectedObjTempl.getTemplateObjectList();
		
		DBObject item = new BasicDBObject();
		list.add(item);
		
		item.put("op", "add/replace");
		item.put("field", objTempl.getField().getText().trim());
		if ("String".equalsIgnoreCase(selectedType))
			item.put("value", objTempl.getValue().getText());
		else if ("Long".equalsIgnoreCase(selectedType))
		{
			if (objTempl.getValue().validateLongRange(-20000000, 2000000).size() == 0)
				item.put("value", Long.valueOf(objTempl.getValue().getText()));
			else
			{
				objTempl.getValue().error();
				return;
			}
		}
		else if ("Integer".equalsIgnoreCase(selectedType))
		{
			if (objTempl.getValue().validateIntRange(-20000000, 2000000).size() == 0)
				item.put("value", Integer.valueOf(objTempl.getValue().getText()));
			else
			{
				objTempl.getValue().error();
				return;
			}
		}
		else
			logger.warn("Unknown type: " + selectedType);

		selectedObjTempl.setTemplateObjectList(list);
		selectedObjTempl.save(getUser().getUserName());
		
		objTempl.editObjTempl(selectedObjTempl);
	}
	
	public void importObjTemplItem()
	{
		objTempl.importObjTemplItem();
	}

	private DBObject selectedObjTemplObj;
	public void loadObjTemplSource()
	{
		List<UEntryError> errors = UEntryError.getEmptyList();
		errors.addAll( objTempl.getObjectId().validateStringLen(1, 128) );
		errors.addAll( objTempl.getObjectType().validateMinSelectionIndex(1) );

		if (errors.size() > 0)
			return;
		
		String type = (String)objTempl.getObjectType().getSelectedObject();
		if ("User".equalsIgnoreCase(type))
		{
			UserAccount user =  UserAccount.getUserById(objTempl.getObjectId().getText());
			objTempl.loadObject(user);
			selectedObjTemplObj = user;
		}
		else if ("Site".equalsIgnoreCase(type))
		{
			SiteAccount site = SiteAccount.getSiteById(objTempl.getObjectId().getText());
			objTempl.loadObject(site);
			selectedObjTemplObj = site;
		}
	}
	
	public void importObjTemplItemNow(Component c)
	{
		Button b = (Button)c;
		String fieldName = (String)b.get("name");
		
		BasicDBList list = selectedObjTempl.getTemplateObjectList();
		
		DBObject item = new BasicDBObject();
		item.put("op", "add/replace");
		item.put("field", fieldName);
		item.put("value", selectedObjTemplObj.get(fieldName));
		list.add(item);
		
		selectedObjTempl.setTemplateObjectList(list);
		selectedObjTempl.save(getUser().getUserName());
		
		b.setText("IMPORTED!");
	}

	/** Copies an existing role to a new one */
	public void duplicateRole(Component comp)
	{
		List<UEntryError> errors = UEntryError.getEmptyList();
		errors.addAll( rdv.getRoleNameField().validateStringLen(1, 255) );
		
		if (errors.size() > 0)
		{
			rdv.setError(errors);
			return;
		}

		Button btn = (Button)comp;
		String roleName = (String)btn.get("name"); // source role

		try {

			selectedResourceDef.addRole(new RoleDefinition(selectedResourceDef.getRoleByName(roleName), rdv.getRoleNameField().getText()));
			selectedResourceDef.save(getUser().getUserName());
			rdv.editResourceDef(selectedResourceDef);
			
		} catch (ObjectExistsException e) {
			rdv.setError(UEntryError.getTopLevelError(Msg.getString("Object-Exists")));
		}
	}

	/** Save a new RoleDefinition to the selected ResourceDefinition */
	public void saveRole(Component comp)
	{
		List<UEntryError> errors = UEntryError.getEmptyList();
		errors.addAll( rdv.getRoleNameField().validateStringLen(1, 255) );
		
		if (errors.size() > 0)
		{
			rdv.setError(errors);
			return;
		}

		try {
			
			selectedResourceDef.addRole(new RoleDefinition(rdv.getRoleNameField().getText()));
			selectedResourceDef.save(getUser().getUserName());
			rdv.editResourceDef(selectedResourceDef);
			
		} catch (ObjectExistsException e) {
			rdv.setError(UEntryError.getTopLevelError(Msg.getString("Object-Exists")));
		}
	}

	/** Edit a TOS Object */
	public void editTos(Component comp)
	{
		Button btn = (Button)comp;
		String tosName = (String)btn.get("name");
		
		selectedTos = TermsConditions.getTOS(tosName);
		if (selectedTos != null)
			tos.editTos(selectedTos);
	}
	
	/** Updated a Object Template Object */
	public void updateObjTempl()
	{
		selectedObjTempl.save(getUser().getUserName());
	}

	/** Update a TOS Object */
	public void updateTos()
	{
		selectedTos.setRenewalDays(-1);
		try { selectedTos.setRenewalDays( Integer.valueOf(tos.getRenewalDaysField().getText()) ); } catch (Exception er) {} 
		selectedTos.setText( tos.getTosTextField().getText() );
		selectedTos.setDisplayName( tos.getDisplayNameField().getText() );
		
		selectedTos.save(getUser().getUserName());
	}

	/** Create Notification Event */
	public void createNotifTempl()
	{
		List<UEntryError> errors = UEntryError.getEmptyList();
		errors.addAll( templ.getEventNameField().validateStringLen(1, 255) );
		
		if (errors.size() > 0)
		{
			templ.setError(errors);
			return;
		}
		
		try {
			
			NotificationTemplate t = NotificationTemplate.createTemplate(templ.getEventNameField().getText());
			t.save(getUser().getUserName());
			templ.setup();
			
		} catch (ObjectExistsException e) {
			templ.setError(UEntryError.getTopLevelError(Msg.getString("Object-Exists")));
		}
	}

	/** Cancel Editing */
	public void cancelNotifTemplEdit()
	{
		templ.setup();
	}

	/** Remove a notification template */
	public void deleteNotifTempl(Component comp)
	{
		Button btn = (Button)comp;
		String name = (String)btn.get("name");
		
		selectedTempl = NotificationTemplate.getNotificationTemplate(name);
		if (selectedTempl != null)
		{
			selectedTempl.delete();
			templ.setup();
		}
		
	}
	
	/** Edit a Notification Object */
	public void editNotifTempl(Component comp)
	{
		Button btn = (Button)comp;
		String name = (String)btn.get("name");
		
		selectedTempl = NotificationTemplate.getNotificationTemplate(name);
		if (selectedTempl != null)
			templ.editTemplate(selectedTempl);
	}
	
	public void editCurrentObjTempl()
	{
		objTempl.editObjTempl(selectedObjTempl);
	}
	
	
	public void exportObjTempl(Component comp)
	{
		Button btn = (Button)comp;
		String name = (String)btn.get("name");
		
		UntzDBObjectTemplate item = UntzDBObjectTemplate.getTemplate(name);
		if (item != null)
		{
			UFile tempFile = UDataMgr.writeDBObjectToFile(item);
			if (tempFile != null)
				UDownloadCmd.sendFileToUser(tempFile, "application/binary", item.getTemplateName() + "-UStackObjTemplate.tmpl", true);
		}
	}
	
	public void importObjTemplate()
	{
		importSource = "objtempl";
		objTempl.prepareObjTemplImport();
	}
	
	public void deleteObjTempl(Component comp)
	{
		Button btn = (Button)comp;
		String name = (String)btn.get("name");
		
		selectedObjTempl = UntzDBObjectTemplate.getTemplate(name);
		if (selectedObjTempl != null)
			selectedObjTempl.delete();
		
		objTempl.setup();
	}
	
	/** Edit a Notification Object */
	public void editObjTempl(Component comp)
	{
		Button btn = (Button)comp;
		String name = (String)btn.get("name");
		
		selectedObjTempl = UntzDBObjectTemplate.getTemplate(name);
		if (selectedObjTempl != null)
			objTempl.editObjTempl(selectedObjTempl);
	}
	
	public void addNotifSubscription()
	{
		
		List<UEntryError> errors = UEntryError.getEmptyList();
		errors.addAll( uav.getNotifTemplField().validateStringLen(1, 255) );
		
		if (uav.getEmailAddrField().getText().length() == 0 && uav.getSMSAddrField().getText().length() == 0)
			errors.add( UEntryError.getInstance("Destination", "Please provide an email address or sms number") );
		
		NotificationTemplate notifTempl = NotificationTemplate.getNotificationTemplate(uav.getNotifTemplField().getText());
		if (notifTempl == null)
			errors.add( UEntryError.getInstance("Event Name", "Not Found") );
		
		if (errors.size() > 0)
		{
			uav.setErrorList(errors);
			return;
		}
		
		NotificationInst ni1 = NotificationInst.subscribe(notifTempl, notifTempl.getEventName(), selectedUserAccount.getUserName());
		ni1.put("partner", uav.getPartnerField().getText());
		if (uav.getSMSAddrField().getText().length() > 0)
			ni1.addType("sms", uav.getSMSAddrField().getText()); 
		if (uav.getEmailAddrField().getText().length() > 0)
			ni1.addType("email", uav.getEmailAddrField().getText());
		ni1.save(getUser().getUserName());
		uav.loadUserAccount(this.selectedUserAccount);
	}
	
	/** Remove a user's NotificationInst */
	public void removeNotifSubscription(Component comp)
	{
		Button btn = (Button)comp;
		String id = (String)btn.get("id");
		
		NotificationInst ni = NotificationInst.getById(id);
		if (ni != null)
		{
			ni.delete();
			uav.loadUserAccount(this.selectedUserAccount);
		}
	}

	/** Update a Notification Object */
	public void updateNotifTempl()
	{
		if (templ.getEmailTemplField().getText().length() > 0)
		{
			List<UEntryError> errors = UEntryError.getEmptyList();
			errors.addAll( templ.getFromNameField().validateStringLen(1, 255) );
			errors.addAll( templ.getFromAddrField().validateStringLen(1, 255) );
			errors.addAll( templ.getSubjectField().validateStringLen(1, 255) );
			
			if (errors.size() > 0)
			{
				templ.setError(errors);
				return;
			}

			DBObject emailType = BasicDBObjectBuilder.start("templateText", templ.getEmailTemplField().getText()).get();
			emailType.put("fromName", templ.getFromNameField().getText());
			emailType.put("fromAddress", templ.getFromAddrField().getText());
			emailType.put("subject", templ.getSubjectField().getText());
			if (templ.getHTMLEmailTemplField().getText().length() > 0)
				emailType.put("htmlTemplateText", templ.getHTMLEmailTemplField().getText());
			else
				emailType.removeField("htmlTemplateText");
				
			
			selectedTempl.addType("email", emailType);
		}
		else
			selectedTempl.removeType("email");


		if (templ.getSMSTemplField().getText().length() > 0)
			selectedTempl.addType("sms", BasicDBObjectBuilder.start("templateText", templ.getSMSTemplField().getText()).get());
		else
			selectedTempl.removeType("sms");

		if (templ.getIOSTemplField().getText().length() > 0)
		{
			List<UEntryError> errors = UEntryError.getEmptyList();
			errors.addAll( templ.getIOSPushQueueName().validateStringLen(1, 255) );
			
			templ.setError(errors);
			if (errors.size() > 0)
				return;

			DBObject push = BasicDBObjectBuilder.start("templateText", templ.getIOSTemplField().getText()).get();
			push.put("iosPushQueueName", templ.getIOSPushQueueName().getText());
			selectedTempl.addType("ios-push", push);
		}
		else
			selectedTempl.removeType("ios-push");

		if (templ.getFBTemplField().getText().length() > 0)
		{
			List<UEntryError> errors = UEntryError.getEmptyList();
			errors.addAll( templ.getFBLinkNameField().validateStringLen(1, 255) );
			
			templ.setError(errors);
			if (errors.size() > 0)
				return;

			DBObject push = BasicDBObjectBuilder.start("templateText", templ.getFBTemplField().getText()).get();
			if (templ.getFBPostToField().getText().length() > 0)
				push.put("postTo", templ.getFBPostToField().getText());
			else
				push.removeField("postTo");
			
			push.put("linkName", templ.getFBLinkNameField().getText());
			push.put("caption", templ.getFBCaptionField().getText());
			push.put("description", templ.getFBDescriptionField().getText());
			push.put("link", templ.getFBLinkField().getText());
			push.put("picture", templ.getFBPictureField().getText());
			selectedTempl.addType("facebook", push);
		}
		else
			selectedTempl.removeType("facebook");
		
		if (templ.getLinkActionClassField().getText().length() == 0)
			selectedTempl.setLinkActionClass(null);
		else
			selectedTempl.setLinkActionClass(templ.getLinkActionClassField().getText());

		selectedTempl.clearManagedBy();
		String[] managedByArray = templ.getManagedByField().getText().split(",");
		for (String managedBy : managedByArray)
			selectedTempl.addManagedBy(managedBy);

		selectedTempl.save(getUser().getUserName());
	}
	
	public void removeObjTemplItem(Component src)
	{
		Button b = (Button)src;
		int idx = (Integer)b.get("idx");
		
		BasicDBList list = selectedObjTempl.getTemplateObjectList();
		list.remove(idx);
		selectedObjTempl.setTemplateObjectList(list);
		selectedObjTempl.save(getUser().getUserName());
		
		objTempl.editObjTempl(selectedObjTempl);
	}

	/** Create an Object Template */
	public void createObjTemplate()
	{
		List<UEntryError> errors = UEntryError.getEmptyList();
		errors.addAll( objTempl.getTemplNameField().validateStringLen(1, 255) );
		
		if (errors.size() > 0)
		{
			objTempl.setError(errors);
			return;
		}
		
		try {
			
			UntzDBObjectTemplate t = UntzDBObjectTemplate.createTemplate(objTempl.getTemplNameField().getText());
			t.save(getUser().getUserName());
			objTempl.setup();
			
		} catch (ObjectExistsException e) {
			objTempl.setError(UEntryError.getTopLevelError(Msg.getString("Object-Exists")));
		}
	}
	
	/** Create a TOS */
	public void createTos()
	{
		List<UEntryError> errors = UEntryError.getEmptyList();
		errors.addAll( tos.getTosNameField().validateStringLen(1, 255) );
		
		if (errors.size() > 0)
		{
			tos.setError(errors);
			return;
		}
		
		try {
			
			TermsConditions t = TermsConditions.createTOS(tos.getTosNameField().getText());
			t.save(getUser().getUserName());
			tos.setup();
			
		} catch (ObjectExistsException e) {
			tos.setError(UEntryError.getTopLevelError(Msg.getString("Object-Exists")));
		}
	}

	/** Cancel Editing */
	public void cancelObjTemplEdit()
	{
		objTempl.setup();
	}

	/** Cancel Editing */
	public void cancelTosEdit()
	{
		tos.setup();
	}

	/** Remove a TOS from a role */
	public void deleteTosFromRole(Component comp)
	{
		Button btn = (Button)comp;
		String tosName = (String)btn.get("name");
		
		selectedRoleDef.removeTOS(tosName);
		selectedResourceDef.setRole(selectedRoleDef);
		selectedResourceDef.save(getUser().getUserName());
		
		rdv.editResourceDef(selectedResourceDef);
		rdv.editRole(selectedRoleDef);
	}

	/** Add a TOS to a role */
	public void addTosToRole()
	{
		if (rdv.getTosNameField().getSelectedIndex() == 0)
			return;
		
		TermsConditions tc = (TermsConditions)rdv.getTosNameField().getSelectedObject();
		selectedRoleDef.addTOS(tc.getName());
		selectedResourceDef.setRole(selectedRoleDef);
		selectedResourceDef.save(getUser().getUserName());
		
		rdv.editResourceDef(selectedResourceDef);
		rdv.editRole(selectedRoleDef);
	}
	
	/** Cancel the editing of a ResourceDefinition */
	public void cancelResourceDefinitionEdit()
	{
		rdv.setup();
	}

	/** Delete a RoleDefinition from the currently selected ResourceDefinition */
	public void deleteRole(Component comp)
	{
		Button btn = (Button)comp;

		String tgtRole = (String)btn.get("name");
		
		selectedResourceDef.deleteRole(selectedResourceDef.getRoleByName(tgtRole));
		selectedResourceDef.save(getUser().getUserName());
		rdv.editResourceDef(selectedResourceDef);
	}
	
	/** Edit a RoleDefinition */
	public void editRole(Component comp)
	{
		Button btn = (Button)comp;

		String tgtRole = (String)btn.get("name");
		selectedRoleDef = selectedResourceDef.getRoleByName(tgtRole);
		rdv.editRole(selectedRoleDef);
	}
	
	public void saveRoleOrder()
	{
		List<UEntryError> errors = UEntryError.getEmptyList();
		errors.addAll( rdv.getRoleOrderField().validateIntRange(0, 1000) );
		
		if (errors.size() > 0)
		{
			rdv.setPermissionError(errors);
			return;
		}

		selectedRoleDef.setRoleOrder( Integer.valueOf( rdv.getRoleOrderField().getText() ).intValue() );
		selectedRoleDef.setLinkActionClass( rdv.getRoleLinkActionClassField().getText()  );
		
		if (rdv.getUserTemplateField().getSelectedIndex() > 0)
			selectedRoleDef.setObjectTemplate(((UntzDBObjectTemplate)rdv.getUserTemplateField().getSelectedObject()).getTemplateName());
		
		selectedResourceDef.setRole(selectedRoleDef);
		selectedResourceDef.save(getUser().getUserName());
		
		rdv.editResourceDef(selectedResourceDef);
		rdv.editRole(selectedRoleDef);
	}
	
	/** Save a new Permission to the selected RoleDefinition */
	public void savePermission(Component comp)
	{
		List<UEntryError> errors = UEntryError.getEmptyList();
		errors.addAll( rdv.getPermissionNameField().validateStringLen(1, 255) );
		
		if (errors.size() > 0)
		{
			rdv.setPermissionError(errors);
			return;
		}

		selectedRoleDef.addPermission(rdv.getPermissionNameField().getText());
		selectedResourceDef.setRole(selectedRoleDef);
		selectedResourceDef.save(getUser().getUserName());
		
		rdv.editResourceDef(selectedResourceDef);
		rdv.editRole(selectedRoleDef);
	}

	/** Delete a Permission from the currently selected RoleDefinition */
	public void deletePermission(Component comp)
	{
		Button btn = (Button)comp;

		String tgtRole = (String)btn.get("name");
		
		selectedRoleDef.deletePermission(tgtRole);
		selectedResourceDef.save(getUser().getUserName());
		rdv.editResourceDef(selectedResourceDef);
		rdv.editRole(selectedRoleDef);
	}

	/** Save a new Additional Resource Role to the selected RoleDefinition */
	public void saveAddtResRole(Component comp)
	{
		List<UEntryError> errors = UEntryError.getEmptyList();
		errors.addAll( rdv.getResNameField().validateStringLen(1, 255) );
		errors.addAll( rdv.getRoleField().validateStringLen(1, 255) );
		
		if (errors.size() > 0)
		{
			rdv.setPluginError(errors);
			return;
		}

		selectedRoleDef.addAdditionalResourceRole(rdv.getResNameField().getText(), rdv.getRoleField().getText());
		selectedResourceDef.setRole(selectedRoleDef);
		selectedResourceDef.save(getUser().getUserName());
		
		rdv.editResourceDef(selectedResourceDef);
		rdv.editRole(selectedRoleDef);
	}
	
	public void saveVariable(Component comp) 
	{
		List<UEntryError> errors = UEntryError.getEmptyList();
		errors.addAll( rdv.getVariableField().validateStringLen(1, 255) );
		errors.addAll( rdv.getValueField().validateStringMinLen(1) );

		DBObject varJSON = null;
		
		try { 
			
			String v = rdv.getValueField().getText();

			try {
				varJSON = (DBObject)JSON.parse(v);
			} catch (Exception e) {
	            v = v.replaceAll("\"", "\\\\\"");
	            v = "{ code : \"" + v  + "\" }";
				varJSON = (DBObject)JSON.parse(v);
			}
            
		} catch (Exception e) {
			logger.warn("Failed to parse to JSON : " +rdv.getValueField().getText());
			rdv.getValueField().error();
			errors.add(UEntryError.getInstance("Variable", "Cannot Parse Value"));
		}

		if (errors.size() > 0)
		{
			rdv.setPluginError(errors);
			return;
		}

		selectedRoleDef.addVariable(rdv.getVariableField().getText(), varJSON);
		selectedResourceDef.setRole(selectedRoleDef);
		selectedResourceDef.save(getUser().getUserName());
		
		rdv.editResourceDef(selectedResourceDef);
		rdv.editRole(selectedRoleDef);
	}
	
	public void deleteVariable(Component comp)
	{
		Button btn = (Button)comp;

		String var = (String)btn.get("var");
		
		selectedRoleDef.deleteVariable(var);
		selectedResourceDef.save(getUser().getUserName());
		rdv.editResourceDef(selectedResourceDef);
		rdv.editRole(selectedRoleDef);
	}
	

	
	/** Save a new Auto-Subscription to the selected RoleDefinition */
	public void saveAutoSub(Component comp)
	{
		List<UEntryError> errors = UEntryError.getEmptyList();
		errors.addAll( rdv.getEventField().validateStringLen(1, 255) );
		errors.addAll( rdv.getMethodField().validateStringLen(1, 255) );
		
		if (errors.size() > 0)
		{
			rdv.setPluginError(errors);
			return;
		}

		selectedRoleDef.addAutoSubscription(rdv.getEventField().getText(), rdv.getMethodField().getText());
		selectedResourceDef.setRole(selectedRoleDef);
		selectedResourceDef.save(getUser().getUserName());
		
		rdv.editResourceDef(selectedResourceDef);
		rdv.editRole(selectedRoleDef);
	}
	
	public void deleteAutoSub(Component comp)
	{
		Button btn = (Button)comp;

		String event = (String)btn.get("event");
		String method = (String)btn.get("method");
		
		selectedRoleDef.deleteAutoSub(event, method);
		selectedResourceDef.save(getUser().getUserName());
		rdv.editResourceDef(selectedResourceDef);
		rdv.editRole(selectedRoleDef);
	}
	
	public void deleteAddtResRole(Component comp)
	{
		Button btn = (Button)comp;

		String resName = (String)btn.get("resName");
		String role = (String)btn.get("role");
		
		selectedRoleDef.deleteAddtResRole(resName, role);
		selectedResourceDef.save(getUser().getUserName());
		rdv.editResourceDef(selectedResourceDef);
		rdv.editRole(selectedRoleDef);
	}
	
	/** Save a new Plugin to the selected RoleDefinition */
	public void savePlugin(Component comp)
	{
		List<UEntryError> errors = UEntryError.getEmptyList();
		errors.addAll( rdv.getPluginNameField().validateStringLen(1, 255) );
		
		if (errors.size() > 0)
		{
			rdv.setPluginError(errors);
			return;
		}

		selectedRoleDef.addPlugin(rdv.getPluginNameField().getText(), rdv.getPluginParentField().getText());
		selectedResourceDef.setRole(selectedRoleDef);
		selectedResourceDef.save(getUser().getUserName());
		
		rdv.editResourceDef(selectedResourceDef);
		rdv.editRole(selectedRoleDef);
	}
	
	public void moveUp(Component comp)
	{
		Button btn = (Button)comp;
		int idx = (Integer)btn.get("idx");

		if (idx == 0)
			return;
		
		BasicDBList pluginList = selectedRoleDef.getPluginList();
		DBObject curPlace = (DBObject)pluginList.get(idx - 1);
		DBObject selected = (DBObject)pluginList.get(idx);

		pluginList.set(idx - 1, selected);
		pluginList.set(idx, curPlace);
		
		selectedRoleDef.setPluginList(pluginList);
		selectedResourceDef.save(getUser().getUserName());
		
		rdv.editResourceDef(selectedResourceDef);
		rdv.editRole(selectedRoleDef);
	}

	public void moveDown(Component comp)
	{
		Button btn = (Button)comp;
		int idx = (Integer)btn.get("idx");

		BasicDBList pluginList = selectedRoleDef.getPluginList();
		
		if (idx == (pluginList.size() - 1))
			return;
		
		DBObject curPlace = (DBObject)pluginList.get(idx + 1);
		DBObject selected = (DBObject)pluginList.get(idx);

		pluginList.set(idx + 1, selected);
		pluginList.set(idx, curPlace);
		
		selectedRoleDef.setPluginList(pluginList);
		selectedResourceDef.save(getUser().getUserName());
		
		rdv.editResourceDef(selectedResourceDef);
		rdv.editRole(selectedRoleDef);
	}

	/** Delete a Plugin from the currently selected RoleDefinition */
	public void deletePlugin(Component comp)
	{
		Button btn = (Button)comp;

		String tgtPlugin = (String)btn.get("name");
		String tgtParent = (String)btn.get("parent");
		
		selectedRoleDef.deletePlugin(tgtPlugin, tgtParent);
		selectedResourceDef.save(getUser().getUserName());
		rdv.editResourceDef(selectedResourceDef);
		rdv.editRole(selectedRoleDef);
	}
	
	/** delete the configuration item from the database */
	public void deleteConfigItem(Component comp)
	{
		Button btn = (Button)comp;

		UDBConfigItem item = UDBConfigItem.getByPropertyName((String)btn.get("propertyName"));
		if (item != null)
		{
			item.delete();
			hv.setup();
		}
	}
	
	/** save the configuration item to the database */
	public void saveConfigItem(Component comp)
	{
		List<UEntryError> errors = UEntryError.getEmptyList();
		errors.addAll( hv.getPropertyNameField().validateStringLen(1, 255) );
		errors.addAll( hv.getValueField().validateStringMaxLen(255) );
		
		if (errors.size() > 0)
		{
			hv.setError(errors);
			return;
		}
		
		UDBConfigItem item = UDBConfigItem.getByPropertyName(hv.getPropertyName());
		if (item == null)
			item = new UDBConfigItem(hv.getPropertyName());
		
		item.setValue(hv.getValue());
		item.save(getUser().getUserName());
		
		hv.setup();
	}

	/** handle the expired password event */
	public void savePassword(Component comp) 
	{
		UserDetailsEntry ude = (UserDetailsEntry)comp;

		logger.info("Trying to save password for account: " + ude.getUserName());

		try {
			
			if (!ude.getPassword1().equals(ude.getPassword2()))
				throw new PasswordMismatchException();

			UserAccount user = getUser();
			user.setPassword(getUser().getUserName(), ude.getPassword1());
			user.save(getUser().getUserName());

			ApplicationInstance.getUStackActive().userLogin(user);

		} catch (Exception err) {
			logger.warn("Failed to create user account [" + ude.getUserName() + "]", err);
			ude.setErrorList(UEntryError.getListInstance(Msg.getString("Password"), err.getMessage()));
		}
	}

	/** Search for sites based on name */
	public void searchSites()
	{
		List<DBObject> objects = SiteAccount.search(sav.getLookupTable().getHeader(), sav.getLookupTable().getPager(), SiteAccount.getDBCollection(), null);
		sav.loadSiteList(objects, sav.getLookupTable().getPager().getResultCount());
	}
	
	/** Does a lookup for a user account */
	public void lookupUserAccount()
	{
		selectedUserAccount = UserAccount.getUser( uav.getLookupUserNameField().getText() );
		if (selectedUserAccount == null)
		{
			uav.lookupAlert(Msg.getString("Unknown-UserName"));
			return;
		}
		
		uav.loadUserAccount(selectedUserAccount);
	}

	/** Removes a resource link from the currently selected user */
	public void removeUserResourceLink(Component comp)
	{
		logger.info("removeUserResourceLink");
		
		Button btn = (Button)comp;
		selectedUserAccount.removeResourceLinkIdx((Integer)btn.get("LinkIdx"));
		selectedUserAccount.save(getUser().getUserName());
		
		uav.loadUserAccount(selectedUserAccount);
	}
	
	public void addNotifSubscription(Component comp)
	{
		UNotificationSelect uns = (UNotificationSelect)comp;
		
		uns.subscribeUser( selectedUserAccount.getUserName() );
		uav.loadUserAccount(selectedUserAccount);
	}
	
	/** Add a selected resource and role */
	public void createUserResourceLink(Component comp)
	{
		UResourceRoleSelect urs = (UResourceRoleSelect)comp;
		
		selectedUserAccount.addResourceLink(urs.getResourceLink());
		selectedUserAccount.save(getUser().getUserName());
		
		uav.loadUserAccount(selectedUserAccount);
	}


	/** Removes a resource link from the currently selected site */
	public void removeSiteResourceLink(Component comp)
	{
		Button btn = (Button)comp;
		selectedSiteAccount.removeResourceLinkIdx((Integer)btn.get("LinkIdx"));
		selectedSiteAccount.save(getUser().getUserName());
		
		sav.loadSiteAccount(selectedSiteAccount);
	}
	
	/** Add a selected resource and role */
	public void createSiteResourceLink(Component comp)
	{
		UResourceRoleSelect urs = (UResourceRoleSelect)comp;
		
		selectedSiteAccount.addResourceLink(urs.getResourceLink());
		selectedSiteAccount.save(getUser().getUserName());
		
		sav.loadSiteAccount(selectedSiteAccount);
	}

	/** Create a new site account */
	public void newSiteAccount()
	{
		List<UEntryError> errors = UEntryError.getEmptyList();
		errors.addAll( sav.getSiteNameField().validateStringLen(1, 255) );
		
		if (errors.size() > 0)
		{
			sav.setErrorList(errors);
			return;
		}
		
		String siteName = sav.getSiteNameField().getText();
		logger.info("Trying to create account: " + siteName);
		
		try {
				
			// create new account
			SiteAccount site = SiteAccount.createSite(siteName);
			logger.info("Saving site account : " + siteName);
			site.save(getUser().getUserName());
			
			sav.siteCreatedUpdate(siteName);
			
		} catch (Exception err) {
			logger.warn("Failed to create site account [" + siteName + "]", err);
			sav.setErrorList(UEntryError.getListInstance(Msg.getString("SiteName"), err.getMessage()));
		}
	}

	/** Load the selected site */
	public void loadSite()
	{
		DBObject obj = sav.getLookupTable().getSelectedObject();
		selectedSiteAccount = new SiteAccount(obj);
		sav.loadSiteAccount(selectedSiteAccount);
	}
	
	public void resetPassword()
	{
		try {
			String newPass = "$$$" + selectedUserAccount.getUserName() + "$$$";
			selectedUserAccount.setPassword(getUser().getUserName(), newPass);
			selectedUserAccount.expirePassword(getUser().getUserName());
			selectedUserAccount.save(getUser().getUserName());
			uav.actionError("Password User Set : " + newPass);
		} catch (Exception e) {
			uav.actionError("Failed to set user's password : " + e.getMessage());
		}
	}

	/** Sends an email to the user regarding a lost password */
	public void sendForgotPassword()
	{
		try {
			// TODO: provide a reasonable default email
			selectedUserAccount.sendForgotPassword( "demo@untzuntz.com" );
			selectedUserAccount.save(getUser().getUserName());
			uav.actionError(Msg.getString("ForgotPassword-Sent", selectedUserAccount.getPrimaryEmail()));
		} catch (AddressException e) {
			uav.actionError(Msg.getString("Missing-ReqAcctData", "Email"));
		} catch (RequiredAccountDataMissingException e) {
			uav.actionError(Msg.getString("Missing-ReqAcctData", "Email"));
		} 
	}
	
	/** Called to generate a new user account */
	public void newUserAccount()
	{
		List<UEntryError> errors = UEntryError.getEmptyList();
		errors.addAll( uav.getUserNameField().validateStringLen(1, 255) );
		
		if (errors.size() > 0)
		{
			uav.setErrorList(errors);
			return;
		}
		
		String userName = uav.getUserNameField().getText();
		logger.info("Trying to create account: " + userName);
		
		try {
				
			if ("admin".equalsIgnoreCase(userName))
				throw new InvalidUserAccountName("Reserved");
			
			String genPassword = Authentication.generatePassword();
			
			// create new account
			UserAccount user = UserAccount.createUser(getUser().getUserName(), userName, genPassword);
			logger.info("Saving user account : " + userName);
			user.save(getUser().getUserName());
			
			uav.userCreatedUpdate(userName, genPassword);
			
		} catch (Exception err) {
			logger.warn("Failed to create user account [" + userName + "]", err);
			uav.setErrorList(UEntryError.getListInstance(Msg.getString("UserName"), err.getMessage()));
		}
	}

	/** handle the create admin account event */
	public void createAccount(Component comp)
	{
		UserDetailsEntry ude = (UserDetailsEntry)comp;
		
		logger.info("Trying to create account: " + ude.getUserName());
		
		try {
				
			if (!ude.getPassword1().equals(ude.getPassword2()))
				throw new PasswordMismatchException();
			if ("admin".equalsIgnoreCase(ude.getUserName()))
				throw new InvalidUserAccountName("Reserved");
			
			// create new account
			UserAccount user = UserAccount.createUser(getUser().getUserName(), ude.getUserName(), ude.getPassword1());
			logger.info("Saving user admin account : " + ude.getUserName());
			user.addResourceLink(new ResourceLink(ResourceDefinition.getByName("Setup App"), "General"));
			user.save(ApplicationInstance.SUBSYS_SETUP);
			
			// delete old admin account
			UserAccount oldAdminUser = getUser();
			oldAdminUser.delete();

			// re-login as the new user
			executeAction("login", ude);
				
		} catch (Exception err) {
			logger.warn("Failed to create user account [" + ude.getUserName() + "]", err);
			ude.setErrorList(UEntryError.getListInstance(Msg.getString("Password"), err.getMessage()));
		}
		
	}

	@Override
	public void loadPlugin(UPluginInt plugin) {} // not supported
	
}
