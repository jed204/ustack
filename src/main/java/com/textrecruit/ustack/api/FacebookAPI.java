package com.textrecruit.ustack.api;

import java.io.Writer;
import java.net.URI;
import java.net.URLEncoder;

import com.textrecruit.ustack.exceptions.AuthExceptionUserPasswordMismatch;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;

import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import com.textrecruit.ustack.util.BasicUtils;

public class FacebookAPI {

	protected static Logger logger = Logger.getLogger(FacebookAPI.class);
	
	public static DBObject validateUserAccessToken(String userId, String token) throws Exception
	{
		if (userId == null || token == null)
			throw new AuthExceptionUserPasswordMismatch();
			
		URI uri = URIUtils.createURI("https", "graph.facebook.com", 443, "/me", "access_token=" + URLEncoder.encode(token, "UTF-8"), null);
		logger.info("URL: " + uri);
		
		DefaultHttpClient client = new DefaultHttpClient();  
		HttpGet get = new HttpGet(uri);

		HttpResponse response = client.execute(get);

		HttpEntity e = response.getEntity();
		Writer writer = BasicUtils.getResponseString(e);
		DBObject o = (DBObject)JSON.parse(writer.toString());
		logger.info("Facebook response: " + o);
		
		if (o.get("error") != null)
			throw new AuthExceptionUserPasswordMismatch();
			
		String userName = (String)o.get("username");
		String id = (String)o.get("id");
		if (userName.equalsIgnoreCase(userId))
			return o;
		if (id.equalsIgnoreCase(userId))
			return o;
		
		throw new AuthExceptionUserPasswordMismatch();
	}
	
}
