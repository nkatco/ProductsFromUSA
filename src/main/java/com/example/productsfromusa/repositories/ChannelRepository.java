package com.example.productsfromusa.repositories;

import com.example.productsfromusa.models.Channel;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChannelRepository extends CrudRepository<Channel, Long> {
    Channel findChannelByTelegramId(long telegramId);
    Channel findChannelById(String id);
    Channel findChannelByUserId(String id);
    List<Channel> findAllByTelegramId(long id);
    List<Channel> findAllByUserId(String id);
    Channel save(Channel Channel);
    void deleteChannelByTelegramId(long telegramId);
    List<Channel> findAll();
}
