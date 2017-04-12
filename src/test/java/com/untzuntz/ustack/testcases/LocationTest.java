package com.untzuntz.ustack.testcases;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.FileInputStream;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.untzuntz.ustack.data.Country;
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
	
}
