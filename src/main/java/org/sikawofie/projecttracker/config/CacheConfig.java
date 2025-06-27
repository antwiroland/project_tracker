package org.sikawofie.projecttracker.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import java.time.Duration;
import java.util.List;
import java.util.Set;

@Configuration
@EnableCaching
public class CacheConfig {

    private final List<String> cacheName = List.of( "projects",
            "developers",
            "tasks",
            "projectsPage",
            "developerTasks");

    @Bean
    @Profile("dev")
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager(String.valueOf(cacheName));
    }

    @Bean
    @Profile("prod")
    public CacheManager redisCacheManager(RedisConnectionFactory redisConnectionFactory){
        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(15)).disableCachingNullValues();


        return RedisCacheManager.builder(redisConnectionFactory)
                .cacheDefaults(defaultConfig)
                .initialCacheNames(Set.copyOf(cacheName)).build();
    }
}
