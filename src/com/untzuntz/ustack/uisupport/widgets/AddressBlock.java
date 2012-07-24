package com.untzuntz.ustack.uisupport.widgets;

import java.util.List;
import java.util.Vector;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.untzuntz.ustack.data.UDataMgr;
import com.untzuntz.ustack.main.Msg;
import com.untzuntz.ustack.uisupport.UButton;
import com.untzuntz.ustack.uisupport.UCountrySelect;
import com.untzuntz.ustack.uisupport.UEntryError;
import com.untzuntz.ustack.uisupport.UGrid;
import com.untzuntz.ustack.uisupport.UPhoneField;
import com.untzuntz.ustack.uisupport.URow;
import com.untzuntz.ustack.uisupport.UStackStatics;
import com.untzuntz.ustack.uisupport.UStateSelect;
import com.untzuntz.ustack.uisupport.UTextField;
import com.untzuntz.ustack.uisupport.UTimeZoneSelect;

import nextapp.echo.app.ApplicationInstance;
import nextapp.echo.app.Column;
import nextapp.echo.app.Extent;
import nextapp.echo.app.Label;
import nextapp.echo.app.Style;
import nextapp.echo.app.event.ActionEvent;
import nextapp.echo.app.event.ActionListener;

public class AddressBlock extends Column implements ActionListener {

	private static final long serialVersionUID = 1L;
	private UCountrySelect country;
	private UTimeZoneSelect timeZone;
	private UTextField address1;
	private UTextField address2;
	private UTextField city;
	private UStateSelect state;
	private UTextField postalCode;
	private UPhoneField telephoneNumber;
	private UPhoneField faxNumber;
	private Label latLong;
	private UGrid grid;
	private Style style;
	
	public boolean showTimeZone;
	public void setShowTimezone(boolean show) { showTimeZone = show; }
	public boolean showFax;
	public void setShowFax(boolean show) { showFax = show; }
	public boolean showPhone;
	public void setShowPhone(boolean show) { showPhone = show; }
	public boolean showLocation;
	public void setShowLocation(boolean show) { showLocation = show; }
	public void setStyle(Style style) {
		this.style = style;
	}
	
	public AddressBlock(boolean manualInit)
	{
		showTimeZone = true;
		showFax = true;
		showPhone = true;
		showLocation = true;
		if (!manualInit)
			setup();
	}
	
	public AddressBlock()
	{
		showTimeZone = true;
		setup();
	}
	
	public void setFocus()
	{
		ApplicationInstance.getActive().setFocusedComponent(country);
	}
	
	public void setColumnWidth(int col, Extent ex) {
		grid.setColumnWidth(col, ex);
	}
	
	public void setupCol()
	{
		fieldSetup();
		
		if (showLocation)
		{
			add(new Label(Msg.getString("latLong") + ":"));
			add(latLongRow);
		}
		add(new Label(Msg.getString("country") + ":"));
		add(new URow(country));
		add(new Label(Msg.getString("address1") + ":"));
		add(address1);
		add(new Label(Msg.getString("address2") + ":"));
		add(address2);
		add(new Label(Msg.getString("city") + ":"));
		add(city);
		add(new Label(Msg.getString("stateProvRegion") + ":"));
		add(new URow(state));
		add(new Label(Msg.getString("postalCode") + ":"));
		add(postalCode);
		if (showTimeZone)
		{
			add(new Label(Msg.getString("timeZone") + ":"));
			add(new URow(timeZone));
		}

		if (showPhone)
		{
			add(new Label(Msg.getString("telephoneNumber") + ":"));
			add(telephoneNumber);
		}
		if (showFax)
		{
			add(new Label(Msg.getString("faxNumber") + ":"));
			add(faxNumber);
		}
	}
	
	private URow latLongRow;
	private void fieldSetup()
	{
		latLongRow = new URow();
		latLongRow.add(latLong = new Label("n/a"));
		latLongRow.add(new UButton(Msg.getString("calculate"), UStackStatics.WEB_BUTTON, this, "calculateLatLong"));
		
		country = new UCountrySelect(state = new UStateSelect(), timeZone = new UTimeZoneSelect());
		address1 = new UTextField("Address 1");
		address2 = new UTextField("Address 2");
		city = new UTextField("City");
		postalCode = new UTextField("Postal Code");
		
		if (showPhone)
		{
			telephoneNumber = new UPhoneField("Telephone Number");
			country.addPhoneEntry(telephoneNumber);
		}
		
		if (showFax)
		{
			faxNumber = new UPhoneField("Fax Number");
			country.addPhoneEntry(faxNumber);
		}
		
		country.addActionListener(this);
		country.setStyle(style);
		address1.addActionListener(this);
		address1.setStyle(style);
		address2.addActionListener(this);
		address2.setStyle(style);
		city.addActionListener(this);
		city.setStyle(style);
		state.setStyle(style);
		postalCode.addActionListener(this);
		postalCode.setStyle(style);
		if (showPhone)
		{
			telephoneNumber.addActionListener(this);
			telephoneNumber.setStyle(style);
		}
		country.setActionCommand("gotoAddress1");
		address1.setActionCommand("gotoAddress2");
		address2.setActionCommand("gotoCity");
		city.setActionCommand("gotoState");
		state.setActionCommand("gotoPostalCode");
		if (showTimeZone)
		{
			postalCode.setActionCommand("gotoTimeZone");
			postalCode.setStyle(style);
			timeZone.setStyle(style);
		}
		if (showFax)
		{
			faxNumber.setActionCommand("gotoFaxNumber");
			faxNumber.setStyle(style);
		}
		
		address1.setWidth(UStackStatics.EX_200);
		address2.setWidth(UStackStatics.EX_200);
		city.setWidth(UStackStatics.EX_100);
		postalCode.setWidth(UStackStatics.EX_50);

	}
	
	public void setup()
	{
		fieldSetup();
		
		removeAll();
		
		grid = new UGrid(2, UStackStatics.IN_5);
		add(grid);

		if (showLocation)
		{
			grid.add(Msg.getString("latLong"));
			grid.add(latLongRow);
		}
		grid.add(Msg.getString("country"));
		grid.add(new URow(country));
		grid.add(Msg.getString("address1"));
		grid.add(address1);
		grid.add(Msg.getString("address2"));
		grid.add(address2);
		grid.add(Msg.getString("city"));
		grid.add(city);
		grid.add(Msg.getString("stateProvRegion"));
		grid.add(new URow(state));
		grid.add(Msg.getString("postalCode"));
		grid.add(postalCode);
		if (showTimeZone)
		{
			grid.add(Msg.getString("timeZone"));
			grid.add(new URow(timeZone));
		}

		if (showPhone)
		{
			grid.add(Msg.getString("telephoneNumber"));
			grid.add(telephoneNumber);
		}
		if (showFax)
		{
			grid.add(Msg.getString("faxNumber"));
			grid.add(faxNumber);
		}
	}

	public void actionPerformed(ActionEvent e) {

		if ("gotoAddress1".equalsIgnoreCase(e.getActionCommand()))
			ApplicationInstance.getActive().setFocusedComponent(address1);
		else if ("gotoAddress2".equalsIgnoreCase(e.getActionCommand()))
			ApplicationInstance.getActive().setFocusedComponent(address2);
		else if ("gotoCity".equalsIgnoreCase(e.getActionCommand()))
			ApplicationInstance.getActive().setFocusedComponent(city);
		else if ("gotoState".equalsIgnoreCase(e.getActionCommand()))
			state.setFocus();
		else if ("gotoPostalCode".equalsIgnoreCase(e.getActionCommand()))
			ApplicationInstance.getActive().setFocusedComponent(postalCode);
		else if ("gotoTimeZone".equalsIgnoreCase(e.getActionCommand()))
			ApplicationInstance.getActive().setFocusedComponent(timeZone);
		else if ("gotoTelephoneNumber".equalsIgnoreCase(e.getActionCommand()))
			ApplicationInstance.getActive().setFocusedComponent(telephoneNumber);
		else if ("gotoFaxNumber".equalsIgnoreCase(e.getActionCommand()))
			ApplicationInstance.getActive().setFocusedComponent(faxNumber);
		else if ("calculateLatLong".equalsIgnoreCase(e.getActionCommand()))
			calculateLatLong();
	}
	
	private DBObject loc;
	private void calculateLatLong()
	{
		loc = null;
		DBObject addr = new BasicDBObject();
		getAddress(addr);
		loc = UDataMgr.calculateLatLong(addr);
		if (loc == null)
			latLong.setText("Error");
		else
			setLatLong(loc);
	}
	
	public List<UEntryError> validateEntries()
	{
		List<UEntryError> ret = new Vector<UEntryError>();
		
		ret.addAll( address1.validateStringLen(1, 255) );
		ret.addAll( city.validateStringLen(1, 255) );
		ret.addAll( state.validateEntries() );
		ret.addAll( country.validateMinSelectionIndex(1) );
		ret.addAll( postalCode.validateStringLen(1, 255) );
		if (showTimeZone)
			ret.addAll( timeZone.validateMinSelectionIndex(1) );
		if (showPhone)
			ret.addAll( telephoneNumber.validateEntriesIfValue() );
		if (showFax)
			ret.addAll( faxNumber.validateEntriesIfValue() );
		
		return ret;
	}
	
	public UPhoneField getTelephoneNumber() {
		return telephoneNumber;
	}
	
	public void getPhone(DBObject tgt)
	{
		DBObject obj = null;
		if (showPhone && (obj = telephoneNumber.getDBObject()) != null)
			tgt.put("primaryTelephone", obj);
		if (showFax && (obj = faxNumber.getDBObject()) != null)
			tgt.put("faxNumber", obj);
	}
	
	public void getAddress(DBObject addr)
	{
		if (addr == null)
			return;
		
		addr.put("country", country.getString());
		addr.put("address1", address1.getText());
		addr.put("address2", address2.getText());
		addr.put("city", city.getText());
		addr.put("state", state.getString());
		addr.put("postalCode", postalCode.getText());
		if (showTimeZone)
			addr.put("timeZone", timeZone.getString());
		if (loc != null)
			addr.put("loc", loc);
	}
	
	public void setPhone(DBObject src)
	{
		if (src == null)
			return;

		if (showPhone)
			telephoneNumber.setPhoneNumber( (DBObject)src.get("primaryTelephone") );
		if (showFax)
			faxNumber.setPhoneNumber( (DBObject)src.get("faxNumber") );
	}
	
	private void setLatLong(DBObject loc)
	{
		if (loc != null)
			latLong.setText( loc.get("lat") + ", " + loc.get("lng") );
		else
			latLong.setText("n/a");
	}
	
	public void setAddress(DBObject addr)
	{
		setLatLong((DBObject)addr.get("loc"));
		country.setSelected( (String)addr.get("country") );
		address1.setText( (String)addr.get("address1") );
		address2.setText( (String)addr.get("address2") );
		city.setText( (String)addr.get("city") );
		state.setSelected( (String)addr.get("state") );
		postalCode.setText( (String)addr.get("postalCode") );
		if (showTimeZone)
			timeZone.setSelected( (String)addr.get("timeZone") );
	}

	public UCountrySelect getCountry() {
		return country;
	}

	public void setCountry(UCountrySelect country) {
		this.country = country;
	}

	public UTextField getAddress1() {
		return address1;
	}

	public void setAddress1(UTextField address1) {
		this.address1 = address1;
	}

	public UTextField getAddress2() {
		return address2;
	}

	public void setAddress2(UTextField address2) {
		this.address2 = address2;
	}

	public UTextField getCity() {
		return city;
	}

	public void setCity(UTextField city) {
		this.city = city;
	}

	public UStateSelect getState() {
		return state;
	}

	public void setState(UStateSelect state) {
		this.state = state;
	}

	public UTextField getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(UTextField postalCode) {
		this.postalCode = postalCode;
	}
	
	
}
