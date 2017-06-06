package com.untzuntz.ustack.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
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
				
				MongoClientOptions.Builder clientOpts = MongoClientOptions.builder();
				if (UOpts.getBool(UAppCfg.MONGO_DB_KEEPALIVE))
					clientOpts.socketKeepAlive(true);
				if (UOpts.getInt(UAppCfg.MONGO_DB_CONNECTIONS_PER_HOST) > 0)
				{
					clientOpts.connectionsPerHost(UOpts.getInt(UAppCfg.MONGO_DB_CONNECTIONS_PER_HOST));
					logger.info(String.format("MongoDB Connections Per Host: %d", UOpts.getInt(UAppCfg.MONGO_DB_CONNECTIONS_PER_HOST)));
				}
				
				// setup the actual mongo object
				
				if (UOpts.getString(UAppCfg.MONGO_DB_AUTH_DATABASE) != null)
				{
					MongoCredential credential = MongoCredential.createCredential(UOpts.getString(UAppCfg.MONGO_DB_AUTH_USERNAME), 
							UOpts.getString(UAppCfg.MONGO_DB_AUTH_DATABASE), UOpts.getString(UAppCfg.MONGO_DB_AUTH_PASSWORD).toCharArray());
					
					logger.info("U: " + credential.getUserName() + " / " + UOpts.getString(UAppCfg.MONGO_DB_AUTH_DATABASE));
					
					m = new MongoClient(addrs, Arrays.asList(credential), clientOpts.build());
				}
				else {
					m = new MongoClient(addrs, clientOpts.build());
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
