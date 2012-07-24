package com.untzuntz.ustack.main.setup;


import nextapp.echo.app.Color;

import com.untzuntz.ustack.main.Msg;
import com.untzuntz.ustack.main.UControllerInt;
import com.untzuntz.ustack.uisupport.UBlockLabel;
import com.untzuntz.ustack.uisupport.UCenterCol;
import com.untzuntz.ustack.uisupport.ULabel;
import com.untzuntz.ustack.uisupport.UStackStatics;
import com.untzuntz.ustack.uisupport.UViewColumn;
import com.untzuntz.ustack.uisupport.widgets.UserDetailsEntry;

import echopoint.Strut;

public class ResetPasswordView extends UViewColumn {

	private static final long serialVersionUID = 1L;
	
	public static final String REASON_EXPIRED = "Expired";
	public static final String REASON_FORGOT = "Forgot";
	
	public ResetPasswordView(UControllerInt ctrl) { 
		super(ctrl);
	}

	public ResetPasswordView(UControllerInt ctrl, boolean setup) { 
		super(ctrl, setup);
	}
	
	private String reason;

	public ResetPasswordView setReason(String rsn) {
		reason = rsn;
		internalSetup();
		return this;
	}
	
	@Override
	public void setup() {
		
		if (reason == null)
			reason = REASON_EXPIRED;

		UCenterCol body = new UCenterCol();
		body.setBorder(UStackStatics.BDR_SIMPLE);
		body.setInsets(UStackStatics.IN_15);
		body.setCellSpacing(UStackStatics.EX_5);
		add(body);
		
		body.add(new ULabel(Msg.getString(reason + "Password"), UStackStatics.FONT_BOLD_MED));

		Strut sp = new Strut(550, 0);
		sp.setBorder(UStackStatics.BDR_SIMPLE);
		body.add(sp);
		
		body.add(new UBlockLabel(Msg.getString(reason + "PasswordInfo"), UStackStatics.EX_550, UStackStatics.IN_TB_10));

		UserDetailsEntry ude = new UserDetailsEntry(this);
		ude.getUserNameField().setText(getController().getUser().getUserName());
		ude.getUserNameField().setEnabled(false);
		ude.getUserNameField().setForeground(Color.DARKGRAY);
		ude.getSaveButton().setText(Msg.getString("SavePassword"));
		ude.setSaveCommand("savePassword");
		body.add(ude);
	}
	
}
