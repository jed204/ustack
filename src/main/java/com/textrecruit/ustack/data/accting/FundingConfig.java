package com.textrecruit.ustack.data.accting;

import java.util.Date;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class FundingConfig extends BasicDBObject implements DBObject {
	
	private static final long serialVersionUID = 1L;
	
	public FundingConfig(DBObject obj)
	{
		putAll(obj);
	}
	
	public FundingConfig(String name, String cls) {
		setFundingActorClass(cls);
		setName(name);
		put("created", new Date());
	}

	public String getName() { 
		return getString("name");
	}
	
	public void setName(String name) {
		put("name", name);
	}
	
	public String getFundingActorClass() { 
		return getString("fundingActorClass");
	}
	
	public String getFundingActorClassUI() { 
		return getString("fundingActorClass") + "UI";
	}
	
	public void setFundingActorClass(String cls) {
		put("fundingActorClass", cls);
	}
	
	public static FundingInt getFundingInstance(String cls)
	{
		FundingInt ret = null;
		
		try {
			ret = (FundingInt)FundingConfig.class.getClassLoader().loadClass(cls).newInstance();
		} catch (Exception er) {}
		
		return ret;
	}

}
