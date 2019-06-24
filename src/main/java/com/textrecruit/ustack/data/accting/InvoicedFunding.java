package com.textrecruit.ustack.data.accting;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.textrecruit.ustack.main.UNotificationSvc;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Vector;

public class InvoicedFunding implements FundingInt {

	protected CreditAccount acct;
	protected FundingConfig cfg;
	protected boolean testMode;

	public void setAPIName(String a) {}
	
	public String getDescription()
	{
		return "Invoice Funding";
	}
	
	public void setCreditAccount(CreditAccount a)
	{
		acct = a;
	}
	
	public void setFundingConfig(FundingConfig c) 
	{
		cfg = c;
	}
	public void cancelSubscription() throws Exception
	{}
	
	public boolean isSubscribed(String planId) throws Exception { return false; }

	public void subscribeTo(String planId, boolean prorate) throws Exception
	{}

	public List<String> getErrors()
	{
		List<String> errors = new Vector<String>();
		return errors;
	}
	
	@SuppressWarnings("unchecked")
	public DBObject getFundingData()
	{
		DBObject ret = new BasicDBObject();
		ret.putAll((Map<String,Object>)cfg.toMap());
		return ret;
	}
	

	public String requestFunding(String actor, String userIpAddress, String description, int price, boolean test) throws Exception {

		UNotificationSvc svc = new UNotificationSvc();
		svc.setData("creditAccount", acct);
		svc.notify("ustack.invoiceRefreshNeeded", null);
		acct.save(actor);

		return "Pending";
	}

	public FundingConfig createFunding(String actor, String name, String customerType, String customerId, String company, String email, String firstName, 
			String lastName, String addr1, String addr2, String city, String state, String zip, String country, String phone, String fax, 
			String cardNumber, Date expirationDate, String ccv, boolean test) throws Exception
	{
		cfg = new FundingConfig(name, "com.textrecruit.ustack.data.accting.InvoicedFunding");
		cfg.put("fundingType", "Invoice (Paper)");
		cfg.put("customerId", customerId);
		cfg.put("companyName", company);
		cfg.put("userEmail", email);
		cfg.put("firstName", firstName);
		cfg.put("lastName", lastName);
		cfg.put("address1", addr1);
		cfg.put("address2", addr2);
		cfg.put("city", city);
		cfg.put("state", state);
		cfg.put("postalCode", zip);
		cfg.put("country", country);
		cfg.put("phone", phone);
		
		return cfg;
	}
	
	public void updateBillingAddress(String actor, String company, String firstName, String lastName, String addr1, String addr2, String city, String state, String zip, String country, String phone, String fax, boolean test) throws Exception
	{
		cfg.put("companyName", company);
		cfg.put("firstName", firstName);
		cfg.put("lastName", lastName);
		cfg.put("address1", addr1);
		cfg.put("address2", addr2);
		cfg.put("city", city);
		cfg.put("state", state);
		cfg.put("postalCode", zip);
		cfg.put("country", country);
		cfg.put("phone", phone);
	}
	
	public void updateCreditCardInfo(String actor, String cardNumber, Date expirationDate, String ccv, boolean test) throws Exception
	{
		// stub
	}

	public String oneTime(String actor, String userIpAddress, String description, int price, 
			String cardNum, Date expDate, String cardCode, 
			String firstName, String lastName, 
			String addr1, String addr2, 
			String city, String state, String zip, String country, String customerId, boolean test)  throws Exception 
	{
		return "";
	}

}
