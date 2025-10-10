package com.qrystal.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@EnableCaching
@RequiredArgsConstructor
public class RedisConfig {
    @Value("${spring.redis.host:}")
    private String host;

    @Value("${spring.redis.port:6379}")
    private int port;

    @Value("${spring.redis.password:}")
    private String password;

    // Redis 연결 팩토리 설정 (조건부 - Redis 호스트가 설정된 경우에만)
    @Bean
    @ConditionalOnExpression("'${spring.redis.host:}'.length() > 0")
    public RedisConnectionFactory redisConnectionFactory() {
        // Redis 단독 서버 설정
        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration();
        configuration.setHostName(host);
        configuration.setPort(port);
        if (!password.isEmpty()) {
            configuration.setPassword(password);
        }

        // Lettuce 연결 팩토리 생성
        return new LettuceConnectionFactory(configuration);
    }

    @Bean
    @ConditionalOnExpression("'${spring.redis.host:}'.length() > 0")
    public RedisTemplate<String, Object> redisTemplate() {
        // ObjectMapper 설정: JSON 직렬화/역직렬화를 위한 객체
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());  // Java 8 날짜/시간 타입 지원
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);  // 날짜를 타임스탬프가 아닌 ISO-8601 형식으로 직렬화

        // RedisTemplate 객체 생성
        RedisTemplate<String, Object> template = new RedisTemplate<>();

        // Redis 연결 팩토리 설정
        template.setConnectionFactory(redisConnectionFactory());

        // JSON Serializer 설정
        Jackson2JsonRedisSerializer<Object> serializer = new Jackson2JsonRedisSerializer<>(Object.class);
        serializer.setObjectMapper(objectMapper);  // 위에서 설정한 ObjectMapper 사용

        // 키/값 직렬화 설정
        template.setKeySerializer(new StringRedisSerializer());  // 키: 문자열로 직렬화
        template.setValueSerializer(serializer);  // 값: JSON으로 직렬화

        // 해시 키/값 직렬화 설정
        template.setHashKeySerializer(new StringRedisSerializer());  // 해시 키: 문자열로 직렬화
        template.setHashValueSerializer(serializer);  // 해시 값: JSON으로 직렬화

        return template;  // 설정이 완료된 RedisTemplate 반환
    }

}