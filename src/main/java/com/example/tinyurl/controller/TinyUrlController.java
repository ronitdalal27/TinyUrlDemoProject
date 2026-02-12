package com.example.tinyurl.controller;

import com.example.tinyurl.dto.ShortenUrlRequest;
import com.example.tinyurl.dto.StatsResponse;
import com.example.tinyurl.entity.TinyUrl;
import com.example.tinyurl.service.RateLimitService;
import com.example.tinyurl.service.TinyUrlService;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Map;

@RestController
public class TinyUrlController {

    @Value("${app.base-url}")
    private String baseUrl;

    @Autowired
    TinyUrlService service;

    @Autowired
    RateLimitService rateLimitService;

    // CREATE SHORT URL
    @PostMapping("/api/shorten")
    public ResponseEntity<Map<String, String>> shorten(@RequestBody ShortenUrlRequest request, HttpServletRequest httpRequest) {

        if (request.getLongUrl() == null || request.getLongUrl().isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "longUrl is required"));
        }

        String clintIp = httpRequest.getRemoteAddr();

        rateLimitService.checkRateLimit(clintIp);

        String shortKey = service.createShortUrl(
                request.getLongUrl(),
                request.getCustomAlias()
        );

        String shortUrl = baseUrl +"/"+ shortKey;

        return ResponseEntity.ok(Map.of("shortUrl", shortUrl));
    }

    // REDIRECT SHORT URL
    @GetMapping("/{shortKey}")
    public ResponseEntity<Void> redirect(@PathVariable String shortKey) {

        TinyUrl tinyUrl = service.redirect(shortKey);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(tinyUrl.getLongUrl()));

        return new ResponseEntity<>(headers, HttpStatus.FOUND); // 302
    }

    // GET STATS
    @GetMapping("/api/stats/{shortKey}")
    public ResponseEntity<StatsResponse> getStats(
            @PathVariable String shortKey) {

        return ResponseEntity.ok(service.getStats(shortKey));
    }
}
