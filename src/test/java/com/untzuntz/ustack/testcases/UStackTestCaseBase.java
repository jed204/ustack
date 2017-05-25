package com.untzuntz.ustack.testcases;

import com.github.fakemongo.Fongo;
import com.untzuntz.ustack.data.MongoDB;
import com.untzuntz.ustack.main.UAppCfg;

public class UStackTestCaseBase {

	protected static final long runId = System.currentTimeMillis();
	
	public UStackTestCaseBase()
	{
		System.setProperty("TestCase", "true");
		System.setProperty(UAppCfg.CACHE_HOST_STRING, "localhost:11211");
		System.setProperty(UAppCfg.MONGO_DB_HOST, "localhost:27017");
		System.setProperty(UAppCfg.DIRECTORY_SCRATCH, "/tmp/");

		MongoDB.setMongo(new Fongo("TestCase").getMongo());
	}
	
}
