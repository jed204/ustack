package com.textrecruit.ustack.data;

import com.textrecruit.ustack.main.UAppCfg;
import org.apache.log4j.Logger;
import org.redisson.Redisson;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RBucket;
import org.redisson.api.RKeys;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

public class URedisClient implements UDataCacheClientInt {

    static Logger logger = Logger.getLogger(URedisClient.class);

    public enum ConnectionType {
        SENTINEL,
        SINGLE,
        REPLICATED
    }

    private RedissonClient redisson;

    public URedisClient(String connectionString, ConnectionType type) {

        String password = System.getProperty(UAppCfg.CACHE_HOST_PASSWORD);

        if (ConnectionType.REPLICATED.equals(type)) {
            connectReplicated(connectionString, password);
        } else if (ConnectionType.SENTINEL.equals(type)) {
            connectSentinel(connectionString, password);
        } else {
            connectSingle(connectionString, password);
        }

    }

    private void connectReplicated(String connectionString, String password) {

        try {

            Config config = new Config();
            config.useReplicatedServers()
                    .setPassword(password)
                    .addNodeAddress(connectionString)
                    .setTimeout(10000)
                    .setConnectTimeout(20000)
                    .setPingConnectionInterval(1000)
                    .setKeepAlive(true);

            redisson = Redisson.create(config);

        } catch (Exception e) {
            logger.error("Unable to connect to redis in replicated mode", e);
        }

    }

    private void connectSentinel(String connectionString, String password) {

        try {

            Config config = new Config();
            config.useSentinelServers()
                    .setPassword(password)
                    .setMasterName("mymaster")
                    .addSentinelAddress(connectionString);

            redisson = Redisson.create(config);

        } catch (Exception e) {
            logger.error("Unable to connect to redis in sentinel mode", e);
        }

    }

    private void connectSingle(String connectionString, String password) {

        try {

            Config config = new Config();
            config.useSingleServer()
                    .setPassword(password)
                    .setAddress(connectionString);

            redisson = Redisson.create(config);

        } catch (Exception e) {
            logger.error("Unable to connect to redis in single mode", e);
        }

    }

    @Override
    public boolean isConnected() {

        if (redisson == null) {
            return false;
        }

        try {
            if (redisson.getClusterNodesGroup().pingAll()) {
                return true;
            }
        } catch (Exception e) {
            // Ignore
        }

        try {
            if (redisson.getNodesGroup().pingAll()) {
                return true;
            }
        } catch (Exception e) {
            // Ignore
        }

        return false;
    }

    @Override
    public long incr(String key, int exp) {

        try {
            RAtomicLong val = redisson.getAtomicLong(key);
            long expireTimestamp = System.currentTimeMillis() + (exp*1000);
            val.expireAt(expireTimestamp);
            return val.incrementAndGet();
        } catch (Exception e) {
            logger.error("Unable to get key from redis server", e);
        }
        return 0L;

    }

    @Override
    public void set(String key, int exp, Object value) {

        try {
            RBucket val = redisson.getBucket(key);
            val.set(value);
            long expireTimestamp = System.currentTimeMillis() + (exp*1000);
            val.expireAt(expireTimestamp);
        } catch (Exception e) {
            logger.warn("Unable to set key from redis server", e);
        }

    }

    @Override
    public Object get(String key) {

        Object result = null;

        try {
            result = redisson.getBucket(key).get();
        } catch (Exception e) {
            logger.error("Unable to get key from redis server", e);
        }

        return result;

    }

    @Override
    public boolean delete(String key) {

        try {
            RKeys keys = redisson.getKeys();
            return keys.delete(key) > 0;
        } catch (Exception e) {
            logger.error("Unable to delete value from redis server", e);
        }

        return false;

    }

}
