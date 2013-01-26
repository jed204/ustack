package com.untzuntz.ustack.data;

import java.util.Date;
import java.util.List;
import java.util.Vector;

import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

/**
 * A state within a country
 * 
 * @author jdanner
 *
 */
public class State extends UntzDBObject {

	private static final long serialVersionUID = 1L;

	public String getCollectionName() { return "countries"; }
	
	private State() {
		put("created", new Date());
	}
	
	public State(DBObject obj)
	{
		putAll(obj);
	}
	
	public String toString()
	{
		return getString("state");
	}

	public static List<State> getCountries() 
	{
		List<State> ret = new Vector<State>();
		
		DBCollection col = new State().getCollection();
		DBCursor cur = col.find();
		while (cur.hasNext())
			ret.add(new State(cur.next()));
		
		return ret;
	}
	
	public String resolveStateAbbrev(String state)
	{
		return resolveStateAbbrev("United States", state);
	}
	
	public String resolveStateAbbrev(String country, String state)
	{
		Country cntry = Country.resolveCountry(country);
		if (cntry == null)
			return null;
		
		DBObject stateObj = cntry.resolveState(state);
		if (stateObj == null)
			return null;
		
		return (String)stateObj.get("stateAbbrev");
	}
	

	
}
