package com.example.tinyurl.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.tinyurl.entity.TinyUrl;

public interface TinyUrlRepository extends JpaRepository<TinyUrl, Long> {
    Optional<TinyUrl> findByShortKey(String shortKey);
    boolean existsByShortKey(String shortKey);
    Optional<TinyUrl> findByLongUrl(String longUrl);
}
    
