package com.example.pricetrackbot.parsers;


import com.example.pricetrackbot.service.impl.ProductImpl;
import com.example.pricetrackbot.service.impl.UserImpl;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class ParserWildberries {
    private final ProductImpl productImpl;
    private final UserImpl userImpl;

    //строка через которую получаем json с товаром
    private static final String URL = "https://card.wb.ru/cards/v1/detail?appType=1&curr=rub&dest=-2227024&spp=26&nm=";

    public ParserWildberries(ProductImpl productImpl, UserImpl userImpl) {
        this.productImpl = productImpl;
        this.userImpl = userImpl;
    }

    public JSONObject extractNumbers(String url, Update update) {
        int art = extractNumbersFromUrl(url);
        var jsonObject = parsePriceWildberries(art);
        var name = jsonObject.getString("name");
        var salePriceU = jsonObject.getInt("salePriceU") / 100;

        userImpl.addUser(update);
        productImpl.addProductMarketplace(salePriceU, name, update);
        return jsonObject;
    }

    /**
     * Парсим артикул товара с приходящего URL
     *
     * @param url ссылка на сам товар
     * @return артикул товара
     */
    public int extractNumbersFromUrl(String url) {
        Pattern pattern = Pattern.compile("\\d+");
        int art = 0;

        Matcher matcher = pattern.matcher(url);
        while (matcher.find()) {
            art = Integer.parseInt(matcher.group());
        }

        return art;
    }

    /**
     * Парсим данные из JSON товара
     *
     * @param art артикул товара
     * @return данные о товаре
     */
    public JSONObject parsePriceWildberries(int art) {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(URL + art).openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                //Построчное чтение данных и записывание в StringBuilder
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder content = new StringBuilder();

                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }

                in.close();
                //Полученная строка JSON преобразуется в объект JSONObject.
                //Затем извлекается массив products из объекта data.
                //В цикле происходит попытка вернуть первый объект из этого массива.
                String json = String.valueOf(content);

                JSONObject jsonObject = new JSONObject(json);
                JSONArray products = jsonObject.getJSONObject("data").getJSONArray("products");

                for (int i = 0; i < products.length(); i++) {
                    return products.getJSONObject(i);
                }

            } else {
                System.out.println("HTTP request failed with response code: " + responseCode);
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return null;
    }
}
