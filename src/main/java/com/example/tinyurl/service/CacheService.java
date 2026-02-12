package com.example.tinyurl.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class CacheService {

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    private static final Logger log = LoggerFactory.getLogger(CacheService.class);

    public String get(String key) {
        String value = redisTemplate.opsForValue().get(key);
        if (value != null) {
            log.info("Cache HIT for shortKey: {}", key);
        } else {
            log.info("Cache MISS for shortKey: {}", key);
        }
        return value;
    }

    public void put(String key, String value) {
        redisTemplate.opsForValue()
                .set(key, value, Duration.ofHours(24));
    }
}
