package com.example.tinyurl.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tiny_url")
public class TinyUrl {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; //this will be auto generated primary key

    @Column(name = "long_url", nullable = false ,length = 2048)
    private String longUrl; //this is our original long url

    @Column(name = "short_key", nullable = false, length = 7, unique = true)
    private String shortKey; //this will be our short url for original long url

    @Column(name = "click_count", nullable = false)
    private int clickCount; //this will keep track of number of times short url is clicked

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt; //this will keep track of when the short url was created

    @PrePersist
    public void onCreate() { //method to set createdAt and clickCount before persisting, means before saving data into database
        this.createdAt = LocalDateTime.now();    
        this.clickCount = 0;
    }
    
}
