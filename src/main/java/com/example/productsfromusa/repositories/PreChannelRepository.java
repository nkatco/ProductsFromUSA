package com.example.productsfromusa.repositories;

import com.example.productsfromusa.models.PreChannel;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PreChannelRepository extends CrudRepository<PreChannel, Long> {
    PreChannel findPreChannelByTelegramId(long telegramId);
    PreChannel findPreChannelById(String id);
    PreChannel save(PreChannel preChannel);
    void deletePreChannelByTelegramId(long telegramId);
    List<PreChannel> findAll();
}
