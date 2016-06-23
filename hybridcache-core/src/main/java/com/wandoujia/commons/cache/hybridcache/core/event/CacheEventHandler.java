package com.wandoujia.commons.cache.hybridcache.core.event;

/**
 * User: xudong
 * Date: 9/25/13
 * Time: 1:40 PM
 */
public interface CacheEventHandler {
    String channel = "cache_event_channel";

    // TODO: local server will also receive this event, then local cache is operated twice.
    void publishEvent(CacheEvent cacheEvent);

    void handleMessage(CacheEvent cacheEvent);
}
