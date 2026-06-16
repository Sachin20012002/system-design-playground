package com.sachin.url_shortener;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;

@SpringBootApplication
public class UrlShortenerApplication {

	public static void main(String[] args) {
		System.out.println("Timezone = " + TimeZone.getDefault().getID());
		SpringApplication.run(UrlShortenerApplication.class, args);
	}

}
