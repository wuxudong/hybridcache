package com.wandoujia.commons.cache.hybridcache.core.event;

import java.io.Serializable;

/**
 * User: xudong
 * Date: 9/25/13
 * Time: 1:26 PM
 */
public class CacheEvent implements Serializable{

    public static final int PUT = 1;
    public static final int EVICT = 2;
    public static final int CLEAR = 3;

    private int type;

    private String name;

    private Object key;

    private Object value;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getKey() {
        return key;
    }

    public void setKey(Object key) {
        this.key = key;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "CacheEvent{" +
                "type=" + type +
                ", name='" + name + '\'' +
                ", key=" + key +
                ", value=" + value +
                '}';
    }
}
