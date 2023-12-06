package com.example.pricetrackbot.service.entity;

import lombok.Data;
import org.springframework.stereotype.Component;

import javax.persistence.Entity;
import javax.persistence.*;


@Component
@Data
@Entity
@Table(name = "product_marketplace")
public class ProductMarketplace {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "price", nullable = false)
    private Integer price;

    @Column(name = "name_Product", nullable = false)
    private String nameProduct;

    @Column(name = "url", nullable = false)
    private String url;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    Users user;
}


