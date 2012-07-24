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
public class UserPassEntry extends Grid implements ActionListener,UserPassEntryInt {

	private static final long serialVersionUID = 1L;
	private TextField userName;
	private PasswordField password;
	private UErrorColumn errors;
	private ActionListener actionListener;

	/**
	 * Constructor - provide a callback actionListener for external events
	 * 
	 * @param actionListener
	 */
	public UserPassEntry(ActionListener actionListener) {

		this.actionListener = actionListener;
		setSize(3);
		setInsets(UStackStatics.IN_WIDE);

		userName = new UTextField(Msg.getString("Username"), UStackStatics.EX_150, this, "gotoPassword");
		password = new UPasswordField("Password", UStackStatics.EX_150, this, "login");
		errors = new UErrorColumn();
		errors.setLayoutData(UStackStatics.GRID_SPAN2_RIGHT);

		Button fgpw = new UButton(Msg.getString("ForgotYourPassword"), UStackStatics.LIGHT_BUTTON, this, "forgotPassword");
		fgpw.setRolloverEnabled(true);
		fgpw.setRolloverFont(UStackStatics.FONT_SMALL_UL);
		fgpw.setLayoutData(UStackStatics.GRID_SPAN2_RIGHT);

		add(errors);
		add(new Label(""));

		add(new Label(Msg.getString("Username") + ":"));
		add(new Label(Msg.getString("Password") + ":"));
		add(new Label(""));
		
		add(userName);
		add(password);
		add(new UButton(Msg.getString("SignIn"), UStackStatics.GO_BUTTON, this, "login"));
		
		add(fgpw);
		add(new Label(""));

		ApplicationInstance.getActive().setFocusedComponent(userName);
	}
	
	public String getUserName() {
		return userName.getText();
	}
	
	public String getPassword() { 
		return password.getText();
	}
	
	public void setErrorList(List<UEntryError> errorList)
	{
		errors.setErrorList(errorList);
	}

	public void actionPerformed(ActionEvent e) {

		String action = e.getActionCommand();
		
		if ("gotoPassword".equalsIgnoreCase(action))
			ApplicationInstance.getActive().setFocusedComponent(password);
		else
		{
			ActionEvent ae = new ActionEvent(this, e.getActionCommand()); // map to this class
			actionListener.actionPerformed(ae);
		}
		
	}
	
}
