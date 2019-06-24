package com.textrecruit.ustack.aaa;

import com.textrecruit.ustack.data.APIClient;
import com.textrecruit.ustack.data.UDataCache;
import com.textrecruit.ustack.data.UserAccount;
import com.textrecruit.ustack.data.accting.CreditAccount;
import com.textrecruit.ustack.exceptions.AuthExceptionUserDisabled;
import com.textrecruit.ustack.exceptions.AuthExceptionUserLocked;
import com.textrecruit.ustack.exceptions.AuthExceptionUserPasswordMismatch;
import com.textrecruit.ustack.exceptions.AuthenticationException;
import com.textrecruit.ustack.main.Msg;
import com.textrecruit.ustack.main.UAppCfg;
import com.textrecruit.ustack.main.UOpts;
import com.textrecruit.ustack.uisupport.UEntryError;
import org.apache.log4j.Logger;
import org.jasypt.util.password.StrongPasswordEncryptor;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.List;
import java.util.Vector;
import java.util.regex.Pattern;

/**
 * Autenticate user
 * 
 * @author jdanner
 *
 */
public class Authentication {

	private static Logger logger = Logger.getLogger(Authentication.class);

	/**
	 * Verifies the password length and content
	 * @param password1
	 * @param password2
	 * @return
	 */
	public static List<UEntryError> verifyPasswordRequirements(String password1, String password2)
	{
		List<UEntryError> ret = new Vector<UEntryError>();
		
		if (!password1.equals(password2))
			ret.add(UEntryError.getInstance("Password", Msg.getString("EntryError-PasswordMismatch")));

		if (password1.length() < 6)
			ret.add(UEntryError.getInstance("Password", Msg.getString("EntryError-PasswordLength", 6)));
		
		int passedCnt = 0;
		if (Pattern.matches(".*[A-Z].*", password1))
			passedCnt++;
		if (Pattern.matches(".*[a-z].*", password1))
			passedCnt++;
		if (Pattern.matches(".*[0-9].*", password1))
			passedCnt++;
		if (Pattern.matches(".*[^a-zA-Z0-9_].*", password1))
			passedCnt++;
		
		if (passedCnt < 3)
			ret.add(UEntryError.getInstance("Password", Msg.getString("EntryError-PasswordAdvanced")));
		
		return ret;
	}

	/**
	 *
	 * @param ca
	 * @param pin
	 * @throws AuthenticationException
	 */
	public static void authenticatePIN(CreditAccount ca, String pin) throws AuthenticationException
	{
		if (pin == null)
			throw new AuthExceptionUserPasswordMismatch();
			
		if (ca == null)
			throw new AuthExceptionUserPasswordMismatch();

		if (ca.isDisabled()) // check for disabled account
			throw new AuthExceptionUserDisabled();
		
		if (ca.isLocked()) // check for locked account
			throw new AuthExceptionUserLocked();

		if (!pin.equalsIgnoreCase(ca.getString("pin")))
		{
			ca.increasePINErrorCount();
			throw new AuthExceptionUserPasswordMismatch();
		}
	}

	/**
	 *
	 * @param user
	 * @throws AuthenticationException
	 */
	public static void checkAccountBasics(UserAccount user) throws AuthenticationException
	{
		if (user == null) // user doesn't exist
			throw new AuthExceptionUserPasswordMismatch();
		
		if (user.isDisabled()) // check for disabled account
			throw new AuthExceptionUserDisabled();
		
		if (user.isLocked()) // check for locked account
			throw new AuthExceptionUserLocked();
		
		// note: we don't check for password expiration - your app should (user.isPasswordExpired())
	}

	/**
	 *
	 * @param clientId
	 * @param apiKey
	 * @throws AuthenticationException
	 */
	public static void authenticateAPI(String clientId, String apiKey) throws AuthenticationException
	{
		String cacheKey = "api" + clientId + apiKey.replace("-", "");
		if (UDataCache.getInstance() != null && UDataCache.getInstance().get(cacheKey) != null)
			return;
		
		APIClient acct = APIClient.getAPIClient(clientId);
		
		if (acct == null) // acct doesn't exist
			throw new AuthExceptionUserPasswordMismatch();
		
		if (acct.isDisabled()) // check for disabled account
			throw new AuthExceptionUserDisabled();
		
		if (acct.isLocked()) // check for locked account
			throw new AuthExceptionUserLocked();

		if (!acct.checkAPIKey(apiKey))
			throw new AuthExceptionUserPasswordMismatch();
			
		if (UDataCache.getInstance() != null)
			UDataCache.getInstance().set(cacheKey, 1800, "t");
	}

	/**
	 *
	 * @param userName
	 * @param password
	 * @return
	 * @throws AuthenticationException
	 */
	public static UserAccount authenticateUserHash(String userName, String password) throws AuthenticationException
	{
		UserAccount user = null;
		try {
			if (password == null || password.length() < UOpts.getInt(UAppCfg.PASSWORD_MIN_LENGTH)) // verify a reasonably valid password is provided, otherwise skip it
				throw new AuthExceptionUserPasswordMismatch();
			
			user = UserAccount.getUser(userName);
			checkAccountBasics(user);
			
			if (!password.equals(user.getString("password")))
			{
				logger.info("USER ACCT PASS: [ " + user.getString("password") + "]");
				logger.info(" PROVIDED PASS: [ " + password + "]");
				
				user.increasePasswordErrorCount();
				throw new AuthExceptionUserPasswordMismatch();
			}
			
			// mark user as logged in (this also saves the user object)
			user.loggedIn();
			
		} catch (AuthenticationException ae) {
			logger.warn("Hashed Authentication FAILED: [" + userName + "/**********(Len:" + (password == null ? "null" : password.length()) + ")]");
			throw ae;
		}
		
		logger.info("Hashed Authentication Success: [" + userName + "/**********(Len:" + (password == null ? "null" : password.length()) + ")]");
		
		return user;
	}
	
	/**
	 * Authenticate the user and password
	 * 
	 * @param userName
	 * @param password
	 * @return
	 * @throws AuthenticationException
	 */
	public static UserAccount authenticateUser(String userName, String password) throws AuthenticationException
	{
		// TODO: Do some additional testing and implement this
//		String userCacheKey = "userAcct" + userName + password;
//		if (UDataCache.getInstance() != null)
//		{
//			UserAccount user = (UserAccount)UDataCache.getInstance().get(userCacheKey);
//			if (user != null)
//				return user;
//		}
		
		UserAccount user = null;
		try {
			if (password == null || password.length() < UOpts.getInt(UAppCfg.PASSWORD_MIN_LENGTH)) // verify a reasonably valid password is provided, otherwise skip it
				throw new AuthExceptionUserPasswordMismatch();

			user = UserAccount.getUser(userName);
			checkAccountBasics(user);
			
			String cacheKey = "userAuth" + userName + password;
			if (UDataCache.getInstance() == null || !"t".equals((String)UDataCache.getInstance().get(cacheKey)))
			{
				// verify password
				StrongPasswordEncryptor encryptor = new StrongPasswordEncryptor();
				if (!encryptor.checkPassword(user.getString("salt") + password, user.getString("password")))
				{
					user.increasePasswordErrorCount();
					throw new AuthExceptionUserPasswordMismatch();
				}
				
				if (UDataCache.getInstance() != null)
					UDataCache.getInstance().set(cacheKey, 600, "t");
			}

			// mark user as logged in (this also saves the user object)
			user.loggedIn();
			
		} catch (AuthenticationException ae) {
			logger.warn("Authentication FAILED: [" + userName + "/**********(Len:" + (password == null ? "null" : password.length()) + ")]");
			throw ae;
		}
		
		logger.info("Authentication Success: [" + userName + "/**********(Len:" + (password == null ? "null" : password.length()) + ")]");

		// TODO: Do some additional testing and implement this
		//if (UDataCache.getInstance() != null)
		//	UDataCache.getInstance().set(userCacheKey, 600, user);

		return user;
	}

	/** Note: a few hard to distinguish characters are removed - e.g. 0 and O */
	final static char[] printableAscii = new char[]{'!', '$', '*', '?',
            '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};

	/**
	 * Generates a random password of 6 characters
	 * 
	 * @return
	 * @throws NoSuchAlgorithmException
	 */
	public static String generatePassword() throws NoSuchAlgorithmException
	{
		return generatePassword(6);
	}
	
	/**
	 * Generates a random password
	 * 
	 * @return
	 * @throws NoSuchAlgorithmException 
	 */
	public static String generatePassword(int len) throws NoSuchAlgorithmException
	{
		SecureRandom wheel = SecureRandom.getInstance("SHA1PRNG");

		StringBuffer buf = new StringBuffer();
		
		while (buf.length() < len)
		{
			int nextInt = wheel.nextInt(printableAscii.length);
			buf.append(printableAscii[nextInt]);
		}
		
		return buf.toString();
	}
	
}
