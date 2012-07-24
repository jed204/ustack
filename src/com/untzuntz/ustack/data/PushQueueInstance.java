package com.untzuntz.ustack.data;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Date;

import org.apache.log4j.Logger;
import org.bson.types.Binary;
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
public class PushQueueInstance extends UntzDBObject {

	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(PushQueueInstance.class);

	public String getCollectionName() { return "pushQueueInst"; }
	
	private PushQueueInstance() {
		put("created", new Date());
	}
	
	public PushQueueInstance(DBObject obj)
	{
		putAll(obj);
	}
	
	public int getThreads() {
		return getInt("threads", 5);
	}
	
	public boolean isProduction() {
		if ("true".equalsIgnoreCase(getString("production")))
			return true;
		
		return false;
	}
	
	public String getId() {
		return get("_id") + "";
	}
	
	/** Gets the DB Collection for the UserAccount object */
	public static DBCollection getDBCollection() {
		return new PushQueueInstance().getCollection();
	}
	
	private static final String bapw() {
		return UOpts.getAppName() + "!2012@&!*!*HAA";
	}
	
	public String getPassword() {
		StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
		encryptor.setPassword(bapw());
		return encryptor.decrypt( getString("password") );
	}
	
	public static PushQueueInstance createKeyStore(String name, InputStream in, String password) throws IOException
	{
		StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
		encryptor.setPassword(bapw());
		
		byte[] data = new byte[16384];
		ByteBuffer buf = ByteBuffer.allocate(128000);
		int read = 1;
		while ((read = in.read(data)) > -1)
			buf.put(data, 0, read);
		
		Binary bin = new Binary(buf.array());
		
		PushQueueInstance branding = getByName(name);
		if (branding == null)
		{
			branding = new PushQueueInstance();
			branding.put("name", name);
		}
		
		branding.put("password", encryptor.encrypt(password));
		branding.put("data", bin);
		return branding;
	}
	
	public byte[] getBinaryData()
	{
		return (byte[])get("data");
	}
	
	public static PushQueueInstance getById(String id)
	{
		if (id == null)
			return null;
		
		DBObject book = new PushQueueInstance().getCollection().findOne(BasicDBObjectBuilder.start("_id", new ObjectId(id)).get());
		
		if (book == null)
			return null;
		
		return new PushQueueInstance(book);
	}
	
	public static PushQueueInstance getByName(String name)
	{
		DBObject search = new BasicDBObject("name", name);
		
		DBCollection col = new PushQueueInstance().getCollection();
		DBObject obj = col.findOne( search );
		
		if (obj != null)
		{
			PushQueueInstance ret = new PushQueueInstance(obj);
			logger.info("Push Queue Instance Found => " + ret);
			return ret;
		}
		
		return null;
	}


}
