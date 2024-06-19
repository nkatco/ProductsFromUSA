package com.example.productsfromusa.services.data;
import com.example.productsfromusa.models.Token;
import com.example.productsfromusa.repositories.TokenRepository;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TokenService implements TokenServiceImpl {
    @Autowired
    private TokenRepository tokenRepository;
    @Autowired
    private EntityManager entityManager;

    @Transactional
    public boolean addToken(Token token) {
        tokenRepository.save(token);
        return existsById(token.getId());
    }

    public Token getTokenById(String id) {
        return tokenRepository.findTokenById(id);
    }
    public Token getTokenByChannelId(String id) {
        return tokenRepository.findTokenByChannelId(id);
    }

    @Transactional
    public void saveToken(Token token) {
        tokenRepository.save(token);
    }
    @Transactional
    public Token mergeToken(Token token) {
        return entityManager.merge(token);
    }
    public Token getTokenByUserId(String id) {
        return tokenRepository.findTokenByUserId(id);
    }
    public List<Token> getTokensByUserId(String id) {
        return tokenRepository.findAllByUserId(id);
    }
    @Transactional
    public void removeTokenById(String id) {
        tokenRepository.deleteTokenById(id);
    }

    public List<Token> getAll() {
        return tokenRepository.findAll();
    }

    @Override
    public boolean existsById(String id) {
        return existsById(id);
    }
}
