package com.textrecruit.ustack.testcases;

import com.textrecruit.ustack.data.AddressBook;
import com.textrecruit.ustack.data.AddressBookEntry;
import com.textrecruit.ustack.util.AddressBookSearch;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class AddressBookTest extends UStackTestCaseBase {

	protected static Logger logger = Logger.getLogger(AddressBookTest.class);

	public AddressBookTest()
	{
		super();
	}

	@Before
	public void setUp() throws Exception {
		AddressBook myBook1 = AddressBook.getByName("test@testy.com" + runId);
		assertEquals(0, myBook1.getEntryList().size());

		AddressBookEntry entry1 = new AddressBookEntry("john@textrecruit.com" + runId);
		AddressBookEntry entry2 = new AddressBookEntry("dave@textrecruit.com" + runId);
		AddressBookEntry entry3 = new AddressBookEntry("john@textrecruit.com" + runId); // should be the same as entry1
		entry1.setDisplayValue("Johnny");
		entry1.setType("user");
		entry2.setDisplayValue("David Davies");
		entry2.setType("user");
		entry3.setDisplayValue("Johnny Same");

		myBook1.addEntry(entry1);
		myBook1.addEntry(entry2);
		myBook1.addEntry(entry3);
		myBook1.save("Test Case");

		assertEquals(2, myBook1.getEntryList().size()); // only 2 are expected because 'entry3' should no add due to matching entry1
	}

	@Test public void testAddLogic() throws Exception
	{
		
		AddressBook myBook2 = AddressBook.getByName("test@testy.com" + runId);
		assertEquals(2, myBook2.getEntryList().size()); // we should have values from the last test
		
		Assert.assertEquals(1, AddressBookSearch.search(myBook2, "Johnny", null, null).size());
		assertEquals(1, AddressBookSearch.search(myBook2, "David Davies", null, null).size());
		assertEquals(0, AddressBookSearch.search(myBook2, "INVALID", null, null).size());
		assertEquals(1, AddressBookSearch.search(myBook2, "Johnny", null, "user").size());
		assertEquals(1, AddressBookSearch.search(myBook2, "David Davies", null, "user").size());
		assertEquals(2, AddressBookSearch.search(myBook2, null, null, "user").size());
		assertEquals(0, AddressBookSearch.search(myBook2, null, null, "site").size());
		
		AddressBook myBook3 = AddressBook.getByName("test@testy.com" + runId);
		assertEquals(2, myBook3.getEntryList().size()); // we should have values from the last test

		AddressBookEntry removeEntry1 = new AddressBookEntry("dave@textrecruit.com" + runId);
		myBook3.removeEntry(removeEntry1);
		myBook3.save("Test Case");
	}
	
	@Test public void testSubscriptions() throws Exception
	{
		AddressBook myBook1 = AddressBook.getByName("test@testy.com" + runId);
		assertEquals(2, myBook1.getEntryList().size());

		AddressBookEntry entry1 = new AddressBookEntry("john@textrecruit.com" + runId);
		AddressBookEntry entry2 = new AddressBookEntry("dave@textrecruit.com" + runId);
		AddressBookEntry entry3 = new AddressBookEntry("mike@textrecruit.com" + runId);
		AddressBookEntry entry4 = new AddressBookEntry("guy@textrecruit.com" + runId);
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
		assertEquals(2, myBookReload1.getEntries().size());
		myBookReload1.save("Test Case");
	}

}
