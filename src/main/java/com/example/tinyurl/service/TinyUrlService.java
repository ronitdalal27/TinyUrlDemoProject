package com.example.tinyurl.service;

import org.springframework.stereotype.Service;

import com.example.tinyurl.entity.TinyUrl;
import com.example.tinyurl.exception.InvalidUrlException;
import com.example.tinyurl.exception.ShortUrlNotFoundException;
import com.example.tinyurl.repository.TinyUrlRepository;
import com.example.tinyurl.utils.Base62Util;
import com.example.tinyurl.utils.UrlNormalizer;
import com.example.tinyurl.utils.UrlValidator;

import jakarta.transaction.Transactional;

@Service
public class TinyUrlService {
    private final TinyUrlRepository repository;

    public TinyUrlService(TinyUrlRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public String createShortUrl(String longUrl) {

        // String test = null;
        // test.length();  //just to check exception handling of genericexceptionhandler wokring fine

        if (!UrlValidator.isValid(longUrl)) {
            throw new InvalidUrlException("Invalid URL format only http and https are accpeted your current url is : " + longUrl);
        }

        String normalizedUrl = UrlNormalizer.normalize(longUrl);

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

    @Transactional
    public TinyUrl getAndIncrement(String shortKey) {
        TinyUrl tinyUrl = repository.findByShortKey(shortKey)
                                    .orElseThrow(() -> new ShortUrlNotFoundException("Short URL not found: " + shortKey));

        tinyUrl.setClickCount(tinyUrl.getClickCount() + 1);
        return repository.save(tinyUrl);
    }
}
