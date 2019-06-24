package com.textrecruit.ustack.main;

import java.util.Calendar;
import java.util.Date;

import com.textrecruit.ustack.aaa.Authentication;
import com.textrecruit.ustack.data.NotificationTemplate;
import com.textrecruit.ustack.data.UniqueReference;
import com.textrecruit.ustack.data.UserAccount;
import org.apache.log4j.Logger;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class UForgotPasswordSvc {
	
    static Logger           		logger               	= Logger.getLogger(UForgotPasswordSvc.class);

	public static void sendResetPassword(String requestedBy, DBObject extras, String userName, String toEmail, String toName, String templateName, String password)
	{
		UserAccount user = UserAccount.getUser(userName);
		if (user == null)
			return;
		
		try {
			
			if (password == null)
			{
				password = Authentication.generatePassword();
				user.setPassword(requestedBy, password);
				user.save(requestedBy);
			}
			
			user.put("newPassword", password);
			
			NotificationTemplate template = NotificationTemplate.getNotificationTemplate(templateName);
			UNotificationSvc svc = new UNotificationSvc();
			svc.setData("user", user);
	
			DBObject endpoint = new BasicDBObject();
			endpoint.put("destinationName", toName);
			endpoint.put("destination", toEmail);
	
			svc.sendEmail(null, template, endpoint);
		} catch (Exception er) {
			logger.error("Failed to reset password for user [" + userName + "]", er);
		}
	}
	
	public static UniqueReference sendForgotPassword(String requestedBy, DBObject extras, String urlPrefix, String userName, String toEmail, String toName, String templateName)
	{
		NotificationTemplate template = NotificationTemplate.getNotificationTemplate(templateName);
		if (template == null)
		{
			logger.error(String.format("Unknown forgot password template [%s]", templateName));
			throw new IllegalArgumentException(String.format("Unknown forgot password template [%s]", templateName));
		}

		UniqueReference uniqRef = UniqueReference.createUniqRef("pwreset");
		if (uniqRef == null)
		{
			logger.error(String.format("Unknown unique reference action [%s]", "pwreset"));
			throw new IllegalArgumentException(String.format("Unknown unique reference action [%s]", "pwreset"));
		}

		// set expires
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.HOUR, 24);
		
		uniqRef.put("name", toName);
		uniqRef.put("email", toEmail);
		uniqRef.put("userName", userName);
		uniqRef.put("time", new Date());
		uniqRef.put("expires", cal.getTime());
		uniqRef.put("requestedBy", requestedBy);
		if (urlPrefix != null && urlPrefix.indexOf("%s") > -1)
			uniqRef.put("url", String.format(urlPrefix, uniqRef.getUid()));
		else
			uniqRef.put("url", urlPrefix + "/rdr?act=" + uniqRef.getString("actionName") + "&uid=" + uniqRef.getUid());
		if (extras != null)
			uniqRef.putAll(extras);
		uniqRef.save(requestedBy);

		UNotificationSvc svc = new UNotificationSvc();
		svc.setData("resetinfo", uniqRef);

		DBObject endpoint = new BasicDBObject();
		endpoint.put("destinationName", toName);
		endpoint.put("destination", toEmail);

		svc.sendEmail(null, template, endpoint);
		
		return uniqRef;
	}
	
}
