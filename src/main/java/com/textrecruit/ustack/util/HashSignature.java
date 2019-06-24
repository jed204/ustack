package com.textrecruit.ustack.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.util.Hashtable;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * Collection of methods to hash and sign data
 * 
 * @author jdanner
 *
 */
public class HashSignature {

    /**
     * All strings are handled as UTF-8
     */
    private static final String UTF8_CHARSET = "UTF-8";
    
    /**
     * The HMAC algorithm required by Amazon
     */
    public static final String HMAC_SHA256_ALGORITHM = "HmacSHA256";
    public static final String SHA256_ALGORITHM = "SHA-256";
	
    private static final Hashtable<String,Mac> keys = new Hashtable<String,Mac>();
    
    
    public static String sha256Digest(String input) throws Exception {
    	
    	MessageDigest md = MessageDigest.getInstance("SHA-256");

        byte[] hash = md.digest(input.getBytes());

        StringBuffer sb = new StringBuffer();
        for(byte b : hash) {
            sb.append(Integer.toHexString(b & 0xff));
        }

        return sb.toString();
    }
    
    public static String sha256Hash(String key, String stringToSign) {
    	
		Mac mac = keys.get(key);
		if (mac == null)
		{
			try {
		        byte[] secretyKeyBytes = key.getBytes(UTF8_CHARSET);
		        SecretKeySpec secretKeySpec = new SecretKeySpec(secretyKeyBytes, HMAC_SHA256_ALGORITHM);
		        mac = Mac.getInstance(HMAC_SHA256_ALGORITHM);
		        mac.init(secretKeySpec);
		        keys.put(key, mac);
			} catch (Exception e) {
				return null;
			}
		}
    	
        StringBuffer signature = new StringBuffer();
        
        byte[] data;
        byte[] rawHmac;
        try {
            data = stringToSign.getBytes(UTF8_CHARSET);
            rawHmac = mac.doFinal(data);

            for(byte b : rawHmac) {
            	signature.append(Integer.toHexString(b & 0xff));
            }
            
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(UTF8_CHARSET + " is unsupported!", e);
        }
        return signature.toString();
    }
	
}
