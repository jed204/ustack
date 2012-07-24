package com.untzuntz.ustack.main.setup;

import java.util.List;

import nextapp.echo.app.Button;
import nextapp.echo.app.Grid;

import com.untzuntz.ustack.data.UDBConfigItem;
import com.untzuntz.ustack.main.Msg;
import com.untzuntz.ustack.main.UControllerInt;
import com.untzuntz.ustack.uisupport.UButton;
import com.untzuntz.ustack.uisupport.UCenterCol;
import com.untzuntz.ustack.uisupport.UEntryError;
import com.untzuntz.ustack.uisupport.UErrorColumn;
import com.untzuntz.ustack.uisupport.ULabel;
import com.untzuntz.ustack.uisupport.UStackStatics;
import com.untzuntz.ustack.uisupport.UTextField;
import com.untzuntz.ustack.uisupport.UViewColumn;

import echopoint.Strut;

public class ConfigItemsView extends UViewColumn {

	private static final long serialVersionUID = 1L;

	public ConfigItemsView(UControllerInt ctrl) { super(ctrl); }
	
	private UTextField param;
	private UTextField value;
	private UErrorColumn errors;
	
	public void setError(List<UEntryError> errList) { errors.setErrorList(errList); }
	
	@Override
	public void setup() {

		removeAll();
		
		UCenterCol body = new UCenterCol();
		body.setInsets(UStackStatics.IN_15);
		body.setCellSpacing(UStackStatics.EX_5);
		add(body);

		body.add(new ULabel(Msg.getString("ConfigItems"), UStackStatics.FONT_BOLD_LARGE));
		body.add(new Strut(800, 15));

		Grid configItems = new Grid(3);
		configItems.setInsets(UStackStatics.IN_5);
		configItems.setWidth(UStackStatics.EX_800);
		configItems.setColumnWidth(0, UStackStatics.EX_50P);
		configItems.setColumnWidth(2, UStackStatics.EX_25);
		body.add(configItems);
		
		configItems.add(new ULabel(Msg.getString("Parameter"), UStackStatics.FONT_BOLD_MED));
		configItems.add(new ULabel(Msg.getString("Value"), UStackStatics.FONT_BOLD_MED));
		configItems.add(new ULabel("", UStackStatics.FONT_NORMAL_BOLD));
		
		List<UDBConfigItem> items = UDBConfigItem.getAll();
		for (UDBConfigItem item : items)
		{
			Button btn = new UButton(Msg.getString("Remove"), UStackStatics.WEB_BUTTON, this, "deleteConfigItem");
			btn.set("propertyName", item.getPropertyName());
			
			configItems.add(new ULabel(item.getPropertyName(), 50));
			configItems.add(new ULabel(item.getValue(), 50));
			configItems.add(btn);
		}
		
		param = new UTextField(Msg.getString("Parameter"));
		value = new UTextField(Msg.getString("Value"));
		
		configItems.add(param);
		configItems.add(value);
		configItems.add(new UButton(Msg.getString("Save"), UStackStatics.WEB_BUTTON, this, "saveConfigItem"));
		
		body.add(errors = new UErrorColumn());


		
		
	}

	public UTextField getPropertyNameField() {
		return param;
	}
	
	public UTextField getValueField() {
		return value;
	}
	
	public String getPropertyName() {
		return param.getText();
	}
	
	public String getValue() {
		return value.getText();
	}


}
