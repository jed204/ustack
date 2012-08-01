package com.untzuntz.ustack.data.accting;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import nextapp.echo.app.Color;
import nextapp.echo.app.Column;
import nextapp.echo.app.Component;
import nextapp.echo.app.Label;
import nextapp.echo.app.event.ActionEvent;
import nextapp.echo.app.event.ActionListener;

import org.apache.log4j.Logger;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.stripe.model.Charge;
import com.stripe.model.Customer;
import com.untzuntz.components.app.Stripe;
import com.untzuntz.ustack.data.ExternalAPIParams;
import com.untzuntz.ustack.main.Msg;
import com.untzuntz.ustack.uisupport.UButton;
import com.untzuntz.ustack.uisupport.UColumn;
import com.untzuntz.ustack.uisupport.UEntryError;
import com.untzuntz.ustack.uisupport.UErrorColumn;
import com.untzuntz.ustack.uisupport.UGrid;
import com.untzuntz.ustack.uisupport.ULabel;
import com.untzuntz.ustack.uisupport.URow;
import com.untzuntz.ustack.uisupport.UStackStatics;
import com.untzuntz.ustack.uisupport.UTextField;

import echopoint.Strut;

public class StripeFunding implements FundingInt,ActionListener {

	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(StripeFunding.class);
	
	private CreditAccount acct;
	private FundingConfig cfg;
	private boolean testMode;
	private ActionListener parent;
	private String actionCommand;
	private String desc;
	private String apiName;
	private ExternalAPIParams stripeParams;
	
	public void setAPIName(String a) {
		this.apiName = a;
		this.stripeParams = ExternalAPIParams.getByName(a);
		
		logger.info("API Name [" + a + "]");
	}

	public void setActionCommand(String cmd) {
		actionCommand = cmd;
	}
	
	public void setActionListener(ActionListener al) {
		parent = al;
	}
	
	public void setTestMode(boolean tm)
	{
		testMode = tm;
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

	private String uiActor;
	private UTextField name;
	private Stripe stripe;
	private Column stripeArea;
	private UErrorColumn errors;
	public Component getFundingSetupUI(String actor, boolean test)
	{
		testMode = test;
		
		uiActor = actor;
		Column leftCol = new UColumn();

		leftCol.add(new ULabel("Credit Card (Stripe.com)", UStackStatics.FONT_BOLD_LARGE));

		if (stripeParams == null)
		{
			UColumn er = new UColumn();
			er.setBackground(Color.RED);
			er.setForeground(Color.WHITE);
			
			er.add(new Label("Unable to load API key for '" + apiName + "'"));
			
			leftCol.add(er);
			return leftCol;
		}
		
		
		/** Basic Info UI */
		leftCol.add(new Strut(0, 5));
		leftCol.add(new ULabel(Msg.getString("BasicInfo"), UStackStatics.FONT_BOLD_MED));

		UGrid ccgrid = new UGrid(2, UStackStatics.IN_5);
		leftCol.add(ccgrid);
		
		ccgrid.add("Funding Name");
		ccgrid.add(name = new UTextField(""));

		
		/** Credit Card Info UI */
		leftCol.add(new Strut(0, 5));
		leftCol.add(stripeArea = new UColumn());
		stripeArea.add(stripe = new Stripe(stripeParams.getPublicKey(), stripeParams.getPrivateKey()));	
		
		URow acts = new URow();
		acts.add(new UButton("Save", UStackStatics.WEB_BUTTON, this, "save"));
		leftCol.add(acts);
		
		leftCol.add(errors = new UErrorColumn());
		
		if (cfg != null)
		{
			name.setText(cfg.getName());
			ccgrid.add(new Label("Stripe.com Customer ID: "));
			ccgrid.add(new Label(cfg.getString("customerId")));

			try {
				Customer cust = Customer.retrieve(cfg.getString("customerId"), stripe.getStripePrivateAPIKey());

				if (cust.getActiveCard() != null)
				{
					ccgrid.add(new Label("Last 4 card digits:"));
					URow row = new URow();
					row.add(new Label(cust.getActiveCard().getLast4()));
					row.add(new UButton("Edit Card", UStackStatics.COLOR_WEB_LINK, this, "editCard"));
					ccgrid.add(row);
					ccgrid.add(new Label("Card Expiration Date: "));
					ccgrid.add(new Label(cust.getActiveCard().getExpMonth() + "/" + cust.getActiveCard().getExpYear()));
				}
				else
					ccgrid.add(new Label("No active credit card"));
				
			} catch (Exception er) {
				logger.warn("Failed to get customer from Stripe", er);
			}
			stripe.setVisible(false);
		}
		
		return leftCol;
	}
	
	private void editCard()
	{
		stripe.setVisible(true);
	}
	
	private void saveFunding()
	{
		List<UEntryError> errorList = UEntryError.getEmptyList();
		errorList.addAll( name.validateStringLen(1, 255));
		
		errors.setErrorList(errorList);
		if (errorList.size() > 0)
			return;
		
		if (cfg == null)
		{
			try {
				createFunding(uiActor, name.getText(), null, acct.getUid(), null, 
						null, 
						null, null,
						null, null, 
						null, null, null,
						null, null, null,
						stripe.getTransactionToken(), null, null, testMode);
				
				acct.addFundingSource(cfg);
				acct.save(uiActor);
				
			} catch (Exception err) {
				logger.error("Failed to create funding object", err);
				errorList.add(UEntryError.getInstance("Create Failed", "Failed to create profile - " + err.getMessage()));
			}
			
		}
		else if (stripe.isVisible() && stripe.getTransactionToken() != null)
		{
			try {
				// do updates
				if (stripe.getTransactionToken() != null)
					updateCreditCardInfo(uiActor, stripe.getTransactionToken(), null, null, testMode);
				
			} catch (Exception err) {
				logger.error("Failed to update funding object", err);
				errorList.add(UEntryError.getInstance("Update Failed", "Failed to update profile - " + err.getMessage()));
			}
		}
		cfg.setName(name.getText());
		errors.setErrorList(errorList);
		
		if (errorList.size() == 0)
		{
			ActionEvent ae = new ActionEvent(this, actionCommand);
			parent.actionPerformed(ae);
		}
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
		String description = name + " - " + customerId;
		if (lastName != null)
			description = lastName + " - " + customerId;
		if (company != null && company.length() > 0)
			description = company + " - " + customerId;
		
		Map<String, Object> customerParams = new HashMap<String, Object>();
		customerParams.put("email", email);
		customerParams.put("description", description);
		customerParams.put("card", cardNumber); // obtained with Stripe.js
		
		Customer cust = Customer.create(customerParams, stripeParams.getPrivateKey());
		logger.info("Creating customer [" + description + "] => Token: " + cardNumber + " => Cust ID: " + cust.getId());
		
		cfg = new FundingConfig(name, "com.untzuntz.ustack.data.accting.StripeFunding");
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
	
	public void actionPerformed(ActionEvent e) {

		if ("save".equalsIgnoreCase(e.getActionCommand()))
			saveFunding();
		else if ("editCard".equalsIgnoreCase(e.getActionCommand()))
			editCard();
	}

		
}
