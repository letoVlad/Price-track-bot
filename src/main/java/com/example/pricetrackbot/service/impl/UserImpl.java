package com.example.pricetrackbot.service.impl;

import com.example.pricetrackbot.service.repositories.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserImpl {
    private  final UserRepository userRepository;

    public UserImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
}
