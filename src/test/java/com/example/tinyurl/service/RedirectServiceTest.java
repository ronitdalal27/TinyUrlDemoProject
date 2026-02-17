package com.example.tinyurl.service;

import com.example.tinyurl.entity.TinyUrl;
import com.example.tinyurl.exception.ShortUrlNotFoundException;
import com.example.tinyurl.exception.UrlExpiredException;
import com.example.tinyurl.repository.TinyUrlRepository;
import com.example.tinyurl.service.implementation.RedirectService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RedirectServiceTest {

    @Mock
    private TinyUrlRepository repository;

    @InjectMocks
    private RedirectService redirectService;

    @Test
    void shouldIncrementClickCountWhenShortKeyExists() {

        TinyUrl tinyUrl = new TinyUrl();
        tinyUrl.setShortKey("abc123");
        tinyUrl.setClickCount(5L);
        tinyUrl.setExpiresAt(LocalDateTime.now().plusDays(5));

        when(repository.findByShortKey("abc123"))
                .thenReturn(Optional.of(tinyUrl));

        when(repository.save(any(TinyUrl.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        TinyUrl result = redirectService.incrementClickAndGet("abc123");

        assertEquals(6L, result.getClickCount());
        verify(repository).save(tinyUrl);
    }

    @Test
    void shouldThrowExceptionWhenShortKeyNotFound() {

        when(repository.findByShortKey("wrong"))
                .thenReturn(Optional.empty());

        assertThrows(ShortUrlNotFoundException.class, () ->
                redirectService.incrementClickAndGet("wrong"));
    }

    @Test
    void shouldThrowExceptionWhenUrlExpired() {

        TinyUrl tinyUrl = new TinyUrl();
        tinyUrl.setShortKey("expired123");
        tinyUrl.setClickCount(2L);
        tinyUrl.setExpiresAt(LocalDateTime.now().minusDays(1)); // expired

        when(repository.findByShortKey("expired123"))
                .thenReturn(Optional.of(tinyUrl));

        assertThrows(UrlExpiredException.class, () ->
                redirectService.incrementClickAndGet("expired123"));

        verify(repository, never()).save(any());
    }
}
