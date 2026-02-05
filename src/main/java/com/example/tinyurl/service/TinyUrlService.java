package com.example.tinyurl.service;

import org.springframework.stereotype.Service;

import com.example.tinyurl.entity.TinyUrl;
import com.example.tinyurl.repository.TinyUrlRepository;
import com.example.tinyurl.utils.Base62Util;

import jakarta.transaction.Transactional;

@Service
public class TinyUrlService {
    private final TinyUrlRepository repository;

    public TinyUrlService(TinyUrlRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public String createShortUrl(String longUrl) {

        // If already exists, return same short URL
        return repository.findByLongUrl(longUrl)
                .map(tiny -> tiny.getShortKey())
                .orElseGet(() -> {
                    TinyUrl tinyUrl = new TinyUrl();
                    tinyUrl.setLongUrl(longUrl);

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
                                    .orElseThrow(() -> new RuntimeException("Short URL not found"));

        tinyUrl.setClickCount(tinyUrl.getClickCount() + 1);
        return repository.save(tinyUrl);
    }
}
