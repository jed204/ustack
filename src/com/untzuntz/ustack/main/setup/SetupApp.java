package com.untzuntz.ustack.main.setup;

import nextapp.echo.app.Component;
import nextapp.echo.app.event.ActionListener;

import com.untzuntz.ustack.aaa.Authorization;
import com.untzuntz.ustack.aaa.UStackPermissionEnum;
import com.untzuntz.ustack.data.UserAccount;
import com.untzuntz.ustack.exceptions.AuthorizationException;
import com.untzuntz.ustack.main.ApplicationInstance;
import com.untzuntz.ustack.main.Msg;
import com.untzuntz.ustack.main.UControllerInt;
import com.untzuntz.ustack.main.UPluginInt;

/**
 * Setup Application
 * 
 * This is also a reference application for the UStack stack
 * 
 * @author jdanner
 *
 */
public class SetupApp extends ApplicationInstance implements ActionListener,UControllerInt {

	private static final long serialVersionUID = 1L;
	
	public String getResourceName() { return "Setup App"; }
	
	public SetupApp(String appName)
	{
		super(appName);
	}
	
	public Component getDefaultView()
	{
		return new LoginView((UControllerInt)ApplicationInstance.getActive());
	}
	
	public void setupApp()
	{
		setupApp(Msg.getString("ApplicationSetup"));
		loadController(this);
	}

	public void userLogin(UserAccount usr)
	{
		super.userLogin(usr);

		loadController(new SetupAppController());
	}

	@Override
	public void authorizeUser(UserAccount usr, UStackPermissionEnum perm) throws AuthorizationException {
		Authorization.authorizeUser(usr, getResourceName(), null, perm);
	}

	public void logout()
	{
		userLogout();
		loadController(this);
	}
	
	public void handleForgotPasswordError(String reason) {
		LoginView lv = new LoginView(getController(), false);
		lv.setSpecialMessage(Msg.getString("ForgotPasswordLink-" + reason));
		getController().loadView(lv);
	}

	public void handleForgotPassword() {
		getController().loadView(new ResetPasswordView(getController(), false).setReason(ResetPasswordView.REASON_FORGOT));
	}
	
	public void handleExpiredPassword() {} // handled in the SetupAppController
	public void loadPlugin(UPluginInt plugin) {} // not supported

	public UPluginInt getPluginById(String id) {
		return null;
	}
	
}
