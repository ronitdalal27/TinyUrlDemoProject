package com.example.tinyurl.service;

import com.example.tinyurl.service.implementation.CacheService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CacheServiceTest {

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @InjectMocks
    private CacheService cacheService;

    @BeforeEach
    void setup() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    void shouldReturnValueWhenKeyExists() {
        when(valueOperations.get("abc")).thenReturn("https://google.com");

        String result = cacheService.get("abc");

        assertEquals("https://google.com", result);
    }

    @Test
    void shouldReturnNullWhenKeyDoesNotExist() {
        when(valueOperations.get("abc")).thenReturn(null);

        String result = cacheService.get("abc");

        assertNull(result);
    }

    @Test
    void shouldPutValueIntoRedis() {
        cacheService.put("abc", "https://google.com");

        verify(valueOperations).set(eq("abc"), eq("https://google.com"), any(Duration.class));
    }
}
