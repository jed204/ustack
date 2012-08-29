package com.untzuntz.ustack.testcases;

import static org.junit.Assert.assertEquals;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.mongodb.Mongo;
import com.untzuntz.ustack.data.AddressBook;
import com.untzuntz.ustack.data.AddressBookEntry;
import com.untzuntz.ustack.data.MongoDB;

public class AddressBookTest extends UStackTestCaseBase {

	protected static Logger logger = Logger.getLogger(AccountingTest.class);

	public AddressBookTest()
	{
		super();
	}
	
	@Test public void testAddLogic() throws Exception
	{
		AddressBook myBook1 = AddressBook.getByName("test@testy.com" + runId);
		assertEquals(0, myBook1.getEntryList().size());

		AddressBookEntry entry1 = new AddressBookEntry("john@untzuntz.com" + runId);
		AddressBookEntry entry2 = new AddressBookEntry("dave@untzuntz.com" + runId);
		AddressBookEntry entry3 = new AddressBookEntry("john@untzuntz.com" + runId); // should be the same as entry1
		entry1.setDisplayValue("Johnny");
		entry2.setDisplayValue("David Davies");
		entry3.setDisplayValue("Johnny Same");
		
		myBook1.addEntry(entry1);
		myBook1.addEntry(entry2);
		myBook1.addEntry(entry3);
		myBook1.save("Test Case");
		
		assertEquals(2, myBook1.getEntryList().size()); // only 2 are expected because 'entry3' should no add due to matching entry1
	}
	
	@Test public void testRemoveLogic() throws Exception
	{
		AddressBook myBook1 = AddressBook.getByName("test@testy.com" + runId);
		assertEquals(2, myBook1.getEntryList().size()); // we should have values from the last test

		AddressBookEntry removeEntry1 = new AddressBookEntry("dave@untzuntz.com" + runId);
		myBook1.removeEntry(removeEntry1);
		myBook1.save("Test Case");
	}
	
	@Test public void testSubscriptions() throws Exception
	{
		AddressBook myBook1 = AddressBook.getByName("test@testy.com" + runId);
		assertEquals(1, myBook1.getEntryList().size()); // we should have values from the last test

		AddressBookEntry entry1 = new AddressBookEntry("john@untzuntz.com" + runId);
		AddressBookEntry entry2 = new AddressBookEntry("dave@untzuntz.com" + runId);
		AddressBookEntry entry3 = new AddressBookEntry("mike@untzuntz.com" + runId); 
		AddressBookEntry entry4 = new AddressBookEntry("guy@untzuntz.com" + runId); 
		entry1.setDisplayValue("Johnny");
		entry2.setDisplayValue("David Davies");
		entry3.setDisplayValue("Mike Gordon");
		entry4.setDisplayValue("GUY!");

		AddressBook mySiteBook1 = AddressBook.getByName("Some Site" + runId);
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
		AddressBook myBookReload1 = AddressBook.getByName("test@testy.com" + runId);
		assertEquals(4, myBookReload1.getEntries().size()); // we should have values from the last test
		
		// Unsubscribe
		myBookReload1.unsubscribeFrom(mySiteBook1);
		assertEquals(1, myBookReload1.getEntries().size()); // we should have values from the last test
		myBookReload1.save("Test Case");
	}

}
