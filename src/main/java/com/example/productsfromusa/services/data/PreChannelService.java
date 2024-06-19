package com.example.productsfromusa.services.data;

import com.example.productsfromusa.models.PreChannel;
import com.example.productsfromusa.repositories.PreChannelRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PreChannelService implements PreChannelServiceImpl {
    @Autowired
    private PreChannelRepository preChannelRepository;

    @Transactional
    public boolean addPreChannel(PreChannel preChannel) {
        preChannelRepository.save(preChannel);
        return existsByTelegramId(preChannel.getTelegramId());
    }

    public PreChannel getPreChannelById(String id) {
        return preChannelRepository.findPreChannelById(id);
    }

    @Transactional
    public void savePreChannel(PreChannel preChannel) {
        preChannelRepository.save(preChannel);
    }

    public PreChannel getPreChannelByTelegramId(long telegramId) {
        return preChannelRepository.findPreChannelByTelegramId(telegramId);
    }
    @Transactional
    public void removePreChannelByTelegramId(long telegramId) {
        preChannelRepository.deletePreChannelByTelegramId(telegramId);
    }

    public List<PreChannel> getAll() {
        return preChannelRepository.findAll();
    }

    @Override
    public boolean existsByTelegramId(long telegramId) {
        if(getPreChannelByTelegramId(telegramId) != null) {
            return true;
        }
        return false;
    }
}
