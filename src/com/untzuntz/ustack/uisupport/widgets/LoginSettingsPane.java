package com.untzuntz.ustack.uisupport.widgets;

import nextapp.echo.app.Button;
import nextapp.echo.app.Color;
import nextapp.echo.app.ContentPane;
import nextapp.echo.app.FillImage;
import nextapp.echo.app.Grid;
import nextapp.echo.app.Label;
import nextapp.echo.app.Row;
import nextapp.echo.app.event.ActionEvent;
import nextapp.echo.app.event.ActionListener;

import com.untzuntz.ustack.main.Msg;
import com.untzuntz.ustack.main.UControllerInt;
import com.untzuntz.ustack.uisupport.UButton;
import com.untzuntz.ustack.uisupport.URow;
import com.untzuntz.ustack.uisupport.UStackStatics;

/**
 * Creates a ContentPane with a black bar and a call for settings and logging out
 * @author jdanner
 *
 */
public class LoginSettingsPane extends ContentPane implements ActionListener {

	private static final long serialVersionUID = 1L;
	
	private UControllerInt controller;
	public LoginSettingsPane(UControllerInt ctrl, String message) {
		controller = ctrl;
		setBackgroundImage(new FillImage(UStackStatics.IMAGE_SLIM));
		setForeground(Color.WHITE);
		setup(message);
	}
	
	public void setup(String message)
	{
		removeAll();
		
		Grid g = new Grid(3);
		add(g);
		
		g.setColumnWidth(0, UStackStatics.EX_33P);
		g.setColumnWidth(1, UStackStatics.EX_33P);
		g.setColumnWidth(2, UStackStatics.EX_33P);
		g.setWidth(UStackStatics.EX_100P);
		g.setInsets(UStackStatics.IN_T7);
		
		/*
		 * Settings
		 */
		Button settings = new UButton(Msg.getString("AccountSettings"), UStackStatics.WEB_BUTTON, this, "settings");
		settings.setRolloverEnabled(true);
		settings.setRolloverBackground(UStackStatics.DARK_GRAY);
		g.add(new URow(settings));

		// Middle
		g.add(new URow(new Label(message), UStackStatics.GRID_CENTER));

		/*
		 * Logout
		 */
		Row lout = new URow(UStackStatics.GRID_RIGHT);
		g.add(lout);
		
		lout.add(new Label(Msg.getString("LoggedInAs", controller.getUser().getUserName())));

		Button logout = new UButton(Msg.getString("Logout"), UStackStatics.GO_BUTTON, this, "logout");
		logout.setRolloverEnabled(true);
		logout.setRolloverFont(UStackStatics.FONT_BOLD_UL_SMALL);
		lout.add(logout);
	}
	
	@SuppressWarnings("unchecked")
	public void actionPerformed(ActionEvent e) {

		controller.actionPerformed(e);
		
	}

}
