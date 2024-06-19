package com.example.productsfromusa.services;

import com.example.productsfromusa.models.*;
import com.example.productsfromusa.restServices.ShortLinkService;
import com.example.productsfromusa.schedulers.AnonsScheduler;
import com.example.productsfromusa.services.data.AnonsPostService;
import com.example.productsfromusa.services.data.AnonsService;
import com.example.productsfromusa.services.data.LastPostService;
import com.example.productsfromusa.services.data.PostService;
import com.example.productsfromusa.utils.TelegramBotUtils;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;

@Service
public class PreAnonsService {

    private static final Logger logger = LoggerFactory.getLogger(PreAnonsService.class);

    @Autowired
    public PostService postService;
    @Autowired
    public AnonsService anonsService;
    @Autowired
    public AnonsScheduler anonsScheduler;
    @Autowired
    public AnonsPostService anonsPostService;
    @Autowired
    public LastPostService lastPostService;
    @Autowired
    public ShortLinkService shortLinkService;
    @Lazy
    @Autowired
    public TelegramBotUtils telegramBotUtils;
    @Autowired
    public ScanAnonsService scanAnonsService;
    @Autowired
    public SendPostService sendPostService;
    @Value("${bot.domain}")
    String domain;

    @Value("${server.port}")
    String port;

    public void checkAnonsPosts(Anons anons) {
        Set<AnonsPost> anonsPostSet = anons.getAnonsPosts();
        for (AnonsPost anonsPost : anonsPostSet) {
            logger.info("Set post for {} in {}", anons.getToken().getName(), anons.getDate());
            Anons anons1 = scanAnonsService.getAnonsPostForAnons(anons, anonsPost, anonsPostSet);
            if (anons1 != null) {
                anons = anons1;
            } else {
                logger.info("Not found post for {} in {}", anons.getToken().getName(), anons.getDate());
                break;
            }
        }
        anonsService.saveAnons(anons);
        LocalDateTime currentDateTime = LocalDateTime.now();
        LocalDateTime futureDateTime = currentDateTime.plusMinutes(3);
        try {
            anonsScheduler.scheduleCheckAnonsTask("anons_check_group1", futureDateTime, anons);
            logger.info("New schedule {} for {} {}", futureDateTime, anons.getToken().getName(), anons.getDate());
        } catch (SchedulerException e) {
            logger.error("Failed to schedule check anons task", e);
            throw new RuntimeException(e);
        }
    }

    public void anonsPost(Anons anons) {
        for (AnonsPost anonsPost : anons.getAnonsPosts()) {
            Post post = anonsPost.getPost();
            if (post != null) {
                String url = post.getRef();
                ShortLink shortLink = new ShortLink();
                shortLink.setLink(url);
                shortLink.setAnonsPost(anonsPost);
                url = "http://" + domain + "/link/" + shortLinkService.saveShortLink(shortLink).getId();
                SendMessagePost s = sendPostService.getPostByChannelSettings(post, anons.getToken().getChannel().getChannelSettings());
                telegramBotUtils.sendMessageForChannel(anons.getToken().getChannel(), s.getPhoto(), s.getText(), url);
            }
        }
        for (AnonsPost anonsPost : anons.getAnonsPosts()) {
            Post post = anonsPost.getPost();
            if (post != null) {
                anonsPost.setPost(null);
                anonsPostService.saveAnons(anonsPost);
            }
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        LocalTime time = LocalTime.parse(anons.getDate(), formatter);
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime resultDateTime = LocalDateTime.of(now.toLocalDate(), time);
        resultDateTime = resultDateTime.plusDays(1);
        try {
            anonsScheduler.scheduleAnonsTask("anons_group1", resultDateTime, anons);
            logger.info("New schedule {}", resultDateTime);
        } catch (SchedulerException e) {
            logger.error("Failed to schedule anons task", e);
            throw new RuntimeException(e);
        }
    }
}