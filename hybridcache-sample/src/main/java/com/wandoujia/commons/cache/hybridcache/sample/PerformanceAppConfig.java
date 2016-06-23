package com.wandoujia.commons.cache.hybridcache.sample;

import com.wandoujia.commons.cache.hybridcache.core.HybridCacheManager;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.cache.guava.GuavaCacheManager;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.util.Assert;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

//import org.springmodules.cache.guava.GuavaCacheManager;

/**
 * User: xudong
 * Date: 9/23/13
 * Time: 3:40 PM
 */
@Configuration
@EnableCaching
public class PerformanceAppConfig {
    public static void main(String[] args) throws InterruptedException {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(PerformanceAppConfig.class);
        SampleService sampleService = applicationContext.getBean(SampleService.class);


        Pojo pojo = new Pojo();
        pojo.setName("name");
        byte[] bytes = new byte[1024 * 1024];
        Arrays.fill(bytes, (byte) 1);

        pojo.setBlob(new String(bytes));

        sampleService.save("id", pojo);

        // warm up
        sampleService.load("id");

        Map map = new ConcurrentHashMap();
        map.put("id", pojo);

        long start = System.currentTimeMillis();
        int count = 1000000;
        for (int i = 0; i < count; i++) {
            Pojo p = sampleService.load("id");
            Assert.notNull(p);

        }

        System.out.println("load " + count + " times spend " + (System.currentTimeMillis() - start) + " ms");
    }

    @Bean
    JedisConnectionFactory jedisConnectionFactory() {
        return new JedisConnectionFactory();
    }

    @Bean
    RedisTemplate<String, Object> redisTemplate() {
        final RedisTemplate<String, Object> template = new RedisTemplate<String, Object>();
        template.setConnectionFactory(jedisConnectionFactory());
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new JdkSerializationRedisSerializer());
        template.setValueSerializer(new JdkSerializationRedisSerializer());
        return template;
    }

    @Bean
    CacheManager cacheManager() {
        CacheManager localCacheManager = new ConcurrentMapCacheManager();
        CacheManager remoteCacheManager = new RedisCacheManager(redisTemplate());
        return new HybridCacheManager(localCacheManager, remoteCacheManager, redisTemplate());
    }

    @Bean
    SampleService sampleService() {
        return new SampleService();
    }

}
