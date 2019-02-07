package com.untzuntz.ustack.data;

import org.apache.log4j.Logger;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;

public class UJedisConnectionSingle implements UJedisConnectionInt {

    static Logger logger = Logger.getLogger(UJedisConnectionSingle.class);

    private Jedis jedis;


    public UJedisConnectionSingle(HostAndPort server) {

        jedis = new Jedis(server);

    }

    @Override
    public boolean isConnected() {

        if (jedis != null && jedis.isConnected()) {
            return true;
        }

        return false;
    }

    @Override
    public Long incr(byte[] key) {

        return jedis.incr(key);

    }

    @Override
    public String setex(byte[] key, int seconds, byte[] value) {

        return jedis.setex(key, seconds, value);

    }

    @Override
    public byte[] get(byte[] key) {

        return jedis.get(key);

    }

    @Override
    public Long del(final byte[] key) {

        return jedis.del(key);

    }


}
