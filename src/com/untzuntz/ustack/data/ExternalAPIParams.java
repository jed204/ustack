package com.untzuntz.ustack.data;

import java.io.IOException;
import java.util.Date;

import org.bson.types.ObjectId;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;

import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.untzuntz.ustack.main.UOpts;

/**
 * An IOS / Android (coming soon) push queue
 * 
 * @author jdanner
 *
 */
public class ExternalAPIParams extends UntzDBObject {

	private static final long serialVersionUID = 1L;

	public String getCollectionName() { return "extApiParams"; }
	
	private ExternalAPIParams() {
		put("created", new Date());
	}
	
	public ExternalAPIParams(DBObject obj)
	{
		putAll(obj);
	}
	
	public String getId() {
		return get("_id") + "";
	}
	
	/** Gets the DB Collection for the UserAccount object */
	public static DBCollection getDBCollection() {
		return new ExternalAPIParams().getCollection();
	}
	
	private static final String bapw() {
		return UOpts.getAppName() + "!2012@&#**!HSAFAB";
	}
	
	public String getPublicKey() {
		return getString("publicKey");
	}
	
	public String getPrivateKey() {
		StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
		encryptor.setPassword(bapw());
		return encryptor.decrypt( getString("privateKey") );
	}
	
	public static ExternalAPIParams createExternalAPIParams(String name, String publicKey, String privateKey) throws IOException
	{
		StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
		encryptor.setPassword(bapw());
		
		ExternalAPIParams branding = getByName(name);
		if (branding == null)
		{
			branding = new ExternalAPIParams();
			branding.put("name", name);
		}
		
		branding.put("publicKey", publicKey);
		branding.put("privateKey", encryptor.encrypt(privateKey));
		
		return branding;
	}
	
	
	public static ExternalAPIParams getById(String id)
	{
		if (id == null)
			return null;
		
		DBObject book = new ExternalAPIParams().getCollection().findOne(BasicDBObjectBuilder.start("_id", new ObjectId(id)).get());
		
		if (book == null)
			return null;
		
		return new ExternalAPIParams(book);
	}
	
	public static ExternalAPIParams getByName(String name)
	{
		DBObject search = new BasicDBObject("name", name);
		
		DBCollection col = new ExternalAPIParams().getCollection();
		DBObject obj = col.findOne( search );
		
		if (obj != null)
		{
			ExternalAPIParams ret = new ExternalAPIParams(obj);
			return ret;
		}
		
		return null;
	}


}
