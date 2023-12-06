package com.example.pricetrackbot.service.repositories;


import com.example.pricetrackbot.service.entity.ProductMarketplace;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<ProductMarketplace, Integer> {
}
