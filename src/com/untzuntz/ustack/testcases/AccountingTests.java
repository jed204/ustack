package com.untzuntz.ustack.testcases;

import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

import junit.framework.TestCase;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.jasypt.util.text.BasicTextEncryptor;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.Mongo;
import com.untzuntz.ustack.data.MongoDB;
import com.untzuntz.ustack.data.accting.AccountTransaction;
import com.untzuntz.ustack.data.accting.AuthorizeNetFunding;
import com.untzuntz.ustack.data.accting.CreditAccount;
import com.untzuntz.ustack.data.accting.FundingConfig;
import com.untzuntz.ustack.data.accting.InsufficientFundsException;
import com.untzuntz.ustack.data.accting.Product;
import com.untzuntz.ustack.exceptions.AccountExistsException;
import com.untzuntz.ustack.exceptions.InvalidUserAccountName;
import com.untzuntz.ustack.main.ApplicationInstance;
import com.untzuntz.ustack.main.UAppCfg;

public class AccountingTests extends TestCase {

	protected static Logger logger = Logger.getLogger(AccountingTests.class);

	public AccountingTests()
	{
		BasicTextEncryptor textEncryptor = new BasicTextEncryptor();
		textEncryptor.setPassword(AuthorizeNetFunding.SB_ENC);
		
		String login = textEncryptor.encrypt("3gZrV955G7");
		String key = textEncryptor.encrypt("8W2CmG7h4Vf259dD");
		
		//MpKSItay8lSjMXbo5lvuDpOoPUY99YUGYT5arjs=

		System.setProperty(UAppCfg.AUTHNET_SANDBOX_LOGINID, login);
		System.setProperty(UAppCfg.AUTHNET_SANDBOX_TRANSACTIONKEY, key);
		System.setProperty("TestCase", "true");
	}
	
	public void testInit()
	{
		BasicConfigurator.configure();

		logger.info("L: " + System.getProperty(UAppCfg.AUTHNET_SANDBOX_LOGINID) + " / K: " + System.getProperty(UAppCfg.AUTHNET_SANDBOX_TRANSACTIONKEY));

		Mongo m = MongoDB.getMongo();
		if (!m.getAddress().sameHost("localhost")) // verify we are connected locally
		{
			logger.error("Database not hosted locally - stopping test");
			System.exit(0);
		}
		
		// output some info about the database
		DBCollection col = MongoDB.getCollection(ApplicationInstance.getAppName(), "accountTransactions");
		DB db = col.getDB();
		
		logger.info("Database Name: " + db.getName() + " (" + db.getCollectionNames().size() + " collections)");
		
		if (!"testCase".equalsIgnoreCase(db.getName()))
		{
			logger.error("Invalid database selected : please verify TestCase variable : " + db.getName());
			System.exit(0);
		}
		
		Set<String> colls = db.getCollectionNames();
		Iterator<String> it = colls.iterator();
		
		while (it.hasNext())
			logger.info("   - Collection: " + it.next());

		if (colls.size() > 0)
			db.dropDatabase();
	}
	
	public void testProduct() throws Exception
	{
		Product prod1 = Product.createProduct("TESTADD-1", "10 Credits");
		prod1.setCreditAmount(10);
		prod1.setDefaultPrice(2000);
		assertNotNull(prod1);
		prod1.save("testProduct() Test Case");

		Product prod2 = Product.createProduct("TESTSUB-1", "Transfer Cost");
		assertNotNull(prod2);
		prod2.setCreditAmount(-2);
		prod2.save("testProduct() Test Case");

		Product prod3 = Product.createProduct("TESTFOUR-1", "$4.00");
		prod3.setDefaultPrice(400);
		assertNotNull(prod3);
		prod3.save("testProduct() Test Case");

		// Try to create the same product
		try { Product.createProduct("TESTADD-1", "Bla"); fail(); } catch (AccountExistsException e) {}
		
		// Try to create an invalid product
		try { Product.createProduct("", "Bla"); fail(); } catch (InvalidUserAccountName e) {}
	}
	
	public void testCreditAccount() throws Exception
	{
		CreditAccount acct1 = CreditAccount.createAccount("Sample Account Name");
		assertNotNull(acct1);
		acct1.addFundingSource(new FundingConfig("Customer Credit Card", "com.untzuntz.ustack.data.accting.TestCaseFunding"));
		acct1.setFundingProductId("TESTADD-1");
		acct1.save("testCreditAccount() Test Case");

		// Try to create the same product
		try { CreditAccount.createAccount("Sample Account Name"); fail(); } catch (AccountExistsException e) {}
		
		// Try to create an invalid product
		try { CreditAccount.createAccount(""); fail(); } catch (InvalidUserAccountName e) {}
	}
	
	public void testCreditsTracking() throws Exception
	{
		CreditAccount acct1 = CreditAccount.getAccount("Sample Account Name");

		// Add 10 credits
		AccountTransaction tran1 = new AccountTransaction("testCreditsTracking() Test Case", "TESTADD-1");
		assertEquals(10, tran1.getCredits());
		acct1.executeTransaction("testCreditsTracking() Test Case", tran1);
		assertEquals(10, acct1.getCreditCount());
		
		AccountTransaction tran2 = new AccountTransaction("testCreditsTracking() Test Case", "TESTSUB-1");
		assertEquals(-2, tran2.getCredits());
		acct1.executeTransaction("testCreditsTracking() Test Case", tran2);
		assertEquals(8, acct1.getCreditCount());
		
		acct1.executeTransaction("testCreditsTracking() Test Case", tran2);
		assertEquals(6, acct1.getCreditCount());
		acct1.executeTransaction("testCreditsTracking() Test Case", tran2);
		assertEquals(4, acct1.getCreditCount());
		acct1.executeTransaction("testCreditsTracking() Test Case", tran2);
		assertEquals(2, acct1.getCreditCount());
		acct1.executeTransaction("testCreditsTracking() Test Case", tran2);
		assertEquals(0, acct1.getCreditCount());
		try { acct1.executeTransaction("testCreditsTracking() Test Case", tran2); fail(); } catch (InsufficientFundsException er) {}
		assertEquals(-2, acct1.getCreditCount());
		assertEquals(CreditAccount.STATUS_DISABLED, acct1.getStatus());
	}
	
	public void testPriceTracking() throws Exception
	{
		CreditAccount acct1 = CreditAccount.getAccount("Sample Account Name");

		// Add 10 credits
		AccountTransaction tran1 = new AccountTransaction("testCreditsTracking() Test Case", "TESTFOUR-1");
		assertEquals(400, tran1.getPrice());
		acct1.executeTransaction("testCreditsTracking() Test Case", tran1);
		assertEquals(2400, acct1.getPriceTotal());
		acct1.executeTransaction("testCreditsTracking() Test Case", tran1);
		assertEquals(2800, acct1.getPriceTotal());
		acct1.executeTransaction("testCreditsTracking() Test Case", tran1);
		assertEquals(3200, acct1.getPriceTotal());
	}
	
	public void testAuthorizeNet() throws Exception
	{
		CreditAccount acct1 = CreditAccount.getAccount("Sample Account Name");
		
		AuthorizeNetFunding funding = new AuthorizeNetFunding();
		funding.setCreditAccount(acct1);
		
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.YEAR, 5);
		Date expiration = cal.getTime();
		
		FundingConfig cfg = funding.createFunding("testCase", "Test Funding", "Business", acct1.getUid(), "My Company", "me@joy.com", "Mike", "Gordon", "123 Main St.", null, "State College", "Pennsylvania", "16801", "United States", "612-555-1212", null, "4111-1111-1111-1111", expiration, "000", true);
		acct1.addFundingSource(cfg);
		acct1.save("testAuthorizeNet() Test Case");
		
		funding.requestFunding("testAuthorizeNet() Test Case", null, "Test Case Add", 1000, true);
		funding.updateBillingAddress("testCase", "My Company", "Mike", "Gordon", "123 Main St.", null, "State College", "Pennsylvania", "16801", "United States", "612-555-1212", null, true);
		funding.updateCreditCardInfo("testCase", "4111-1111-1111-1111", expiration, "000", true);
		acct1.save("testAuthorizeNet() Test Case");
	}

	public void testCreditsTrackingFallback() throws Exception
	{
		CreditAccount acct1 = CreditAccount.getAccount("Sample Account Name");

		AccountTransaction tran2 = new AccountTransaction("testCreditsTracking() Test Case", "TESTSUB-1");
		assertEquals(-2, tran2.getCredits());
		
		// Transaction should try to refresh and add 10 credits
		acct1.executeTransaction("testCreditsTracking() Test Case", tran2);
		assertEquals(6, acct1.getCreditCount());
	}
}
