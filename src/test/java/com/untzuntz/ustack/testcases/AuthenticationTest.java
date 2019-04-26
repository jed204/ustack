package com.untzuntz.ustack.testcases;

import static org.junit.Assert.fail;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.untzuntz.ustack.aaa.Authentication;
import com.untzuntz.ustack.data.UserAccount;
import com.untzuntz.ustack.exceptions.AccountExistsException;
import com.untzuntz.ustack.exceptions.AuthExceptionUserLocked;
import com.untzuntz.ustack.exceptions.AuthExceptionUserPasswordMismatch;
import com.untzuntz.ustack.exceptions.AuthenticationException;
import com.untzuntz.ustack.exceptions.PasswordException;
import com.untzuntz.ustack.main.UAppCfg;
import com.untzuntz.ustack.main.UOpts;

public class AuthenticationTest extends UStackTestCaseBase {

	protected static Logger logger = Logger.getLogger(AuthenticationTest.class);

	public AuthenticationTest()
	{
		super();
		System.setProperty(UAppCfg.PASSWORD_MIN_LENGTH, "6");
	}

	/** Tests the minimium length requirement */
	@Test public void testPasswordLength()
	{
		// upon creation
		UserAccount testUser = null;
		try {
			testUser = UserAccount.createUser("testcase", "testUser1" + runId, "12345678901234567890ABC");
		} 
		catch (AccountExistsException er) { 
			fail(); 
		} catch (PasswordException er) {
			fail();
		}

		// upon later setting
		try {
			testUser.setPassword("testcase", "123");
			fail();
		} catch (PasswordException er) {}
	}

	/** Tests the ability to authenticate a user and the blocking of invalid pw */
	@Test public void testAuthentication()
	{
		// upon creation
		UserAccount testUser = null;
		try {
			testUser = UserAccount.createUser("testcase", "testUser1" + runId, "123567890");
			testUser.save("TestCase");
		} 
		catch (PasswordException er) {}
		catch (AccountExistsException er) { fail(); }
		
		try { 
			Authentication.authenticateUser(testUser.getUserName(), "123567890");
		} catch (AuthenticationException err) {
			fail();
		}

		try { 
			Authentication.authenticateUser(testUser.getUserName(), "ABCDEFGHIJ");
			fail();
		} catch (AuthenticationException err) {}
	}

	@Test public void testInvalidUser()
	{
		try { 
			Authentication.authenticateUser("INVALID USER NAME", "123567890");
			fail();
		} catch (AuthenticationException err) {
		}
	}
	
	/** Tests the ability to authenticate a user and the blocking of invalid pw - confirming with UTF-8 codes */
	@Test public void testAuthenticationNonUS()
	{
		// upon creation
		UserAccount testUser = null;
		try {
			testUser = UserAccount.createUser("testcase", "testUser5" + runId, "湘南鎌倉総合病");
			testUser.save("TestCase");
		} 
		catch (PasswordException er) {}
		catch (AccountExistsException er) { fail(); }
		
		try { 
			Authentication.authenticateUser(testUser.getUserName(), "湘南鎌倉総合病");
		} catch (AuthenticationException err) {
			fail();
		}

		try { 
			Authentication.authenticateUser(testUser.getUserName(), "合倉鎌総院湘南");
			fail();
		} catch (AuthenticationException err) {}
	}

	/** Tests the account locking on too many failed attempts */
	@Test public void testAccountLock()
	{
		System.setProperty(UAppCfg.USER_ACCOUNT_LOCKTIME_SEC, "900");

		// upon creation
		UserAccount testUser = null;
		try {
			testUser = UserAccount.createUser("testcase", "testUser2" + runId, "123567890");
			testUser.save("TestCase");
		} 
		catch (PasswordException er) {}
		catch (AccountExistsException er) { fail(); }

		// fail pw check until the error limit
		for (int i = 0; i < UOpts.getInt(UAppCfg.PASSWORD_ERROR_LIMIT); i++)
		{
			try { 
				Authentication.authenticateUser(testUser.getUserName(), "BAD PASSWORD");
				fail();
			} 
			catch (AuthExceptionUserPasswordMismatch err) {}
			catch (AuthenticationException err) { fail(); }
			
		}
		
		try { 
			Authentication.authenticateUser(testUser.getUserName(), "BAD PASSWORD");
			fail();
		} 
		catch (AuthExceptionUserLocked err) {}
		catch (AuthenticationException err) {}

		// good password should FAIL b/c we are locked
		try {
			Authentication.authenticateUser(testUser.getUserName(), "123567890");
			fail();
		}
		catch (AuthExceptionUserLocked err) {}
		catch (AuthenticationException err) {}

	}
	
	/** Tests the failed password resets after successful */
	@Test public void testFailedPasswordReset()
	{
		// upon creation
		UserAccount testUser = null;
		try {
			testUser = UserAccount.createUser("testcase", "testUser3" + runId, "123567890");
			testUser.save("TestCase");
		} 
		catch (PasswordException er) {}
		catch (AccountExistsException er) { fail(); }

		// fail pw check until the error limit
		for (int i = 0; i < UOpts.getInt(UAppCfg.PASSWORD_ERROR_LIMIT) - 1; i++)
		{
			try { 
				logger.info("Failing Password # " + (i + 1));
				Authentication.authenticateUser(testUser.getUserName(), "BAD PASSWORD");
				fail();
			} 
			catch (AuthExceptionUserPasswordMismatch err) {}
			catch (AuthenticationException err) { fail(); }
			
		}
		
		try { 
			// pass to reset
			logger.info("Passing Authentication");
			Authentication.authenticateUser(testUser.getUserName(), "123567890");
		} 
		catch (AuthenticationException err) { fail (); }
		
		// fail pw check until the error limit (again) - this would fail right away if we didn't reset
		for (int i = 0; i < UOpts.getInt(UAppCfg.PASSWORD_ERROR_LIMIT) - 1; i++)
		{
			try { 
				logger.info("Failing Password # " + (i + 1));
				Authentication.authenticateUser(testUser.getUserName(), "BAD PASSWORD");
				fail();
			} 
			catch (AuthExceptionUserPasswordMismatch err) {}
			catch (AuthenticationException err) { fail(); }
			
		}
	}
	
	
}
