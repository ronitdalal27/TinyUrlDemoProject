package com.example.tinyurl.service;

import com.example.tinyurl.exception.RateLimitExceededException;
import com.example.tinyurl.service.implementation.RateLimitService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RateLimitServiceTest {

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @InjectMocks
    private RateLimitService rateLimitService;

    @BeforeEach
    void setup() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    void shouldAllowRequestWhenUnderLimit() {

        when(valueOperations.increment("rate_limit:127.0.0.1"))
                .thenReturn(1L);

        rateLimitService.checkRateLimit("127.0.0.1");

        verify(redisTemplate).expire(eq("rate_limit:127.0.0.1"), any());
    }

    @Test
    void shouldThrowExceptionWhenLimitExceeded() {

        when(valueOperations.increment("rate_limit:127.0.0.1"))
                .thenReturn(3L);

        assertThrows(RateLimitExceededException.class, () ->
                rateLimitService.checkRateLimit("127.0.0.1")
        );
    }
}
