package com.example.pricetrackbot.alert;

import com.example.pricetrackbot.config.PriceTrackBot;
import com.example.pricetrackbot.parsers.ParserLamoda;
import com.example.pricetrackbot.parsers.ParserWildberries;
import com.example.pricetrackbot.service.entity.ProductMarketplace;
import com.example.pricetrackbot.service.impl.ProductImpl;
import com.example.pricetrackbot.service.repositories.ProductRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Component
public class CheckPrice {
    private final ProductImpl productImpl;
    private final ProductRepository productRepository;
    private final ParserWildberries parserWildberries;
    private final ParserLamoda parserLamoda;
    private final PriceTrackBot priceTrackBot;

    public CheckPrice(ProductImpl productImpl, ProductRepository productRepository, ParserWildberries parserWildberries, ParserLamoda parserLamoda, PriceTrackBot priceTrackBot) {
        this.productImpl = productImpl;
        this.productRepository = productRepository;
        this.parserWildberries = parserWildberries;
        this.parserLamoda = parserLamoda;
        this.priceTrackBot = priceTrackBot;
    }

    @Scheduled(fixedRate = 10800000)
//    @Scheduled(fixedRate = 10000)
    public void task() throws MalformedURLException {
        List<ProductMarketplace> productMarketplaceList = new ArrayList<>();
        List<ProductMarketplace> productRepositoryAll = productRepository.findAll();

        for (ProductMarketplace productMarketplace : productRepositoryAll) {
            URL parseUrl = new URL(productMarketplace.getUrl());
            String host = parseUrl.getHost();

            if ("www.wildberries.ru".equals(host)) {
                updatePriceAndAddToList(productMarketplace, this::checkPriceWildberries, productMarketplaceList);
            } else if ("www.lamoda.ru".equals(host)) {
                updatePriceAndAddToList(productMarketplace, this::checkPriceLamoda, productMarketplaceList);
            }
        }
        priceTrackBot.sendMessage(productMarketplaceList);
    }

    private void updatePriceAndAddToList(ProductMarketplace productMarketplace, Function<ProductMarketplace, Integer> priceChecker, List<ProductMarketplace> resultList) {
        if (priceChecker.apply(productMarketplace) < productMarketplace.getPrice()) {
            Optional<ProductMarketplace> productMarketplaceOptional = productRepository.findById(productMarketplace.getId());

            productMarketplaceOptional.ifPresentOrElse(
                    oldProduct -> {
                        Integer oldPrice = oldProduct.getPrice();
                        oldProduct.setPrice(priceChecker.apply(productMarketplace));
                        oldProduct.setOldPrice(oldPrice);

                        productRepository.saveAndFlush(oldProduct);
                        resultList.add(oldProduct);
                    },
                    () -> resultList.add(productMarketplace)
            );
        }
    }

    private int checkPriceWildberries(ProductMarketplace productMarketplace) {
        var url = productMarketplace.getUrl();
        var art = parserWildberries.extractNumbersFromUrl(url);
        var jsonObject = parserWildberries.parsePriceWildberries(art);
        return jsonObject.getInt("salePriceU") / 100;
    }

    private int checkPriceLamoda(ProductMarketplace productMarketplace) {
        try {
            var price = parserLamoda.extractPrice(productMarketplace.getUrl());
            return price;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
