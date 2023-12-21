package com.example.pricetrackbot.config;

import lombok.Data;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@Data
@PropertySource("application.properties")
public class TelegramBotConfiguration {

    @Value("${bot.name}")
    String botName;

    @Value("${telegram.bot.token}")
    String token ;


}
