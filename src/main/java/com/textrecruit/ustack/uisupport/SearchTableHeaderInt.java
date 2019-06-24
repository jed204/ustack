package com.textrecruit.ustack.uisupport;

import java.util.Hashtable;

public interface SearchTableHeaderInt {

	Hashtable<String,FieldValueMap> getFields();
	public int getSortDirectionInt();
	public String getSortField();
	
}
