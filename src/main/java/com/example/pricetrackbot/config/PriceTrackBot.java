package com.example.pricetrackbot.config;

import com.example.pricetrackbot.component.Buttons;
import com.example.pricetrackbot.parsers.ParserLamoda;
import com.example.pricetrackbot.parsers.ParserOzon;
import com.example.pricetrackbot.parsers.ParserWildberries;
import com.example.pricetrackbot.service.entity.ProductMarketplace;
import com.example.pricetrackbot.service.impl.ProductImpl;
import com.example.pricetrackbot.service.repositories.ProductRepository;
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
import java.util.Map;


@Component
@Slf4j
public class PriceTrackBot extends TelegramLongPollingBot {
    private final TelegramBotConfiguration telegramBotConfiguration;
    private final ParserWildberries parserWildberries;
    private final ParserLamoda parserLamoda;
    private final ProductImpl productImpl;
    private final ProductRepository productRepository;
    private final ParserOzon parserOzon;
    private final String startText = "Отправляйте ссылки на товарыы из магазинов, " +
            "и бот будет следить за снижением цены \uD83D\uDE09 Поддерживаются:\n" +
            "www.wildberries.ru\n" +
            "www.lamoda.ru";

    @Override
    public String getBotUsername() {
        return telegramBotConfiguration.getBotName();
    }

    @Override
    public String getBotToken() {
        return telegramBotConfiguration.getToken();
    }

    public PriceTrackBot(TelegramBotConfiguration telegramBotConfiguration, ParserWildberries parserWildberries, ParserLamoda parserLamoda, ProductImpl productImpl, ProductRepository productRepository, ParserOzon parserOzon) {
        this.telegramBotConfiguration = telegramBotConfiguration;
        this.parserWildberries = parserWildberries;
        this.parserLamoda = parserLamoda;
        this.productImpl = productImpl;
        this.productRepository = productRepository;
        this.parserOzon = parserOzon;
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
            } else if (message.equals("/start")) {
                System.out.println("start");
                sendMessage(update, startText);
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

            switch (host) {
                case "www.wildberries.ru" -> sendMessage(update, parserWildberries.extractNumbers(url, update));
//                case "www.ozon.ru" -> sendMessage(update, parserOzon.parsePriceAndNameOzon(url));
                case "www.lamoda.ru" -> sendMessage(update, parserLamoda.parsePriceLamoda(url, update));
                default -> sendMessage(update, "Not found");
            }

        } catch (
                MalformedURLException e) {
        }

    }

    //Отправка сообщения в чат с данными о товаре Wildberries
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

    //Отправка сообщения в чат с данными о товаре Lamoda
    private void sendMessage(Update update, Map<String, String> hashMap) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            long chatId = update.getMessage().getChatId();

            SendMessage replyMessage = createReplyMessage(chatId,
                    "Product: " + hashMap.get("brandName") + " " + hashMap.get("modelName") + "\n" +
                            "Price: " + hashMap.get("price") + " rub." + "\n" +
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

    public void sendMessage(List<ProductMarketplace> products) {
        for (ProductMarketplace productMarketplace : products) {
            long chatId = productMarketplace.getUser().getId();

            String text = priceDecreasedSend(products);

            SendMessage replyMessage = createReplyMessage(chatId, text);
            replyMessage.enableHtml(true);

            try {
                execute(replyMessage);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    // Метод для создания сообщения о снижение цены
    private String priceDecreasedSend(List<ProductMarketplace> products) {
        StringBuilder textBuilder = new StringBuilder(" \uD83D\uDCC9  **Цена на товар снизилась**\n");
        for (ProductMarketplace product : products) {
            textBuilder.append("- ")
                    .append("<a href='").append(product.getUrl()).append("'>").append(product.getNameProduct()).append("</a>")
                    .append("\n")
                    .append("- Старая цена: ").append(product.getOldPrice() + " RUB.")
                    .append("\n")
                    .append("➡\uFE0F - Новая цена: ").append(product.getPrice() + " RUB.")
                    .append("\n")
                    .append("❌ Удалить - " + "/delete_by_id_").append(product.getId())
                    .append("\n")
                    .append("\n");
        }
        return textBuilder.toString();
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
                    .append("❌ Удалить - " + "/delete_by_id_").append(product.getId())
                    .append("\n")
                    .append("\n");
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
