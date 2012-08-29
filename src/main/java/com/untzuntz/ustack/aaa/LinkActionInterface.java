package com.untzuntz.ustack.aaa;

import com.mongodb.DBObject;
import com.untzuntz.ustack.data.UntzDBObject;
import com.untzuntz.ustack.data.UserAccount;

public interface LinkActionInterface {

	public void linkCreated(UntzDBObject obj, ResourceLink link);
	public void linkRemoved(UntzDBObject obj, ResourceLink link);
	public void setType(String type);
	public void setRole(String role);
	public DBObject getResourceLinkExtras(ResourceLink link);
	public void setUser(UserAccount user);
	public UserAccount getUser();
	
}
