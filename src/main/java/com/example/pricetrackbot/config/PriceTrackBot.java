package com.example.pricetrackbot.config;

import com.example.pricetrackbot.component.BotCommands;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class PriceTrackBot extends TelegramLongPollingBot implements BotCommands {
    private final TelegramBotConfiguration telegramBotConfiguration;

    public PriceTrackBot(TelegramBotConfiguration telegramBotConfiguration) {
        this.telegramBotConfiguration = telegramBotConfiguration;
    }


    @Override
    public String getBotUsername() {
        return telegramBotConfiguration.getBotName();
    }

    @Override
    public String getBotToken() {
        return telegramBotConfiguration.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        String userName = null;
        String receivedMessage;

        if (update.hasMessage()) {
            long chatId = update.getMessage().getChatId();
            long userId = update.getMessage().getFrom().getId();
        }
    }
}
