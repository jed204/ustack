package com.textrecruit.ustack.testcases;

import com.textrecruit.ustack.data.accting.*;
import com.textrecruit.ustack.exceptions.AccountExistsException;
import com.textrecruit.ustack.exceptions.InvalidUserAccountName;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class AccountingTest extends UStackTestCaseBase {

	protected static Logger logger = Logger.getLogger(AccountingTest.class);

	public AccountingTest()
	{
		super();
	}
	
	@Before
	public void testProduct() throws Exception
	{
		Product prod1 = Product.createProduct("TESTADD-1" + runId, "10 Credits");
		prod1.setCreditAmount(10);
		prod1.setDefaultPrice(2000);
		assertNotNull(prod1);
		prod1.save("testProduct() Test Case");

		Product prod2 = Product.createProduct("TESTSUB-1" + runId, "Transfer Cost");
		assertNotNull(prod2);
		prod2.setCreditAmount(-2);
		prod2.save("testProduct() Test Case");

		Product prod3 = Product.createProduct("TESTFOUR-1" + runId, "$4.00");
		prod3.setDefaultPrice(400);
		assertNotNull(prod3);
		prod3.save("testProduct() Test Case");

		// Try to create the same product
		try { Product.createProduct("TESTADD-1" + runId, "Bla"); fail(); } catch (AccountExistsException e) {}
		
		// Try to create an invalid product
		try { Product.createProduct("", "Bla"); fail(); } catch (InvalidUserAccountName e) {}

		CreditAccount acct1 = CreditAccount.createAccount("Sample Account Name" + runId);
		assertNotNull(acct1);
		acct1.addFundingSource(new FundingConfig("Customer Credit Card", "com.textrecruit.ustack.data.accting.TestCaseFunding"));
		acct1.setFundingProductId("TESTADD-1" + runId);
		acct1.save("testCreditAccount() Test Case");
	}
	
	@Test public void testCreditsTracking() throws Exception
	{
		CreditAccount acct1 = CreditAccount.getAccount("Sample Account Name" + runId);
		assertEquals(0, acct1.getCreditCount());

		// Add 10 credits
		AccountTransaction tran1 = new AccountTransaction("testCreditsTracking() Test Case", "TESTADD-1" + runId);
		assertEquals(10, tran1.getCredits());
		acct1.executeTransaction("testCreditsTracking() Test Case", tran1);
		assertEquals(10, acct1.getCreditCount());
		
		AccountTransaction tran2 = new AccountTransaction("testCreditsTracking() Test Case", "TESTSUB-1" + runId);
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
	
	@Test public void testPriceTracking() throws Exception
	{
		CreditAccount acct1 = CreditAccount.getAccount("Sample Account Name" + runId);

		// Add 10 credits
		AccountTransaction tran1 = new AccountTransaction("testCreditsTracking() Test Case", "TESTFOUR-1" + runId);
		assertEquals(400, tran1.getPrice());
		acct1.executeTransaction("testCreditsTracking() Test Case", tran1);
		assertEquals(400, acct1.getPriceTotal());
		acct1.executeTransaction("testCreditsTracking() Test Case", tran1);
		assertEquals(800, acct1.getPriceTotal());
		acct1.executeTransaction("testCreditsTracking() Test Case", tran1);
		assertEquals(1200, acct1.getPriceTotal());
	}

}
