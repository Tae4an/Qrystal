package com.qrystal.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@EnableCaching
@RequiredArgsConstructor
public class RedisConfig {
    @Value("${spring.redis.host}")
    private String host;

    @Value("${spring.redis.port}")
    private int port;

    @Value("${spring.redis.password}")
    private String password;

    // Redis 연결 팩토리 설정
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        // Redis 단독 서버 설정
        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration();
        configuration.setHostName(host);
        configuration.setPort(port);
        configuration.setPassword(password);

        // Lettuce 연결 팩토리 생성
        return new LettuceConnectionFactory(configuration);
    }

    // Redis 템플릿 설정
    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        // 연결 팩토리 설정
        template.setConnectionFactory(redisConnectionFactory());

        // 키/값 직렬화 설정
        template.setKeySerializer(new StringRedisSerializer());  // 키: 문자열 직렬화
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());  // 값: JSON 직렬화

        // 해시 키/값 직렬화 설정
        template.setHashKeySerializer(new StringRedisSerializer());  // 해시 키: 문자열 직렬화
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());  // 해시 값: JSON 직렬화

        return template;
    }
}