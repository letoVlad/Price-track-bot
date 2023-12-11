package com.example.pricetrackbot.alert;

import com.example.pricetrackbot.config.PriceTrackBot;
import com.example.pricetrackbot.parsers.ParserWildberries;
import com.example.pricetrackbot.service.entity.ProductMarketplace;
import com.example.pricetrackbot.service.impl.ProductImpl;
import com.example.pricetrackbot.service.repositories.ProductRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CheckPrice {
    private final ProductImpl productImpl;
    private final ProductRepository productRepository;
    private final ParserWildberries parserWildberries;
    private final PriceTrackBot priceTrackBot;

    public CheckPrice(ProductImpl productImpl, ProductRepository productRepository, ParserWildberries parserWildberries, PriceTrackBot priceTrackBot) {
        this.productImpl = productImpl;
        this.productRepository = productRepository;
        this.parserWildberries = parserWildberries;
        this.priceTrackBot = priceTrackBot;
    }

    @Scheduled(fixedRate = 10000)
    public void task() {
        List<ProductMarketplace> productMarketplaceList = new ArrayList<>();

        var productRepositoryAll = productRepository.findAll();
        for (ProductMarketplace productMarketplace : productRepositoryAll) {
            if (checkPriceMarketplace(productMarketplace) < productMarketplace.getPrice()) {
                productMarketplaceList.add(productMarketplace);
            }
        }
        priceTrackBot.sendMessage(productMarketplaceList);
    }

    private int checkPriceMarketplace(ProductMarketplace productMarketplace) {
        var url = productMarketplace.getUrl();
        var art = parserWildberries.extractNumbersFromUrl(url);
        var jsonObject = parserWildberries.parsePriceWildberries(art);
        return jsonObject.getInt("salePriceU") / 100;
    }
}
