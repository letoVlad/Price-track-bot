package com.example.pricetrackbot.service.repositories;

import com.example.pricetrackbot.service.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<Users, Integer> {
}
