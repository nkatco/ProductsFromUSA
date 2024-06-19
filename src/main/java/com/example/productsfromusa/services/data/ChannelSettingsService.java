package com.example.productsfromusa.services.data;

import com.example.productsfromusa.models.ChannelSettings;
import com.example.productsfromusa.repositories.ChannelSettingsRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChannelSettingsService implements ChannelSettingsServiceImpl {
    @Autowired
    private ChannelSettingsRepository channelSettingsRepository;

    @Transactional
    public boolean addChannelSettings(ChannelSettings channelSettings) {
        channelSettingsRepository.save(channelSettings);
        return existsById(channelSettings.getId());
    }
    @Transactional
    public ChannelSettings saveChannelSettings(ChannelSettings channelSettings) {
        return channelSettingsRepository.save(channelSettings);
    }

    public ChannelSettings getChannelSettingsById(String id) {
        return channelSettingsRepository.findChannelSettingsById(id);
    }
    @Transactional
    public void removeChannelByTelegramId(String id) {
        channelSettingsRepository.deleteChannelSettingsById(id);
    }

    public List<ChannelSettings> getAll() {
        return channelSettingsRepository.findAll();
    }

    @Override
    public boolean existsById(String id) {
        if(getChannelSettingsById(id) != null) {
            return true;
        }
        return false;
    }
}
