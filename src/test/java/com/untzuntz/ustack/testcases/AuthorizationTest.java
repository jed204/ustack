package com.untzuntz.ustack.testcases;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBObject;
import com.untzuntz.ustack.aaa.Authorization;
import com.untzuntz.ustack.aaa.ResourceDefinition;
import com.untzuntz.ustack.aaa.ResourceLink;
import com.untzuntz.ustack.aaa.RoleDefinition;
import com.untzuntz.ustack.aaa.UBasePermission;
import com.untzuntz.ustack.data.TermsConditions;
import com.untzuntz.ustack.data.UserAccount;
import com.untzuntz.ustack.exceptions.AccountExistsException;
import com.untzuntz.ustack.exceptions.AuthorizationException;
import com.untzuntz.ustack.exceptions.ObjectExistsException;
import com.untzuntz.ustack.exceptions.PasswordException;
import com.untzuntz.ustack.main.UOpts;

public class AuthorizationTest extends UStackTestCaseBase {

	protected static Logger logger = Logger.getLogger(AuthorizationTest.class);

	public AuthorizationTest()
	{
		super();
	}
	
	/** Verify basic functionality of the ResourceDefinition object */
	@Test public void testResourceDefBasics()
	{
		// verify we can create a resource
		try {
			ResourceDefinition.createResource("testRes1" + runId, ResourceDefinition.TYPE_USERACCESS).save("testCase");
		} catch (ObjectExistsException err) {
			fail();
		}

		// verify we can't duplicate resources
		try {
			ResourceDefinition.createResource("testRes1" + runId, ResourceDefinition.TYPE_USERACCESS).save("testCase");
			fail();
		} catch (ObjectExistsException err) {
		}

		// verify we can get the resource back out
		ResourceDefinition res = ResourceDefinition.getByName("testRes1" + runId);
		assertNotNull(res);
		assertEquals("testRes1" + runId, res.getName());
		res.delete();
	}

	/** Tests basic functionality of roles within a resource */
	@Test public void testResourceDefRoles() throws ObjectExistsException,PasswordException, AccountExistsException
	{
		RoleDefinition role1 = new RoleDefinition("role1");
		RoleDefinition role2 = new RoleDefinition("role2");
		RoleDefinition role3 = new RoleDefinition("role3");
		RoleDefinition role4 = new RoleDefinition("role1"); // this is part of the test
		
		ResourceDefinition res = ResourceDefinition.createResource("testRes2" + runId, ResourceDefinition.TYPE_USERACCESS);
		// test adding some roles
		res.addRole(role1);
		res.addRole(role2);
		res.addRole(role3);
		try { res.addRole(role4); fail(); } catch (ObjectExistsException err) {}
		
		// test hasRole call
		assertTrue(res.hasRole("role1"));
		assertFalse(res.hasRole("FAKEROLE"));
		
		// test update role
		RoleDefinition testRole = res.getRoleByName("role1");
		testRole.put("testAdd", "true");
		res.setRole(testRole);
		
		RoleDefinition testRoleUpdated = res.getRoleByName("role1");
		assertNotNull(testRoleUpdated.get("testAdd"));
		
		// test adding a role via the setRole call
		RoleDefinition testAddRole = new RoleDefinition("role99");
		res.setRole(testAddRole);
		assertTrue(res.hasRole("role99"));
		
		// test deleting a role
		res.deleteRole(testAddRole);
		assertFalse(res.hasRole("role99"));
		
		// test duplicating a role
		RoleDefinition testDupedRole = new RoleDefinition(testRoleUpdated, "role98");
		res.addRole(testDupedRole);
		assertTrue(res.hasRole("role98"));

		/*
		 * TOS
		 */
		TermsConditions tc = TermsConditions.createTOS("TestTOS" + runId);
		tc.setRenewalDays(30);
		tc.setDisplayName("Test TOS Name");
		tc.setText("This is the TOS Text");
		tc.save("Test");
		
		testDupedRole.addTOS("TestTOS" + runId);
		assertEquals(1, testDupedRole.getTOSList().size());

		// save resource with role and tc
		res.save("Test");

		UserAccount nUser = UserAccount.createUser("testcase", "testuser99" + runId, "thisisatestaccountpass");
		nUser.addResourceLink(new ResourceLink(res, "role98"));
		nUser.save("Test");
		
		assertFalse(nUser.isTOSAgreed("TestTOS" + runId));
		assertEquals(1, nUser.getTermsConditionsRenewList().size());
		
		nUser.addTOS(tc);
	
		// we should now be agreed to
		assertTrue(nUser.isTOSAgreed("TestTOS" + runId));
		assertEquals(0, nUser.getTermsConditionsRenewList().size());
	}
	
	/** Verify we can link users and resource */
	@Test public void testResourceUserLink()
	{
		// Generate a dummy resource + roles + permissions
		ResourceDefinition res = null;
		try {
			res = ResourceDefinition.createResource("Admin App" + runId, ResourceDefinition.TYPE_USERACCESS);
			
			RoleDefinition role = new RoleDefinition("General");
			role.addPermission("Login");
			res.addRole(role);
			res.save("TestCase");
			
		} catch (ObjectExistsException err) { fail(); }		
		
		UserAccount testUser = null;
		try {
			testUser = UserAccount.createUser("testcase", "testUser8" + runId, "123567890");
			testUser.save("TestCase");
		} catch (Exception er) { fail(); }
		
		// User has no 'rights' as of now - let's try to login to a resource called 'Admin App'
		try {
			Authorization.authorizeUser(testUser, "Admin App" + runId, null, UBasePermission.Login);
			fail();
		} catch (AuthorizationException err) {} 
		
		testUser.addResourceLink(new ResourceLink(res, "General"));
		
		// Try the authorization again
		try {
			Authorization.authorizeUser(testUser, "Admin App" + runId, null, UBasePermission.Login);
		} catch (AuthorizationException err) { fail(); } 
		
		// Try the authorization again with a different permission
		try {
			Authorization.authorizeUser(testUser, "Admin App" + runId, null, UBasePermission.DummyPermission);
			fail();
		} catch (AuthorizationException err) {} 
		
		/*
		 * Test Cache of User Auth List
		 */
		try {
			List<ResourceLink> links = Authorization.getUserAuthList(testUser, "Admin App" + runId, null, UBasePermission.Login);
			assertNotNull(links);
				
			List<ResourceLink> cachedLinks = Authorization.getUserAuthList(testUser, "Admin App" + runId, null, UBasePermission.Login);
			assertEquals(links.size(), cachedLinks.size());
		} catch (AuthorizationException err) {
			fail();
		} 
		
	}
	
	@Test public void testPlugins()
	{
		UOpts.setCacheFlag(true);
		// Generate a dummy resource + roles + permissions
		ResourceDefinition res = null;
		RoleDefinition role = new RoleDefinition("General");
		try {
			res = ResourceDefinition.createResource("Admin App 2" + runId, ResourceDefinition.TYPE_USERACCESS);
			res.save("TestCase");
		} catch (ObjectExistsException err) { fail(); }		
		
		UserAccount testUser = null;
		try {
			testUser = UserAccount.createUser("testcase", "testUser12" + runId, "123567890");
			testUser.save("TestCase");
		} catch (Exception er) { fail(); }
		
		// expect no plugins for the resource at this time
		assertEquals( 0, testUser.getPluginsByContainer("TestContainer").size() );
		
		// add a plugin, expect no plugins for this user due to no access to the resource/role
		role.addPlugin("com.untzuntz.ustack.testcases.TestPlugin", "TestContainer");
		role.addPermission("Login");
		try { res.addRole(role); res.save("TestCase"); } catch (ObjectExistsException err) { fail(); }		
		
		assertEquals( 0, testUser.getPluginsByContainer("TestContainer").size() );

		// add the user to this resource & general role
		testUser.addResourceLink(new ResourceLink(res, "General"));
		
		assertEquals( 1, testUser.getPluginsByContainer("TestContainer").size() );
	}
	
	@Test public void testContextCheck() throws Exception
	{
		// Generate a dummy resource + roles + permissions
		ResourceDefinition res = null;
		try {
			res = ResourceDefinition.createResource("Test Resource" + runId, ResourceDefinition.TYPE_USERACCESS);
			
			RoleDefinition role = new RoleDefinition("General");
			role.addPermission("Login");
			res.addRole(role);
			res.save("TestCase");
			
		} catch (ObjectExistsException err) { fail(); }		
		
		UserAccount testUser = null;
		try {
			testUser = UserAccount.createUser("testcase", "testUser13" + runId, "123567890");
			testUser.save("TestCase");
		} catch (Exception er) { fail(); }
		
		// User has no 'rights' as of now - let's try to login to a resource called 'Admin App'
		try {
			Authorization.authorizeUser(testUser, "Test Resource" + runId, null, UBasePermission.Login);
			fail();
		} catch (AuthorizationException err) {} 
		
		ResourceLink link = new ResourceLink(res, "General");
		link.put("partnerId", "ozzy");
		testUser.addResourceLink(link);
		
		
		DBObject goodContext = BasicDBObjectBuilder.start("partnerId", "ozzy").get();
		DBObject badContext = BasicDBObjectBuilder.start("partnerId", "rambo").get();
		
		// Try the authorization again
		try {
			Authorization.authorizeUser(testUser, "Test Resource" + runId, goodContext, UBasePermission.Login);
		} catch (AuthorizationException err) { fail(); } 
		
		// Try the authorization again with a different context
		try {
			Authorization.authorizeUser(testUser, "Test Resource" + runId, badContext, UBasePermission.Login);
			fail();
		} catch (AuthorizationException err) {} 
		
	}

}
