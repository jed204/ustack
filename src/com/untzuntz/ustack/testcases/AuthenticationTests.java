package com.untzuntz.ustack.testcases;

import java.util.Iterator;
import java.util.Set;

import junit.framework.TestCase;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.Mongo;
import com.untzuntz.ustack.aaa.Authentication;
import com.untzuntz.ustack.data.MongoDB;
import com.untzuntz.ustack.data.UserAccount;
import com.untzuntz.ustack.exceptions.AccountExistsException;
import com.untzuntz.ustack.exceptions.AuthExceptionUserLocked;
import com.untzuntz.ustack.exceptions.AuthExceptionUserPasswordMismatch;
import com.untzuntz.ustack.exceptions.AuthenticationException;
import com.untzuntz.ustack.exceptions.PasswordException;
import com.untzuntz.ustack.main.ApplicationInstance;
import com.untzuntz.ustack.main.UAppCfg;
import com.untzuntz.ustack.main.UOpts;

public class AuthenticationTests extends TestCase {

	protected static Logger logger = Logger.getLogger(AuthenticationTests.class);

	public AuthenticationTests()
	{
		System.setProperty("UAppCfg.CacheHost", "localhost:1121");
		System.setProperty("TestCase", "true");
	}
	
	public void testInit()
	{
		BasicConfigurator.configure();
		
		Mongo m = MongoDB.getMongo();
		if (!m.getAddress().sameHost("localhost")) // verify we are connected locally
		{
			logger.error("Database not hosted locally - stopping test");
			System.exit(0);
		}
		
		// output some info about the database
		DBCollection col = MongoDB.getCollection(ApplicationInstance.getAppName(), "users");
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

	/** Tests the minimium length requirement */
	public void testPasswordLength()
	{
		// upon creation
		UserAccount testUser = null;
		try {
			testUser = UserAccount.createUser("testcase", "testUser1", "123");
			fail();
		} 
		catch (PasswordException er) {}
		catch (AccountExistsException er) { fail(); }
		

		try {
			testUser = UserAccount.createUser("testcase", "testUser1", "12345678901234567890ABC");
		} 
		catch (AccountExistsException er) { fail(); }
		catch (PasswordException er) {
			fail();
		}

		// upon later setting
		try {
			testUser.setPassword("testcase", "123");
			fail();
		} catch (PasswordException er) {}
	}

	/** Tests the ability to authenticate a user and the blocking of invalid pw */
	public void testAuthentication()
	{
		// upon creation
		UserAccount testUser = null;
		try {
			testUser = UserAccount.createUser("testcase", "testUser1", "123567890");
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

	public void testInvalidUser()
	{
		try { 
			Authentication.authenticateUser("INVALID USER NAME", "123567890");
			fail();
		} catch (AuthenticationException err) {
		}
	}
	
	/** Tests the account locking on too many failed attempts */
	public void testAccountLock()
	{
		// upon creation
		UserAccount testUser = null;
		try {
			testUser = UserAccount.createUser("testcase", "testUser2", "123567890");
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
	}
	
	/** Tests the failed password resets after successful */
	public void testFailedPasswordReset()
	{
		// upon creation
		UserAccount testUser = null;
		try {
			testUser = UserAccount.createUser("testcase", "testUser3", "123567890");
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
