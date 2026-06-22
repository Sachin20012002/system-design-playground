package com.sachin.url_shortener.service;

import com.sachin.url_shortener.entity.UrlMapping;
import com.sachin.url_shortener.exception.ShortCodeNotFoundException;
import com.sachin.url_shortener.repository.UrlMappingRepository;
import io.micrometer.core.instrument.Counter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class UrlShortenerService {

    private static final String BASE62 =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    private static final Duration CACHE_TTL =
            Duration.ofHours(24);

    private final UrlMappingRepository urlMappingRepository;
    private final StringRedisTemplate stringRedisTemplate;

    private final Counter cacheHitCounter;
    private final Counter cacheMissCounter;
    private final Counter cachePutCounter;

    public String createShortUrl(String longUrl) {

        Long nextId = stringRedisTemplate.opsForValue()
                .increment("global:url:id");

        if (nextId == null) {
            throw new RuntimeException("Unable to generate unique ID");
        }

        String shortCode = encodeBase62(nextId);

        UrlMapping mapping = new UrlMapping();
        mapping.setLongUrl(longUrl);
        mapping.setShortCode(shortCode);

        urlMappingRepository.save(mapping);

        return shortCode;
    }

    public String getLongUrl(String shortCode) {

        String cachedUrl =
                stringRedisTemplate.opsForValue()
                        .get(shortCode);

        if (cachedUrl != null) {
            cacheHitCounter.increment();
            return cachedUrl;
        }

        cacheMissCounter.increment();

        String longUrl =
                urlMappingRepository.findByShortCode(shortCode)
                        .orElseThrow(() ->
                                new ShortCodeNotFoundException(shortCode))
                        .getLongUrl();

        stringRedisTemplate.opsForValue()
                .set(shortCode, longUrl, CACHE_TTL);

        cachePutCounter.increment();

        return longUrl;
    }

    private String encodeBase62(long value) {

        if (value == 0) {
            return "A";
        }

        StringBuilder sb = new StringBuilder();

        while (value > 0) {

            sb.append(
                    BASE62.charAt(
                            (int) (value % BASE62.length())
                    )
            );

            value /= BASE62.length();
        }

        return sb.reverse().toString();
    }
}