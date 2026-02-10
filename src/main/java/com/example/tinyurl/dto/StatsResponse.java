package com.example.tinyurl.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StatsResponse {
    private String shortUrl;
    private String longUrl;
    private Long clickCount;
    private LocalDateTime createdAt;
}
