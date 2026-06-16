package com.sachin.url_shortener.controller;

import com.sachin.url_shortener.dto.CreateShortUrlRequest;
import com.sachin.url_shortener.dto.CreateShortUrlResponse;
import com.sachin.url_shortener.service.UrlShortenerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequiredArgsConstructor
public class UrlShortenerController {

    private final UrlShortenerService urlShortenerService;

    @PostMapping("/api/v1/urls")
    public CreateShortUrlResponse createShortUrl(
            @RequestBody CreateShortUrlRequest request) {

        String shortCode =
                urlShortenerService.createShortUrl(request.longUrl());

        return new CreateShortUrlResponse(shortCode);
    }

    @GetMapping("/{shortCode}")
    public ResponseEntity<Void> redirect(
            @PathVariable String shortCode) {

        String longUrl =
                urlShortenerService.getLongUrl(shortCode);

        return ResponseEntity
                .status(HttpStatus.FOUND)
                .location(URI.create(longUrl))
                .build();
    }
}