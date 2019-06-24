package com.textrecruit.ustack.data;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

/**
 * Basic Country Object
 * 
 * @author jdanner
 *
 */
public class Country extends UntzDBObject {

	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(Country.class);

	public String getCollectionName() { return "countries"; }
	
	private Country() {
		put("created", new Date());
	}
	
	public Country(DBObject obj)
	{
		putAll(obj);
	}
	
	public String toString()
	{
		return getString("commonName");
	}
	
	public BasicDBList getStateList()
	{
		BasicDBList ret = (BasicDBList)get("stateList");
		if (ret == null)
			ret = new BasicDBList();
		
		return ret;
	}
	
	public DBObject getStateByAbbrev(String abbrev)
	{
		if (abbrev == null)
			return null;
		
		BasicDBList states = getStateList();
		for (int i = 0; i < states.size(); i++)
		{
			DBObject state = (DBObject)states.get(i);
			if (abbrev.equalsIgnoreCase( (String)state.get("stateAbbrev") ))
				return state;
		}
		return null;
	}
	
	public DBObject resolveState(String data)
	{
		if (data == null)
			return null;
		
		BasicDBList states = getStateList();
		for (int i = 0; i < states.size(); i++)
		{
			DBObject state = (DBObject)states.get(i);
			if (data.equalsIgnoreCase( (String)state.get("state") ))
				return state;
			if (data.equalsIgnoreCase( (String)state.get("stateAbbrev") ))
				return state;
		}
		return null;
	}
	
	public static Country resolveCountry(String data)
	{
   		// lookup country to make sure it matches up, check for iso3LetterCode
		Country country = Country.getCountryByName(data);
		if (country == null)
			country = Country.getCountryByISO3(data);

		return country;
	}

	public static Country getCountryByISO3(String iso3LetterCode)
	{
		DBCollection col = new Country().getCollection();
		DBObject obj = col.findOne( BasicDBObjectBuilder.start("iso3LetterCode", iso3LetterCode).get() );
		if (obj != null)
			return new Country(obj);
		
		return null;
	}

	public static Country getCountryByName(String name)
	{
		DBCollection col = new Country().getCollection();
		DBObject obj = col.findOne( BasicDBObjectBuilder.start("commonName", name).get() );
		if (obj != null)
			return new Country(obj);
		
		return null;
	}

	public static List<Country> getCountries() 
	{
		List<Country> ret = new Vector<Country>();
		
		DBCollection col = new Country().getCollection();
		DBCursor cur = col.find().sort(BasicDBObjectBuilder.start("commonName", 1).get());
		while (cur.hasNext())
			ret.add(new Country(cur.next()));
		
		return ret;
	}
	
	public static long getCountryCount()
	{
		return new Country().getCollection().count();
	}
	
	public static void loadCountriesToDB()
	{
		loadCountriesToDB(Country.class.getClassLoader().getResourceAsStream("com/textrecruit/ustack/resources/countries.csv"));
	}

	public static void loadCountriesToDB(InputStream is)
	{
		if (is == null)
		{
			logger.error("Failed to load countries into database -> No input stream provided (null)");
			return;
		}
		
		try {
			BufferedReader br = new BufferedReader( new InputStreamReader(is) );
			String strLine = "";
			StringTokenizer st = null;
			int lineNumber = 0;
			
			Hashtable<String,DBObject> map = new Hashtable<String,DBObject>();
			List<DBObject> list = new Vector<DBObject>();
			
			String[] headers = null;
			while( (strLine = br.readLine()) != null)
			{
				// split data
				st = new StringTokenizer(strLine, "\"");
				int tokenNumber = 0;

				String[] data = new String[19];
				 
				while(st.hasMoreTokens())
				{
					String tok = st.nextToken();
					if (",".equalsIgnoreCase(tok))
						tokenNumber += 0;
					else if (",,".equalsIgnoreCase(tok))
						tokenNumber += 1;
					else if (",,,".equalsIgnoreCase(tok))
						tokenNumber += 2;
					else if (",,,,".equalsIgnoreCase(tok))
						tokenNumber += 3;
					else if (",,,,,".equalsIgnoreCase(tok))
						tokenNumber += 4;
					else if (",,,,,,".equalsIgnoreCase(tok))
						tokenNumber += 5;
					else if (",,,,,,,".equalsIgnoreCase(tok))
						tokenNumber += 6;
					else
					{
						data[tokenNumber] = tok;
						tokenNumber++;
					}
				}

				if (lineNumber == 0) // set as field headers
				{
					headers = data;
				}
				else
				{
					// save to db
					DBObject obj = new BasicDBObject();
					for (int i = 0; i < 16; i++)
					{
						if (data[i] != null)
							obj.put( headers[i], data[i] );
					}
					
					if (map.get(data[0]) == null)
					{
						map.put(data[0], obj); // store country
						list.add(obj);
					}
					
					if (data[16] != null)
					{
						DBObject country = map.get(data[0]);
						DBObject stateObj = new BasicDBObject();

						for (int i = 16; i < 19; i++)
						{
							if (data[i] != null)
								stateObj.put( headers[i], data[i] );
						}
						
						if (stateObj.toMap().size() > 0)
						{
							BasicDBList stateList = (BasicDBList)country.get("stateList");
							if (stateList == null)
								stateList = new BasicDBList();
							
							stateList.add(stateObj);
							country.put("stateList", stateList);
						}
					}
				}
				lineNumber++;
			}

			// reset the collection and save our objects
			DBCollection col = new Country().getCollection();
			col.drop();
			for (DBObject obj : list)
				col.save(obj);
			
			logger.info("Loaded " + col.count() + " countries into the database");
		 
		} catch (Exception err) {
			logger.error("Failed to load in country", err);
		} finally {
			try { 
				if (is != null)
					is.close();
			} catch (Exception e) {}
		}
	}
}
