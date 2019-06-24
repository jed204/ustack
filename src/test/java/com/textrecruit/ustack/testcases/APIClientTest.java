package com.textrecruit.ustack.testcases;

import com.mongodb.BasicDBList;
import com.mongodb.DBObject;
import com.textrecruit.ustack.aaa.Authentication;
import com.textrecruit.ustack.data.APIClient;
import com.textrecruit.ustack.exceptions.AccountExistsException;
import com.textrecruit.ustack.exceptions.AuthenticationException;
import com.textrecruit.ustack.exceptions.PasswordException;
import org.apache.log4j.Logger;
import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class APIClientTest extends UStackTestCaseBase {

	protected static Logger logger = Logger.getLogger(APIClientTest.class);

	public APIClientTest()
	{
		super();
	}
	
	@Test public void testAPIClientCreate() throws AccountExistsException, PasswordException
	{
		String clientId = "testapi1" + runId + UUID.randomUUID();
		APIClient ret = APIClient.createAPI("TestCase", clientId);
		assertEquals(clientId, ret.getClientId());
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
}
