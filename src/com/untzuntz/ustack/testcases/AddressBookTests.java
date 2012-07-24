package com.untzuntz.ustack.testcases;

import java.util.Iterator;
import java.util.Set;

import junit.framework.TestCase;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.Mongo;
import com.untzuntz.ustack.data.AddressBook;
import com.untzuntz.ustack.data.AddressBookEntry;
import com.untzuntz.ustack.data.MongoDB;
import com.untzuntz.ustack.main.ApplicationInstance;

public class AddressBookTests extends TestCase {

	protected static Logger logger = Logger.getLogger(AccountingTests.class);

	public AddressBookTests()
	{
		System.setProperty("TestCase", "true");
	}
	
	public void testInit()
	{
		BasicConfigurator.configure();

		Mongo m = MongoDB.getMongo();
		if (!m.getAddress().sameHost("localhost")) // verify we are connected locally
		{
			logger.error("Database not hosted locally - stopping test");
			System.exit(0);
		}
		
		// output some info about the database
		DBCollection col = MongoDB.getCollection(ApplicationInstance.getAppName(), "addressBooks");
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
	
	public void testAddLogic() throws Exception
	{
		AddressBook myBook1 = AddressBook.getByName("test@testy.com");
		assertEquals(0, myBook1.getEntryList().size());

		AddressBookEntry entry1 = new AddressBookEntry("john@untzuntz.com");
		AddressBookEntry entry2 = new AddressBookEntry("dave@untzuntz.com");
		AddressBookEntry entry3 = new AddressBookEntry("john@untzuntz.com"); // should be the same as entry1
		entry1.setDisplayValue("Johnny");
		entry2.setDisplayValue("David Davies");
		entry3.setDisplayValue("Johnny Same");
		
		myBook1.addEntry(entry1);
		myBook1.addEntry(entry2);
		myBook1.addEntry(entry3);
		myBook1.save("Test Case");
		
		assertEquals(2, myBook1.getEntryList().size()); // only 2 are expected because 'entry3' should no add due to matching entry1
	}
	
	public void testRemoveLogic() throws Exception
	{
		AddressBook myBook1 = AddressBook.getByName("test@testy.com");
		assertEquals(2, myBook1.getEntryList().size()); // we should have values from the last test

		AddressBookEntry removeEntry1 = new AddressBookEntry("dave@untzuntz.com");
		myBook1.removeEntry(removeEntry1);
		myBook1.save("Test Case");
	}
	
	public void testSubscriptions() throws Exception
	{
		AddressBook myBook1 = AddressBook.getByName("test@testy.com");
		assertEquals(1, myBook1.getEntryList().size()); // we should have values from the last test

		AddressBookEntry entry1 = new AddressBookEntry("john@untzuntz.com");
		AddressBookEntry entry2 = new AddressBookEntry("dave@untzuntz.com");
		AddressBookEntry entry3 = new AddressBookEntry("mike@untzuntz.com"); 
		AddressBookEntry entry4 = new AddressBookEntry("guy@untzuntz.com"); 
		entry1.setDisplayValue("Johnny");
		entry2.setDisplayValue("David Davies");
		entry3.setDisplayValue("Mike Gordon");
		entry4.setDisplayValue("GUY!");

		AddressBook mySiteBook1 = AddressBook.getByName("Some Site");
		mySiteBook1.addEntry(entry1);
		mySiteBook1.addEntry(entry2);
		mySiteBook1.addEntry(entry3);
		mySiteBook1.addEntry(entry4);
		mySiteBook1.save("Test Case");
		assertEquals(4, mySiteBook1.getEntryList().size()); // we should have values from the last test
		myBook1.subscribeTo(mySiteBook1, "Test Case");
		myBook1.save("Test Case");

		assertEquals(4, myBook1.getEntries().size()); // expecting 4 because john@ will match in the site's book and should not be added twice
		
		// Verify Reload Counts
		AddressBook myBookReload1 = AddressBook.getByName("test@testy.com");
		assertEquals(4, myBookReload1.getEntries().size()); // we should have values from the last test
		
		// Unsubscribe
		myBookReload1.unsubscribeFrom(mySiteBook1);
		assertEquals(1, myBookReload1.getEntries().size()); // we should have values from the last test
		myBookReload1.save("Test Case");
	}

}
