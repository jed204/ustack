package com.textrecruit.ustack.aaa;

import com.mongodb.DBObject;
import com.textrecruit.ustack.data.UntzDBObject;
import com.textrecruit.ustack.data.UserAccount;

public interface LinkActionInterface {

	public void linkCreated(UntzDBObject obj, ResourceLink link);
	public void linkRemoved(UntzDBObject obj, ResourceLink link);
	public void setType(String type);
	public void setRole(String role);
	public DBObject getResourceLinkExtras(ResourceLink link);
	public void setUser(UserAccount user);
	public UserAccount getUser();
	
}
