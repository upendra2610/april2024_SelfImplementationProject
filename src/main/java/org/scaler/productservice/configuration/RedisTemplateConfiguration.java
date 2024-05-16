package org.scaler.productservice.configuration;


import org.scaler.productservice.dtos.FakeStoreProductDto;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

@Configuration
public class RedisTemplateConfiguration {

    @Bean
    public RedisTemplate<Long, FakeStoreProductDto> redisTemplate(RedisConnectionFactory redisConnectionFactory){
        RedisTemplate<Long, FakeStoreProductDto> redisTemplate =new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);

        return redisTemplate;
    }
}


