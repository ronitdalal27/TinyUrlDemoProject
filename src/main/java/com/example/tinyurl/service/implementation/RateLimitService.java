package com.example.tinyurl.service.implementation;

import com.example.tinyurl.exception.RateLimitExceededException;
import com.example.tinyurl.service.interfaces.RateLimitServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class RateLimitService implements RateLimitServiceInterface {

    private static final int MAX_REQUESTS = 2;
    private static final Duration WINDOW = Duration.ofMinutes(1); //this means that each IP can make 2 requests every 1 minute

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public void checkRateLimit(String ip) {

        String key = "rate_limit:" + ip;

        Long count = redisTemplate.opsForValue().increment(key);

        if (count == 1) {
            redisTemplate.expire(key, WINDOW);
        }

        if (count > MAX_REQUESTS) {
            throw new RateLimitExceededException("Too many requests. Try again later.");
        }
    }
}

