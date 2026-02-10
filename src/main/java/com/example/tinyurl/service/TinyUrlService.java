package com.example.tinyurl.service;

import java.time.Duration;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.example.tinyurl.dto.StatsResponse;
import com.example.tinyurl.entity.TinyUrl;
import com.example.tinyurl.exception.InvalidUrlException;
import com.example.tinyurl.exception.ShortUrlNotFoundException;
import com.example.tinyurl.repository.TinyUrlRepository;
import com.example.tinyurl.utils.Base62Util;
import com.example.tinyurl.utils.UrlNormalizer;
import com.example.tinyurl.utils.UrlValidator;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class TinyUrlService {
    private final TinyUrlRepository repository;
    private final RedisTemplate<String, String> redisTemplate; // here we can change the name also, not neccessary same as redisTemplate in RedisConfig class because we are autowiring it by type not by name so it will work fine even if we change the name here but for better readability we can keep the same name
    private static final Logger log = LoggerFactory.getLogger(TinyUrlService.class);

    public TinyUrlService(TinyUrlRepository repository, RedisTemplate<String, String> redisTemplate) {
        this.repository = repository;
        this.redisTemplate = redisTemplate;
    }

    @Transactional
    public String createShortUrl(String longUrl, String customAlias) {

        // String test = null;
        // test.length();  //just to check exception handling of genericexceptionhandler wokring fine

        if (!UrlValidator.isValid(longUrl)) {
            throw new InvalidUrlException("Invalid URL format only http and https are accpeted your current url is : " + longUrl);
        }

        String normalizedUrl = UrlNormalizer.normalize(longUrl);

        // If custom alias is provided
        if (customAlias != null && !customAlias.isBlank()) {

            validateCustomAlias(customAlias);

            if (repository.existsByShortKey(customAlias)) {
                throw new InvalidUrlException("Custom alias already in use, please choose another one");
            }

            TinyUrl tinyUrl = new TinyUrl();
            tinyUrl.setLongUrl(normalizedUrl);
            tinyUrl.setShortKey(customAlias);

            repository.save(tinyUrl);
            return customAlias;
        }

        // If already exists, return same short URL
        return repository.findByLongUrl(normalizedUrl)
                .map(TinyUrl::getShortKey)
                .orElseGet(() -> {
                    TinyUrl tinyUrl = new TinyUrl();
                    tinyUrl.setLongUrl(normalizedUrl);

                    // Save first to get auto-generated ID
                    TinyUrl saved = repository.save(tinyUrl);

                    // Convert ID to Base62
                    String shortKey = Base62Util.encode(saved.getId());
                    saved.setShortKey(shortKey);

                    repository.save(saved);
                    return shortKey;
                });
    }

    @Transactional// to handle concurrent access to same short
    public TinyUrl getAndIncrement(String shortKey) {

        //Try Redis
        String cachedLongUrl = redisTemplate.opsForValue().get(shortKey);

        if (cachedLongUrl != null) {
            log.info("Cache HIT for shortKey: {}", shortKey);
            return incrementClickCount(shortKey);
        }

        //Cache MISS → DB
        log.info("Cache MISS for shortKey: {}", shortKey);

        TinyUrl tinyUrl = repository.findByShortKey(shortKey)
                .orElseThrow(() -> new ShortUrlNotFoundException("Short URL not found"));

        //Store in Redis (URL only)
        redisTemplate.opsForValue()
                .set(shortKey, tinyUrl.getLongUrl(), Duration.ofHours(24));

        //Update click count
        tinyUrl.setClickCount(tinyUrl.getClickCount() + 1);
        return repository.save(tinyUrl);
    }

    @Transactional
    public StatsResponse getStats(String shortKey) {

        TinyUrl tinyUrl = repository.findByShortKey(shortKey)
                .orElseThrow(() -> new ShortUrlNotFoundException("Short URL not found"));

        return new StatsResponse(
                "http://localhost:8080/" + shortKey,
                tinyUrl.getLongUrl(),
                tinyUrl.getClickCount(),
                tinyUrl.getCreatedAt()
        );
    }


    private TinyUrl incrementClickCount(String shortKey) {
        TinyUrl tinyUrl = repository.findByShortKey(shortKey)
                .orElseThrow(() -> new ShortUrlNotFoundException("Short URL not found"));
    
        tinyUrl.setClickCount(tinyUrl.getClickCount() + 1);
        return repository.save(tinyUrl);
    }

    private void validateCustomAlias(String alias) {
        if (!alias.matches("^[a-zA-Z0-9]{3,20}$")) {
            throw new InvalidUrlException(
                "Custom alias must be alphanumeric and 3–20 characters long"
            );
        }
    }

}

/*
    now lets talk about opsForValue() method of RedisTemplate, 
    it is used to perform operations on simple key-value pairs in Redis. 
    It provides methods to set, get, and delete values associated with keys. 
    In our case, we are using it to store the mapping of shortKey to longUrl in Redis. 
    When we call redisTemplate.opsForValue().set(shortKey, tinyUrl.getLongUrl(), Duration.ofHours(24)),
    we are setting the value of shortKey to longUrl in Redis with an expiration time of 24 hours in string format.
    and when we call redisTemplate.opsForValue().get(shortKey), we are retrieving the value associated with shortKey from Redis, which will be the longUrl if it exists in Redis, otherwise it will return null.    
 */