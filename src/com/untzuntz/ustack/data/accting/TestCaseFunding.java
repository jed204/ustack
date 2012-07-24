package com.untzuntz.ustack.data.accting;

import java.util.Date;
import java.util.List;
import java.util.Vector;

import nextapp.echo.app.Component;
import nextapp.echo.app.event.ActionListener;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class TestCaseFunding implements FundingInt {

	CreditAccount acct;
	FundingConfig cfg;
	
	public void setActionCommand(String cmd) {}
	public void setActionListener(ActionListener al) {}
	
	public String getDescription()
	{
		return "Test Case Funding";
	}
	
	public void setAPIName(String a) {}

	public void setCreditAccount(CreditAccount a)
	{
		acct = a;
	}
	
	public void setFundingConfig(FundingConfig c) 
	{
		cfg = c;
	}
	
	public DBObject getFundingData()
	{
		return new BasicDBObject();
	}
	
	public Component getFundingSetupUI(String actor, boolean test)
	{
		return null;
	}
	
	public void subscribeTo(String planId, boolean prorate) throws Exception
	{}

	/**
	 * All funding requests under '1000' are accepted, everything else fails
	 */
	public String requestFunding(String actor, String userIpAddress, String description, int price, boolean test) throws Exception {

		if (price > 1000)
			throw new Exception("Transaction result: rejected due to insufficient funding");
		
		return "SUCCESS";
	}

	public FundingConfig createFunding(String actor, String name, String customerType, String customerId, String company, String email, String firstName, String lastName, String addr1, String addr2, String city, String state, String zip, String country, String phone, String fax, String cardNumber, Date expirationDate, String ccv, boolean test) throws Exception
	{
		return null;
	}
	
	public void updateBillingAddress(String actor, String company, String firstName, String lastName, String addr1, String addr2, String city, String state, String zip, String country, String phone, String fax, boolean test) throws Exception
	{
		// stub
	}
	
	public void updateCreditCardInfo(String actor, String cardNumber, Date expirationDate, String ccv, boolean test) throws Exception
	{
		// stub
	}
	
	public List<String> getErrors()
	{
		List<String> errors = new Vector<String>();
		return errors;
	}

	public void setTestMode(boolean tm) {
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
