package com.example.tinyurl.service;

import com.example.tinyurl.dto.StatsResponse;
import com.example.tinyurl.entity.TinyUrl;
import com.example.tinyurl.exception.UrlAlreadyExistsException;
import com.example.tinyurl.repository.TinyUrlRepository;
import com.example.tinyurl.service.implementation.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TinyUrlServiceTest {

    @Mock
    private TinyUrlRepository repository;

    @Mock
    private UrlValidationService validationService;

    @Mock
    private ShortKeyService shortKeyService;

    @Mock
    private CacheService cacheService;

    @Mock
    private RedirectService redirectService;

    @Mock
    private StatsService statsService;

    @InjectMocks
    private TinyUrlService tinyUrlService;

    @Test
    void shouldReturnExistingShortKeyWhenUrlAlreadyExists() {

        String longUrl = "https://google.com";
        String normalizedUrl = "https://google.com";

        TinyUrl existing = new TinyUrl();
        existing.setShortKey("abc123");

        when(validationService.validateAndNormalizeUrl(longUrl))
                .thenReturn(normalizedUrl);

        when(repository.findByLongUrl(normalizedUrl))
                .thenReturn(Optional.of(existing));

        String result = tinyUrlService.createShortUrl(longUrl, null);

        assertEquals("abc123", result);
    }

    @Test
    void shouldThrowExceptionWhenExistingUrlAndCustomAliasProvided() {

        String longUrl = "https://google.com";
        String normalizedUrl = "https://google.com";

        TinyUrl existing = new TinyUrl();
        existing.setShortKey("abc123");

        when(validationService.validateAndNormalizeUrl(longUrl))
                .thenReturn(normalizedUrl);

        when(repository.findByLongUrl(normalizedUrl))
                .thenReturn(Optional.of(existing));

        assertThrows(UrlAlreadyExistsException.class, () ->
                tinyUrlService.createShortUrl(longUrl, "customAlias"));
    }

    @Test
    void shouldCreateNewShortUrlWhenUrlDoesNotExist() {

        String longUrl = "https://new.com";
        String normalizedUrl = "https://new.com";

        when(validationService.validateAndNormalizeUrl(longUrl))
                .thenReturn(normalizedUrl);

        when(repository.findByLongUrl(normalizedUrl))
                .thenReturn(Optional.empty());

        TinyUrl savedEntity = new TinyUrl();
        savedEntity.setId(10L);

        when(repository.save(any(TinyUrl.class)))
                .thenReturn(savedEntity);

        when(shortKeyService.generateFromId(10L))
                .thenReturn("xyz123");

        String result = tinyUrlService.createShortUrl(longUrl, null);

        assertEquals("xyz123", result);
    }

    @Test
    void shouldCreateShortUrlWithCustomAlias() {

        String longUrl = "https://custom.com";
        String normalizedUrl = "https://custom.com";
        String customAlias = "ronit";

        when(validationService.validateAndNormalizeUrl(longUrl))
                .thenReturn(normalizedUrl);

        when(repository.findByLongUrl(normalizedUrl))
                .thenReturn(Optional.empty());

        String result = tinyUrlService.createShortUrl(longUrl, customAlias);

        assertEquals("ronit", result);
        verify(repository).save(any(TinyUrl.class));
    }

    @Test
    void shouldThrowExceptionWhenCustomAliasAlreadyExists() {

        String longUrl = "https://custom.com";
        String normalizedUrl = "https://custom.com";
        String customAlias = "ronit";

        when(validationService.validateAndNormalizeUrl(longUrl))
                .thenReturn(normalizedUrl);

        when(repository.findByLongUrl(normalizedUrl))
                .thenReturn(Optional.empty());

        doThrow(new RuntimeException("Alias exists"))
                .when(shortKeyService)
                .ensureAliasIsUnique(customAlias);

        assertThrows(RuntimeException.class, () ->
                tinyUrlService.createShortUrl(longUrl, customAlias));
    }

    @Test
    void shouldCallCachePutWhenCacheMiss() {

        TinyUrl tinyUrl = new TinyUrl();
        tinyUrl.setShortKey("abc");
        tinyUrl.setLongUrl("https://google.com");

        when(cacheService.get("abc")).thenReturn(null);
        when(redirectService.incrementClickAndGet("abc")).thenReturn(tinyUrl);

        TinyUrl result = tinyUrlService.redirect("abc");

        assertEquals("https://google.com", result.getLongUrl());
        verify(cacheService).put("abc", "https://google.com");
    }

    @Test
    void shouldNotCallCachePutWhenCacheHit() {

        TinyUrl tinyUrl = new TinyUrl();
        tinyUrl.setShortKey("abc");
        tinyUrl.setLongUrl("https://google.com");

        when(cacheService.get("abc")).thenReturn("https://google.com");
        when(redirectService.incrementClickAndGet("abc")).thenReturn(tinyUrl);

        tinyUrlService.redirect("abc");

        verify(cacheService, never()).put(any(), any());
    }

    @Test
    void shouldReturnStatsFromStatsService() {

        StatsResponse response = new StatsResponse(
                "http://localhost:8080/abc",
                "https://google.com",
                5L,
                LocalDateTime.now()
        );

        when(statsService.getStats("abc")).thenReturn(response);

        StatsResponse result = tinyUrlService.getStats("abc");

        assertEquals(response, result);
    }
}
