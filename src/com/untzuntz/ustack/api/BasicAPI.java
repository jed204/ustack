package com.untzuntz.ustack.api;

import static org.jboss.netty.handler.codec.http.HttpHeaders.setContentLength;
import static org.jboss.netty.handler.codec.http.HttpVersion.HTTP_1_1;

import org.apache.log4j.Logger;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.untzuntz.ustack.aaa.Authentication;
import com.untzuntz.ustack.data.UniqueReference;
import com.untzuntz.ustack.data.UserAccount;
import com.untzuntz.ustack.main.UForgotPasswordSvc;
import com.untzuntz.ustackserverapi.APIResponse;
import com.untzuntz.ustackserverapi.CallParameters;
import com.untzuntz.ustackserverapi.MethodDefinition;

public class BasicAPI {

    static Logger           		logger               	= Logger.getLogger(BasicAPI.class);
    
    public void hashtest(MethodDefinition def, Channel channel, HttpRequest req, CallParameters callParams) throws Exception
    {
    	String serverSig = callParams.getRequestSignature(def.getHashKey());
    	
    	DBObject resp = new BasicDBObject("serverSignature", serverSig);
    	String clientSig = "(NOT PROVIDED)";  
    	if (callParams.getParameter("sig") != null)
    		clientSig = callParams.getParameter("sig");
    	
    	resp.put("clientSignature", clientSig);
    	resp.put("signatureStatus", "Error");
		if (clientSig.equals(serverSig))
	    	resp.put("signatureStatus", "Match");
			
		APIResponse.httpOk(channel, resp);
    }

    public void confirmEmailAddress(MethodDefinition def, Channel channel, HttpRequest req, CallParameters callParams) throws Exception
    {
		UniqueReference ref = UniqueReference.getByUID( callParams.getParameter("uid") );
		if (ref != null)
		{
			logger.info("Loaded unique reference from uid : " + callParams.getParameter("uid"));
			
			ref.put("used", "true");
			UniqueReference.save(ref, ref.getString("userName"));
			
			UserAccount user = UserAccount.getUser(ref.getString("userName"));
			if (user != null)
			{
				user.put("emailConfirmed", "true");
				user.save(user.getUserName());
			}
			
			if (ref.get("successUrl") != null)
			{
				HttpResponse res = new DefaultHttpResponse(HTTP_1_1, HttpResponseStatus.TEMPORARY_REDIRECT);
				res.setHeader("Location", (String)ref.get("successUrl"));
				setContentLength(res, res.getContent().readableBytes());
				channel.write(res).addListener(ChannelFutureListener.CLOSE);
			}
			else
	    		APIResponse.httpOk(channel, APIResponse.success(""));
			
			return;
		}
		APIResponse.httpError(channel, APIResponse.error("Error during email confirmation process"));
    }
    
	public void forgotPassword(MethodDefinition def, Channel channel, HttpRequest req, CallParameters callParams) throws Exception
	{
		UserAccount usrAcct = UserAccount.getUser(callParams.getParameter("userName"));
		if (usrAcct != null && !usrAcct.isDisabled())
		{
			try {
				UForgotPasswordSvc.sendForgotPassword(usrAcct.getUserName(), getClientIPObject(channel, req), (String)def.getData("url"), usrAcct.getUserName(), usrAcct.getPrimaryEmail(), usrAcct.getFullName(), (String)def.getData("template"));
			} catch (Exception e) {
	    		logger.warn("User [" + usrAcct.getUserName() + "] hit an exception during forgot password", e);
	    		APIResponse.httpError(channel, APIResponse.error("Error during forgot password request"));
	    		return;
			}
    		APIResponse.httpOk(channel, APIResponse.success(""));
		}
		else
    		APIResponse.httpError(channel, APIResponse.error("Error during forgot password request"));

	}
	
	public void resetPassword(MethodDefinition def, Channel channel, HttpRequest req, CallParameters callParams) throws Exception
	{
		UserAccount usrAcct = UserAccount.getUser(callParams.getParameter("userName"));
		if (usrAcct != null && !usrAcct.isDisabled())
		{
			try {
				UForgotPasswordSvc.sendResetPassword(usrAcct.getUserName(), getClientIPObject(channel, req), usrAcct.getUserName(), usrAcct.getPrimaryEmail(), usrAcct.getFullName(), (String)def.getData("template"), null);
			} catch (Exception e) {
	    		logger.warn("User [" + usrAcct.getUserName() + "] hit an exception during forgot password", e);
	    		APIResponse.httpError(channel, APIResponse.error("Error during reset password request"));
	    		return;
			}
    		APIResponse.httpOk(channel, APIResponse.success(""));
		}
		else
    		APIResponse.httpError(channel, APIResponse.error("Error during reset password request"));

	}
	
	public void authentication(MethodDefinition def, Channel channel, HttpRequest req, CallParameters callParams) throws Exception
	{
		/*
		 * 
		 * Main Authentication / Authorization
		 * 
		 */
		if (callParams.getUserName() == null || callParams.getParameter("accesscode") == null || callParams.getUserName().length() == 0 || callParams.getParameter("accesscode").length() == 0)
			throw new Exception("Invalid API Request");
		
		if (callParams.getParameter("deviceId") == null)
		{
			Authentication.authenticateUser(callParams.getUserName(), callParams.getParameter("accesscode"));
			
    		APIResponse.httpOk(channel, new BasicDBObject("status", "SUCCESS"));
		}
		
	}

	public static DBObject getClientIPObject(Channel channel, HttpRequest req)
	{
    	String clientIP = req.getHeader("X-Real-IP");
		if (clientIP == null)
			clientIP = req.getHeader("X-Forwarded-For");
		if (clientIP == null)
			clientIP = channel.getRemoteAddress().toString();
		String clientCountry = req.getHeader("X-Country-Code");
		if (clientCountry == null)
			clientCountry = "UNK";
		
		logger.info("IP: " + clientIP + " -> " + clientCountry);

		DBObject ret = new BasicDBObject();
		ret.put("requestIP", clientIP);
		ret.put("requestCountry", clientCountry);
		return ret;		
	}


}
