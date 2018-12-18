package com.untzuntz.ustack.data;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.untzuntz.ustack.exceptions.AuthExceptionAuthError;
import org.apache.commons.lang.StringUtils;
import org.jasypt.util.text.BasicTextEncryptor;

import com.Ostermiller.util.Base64;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public class AccessToken {

	public static String INTERNAL_KEY;
	public static String INTERNAL_JWT_KEY;
	public static String JWT_ISSUER;

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

	private static JWTVerifier getJwtVerifier(String issuer) {
		return JWT.require(getAlgorithm())
				.withIssuer(issuer)
				.build(); // Reusable verifier instance
	}

	private static Algorithm getAlgorithm() {
		try {
			return Algorithm.HMAC256("s]DTV7EqgZ6BAiVgW" + INTERNAL_JWT_KEY + "qqKN6n)MxM{x(FRot?93zNe^8gR");
		} catch (UnsupportedEncodingException e) {
			// major fail
		}
		return null;
	}

	public static DecodedJWT decodeJwt(String encodedJWT) throws AuthExceptionAuthError {
		DecodedJWT jwt;
		try {
			jwt = getJwtVerifier(JWT_ISSUER).verify(encodedJWT);
		} catch (JWTVerificationException exception) {
			System.err.println("exception = " + exception);
			throw new AuthExceptionAuthError();
		}
		return jwt;
	}

	public static String encodeJwt(String clientId, String userName, long expirationAge, String ipAddress, Map<String,String> custom) throws AuthExceptionAuthError {

		String token;
		try {
			JWTCreator.Builder tokenBuilder = JWT.create()
					.withExpiresAt(new Date((System.currentTimeMillis() + expirationAge)))
					.withIssuer(JWT_ISSUER)
					.withClaim("id", UUID.randomUUID().toString())
					.withClaim("clientId", clientId)
					.withClaim("userName", userName);

			if (StringUtils.isNotEmpty(ipAddress)) {
				tokenBuilder.withClaim("ipAddress", ipAddress);
			}

			if (custom != null) {

				Iterator<String> it = custom.keySet().iterator();
				while (it.hasNext()) {
					String key = it.next();
					tokenBuilder.withClaim(key, custom.get(custom.get(key)));
				}

			}

			token = tokenBuilder.sign(getAlgorithm());

		} catch (JWTCreationException exception) {
			throw new AuthExceptionAuthError();
		}

		return token;

	}

	public static String encode(String clientId, String userName, long expirationAge)
	{
		AccessToken at = new AccessToken();

		StringBuffer buf = new StringBuffer();
		buf.append(clientId).append("|");
		buf.append(userName).append("|");
		buf.append((System.currentTimeMillis() + expirationAge));

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
		if (spl.length < 3)
			return null;
		
		return at.getAccessDetails(spl);
	}
	
	private AccessTokenDetails getAccessDetails(String[] spl) {
		
		AccessTokenDetails ret = new AccessTokenDetails();
		ret.clientId = spl[0];
		ret.userName = spl[1];
		ret.expirationAge = Long.valueOf(spl[2]);

		return ret;

	}
	
	public class AccessTokenDetails {
		
		private String clientId;
		private String userName;
		private long expirationAge;
		private String ipAddress;
		private String customData;

		public String getIpAddress() {
			return ipAddress;
		}

		public void setIpAddress(String ipAddress) {
			this.ipAddress = ipAddress;
		}

		public String getCustomData() {
			return customData;
		}

		public void setCustomData(String customData) {
			this.customData = customData;
		}

		public String getClientId() {
			return clientId;
		}

		public void setClientId(String clientId) {
			this.clientId = clientId;
		}

		public String getUserName() {
			return userName;
		}

		public void setUserName(String userName) {
			this.userName = userName;
		}

		public long getExpirationAge() {
			return expirationAge;
		}

		public void setExpirationAge(long expirationAge) {
			this.expirationAge = expirationAge;
		}
	}
	
}
