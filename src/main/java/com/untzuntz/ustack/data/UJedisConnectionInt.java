package com.untzuntz.ustack.data;

public interface UJedisConnectionInt {

    boolean isConnected();
    Long incr(final byte[] key);
    String setex(final byte[] key, final int seconds, final byte[] value);
    byte[] get(final byte[] key);
    Long del(final byte[] key);

}
