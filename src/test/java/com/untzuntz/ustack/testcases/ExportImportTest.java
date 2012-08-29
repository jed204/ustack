package com.untzuntz.ustack.testcases;

import static org.junit.Assert.assertEquals;

import org.apache.commons.codec.binary.Base64;
import org.junit.Test;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.untzuntz.ustack.data.UDataCache;
import com.untzuntz.ustack.data.UDataMgr;
import com.untzuntz.ustack.main.UFile;

public class ExportImportTest extends UStackTestCaseBase {

	@Test public void testBSONExportImport()
	{
		System.clearProperty("UAppCfg.CacheHost");
		
		BasicDBObject myObject = new BasicDBObject();
		myObject.put("test", "yes");
		
		String bdoStr = UDataMgr.writeDBObjectToString(myObject);
		
//		BSONEncoder encoder = new BSONEncoder();
//		byte[] objectBytes = encoder.encode(myObject);
//		
//		BSONDecoder decoder = new BSONDecoder();
//		BSONObject bObj = decoder.readObject(objectBytes);
//		
//		BasicDBObject myNewObject = new BasicDBObject();
//		myNewObject.putAll(bObj);
		
		BasicDBObject myNewObject = (BasicDBObject)UDataMgr.readDBObjectFromString(bdoStr);
		
		assertEquals("yes", myNewObject.getString("test"));
		
		BasicDBList list = new BasicDBList();
		list.add(new BasicDBObject("One", "True"));
		list.add(new BasicDBObject("Two", "True"));
		
		String origList = UDataMgr.writeDBListToString(list);
		
		BasicDBList newList = (BasicDBList)UDataMgr.readDBListFromString(origList);
		assertEquals(2, newList.size());
		
		DBObject obj1 = (DBObject)newList.get(0);
		DBObject obj2 = (DBObject)newList.get(1);
		
		assertEquals("True", (String)obj1.get("One"));
		assertEquals("True", (String)obj2.get("Two"));
		
		/*
		 * Through Memcached
		 */
		System.setProperty("UAppCfg.CacheHost", "localhost:11211");

		String resListStr = UDataMgr.writeDBListToString(list);
		String cacheVal = new String(Base64.encodeBase64(resListStr.getBytes()));
		
		UDataCache.getInstance().set("test", 100, cacheVal);
		
		String recvVal = (String)UDataCache.getInstance().get("test");

//		assertNotNull(recvVal);
//		BasicDBList newList2 = UDataMgr.readDBListFromString(new String(Base64.decodeBase64(recvVal.getBytes())));
//		assertEquals(2, newList2.size());
//		
//		DBObject obj1b = (DBObject)newList2.get(0);
//		DBObject obj2b = (DBObject)newList2.get(1);
//		
//		assertEquals("True", (String)obj1b.get("One"));
//		assertEquals("True", (String)obj2b.get("Two"));
		
	}
	
	
	@Test public void testBSONFileExportImport()
	{
		BasicDBObject myObject = new BasicDBObject();
		myObject.put("test", "yes");
		
		UFile tempFile = UDataMgr.writeDBObjectToFile(myObject);
		
		DBObject myNewObject = UDataMgr.readDBObjectFromFile(tempFile);
		assertEquals("yes", (String)myNewObject.get("test"));
	}
	
}
