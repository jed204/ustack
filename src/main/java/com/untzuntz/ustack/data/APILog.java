package com.untzuntz.ustack.data;

import java.net.InetAddress;
import java.util.Date;

import org.apache.log4j.Logger;

import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.untzuntz.ustack.main.UAppCfg;
import com.untzuntz.ustack.main.UOpts;

/**
 * Tracks API requests and responses for debugging purposes
 * 
 * @author jdanner
 *
 */
public class APILog extends UntzDBObject {

	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(APILog.class);
	private static final String apiHostname;
	
	static 
	{
		String tHost = null;
		try { tHost = InetAddress.getLocalHost().getHostName(); } catch (Exception err) { tHost = "Unknown"; }
		apiHostname = tHost;
	}
	
	public String getCollectionName() { return "apiLogs"; }
	
	public static void generateCollection()
	{
		int maxMessages = 50000;
		int maxSize = maxMessages * 2048;
		
		APILog l = new APILog();
		String colName = l.getCollectionName();
		logger.info("Checking for collection '" + getDatabaseName() + "." + colName + "' ...");
		
		DB db = MongoDB.getMongo().getDB(getDatabaseName());
		DBCollection col = db.getCollection(colName);
		
		if (col == null || !col.isCapped())
		{
			logger.info("Creating '" + colName + "' collection...");
			DBObject cc = BasicDBObjectBuilder.start("capped", true).add("size", maxSize).add("max", maxMessages).get();
			db.createCollection(colName, cc);
		}
		else
			logger.info(colName + " exists.");
	}
	
	
	public static DBCollection getDBCollection() {
		return new APILog().getCollection();
	}

	public APILog() {
		put("date", new Date());
		put("apiHost", apiHostname);
	}
	
	public String getId() {
		return get("_id") + "";
	}

	/** Return the name of the database that houses the 'users' collection */
	public static final String getDatabaseName() {
		
		if (UOpts.getString(UAppCfg.DATABASE_ADDRBOOK_COL) != null)
			return UOpts.getString(UAppCfg.DATABASE_ADDRBOOK_COL);
		
		return UOpts.getAppName();
		
	}
	
}
