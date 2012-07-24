package com.untzuntz.ustack.main.setup;

import nextapp.echo.app.Color;
import nextapp.echo.app.Column;
import nextapp.echo.app.Grid;

import com.untzuntz.ustack.main.Msg;
import com.untzuntz.ustack.main.UControllerInt;
import com.untzuntz.ustack.uisupport.UStackStatics;
import com.untzuntz.ustack.uisupport.UBlockLabel;
import com.untzuntz.ustack.uisupport.UCenterCol;
import com.untzuntz.ustack.uisupport.ULabel;
import com.untzuntz.ustack.uisupport.UViewColumn;
import com.untzuntz.ustack.uisupport.widgets.UserPassEntry;

import echopoint.Strut;

/**
 * Login UI for the application setup app
 * 
 * @author jdanner
 *
 */
public class LoginView extends UViewColumn {

	private static final long serialVersionUID = 1L;

	public LoginView(UControllerInt ctrl, boolean setup) { super(ctrl, setup); }
	
	public LoginView(UControllerInt ctrl) { super(ctrl); }

	private String specialMessage;
	public void setSpecialMessage(String msg)
	{
		specialMessage = msg;
		internalSetup();
	}
	
	@Override
	public void setup() {

		Column header = new UCenterCol();
		header.setInsets(UStackStatics.IN_5);
		header.setForeground(Color.WHITE);
		header.setBackground(UStackStatics.DARK_BLUE);
		add(header);
		
		Grid main = new Grid();
		main.setWidth(UStackStatics.EX_900);
		header.add(main);
		
		main.add(new ULabel(Msg.getString("ApplicationSetup"), UStackStatics.FONT_BOLD_MLARGE));
		
		UserPassEntry loginGrid = new UserPassEntry(this);
		loginGrid.setLayoutData(UStackStatics.GRID_RIGHT);
		main.add(loginGrid);

		add(new Strut(0, 10));
		
		if (specialMessage != null)
		{
			UCenterCol specMsg = new UCenterCol();
			specMsg.setBorder(UStackStatics.BDR_SIMPLE);
			specMsg.setInsets(UStackStatics.IN_15);
			add(specMsg);
			
			UBlockLabel lbl = new UBlockLabel(specialMessage, UStackStatics.EX_550, UStackStatics.IN_TB_10);
			lbl.setFont(UStackStatics.FONT_BOLD_MED);
			lbl.setForeground(Color.RED);
			
			specMsg.add(new Strut(0, 15));
			specMsg.add(lbl);
			specMsg.add(new Strut(0, 15));
			
			add(new Strut(0, 20));
		}


		UCenterCol body = new UCenterCol();
		body.setBorder(UStackStatics.BDR_SIMPLE);
		body.setInsets(UStackStatics.IN_15);
		add(body);
		

		body.setCellSpacing(UStackStatics.EX_5);
		
		body.add(new ULabel(Msg.getString("ApplicationSetupLogin"), UStackStatics.FONT_BOLD_MED));
		
		Strut sp = new Strut(550, 0);
		sp.setBorder(UStackStatics.BDR_SIMPLE);
		body.add(sp);
		
		body.add(new UBlockLabel(Msg.getString("ApplicationSetupInfo"), UStackStatics.EX_550, UStackStatics.IN_TB_10));

	}

}
