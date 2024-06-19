package com.example.productsfromusa.services.data;

import com.example.productsfromusa.models.User;
import com.example.productsfromusa.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserServiceImpl {
    @Autowired
    private UserRepository userRepository;

    @Transactional
    public boolean existsByTelegramId(long id) {
        User user = userRepository.findUserByTelegramId(id);
        if (user != null) {
            return true;
        } else {
            return false;
        }
    }
    @Transactional
    public boolean addUser(User user) {
        userRepository.save(user);
        return existsByTelegramId(user.getTelegramId());
    }

    @Transactional
    public void saveUser(User user) {
        userRepository.save(user);
    }

    public User getUserByTelegramId(long telegramId) {
        return userRepository.findUserByTelegramId(telegramId);
    }
}
