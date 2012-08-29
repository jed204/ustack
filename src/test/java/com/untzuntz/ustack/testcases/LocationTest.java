package com.untzuntz.ustack.testcases;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.FileInputStream;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.untzuntz.ustack.data.Country;
import com.untzuntz.ustack.data.MongoDB;
import com.untzuntz.ustack.data.UDataMgr;

public class LocationTest extends UStackTestCaseBase {
	
	protected static Logger logger = Logger.getLogger(LocationTest.class);

	public LocationTest()
	{
		super();
	}
	
	@Test public void testCountryLoad() throws Exception
	{
		Country.loadCountriesToDB(new FileInputStream("src/main/resources/com/untzuntz/ustack/resources/countries.csv"));
		assertEquals(272, Country.getCountryCount());
	}
	
	@Test public void testLatLongLookup()
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
		assertEquals( Double.valueOf(39.0956923d), (Double)loc.get("lat") );
		assertEquals( Double.valueOf(-84.5244312d), (Double)loc.get("lng") );
	}
	
}
