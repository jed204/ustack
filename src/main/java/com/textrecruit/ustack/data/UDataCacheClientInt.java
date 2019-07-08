package com.textrecruit.ustack.data;

public interface UDataCacheClientInt {

    long incr(String key, int exp);
    void set(final String key, final int exp, final Object value);
    Object get(final String key);
    boolean delete(final String key);
    boolean isConnected();

}
