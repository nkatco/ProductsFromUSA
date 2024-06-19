package com.example.productsfromusa.services.data;

import com.example.productsfromusa.models.Channel;
import com.example.productsfromusa.repositories.ChannelRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChannelService implements ChannelServiceImpl {
    @Autowired
    private ChannelRepository channelRepository;

    @Transactional
    public boolean addChannel(Channel channel) {
        channelRepository.save(channel);
        return existsByTelegramId(channel.getTelegramId());
    }

    public Channel getChannelById(String id) {
        return channelRepository.findChannelById(id);
    }
    public List<Channel> getAllChannelsByTelegramId(long id) {
        return channelRepository.findAllByTelegramId(id);
    }

    @Transactional
    public void saveChannel(Channel channel) {
        channelRepository.save(channel);
    }

    public Channel getChannelByTelegramId(long telegramId) {
        return channelRepository.findChannelByTelegramId(telegramId);
    }
    public Channel getChannelByUserId(String id) {
        return channelRepository.findChannelByUserId(id);
    }
    public List<Channel> getChannelsByUserId(String id) {
        return channelRepository.findAllByUserId(id);
    }
    @Transactional
    public void removeChannelByTelegramId(long telegramId) {
        channelRepository.deleteChannelByTelegramId(telegramId);
    }

    public List<Channel> getAll() {
        return channelRepository.findAll();
    }

    @Override
    public boolean existsByTelegramId(long telegramId) {
        if(getChannelByTelegramId(telegramId) != null) {
            return true;
        }
        return false;
    }
}
