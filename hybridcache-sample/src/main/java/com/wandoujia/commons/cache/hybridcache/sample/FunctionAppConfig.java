package com.wandoujia.commons.cache.hybridcache.sample;

import com.wandoujia.commons.cache.hybridcache.core.HybridCacheManager;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
//import org.springmodules.cache.guava.GuavaCacheManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * User: xudong
 * Date: 9/23/13
 * Time: 3:40 PM
 */
@Configuration
@EnableCaching
public class FunctionAppConfig {
    public static void main(String[] args) throws InterruptedException, IOException {


        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(FunctionAppConfig.class);
        SampleService sampleService = applicationContext.getBean(SampleService.class);

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String line = null;
        while ((line = reader.readLine()) != null) {
            String[] tokens = line.split(" ");
            if ("save".equals(tokens[0])) {
                Pojo p = new Pojo();
                p.setName(tokens[2]);
                p.setBlob(tokens[2]);
                sampleService.save(tokens[1], p);
                System.out.println("save " + tokens[1]+ " " + p);
            }

            if ("get".equals(tokens[0])) {
                System.out.println(sampleService.load(tokens[1]));
            }

        }

        reader.close();
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
