package com.example.productsfromusa.repositories;

import com.example.productsfromusa.models.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
    User findUserByTelegramId(long telegramId);

    User save(User user);
}
