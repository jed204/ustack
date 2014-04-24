package com.untzuntz.ustack.main;

import java.io.File;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import com.sun.jersey.core.util.MultivaluedMapImpl;

public class Emailer {

	private static Logger logger = Logger.getLogger(Emailer.class);

	/**
	 * Handles sending an email from the application
	 * 
	 * @param from
	 * @param subject
	 * @param message
	 * @param recipients
	 * @throws MessagingException
	 */
	public static void postMail(String to, String from, String fromName, String subject, String message, String htmlMessage) throws AddressException
	{
		postMail(new InternetAddress[] { new InternetAddress(to) }, null, null, from, fromName, subject, message, htmlMessage, null, null);
	}
	
	public static void postMail(String to, String from, String fromName, String subject, String message, String htmlMessage, Hashtable<String,File> attachments, String campaignId) throws AddressException
	{
		postMail(new InternetAddress[] { new InternetAddress(to) }, null, null, from, fromName, subject, message, htmlMessage, attachments, campaignId);
	}
	
	
	
	/**
	 * Handles sending an email from the application with additional options
	 * @param to
	 * @param cc
	 * @param bcc
	 * @param from
	 * @param subject
	 * @param message
	 * @throws MessagingException
	 */
	public static void postMail(InternetAddress[] to, InternetAddress[] cc, InternetAddress[] bcc, String from, String fromName, String subject, String message, String htmlMessage) throws AddressException
	{
		postMail(to, cc, bcc, from, fromName, subject, message, htmlMessage, null, null);
	}
	
	public static void postMail(InternetAddress[] to, InternetAddress[] cc, InternetAddress[] bcc, String from, String fromName, String subject, String message, String htmlMessage, Hashtable<String,File> attachments, String campaignId) throws AddressException
	{
		if ("mailgun".equalsIgnoreCase(UOpts.getString(UAppCfg.EMAIL_MODE)))
		{
			mailGun(to, cc, bcc, from, fromName, subject, message, htmlMessage, attachments, campaignId);
			return;
		}
		
		boolean debug = false;
		String emailHost = UOpts.getString(UAppCfg.EMAIL_HOST);
		String emailPort = UOpts.getString(UAppCfg.EMAIL_PORT);
		
		if (emailPort == null || emailPort.length() == 0)
			emailPort = "25";

		if (emailHost == null || emailHost.length() == 0)
			logger.info("Email Destination Not Set! Property name: " + UAppCfg.EMAIL_HOST);
		else
			logger.info("Email Destination Info => " + emailHost + ":" + emailPort);

		// Set the host smtp address
		Properties props = new Properties();
		props.put("mail.smtp.host", emailHost);
	    props.put("mail.smtp.port", emailPort);
	    
	    if (UOpts.getBool(UAppCfg.EMAIL_TLS))
	    {
	    	props.put("mail.smtp.starttls.enable","true");
	    }
	    
	    if (UOpts.getBool(UAppCfg.EMAIL_SECURE_SSL))
	    {
	    	props.put("mail.smtp.EnableSSL.enable","true");
	        props.put("mail.smtp.socketFactory.port", emailPort);
	        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
	        props.put("mail.smtp.socketFactory.fallback", "false");
	    }
	    
	    Session session = null;
	    if (UOpts.getBool(UAppCfg.EMAIL_AUTH))
	    {
	    	props.put("mail.smtp.auth", "true");
	    	Authenticator auth = new Authenticator() {
	    		public PasswordAuthentication getPasswordAuthentication() {
	    			return new PasswordAuthentication(UOpts.getString(UAppCfg.EMAIL_AUTH_USER), UOpts.getString(UAppCfg.EMAIL_AUTH_PASS));
	    		}
	    	};
    		session = Session.getDefaultInstance(props, auth);
	    }
	    else
			session = Session.getDefaultInstance(props, null);

		// create some properties and get the default Session
		session.setDebug(debug);

		// create a message
		Message msg = new MimeMessage(session);

		// set the from and to address
		InternetAddress addressFrom = null;
		try {
			addressFrom = new InternetAddress(from, fromName);;
			msg.setFrom(addressFrom);
	
			msg.setRecipients(Message.RecipientType.TO, to);
			if (cc != null)
				msg.setRecipients(Message.RecipientType.CC, cc);
			if (bcc != null)
				msg.setRecipients(Message.RecipientType.BCC, cc);
	
			// Setting the Subject and Content Type
			msg.addHeader("X-UStack", "1.0");
			msg.setSubject(MimeUtility.encodeText(subject, "utf-8", "B"));
			msg.setSentDate(new Date());
			
			if (htmlMessage == null && (attachments == null || attachments.size() == 0))
				msg.setContent(message, "text/plain; charset=UTF-8");
			else
			{
				Multipart mp = new MimeMultipart("alternative");
				
				// Text Body
				MimeBodyPart text = new MimeBodyPart();
			    text.setText(message);
			    text.setHeader("MIME-Version" , "1.0" );
			    text.setHeader("Content-Type" , "text/plain; charset=UTF-8");
		        mp.addBodyPart(text);

			    if (htmlMessage != null)
			    {
			    	// HTML Body
				    MimeBodyPart html = new MimeBodyPart();
				    html.setContent(htmlMessage, "text/html");
				    html.setHeader("MIME-Version" , "1.0" );
				    html.setHeader("Content-Type" , "text/html" );
			        mp.addBodyPart(html);
			    }

			    // Add attachments last
			    if (attachments != null)
			    {
			    	Enumeration<String> enu = attachments.keys();
			    	while (enu.hasMoreElements())
			    	{
			    		String name = enu.nextElement();
			    		File src = attachments.get(name);
			    		if (src.exists())
			    		{
				    		logger.info("\t- Adding file [" + src + "] to message as '" + name + "'");
				    		
				    		MimeBodyPart attachmentPart = new MimeBodyPart();
					    	attachmentPart.setDataHandler(new DataHandler(new FileDataSource(src)));
					    	attachmentPart.setFileName(name);
					    	mp.addBodyPart(attachmentPart);
			    		}
			    		else
			    			logger.error("\t- Unable to locate file [" + src + "] for attachment to message");
			    	}
			    }

		        // Set Multipart as the message's content
		        msg.setContent(mp);
			}
			
			msg.saveChanges();

			logger.info("Sending Email [" + from + " => " + to[0] + "] // Subj: '" + subject + "' // " + message + " via " + props.get("mail.smtp.host") + ":" + props.get("mail.smtp.port"));
			
			Transport transport = session.getTransport("smtp");
			transport.connect((String)props.get("mail.smtp.host"), Integer.valueOf((String)props.get("mail.smtp.port")), null, null);
			transport.sendMessage(msg, msg.getAllRecipients());
			transport.close();
		    
		} catch (Exception err) {
			// TODO: Save message and resend later
			logger.error("Email delivery failed", err);
		}
	}
	
	public static void mailGun(InternetAddress[] to, InternetAddress[] cc, InternetAddress[] bcc, String from, String fromName, String subject, String message, String htmlMessage, Hashtable<String,File> attachments, String campaignId) throws AddressException
	{
		Client client = Client.create();
	    client.addFilter(new HTTPBasicAuthFilter("api", UOpts.getString(UAppCfg.MAILGUN_KEY)));
	 
	    MultivaluedMapImpl formData = new MultivaluedMapImpl();
	 
	    WebResource webResource = client.resource(String.format("https://api.mailgun.net/v2/%s/messages", UOpts.getString(UAppCfg.MAILGUN_DOMAIN)));

	    String toStr = getList(to);
	    String ccStr = getList(cc);
	    String bccStr = getList(bcc);
	    
	    formData = new MultivaluedMapImpl();
	    formData.add("from", String.format("%s <%s>", fromName ,from));
	    formData.add("to", toStr);
	    if (cc != null)
	    	formData.add("cc", ccStr);
	    if (bcc != null)
	    	formData.add("bcc", bccStr);
	    formData.add("subject", subject);
	    formData.add("text", message);
	    if (htmlMessage != null)
	    	formData.add("html", htmlMessage);
	    if (campaignId != null)
	    	formData.add("o:campaign", campaignId);
	    
//	    MultiPart multiPart = new MultiPart();
//	    multiPart.bodyPart(new BodyPart(project, MediaType.APPLICATION_XML_TYPE)).
	    
	    ClientResponse clientResponse = webResource.type(MediaType.APPLICATION_FORM_URLENCODED).post(ClientResponse.class, formData);
	    String output = clientResponse.getEntity(String.class);
	    
		logger.info("Sending Email [" + from + " => " + to[0] + "] // Subj: '" + subject + "' // " + message + " via MAILGUN");

	    logger.info(String.format("MailGun Response: %s", output));
	}
	
	private static String getList(InternetAddress[] addrs)
	{
		if (addrs == null || addrs.length == 0)
			return null;
		
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < addrs.length; i++)
		{
			InternetAddress addr = addrs[i];
			buf.append(addr.toString());
			if ((i + 1) < addrs.length)
				buf.append(",");
		}
		return buf.toString();
	}
}
