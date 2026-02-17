package com.example.tinyurl.service;

import com.example.tinyurl.dto.StatsResponse;
import com.example.tinyurl.entity.TinyUrl;
import com.example.tinyurl.exception.ShortUrlNotFoundException;
import com.example.tinyurl.repository.TinyUrlRepository;
import com.example.tinyurl.service.implementation.StatsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StatsServiceTest {

    @Mock
    private TinyUrlRepository repository;

    @InjectMocks
    private StatsService statsService;

    @Test
    void shouldReturnStatsWhenShortKeyExists() {

        TinyUrl tinyUrl = new TinyUrl();
        tinyUrl.setShortKey("abc123");
        tinyUrl.setLongUrl("https://google.com");
        tinyUrl.setClickCount(10L);
        tinyUrl.setCreatedAt(LocalDateTime.now());

        when(repository.findByShortKey("abc123"))
                .thenReturn(Optional.of(tinyUrl));

        StatsResponse response = statsService.getStats("abc123");

        assertEquals("https://google.com", response.getLongUrl());
        assertEquals(10L, response.getClickCount());
    }

    @Test
    void shouldThrowExceptionWhenShortKeyNotFound() {

        when(repository.findByShortKey("wrong"))
                .thenReturn(Optional.empty());

        assertThrows(ShortUrlNotFoundException.class, () ->
                statsService.getStats("wrong")
        );
    }
}
