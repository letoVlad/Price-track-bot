package com.example.pricetrackbot.parsers;


import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
@Component
public class parserWildberries {
    private final String URL = "https://card.wb.ru/cards/v1/detail?appType=1&curr=rub&dest=-2227024&spp=26&nm=";

    public void parsePriceWildberries(int art) {

        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(URL + art).openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder content = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }
                in.close();
                String json = String.valueOf(content);

                JSONObject jsonObject = new JSONObject(json);
                JSONArray products = jsonObject.getJSONObject("data").getJSONArray("products");

                for (int i = 0; i < products.length(); i++) {
                    JSONObject product = products.getJSONObject(i);

                    String name = product.getString("name");
                    int salePriceU = product.getInt("salePriceU");

                    // Вывод данных
                    System.out.println("Name: " + name);
                    System.out.println("Sale Price: " + salePriceU / 100);
                }

            } else {
                System.out.println("HTTP request failed with response code: " + responseCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
