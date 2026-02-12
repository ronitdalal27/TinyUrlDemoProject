package com.example.tinyurl.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.tinyurl.entity.TinyUrl;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TinyUrlRepository extends JpaRepository<TinyUrl, Long> {
    Optional<TinyUrl> findByShortKey(String shortKey);
    boolean existsByShortKey(String shortKey);
    Optional<TinyUrl> findByLongUrl(String longUrl);
    @Modifying
    @Query("UPDATE TinyUrl t SET t.clickCount = t.clickCount + 1 WHERE t.shortKey = :shortKey")
    int incrementClickCount(@Param("shortKey") String shortKey);

}
    
