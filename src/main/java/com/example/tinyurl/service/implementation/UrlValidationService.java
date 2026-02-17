package com.example.tinyurl.service.implementation;

import com.example.tinyurl.exception.InvalidUrlException;
import com.example.tinyurl.service.interfaces.UrlValidationServiceInterface;
import com.example.tinyurl.utils.UrlNormalizer;
import com.example.tinyurl.utils.UrlValidator;
import org.springframework.stereotype.Service;


@Service
public class UrlValidationService implements UrlValidationServiceInterface {

    @Override
    public String validateAndNormalizeUrl(String longUrl) {
        if (!UrlValidator.isValid(longUrl)) {
            throw new InvalidUrlException("Invalid URL format. Only http and https allowed: " + longUrl);
        }
        return UrlNormalizer.normalize(longUrl);
    }

    @Override
    public void validateCustomAlias(String alias) {
        if (!alias.matches("^[a-zA-Z0-9]{3,20}$")) {
            throw new InvalidUrlException("Custom alias must be alphanumeric and 3–20 characters long");
        }
    }

}
