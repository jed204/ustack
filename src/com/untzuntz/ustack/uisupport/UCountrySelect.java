package com.untzuntz.ustack.uisupport;

import java.util.List;
import java.util.Vector;

import nextapp.echo.app.event.ActionEvent;
import nextapp.echo.app.event.ActionListener;

import com.untzuntz.ustack.data.Country;

public class UCountrySelect extends USelectField implements ActionListener {

	private static final long serialVersionUID = 1L;
	private UStateSelect stateSelect;
	private UTimeZoneSelect timeZoneSelect;
	private List<UPhoneField> phones;

	public UCountrySelect()
	{
		setup();
	}
	
	public UCountrySelect(UStateSelect sel, UTimeZoneSelect tzSel)
	{
		setStateSelect(sel);
		setTimezoneSelect(tzSel);
		setup();
	}
	
	private void setup()
	{
		fieldName = "Country";

	    setData(Country.getCountries());
	    
	    setSelected("United States");
	    addActionListener(this);
	}
	
	public void setTimezoneSelect(UTimeZoneSelect tzSel)
	{
		timeZoneSelect = tzSel;
	}
	
	public void addPhoneEntry(UPhoneField phone)
	{
		if (phones == null)
			phones = new Vector<UPhoneField>();
		
		phones.add(phone);
		update();
	}
	
	public void setStateSelect(UStateSelect state)
	{
		stateSelect = state;
	}
	
	public void setSelected(String state)
	{
		super.setSelected(state);
		update();
	}
	
	private void update()
	{
		if (stateSelect != null)
			stateSelect.setCountry( getString() );
		if (timeZoneSelect != null)
			timeZoneSelect.setCountry( getString() );
		if (phones != null)
		{
			for (UPhoneField phone : phones)
				phone.setCountry( (Country)getSelectedObject() );
		}
	}

	public void actionPerformed(ActionEvent e) {
		update();
	}
	
}
