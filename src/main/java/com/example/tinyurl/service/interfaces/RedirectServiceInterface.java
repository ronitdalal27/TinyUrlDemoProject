package com.example.tinyurl.service.interfaces;

import com.example.tinyurl.entity.TinyUrl;

public interface RedirectServiceInterface {
    public TinyUrl incrementClickAndGet(String shortKey);
}
