package com.example.pricetrackbot.service.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "users")
public class Users {

    @Id
    @Column(columnDefinition = "bigint")
    private Long id;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "user")
    private List<ProductMarketplace> productEntities = new ArrayList<>();
}
