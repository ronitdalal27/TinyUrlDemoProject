package com.example.tinyurl.service.interfaces;

import com.example.tinyurl.dto.StatsResponse;
import com.example.tinyurl.entity.TinyUrl;

public interface TinyUrlServiceInterface {
    public String createShortUrl(String longUrl, String customAlias);
    public TinyUrl redirect(String shortKey);
    public  StatsResponse getStats(String shortKey);
}
