package com.example.pricetrackbot.alert;

import com.example.pricetrackbot.parsers.ParserWildberries;
import com.example.pricetrackbot.service.entity.ProductMarketplace;
import com.example.pricetrackbot.service.impl.ProductImpl;
import com.example.pricetrackbot.service.repositories.ProductRepository;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CheckPrice {
    private final ProductImpl productImpl;
    private final ProductRepository productRepository;
    private final ParserWildberries parserWildberries;

    public CheckPrice(ProductImpl productImpl, ProductRepository productRepository, ParserWildberries parserWildberries) {
        this.productImpl = productImpl;
        this.productRepository = productRepository;
        this.parserWildberries = parserWildberries;
    }

    public List<ProductMarketplace> task() {
        List<ProductMarketplace> productMarketplaceList = new ArrayList<>();
        var productRepositoryAll = productRepository.findAll();
        for (ProductMarketplace productMarketplace : productRepositoryAll) {
            if (checkPriceMarketplace(productMarketplace) > productMarketplace.getPrice()) {
                productMarketplaceList.add(productMarketplace);
            }

        }

    }

    private int checkPriceMarketplace(ProductMarketplace productMarketplace) {
        var url = productMarketplace.getUrl();
        var art = parserWildberries.extractNumbersFromUrl(url);
        var jsonObject = parserWildberries.parsePriceWildberries(art);
        return jsonObject.getInt("salePriceU") / 100;
    }
}
