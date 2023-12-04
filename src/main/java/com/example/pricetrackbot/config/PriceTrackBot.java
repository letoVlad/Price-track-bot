package com.example.pricetrackbot.config;

import com.example.pricetrackbot.component.BotCommands;
import com.example.pricetrackbot.component.Buttons;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;


@Component
@Slf4j
public class PriceTrackBot extends TelegramLongPollingBot implements BotCommands {
    private final String SELECTION = " Выберите маркетплейс для мониторинга цены на товар.";
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

        //если получено сообщение текстом
        if (update.hasMessage()) {
            long chatId = update.getMessage().getChatId();

            if (update.getMessage().hasText()) {
                startSelection(chatId, update);

            }
        }
        if (update.hasCallbackQuery()) {
            long chatId = update.getCallbackQuery().getMessage().getChatId();

            String callbackData = update.getCallbackQuery().getData();
            int messageId = update.getCallbackQuery().getMessage().getMessageId();

            switch (callbackData) {
                case "Wildberries" -> wildberriesSelection(messageId, chatId);
            }
        }

    }

    private void wildberriesSelection(int messageId, long chatId) {
        String messageText = "Вы выбрали приют для кошек";
//        InlineKeyboardMarkup catsButtons = buttons.secondLayerButtons();
//        changeMessage(messageId, chatId, messageText);
        SendMessage message = new SendMessage();
        message.setText(messageText);
        message.setChatId(chatId);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * метод для создания/изменения сообщения
     */
    private void changeMessage(int messageId, long chatIdInButton, String messageText) {
        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setChatId(String.valueOf(chatIdInButton));
        editMessageText.setText(messageText);
        editMessageText.setMessageId(messageId);
//        editMessageText.setReplyMarkup(keyboardMarkup);
        try {
            execute(editMessageText);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private void startSelection(long chatId, Update update) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setReplyMarkup(new Buttons().selectionAnimalButtons());
        sendMessage.setText(SELECTION);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }


//    private void botAnswerUtils(String receivedMessage, long chatId, String userName) {
//        switch (receivedMessage) {
//            case "/start" -> startBot(chatId, userName);
//            case "/help" -> sendHelpText(chatId);
////            case "кто больше всех говорит за день!?" -> ListPeopleMostPosts(chatId);
////            case "кто больше всех говорит за неделю!?" -> ListPeopleMostPostsWeek(chatId);
//            default -> {
//            }
//        }
//    }
//
//    private void sendHelpText(long chatId) {
//        SendMessage message = new SendMessage();
//        message.setChatId(chatId);
//        message.setText(BotCommands.HELP_TEXT);
//
//        try {
//            execute(message);
//            log.info("Reply sent");
//        } catch (TelegramApiException e) {
//            log.error(e.getMessage());
//        }
//    }
//
//    private void startBot(long chatId, String userName) {
//        SendMessage message = new SendMessage();
//        message.setChatId(chatId);
//        message.setText("Привет " + userName);
//
//        try {
//            execute(message);
//            log.info("Reply sent");
//        } catch (TelegramApiException e) {
//            log.error(e.getMessage());
//        }
//    }
}
