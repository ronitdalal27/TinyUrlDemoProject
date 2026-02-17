package com.example.tinyurl.service.interfaces;

public interface CacheServiceInterface {
    public String get(String key);
    public void put(String key, String value);
}
