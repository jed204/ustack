package com.untzuntz.ustack.data.accting;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import net.authorize.Environment;
import net.authorize.Merchant;
import net.authorize.ResponseField;
import net.authorize.cim.Result;
import net.authorize.cim.Transaction;
import net.authorize.cim.TransactionType;
import net.authorize.cim.ValidationModeType;
import net.authorize.data.Customer;
import net.authorize.data.Order;
import net.authorize.data.cim.CustomerProfile;
import net.authorize.data.cim.PaymentProfile;
import net.authorize.data.cim.PaymentTransaction;
import net.authorize.data.creditcard.CreditCard;
import net.authorize.data.xml.Address;
import net.authorize.data.xml.CustomerType;
import net.authorize.data.xml.Payment;
import net.authorize.xml.Message;

import org.apache.log4j.Logger;
import org.jasypt.util.text.BasicTextEncryptor;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.untzuntz.ustack.main.UAppCfg;
import com.untzuntz.ustack.main.UOpts;

public class AuthorizeNetFunding implements FundingInt {

	private static Logger logger = Logger.getLogger(AuthorizeNetFunding.class);
	public static final String SB_ENC = "AASHFJAHK32H82CKAjbabcakuywuq2992e2SS";
	public static final String PD_ENC = "knxaakjaJDAFOASQHQNSQU9SQDUBQYXBQWKYG";
	
	protected CreditAccount acct;
	protected FundingConfig cfg;
	protected boolean testMode;
	protected String apiName;
	private String desc;
	
	public void setAPIName(String a) {
		this.apiName = a;
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
	
	public void cancelSubscription() throws Exception
	{}

	public boolean isSubscribed(String planId) throws Exception { return false; }
	
	public void subscribeTo(String planId, boolean prorate) throws Exception
	{}
	
	public String oneTime(String actor, String userIpAddress, String description, int price, 
							String cardNum, Date expDate, String cardCode, 
							String firstName, String lastName, 
							String addr1, String addr2,
							String city, String state, String zip, String country, String accountUid, boolean test)  throws Exception 
	{
		testMode = test;
		
		Merchant merchant = getMerchant(test);

		Customer customer = Customer.createCustomer();
		customer.setFirstName(firstName);
		customer.setLastName(lastName);
		String address = addr1;
		if (addr2 != null && addr2.length() > 0)
			address += " - " + addr2;
		customer.setAddress(address);
		customer.setCity(city);
		customer.setState(state);
		customer.setZipPostalCode(zip);
		customer.setCustomerId(accountUid);

		Order order = Order.createOrder();
		order.setDescription(description);
		order.setInvoiceNumber(Long.toString(System.currentTimeMillis()));
		order.setTotalAmount(BigDecimal.valueOf( price / 100.0f ));

		// create credit card
		CreditCard creditCard = CreditCard.createCreditCard();
		creditCard.setCreditCardNumber(cardNum);
		creditCard.setExpirationDate(expDate);
		creditCard.setCardCode(cardCode);
		

		if (cardNum.length() > 4)
			desc = "XXXX" + cardNum.substring( cardNum.length() - 4 ) + " (AuthNET)";
		else
			desc = "Credit Card (AuthNET)";
		
		logger.info("Attempting one-time charge [Authorize.NET] => " + order.getTotalAmount() + " // Desc [" + description + "], charge to [" + desc + "]");
		
		// create transaction
		net.authorize.aim.Transaction authCaptureTransaction = merchant.createAIMTransaction(net.authorize.TransactionType.AUTH_CAPTURE, order.getTotalAmount());
		authCaptureTransaction.setCustomer(customer);
		authCaptureTransaction.setOrder(order);
		authCaptureTransaction.setCreditCard(creditCard);

		net.authorize.aim.Result<net.authorize.aim.Transaction> result = (net.authorize.aim.Result<net.authorize.aim.Transaction>) merchant.postTransaction(authCaptureTransaction);
		
		if (!result.isApproved())
			throw new Exception(result.getResponseText() + " (" + result.getResponseCode().getCode() + ") => " + actor + " via " + userIpAddress + " for (" + description + ") and " + price + " => Info: " + firstName + "/" + lastName + "/" + zip);
		else
			logger.info("NOACCT-OT => Result Code: " + (result.getResponseCode() != null ? result.getResponseText() : "No result code") + " [OK: " + result.isApproved() + "] => " + actor + " via " + userIpAddress + " for (" + description + ") and " + price + " => Info: " + firstName + "/" + lastName + "/" + zip);
		
		if (result.getTarget() != null)
			return result.getTarget().getTransactionId();
		
		return "";
	}
	
	/**
	 * Access the Authorize.NET Interface to request funding for the provided account
	 */
	public String requestFunding(String actor, String userIpAddress, String description, int price, boolean test) throws Exception 
	{
		if (price == 0)
			return "NOTRANSACTION";
		
		testMode = test;
		
		Merchant merchant = getMerchant(test);
		
		Order order = Order.createOrder();
		order.setDescription(description);
		order.setInvoiceNumber(Long.toString(System.currentTimeMillis()));
		order.setTotalAmount(BigDecimal.valueOf( price / 100.0f ));
		
		PaymentTransaction paymentTransaction = PaymentTransaction.createPaymentTransaction();
		paymentTransaction.setOrder(order);
		paymentTransaction.setCardCode(null);
		
		net.authorize.cim.Transaction transaction = merchant.createCIMTransaction(TransactionType.CREATE_CUSTOMER_PROFILE_TRANSACTION);
		transaction.setRefId(acct.getUid());
		transaction.setCustomerProfileId(cfg.getString("customerProfileId"));
		transaction.setCustomerPaymentProfileId(cfg.getString("customerPaymentProfileId"));
		paymentTransaction.setTransactionType(net.authorize.TransactionType.AUTH_CAPTURE);
		transaction.setPaymentTransaction(paymentTransaction);
		if (userIpAddress != null)	
			transaction.addExtraOption("ip_address", userIpAddress);

		Result<Transaction> result = (Result<Transaction>)merchant.postTransaction(transaction);
		printMessages(result);
		
		if (!result.isOk())
		{
			Message msg = result.getMessages().get(0);
			throw new Exception(msg.getText() + " (" + msg.getCode() + ")");
		}
		
		String transactionId = result.getDirectResponseList().get(0).getDirectResponseMap().get(ResponseField.TRANSACTION_ID);
		return transactionId;
	}
	
	private Result<Transaction> lastResult;
	
	/** Prints the Resulting Messages and status to the logger */
	private void printMessages(Result<Transaction> result)
	{
		lastResult = result;
		
		logger.info(acct.getUid() + " => Result Code: " + (result.getResultCode() != null ? result.getResultCode() : "No result code") + " [OK: " + result.isOk() + "]");
		if(result.getCustomerProfileId() != null){
			logger.info(acct.getUid() + " => Result customerProfile Id: " + result.getCustomerProfileId());
		}
		for(int i = 0; i < result.getMessages().size(); i++){
			Message message = (Message)result.getMessages().get(i);
			logger.info(acct.getUid() + " => " + message.getCode() + " - " + message.getText() + " // " + message.getResultCode());
		}
	}
	
	public List<String> getErrors()
	{
		List<String> errors = new Vector<String>();
		
		for(int i = 0; lastResult != null && i < lastResult.getMessages().size(); i++){
			Message message = (Message)lastResult.getMessages().get(i);
			errors.add(message.getCode() + " - " + message.getText() + " // " + message.getResultCode());
		}
		
		return errors;
	}
	
	public DBObject getFundingData()
	{
		DBObject ret = new BasicDBObject();
		
		Merchant merchant = getMerchant(testMode);

		/*
		 * Get Existing Profile
		 */
		net.authorize.cim.Transaction transaction = merchant.createCIMTransaction(TransactionType.GET_CUSTOMER_PROFILE);
		transaction.setCustomerProfileId(cfg.getString("customerProfileId"));

		Result<Transaction> result = (Result<Transaction>)merchant.postTransaction(transaction);
		printMessages(result);

		CustomerProfile custProfile = result.getCustomerProfile();
		if (custProfile != null)
			ret.put("email", custProfile.getEmail());
		else
			logger.error("Failed to load customer profile by id : " + cfg.getString("customerProfileId"));
		
		/*
		 * Get Existing Profile
		 */
		net.authorize.cim.Transaction transaction2 = merchant.createCIMTransaction(TransactionType.GET_CUSTOMER_PAYMENT_PROFILE);
		transaction2.setCustomerProfileId(cfg.getString("customerProfileId"));
		transaction2.setCustomerPaymentProfileId(cfg.getString("customerPaymentProfileId"));

		Result<Transaction> result2 = (Result<Transaction>)merchant.postTransaction(transaction2);
		printMessages(result);

	    PaymentProfile profile = result2.getCustomerPaymentProfile();
	    if (profile == null)
	    {
	    	logger.error("Failed to load payment profile by id : [" + cfg.getString("customerProfileId") + "/" + cfg.getString("customerPaymentProfileId") + "]");
	    	return ret;
	    }

	    Address billingInfo = profile.getBillTo();
	    
	    ret.put("address1", billingInfo.getAddress());
	    ret.put("city", billingInfo.getCity());
	    ret.put("state", billingInfo.getState());
	    ret.put("postalCode", billingInfo.getZipPostalCode());
	    ret.put("country", billingInfo.getCountry());
	    ret.put("telephoneNumber", billingInfo.getPhoneNumber());	    

	    if (profile.getCustomerType().equals(CustomerType.INDIVIDUAL))
	    	ret.put("type", "Individual");
	    else
	    	ret.put("type", "Business");
	    
	    ret.put("firstName", billingInfo.getFirstName());
	    ret.put("lastName", billingInfo.getLastName());
	    ret.put("companyName", billingInfo.getCompany());

	    ret.put("displayCardNum", cfg.getString("displayCardNum"));
	    ret.put("expirationDate", "XXXX");
	    ret.put("ccv", "000");
	    
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
		testMode = test;
		Merchant merchant = getMerchant(test);

		SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM");
		
		Address billingInfo = (Address) Address.createAddress();
		billingInfo.setFirstName(firstName);
		billingInfo.setLastName(lastName);
		billingInfo.setCompany(company);
		
		String address = addr1;
		if (addr2 != null && addr2.length() > 0)
			address += " - " + addr2;
		billingInfo.setAddress(address);
		
		billingInfo.setCity(city);
		billingInfo.setState(state);
		billingInfo.setCountry(country);
		billingInfo.setZipPostalCode(zip);
		billingInfo.setPhoneNumber(phone);
		billingInfo.setFaxNumber(fax);
		
		CreditCard creditCard = CreditCard.createCreditCard();
		creditCard.setCreditCardNumber(cardNumber);
		creditCard.setExpirationDate(fmt.format(expirationDate));
		creditCard.setCardCode(ccv);
		
		CustomerProfile customerProfile = CustomerProfile.createCustomerProfile();
		customerProfile.setMerchantCustomerId(customerId);
		customerProfile.setEmail(email);
		
		PaymentProfile paymentProfile = PaymentProfile.createPaymentProfile();
		paymentProfile.addPayment(Payment.createPayment(creditCard));
		paymentProfile.setBillTo(billingInfo);
		if ("Individual".equalsIgnoreCase(customerType))
		{
			customerProfile.setDescription(name + " - " + firstName + " " + lastName + " Credit Account");
			paymentProfile.setCustomerType(CustomerType.INDIVIDUAL);
		}
		else
		{
			customerProfile.setDescription(name + " - " + company + " Credit Account");
			paymentProfile.setCustomerType(CustomerType.BUSINESS);
		}
		
		net.authorize.cim.Transaction transaction = merchant.createCIMTransaction(TransactionType.CREATE_CUSTOMER_PROFILE);
		transaction.setRefId(acct.getUid());
		transaction.setCustomerProfile(customerProfile);
		transaction.addPaymentProfile(paymentProfile);
		if (test)
			transaction.setValidationMode(ValidationModeType.TEST_MODE);
		else
			transaction.setValidationMode(ValidationModeType.LIVE_MODE);

		Result<Transaction> result = (Result<Transaction>)merchant.postTransaction(transaction);
		printMessages(result);

		if (!result.isOk())
		{
			Message msg = result.getMessages().get(0);
			throw new Exception(msg.getText() + " (" + msg.getCode() + ")");
		}
		
		cfg = new FundingConfig(name, "com.untzuntz.ustack.data.accting.AuthorizeNetFunding");
		cfg.put("customerProfileId", result.getCustomerProfileId());
		cfg.put("customerType", customerType);
		cfg.put("fundingType", "Credit Card (ANET)");

		// Mask cc && exp data and store on the cfg
		if (cardNumber.length() > 4)
		{
			cardNumber = "XXXX" + cardNumber.substring( cardNumber.length() - 4 );
			cfg.put("displayCardNum", cardNumber);
			cfg.setName(cfg.getName() + " - " + cardNumber);
		}

		List<String> paymentProfiles = result.getCustomerPaymentProfileIdList();
		if (paymentProfiles.size() > 0)
			cfg.put("customerPaymentProfileId", paymentProfiles.get(0));
		
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
		
		testMode = test;
		Merchant merchant = getMerchant(test);

		SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM");

		/*
		 * Get Existing Profile
		 */
		net.authorize.cim.Transaction transaction = merchant.createCIMTransaction(TransactionType.GET_CUSTOMER_PAYMENT_PROFILE);
		transaction.setCustomerProfileId(cfg.getString("customerProfileId"));
		transaction.setCustomerPaymentProfileId(cfg.getString("customerPaymentProfileId"));

		Result<Transaction> result = (Result<Transaction>)merchant.postTransaction(transaction);
		printMessages(result);

	    PaymentProfile profile = result.getCustomerPaymentProfile();

		CreditCard creditCard = CreditCard.createCreditCard();
		creditCard.setCreditCardNumber(cardNumber);
		creditCard.setExpirationDate(fmt.format(expirationDate));
		creditCard.setCardCode(ccv);
		
		profile.setPaymentList(new ArrayList<Payment>());
		profile.addPayment(Payment.createPayment(creditCard));
		
		/*
		 * Update Profile
		 */
		net.authorize.cim.Transaction updateTransaction = merchant.createCIMTransaction(TransactionType.UPDATE_CUSTOMER_PAYMENT_PROFILE);
		updateTransaction.setRefId(acct.getUid());
		updateTransaction.setCustomerProfileId(cfg.getString("customerProfileId"));
		updateTransaction.addPaymentProfile(profile);
		if (test)
			updateTransaction.setValidationMode(ValidationModeType.TEST_MODE);
		else
			updateTransaction.setValidationMode(ValidationModeType.LIVE_MODE);

		Result<Transaction> updateResult = (Result<Transaction>)merchant.postTransaction(updateTransaction);
		printMessages(updateResult);

		if (!updateResult.isOk())
		{
			Message msg = updateResult.getMessages().get(0);
			throw new Exception(msg.getText() + " (" + msg.getCode() + ")");
		}
		
		// Mask cc && exp data and store on the cfg
		if (cardNumber.length() > 4)
		{
			cardNumber = "XXXX" + cardNumber.substring( cardNumber.length() - 4 );
			cfg.put("displayCardNum", cardNumber);
		}
		
		acct.save(actor);
	}
	
	private Merchant getMerchant(boolean test)
	{
		Merchant merchant = null;
		
		BasicTextEncryptor textEncryptor = new BasicTextEncryptor();
		if (test)
			textEncryptor.setPassword(SB_ENC);
		else
			textEncryptor.setPassword(PD_ENC);
		
		if (test)
			merchant = Merchant.createMerchant(Environment.SANDBOX, textEncryptor.decrypt(UOpts.getString(UAppCfg.AUTHNET_SANDBOX_LOGINID)), textEncryptor.decrypt(UOpts.getString(UAppCfg.AUTHNET_SANDBOX_TRANSACTIONKEY)));
		else
			merchant = Merchant.createMerchant(Environment.PRODUCTION, textEncryptor.decrypt(UOpts.getString(UAppCfg.AUTHNET_PROD_LOGINID)), textEncryptor.decrypt(UOpts.getString(UAppCfg.AUTHNET_PROD_TRANSACTIONKEY)));

		return merchant;
	}

	/** 
	 * Updates the billing address for the account
	 */
	public void updateBillingAddress(String actor, String company, String firstName, String lastName, String addr1, String addr2, String city, String state, String zip, String country, String phone, String fax, boolean test) throws Exception
	{
		if (cfg == null)
			throw new Exception("Invalid call setup - no funding config");
		
		testMode = test;
		Merchant merchant = getMerchant(test);

		/*
		 * Get Existing Profile
		 */
		net.authorize.cim.Transaction transaction = merchant.createCIMTransaction(TransactionType.GET_CUSTOMER_PAYMENT_PROFILE);
		transaction.setCustomerProfileId(cfg.getString("customerProfileId"));
		transaction.setCustomerPaymentProfileId(cfg.getString("customerPaymentProfileId"));

		Result<Transaction> result = (Result<Transaction>)merchant.postTransaction(transaction);
		printMessages(result);

	    PaymentProfile profile = result.getCustomerPaymentProfile();
		
		Address billingInfo = profile.getBillTo();
		billingInfo.setFirstName(firstName);
		billingInfo.setLastName(lastName);
		billingInfo.setCompany(company);
		
		String address = addr1;
		if (addr2 != null && addr2.length() > 0)
			address += " - " + addr2;
		billingInfo.setAddress(address);
		
		billingInfo.setCity(city);
		billingInfo.setState(state);
		billingInfo.setCountry(country);
		billingInfo.setZipPostalCode(zip);
		billingInfo.setPhoneNumber(phone);
		billingInfo.setFaxNumber(fax);
		
		/*
		 * Update Profile
		 */
		net.authorize.cim.Transaction updateTransaction = merchant.createCIMTransaction(TransactionType.UPDATE_CUSTOMER_PAYMENT_PROFILE);
		updateTransaction.setRefId(acct.getUid());
		updateTransaction.setCustomerProfileId(cfg.getString("customerProfileId"));
		updateTransaction.addPaymentProfile(profile);
		if (test)
			updateTransaction.setValidationMode(ValidationModeType.TEST_MODE);
		else
			updateTransaction.setValidationMode(ValidationModeType.LIVE_MODE);

		Result<Transaction> updateResult = (Result<Transaction>)merchant.postTransaction(updateTransaction);
		printMessages(updateResult);

		if (!updateResult.isOk())
		{
			Message msg = updateResult.getMessages().get(0);
			throw new Exception(msg.getText() + " (" + msg.getCode() + ")");
		}
	}

}
