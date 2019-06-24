package com.textrecruit.ustack.data;

import org.apache.log4j.Logger;
import org.redisson.Redisson;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RBucket;
import org.redisson.api.RKeys;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

public class URedisClient implements UDataCacheClientInt {

    static Logger logger = Logger.getLogger(URedisClient.class);

    private RedissonClient redisson;

    public URedisClient(String connectionString) {

        connect(connectionString);

    }

    public void connect(String connectionString) {

        try {

            Config config = new Config();
            config.useSentinelServers()
                    .setMasterName("mymaster")
                    .addSentinelAddress(connectionString);

            redisson = Redisson.create(config);

        } catch (Exception e) {
            logger.error("Unable to connect to redis in cluster mode", e);
        }

        if (redisson == null || !isConnected()) {
            try {

                Config config = new Config();
                config.useSingleServer().setAddress(connectionString);
                redisson = Redisson.create(config);

            } catch (Exception e) {
                logger.error("Unable to connect to redis in single mode", e);
            }
        }

    }

    public boolean isConnected() {

        if (redisson.getClusterNodesGroup().pingAll()) {
            return true;
        }

        return false;
    }

    @Override
    public long incr(String key, int exp) {

        try {
            RAtomicLong val = redisson.getAtomicLong(key);
            val.expireAt(exp);
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
            val.expireAt(exp);
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
