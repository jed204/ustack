package com.untzuntz.ustack.data;

import java.net.InetSocketAddress;
import java.util.List;

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
	private static final int CLIENT_COUNT = 20;
	 
	private UDataCache() {
		
		try {
			List<InetSocketAddress> addrList = AddrUtil.getAddresses(UOpts.getString(UAppCfg.CACHE_HOST_STRING));
			m = new MemcachedClient[CLIENT_COUNT + 1];
			for (int i = 0; i <= CLIENT_COUNT; i ++) {
				MemcachedClient c =  new MemcachedClient(new BinaryConnectionFactory(), addrList);
				m[i] = c;
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
		
		MemcachedClient client = getCache();
		if (client != null)
		{
			try {
				return client.incr(key.replace((char)' ', (char)'_'), 1, 0, ttl);
			} catch (Exception er) {}
		}
		return 0L;
	}
	
	public void set(String key, int ttl, final Object o) {
		
		MemcachedClient client = getCache();
		if (client == null)
			return;

		try {
			client.set(NAMESPACE + key.replace((char)' ', (char)'_'), ttl, o);
		} catch (Exception e) {}
		
	}
	
	public Object get(String key) {
		
		MemcachedClient client = getCache();
		if (client == null)
			return null;
		
		try {
			Object o = client.get(NAMESPACE + key.replace((char)' ', (char)'_'));
			if(o == null) {
				logger.debug("Cache MISS for KEY: " + key);
			} else {
				logger.debug("Cache HIT for KEY: " + key);
			}
			return o;	
		} catch (Exception e) {
			logger.warn("unable to get key from memcache server", e);
		}
		
		return null;
		
	}
	 
	public Object delete(String key) {
		
		MemcachedClient client = getCache();
		if (client == null)
			return null;
		
		try {
			return client.delete(NAMESPACE + key.replace((char)' ', (char)'_'));
		} catch (Exception e) {}
	
		return null;
	}
	
	public MemcachedClient getCache() {
		
		if (!UOpts.getCacheEnabled())
			return null;

		MemcachedClient c = null;
		
		try {

			int i = (int)(Math.random() * CLIENT_COUNT);
			c = m[i];
			
		} catch(Exception e) {
			logger.warn("Failed to get memcache client", e);
		}
		
		return c;
		
	}
	
}
