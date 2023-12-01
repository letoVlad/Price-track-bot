package com.example.pricetrackbot.config;

import lombok.Data;
import lombok.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class TelegramBotConfiguration {

    @Value("${telegram.bot.name}")
    String botName;

    @Value("${telegram.bot.token}")
    String token;


}
