package com.example.pricetrackbot.service.impl;

import com.example.pricetrackbot.service.entity.ProductMarketplace;
import com.example.pricetrackbot.service.entity.Users;
import com.example.pricetrackbot.service.repositories.ProductRepository;
import com.example.pricetrackbot.service.repositories.UserRepository;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.ws.rs.NotFoundException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

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

    public String deleteById(Long deleteId, Update update) {
        Long chatId = update.getMessage().getChatId();

        Optional<ProductMarketplace> productOptional = productRepository.findById(deleteId);

        if (productOptional.isPresent()) {
            ProductMarketplace product = productOptional.get();
            Long userId = product.getUser().getId();

            if (Objects.equals(userId, chatId)) {
                productRepository.deleteById(deleteId);
                return "Товар удален";
            } else {
                return "Ошибка: Вы не являетесь владельцем товара";
            }
        } else {
            return "Ошибка: Товар не найден";
        }
    }

    public List<ProductMarketplace> productMarketplaceList(Long userId) {
        Users user = checkForUser(userId);
        return user.getProductEntities();
    }

//    public void updatePriceProduct(Long productId, Integer newPrice) {
//        ProductMarketplace productMarketplaceUpdatePrice = checkForProduct(productId);
//        productMarketplaceUpdatePrice.setPrice(newPrice);
//        productRepository.saveAndFlush(productMarketplaceUpdatePrice);
//    }

    private Users checkForUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("User c индексом \"%s\" не найден.", userId)
                ));
    }

//    private ProductMarketplace checkForProduct(Long productId) {
//        return productRepository.findById(productId)
//                .orElseThrow(() -> new NotFoundException(
//                        String.format("User c индексом \"%s\" не найден.", productId)
//                ));
//    }
}
