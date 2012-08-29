package com.untzuntz.ustack.data;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.Mongo;
import com.mongodb.MongoOptions;
import com.mongodb.ReadPreference;
import com.mongodb.ServerAddress;
import com.untzuntz.ustack.main.UAppCfg;
import com.untzuntz.ustack.main.UOpts;

/**
 * Singleton for access to MongoDB
 * 
 * @author jdanner
 *
 */
public class MongoDB {

	private static Logger logger = Logger.getLogger(MongoDB.class);

	private static Mongo m;
	
	/**
	 * Direct access to the Mongo singleton
	 * 
	 * @return
	 */
	public static Mongo getMongo() { 
	
		if (m == null)
		{
			logger.info("Setup MongoDB Singleton...");
			try { 

				long start = System.currentTimeMillis();
				List<ServerAddress> addrs = new ArrayList<ServerAddress>();

				String connStr = UOpts.getString(UAppCfg.MONGO_DB_HOST);
				String[] split = connStr.split(",");
				for (String conn : split)
				{
					int idx = conn.indexOf(":");
					if (idx == -1)
						addrs.add( new ServerAddress( conn , 27017 ) );
					else
					{
						String name = conn.substring(0, idx);
						String port = conn.substring(idx + 1);
						addrs.add( new ServerAddress( name , Integer.valueOf(port) ) );
					}
				}
				
				MongoOptions options = new MongoOptions(); 
				if (UOpts.getBool(UAppCfg.MONGO_DB_KEEPALIVE))
					options.socketKeepAlive = true;
				if (UOpts.getInt(UAppCfg.MONGO_DB_CONNECTIONS_PER_HOST) > 0)
					options.connectionsPerHost = UOpts.getInt(UAppCfg.MONGO_DB_CONNECTIONS_PER_HOST);
				if (UOpts.getBool(UAppCfg.MONGO_DB_AUTORETRY))
					options.autoConnectRetry = true;

				// setup the actual mongo object
				m = new Mongo(addrs, options);
				
				if (UOpts.getString(UAppCfg.MONGO_DB_AUTH_DATABASE) != null)
				{
					DB db = m.getDB(UOpts.getString(UAppCfg.MONGO_DB_AUTH_DATABASE));
					boolean auth = db.authenticate(UOpts.getString(UAppCfg.MONGO_DB_AUTH_USERNAME), UOpts.getString(UAppCfg.MONGO_DB_AUTH_PASSWORD).toCharArray());
					logger.info("Database Authentication Status: " + auth + " [" + UOpts.getString(UAppCfg.MONGO_DB_AUTH_USERNAME) + "@" + UOpts.getString(UAppCfg.MONGO_DB_AUTH_DATABASE) + "]");
				}
				
				if (UOpts.getBool(UAppCfg.MONGO_DB_READS_OK))
				{
					logger.info("Setting Read Preference: SECONDARY");
					m.setReadPreference(ReadPreference.SECONDARY);
				}
				
				logger.info("MongoDB Singleton Setup Complete - " + (System.currentTimeMillis() - start) + " ms ==> " + addrs);
				
			} catch (Exception e) {
				logger.error("Failed to load mongodb : " + e.getMessage(), e);
			}
		}

		return m;
		
	}
	
	/**
	 * Returns a collection from the provided database
	 * 
	 * @param database
	 * @param collection
	 * @return
	 */
	public static DBCollection getCollection(String database, String collection)
	{
		if (database == null || collection == null)
			return null;
		
		Mongo m = getMongo();
		if (m == null)
			return null;
		DB db = m.getDB( database );
		return db.getCollection(collection);
	}

}
