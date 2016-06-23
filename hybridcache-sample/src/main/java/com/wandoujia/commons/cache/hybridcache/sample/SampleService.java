package com.wandoujia.commons.cache.hybridcache.sample;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.HashMap;
import java.util.Map;

/**
 * User: xudong
 * Date: 9/25/13
 * Time: 3:27 PM
 */
public class SampleService {
    private Map<String, Pojo> storage = new HashMap<String, Pojo>();

    public SampleService() {
    }

    @CacheEvict(value = "sample", key = "#key")
    public void save(String key, Pojo value) {
        storage.put(key ,value);
    }

    @Cacheable(value = "sample", key = "#key")
    public Pojo load(String key) {
        // simulate slow database operation
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {

        }
        System.out.println("cache miss");
        return storage.get(key);
    }

}
