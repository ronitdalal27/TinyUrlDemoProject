package com.example.tinyurl.service;

import com.example.tinyurl.exception.InvalidUrlException;
import com.example.tinyurl.repository.TinyUrlRepository;
import com.example.tinyurl.utils.Base62Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ShortKeyService {

    @Autowired
    TinyUrlRepository repository;

    public String generateFromId(Long id) {
        return Base62Util.encode(id);
    }

    public void ensureAliasIsUnique(String alias) {
        if (repository.existsByShortKey(alias)) {
            throw new InvalidUrlException(
                    "Custom alias already in use, please choose another one"
            );
        }
    }
}

