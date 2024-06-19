package com.example.productsfromusa.repositories;

import com.example.productsfromusa.models.Channel;
import com.example.productsfromusa.models.Post;
import com.example.productsfromusa.models.Statistic;
import com.example.productsfromusa.models.Token;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StatisticRepository extends CrudRepository<Statistic, Long> {
    Statistic findStatisticById(String id);
    List<Statistic> findAllByChannel(Channel channel);
    Statistic save(Statistic statistic);
    List<Statistic> findAllByIpAndLinkId(String ip, String linkId);
    void deleteStatisticById(String id);
    List<Statistic> findAll();
    boolean existsById(String id);
    List<Statistic> findAllByChannelAndCreationDateBetween(Channel channel, LocalDateTime creationDate, LocalDateTime creationDate2);
}
