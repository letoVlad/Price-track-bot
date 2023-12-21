package com.example.pricetrackbot.parsers;

import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.stereotype.Component;

@Component
public class ParserOzon {

    public JSONObject parsePriceAndNameOzon(String url) {
        JSONObject jsonObject = new JSONObject();

        // Установка пути к драйверу браузера (в данном случае, Chrome)
        System.setProperty("webdriver.chrome.driver", "C:\\chromoDriver120\\chromedriver.exe");


        // Создание экземпляра веб-драйвера
        WebDriver driver = new ChromeDriver();

        // Открытие веб-страницы
        driver.get(url);

        // Нахождение элемента по классу
        WebElement priceProduct = driver.findElement(By.className("ln0"));
        WebElement nameProduct = driver.findElement(By.className("lo"));


        jsonObject.append("name", nameProduct);
        jsonObject.append("salePriceU", priceProduct);

        // Закрытие браузера
        driver.quit();
        return jsonObject;
    }
}
