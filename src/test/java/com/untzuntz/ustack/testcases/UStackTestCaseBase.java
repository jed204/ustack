package com.untzuntz.ustack.testcases;

import com.github.fakemongo.Fongo;
import com.untzuntz.ustack.data.MongoDB;
import com.untzuntz.ustack.data.UDataCache;
import com.untzuntz.ustack.data.UMemcachedClient;
import com.untzuntz.ustack.main.UAppCfg;
import com.untzuntz.ustack.main.UOpts;
import net.rubyeye.xmemcached.MemcachedClient;
import org.junit.Ignore;
import org.junit.Test;

import java.util.UUID;

import static org.mockito.Mockito.mock;

public class UStackTestCaseBase {

	protected static final long runId = System.currentTimeMillis();
	
	public UStackTestCaseBase()
	{
		System.setProperty("TestCase", "true");
		System.setProperty(UAppCfg.CACHE_HOST_STRING, "localhost:11211");
		System.setProperty(UAppCfg.MONGO_DB_HOST, "localhost:27017");
		System.setProperty(UAppCfg.DIRECTORY_SCRATCH, "/tmp/");

		MongoDB.setMongo(new Fongo("TestCase").getMongo());

		MemcachedClient mc = mock(MemcachedClient.class);
		UMemcachedClient client = new UMemcachedClient(mc);

		UDataCache.setCacheClient(client);
	}

	@Ignore
	@Test public void testConnect() throws Exception
	{
		int i = 0;
		UOpts.setCacheFlag(true);
		do {
			String uid = "hello-" + UUID.randomUUID().toString();
			UDataCache.getInstance().set("hi", 100, uid);

			Thread.sleep(1000);

			String pUid = (String) UDataCache.getInstance().get("hi");
			//assertEquals(uid, pUid);

			i++;
			if (uid.equals(pUid)) {
				System.out.println("Excellent: " + i);
			}
			else {
				System.out.println("Awww, man: " + i);
			}
		} while (true);
	}
}
