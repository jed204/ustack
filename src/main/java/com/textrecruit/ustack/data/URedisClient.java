package com.textrecruit.ustack.data;

import com.textrecruit.ustack.main.UAppCfg;
import com.textrecruit.ustack.main.UOpts;
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

        if (ConnectionType.REPLICATED.equals(type)) {
            connectReplicated(connectionString);
        } else if (ConnectionType.SENTINEL.equals(type)) {
            connectSentinel(connectionString);
        } else {
            connectSingle(connectionString);
        }

    }

    public void connectReplicated(String connectionString) {

        try {

            Config config = new Config();
            config.useReplicatedServers()
                    .setPassword(UOpts.getString(UAppCfg.CACHE_HOST_PASSWORD))
                    .addNodeAddress(connectionString);

            redisson = Redisson.create(config);

        } catch (Exception e) {
            logger.error("Unable to connect to redis in sentinel mode", e);
        }

    }

    public void connectSentinel(String connectionString) {

        try {

            Config config = new Config();
            config.useSentinelServers()
                    .setPassword(UOpts.getString(UAppCfg.CACHE_HOST_PASSWORD))
                    .setMasterName("mymaster")
                    .addSentinelAddress(connectionString);

            redisson = Redisson.create(config);

        } catch (Exception e) {
            logger.error("Unable to connect to redis in sentinel mode", e);
        }

    }

    public void connectSingle(String connectionString) {

        try {

            Config config = new Config();
            config.useSingleServer()
                    .setPassword(UOpts.getString(UAppCfg.CACHE_HOST_PASSWORD))
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
