package com.example.pricetrackbot.service.impl;

import com.example.pricetrackbot.service.entity.Users;
import com.example.pricetrackbot.service.repositories.UserRepository;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

@Service
public class UserImpl {
    private final UserRepository userRepository;

    public UserImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void addUser(Update update) {
        Users newUser = new Users();
        newUser.setId(Math.toIntExact(update.getMessage().getChatId()));
        userRepository.saveAndFlush(newUser);
    }
}
