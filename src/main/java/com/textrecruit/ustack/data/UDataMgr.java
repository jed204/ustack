package com.textrecruit.ustack.data;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Date;

import org.apache.log4j.Logger;
import org.bson.BSONDecoder;
import org.bson.BSONEncoder;
import org.bson.BSONObject;
import org.bson.BasicBSONDecoder;
import org.bson.BasicBSONEncoder;
import org.bson.types.BasicBSONList;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.simple.JSONValue;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBObject;
import com.mongodb.util.JSONCallback;
import com.textrecruit.ustack.main.UFile;

public class UDataMgr {

	private static Logger logger = Logger.getLogger(UntzDBObject.class);

	/** Uses Google's Map API to calculate lat/long of an address */
	public static DBObject calculateLatLong(DBObject addr)
	{
		addr.removeField("loc");
		addr.removeField("latLongUpdated");

		if (addr.get("address1") == null || addr.get("city") == null)
		{
			logger.warn("No 'address1' or 'city' - skipping geo lookup");
			return null;
		}

		StringBuffer addrParam = new StringBuffer();
		addrParam.append((String)addr.get("address1"));
		if (addr.get("address2") != null)
			addrParam.append(" - ").append((String)addr.get("address2"));
		addrParam.append(",").append((String)addr.get("city"));
		addrParam.append(",").append((String)addr.get("state"));
		addrParam.append(",").append((String)addr.get("postalCode"));
		addrParam.append(",").append((String)addr.get("country"));

		DBObject loc = calculateLatLong(addrParam.toString());
		if (loc != null)
		{
			addr.put("loc", loc);
			addr.put("latLongUpdated", new Date());
		}
		return loc;
	}

	/** Uses Google's Map API to calculate lat/long of an address */
	public static DBObject calculateLatLong(String addr)
	{
		DBObject ret = GeoLookupCache.getLookup(addr);
		if (ret != null)
		{
			logger.info("Located 'loc' via geoCache : " + ret);
			return ret;
		}
		
		InputStream in = null;
		try {
			StringBuffer url = new StringBuffer();
			url.append("http://maps.googleapis.com/maps/api/geocode/json?address=");

			logger.info("Lookup Address: " + addr);
			url.append( URLEncoder.encode(addr, "UTF-8") );
			url.append("&sensor=false");
			
			logger.debug("URL Request [" + url.toString() + "]");
			
			URL google = new URL(url.toString());
			in = google.openStream();
			
			Object obj= JSONValue.parse(new InputStreamReader(in));
			JSONObject tgt = (JSONObject)obj;
			logger.debug("Response: " + tgt);
			
			if ("OK".equalsIgnoreCase((String)tgt.get("status")))
			{
				JSONArray results = (JSONArray)tgt.get("results");
				if (results != null && results.length() > 0)
				{
					JSONObject result = (JSONObject)results.get(0);
					JSONObject geom = (JSONObject)result.get("geometry");
					if (geom != null)
					{
						JSONObject loca = (JSONObject)geom.get("location");
						
						if (loca != null)
						{
							DBObject loc = new BasicDBObject();
							loc.put("lat", (Double)loca.get("lat"));
							loc.put("lng", (Double)loca.get("lng"));
							logger.debug("Returning [" + loc + "]");
							
							GeoLookupCache.saveLookup(addr, loc);
							
							return loc;
						}
					}
					else
						logger.warn("No geometry");
				}
				else
					logger.warn("No array results");
					
			}
			else
				logger.warn("Lookup status : " + tgt.get("status"));
			
		} catch (Exception err) {
			logger.error("Failed to execute lookup", err);
		} finally {
			if (in != null)
				try { in.close(); } catch (Exception er) {}
		}
		return null;
	}
	
	/** Returns DBObject for resource + role specific search */
	public static DBObject getResourceSearchAid(String resDefId, String roleName)
	{
		DBObject elem = new BasicDBObject();
		elem.put("resDefId", resDefId);
		elem.put("role", roleName);
		return BasicDBObjectBuilder.start("resourceLinkList", BasicDBObjectBuilder.start("$elemMatch", elem).get()).get();
	}

	/** Returns DBObject for resource specific search */
	public static DBObject getResourceSearchAid(String resDefId)
	{
		return BasicDBObjectBuilder.start("resourceLinkList.resDefId", resDefId).get();
	}
	
	/** Returns a DBOBject to aid in a 'near' search */
	public static DBObject getNearSearch(String nearString)
	{
		DBObject extras = new BasicDBObject();
		DBObject nearResult = UDataMgr.calculateLatLong(nearString);
		if (nearResult != null)
			extras.put("loc", BasicDBObjectBuilder.start("$near", new double[] { (Double)nearResult.get("lat"), (Double)nearResult.get("lng") }).get());

		return extras;
	}
	
	/** Returns a DBOBject to aid in a 'near' search including within miles */
	public static DBObject getNearSearch(String nearString, int withinMiles)
	{
		DBObject extras = new BasicDBObject();
		DBObject nearResult = UDataMgr.calculateLatLong(nearString);
		if (nearResult != null)
			extras.put("loc", BasicDBObjectBuilder.start("$near", new double[] { (Double)nearResult.get("lat"), (Double)nearResult.get("lng"), (double)((double)withinMiles / (double)69) }).get());

		return extras;
	}

	/**
	 * Imports a DBObject from a String for export and import operations
	 * @param inputFile
	 * @return
	 */
	public static DBObject readDBObjectFromString(String input)
	{
		BSONDecoder decoder = new BasicBSONDecoder();
		BSONObject bObj = decoder.readObject(input.getBytes());

		BasicDBObject ret = new BasicDBObject();
		ret.putAll(bObj);
		return ret;
	}

	/**
	 * Exports a DBObject to a String for export and import operations
	 * @param obj
	 * @return
	 */
	public static String writeDBObjectToString(DBObject obj)
	{
		BSONEncoder encoder = new BasicBSONEncoder();
		byte[] objectBytes = encoder.encode(obj);
		return new String(objectBytes);
	}

	/**
	 * Imports a DBObject from a String for export and import operations
	 * @param inputFile
	 * @return
	 */
	public static BasicDBList readDBListFromString(String input)
	{
		BSONDecoder decoder = new BasicBSONDecoder();
		BSONObject bObj = decoder.readObject(input.getBytes());
		BasicBSONList list = new BasicBSONList();
		list.putAll(bObj);

		BasicDBList ret = new BasicDBList();
		for (int i = 0; i < list.size(); i++)
		{
			DBObject add = new BasicDBObject();
			add.putAll((BSONObject)list.get(i));
			ret.add(add);
		}
		return ret;
	}

	/**
	 * Exports a BasicDBList to a String for export and import operations
	 * @param obj
	 * @return
	 */
	public static String writeDBListToString(BasicDBList obj)
	{
		BSONEncoder encoder = new BasicBSONEncoder();
		byte[] objectBytes = encoder.encode(obj);
		return new String(objectBytes);
	}

	/**
	 * Imports a DBObject from a file for export and import operations
	 * @param inputFile
	 * @return
	 */
	public static DBObject readDBObjectFromFile(UFile inputFile)
	{
		InputStream in = null;
		try {
			in = inputFile.getInputStream();
			return readDBObjectFromInputStream(in);
			
		} catch (Exception err) {
			logger.error("Failed to read/import to file", err);
			return null;
		} finally {
			if (in != null)
				try { in.close(); } catch (Exception er) {}
		}
	}
	
	public static DBObject readDBObjectFromInputStream(InputStream in) throws Exception
	{
		BSONDecoder decoder = new BasicBSONDecoder();

		JSONCallback callback = new JSONCallback();
		decoder.decode(in, callback);
		DBObject obj = (DBObject)callback.get();		
		return obj;
	}

	/**
	 * Exports a DBObject to a file for export and import operations
	 * @param obj
	 * @return
	 */
	public static UFile writeDBObjectToFile(DBObject obj)
	{
		UFile tempFile = UFile.getTempFile();
		BSONEncoder encoder = new BasicBSONEncoder();
		byte[] objectBytes = encoder.encode(obj);
		OutputStream out = null;
		try {
			
			out = tempFile.getOutputStream();
			out.write(objectBytes);
			return tempFile;
			
		} catch (Exception err) {
			logger.error("Failed to write/export to file", err);
			return null;
		} finally {
			if (out != null)
				try { out.close(); } catch (Exception er) {}
		}
	}
	

}
