package com.example.productsfromusa.repositories;

import com.example.productsfromusa.models.Token;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TokenRepository extends CrudRepository<Token, Long> {
    Token findTokenById(String id);
    Token findTokenByUserId(String id);
    Token findTokenByChannelId(String id);
    List<Token> findAllByUserId(String id);
    Token save(Token token);
    void deleteTokenById(String idd);
    List<Token> findAll();
    boolean existsById(String id);
}
