package com.example.pricetrackbot.config;

import com.example.pricetrackbot.component.Buttons;
import com.example.pricetrackbot.parsers.ParserWildberries;
import com.example.pricetrackbot.service.entity.ProductMarketplace;
import com.example.pricetrackbot.service.impl.ProductImpl;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;


@Component
@Slf4j
public class PriceTrackBot extends TelegramLongPollingBot {
    private final TelegramBotConfiguration telegramBotConfiguration;
    private final ParserWildberries parserWildberries;
    private final ProductImpl productImpl;

    @Override
    public String getBotUsername() {
        return telegramBotConfiguration.getBotName();
    }

    @Override
    public String getBotToken() {
        return telegramBotConfiguration.getToken();
    }

    public PriceTrackBot(TelegramBotConfiguration telegramBotConfiguration, ParserWildberries parserWildberries, ProductImpl productImpl) {
        this.telegramBotConfiguration = telegramBotConfiguration;
        this.parserWildberries = parserWildberries;
        this.productImpl = productImpl;
        Buttons.registerCommands(this);
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String message = update.getMessage().getText();

            if (message.equals("/list")) {
                handleListCommand(update);
            } else if (message.startsWith("/delete_by_id_")) {
                handleDeleteCommand(message, update);
            } else {
                checkShop(message, update);
            }
        }
    }

    private void handleListCommand(Update update) {
        var productMarketplaces = productImpl.productMarketplaceList(update.getMessage().getChatId());
        sendMessage(update, productMarketplaces);
    }

    private void handleDeleteCommand(String message, Update update) {
        String idString = message.substring("/delete_by_id_".length());
        try {
            int deleteID = Integer.parseInt(idString);
            var string = productImpl.deleteById((long) deleteID);
            sendMessage(update, string);
        } catch (NumberFormatException e) {
            sendMessage(update, "Неверный формат id: " + idString);
        }
    }

    //Чек на шоп
    private void checkShop(String url, Update update) {
        try {
            URL parseUrl = new URL(url);
            String host = parseUrl.getHost();

            if (host.equals("www.wildberries.ru")) {
                sendMessage(update, parserWildberries.extractNumbers(url, update));
            } else {
                sendMessage(update, "Not found");
            }

        } catch (MalformedURLException e) {

        }
    }

    //Отправка сообщения в чат с данными о товаре
    private void sendMessage(Update update, JSONObject jsonObject) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            long chatId = update.getMessage().getChatId();

            SendMessage replyMessage = createReplyMessage(chatId,
                    "Product: " + jsonObject.getString("name") + "\n" +
                            "Price: " + jsonObject.getInt("salePriceU") / 100 + " rub." + "\n" +
                            "Added to tracking!");

            try {
                execute(replyMessage);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    private void sendMessage(Update update, String text) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            long chatId = update.getMessage().getChatId();

            SendMessage replyMessage = createReplyMessage(chatId, text);

            try {
                execute(replyMessage);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    private void sendMessage(Update update, List<ProductMarketplace> products) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            long chatId = update.getMessage().getChatId();
            String text = createProductListText(products);

            SendMessage replyMessage = createReplyMessage(chatId, text);
            replyMessage.enableHtml(true);

            try {
                execute(replyMessage);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    // Метод для создания текста сообщения на основе списка товаров
    private String createProductListText(List<ProductMarketplace> products) {
        StringBuilder textBuilder = new StringBuilder("Список отслеживаемых товаров:\n");

        for (ProductMarketplace product : products) {
            textBuilder.append("- ")
                    .append("<a href='").append(product.getUrl()).append("'>").append(product.getNameProduct()).append("</a>")
                    .append("\n")
                    .append("- Цена: ").append(product.getPrice() + " RUB.")
                    .append("\n")
                    .append("Удалить - " + "/delete_by_id_").append(product.getId())
                    .append("\n")
                    .append("\n");
            System.out.println(product.getId());
        }
        return textBuilder.toString();
    }

    //Ответ на сообщения из чата
    private SendMessage createReplyMessage(long chatId, String text) {
        SendMessage replyMessage = new SendMessage();
        replyMessage.setChatId(chatId);
        replyMessage.setText(text);
        return replyMessage;
    }
}
