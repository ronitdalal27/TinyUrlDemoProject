package com.example.tinyurl.dto;

import lombok.Data;

@Data
public class ShortenUrlRequest {
    private String longUrl;
    private String customAlias;
}
