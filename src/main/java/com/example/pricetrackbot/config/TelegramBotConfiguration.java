package com.example.pricetrackbot.config;

import lombok.Data;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@Data
@PropertySource("application.properties")
public class TelegramBotConfiguration {

    //    @Value("${bot.name}")
    String botName = "PriceAlert_Track_Bot";

    //    @Value("${telegram.bot.token}")
    String token = "6830946685:AAGHvTBMGrHD5wG32NTzR4n7-MEWEq8BuVA";


}
