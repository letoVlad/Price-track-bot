package com.example.pricetrackbot.service.impl;

import com.example.pricetrackbot.service.entity.ProductMarketplace;
import com.example.pricetrackbot.service.entity.Users;
import com.example.pricetrackbot.service.repositories.ProductRepository;
import com.example.pricetrackbot.service.repositories.UserRepository;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.ws.rs.NotFoundException;
import java.util.List;

@Service
public class ProductImpl {
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public ProductImpl(ProductRepository productRepository, UserRepository userRepository) {
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    public void addProductMarketplace(int price, String nameProduct, Update update) {
        ProductMarketplace productMarketplace = new ProductMarketplace();
        productMarketplace.setNameProduct(nameProduct);
        productMarketplace.setPrice(price);
        productMarketplace.setUrl(update.getMessage().getText());
        productMarketplace.setUser(checkForUser(update.getMessage().getChatId()));
        productRepository.saveAndFlush(productMarketplace);
    }

    public String deleteById(Long deleteId) {
        if (productRepository.findById(deleteId).isPresent()) {
            productRepository.deleteById(deleteId);
            return "Товар удален";
        } else {
            return "Ошибка";
        }
    }

    public List<ProductMarketplace> productMarketplaceList(Long userId) {
        Users user = checkForUser(userId);
        return user.getProductEntities();
    }

    public void updatePriceProduct(Long productId, Integer newPrice) {
        ProductMarketplace productMarketplaceUpdatePrice = checkForProduct(productId);
        productMarketplaceUpdatePrice.setPrice(newPrice);
        productRepository.saveAndFlush(productMarketplaceUpdatePrice);
    }

    private Users checkForUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("User c индексом \"%s\" не найден.", userId)
                ));
    }

    private ProductMarketplace checkForProduct(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("User c индексом \"%s\" не найден.", productId)
                ));
    }
}
