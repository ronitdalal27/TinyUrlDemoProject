package com.example.tinyurl.service.interfaces;

public interface ShortKeyServiceInterface {
    public String generateFromId(Long id);
    public void ensureAliasIsUnique(String alias);
}
