package com.textrecruit.ustack.data.accting;

import java.util.Date;
import java.util.List;

import com.mongodb.DBObject;


public interface FundingInt {

	public void setCreditAccount(CreditAccount a);
	public void setFundingConfig(FundingConfig c);
	public void setAPIName(String a);
	
	public String getDescription();
	public DBObject getFundingData();
	
	public List<String> getErrors();
	
	public void cancelSubscription() throws Exception;
	public void subscribeTo(String planId, boolean prorate) throws Exception;
	public boolean isSubscribed(String planId) throws Exception;
	
	public String requestFunding(String actor, String userIpAddress, String description, int price, boolean test) throws Exception;
	
	public void updateBillingAddress(String actor, String company, String firstName, String lastName, 
			String addr1, String addr2, String city, String state, String zip, String country, 
			String phone, String fax, boolean test) throws Exception;
	
	public void updateCreditCardInfo(String actor, String cardNumber, Date expirationDate, String ccv, boolean test) throws Exception;
	
	public FundingConfig createFunding(String actor, String name, String customerType, String customerId, String company, 
			String email, 
			String firstName, String lastName, 
			String addr1, String addr2, 
			String city, String state, String zip, 
			String country, 
			String phone, String fax, 
			String cardNumber, Date expirationDate, String ccv, boolean test) throws Exception;

	public String oneTime(String actor, String userIpAddress, String description, int price, 
			String cardNum, Date expDate, String cardCode, 
			String firstName, String lastName, 
			String addr1, String addr2, 
			String city, String state, String zip, String country, String accountUid, boolean test) throws Exception;

}
