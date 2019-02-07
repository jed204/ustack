package com.untzuntz.ustack.data;

import org.apache.log4j.Logger;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;

import java.util.HashSet;
import java.util.Set;

public class UJedisConnectionCluster implements UJedisConnectionInt {

    static Logger logger = Logger.getLogger(UJedisConnectionCluster.class);

    private JedisCluster jc;

    public UJedisConnectionCluster(HostAndPort server) {

        Set<HostAndPort> jedisClusterNodes = new HashSet<>();
        //Jedis Cluster will attempt to discover cluster nodes automatically
        jedisClusterNodes.add(server);
        jc = new JedisCluster(jedisClusterNodes);

    }

    @Override
    public boolean isConnected() {

        if (jc.getClusterNodes().size() > 0) {
            return true;
        }

        return false;
    }

    @Override
    public Long incr(byte[] key) {

        return jc.incr(key);

    }

    @Override
    public String setex(byte[] key, int seconds, byte[] value) {

        return jc.setex(key, seconds, value);

    }

    @Override
    public byte[] get(byte[] key) {

        return jc.get(key);

    }

    @Override
    public Long del(final byte[] key) {

        return jc.del(key);

    }

}
