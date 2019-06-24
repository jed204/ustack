package com.textrecruit.ustack.aaa;

import com.mongodb.BasicDBList;
import com.mongodb.DBObject;
import com.textrecruit.ustack.data.APIClient;
import com.textrecruit.ustack.data.UDataCache;
import com.textrecruit.ustack.data.UserAccount;
import com.textrecruit.ustack.exceptions.*;
import com.textrecruit.ustack.main.UOpts;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.Vector;

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
		return authorizeUserBool(user, resource, null, perm, null);
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
		return authorizeUserBool(user, resource, context, perm, null);
	}
	
	/**
	 * Verify authorization filtered by partner, return a boolean
	 * @param user
	 * @param resource
	 * @param context
	 * @param perm
	 * @param partner
	 * @return
	 */
	public static boolean authorizeUserBool(UserAccount user, String resource, DBObject context, UStackPermissionEnum perm, String partner)
	{
		try {
			
			authorizeUser(user, resource, context, perm, partner);
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
		return authorizeUserBool(user, resource, context, perm, null);
	}
	
	/**
	 * Verify authorization filtered by partner, return a boolean
	 * @param user
	 * @param resource
	 * @param context
	 * @param perm
	 * @param partner
	 * @return
	 */
	public static boolean authorizeUserBool(UserAccount user, String resource, DBObject context, String perm, String partner)
	{
		try {
			
			authorizeUser(user, resource, context, perm, partner);
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
	 * @param user
	 * @param resource
	 * @param context
	 * @param perm
	 * @throws AuthenticationException
	 */
	public static void authorizeUser(UserAccount user, String resource, DBObject context, UStackPermissionEnum perm) throws AuthorizationException
	{
		authorizeUser(user, resource, context, perm, null);
	}
	
	/**
	 * Verify authorization filterd by partner, throw an exception
	 * 	@param user
	 * @param resource
	 * @param context
	 * @param perm
	 * @param partner
	 * @throws AuthenticationException
	 */
	public static void authorizeUser(UserAccount user, String resource, DBObject context, UStackPermissionEnum perm, String partner) throws AuthorizationException
	{		
		authorizeUser(user, resource, context, perm.getPermission(), partner);
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
	 * @param clientId
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
		authorizeUser(user, resource, context, perm, null);
	}
	
	/**
	 * Verify authorization filtered by partner, throw an exception
	 * 
	 * @param user
	 * @param resource
	 * @param context
	 * @param perm
	 * @param partner
	 * @throws AuthorizationException
	 */
	public static void authorizeUser(UserAccount user, String resource, DBObject context, String perm, String partner) throws AuthorizationException
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
				
				if (!def.partnerMatch(partner))
					continue;
				
				RoleDefinition role = def.getRoleByName(link.getRoleName());
				if (role == null)
					throw new InvalidAuthorizationConfig("No role named '" + link.getRoleName() + "' for resource '" + resource + "'");
	
				if (role.hasPermission(perm)) {
					passed = true;
				} else {
					logger.info(role.getName() + " does not have " + perm);
				}
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
	 * @param user
	 * @param resource
	 * @param context
	 * @param perm
	 * @return
	 * @throws AuthorizationException
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
