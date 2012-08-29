package com.untzuntz.ustack.data;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.jasypt.salt.RandomSaltGenerator;
import org.jasypt.util.text.BasicTextEncryptor;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

/**
 * API Mapping is used to map external API user accounts to user accounts in UStack
 * 
 * @author jdanner
 *
 */
public class APIMapping extends BasicDBObject {
	
	protected static Logger   	logger           = Logger.getLogger(APIMapping.class);

	private static final long serialVersionUID = 1L;
	
	private BasicTextEncryptor textEncryptor;
	
	public APIMapping(DBObject map) {
		putAll(map);
	}
	
	private APIMapping() {}

	public static APIMapping createMapping(String name)
	{
		APIMapping ret = new APIMapping();
		ret.setName(name);
		return ret;
	}
	
	public void setName(String name) {
		put("name", name);
	}
	
	public String getName() { 
		return getString("name");
	}
	
	private BasicTextEncryptor getEncryptor()
	{
		if (textEncryptor != null)
			return textEncryptor;
		
		String saltStr = null;
		if (get("s2") == null)
		{
			RandomSaltGenerator rsg = new RandomSaltGenerator();
			
			Base64 base = new Base64();
			saltStr = new String(base.encode(rsg.generateSalt(10)));
			
			logger.info("Salt Str: " + saltStr);
			
			put("s2", saltStr);
		}
		else
			saltStr = getString("s2");
		
		textEncryptor = new BasicTextEncryptor();
		textEncryptor.setPassword(getName() + "-" + saltStr + "!Msfkajfsakfu3831jimsfkakj#*@");
		return textEncryptor;
	}
	
	public void setAccessInfo(String tokenId, String secret)
	{
		BasicTextEncryptor textEncryptor = getEncryptor();
//		String encAccountName = textEncryptor.encrypt(accountName);
		String encTokenId = textEncryptor.encrypt(tokenId);
		String encSecret = textEncryptor.encrypt(secret);
//		put("a", encAccountName);
		put("t", encTokenId);
		put("s1", encSecret);
	}
	
//	public String getAccountName()
//	{
//		BasicTextEncryptor textEncryptor = getEncryptor();
//		return textEncryptor.decrypt( getString("a") );
//	}
//	
	public String getTokenId()
	{
		BasicTextEncryptor textEncryptor = getEncryptor();
		return textEncryptor.decrypt( getString("t") );
	}
	
	public String getSecret()
	{
		BasicTextEncryptor textEncryptor = getEncryptor();
		return textEncryptor.decrypt( getString("s1") );
	}
	
}
