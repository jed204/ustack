package com.untzuntz.ustack.uisupport;

import java.util.List;
import java.util.Vector;

import nextapp.echo.app.ApplicationInstance;
import nextapp.echo.app.Color;
import nextapp.echo.app.Column;
import nextapp.echo.app.event.ActionListener;

import com.mongodb.BasicDBList;
import com.mongodb.DBObject;
import com.untzuntz.ustack.data.Country;
import com.untzuntz.ustack.data.State;

public class UStateSelect extends Column {

	private static final long serialVersionUID = 1L;
	public static final int VIEW_DROPDOWN = 0;
	public static final int VIEW_TEXT_ENTRY = 1;
	private USelectField stateSelect;
	private UTextField stateEntry;
	
	public void addActionListener(ActionListener al) {
		stateSelect.addActionListener(al);
		stateEntry.addActionListener(al);
	}
	
	public void setActionCommand(String cmd) {
		stateSelect.setActionCommand(cmd);
		stateEntry.setActionCommand(cmd);
	}
	
	public void setFocus()
	{
		if (stateSelect.isVisible())
			ApplicationInstance.getActive().setFocusedComponent(stateSelect);
		else
			ApplicationInstance.getActive().setFocusedComponent(stateEntry);
	}

	public UStateSelect()
	{
		stateSelect = new USelectField("State");
		stateEntry = new UTextField("State");
		add(stateSelect);
		add(stateEntry);
		setView(VIEW_DROPDOWN);
	}
	
	public String getString()
	{
		String ret = null;
		if (stateSelect.isVisible())
			ret = stateSelect.getString();
		else
			ret = stateEntry.getText();
		
		return ret;
	}

	/** Mark the field in error */
	public void error()
	{
		stateEntry.setBackground(Color.RED);
		stateEntry.setForeground(Color.WHITE);
	}
	
	/** Mark the field as passed */
	public void pass()
	{
		stateEntry.setBackground(Color.WHITE);
		stateEntry.setForeground(Color.BLACK);
	}

	public List<UEntryError> validateEntries()
	{
		List<UEntryError> ret = new Vector<UEntryError>();
	
		if (stateSelect.isVisible())
			ret.addAll( stateSelect.validateMinSelectionIndex(1) );
		
		if (ret.size() > 0)
			error();
		else
			pass(); // clears previous errors

		return ret;
	}
	
	public void setCountry(String name)
	{
		Country ctry = Country.getCountryByName(name);
		
		if (ctry != null)
		{
			BasicDBList states = ctry.getStateList();
			if (states.size() == 0)
				setView(VIEW_TEXT_ENTRY);
			else
			{
				setView(VIEW_DROPDOWN);
				List<State> stateList = new Vector<State>();
				for (int i = 0; i < states.size(); i++)
				{
					State st = new State((DBObject)states.get(i));
					stateList.add(st);
				}
				stateSelect.setData(stateList);
			}
		}
	}
	
	public void setView(int viewType)
	{
		stateSelect.setVisible(false);
		stateEntry.setVisible(false);
		if (viewType == VIEW_DROPDOWN)
		{
			stateSelect.setVisible(true);
			stateSelect.setSelected( stateEntry.getText() );
		}
		else
			stateEntry.setVisible(true);
	}
	
	public void setSelected(String val)
	{
		stateSelect.setSelected(val);
		stateEntry.setText(val);
	}
	
	
}
