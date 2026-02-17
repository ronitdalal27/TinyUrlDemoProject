package com.example.tinyurl.service.interfaces;

public interface UrlValidationServiceInterface {
    public String validateAndNormalizeUrl(String longUrl);
    public void validateCustomAlias(String alias);
}
