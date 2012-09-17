package com.untzuntz.ustack.util;

import java.util.ArrayList;
import java.util.List;

import com.mongodb.BasicDBList;
import com.mongodb.DBObject;
import com.untzuntz.ustack.data.AddressBook;
import com.untzuntz.ustack.data.AddressBookEntry;

public class AddressBookSearch {

	public static List<AddressBookEntry> search(AddressBook book, String displayVal, String match, String type)
	{
		if (displayVal != null)
			displayVal = displayVal.toLowerCase();
		if (match != null)
			match = match.toLowerCase();
		if (type != null)
			type = type.toLowerCase();
		
		List<AddressBookEntry> ret = new ArrayList<AddressBookEntry>();
		if (book == null)
			return ret;
		
		BasicDBList entries = book.getEntryList();
		for (int i = 0; i < entries.size(); i++)
		{
			DBObject entry = (DBObject)entries.get(i);
			boolean entryMatch = true;
			
			if (displayVal != null)
			{
				// wildcard match
				String entryDisplayVal = (String)entry.get("displayVal");
				if (entryDisplayVal != null && entryDisplayVal.toLowerCase().indexOf(displayVal) == -1)
					entryMatch = false;
			}
			
			if (match != null && entryMatch)
			{
				// wildcard match
				String matchVal = (String)entry.get("match");
				if (matchVal != null && matchVal.toLowerCase().indexOf(match) == -1)
					entryMatch = false;
			}
			
			if (type != null && entryMatch)
			{
				// wildcard match
				String typeVal = (String)entry.get("type");
				if (typeVal != null && typeVal.toLowerCase().indexOf(type) == -1)
					entryMatch = false;
			}
			
			if (entryMatch)
				ret.add(new AddressBookEntry(entry));
		}
		
		return ret;
	}

}
