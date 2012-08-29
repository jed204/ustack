package com.untzuntz.ustack.data;

import net.spy.memcached.AddrUtil;
import net.spy.memcached.BinaryConnectionFactory;
import net.spy.memcached.MemcachedClient;

import org.apache.log4j.Logger;

import com.untzuntz.ustack.main.UAppCfg;
import com.untzuntz.ustack.main.UOpts;

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
	private static MemcachedClient[] m = null;
	 
	private UDataCache() {
		
		try {
			m = new MemcachedClient[21];
			for (int i = 0; i <= 20; i ++) {
				MemcachedClient c =  new MemcachedClient(new BinaryConnectionFactory(), AddrUtil.getAddresses(UOpts.getString(UAppCfg.CACHE_HOST_STRING)));
				m[i] = c;
			}
		} catch (Exception e) {}
		
	}
	 
	public static synchronized UDataCache getInstance() {
		
		if(instance == null) {
			logger.info("Creating a new UDataCache instance...");
			instance = new UDataCache();
		}
		return instance;

	}
	
	public void set(String key, int ttl, final Object o) {
		
		MemcachedClient client = getCache();
		if (client == null)
			return;

		try {
			client.set(NAMESPACE + key, ttl, o);
		} catch (Exception e) {}
		
	}
	
	public Object get(String key) {
		
		MemcachedClient client = getCache();
		if (client == null)
			return null;
		
		try {
			Object o = client.get(NAMESPACE + key);
			if(o == null) {
				logger.debug("Cache MISS for KEY: " + key);
			} else {
				logger.debug("Cache HIT for KEY: " + key);
			}
			return o;	
		} catch (Exception e) {}
		
		return null;
		
	}
	 
	public Object delete(String key) {
		
		MemcachedClient client = getCache();
		if (client == null)
			return null;
		
		try {
			return client.delete(NAMESPACE + key);
		} catch (Exception e) {}
	
		return null;
	}
	
	public MemcachedClient getCache() {
		
		MemcachedClient c = null;
		
		try {
			int i = (int)(Math.random()* 20);
			c = m[i];
		} catch(Exception e) {}
		
		return c;
		
	}
	
}
