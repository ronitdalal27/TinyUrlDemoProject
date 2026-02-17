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

    @Column(name = "long_url", nullable = false, length = 768, unique = true)
    private String longUrl;

    // Must be nullable during first insert
    @Column(name = "short_key", length = 20, unique = true)
    private String shortKey;

    @Column(name = "click_count", nullable = false)
    private Long clickCount;

    @Version                    //added this for optimistic locking, we can use this to prevent race conditions means when multiple users try to update the same record at the same time, it will throw an exception
    @Column(nullable = false)
    private Long version;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.clickCount = 0L;
        this.expiresAt = this.createdAt.plusHours(24); // setting default expiration time of short URL to 24 hours after creation
    }
}
