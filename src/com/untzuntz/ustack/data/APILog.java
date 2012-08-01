package com.untzuntz.ustack.data;

import java.net.InetAddress;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.untzuntz.ustack.main.ApplicationInstance;
import com.untzuntz.ustack.main.UAppCfg;
import com.untzuntz.ustack.main.UOpts;
import com.untzuntz.ustackserverapi.CallParameters;

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
		
		return ApplicationInstance.getAppName();
		
	}

	public static APILog create(CallParameters params, Channel chl, HttpRequest req, HttpResponse res, String responseBody)
	{
		APILog ret = new APILog();
		
		// add method
		ret.put("method", req.getMethod());
		ret.put("uri", params.getPath());

		// IP Data
		String realSourceIP = req.getHeader("X-Real-IP");
		if (realSourceIP == null)
			realSourceIP = chl.getRemoteAddress().toString();
		String countryCode = req.getHeader("X-Country-Code");

		ret.put("sourceIP", realSourceIP);
		ret.put("sourceCountry", countryCode);
		
		// request headers
		List<Map.Entry<String, String>> reqHeaders = req.getHeaders();
		BasicDBList headerList = new BasicDBList();
		for (Map.Entry<String, String> entry : reqHeaders)
			headerList.add(new BasicDBObject( entry.getKey(), entry.getValue() ));
		ret.put("requestHeaders", headerList);

		// request parameters
		ret.put("requestParams", params.getParameterList());
		
		// response code
		ret.put("responseCode", res.getStatus().getCode());
		
		// response headers
		Iterator<String> headers = res.getHeaderNames().iterator();
		headerList = new BasicDBList();
		while (headers.hasNext()) {
			String hdr = headers.next();
			headerList.add(new BasicDBObject( hdr, res.getHeader(hdr) ));
		}
		ret.put("responseHeaders", headerList);
		
		// response body
		ret.put("responseBody", responseBody);
		
		ret.save();

		return ret;
	}
	
}
