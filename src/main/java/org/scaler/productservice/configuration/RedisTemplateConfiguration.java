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
//NOTES(Regarding redis cache)
//1. RedisTemplate<String, Object> is storing data in cache in String as Key and Object -
//    as value manner;

//2. RedisConnectionFactory contains details around how to connect to redis instance -
//    (like all url etc. inside it)

//3. at the actual value of url and port we need to provide that value put inside -
//    properties file(check tha file)

//4. all the things regarding how connection needs to happen or when need to happen -
//    all taking care by my spring data and has inbuilt support fo this.

//5. When we deploy, aws provides "ElasticCache" for redis like RDS.

//6. when we save inside redis then this call happen in network and redis not understand objects -
//   so it serialize(converting into set of bytes) and send data over network and when we get -
//   data from redis then it deserialize into object and for this we need to implement Serializable -
//   interface to allow for serializing in particular class.

