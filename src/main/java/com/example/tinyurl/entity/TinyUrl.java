package com.example.tinyurl.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
    name = "tiny_url",
    indexes = {
        @Index(name = "idx_short_key", columnList = "short_key")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TinyUrl {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @Column(name = "long_url", nullable = false, length = 2048)
    private String longUrl;

    // Must be nullable during first insert
    @Column(name = "short_key", length = 7, unique = true)
    private String shortKey;

    @Column(name = "click_count", nullable = false)
    private Long clickCount;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.clickCount = 0L;
    }
}
