package com.example.productsfromusa.services.data;

import com.example.productsfromusa.models.Channel;
import com.example.productsfromusa.models.Statistic;
import com.example.productsfromusa.models.Token;
import com.example.productsfromusa.repositories.StatisticRepository;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class StatisticService implements StatisticServiceImpl {
    @Autowired
    private StatisticRepository statisticRepository;
    @Autowired
    private EntityManager entityManager;

    @Transactional
    public boolean addStatistic(Statistic statistic) {
        statisticRepository.save(statistic);
        return existsById(statistic.getId());
    }

    public List<Statistic> getAllByIpAndLinkId(String ip, String linkId) {
        return statisticRepository.findAllByIpAndLinkId(ip, linkId);
    }

    public List<Statistic> getStatisticsByChannel(Channel channel) {
        return statisticRepository.findAllByChannel(channel);
    }

    public List<Statistic> getStatisticsByChannelAndDate(Channel channel, LocalDateTime creationDate, LocalDateTime creationDate2) {
        return statisticRepository.findAllByChannelAndCreationDateBetween(channel, creationDate, creationDate2);
    }

    public Statistic getStatisticById(String id) {
        return statisticRepository.findStatisticById(id);
    }

    @Transactional
    public void saveStatistic(Statistic statistic) {
        statisticRepository.save(statistic);
    }

    @Transactional
    public Statistic mergeStatistic(Statistic statistic) {
        return entityManager.merge(statistic);
    }

    @Transactional
    public void removeStatisticById(String id) {
        statisticRepository.deleteStatisticById(id);
    }

    public List<Statistic> getAll() {
        return statisticRepository.findAll();
    }

    @Override
    public boolean existsById(String id) {
        return statisticRepository.existsById(id);
    }
}
