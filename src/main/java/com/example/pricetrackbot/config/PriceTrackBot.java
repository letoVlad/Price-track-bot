package com.example.pricetrackbot.config;

import com.example.pricetrackbot.component.BotCommands;
import com.example.pricetrackbot.component.Buttons;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
@Slf4j
public class PriceTrackBot extends TelegramLongPollingBot implements BotCommands {
    private final String SELECTION = "Выберите маркетплейс для мониторинга цены на товар.";
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

        //если получено сообщение текстом
        if (update.hasMessage()) {
            long chatId = update.getMessage().getChatId();
            long userId = update.getMessage().getFrom().getId();

            //если у пользователя скрыт UserName то берем getFirstName
            if (update.getMessage().getFrom().getUserName() == null) {
                userName = update.getMessage().getFrom().getFirstName();
            } else {
                userName = update.getMessage().getFrom().getUserName();
            }

            if (update.getMessage().hasText()) {
                receivedMessage = update.getMessage().getText();
                botAnswerUtils(receivedMessage, chatId, userName);
                startSelection(chatId, update);
            } else if (update.hasCallbackQuery()) {
                chatId = update.getCallbackQuery().getMessage().getChatId();
                userId = update.getCallbackQuery().getFrom().getId();
                userName = update.getCallbackQuery().getFrom().getUserName();
                receivedMessage = update.getCallbackQuery().getData();
                botAnswerUtils(receivedMessage, chatId, userName);
            }
        }

    }

    private void startSelection(long chatId, Update update) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setReplyMarkup(new Buttons().selectionAnimalButtons());
        sendMessage.setText("Привет! " + update.getMessage().getFrom().getFirstName() + SELECTION);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private void botAnswerUtils(String receivedMessage, long chatId, String userName) {
        switch (receivedMessage) {
            case "/start" -> startBot(chatId, userName);
            case "/help" -> sendHelpText(chatId);
//            case "кто больше всех говорит за день!?" -> ListPeopleMostPosts(chatId);
//            case "кто больше всех говорит за неделю!?" -> ListPeopleMostPostsWeek(chatId);
            default -> {
            }
        }
    }

    private void sendHelpText(long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(BotCommands.HELP_TEXT);

        try {
            execute(message);
            log.info("Reply sent");
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }

    private void startBot(long chatId, String userName) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Привет " + userName);

        try {
            execute(message);
            log.info("Reply sent");
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }
}
