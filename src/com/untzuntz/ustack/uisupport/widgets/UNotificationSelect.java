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

import com.untzuntz.ustack.aaa.LinkActionHelper;
import com.untzuntz.ustack.aaa.LinkActionInterface;
import com.untzuntz.ustack.data.NotificationInst;
import com.untzuntz.ustack.data.NotificationTemplate;
import com.untzuntz.ustack.data.UserAccount;
import com.untzuntz.ustack.main.Msg;
import com.untzuntz.ustack.uisupport.LinkUIComponent;
import com.untzuntz.ustack.uisupport.UButton;
import com.untzuntz.ustack.uisupport.UColumn;
import com.untzuntz.ustack.uisupport.UEntryError;
import com.untzuntz.ustack.uisupport.UGrid;
import com.untzuntz.ustack.uisupport.ULabel;
import com.untzuntz.ustack.uisupport.UPhoneField;
import com.untzuntz.ustack.uisupport.UStackStatics;
import com.untzuntz.ustack.uisupport.UTextField;

public class UNotificationSelect extends Column implements ActionListener {

	private static final long serialVersionUID = 1L;

	private Column templatesCol;
	private Column configCol;
	private List<NotificationTemplate> templates;
	private List<ActionListener> actions;
	private String actionCommand;
	private static final ColumnLayoutData cld = new ColumnLayoutData();
	private LinkActionInterface linkAction;
	private String selectedEvent;
	private UserAccount actor;
	
	public UNotificationSelect(UserAccount actor)
	{
		this.actor = actor;
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
		
		templatesCol = new UColumn(UStackStatics.EX_2);
		templatesCol.setLayoutData(UStackStatics.GRID_TOP);
		configCol = new UColumn(UStackStatics.EX_2);
		configCol.setLayoutData(UStackStatics.GRID_TOP);
		
		inGrid.add(templatesCol);
		inGrid.add(configCol);
		
		templates = new Vector<NotificationTemplate>();
	}
	
	public void setActionCommand(String cmd) {
		actionCommand = cmd;
	}
	
	public void addActionListener(ActionListener al)
	{
		actions.add(al);
	}
	
	public void loadTemplates(List<NotificationTemplate> templList)
	{
		templates.clear();
		templates.addAll(templList);
		
		templatesCol.removeAll();
		templatesCol.add(new ULabel("Select an event:", UStackStatics.FONT_NORMAL_BOLD));
		
		for (NotificationTemplate res : templList)
		{
			Button btn = new UButton(res.getEventName(), UStackStatics.WEB_BUTTON, this, "selectEvent");
			btn.setInsets(UStackStatics.IN_5);
			btn.set("name", res.getEventName());
			templatesCol.add(btn);
		}
	}
	
	private void loadConfig(String resourceName)
	{
		for (NotificationTemplate res : templates)
		{
			if (resourceName.equalsIgnoreCase(res.getEventName()))
				loadConfig(res);
		}
	}

	private UTextField email;
	private UPhoneField sms;
	private void loadConfig(NotificationTemplate res)
	{
		configCol.removeAll();
		configCol.add(new ULabel("Provide destination info:", UStackStatics.FONT_NORMAL_BOLD));

		UGrid grid = new UGrid(2, UStackStatics.IN_5);
		configCol.add(grid);

		if (res.getType("email") != null)
		{
			grid.add(Msg.getString("Email"));
			grid.add(email = new UTextField("Email"));
		}
		if (res.getType("sms") != null)
		{
			grid.add(Msg.getString("SMS"));
			grid.add(sms = new UPhoneField("SMS"));
		}
		grid.add("");
		grid.add(new UButton(Msg.getString("Save"), UStackStatics.WEB_BUTTON, this, "saveConfig"));
	}

	public void actionPerformed(ActionEvent e) {
		
		String action = e.getActionCommand();
		
		if ("selectEvent".equalsIgnoreCase(action))
		{
			for (int i = 0; i < templatesCol.getComponentCount(); i++)
			{
				templatesCol.getComponent(i).setLayoutData(null);
				templatesCol.getComponent(i).setForeground(null);
			}
			
			Button btn = (Button)e.getSource();
			btn.setLayoutData(cld);
			btn.setForeground(Color.WHITE);
			selectedEvent = (String)btn.get("name");
			
			loadConfig(selectedEvent);
		}
		else if ("saveConfig".equalsIgnoreCase(action))
		{
			List<UEntryError> errors = UEntryError.getEmptyList();
			
			if (email != null && email.getText().length() > 0)
				errors.addAll(email.validateEmailAddress());

			if (sms != null)
				errors.addAll(sms.validateEntriesIfValue());
			
			if (errors.size() > 0)
				return;
			
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
		NotificationTemplate def = NotificationTemplate.getNotificationTemplate(selectedEvent);
		linkAction = LinkActionHelper.getLinkAction(def.getLinkActionClass());
		
		if (linkAction == null)
		{
			runActions();
			return;
		}
		// setup the user
		linkAction.setUser(actor);
		
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
	
		if (selectedEvent == null || (linkAction != null && linkAction.getResourceLinkExtras(null) == null))
			ret.add(UEntryError.getInstance("NotificationSelect", Msg.getString("EntryError-NotificationSelect", "Notification Select")));
		
		return ret;
	}
	
	public LinkActionInterface getLinkAction()
	{
		return linkAction;
	}
	
	public String getSelectedEvent() 
	{
		return selectedEvent;
	}
	
	public UPhoneField getSMSField()
	{
		return sms;
	}

	public UTextField getEmailField()
	{
		return email;
	}
	
	public void subscribeUser(String user)
	{
		NotificationTemplate notifTempl = NotificationTemplate.getNotificationTemplate(selectedEvent);
		
		NotificationInst ni1 = NotificationInst.subscribe(notifTempl, notifTempl.getEventName(), user);
		if (getSMSField() != null && getSMSField().getPhoneString() != null)
			ni1.addType("sms", getSMSField().getPhoneString()); 
		if (getEmailField() != null && getEmailField().getText().length() > 0)
			ni1.addType("email", getEmailField().getText());
		if (getLinkAction() != null)
			ni1.putAll(getLinkAction().getResourceLinkExtras(null));

		ni1.save(actor.getUserName());
	}
	
}
