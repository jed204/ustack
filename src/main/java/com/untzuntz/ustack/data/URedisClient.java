package com.untzuntz.ustack.data;

import org.apache.log4j.Logger;
import redis.clients.jedis.HostAndPort;

import java.io.*;

public class URedisClient implements UDataCacheClientInt {

    static Logger logger = Logger.getLogger(URedisClient.class);

    UJedisConnectionInt jc;

    public URedisClient(String connectionString) {

        connect(connectionString);

    }

    public void connect(String connectionString) {

        HostAndPort server = HostAndPort.parseString(connectionString);

        try {
            jc = new UJedisConnectionCluster(server);
        } catch (Exception e) {
            logger.error("Unable to connect to redis in cluster mode", e);
        }
        if (jc == null || !jc.isConnected()) {
            try {
                jc = new UJedisConnectionSingle(server);
            } catch (Exception e) {
                logger.error("Unable to connect to redis in single mode", e);
            }
        }

    }

    @Override
    public long incr(String key, int timeout) {

        try {
            return jc.incr(key.getBytes(), timeout);
        } catch (Exception e) {
            logger.error("Unable to get key from redis server", e);
        }
        return 0L;

    }

    @Override
    public void set(String key, int exp, Object value) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutput out;

        try {

            out = new ObjectOutputStream(bos);
            out.writeObject(value);
            out.flush();
            byte[] bytes = bos.toByteArray();

            jc.setex(key.getBytes(), exp, bytes);

        } catch (Exception e) {

            logger.warn("Unable to set key from redis server", e);

        } finally {

            try {
                bos.close();
            } catch (IOException ex) {
                // ignore close exception
            }

        }

    }

    @Override
    public Object get(String key) {

        Object result = null;
        ObjectInput in = null;

        try {
            byte[] obj = jc.get(key.getBytes());

            if(obj == null) {
                logger.debug("Redis Cache MISS for KEY: " + key);
                return null;
            } else {
                logger.debug("Redis Cache HIT for KEY: " + key);
            }

            ByteArrayInputStream bis = new ByteArrayInputStream(obj);
            in = new ObjectInputStream(bis);
            result = in.readObject();

        } catch (Exception e) {

            logger.error("Unable to get key from redis server", e);

        } finally {

            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                // ignore close exception
            }

        }

        return result;

    }

    @Override
    public boolean delete(String key) {

        try {
            return jc.del(key.getBytes()) > 0;
        } catch (Exception e) {
            logger.error("Unable to delete value from redis server", e);
        }

        return false;

    }

}
