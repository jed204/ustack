package com.textrecruit.ustack.data.accting;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.stripe.model.Charge;
import com.stripe.model.Customer;
import com.stripe.model.Subscription;
import com.textrecruit.ustack.data.ExternalAPIParams;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.util.*;

public class StripeFunding implements FundingInt {

	private static Logger logger = Logger.getLogger(StripeFunding.class);
	
	protected CreditAccount acct;
	protected FundingConfig cfg;
	protected boolean testMode;
	protected String apiName;
	protected ExternalAPIParams stripeParams;
	private String desc;
	
	public void setAPIName(String a) {
		this.apiName = a;
		this.stripeParams = ExternalAPIParams.getByName(a);
		
		logger.info("API Name [" + a + "]");
	}

	public String getDescription()
	{
		if (cfg != null)
			return cfg.getString("fundingType") + " - " + cfg.getString("displayCardNum");
		
		return desc;
	}
	
	public void setCreditAccount(CreditAccount a)
	{
		acct = a;
	}
	
	public void setFundingConfig(FundingConfig c) 
	{
		cfg = c;
	}

	public FundingConfig getFundingConfig()
	{
		return cfg;
	}
	
	/**
	 * One-time charge to Stripe.com for processing
	 */
	public String oneTime(String actor, String userIpAddress, String description, int price, 
			String cardNum, Date expDate, String cardCode, 
			String firstName, String lastName, 
			String addr1, String addr2, 
			String city, String state, String zip, String country, String accountUid, boolean test)  throws Exception 
	{		
		Map<String, Object> defaultCardParams = new HashMap<String, Object>();
		Map<String, Object> defaultChargeParams = new HashMap<String, Object>();
		Map<String, Object> defaultCustomerParams = new HashMap<String, Object>();

		defaultCardParams.put("number", cardNum);
		
		if (!cardNum.startsWith("tok_"))
		{
			Calendar cal = Calendar.getInstance();
			cal.setTime(expDate);
			defaultCardParams.put("exp_month", cal.get(Calendar.MONTH) + 1);
			defaultCardParams.put("exp_year", cal.get(Calendar.YEAR) + 1900);
			
			defaultCardParams.put("cvc", cardCode);
			defaultCardParams.put("name", firstName + " " + lastName);
			defaultCardParams.put("address_line1", addr1);
			defaultCardParams.put("address_line2", addr2);
			defaultCardParams.put("address_city", city);
			defaultCardParams.put("address_state", state);
			defaultCardParams.put("address_zip", zip);
			defaultCardParams.put("address_country", country);
		}
		
		defaultChargeParams.put("amount", BigDecimal.valueOf( price / 100.0f ));
		defaultChargeParams.put("currency", "usd");
		defaultChargeParams.put("card", defaultCardParams);

		defaultCustomerParams.put("card", defaultCardParams);
		defaultCustomerParams.put("description", description);

		Charge createdCharge = Charge.create(defaultChargeParams, stripeParams.getPrivateKey());
		return createdCharge.getId();
	}

	/**
	 * Access the Stripe.com Interface to request funding for the provided account
	 */
	public String requestFunding(String actor, String userIpAddress, String description, int price, boolean test) throws Exception 
	{
		Map<String, Object> chargeParams = new HashMap<String, Object>();
		chargeParams.put("description", description);
		chargeParams.put("customer", cfg.getString("customerId")); // obtained with Stripe.js
		chargeParams.put("currency", "usd");
		chargeParams.put("amount", price);
		Charge charge = Charge.create(chargeParams, stripeParams.getPrivateKey());		
		
		return charge.getId();
	}
	
	public List<String> getErrors()
	{
		return new Vector<String>();
	}
	
	public void cancelSubscription() throws Exception
	{
		Customer cust = Customer.retrieve(cfg.getString("customerId"), stripeParams.getPrivateKey());
		cust.cancelSubscription(stripeParams.getPrivateKey());
	}
	
	public boolean isSubscribed(String planId) throws Exception
	{
		Customer cust = Customer.retrieve(cfg.getString("customerId"), stripeParams.getPrivateKey());
		
		Subscription sub = cust.getSubscription();
		if (sub == null)
			return false;

		if (sub.getPlan().getId().equalsIgnoreCase(planId))
			return true;
		
		return false;
	}
	
	public void subscribeTo(String planId, boolean prorate) throws Exception
	{
		Customer cust = Customer.retrieve(cfg.getString("customerId"), stripeParams.getPrivateKey());
		
		Map<String, Object> subscriptionParams = new HashMap<String, Object>();
		subscriptionParams.put("plan", planId);
		subscriptionParams.put("prorate", prorate + "");
		cust.updateSubscription(subscriptionParams, stripeParams.getPrivateKey());
		
		logger.info("Subscribed '" + cust.getId() + "' to new plan => " + planId);
	}
	
	public DBObject getFundingData()
	{
		DBObject ret = new BasicDBObject();
		
		try {
			Customer cust = Customer.retrieve(cfg.getString("customerId"), stripeParams.getPrivateKey());
			if (cust != null)
			{
				logger.info("Got customer object for cust id [" + cfg.getString("customerId") + "] => " + cust.toString());
				if (cust.getActiveCard() != null)
				{
					ret.put("last4", cust.getActiveCard().getLast4());
					ret.put("expires", cust.getActiveCard().getExpMonth() + "/" + cust.getActiveCard().getExpYear());
				}
				
				if (cust.getSubscription() != null)
				{
					ret.put("subscriptionStatus", cust.getSubscription().getStatus());
					ret.put("subscriptionCurrentStart", cust.getSubscription().getCurrentPeriodStart());
					ret.put("subscriptionCurrentEnd", cust.getSubscription().getCurrentPeriodEnd());
					ret.put("subscriptionStart", cust.getSubscription().getStart());
					
					if (cust.getSubscription().getCancelAtPeriodEnd())
						ret.put("subscriptionCanceledAt", cust.getSubscription().getCanceledAt());
					
					if (cust.getSubscription().getPlan() != null)
					{
						ret.put("planId", cust.getSubscription().getPlan().getId());
						ret.put("planName", cust.getSubscription().getPlan().getName());
					}
				}
				
				if (cust.getNextRecurringCharge() != null)
				{
					ret.put("nextChargeAmount", cust.getNextRecurringCharge().getAmount());
					ret.put("nextChargeDate", cust.getNextRecurringCharge().getDate());
				}
			}
			
		} catch (Exception er) {
			logger.error("Failed to get customer from stripe", er);
		}
		
		return ret;
	}

	/** Create a new funding object */
	public FundingConfig createFunding(String actor, String name, String customerType, String customerId, String company, 
			String email, 
			String firstName, String lastName, 
			String addr1, String addr2, 
			String city, String state, String zip, 
			String country, 
			String phone, String fax, 
			String cardNumber, Date expirationDate, String ccv, boolean test) throws Exception
	{		
		Map<String, Object> customerParams = new HashMap<String, Object>();
		customerParams.put("id", customerId);
		customerParams.put("email", email);
		customerParams.put("description", name);
		customerParams.put("card", cardNumber); // obtained with Stripe.js
		
		Customer cust = Customer.create(customerParams, stripeParams.getPrivateKey());
		logger.info("Creating customer [" + customerId + "] => Token: " + cardNumber + " => Cust ID: " + cust.getId());
		
		cfg = new FundingConfig(name, "com.textrecruit.ustack.data.accting.StripeFunding");
		cfg.put("customerId", cust.getId());
		cfg.put("fundingType", "Credit Card (Stripe.com)");
		cfg.put("displayCardNum", "XXXX");
		cfg.setName(cfg.getName());
		
		return cfg;
	}

	/**
	 * Updates the Credit Card Info
	 * 
	 * @param actor
	 * @param cardNumber
	 * @param expirationDate
	 * @param ccv
	 * @param test
	 * @throws Exception
	 */
	public void updateCreditCardInfo(String actor, String cardNumber, Date expirationDate, String ccv, boolean test) throws Exception
	{
		if (cfg == null || acct == null)
			throw new Exception("Invalid call setup - no funding config");
		
		logger.info("Updating Card for Cust [" + cfg.getString("customerId") + "] => Token: " + cardNumber);
		Customer cust = Customer.retrieve(cfg.getString("customerId"), stripeParams.getPrivateKey());
		
		Map<String, Object> customerParams = new HashMap<String, Object>();
		customerParams.put("card", cardNumber); // obtained with Stripe.js
		cust.update(customerParams, stripeParams.getPrivateKey());
	}
	
	/** 
	 * Updates the billing address for the account
	 */
	public void updateBillingAddress(String actor, String company, String firstName, String lastName, String addr1, String addr2, String city, String state, String zip, String country, String phone, String fax, boolean test) throws Exception
	{
		if (cfg == null)
			throw new Exception("Invalid call setup - no funding config");
		
		// Stripe doesn't need to track this info
	}

		
}
