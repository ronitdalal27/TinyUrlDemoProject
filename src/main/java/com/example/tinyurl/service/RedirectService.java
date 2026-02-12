package com.example.tinyurl.service;

import com.example.tinyurl.entity.TinyUrl;
import com.example.tinyurl.exception.ShortUrlNotFoundException;
import com.example.tinyurl.exception.UrlExpiredException;
import com.example.tinyurl.repository.TinyUrlRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class RedirectService {

    @Autowired
    TinyUrlRepository repository;

    @Transactional
    public TinyUrl incrementClickAndGet(String shortKey) {

        TinyUrl tinyUrl = repository.findByShortKey(shortKey)
                .orElseThrow(() -> new ShortUrlNotFoundException("Short URL not found"));

        if (tinyUrl.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new UrlExpiredException("Short URL has expired");
        }

        tinyUrl.setClickCount(tinyUrl.getClickCount() + 1);
        return repository.save(tinyUrl);
    }
}
