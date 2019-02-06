package com.untzuntz.ustack.data;

import com.untzuntz.ustack.main.UAppCfg;
import com.untzuntz.ustack.main.UOpts;
import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.MemcachedClientBuilder;
import net.rubyeye.xmemcached.XMemcachedClientBuilder;
import net.rubyeye.xmemcached.command.BinaryCommandFactory;
import net.rubyeye.xmemcached.utils.AddrUtil;
import org.apache.log4j.Logger;

import java.net.InetSocketAddress;
import java.util.List;

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
	private static MemcachedClient mc = null;
	private static int CLIENT_COUNT = 1;
	 
	private UDataCache() {
		
		try {
			if (mc == null) {
				List<InetSocketAddress> servers = AddrUtil.getAddresses(UOpts.getString(UAppCfg.CACHE_HOST_STRING));
				MemcachedClientBuilder builder = new XMemcachedClientBuilder(servers);
				builder.setConnectionPoolSize(CLIENT_COUNT);

				// Use binary protocol
				//builder.setCommandFactory(new BinaryCommandFactory());
				// Connection timeout in milliseconds (default: )
				builder.setConnectTimeout(1000);
				// Reconnect to servers (default: true)
				builder.setEnableHealSession(true);
				// Delay until reconnect attempt in milliseconds (default: 2000)
				builder.setHealSessionInterval(2000);

				mc = builder.build();
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
			} catch (Exception e) {
				logger.error("unable to incr key from memcache server", e);
			}
		}
		return 0L;
	}
	
	public void set(String key, int ttl, final Object o) {

		MemcachedClient client = getCache();
		if (client == null)
			return;

		try {
			client.set(NAMESPACE + key.replace((char)' ', (char)'_'), ttl, o);
		} catch (Exception e) {
			logger.warn("unable to set key from memcache server", e);
		}
		
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
			logger.error("unable to get key from memcache server", e);
		}
		
		return null;
		
	}
	 
	public Object delete(String key) {

		MemcachedClient client = getCache();
		if (client == null)
			return null;
		
		try {
			return client.delete(NAMESPACE + key.replace((char)' ', (char)'_'));
		} catch (Exception e) {
			logger.error("unable to delete key from memcache server", e);
		}
	
		return null;
	}
	
	public MemcachedClient getCache() {
		
		if (!UOpts.getCacheEnabled() || mc == null)
			return null;

		return mc;
		
	}

	public static void setMemcacheClient(MemcachedClient client) {
		UDataCache.mc = client;
		UDataCache.CLIENT_COUNT = 1;
	}
}
