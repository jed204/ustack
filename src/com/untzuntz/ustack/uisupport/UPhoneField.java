package com.untzuntz.ustack.uisupport;

import java.util.List;
import java.util.Vector;

import nextapp.echo.app.Color;
import nextapp.echo.app.Style;
import nextapp.echo.app.event.ActionListener;

import org.informagen.echo.app.RegExTextField;

import com.mongodb.DBObject;
import com.untzuntz.ustack.data.Country;
import com.untzuntz.ustack.data.UntzDBObject;
import com.untzuntz.ustack.main.Msg;

public class UPhoneField extends URow {

	private static final long serialVersionUID = 1L;
	private String fieldName;
	private UTextField countryCode;
	private RegExTextField phoneNumber;
	private ULabel message;
	
	public UPhoneField(String fieldName)
	{
		super(UStackStatics.EX_5);
		this.fieldName = fieldName;
		setup();
	}
	
	private void setup()
	{
		countryCode = new UTextField(Msg.getString("countryCode"));
		countryCode.setWidth(UStackStatics.EX_30);
		countryCode.setMaximumLength(3);
		phoneNumber = new RegExTextField();
		phoneNumber.setWidth(UStackStatics.EX_100);
		message = new ULabel("", UStackStatics.FONT_SMALL);
		
		removeAll();
		add(countryCode);
		add(phoneNumber);
		add(message);
	}
	
	public void addActionListener(ActionListener al) {
		phoneNumber.addActionListener(al);
	}
	
	public void setActionCommand(String cmd) {
		phoneNumber.setActionCommand(cmd);
	}
	
	public void setStyle(Style s)
	{
		countryCode.setStyle(s);
		phoneNumber.setStyle(s);
	}
	
	public void setPhoneMessage(String msg)
	{
		if (msg != null)
			message.setText(msg);
		else
			message.setText("");
	}
	
	public void setPhoneRegex(String regex)
	{
		if (regex != null)
			phoneNumber.setRegEx(regex);
	}
	
	public String getPhoneString()
	{
		if (countryCode.getText().length() == 0 || phoneNumber.getText().length() == 0)
			return null;
		
		return countryCode.getText() + " " + phoneNumber.getText();
	}
	
	public DBObject getDBObject()
	{
		return UntzDBObject.getPhoneObject( countryCode.getText(), phoneNumber.getText() );
	}
	
	public void setCountry(Country country)
	{
		if (country == null)
		{
			countryCode.setText("");
			return;
		}
		setPhoneRegex(country.getString("telephoneRegex"));
		setPhoneMessage(country.getString("telephoneMessage"));
		
		String code = country.getString("ituTelephoneCode");
		if (code == null)
		{
			countryCode.setText("");
			return;
		}
			
		if (code.indexOf("-") > -1)
		{
			String follow = code.substring( code.indexOf("-") + 1 );
			code = code.substring(0, code.indexOf("-"));
			if (phoneNumber.getText().length() == 0)
				phoneNumber.setText(follow);
		}
		
		if (code.startsWith("+"))
			code = code.substring(1);
		countryCode.setText(code);
	}
	
	public void setPhoneNumber(String phone)
	{
		if (phone == null)
		{
			phoneNumber.setText("");
			return;
		}
		
		if (phone.indexOf(" ") < 4)
		{
			countryCode.setText(phone.substring(0, phone.indexOf(" ")));
			phoneNumber.setText(phone.substring(phone.indexOf(" ") + 1));
		}
		else
			phoneNumber.setText(phone);
	}
	
	public void setPhoneNumber(DBObject obj)
	{
		if (obj == null)
		{
			phoneNumber.setText("");
			return;
		}
		
		countryCode.setText( (String)obj.get("countryCode") );
		phoneNumber.setText( (String)obj.get("phoneNumber") );
	}

	public List<UEntryError> validateEntriesIfValue()
	{
		if (phoneNumber.getText().length() == 0)
		{
			pass();
			return UEntryError.getEmptyList();
		}
		
		List<UEntryError> ret = new Vector<UEntryError>();
		ret.addAll(countryCode.validateIntRange(1, 1000));
		if (!phoneNumber.isValid())
			ret.add(UEntryError.getInstance(fieldName, Msg.getString("EntryError-InvalidPhoneNumber", fieldName)));
		return handleDisplay(ret);
	}

	public List<UEntryError> validateEntries()
	{
		List<UEntryError> ret = new Vector<UEntryError>();
		ret.addAll(countryCode.validateIntRange(1, 1000));
		if (!phoneNumber.isValid())
			ret.add(UEntryError.getInstance(fieldName, Msg.getString("EntryError-InvalidPhoneNumber", fieldName)));
		return handleDisplay(ret);
	}
	
	/** Mark the field in error */
	public void error()
	{
		setBackground(Color.RED);
		setForeground(Color.WHITE);
	}
	
	/** Mark the field as passed */
	public void pass()
	{
		setBackground(null);
		setForeground(null);
	}

	/** called to check on pass or fail of this field */
	private List<UEntryError> handleDisplay(List<UEntryError> list)
	{
		if (list.size() > 0)
			error();
		else
			pass(); // clears previous errors
		
		return list;
	}
	
}
