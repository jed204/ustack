package com.textrecruit.ustack.data;

import com.mongodb.*;
import com.textrecruit.ustack.exceptions.AccountExistsException;
import com.textrecruit.ustack.exceptions.InvalidUserAccountName;
import com.textrecruit.ustack.exceptions.PasswordException;
import com.textrecruit.ustack.exceptions.PasswordLengthException;
import com.textrecruit.ustack.main.Msg;
import com.textrecruit.ustack.main.UAppCfg;
import com.textrecruit.ustack.main.UOpts;
import org.apache.commons.codec.binary.Base64;
import org.jasypt.salt.RandomSaltGenerator;
import org.jasypt.util.text.BasicTextEncryptor;

import java.util.Date;
import java.util.UUID;

public class APIClient extends UntzDBObject {

	private static final long serialVersionUID = 1L;
	
	public static final String STATUS_DISABLED = "Disabled";
	public static final String STATUS_ACTIVE = "Active";
	private static String INTERNAL_KEY;
	
	public String getCollectionName() { return "apiClients"; }

	private APIClient()
	{
		// setup basic values on account
		put("created", new Date());
		setStatus(STATUS_ACTIVE);
	}
	
	public Date getCreated() {
		return (Date)get("created");
	}

	public String getUserId() {
		return get("_id") + "";
	}

	/** Gets the DB Collection for the UserAccount object */
	public static DBCollection getDBCollection() {
		return new APIClient().getCollection();
	}

	/** Return the name of the database that houses the 'users' collection */
	public static final String getDatabaseName() {
		
		if (UOpts.getString(UAppCfg.DATABASE_USERS_COL) != null)
			return UOpts.getString(UAppCfg.DATABASE_USERS_COL);
		
		return UOpts.getAppName();
		
	}

	/**
	 * Generate a APIClient object from the MongoDB object
	 * @param client
	 */
	public APIClient(DBObject client) {
		super(client);
	}

	public void setClientId(String clientId) {
		put("clientId", clientId);
	}
	
	public String getClientId() {
		return getString("clientId");
	}
	
	/** Sets the user's status */
	public void setStatus(String status)
	{
		put("status", status);
	}

	/**
	 * Returns the current user status
	 * 
	 * @return
	 */
	public String getStatus()
	{
		String status = (String)get("status");
		if (status == null)
			return STATUS_ACTIVE;
		
		return status;
	}
	
	public BasicDBList getAPIKeys() {
		return getList("apiKeys");
	}
	
	public void setAPIKeys(BasicDBList l) {
		setList("apiKeys", l);
	}
	
	public void revokeKey(String actor, String uid)
	{
		BasicDBList keyList = getAPIKeys();
		for (int i = 0; i < keyList.size(); i++)
		{
			DBObject k = (DBObject)keyList.get(i);
			
			if (uid.equals( (String)k.get("uid") ))
			{
				k.put("revoked", "t");
				k.put("revokedBy", actor);
				setAPIKeys(keyList);
				return;
			}
		}
	}
	
	public void generateKey(String actor)
	{
		String init = UUID.randomUUID().toString();
		
		BasicTextEncryptor textEncryptor = getEncryptor();
		String encSecret = textEncryptor.encrypt(init);
		
		BasicDBObject key = new BasicDBObject();
		key.put("uid", UUID.randomUUID().toString());
		key.put("key", encSecret);
		key.put("createdBy", actor);
		
		BasicDBList keys = getAPIKeys();
		keys.add(key);
		setAPIKeys(keys);
	}

	private BasicTextEncryptor textEncryptor;
	public BasicTextEncryptor getEncryptor()
	{
		if (textEncryptor != null)
			return textEncryptor;
		
		String saltStr = null;
		if (get("s2") == null)
		{
			RandomSaltGenerator rsg = new RandomSaltGenerator();
			
			Base64 base = new Base64();
			saltStr = new String(base.encode(rsg.generateSalt(10)));
			
			put("s2", saltStr);
		}
		
		saltStr = getString("s2");
		
		String passwd = getClientId() + "-" + saltStr + APIClient.INTERNAL_KEY;
		textEncryptor = new BasicTextEncryptor();
		textEncryptor.setPassword(passwd);
		return textEncryptor;
	}

	/**
	 * Determine if the account is currently locked
	 * 
	 * @return
	 */
	public boolean isLocked()
	{
		if (get("locked") == null)
			return false;
		
		return true;
	}

	/**
	 * Determines if the account is disabled by the account status
	 * @return
	 */
	public boolean isDisabled()
	{
		String status = getStatus();
		
		if (STATUS_DISABLED.equalsIgnoreCase(status))
			return true;
		
		return false;
	}
	
	public String getValidKey() {

		BasicDBList keyList = getAPIKeys();
		for (int i = 0; i < keyList.size(); i++)
		{
			DBObject k = (DBObject)keyList.get(i);
			
			if (!"t".equalsIgnoreCase( (String)k.get("revoked") ))
			{
				BasicTextEncryptor textEncryptor = getEncryptor();
				return textEncryptor.decrypt( (String)k.get("key") );
			}
		}

		return null;

	}
	
	public String getKey(String uid) {
		
		BasicDBList keyList = getAPIKeys();
		for (int i = 0; i < keyList.size(); i++)
		{
			DBObject k = (DBObject)keyList.get(i);
			
			if (uid.equals( (String)k.get("uid") ))
			{
				BasicTextEncryptor textEncryptor = getEncryptor();
				return textEncryptor.decrypt( (String)k.get("key") );
			}
		}

		return null;
	}

	public boolean checkAPIKey(String apiKey) 
	{
		BasicTextEncryptor textEncryptor = getEncryptor();

		
		BasicDBList keys = getAPIKeys();
		for (int i = 0; i < keys.size(); i++)
		{
			DBObject k = (DBObject)keys.get(i);
		
			String rawKey = (String)k.get("key");
			String storedKey = textEncryptor.decrypt(rawKey);
			if (apiKey.equals(storedKey))
			{
				if (!"t".equalsIgnoreCase((String)k.get("revoked")))
					return true;
				else
					return false;
			}
		}
		
		return false;
	}
	
	public void unlock() 
	{
		removeField("locked");
	}

	/**
	 * Locks the account
	 */
	public void lockAccount()
	{
		put("locked", "t");		
	}
	
	/**
	 * Get an account by client id
	 * 
	 * @param clientId
	 * @return
	 */
	public static APIClient getAPIClient(String clientId)
	{
		if (clientId == null || clientId.length() == 0)
			return null;
		
		clientId = clientId.toLowerCase().trim();
		
		DBObject acct = null;
		try {
			acct = new APIClient().getCollection().findOne(BasicDBObjectBuilder.start("clientId", clientId).get());
		} catch (Exception exp) { 
			return null;
		}
		
		if (acct == null)
			return null;
		
		return new APIClient(acct);
	}
	
	/**
	 * Create a new API account
	 * 
	 * @param clientId
	 * @return
	 * @throws AccountExistsException
	 * @throws PasswordLengthException
	 */
	public static APIClient createAPI(String actor, String clientId) throws AccountExistsException,PasswordException
	{
		if (clientId == null || clientId.length() == 0)
			throw new InvalidUserAccountName(Msg.getString("Invalid-ClientID"));

		clientId = clientId.toLowerCase().trim();
		
		APIClient acct = getAPIClient(clientId);
		if (acct != null) // already exists
			throw new AccountExistsException("Client ID");
		
		// create the actual account
		acct = new APIClient();
		acct.put("createdBy", actor);
		acct.setClientId(clientId);
		acct.generateKey(actor);
		
		return acct;
	}



}
