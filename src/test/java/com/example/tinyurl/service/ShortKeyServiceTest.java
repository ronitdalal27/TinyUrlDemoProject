package com.example.tinyurl.service;

import com.example.tinyurl.exception.InvalidUrlException;
import com.example.tinyurl.repository.TinyUrlRepository;
import com.example.tinyurl.service.implementation.ShortKeyService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShortKeyServiceTest {

    @Mock
    private TinyUrlRepository repository;

    @InjectMocks
    private ShortKeyService shortKeyService;

    @Test
    void shouldGenerateBase62KeyFromId() {
        String result = shortKeyService.generateFromId(1L);

        assertNotNull(result);
        assertEquals(7, result.length());
    }

    @Test
    void shouldNotThrowWhenAliasIsUnique() {

        when(repository.existsByShortKey("ronit"))
                .thenReturn(false);

        assertDoesNotThrow(() ->
                shortKeyService.ensureAliasIsUnique("ronit"));
    }

    @Test
    void shouldThrowExceptionWhenAliasAlreadyExists() {

        when(repository.existsByShortKey("ronit"))
                .thenReturn(true);

        assertThrows(InvalidUrlException.class, () ->
                shortKeyService.ensureAliasIsUnique("ronit"));
    }
}
