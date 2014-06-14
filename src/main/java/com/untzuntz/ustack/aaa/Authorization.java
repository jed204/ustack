package com.untzuntz.ustack.aaa;

import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.mongodb.BasicDBList;
import com.mongodb.DBObject;
import com.untzuntz.ustack.data.APIClient;
import com.untzuntz.ustack.data.UDataCache;
import com.untzuntz.ustack.data.UserAccount;
import com.untzuntz.ustack.exceptions.AuthenticationException;
import com.untzuntz.ustack.exceptions.AuthorizationException;
import com.untzuntz.ustack.exceptions.InvalidAccessAttempt;
import com.untzuntz.ustack.exceptions.InvalidAuthorizationConfig;
import com.untzuntz.ustack.exceptions.InvalidUserAuthException;
import com.untzuntz.ustack.main.UOpts;

public class Authorization {

	private static final int AUTH_CACHE_TTL = 300;
	private static Logger logger = Logger.getLogger(Authorization.class);

	/**
	 * Verify authorization, return a boolean
	 * @param user
	 * @param resource
	 * @param perm
	 * @return
	 */
	public static boolean authorizeUserBool(UserAccount user, String resource, UStackPermissionEnum perm)
	{
		try {
			
			authorizeUser(user, resource, null, perm);
			return true;
			
		} catch (AuthorizationException exp) {}
		
		return false;
	}
	
	/**
	 * Verify authorization, return a boolean
	 * @param user
	 * @param resource
	 * @param context
	 * @param perm
	 * @return
	 */
	public static boolean authorizeUserBool(UserAccount user, String resource, DBObject context, UStackPermissionEnum perm)
	{
		try {
			
			authorizeUser(user, resource, context, perm);
			return true;
			
		} catch (AuthorizationException exp) {}
		
		return false;
	}
	
	/**
	 * Verify authorization, return a boolean
	 * @param user
	 * @param resource
	 * @param context
	 * @param perm
	 * @return
	 */
	public static boolean authorizeUserBool(UserAccount user, String resource, DBObject context, String perm)
	{
		try {
			
			authorizeUser(user, resource, context, perm);
			return true;
			
		} catch (AuthorizationException exp) {}
		
		return false;
	}
	
	/** Build the key to use in the cache */
	public static String buildCacheKey(UserAccount user, String resource, DBObject context, String perm)
	{
		StringBuffer chk = new StringBuffer();
		chk.append("[");
		chk.append("[").append(user.getUserName()).append("]");
		chk.append("[").append(resource.replaceAll(" ", "_")).append("]");
		if (context == null)
			chk.append("[").append("NULL").append("]");
		else
			chk.append("[").append(context.toString().replaceAll(" ", "_")).append("]");
		chk.append("[").append(perm).append("]");
		chk.append("]");
		
		return chk.toString();
	}
	
	/** Build the key to use in the cache */
	public static String buildCacheKey(String clientId, String perm)
	{
		StringBuffer chk = new StringBuffer();
		chk.append("[");
		chk.append("[API-").append(clientId).append("]");
		chk.append("[").append(perm).append("]");
		chk.append("]");
		
		return chk.toString();
	}
	
	/**
	 * Verify authorization, throw an exception
	 * 
	 * @throws AuthenticationException
	 */
	public static void authorizeUser(UserAccount user, String resource, DBObject context, UStackPermissionEnum perm) throws AuthorizationException
	{		
		authorizeUser(user, resource, context, perm.getPermission());
	}
	
	/**
	 * Authorize an API client returning a boolean instead of throwing an Exception
	 * 
	 * @param clientId
	 * @param perm
	 * @return
	 */
	public static boolean authorizeAPIBool(String clientId, UStackPermissionEnum perm)
	{
		try {
			
			authorizeAPI(clientId, perm);
			return true;
			
		} catch (AuthorizationException exp) {}
		
		return false;
	}

	/**
	 * Authorize the API client
	 * 
	 * @param apiClient
	 * @param perm
	 * @throws AuthorizationException
	 */
	public static void authorizeAPI(String clientId, UStackPermissionEnum perm) throws AuthorizationException
	{
		if (clientId == null)
			throw new InvalidUserAuthException();
		
		APIClient apiClient = null;
		try {
			/*
			 * Check Cache First
			 */
			if (UOpts.getCacheEnabled())
			{
				String key = buildCacheKey(clientId, perm.getPermission());
				String curCache = (String)UDataCache.getInstance().get(key);
				if ("TRUE".equals(curCache))
				{
					logger.debug("Authorization Success (CACHE): [" + clientId + "/" + perm + "]");
					return;
				}
				else if (curCache != null)
					throw new InvalidAccessAttempt();
			}
			
			apiClient = APIClient.getAPIClient(clientId);
			if (apiClient == null)
				throw new InvalidUserAuthException();
			/*
			 * Do Lookup
			 */
			List<ResourceLink> links = apiClient.getResourceLinksByName("*", null);
			if (links.size() == 0)
				throw new InvalidAccessAttempt();
			
			logger.debug(links.size() + " Resource Links Found");
			
			boolean passed = false;
			for (int i = 0; !passed && i < links.size(); i++)
			{
				ResourceLink link = links.get(i);
				ResourceDefinition def = ResourceDefinition.getByName(link.getName());
				if (def == null)
					throw new InvalidAuthorizationConfig("No resource named '" + link.getName() + "'");
				
				RoleDefinition role = def.getRoleByName(link.getRoleName());
				if (role == null)
					throw new InvalidAuthorizationConfig("No role named '" + link.getRoleName() + "' for resource '" + link.getName() + "'");
	
				if (role.hasPermission(perm.getPermission()))
					passed = true;
			}
			
			if (!passed)
				throw new InvalidAccessAttempt();
			
			if (UOpts.getCacheEnabled())
			{
				String key = buildCacheKey(clientId, perm.getPermission());
				UDataCache.getInstance().set(key, AUTH_CACHE_TTL, "TRUE");
			}
			
		} catch (AuthorizationException ae) {
			if (apiClient == null)
				logger.debug("Authorization FAILED: [NULL/" + perm + "]");
			else
				logger.debug("Authorization FAILED: [" + apiClient.getClientId() + "/" + perm + "] => " + ae.getMessage());
				
			throw ae;
		}
		
		logger.debug("Authorization Success (DIRECT): [" + apiClient.getClientId() + "/" + perm + "]");

	}

	/**
	 * Verify authorization, throw an exception
	 * 
	 * @param user
	 * @param resource
	 * @param context
	 * @param perm
	 * @throws AuthorizationException
	 */
	public static void authorizeUser(UserAccount user, String resource, DBObject context, String perm) throws AuthorizationException
	{		
		try {
	
			if (user == null)
				throw new InvalidUserAuthException();
			
			/*
			 * Check Cache First
			 */
			if (UOpts.getCacheEnabled())
			{
				String key = buildCacheKey(user, resource, context, perm);
				String curCache = (String)UDataCache.getInstance().get(key);
				if ("TRUE".equals(curCache))
				{
					logger.debug("Authorization Success (CACHE): [" + user.getUserName() + "/" + resource + "/" + context + "/" + perm + "]");
					return;
				}
				else if (curCache != null)
					throw new InvalidAccessAttempt();
			}
			
			/*
			 * Do Lookup
			 */
			List<ResourceLink> links = user.getResourceLinksByName(resource, context);
			if (links.size() == 0)
			{
				links = user.getResourceLinksByFullName(resource, context);
				if (links.size() == 0)
					throw new InvalidAccessAttempt(resource);
			}
			
			logger.debug(links.size() + " Resource Links Found : " + resource + " // " + context);
			
			boolean passed = false;
			for (int i = 0; !passed && i < links.size(); i++)
			{
				ResourceLink link = links.get(i);
				ResourceDefinition def = ResourceDefinition.getByName(link.getName());
				if (def == null)
					throw new InvalidAuthorizationConfig("No resource named '" + resource + "'");
				
				RoleDefinition role = def.getRoleByName(link.getRoleName());
				if (role == null)
					throw new InvalidAuthorizationConfig("No role named '" + link.getRoleName() + "' for resource '" + resource + "'");
	
				if (role.hasPermission(perm))
					passed = true;
			}
			
			if (!passed)
				throw new InvalidAccessAttempt();
			
			if (UOpts.getCacheEnabled())
			{
				String key = buildCacheKey(user, resource, context, perm);
				UDataCache.getInstance().set(key, AUTH_CACHE_TTL, "TRUE");
			}
			
		} catch (AuthorizationException ae) {
			if (user == null)
				logger.debug("Authorization FAILED: [NULL/" + resource + "/" + context + "/" + perm + "]");
			else
				logger.debug("Authorization FAILED: [" + user.getUserName() + "/" + resource + "/" + context + "/" + perm + "] => " + ae.getMessage());
				
			throw ae;
		}
		
		logger.debug("Authorization Success (DIRECT): [" + user.getUserName() + "/" + resource + "/" + context + "/" + perm + "]");
		
	}
	
	/**
	 * Provided a ResourceLink check if there is a permission
	 * 
	 * @param resLink
	 * @param perm
	 * @return
	 */
	public static boolean hasPermission(ResourceLink resLink, UStackPermissionEnum perm)
	{
		try {
			hasPermission(resLink.getResourceDefinition(), resLink.getRoleName(), perm);
			return true;
		} catch (Exception e) {
		}
		return false;
	}	
	
	/**
	 * Check a permission of a resource definition and role
	 * 
	 * @param resource
	 * @param roleName
	 * @param perm
	 * @throws InvalidAuthorizationConfig
	 * @throws InvalidAccessAttempt
	 */
	public static void hasPermission(ResourceDefinition resource, String roleName, UStackPermissionEnum perm) throws InvalidAuthorizationConfig, InvalidAccessAttempt
	{
		RoleDefinition role = resource.getRoleByName(roleName);
		if (role == null)
			throw new InvalidAuthorizationConfig("No role named '" + roleName + "' for resource '" + resource + "'");

		if (!role.hasPermission(perm.getPermission()))
			throw new InvalidAccessAttempt();
	}
	
	/**
	 * Returns a list of ResourceLink objects the user is allowed to complete the permission requested upon
	 * 
	 * @param userName
	 * @throws AuthenticationException
	 */
	public static List<ResourceLink> getUserAuthList(UserAccount user, String resource, DBObject context, UStackPermissionEnum perm) throws AuthorizationException
	{		
		List<ResourceLink> ret = new Vector<ResourceLink>();
		
		try {
	
			if (user == null)
				throw new InvalidUserAuthException();

			/*
			 * Check Cache First
			 */
//			if (UOpts.getCacheEnabled())
//			{
//				String curCache = (String)UDataCache.getInstance().get("DBL" + buildCacheKey(user, resource, context, perm));
//				if (curCache != null)
//				{
//					logger.debug("Authorization List Success (CACHE): [" + user.getUserName() + "/" + resource + "/" + perm.getPermission() + "]");
//					logger.debug("Cache => " + curCache.length() + " ==> " + curCache);
//					BasicDBList list = UDataMgr.readDBListFromString(new String(Base64.decodeBase64(curCache.getBytes())));
//					for (int i = 0; i < list.size(); i++)
//					{
//						DBObject obj = (DBObject)list.get(i);
//						ret.add(new ResourceLink(obj));
//					}
//					return ret;
//				}
//			}
			
			/*
			 * Do Lookup
			 */
			logger.debug("getResourceLinksByName(" + resource + ", " + context + ")");
			List<ResourceLink> links = user.getResourceLinksByName(resource, context);
			if (links.size() == 0)
				throw new InvalidAccessAttempt();
			
			BasicDBList resList = new BasicDBList();
			for (int i = 0; i < links.size(); i++)
			{
				ResourceLink link = links.get(i);
				logger.debug("getByInternalName(" + resource + ")");
				ResourceDefinition def = ResourceDefinition.getByName(link.getName());
				if (def == null)
					throw new InvalidAuthorizationConfig("No resource named '" + resource + "'");
				
				RoleDefinition role = def.getRoleByName(link.getRoleName());
				if (role == null)
					throw new InvalidAuthorizationConfig("No role named '" + link.getRoleName() + "' for resource '" + resource + "'");
	
				if (role.hasPermission(perm.getPermission()))
				{
					ret.add(link);
					resList.add(link);
					logger.debug("Found [" + def.getInternalName() + "/" + def.getName() + "] => " + link.getLinkText() + " / " + link.getRoleName());
				}
			}
			
//			if (UOpts.getCacheEnabled())
//			{
//				String resListStr = UDataMgr.writeDBListToString(resList);
//				String cacheVal = new String(Base64.encodeBase64(resListStr.getBytes()));
//				logger.debug("Setting Cache => " + cacheVal.length() + " ==> " + cacheVal);
//				UDataCache.getInstance().set("DBL" + buildCacheKey(user, resource, context, perm), AUTH_CACHE_TTL, cacheVal);
//			}
			
		} catch (AuthorizationException ae) {
			logger.debug("Authorization List FAILED: [" + user.getUserName() + "/" + resource + "/" + perm.getPermission() + "] => " + ae.getMessage());
			throw ae;
		}
		
		logger.debug("Authorization List Success (DIRECT): [" + user.getUserName() + "/" + resource + "/" + perm.getPermission() + "] => " + ret.size() + " results");
		
		return ret;
	}

}
