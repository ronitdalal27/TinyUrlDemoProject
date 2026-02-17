package com.example.tinyurl.service;

import com.example.tinyurl.exception.InvalidUrlException;
import com.example.tinyurl.service.implementation.UrlValidationService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UrlValidationServiceTest {

    private final UrlValidationService validationService = new UrlValidationService();

    @Test
    void shouldAcceptValidUrl() {
        String validUrl = "https://google.com";

        String result = validationService.validateAndNormalizeUrl(validUrl);

        assertNotNull(result);
    }

    @Test
    void shouldThrowExceptionForInvalidUrl() {
        String invalidUrl = "invalid-url";

        assertThrows(InvalidUrlException.class, () ->
                validationService.validateAndNormalizeUrl(invalidUrl));
    }

    @Test
    void shouldAcceptValidCustomAlias() {

        assertDoesNotThrow(() ->
                validationService.validateCustomAlias("ronit123"));
    }

    @Test
    void shouldThrowExceptionForInvalidCustomAlias() {

        assertThrows(InvalidUrlException.class, () ->
                validationService.validateCustomAlias("ro")); // too short
    }

    @Test
    void shouldThrowExceptionForAliasWithSpecialCharacters() {

        assertThrows(InvalidUrlException.class, () ->
                validationService.validateCustomAlias("ronit@123"));
    }
}
