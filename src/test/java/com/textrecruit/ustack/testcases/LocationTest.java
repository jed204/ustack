package com.textrecruit.ustack.testcases;

import com.textrecruit.ustack.data.Country;
import org.apache.log4j.Logger;
import org.junit.Test;

import java.io.FileInputStream;

import static org.junit.Assert.assertEquals;

public class LocationTest extends UStackTestCaseBase {
	
	protected static Logger logger = Logger.getLogger(LocationTest.class);

	public LocationTest()
	{
		super();
	}
	
	@Test public void testCountryLoad() throws Exception
	{
		Country.loadCountriesToDB(new FileInputStream("src/main/resources/com/textrecruit/ustack/resources/countries.csv"));
		assertEquals(272, Country.getCountryCount());
	}
	
}
