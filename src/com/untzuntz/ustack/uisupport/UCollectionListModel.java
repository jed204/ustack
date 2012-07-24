package com.untzuntz.ustack.uisupport;

import java.util.List;

import nextapp.echo.app.list.AbstractListModel;

import com.untzuntz.ustack.main.Msg;

public class UCollectionListModel extends AbstractListModel {

	private static final long serialVersionUID = -7683395244541083770L;
	private @SuppressWarnings("rawtypes") List data;
	private boolean showNA;
	private boolean showSO;
	private int mod;
	
	public int getMod() {
		return mod;
	}
	
	public void setShowNA(boolean sna) { 
		showNA = sna;
		mod = 0;
		if (showSO || showNA)
			mod = 1;
	}
	public void setShowSelOne(boolean sna) {
		showSO = sna;
		mod = 0;
		if (showSO || showNA)
			mod = 1;
	}
	
	public UCollectionListModel(@SuppressWarnings("rawtypes") List newData) {
		data = newData;
		mod = 1;
	}
	
	public UCollectionListModel(@SuppressWarnings("rawtypes") List newData, boolean sna) {
		data = newData;
		showNA = sna;
		
		mod = 0;
		if (showSO || showNA)
			mod = 1;
	}
	
	public UCollectionListModel(@SuppressWarnings("rawtypes") List newData, boolean sna, boolean sno) {
		data = newData;
		showNA = sna;
		showSO = sno;
		
		mod = 0;
		if (showSO || showNA)
			mod = 1;
	}
	
	public @SuppressWarnings("rawtypes") List getData() { return data; }
	
	public Object get(int index) {
		
    	if ((index == 0 || data == null) && mod > 0)
    	{
    		if (showNA)
        		return Msg.getString("NA");
    		else
    			return Msg.getString("SelectOne");
    	}
    	
    	if (data == null)
    		return "";
    	
    	Object o = data.get(index - mod);
    	return o.toString();
	}

	public int size() {
		
		if (data == null)
			return 0;
		
		return data.size() + mod;
	}
	
}
