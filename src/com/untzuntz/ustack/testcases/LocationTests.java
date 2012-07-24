package com.untzuntz.ustack.testcases;

import java.io.FileInputStream;
import java.util.Iterator;
import java.util.Set;

import junit.framework.TestCase;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.untzuntz.ustack.data.Country;
import com.untzuntz.ustack.data.MongoDB;
import com.untzuntz.ustack.data.UDataMgr;
import com.untzuntz.ustack.main.ApplicationInstance;

public class LocationTests extends TestCase {
	
	protected static Logger logger = Logger.getLogger(LocationTests.class);

	public LocationTests()
	{
		System.setProperty("TestCase", "true");
	}
	
	public void testInit()
	{
		BasicConfigurator.configure();
		
		Mongo m = MongoDB.getMongo();
		if (!m.getAddress().sameHost("localhost")) // verify we are connectected locally
		{
			logger.error("Database not hosted locally - stopping test");
			System.exit(0);
		}
		
		// output some info about the database
		DBCollection col = MongoDB.getCollection(ApplicationInstance.getAppName(), "users");
		DB db = col.getDB();
		
		logger.info("Database Name: " + db.getName() + " (" + db.getCollectionNames().size() + " collections)");
		
		if (!"testCase".equalsIgnoreCase(db.getName()))
		{
			logger.error("Invalid database selected : please verify TestCase variable : " + db.getName());
			System.exit(0);
		}
		
		Set<String> colls = db.getCollectionNames();
		Iterator<String> it = colls.iterator();
		
		while (it.hasNext())
			logger.info("   - Collection: " + it.next());

		if (colls.size() > 0)
			db.dropDatabase();
	}

	public void testCountryLoad() throws Exception
	{
		Country.loadCountriesToDB(new FileInputStream("src/com/untzuntz/ustack/resources/countries.csv"));
		assertEquals(272, Country.getCountryCount());
	}
	
	public void testLatLongLookup()
	{
		DBObject addr = new BasicDBObject();
		addr.put("address1", "700 West Pete Rose Way");
		addr.put("address2", "Suite 436");
		addr.put("city", "Cincinnati");
		addr.put("state", "Ohio");
		addr.put("postalCode", "45203");
		addr.put("country", "United States");
		
		UDataMgr.calculateLatLong(addr);
		DBObject loc = (DBObject)addr.get("loc");
		assertNotNull( loc );
		assertEquals( 39.096179d, (Double)loc.get("lat") );
		assertEquals( -84.525907d, (Double)loc.get("lng") );
	}
	
}
