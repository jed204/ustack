package com.untzuntz.ustack.uisupport.widgets;

import nextapp.echo.app.Column;
import nextapp.echo.app.Label;
import nextapp.echo.app.LayoutData;
import nextapp.echo.app.Row;
import nextapp.echo.app.event.ActionListener;

import com.untzuntz.components.app.AlertLabel;
import com.untzuntz.ustack.main.Msg;
import com.untzuntz.ustack.uisupport.URow;
import com.untzuntz.ustack.uisupport.UStackStatics;
import com.untzuntz.ustack.uisupport.UTextField;

public class SearchWidget extends Column {

	private static final long serialVersionUID = 1L;
	private ActionListener actionListener;
	private String actionCommand;
	private UTextField searchField;
	private AlertLabel alertMsg;
	
	public SearchWidget(ActionListener al, String cmd, LayoutData ld)
	{
		setLayoutData(ld);
		setup(al, cmd);
	}
	
	public SearchWidget(ActionListener al, String cmd)
	{
		setup(al, cmd);
	}
	
	public void setup(ActionListener al, String cmd)
	{
		actionListener = al;
		actionCommand = cmd;
		
		setCellSpacing(UStackStatics.EX_2);
		removeAll();
		
		Label lbl = new Label(Msg.getString("Search"));
		lbl.setIcon(UStackStatics.IMAGE_SEARCH_16);

		alertMsg = new AlertLabel();
		alertMsg.setVisible(false);
		alertMsg.setInsets(UStackStatics.IN_5);
		
		Row row = new URow();
		row.add(alertMsg);
		row.add(lbl);
		add(row);

		searchField = new UTextField("Search");
		searchField.addActionListener(actionListener);
		searchField.setActionCommand(actionCommand);
		add(searchField);
	}
	
	public UTextField getSearchField() {
		return searchField;
	}
	
	public void setAlertMsg(String msg)
	{
		alertMsg.setVisible(true);
		alertMsg.setText(msg);
	}
	
}
