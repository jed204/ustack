package com.untzuntz.ustack.data;

import com.untzuntz.ustack.main.UAppCfg;
import com.untzuntz.ustack.main.UOpts;
import org.apache.log4j.Logger;

/**
 * Memcache data cache interface 
 * 
 * @author jdanner
 *
 */
public class UDataCache {

	static Logger logger = Logger.getLogger(UDataCache.class);

	private static final String NAMESPACE = "UST_n7zv1";
	private static UDataCache instance = null;
	private static UDataCacheClientInt client = null;

	private UDataCache() {
		
		try {
			if (client == null) {

				String connString = UOpts.getString(UAppCfg.CACHE_HOST_STRING);

				if (connString.startsWith("memcached:")) {

					connString = connString.replaceFirst("^memcached:", "");
					client = new UMemcachedClient(connString);

				} else if (connString.startsWith("redis:")) {

					client = new URedisClient(connString);

				} else {

					client = new UMemcachedClient(connString);

				}

			}
		} catch (Exception e) {
			logger.warn("Failed to setup clients", e);
		}
		
	}
	 
	public static synchronized UDataCache getInstance() {
		
		if(instance == null) {
			String hosts = UOpts.getString(UAppCfg.CACHE_HOST_STRING);
			if (hosts != null && hosts.length() > 0)
			{
				logger.info("Creating a new UDataCache instance...[" + hosts + "]");
				instance = new UDataCache();
			}
		}
		return instance;

	}
	
	public long incr(String key, int ttl, final long o) {

		UDataCacheClientInt client = getCacheClient();
		if (client != null) {
			return client.incr(key.replace((char)' ', (char)'_'), ttl);
		}
		return 0L;

	}
	
	public void set(String key, int ttl, final Object o) {

		UDataCacheClientInt client = getCacheClient();
		if (client == null)
			return;

		client.set(NAMESPACE + key.replace((char)' ', (char)'_'), ttl, o);

	}
	
	public Object get(String key) {

		UDataCacheClientInt client = getCacheClient();
		if (client == null)
			return null;

		Object o = client.get(NAMESPACE + key.replace((char)' ', (char)'_'));
		if (o == null) {
			logger.debug("Cache MISS for KEY: " + key);
		} else {
			logger.debug("Cache HIT for KEY: " + key);
		}
		return o;

	}
	 
	public Object delete(String key) {

		UDataCacheClientInt client = getCacheClient();
		if (client == null)
			return null;

		return client.delete(NAMESPACE + key.replace((char)' ', (char)'_'));

	}
	
	public UDataCacheClientInt getCacheClient() {
		
		if (!UOpts.getCacheEnabled() || client == null)
			return null;

		return client;
		
	}

	public static void setCacheClient(UDataCacheClientInt client) {

		UDataCache.client = client;

	}
}
