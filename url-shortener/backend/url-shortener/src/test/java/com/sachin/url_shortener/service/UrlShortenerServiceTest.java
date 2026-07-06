package com.sachin.url_shortener.service;

import com.sachin.url_shortener.repository.UrlMappingRepository;
import io.micrometer.core.instrument.Counter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.types.Expiration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UrlShortenerServiceTest {

    @Mock
    private UrlMappingRepository urlMappingRepository;
    @Mock
    private StringRedisTemplate stringRedisTemplate;
    @Mock
    private ValueOperations<String, String> valueOperations;
    @Mock
    private Counter cacheHitCounter;
    @Mock
    private Counter cacheMissCounter;
    @Mock
    private Counter cachePutCounter;

    private UrlShortenerService urlShortenerService;

    @BeforeEach
    void setUp() {

        when(stringRedisTemplate.opsForValue())
                .thenReturn(valueOperations);

        urlShortenerService = new UrlShortenerService(
                urlMappingRepository,
                stringRedisTemplate,
                cacheHitCounter,
                cacheMissCounter,
                cachePutCounter);
    }

    @Test
    void shouldReturnCachedUrlWhenPresentInRedis() {

        // Arrange
        String shortCode = "ABC";
        String longUrl = "https://google.com";

        when(valueOperations.get(shortCode))
                .thenReturn(longUrl);

        // Act
        String result = urlShortenerService.getLongUrl(shortCode);

        // Assert
        assertEquals(longUrl, result);

        verify(cacheHitCounter).increment();

        verify(cacheMissCounter, never()).increment();

        verify(urlMappingRepository, never())
                .findByShortCode(anyString());

        verify(valueOperations, never()).set(anyString(), anyString(), (Expiration) any());
    }

}
