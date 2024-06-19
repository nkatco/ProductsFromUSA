package com.example.productsfromusa.repositories;

import com.example.productsfromusa.models.ChannelSettings;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChannelSettingsRepository extends CrudRepository<ChannelSettings, Long> {
    ChannelSettings findChannelSettingsById(String id);
    ChannelSettings save(ChannelSettings channelSettings);
    void deleteChannelSettingsById(String id);
    List<ChannelSettings> findAll();
}
