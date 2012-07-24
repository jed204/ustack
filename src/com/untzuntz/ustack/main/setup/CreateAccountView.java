package com.untzuntz.ustack.main.setup;

import com.untzuntz.ustack.main.Msg;
import com.untzuntz.ustack.main.UControllerInt;
import com.untzuntz.ustack.uisupport.UBlockLabel;
import com.untzuntz.ustack.uisupport.UCenterCol;
import com.untzuntz.ustack.uisupport.ULabel;
import com.untzuntz.ustack.uisupport.UStackStatics;
import com.untzuntz.ustack.uisupport.UViewColumn;
import com.untzuntz.ustack.uisupport.widgets.UserDetailsEntry;

import echopoint.Strut;

public class CreateAccountView extends UViewColumn {

	private static final long serialVersionUID = 1L;

	public CreateAccountView(UControllerInt ctrl) { super(ctrl); }
	
	@Override
	public void setup() {

		UCenterCol body = new UCenterCol();
		body.setBorder(UStackStatics.BDR_SIMPLE);
		body.setInsets(UStackStatics.IN_15);
		body.setCellSpacing(UStackStatics.EX_5);
		add(body);
		
		body.add(new ULabel(Msg.getString("CreateAdminAccount"), UStackStatics.FONT_BOLD_MED));

		Strut sp = new Strut(550, 0);
		sp.setBorder(UStackStatics.BDR_SIMPLE);
		body.add(sp);
		
		body.add(new UBlockLabel(Msg.getString("CreateAdminAccountInfo"), UStackStatics.EX_550, UStackStatics.IN_TB_10));

		body.add(new UserDetailsEntry(this).setup());
	}
}
