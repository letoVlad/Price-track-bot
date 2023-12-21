package com.example.pricetrackbot.parsers;

import com.example.pricetrackbot.service.impl.ProductImpl;
import com.example.pricetrackbot.service.impl.UserImpl;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

@Component
public class ParserLamoda {
    private final ProductImpl productImpl;
    private final UserImpl userImpl;

    public ParserLamoda(ProductImpl productImpl, UserImpl userImpl) {
        this.productImpl = productImpl;
        this.userImpl = userImpl;
    }

    public Map<String, String> parsePriceLamoda(String url, Update update) {
        try {
            Map<String, String> map = new HashMap<>();

            String html = sendGetRequest(url);

            Document document = Jsoup.parse(html);

            extractAndAddValue(map, document, ".x-premium-product-page__prices-info .x-premium-product-prices__price", "price");
            extractAndAddValue(map, document, ".x-premium-product-title__model-name", "modelName");
            extractAndAddValue(map, document, ".x-premium-product-title__link .x-premium-product-title__brand-name", "brandName");

            String productName = map.get("price");
            String cleanedPriceStr = productName.replaceAll("[^\\d]", "");

            userImpl.addUser(update);
            productImpl.addProductMarketplace(Integer.parseInt(cleanedPriceStr),
                    map.get("modelName") + " " + map.get("brandName"),
                    update);

            return map;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int extractPrice(String url) throws IOException {
        String html = sendGetRequest(url);
        Document document = Jsoup.parse(html);

        Elements elements = document.select(".x-premium-product-page__prices-info .x-premium-product-prices__price");
        if (!elements.isEmpty()) {
            String price = elements.size() >= 2 ? elements.get(1).text() : elements.first().text();
            String cleanedPriceStr = price.replaceAll("[^\\d]", "");
            return Integer.parseInt(cleanedPriceStr);
        }
        return 0;
    }

    private void extractAndAddValue(Map<String, String> map, Document document, String selector, String key) {
        Elements elements = document.select(selector);

        if (!elements.isEmpty()) {
            String value = elements.size() >= 2 ? elements.get(1).text() : elements.first().text();
            map.put(key, value);
        } else {
            map.put(key, "Not found");
        }
    }

    private String sendGetRequest(String url) throws IOException {
        HttpURLConnection httpClient = (HttpURLConnection) new URL(url).openConnection();

        // Настройка параметров запроса
        httpClient.setRequestMethod("GET");
        httpClient.setRequestProperty("User-Agent", "Mozilla/5.0");

        // Получение ответа
        int responseCode = httpClient.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(new InputStreamReader(httpClient.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;

            while ((line = in.readLine()) != null) {
                response.append(line);
            }
            in.close();

            return response.toString();
        } else {
            System.out.println("GET запрос неудачен. Код ответа: " + responseCode);
            return null;
        }
    }

}
