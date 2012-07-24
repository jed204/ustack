package com.untzuntz.ustack.main.setup;

import java.util.List;

import nextapp.echo.app.Button;
import nextapp.echo.app.Grid;

import com.untzuntz.ustack.data.TermsConditions;
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
import com.untzuntz.ustack.uisupport.UTextArea;
import com.untzuntz.ustack.uisupport.UTextField;
import com.untzuntz.ustack.uisupport.UViewColumn;

import echopoint.Strut;

public class TermsConditionsView extends UViewColumn {

	private static final long serialVersionUID = 1L;

	public TermsConditionsView(UControllerInt ctrl) { super(ctrl); }
	
	private UTextField tosName;
	private UTextField renewalDays;
	private UTextField displayName;
	private UTextArea tosText;
	private UErrorColumn errors;
	
	public void setError(List<UEntryError> errList) { errors.setErrorList(errList); }
	
	@Override
	public void setup() {

		removeAll();
		
		UCenterCol body = new UCenterCol();
		body.setInsets(UStackStatics.IN_15);
		body.setCellSpacing(UStackStatics.EX_5);
		add(body);

		body.add(new ULabel(Msg.getString("TermsOfService"), UStackStatics.FONT_BOLD_LARGE));
		body.add(new Strut(800, 15));

		Grid configItems = new Grid(3);
		configItems.setInsets(UStackStatics.IN_5);
		configItems.setWidth(UStackStatics.EX_800);
		configItems.setColumnWidth(1, UStackStatics.EX_125);
		body.add(configItems);
		
		configItems.add(new ULabel(Msg.getString("Name"), UStackStatics.FONT_BOLD_MED));
		configItems.add(new ULabel(Msg.getString("RenewalDays"), UStackStatics.FONT_NORMAL_BOLD));
		configItems.add(new ULabel("", UStackStatics.FONT_NORMAL_BOLD));
		
		List<TermsConditions> items = TermsConditions.getAll();
		for (TermsConditions item : items)
		{
			Button edit = new UButton(Msg.getString("Edit"), UStackStatics.WEB_BUTTON, this, "editTos");
			edit.set("name", item.getName());
			Button rem = new UButton(Msg.getString("Remove"), UStackStatics.WEB_BUTTON, this, "deleteTos");
			rem.set("name", item.getName());
			
			configItems.add(new ULabel(item.getName(), 100));
			configItems.add(new ULabel(item.getRenewalDays() + "", 100));
			
			URow row = new URow();
			row.add(edit);
			row.add(rem);
			configItems.add(row);
		}
		
		tosName = new UTextField(Msg.getString("Name"));
		
		configItems.add(tosName);
		configItems.add(new UButton(Msg.getString("Save"), UStackStatics.WEB_BUTTON, this, "createTos"));
		
		body.add(errors = new UErrorColumn());
	}

	public UTextField getRenewalDaysField() { 
		return renewalDays;
	}
	
	public UTextField getTosNameField() {
		return tosName;
	}
	
	public UTextField getDisplayNameField()
	{
		return displayName;
	}
	
	public UTextArea getTosTextField() {
		return tosText;
	}

	public void editTos(TermsConditions tos)
	{
		removeAll();
		
		UCenterCol body = new UCenterCol();
		body.setInsets(UStackStatics.IN_15);
		body.setCellSpacing(UStackStatics.EX_5);
		add(body);

		Grid header = new Grid(2);
		header.setWidth(UStackStatics.EX_800);
		header.setColumnWidth(0, UStackStatics.EX_50P);
		body.add(header);

		header.add(new ULabel(tos.getName() + " " + Msg.getString("TermsOfService"), UStackStatics.FONT_BOLD_LARGE));
		header.add(new URow(new UButton(Msg.getString("Close"), UStackStatics.WEB_BUTTON, this, "cancelTosEdit"), UStackStatics.GRID_RIGHT));
		body.add(new Strut(800, 15));

		body.add(new UColumn(new ULabel(Msg.getString("BasicSettings"), UStackStatics.FONT_BOLD_LARGE), UStackStatics.BDR_SIMPLE, UStackStatics.LIGHT_GRAY));
		body.add(new Strut(800, 5));

		UGrid opts = new UGrid(2, UStackStatics.IN_5);
		body.add(opts);
		
		opts.add(Msg.getString("RenewalDays") + ":");
		opts.add(renewalDays = new UTextField(Msg.getString("Renewal Days")));
		
		opts.add(Msg.getString("DisplayName") + ":");
		opts.add(displayName = new UTextField(Msg.getString("Display Name")));

		opts.add(new ULabel(Msg.getString("TosText"), UStackStatics.GRID_TOP));
		opts.add(tosText = new UTextArea("Text", UStackStatics.EX_550, UStackStatics.EX_230));

		opts.add("");
		opts.add(new UButton(Msg.getString("Save"), UStackStatics.WEB_BUTTON, this, "updateTos"));
		
		renewalDays.setText( tos.getRenewalDays() + "" );
		tosText.setText(tos.getText() );
		displayName.setText(tos.getDisplayName());
	}
}
