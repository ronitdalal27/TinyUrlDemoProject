package com.example.tinyurl.controller;

import com.example.tinyurl.dto.ShortenUrlRequest;
import com.example.tinyurl.entity.TinyUrl;
import com.example.tinyurl.service.TinyUrlService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.util.Map;

@RestController
public class TinyUrlController {

    private final TinyUrlService service;

    public TinyUrlController(TinyUrlService service) {
        this.service = service;
    }

    // CREATE SHORT URL
    @PostMapping("/api/shorten")
    public ResponseEntity<Map<String, String>> shorten(@RequestBody ShortenUrlRequest request) {

        if (request.getLongUrl() == null || request.getLongUrl().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "longUrl is required"));
        }

        String shortKey = service.createShortUrl(request.getLongUrl());
        String shortUrl = "http://localhost:8080/" + shortKey;

        return ResponseEntity.ok(Map.of("shortUrl", shortUrl));
    }

    // REDIRECT SHORT URL
    @GetMapping("/{shortKey}")
    public ResponseEntity<Void> redirect(@PathVariable String shortKey) {

        TinyUrl tinyUrl = service.getAndIncrement(shortKey);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(tinyUrl.getLongUrl()));

        return new ResponseEntity<>(headers, HttpStatus.FOUND); // 302
    }
}
