package com.example.pricetrackbot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PriceTrackBotApplication {

    public static void main(String[] args) {
        SpringApplication.run(PriceTrackBotApplication.class, args);
    }

}
