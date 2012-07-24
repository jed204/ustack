package com.untzuntz.ustack.main.setup;

import java.util.List;

import nextapp.echo.app.ApplicationInstance;
import nextapp.echo.app.Button;
import nextapp.echo.app.Label;

import com.mongodb.BasicDBList;
import com.mongodb.DBObject;
import com.untzuntz.components.app.AlertLabel;
import com.untzuntz.ustack.aaa.ResourceDefinition;
import com.untzuntz.ustack.data.NotificationInst;
import com.untzuntz.ustack.data.NotificationTemplate;
import com.untzuntz.ustack.data.UserAccount;
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
import com.untzuntz.ustack.uisupport.UTextField;
import com.untzuntz.ustack.uisupport.UViewColumn;
import com.untzuntz.ustack.uisupport.widgets.UNotificationSelect;
import com.untzuntz.ustack.uisupport.widgets.UResourceRoleSelect;

import echopoint.Strut;

public class UserAccountView extends UViewColumn {

	private static final long serialVersionUID = 1L;

	public UserAccountView(UControllerInt ctrl) { super(ctrl); }
	
	private UTextField userName;
	private UTextField lookupUserName;
	private UTextField eventName;
	private UTextField smsAddr;
	private UTextField emailAddr;
	private UTextField partner;
	private UErrorColumn errors;
	private AlertLabel updateInfo;
	private AlertLabel lookupInfo;
	private AlertLabel actionError;
	private UColumn userArea;
	
	public void setErrorList(List<UEntryError> errList) { 
		updateInfo.setVisible(false);
		errors.setErrorList(errList); 
	}
	
	@Override
	public void setup() {

		removeAll();
		
		UCenterCol body = new UCenterCol();
		body.setInsets(UStackStatics.IN_15);
		body.setCellSpacing(UStackStatics.EX_5);
		add(body);

		body.add(new ULabel(Msg.getString("UserAccounts"), UStackStatics.FONT_BOLD_LARGE));

		URow lrow = new URow();
		body.add(lrow);
		
		lookupUserName = new UTextField(Msg.getString("UserName"));
		lookupUserName.addActionListener(this);
		lookupUserName.setActionCommand("lookupUserAccount");
		lookupInfo = new AlertLabel();
		lookupInfo.setVisible(false);
		lookupInfo.setBackground(UStackStatics.LIGHT_YELLOW);
		lookupInfo.setInsets(UStackStatics.IN_5);
		
		lrow.add(new Label(Msg.getString("Lookup-UserAccount")));
		lrow.add(lookupUserName);
		lrow.add(new UButton(Msg.getString("Lookup"), UStackStatics.WEB_BUTTON, this, "lookupUserAccount"));
		lrow.add(lookupInfo);


		body.add(lrow);

		body.add(new Strut(800, 10));

		userArea = new UColumn();
		body.add(userArea);
		
		body.add(new Strut(800, 50));
		body.add(new ULabel(Msg.getString("New-UserAccount"), UStackStatics.FONT_BOLD_LARGE));

		URow row = new URow();
		body.add(row);
		
		userName = new UTextField(Msg.getString("UserName"));
		userName.addActionListener(this);
		userName.setActionCommand("newUserAccount");
		
		row.add(new Label(Msg.getString("New-UserAccount")));
		row.add(userName);
		row.add(new UButton(Msg.getString("Save"), UStackStatics.WEB_BUTTON, this, "newUserAccount"));
		
		body.add(errors = new UErrorColumn());

		updateInfo = new AlertLabel();
		updateInfo.setVisible(false);
		updateInfo.setBackground(UStackStatics.LIGHT_YELLOW);
		updateInfo.setInsets(UStackStatics.IN_5);
		body.add(updateInfo);
	}
	
	public void userCreatedUpdate(String userNameStr, String password)
	{
		errors.setErrorList(null);
		updateInfo.setVisible(true);
		updateInfo.setText(Msg.getString("User-AccountCreated", userNameStr, password));
		userName.setText("");
		ApplicationInstance.getActive().setFocusedComponent(userName);
	}
	
	public void loadUserAccount(UserAccount user)
	{
		lookupInfo.setVisible(false);
		
		userArea.removeAll();
		
		userArea.add(new UColumn(new ULabel(user.getUserName(), UStackStatics.FONT_BOLD_LARGE), UStackStatics.BDR_SIMPLE, UStackStatics.LIGHT_GRAY));
		
		URow acts = new URow();
		userArea.add(acts);
		
		actionError = new AlertLabel();
		actionError.setVisible(false);
		
		acts.add(new UButton("Send Forgot Password Email", UStackStatics.WEB_BUTTON, this, "sendForgotPassword"));
		acts.add(new UButton("Reset Password", UStackStatics.WEB_BUTTON, this, "resetPassword"));
		acts.add(actionError);
		
		UGrid resGrid = new UGrid(4, UStackStatics.IN_5);
		resGrid.setWidth(UStackStatics.EX_800);
		resGrid.add(new ULabel(Msg.getString("Resource"), UStackStatics.FONT_BOLD_MED));
		resGrid.add(new ULabel(Msg.getString("Role"), UStackStatics.FONT_BOLD_MED));
		resGrid.add(new ULabel(Msg.getString("LinkData"), UStackStatics.FONT_BOLD_MED));
		resGrid.add(new ULabel("", UStackStatics.FONT_BOLD_MED));
		resGrid.setColumnWidth(0, UStackStatics.EX_200);
		resGrid.setColumnWidth(1, UStackStatics.EX_100);
		resGrid.setColumnWidth(3, UStackStatics.EX_25);
		userArea.add(resGrid);

		BasicDBList resLink = user.getResourceLinkList();
		for (int i = 0; i < resLink.size(); i++)
		{
			DBObject obj = (DBObject)resLink.get(i);
			resGrid.add(new Label( (String)obj.get("name") ));
			resGrid.add(new Label( (String)obj.get("role") ));
			resGrid.add(new Label( (String)obj.get("linkText") ));
			
			Button btn = new UButton("Remove", UStackStatics.WEB_BUTTON, this, "removeUserResourceLink");
			btn.set("LinkIdx", new Integer(i));
			resGrid.add(btn);
		}
		
		userArea.add(new Strut(0, 10));
		userArea.add(new ULabel(Msg.getString("Add-ResourceLink"), UStackStatics.FONT_BOLD_MED));

		UResourceRoleSelect urs = new UResourceRoleSelect(getController().getUser());
		urs.setActionCommand("createUserResourceLink");
		urs.addActionListener(this);
		urs.loadResources( ResourceDefinition.getAll(ResourceDefinition.TYPE_USERACCESS, null) );
		userArea.add(urs);


		/*
		 * Subscriptionss
		 */
		userArea.add(new Strut(0, 10));
		userArea.add(new ULabel(Msg.getString("Subscriptions"), UStackStatics.FONT_BOLD_MED));

		UGrid subGrid = new UGrid(4, UStackStatics.IN_5);
		subGrid.setWidth(UStackStatics.EX_800);
		subGrid.add(new ULabel(Msg.getString("EventName"), UStackStatics.FONT_BOLD_MED));
		subGrid.add(new ULabel(Msg.getString("Info"), UStackStatics.FONT_BOLD_MED));
		subGrid.add(new ULabel(Msg.getString("LinkText"), UStackStatics.FONT_BOLD_MED));
		subGrid.add(new ULabel("", UStackStatics.FONT_BOLD_MED));
		userArea.add(subGrid);
		
		List<NotificationInst> notiList = NotificationInst.getNotifications(user.getUserName());
		for (NotificationInst noti : notiList)
		{
			subGrid.add( noti.getEventName() );
			
			String info = "";
			DBObject email = noti.getType("email");
			if (email != null)
				info += email.get("destination");

			DBObject sms = noti.getType("sms");
			if (sms != null)
			{
				if (info.length() > 0)
					info += " / ";
				info += sms.get("destination");
			}

			subGrid.add(new Label( info ));
			subGrid.add(new Label( (String)noti.get("linkText") ));

			Button btn = new UButton("Remove", UStackStatics.WEB_BUTTON, this, "removeNotifSubscription");
			btn.set("id", noti.getNotificationId());
			subGrid.add(btn);
			
		}

		userArea.add(new Strut(0, 10));
		userArea.add(new ULabel(Msg.getString("Add-Subscription"), UStackStatics.FONT_BOLD_MED));

		UNotificationSelect uns = new UNotificationSelect(getController().getUser());
		uns.setActionCommand("addNotifSubscription");
		uns.addActionListener(this);
		uns.loadTemplates( NotificationTemplate.getAll() );
		userArea.add(uns);
	}
	
	public UTextField getPartnerField() {
		return partner;
	}

	public UTextField getNotifTemplField() {
		return eventName;
	}

	public UTextField getEmailAddrField() {
		return emailAddr;
	}

	public UTextField getSMSAddrField() {
		return smsAddr;
	}

	public UTextField getUserNameField() {
		return userName;
	}
	
	public void actionError(String alert)
	{
		actionError.setVisible(true);
		actionError.setText(alert);
	}

		
	public void lookupAlert(String alert)
	{
		lookupInfo.setVisible(true);
		lookupInfo.setText(alert);
		userArea.removeAll();
	}
	
	public UTextField getLookupUserNameField() {
		return lookupUserName;
	}

}
