package com.example.tinyurl.service.interfaces;

import com.example.tinyurl.dto.StatsResponse;

public interface StatsServiceInterface {
     public StatsResponse getStats(String shortKey);
}
