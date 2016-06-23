package com.wandoujia.commons.cache.hybridcache.core;

import com.wandoujia.commons.cache.hybridcache.core.event.CacheEvent;
import com.wandoujia.commons.cache.hybridcache.core.event.CacheEventHandler;
import org.springframework.cache.Cache;

/**
 * User: xudong
 * Date: 9/25/13
 * Time: 12:43 PM
 */
class HybridCache implements Cache {

    private final String name;

    private final Cache localCache;

    private final Cache remoteCache;

    private final CacheEventHandler cacheEventHandler;

    HybridCache(String name, Cache localCache, Cache remoteCache, CacheEventHandler cacheEventHandler) {
        this.name = name;
        this.localCache = localCache;
        this.remoteCache = remoteCache;
        this.cacheEventHandler = cacheEventHandler;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Object getNativeCache() {
        return null;
    }

    @Override
    public ValueWrapper get(Object key) {
        ValueWrapper valueWrapper = localCache.get(key);
        if (valueWrapper == null) {
            valueWrapper = remoteCache.get(key);
            if (valueWrapper != null) {
                localCache.put(key, valueWrapper.get());
            }
        }

        return valueWrapper;
    }

    @Override
    public <T> T get(Object key, Class<T> type) {
        final ValueWrapper valueWrapper = get(key);

        Object value = (valueWrapper != null ? valueWrapper.get() : null);
        if (value != null && type != null && !type.isInstance(value)) {
            throw new IllegalStateException("Cached value is not of required type [" + type.getName() + "]: " + value);
        }
        return (T) value;
    }

    @Override
    public void put(Object key, Object value) {
        localCache.put(key, value);
        remoteCache.put(key, value);

        CacheEvent cacheEvent = new CacheEvent();
        cacheEvent.setType(CacheEvent.PUT);
        cacheEvent.setName(getName());
        cacheEvent.setKey(key);
        cacheEvent.setValue(value);

        cacheEventHandler.publishEvent(cacheEvent);
    }

    @Override
    public ValueWrapper putIfAbsent(Object key, Object value) {
        throw new UnsupportedOperationException("hybridCache is not thread safe");
    }

    @Override
    public void evict(Object key) {
        localCache.evict(key);
        remoteCache.evict(key);

        CacheEvent cacheEvent = new CacheEvent();
        cacheEvent.setType(CacheEvent.EVICT);
        cacheEvent.setName(getName());
        cacheEvent.setKey(key);

        cacheEventHandler.publishEvent(cacheEvent);
    }

    @Override
    public void clear() {
        localCache.clear();
        remoteCache.clear();

        CacheEvent cacheEvent = new CacheEvent();
        cacheEvent.setType(CacheEvent.CLEAR);
        cacheEvent.setName(getName());

        cacheEventHandler.publishEvent(cacheEvent);
    }


}
