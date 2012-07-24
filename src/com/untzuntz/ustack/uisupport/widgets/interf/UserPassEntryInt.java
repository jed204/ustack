package com.untzuntz.ustack.uisupport.widgets.interf;

import java.util.List;

import com.untzuntz.ustack.uisupport.UEntryError;

public interface UserPassEntryInt {

	public String getUserName();
	public String getPassword();
	public void setErrorList(List<UEntryError> errorList);
	
}
