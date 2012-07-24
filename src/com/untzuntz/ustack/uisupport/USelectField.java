package com.untzuntz.ustack.uisupport;

import java.util.List;
import java.util.Vector;

import com.mongodb.BasicDBList;
import com.untzuntz.ustack.main.Msg;

import nextapp.echo.app.Color;
import nextapp.echo.app.SelectField;

public class USelectField extends SelectField {

	private static final long serialVersionUID = 6493860977737335599L;

	protected @SuppressWarnings("rawtypes") List data;
	protected String fieldName;
	protected USelectField() {}
	
	public String getFieldName() { return fieldName; }
	
	public USelectField(String fieldName, String ... values)
	{
		this.fieldName = fieldName;
		List<String> vals = new Vector<String>();
		
		for (String val : values)
			vals.add(val);
		
		setData(vals);
	}

	public USelectField(String fieldName, @SuppressWarnings("rawtypes") List d)
	{
		this.fieldName = fieldName;
		setData(d);
	}

	public USelectField(String fieldName, UCollectionListModel model)
	{
		this.fieldName = fieldName;
		data = model.getData();
		setModel(model);
		setSelectedIndex(0);
	}


	public USelectField(UCollectionListModel model)
	{
		data = model.getData();
		setModel(model);
		setSelectedIndex(0);
	}
	
	public USelectField(String fieldName, BasicDBList d)
	{
		this.fieldName = fieldName;
		setData(d);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void setData(BasicDBList d) {
		
		List dList = new Vector();
		for (int i = 0; d != null && i < d.size(); i++)
			dList.add(d.get(i));

		setData(dList);
	}

	public void setData(@SuppressWarnings("rawtypes") List d)
	{
		data = d;
		if (data != null)
		{
			setModel(new UCollectionListModel(data));
			setSelectedIndex(0);
		}
	}
	
	public void setSelected(String val)
	{
		int mod = 1;
		if (getModel() instanceof UCollectionListModel)
			mod = ((UCollectionListModel)getModel()).getMod();
		
		for (int i = 0; data != null && i < data.size(); i++)
		{
			String dat = data.get(i).toString();
			if (dat.equalsIgnoreCase(val))
			{
				setSelectedIndex(i + mod);
				return;
			}
		}
	}
	
	public String getString()
	{
		Object obj = getSelectedObject();
		if (obj == null)
			return null;
		
		return obj.toString();
	}
	
	public Object getSelectedObject()
	{
		int mod = 1;
		if (getModel() instanceof UCollectionListModel)
			mod = ((UCollectionListModel)getModel()).getMod();
		
		if (mod == 0)
		{
			if (data.size() > 0)
				return data.get(getSelectedIndex());
			return null;
		}
			
		if (getSelectedIndex() > 0)
			return data.get(getSelectedIndex() - mod);
		
		return null;
	}

	public @SuppressWarnings("rawtypes") List getObjects()
	{
		return data;
	}
	
	/** verifies the string is at least minLength but no more than maxLength in characters */
	public List<UEntryError> validateMinSelectionIndex(int minIndex)
	{
		List<UEntryError> list = UEntryError.getEmptyList(); // no errors found
		if (getSelectedIndex() < minIndex)
			list = UEntryError.getListInstance(getFieldName(), Msg.getString("EntryError-SelectOne", getFieldName()));
		
		return handleDisplay(list);
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


