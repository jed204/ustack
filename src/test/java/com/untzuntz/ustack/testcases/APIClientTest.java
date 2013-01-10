package com.untzuntz.ustack.testcases;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.mongodb.BasicDBList;
import com.mongodb.DBObject;
import com.untzuntz.ustack.aaa.Authentication;
import com.untzuntz.ustack.data.APIClient;
import com.untzuntz.ustack.data.UDataCache;
import com.untzuntz.ustack.exceptions.AccountExistsException;
import com.untzuntz.ustack.exceptions.AuthenticationException;
import com.untzuntz.ustack.exceptions.PasswordException;
import com.untzuntz.ustack.main.UOpts;

public class APIClientTest extends UStackTestCaseBase {

	protected static Logger logger = Logger.getLogger(APIClientTest.class);

	public APIClientTest()
	{
		super();
	}
	
	@Test public void testAPIClientCreate() throws AccountExistsException, PasswordException
	{
		APIClient ret = APIClient.createAPI("TestCase", "testapi1" + runId);
		assertEquals("testapi1" + runId, ret.getClientId());
		assertEquals(1, ret.getAPIKeys().size());
	}

	@Test public void testAPIClientAuth() throws AccountExistsException, PasswordException
	{
		
		APIClient ret = APIClient.createAPI("TestCase", "testapi1" + runId);
		ret.save("TestCase");
		
		BasicDBList kl = (BasicDBList)ret.getAPIKeys();
		String key = ret.getKey( (String)((DBObject)kl.get(0)).get("uid") );
		
		try {
			Authentication.authenticateAPI("testApi1" + runId, "BadKey");
			fail();
		} catch (AuthenticationException e) {}
		
		try {
			Authentication.authenticateAPI("testApi1" + runId, key);
			Authentication.authenticateAPI("testApi1" + runId, key);
		} catch (AuthenticationException e) {
			fail();
		}
		
	}
	
	@Test public void testCache() 
	{
		UOpts.setCacheFlag(true);
		
		String key = "apitestApi11355884578251315948fdc17b4dffffbe6538d7dac187";
		UDataCache.getInstance().set(key, 300, "Hello");
		
		assertNotNull( UDataCache.getInstance().get(key) );
		assertEquals("Hello", (String)UDataCache.getInstance().get(key));
	}

	

}
