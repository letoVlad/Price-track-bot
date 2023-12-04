package com.example.pricetrackbot.config;

import com.example.pricetrackbot.component.Buttons;
import com.example.pricetrackbot.parsers.ParserWildberries;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.net.MalformedURLException;
import java.net.URL;


@Component
@Slf4j
public class PriceTrackBot extends TelegramLongPollingBot {
    private final String SELECTION = " Выберите маркетплейс для мониторинга цены на товар.";
    private final TelegramBotConfiguration telegramBotConfiguration;
    private final ParserWildberries parserWildberries;

    @Override
    public String getBotUsername() {
        return telegramBotConfiguration.getBotName();
    }

    @Override
    public String getBotToken() {
        return telegramBotConfiguration.getToken();
    }


    public PriceTrackBot(TelegramBotConfiguration telegramBotConfiguration, ParserWildberries parserWildberries) {
        this.telegramBotConfiguration = telegramBotConfiguration;
        this.parserWildberries = parserWildberries;
        Buttons.registerCommands(this);
    }


    @Override
    public void onUpdateReceived(Update update) {

        //если получено сообщение текстом
        if (update.hasMessage()) {
            long chatId = update.getMessage().getChatId();
            String message = update.getMessage().getText();

            if (update.getMessage().hasText()) {
                checkShop(message, update);

            }
        }


    }

    private void checkShop(String url, Update update) {
        try {
            URL parseUrl;
            parseUrl = new URL(url);

            String host = parseUrl.getHost();

            if (host.equals("www.wildberries.ru")) {
                ParserWildberries.extractNumbers(url);
            } else {
                sendMessage(update);
            }

        } catch (MalformedURLException e) {
            sendMessage(update);
        }

    }


    private void sendMessage(Update update) {

        if (update.hasMessage() && update.getMessage().hasText()) {
            long chatId = update.getMessage().getChatId();
            String messageText = update.getMessage().getText();

            // Отправляем ответное сообщение с цитатой
            SendMessage replyMessage = new SendMessage();
            replyMessage.setChatId(chatId);
            replyMessage.setText("Ваш ответ: " + messageText);
            replyMessage.setReplyToMessageId(update.getMessage().getMessageId());

            try {
                execute(replyMessage);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }

    }

//    private void wildberriesSelection(int messageId, long chatId) {
//        String messageText = "Вы выбрали";
////        InlineKeyboardMarkup catsButtons = buttons.secondLayerButtons();
////        changeMessage(messageId, chatId, messageText);
//        SendMessage message = new SendMessage();
//        message.setText(messageText);
//        message.setChatId(chatId);
//
//        try {
//            execute(message);
//        } catch (TelegramApiException e) {
//            throw new RuntimeException(e);
//        }
//    }

//    /**
//     * метод для создания/изменения сообщения
//     */
//    private void changeMessage(int messageId, long chatIdInButton, String messageText) {
//        EditMessageText editMessageText = new EditMessageText();
//        editMessageText.setChatId(String.valueOf(chatIdInButton));
//        editMessageText.setText(messageText);
//        editMessageText.setMessageId(messageId);
////        editMessageText.setReplyMarkup(keyboardMarkup);
//        try {
//            execute(editMessageText);
//        } catch (TelegramApiException e) {
//            throw new RuntimeException(e);
//        }
//    }


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
