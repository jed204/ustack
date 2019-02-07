package com.untzuntz.ustack.data;

import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.MemcachedClientBuilder;
import net.rubyeye.xmemcached.XMemcachedClientBuilder;
import net.rubyeye.xmemcached.utils.AddrUtil;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.List;

public class UMemcachedClient implements UDataCacheClientInt {

    static Logger logger = Logger.getLogger(UMemcachedClient.class);


    private static MemcachedClient mc = null;
    private static int CLIENT_COUNT = 20;


    public UMemcachedClient(String connectionString) {

        connect(connectionString);

    }

    public UMemcachedClient(MemcachedClient client) {
        mc = client;
    }

    public void connect(String connString) {

        try {
            List<InetSocketAddress> servers = AddrUtil.getAddresses(connString);

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
        } catch (IOException e) {
            logger.warn("Failed to setup UMemcachedClient client", e);
        }


    }

    @Override
    public long incr(String key, int timeout) {

        try {
            return mc.incr(key, 1, 0, timeout);
        } catch (Exception e) {
            logger.error("Unable to incr key from memcache server", e);
        }
        return 0L;

    }

    @Override
    public void set(String key, int exp, Object value) {

        try {
            mc.set(key, exp, value);
        } catch (Exception e) {
            logger.warn("Unable to set key from memcache server", e);
        }

    }

    @Override
    public Object get(String key) {

        try {
            Object o = mc.get(key);
            if(o == null) {
                logger.debug("Memcached Cache MISS for KEY: " + key);
            } else {
                logger.debug("Memcached Cache HIT for KEY: " + key);
            }
            return o;
        } catch (Exception e) {
            logger.error("Unable to get key from memcache server", e);
        }

        return null;

    }

    @Override
    public boolean delete(String key) {
        return false;
    }

}
