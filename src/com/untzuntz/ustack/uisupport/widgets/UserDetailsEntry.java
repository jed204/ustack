package com.untzuntz.ustack.uisupport.widgets;

import java.util.List;

import nextapp.echo.app.Button;
import nextapp.echo.app.Grid;
import nextapp.echo.app.Label;
import nextapp.echo.app.PasswordField;
import nextapp.echo.app.TextField;
import nextapp.echo.app.event.ActionEvent;
import nextapp.echo.app.event.ActionListener;

import com.untzuntz.ustack.main.ApplicationInstance;
import com.untzuntz.ustack.main.Msg;
import com.untzuntz.ustack.uisupport.UButton;
import com.untzuntz.ustack.uisupport.UEntryError;
import com.untzuntz.ustack.uisupport.UErrorColumn;
import com.untzuntz.ustack.uisupport.UPasswordField;
import com.untzuntz.ustack.uisupport.UStackStatics;
import com.untzuntz.ustack.uisupport.UTextField;
import com.untzuntz.ustack.uisupport.widgets.interf.UserPassEntryInt;

/**
 * Provides a username & password UI area + an error label that can be set from the outside
 * 
 * @author jdanner
 *
 */
public class UserDetailsEntry extends Grid implements ActionListener, UserPassEntryInt {

	private static final long serialVersionUID = 1L;
	private TextField userName;
	private PasswordField password1;
	private PasswordField password2;
	private UErrorColumn errors;
	private Button saveButton;
	private ActionListener actionListener;
	
	public String getUserName()
	{
		return userName.getText();
	}
	
	public String getPassword1()
	{
		return password1.getText();
	}

	public String getPassword2()
	{
		return password2.getText();
	}

	public String getPassword() {
		return password1.getText();
	}
	
	public TextField getUserNameField() {
		return userName;
	}
	
	public void setSaveCommand(String cmd) {
		saveButton.setActionCommand(cmd);
		password2.setActionCommand(cmd);
	}
	
	public Button getSaveButton() {
		return saveButton;
	}

	/**
	 * Constructor - provide a callback actionListener for external events
	 * 
	 * @param actionListener
	 */
	public UserDetailsEntry(ActionListener actionListener) {
		this.actionListener = actionListener;
		setup();
	}
	
	/**
	 * Build the actual control
	 * 
	 * @param actionListener
	 * @param userNameStr
	 * @param lockUserAccount
	 */
	public UserDetailsEntry setup() {

		removeAll();
		
		setSize(2);
		setInsets(UStackStatics.IN_WIDE);

		userName = new UTextField(Msg.getString("Username"), UStackStatics.EX_150, this, "gotoPassword1");
		password1 = new UPasswordField("Password 1", UStackStatics.EX_150, this, "gotoPassword2");
		password2 = new UPasswordField("Password 2", UStackStatics.EX_150, this, "createAccount");
		errors = new UErrorColumn();
		errors.setLayoutData(UStackStatics.GRID_SPAN2);
		saveButton = new UButton(Msg.getString("CreateAccount"), UStackStatics.GO_BUTTON, this, "createAccount");

		add(new Label(Msg.getString("Username") + ":"));
		add(userName);
		
		add(new Label(Msg.getString("Password1") + ":"));
		add(password1);
		
		add(new Label(Msg.getString("Password2") + ":"));
		add(password2);
		
		add(new Label(""));
		add(saveButton);

		add(errors);

		ApplicationInstance.getActive().setFocusedComponent(userName);
		
		return this;
	}
	
	/**
	 * Sets the red error text in the event of a problem during login/check
	 * @param errorList
	 */
	public void setErrorList(List<UEntryError> errorList)
	{
		errors.setErrorList(errorList);
	}

	public void actionPerformed(ActionEvent e) {

		String action = e.getActionCommand();
		
		if ("gotoPassword1".equalsIgnoreCase(action))
			ApplicationInstance.getActive().setFocusedComponent(password1);
		else if ("gotoPassword2".equalsIgnoreCase(action))
			ApplicationInstance.getActive().setFocusedComponent(password2);
		else
		{
			ActionEvent ae = new ActionEvent(this, e.getActionCommand()); // map to this class
			actionListener.actionPerformed(ae);
		}
		
	}
	
}
