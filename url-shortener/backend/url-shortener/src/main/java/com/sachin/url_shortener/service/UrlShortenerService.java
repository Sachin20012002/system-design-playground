package com.sachin.url_shortener.service;

import com.sachin.url_shortener.entity.UrlMapping;
import com.sachin.url_shortener.exception.ShortCodeNotFoundException;
import com.sachin.url_shortener.repository.UrlMappingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
@RequiredArgsConstructor
public class UrlShortenerService {

    private static final String BASE62 =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    private static final int SHORT_CODE_LENGTH = 6;
    private static final int MAX_RETRIES = 5;

    private final UrlMappingRepository urlMappingRepository;

    private final SecureRandom secureRandom = new SecureRandom();

    public String createShortUrl(String longUrl) {

        for (int attempt = 0; attempt < MAX_RETRIES; attempt++) {

            String shortCode = generateShortCode();

            if (urlMappingRepository.existsByShortCode(shortCode)) {
                continue;
            }

            UrlMapping mapping = new UrlMapping();
            mapping.setLongUrl(longUrl);
            mapping.setShortCode(shortCode);

            urlMappingRepository.save(mapping);

            return shortCode;
        }

        throw new RuntimeException(
                "Unable to generate a unique short code after "
                        + MAX_RETRIES + " attempts");
    }

    public String getLongUrl(String shortCode) {

        return urlMappingRepository.findByShortCode(shortCode)
                .orElseThrow(() ->
                        new ShortCodeNotFoundException(shortCode))
                .getLongUrl();
    }

    private String generateShortCode() {

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < SHORT_CODE_LENGTH; i++) {
            sb.append(
                    BASE62.charAt(
                            secureRandom.nextInt(BASE62.length())
                    )
            );
        }

        return sb.toString();
    }
}