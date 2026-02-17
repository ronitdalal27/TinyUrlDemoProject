package com.example.tinyurl.service.implementation;

import com.example.tinyurl.service.interfaces.CacheServiceInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class CacheService implements  CacheServiceInterface {

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    private static final Logger log = LoggerFactory.getLogger(CacheService.class);
    private static final Duration CACHE_TTL = Duration.ofHours(24);


    @Override
    public String get(String key) {
        String value = redisTemplate.opsForValue().get(key);
        if (value != null) {
            log.info("Cache HIT for shortKey: {}", key);
        } else {
            log.info("Cache MISS for shortKey: {}", key);
        }
        return value;
    }

    @Override
    public void put(String key, String value) {
        redisTemplate.opsForValue()
                .set(key, value, CACHE_TTL);
    }
}
