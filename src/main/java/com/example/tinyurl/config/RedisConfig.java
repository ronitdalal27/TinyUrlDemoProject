package com.example.tinyurl.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, String> redisTemplate( RedisConnectionFactory connectionFactory) {

        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // Keys and values as plain strings
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());

        return template;
    }
}

/*
    information about redis configuration class :-
    redisTemplate is bean name and RedisTemplate<String, String> is the return type of this bean 
    here we are returning RedisTemplate bean which has <String,String> as key(shorturl) and value(longurl)
    then we are creating template as object of RedisTemplate and then using that object we are setting connection factory using RedisConnectionFactory object, with this line we are connecting our RedisTemplate to Redis server
    then we are setting key and value serializer as StringRedisSerializer because we want to store both key and value as plain strings in redis becuase redis stores in bytes and we want to convert it to string and then
    finally we are returning the template object which will be used in our service class to interact with Redis
 */