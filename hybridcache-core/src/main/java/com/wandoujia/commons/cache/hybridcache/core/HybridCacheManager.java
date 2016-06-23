package com.wandoujia.commons.cache.hybridcache.core;

import com.wandoujia.commons.cache.hybridcache.core.event.CacheEvent;
import com.wandoujia.commons.cache.hybridcache.core.event.CacheEventHandler;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * User: xudong
 * Date: 9/23/13
 * Time: 4:54 PM
 */
public class HybridCacheManager implements CacheManager, InitializingBean {

    // fast lookup by name map
    private final ConcurrentMap<String, Cache> caches = new ConcurrentHashMap<String, Cache>();
    private final Collection<String> names = Collections.unmodifiableSet(caches.keySet());
    private final CacheManager localCacheManager;
    private final CacheManager remoteCacheManager;
    private final RedisTemplate redisTemplate;
    private final CacheEventHandler cacheEventHandler = new CacheEventHandler() {
        @Override
        public void publishEvent(CacheEvent cacheEvent) {
            redisTemplate.convertAndSend(channel, cacheEvent);
        }

        @Override
        public void handleMessage(CacheEvent message) {
            switch (message.getType()) {
                case CacheEvent.PUT:
                    localCacheManager.getCache(message.getName()).put(message.getKey(), message.getValue());
                    break;
                case CacheEvent.EVICT:
                    localCacheManager.getCache(message.getName()).evict(message.getKey());
                    break;
                case CacheEvent.CLEAR:
                    localCacheManager.getCache(message.getName()).clear();
                    break;
            }
        }
    };

    public HybridCacheManager(CacheManager localCacheManager, CacheManager remoteCacheManager, RedisTemplate redisTemplate) {
        this.localCacheManager = localCacheManager;
        this.remoteCacheManager = remoteCacheManager;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public Cache getCache(String name) {
        Cache c = caches.get(name);
        if (c == null) {
            c = new HybridCache(name, localCacheManager.getCache(name), remoteCacheManager.getCache(name), cacheEventHandler);
            caches.put(name, c);
        }

        return c;
    }

    @Override
    public Collection<String> getCacheNames() {
        return names;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        MessageListenerAdapter listener = new MessageListenerAdapter(cacheEventHandler);
        listener.setSerializer(new JdkSerializationRedisSerializer());
        listener.afterPropertiesSet();

        final RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(redisTemplate.getConnectionFactory());
        container.addMessageListener(listener, new ChannelTopic(CacheEventHandler.channel));
        container.afterPropertiesSet();
        container.start();
    }
}
