package com.example.tinyurl.service.implementation;

import com.example.tinyurl.dto.StatsResponse;
import com.example.tinyurl.entity.TinyUrl;
import com.example.tinyurl.exception.ShortUrlNotFoundException;
import com.example.tinyurl.repository.TinyUrlRepository;
import com.example.tinyurl.service.interfaces.StatsServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StatsService implements StatsServiceInterface {

    @Autowired
    TinyUrlRepository repository;

    @Override
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
}
