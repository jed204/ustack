package com.untzuntz.ustack.data;

import org.jasypt.util.text.BasicTextEncryptor;

import com.Ostermiller.util.Base64;

public class AccessToken {

	public static String INTERNAL_KEY;

	private BasicTextEncryptor textEncryptor;
	private BasicTextEncryptor getEncryptor()
	{
		if (textEncryptor != null)
			return textEncryptor;
		
		String passwd = "-30393djdsn" + INTERNAL_KEY + "ksslaPZ";
		textEncryptor = new BasicTextEncryptor();
		textEncryptor.setPassword(passwd);
		return textEncryptor;
	}

	public static String encode(String clientId, String userName, long expirationAge)
	{
		AccessToken at = new AccessToken();
		
		StringBuffer buf = new StringBuffer();
		buf.append(clientId).append("|");
		buf.append(userName).append("|");
		buf.append(expirationAge);
		
		return Base64.encode(at.getEncryptor().encrypt(buf.toString()));
	}
	
	public static AccessTokenDetails decode(String value)
	{
		if (value == null)
			return null;
		
		AccessToken at = new AccessToken();
		String decrypted = null;
		try {
			decrypted = at.getEncryptor().decrypt(Base64.decode(value));
		} catch (org.jasypt.exceptions.EncryptionOperationNotPossibleException err) {
			// invalid token
		}
		if (decrypted == null)
			return null;
		
		String[] spl = decrypted.split("\\|");
		if (spl.length != 3)
			return null;
		
		return at.getAccessDetails(spl);
	}
	
	private AccessTokenDetails getAccessDetails(String[] spl) {
		
		AccessTokenDetails ret = new AccessTokenDetails();
		ret.clientId = spl[0];
		ret.userName = spl[1];
		ret.expirationAge = System.currentTimeMillis() + Long.valueOf(spl[2]);
		
		return ret;

	}
	
	public class AccessTokenDetails {
		
		public String clientId;
		public String userName;
		public long expirationAge;
		
	}
	
}
