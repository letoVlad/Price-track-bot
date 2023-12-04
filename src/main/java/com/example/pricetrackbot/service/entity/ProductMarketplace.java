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
    private long id;

    @Column(name = "price", nullable = false)
    private int price;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    Users user;
}


