package com.example.tinyurl.service.implementation;

import com.example.tinyurl.exception.UrlAlreadyExistsException;
import com.example.tinyurl.service.interfaces.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.tinyurl.dto.StatsResponse;
import com.example.tinyurl.entity.TinyUrl;
import com.example.tinyurl.repository.TinyUrlRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Isolation;

@Service
public class TinyUrlService implements TinyUrlServiceInterface {

    @Autowired
    TinyUrlRepository repository;
    @Autowired
    UrlValidationServiceInterface validationService;
    @Autowired
    ShortKeyServiceInterface shortKeyService;
    @Autowired
    CacheServiceInterface cacheService;
    @Autowired
    RedirectServiceInterface redirectService;
    @Autowired
    StatsServiceInterface statsService;

    @Override
    @Transactional
    public String createShortUrl(String longUrl, String customAlias) {
        String normalizedUrl = validationService.validateAndNormalizeUrl(longUrl);

        //Check if URL already exists
        TinyUrl existing = repository.findByLongUrl(normalizedUrl).orElse(null);

        // STRICT RULE
        if (existing != null) {

            if (customAlias != null && !customAlias.isBlank()) {
                throw new UrlAlreadyExistsException("This URL already has a short link. Custom alias not allowed.");
            }

            // If no alias requested → return existing
            return existing.getShortKey();
        }

        // Custom alias flow
        if (customAlias != null && !customAlias.isBlank()) {

            validationService.validateCustomAlias(customAlias);
            shortKeyService.ensureAliasIsUnique(customAlias);

            TinyUrl tinyUrl = new TinyUrl();
            tinyUrl.setLongUrl(normalizedUrl);
            tinyUrl.setShortKey(customAlias);

            repository.save(tinyUrl);
            return customAlias;
        }

        // Existing URL reuse
        return repository.findByLongUrl(normalizedUrl)
                .map(TinyUrl::getShortKey)
                .orElseGet(() -> {
                    TinyUrl tinyUrl = new TinyUrl();
                    tinyUrl.setLongUrl(normalizedUrl);

                    TinyUrl saved = repository.save(tinyUrl);
                    String shortKey = shortKeyService.generateFromId(saved.getId());

                    saved.setShortKey(shortKey);
                    repository.save(saved);

                    return shortKey;
                });
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)// to prevent dirty reads while allowing concurrent access
    public TinyUrl redirect(String shortKey) {

        String cached = cacheService.get(shortKey);

        TinyUrl tinyUrl = redirectService.incrementClickAndGet(shortKey);

        if (cached == null) {
            cacheService.put(shortKey, tinyUrl.getLongUrl());
        }

        return tinyUrl;
    }

    @Override
    public StatsResponse getStats(String shortKey) {
        return statsService.getStats(shortKey);
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