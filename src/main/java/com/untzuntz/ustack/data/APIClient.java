package com.untzuntz.ustack.data;

import java.util.Date;

import org.jasypt.salt.RandomSaltGenerator;
import org.jasypt.util.password.StrongPasswordEncryptor;

import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.untzuntz.ustack.exceptions.AccountExistsException;
import com.untzuntz.ustack.exceptions.InvalidUserAccountName;
import com.untzuntz.ustack.exceptions.PasswordException;
import com.untzuntz.ustack.exceptions.PasswordLengthException;
import com.untzuntz.ustack.main.Msg;
import com.untzuntz.ustack.main.UAppCfg;
import com.untzuntz.ustack.main.UOpts;

public class APIClient extends UntzDBObject {

	private static final long serialVersionUID = 1L;
	
	public static final String STATUS_DISABLED = "Disabled";
	public static final String STATUS_ACTIVE = "Active";
	
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
	 * @param user
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

	/**
	 * Salts, encrypts and stores apiKey
	 * 
	 * @param password
	 */
	public void setAPIKey(String actor, String password) throws PasswordException
	{
		if (isDisabled())
			return;

		// salt + password setup
		RandomSaltGenerator rsg = new RandomSaltGenerator();
		String saltStr = new String(rsg.generateSalt(10));
		
		put("salt", saltStr);
		password = saltStr + password; // salt the password for enhanced one-way hash
		
		StrongPasswordEncryptor encryptor = new StrongPasswordEncryptor();
		String encPassword = encryptor.encryptPassword(password); // encrypt
		put("apiKey", encPassword);
		put("apiKeyChangeDate", new Date());

		// clear locking data
		unlock();
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
	public static APIClient createAPI(String actor, String clientId, String apiKey) throws AccountExistsException,PasswordException
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
		acct.setAPIKey(actor, apiKey);
		
		return acct;
	}



}
