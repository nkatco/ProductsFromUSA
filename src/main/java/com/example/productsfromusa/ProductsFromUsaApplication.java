package com.example.productsfromusa;

import com.example.productsfromusa.models.*;
import com.example.productsfromusa.services.SendPostService;
import com.example.productsfromusa.services.data.CategoryService;
import com.example.productsfromusa.services.data.PostService;
import com.example.productsfromusa.utils.TelegramBotUtils;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.List;

@SpringBootApplication
@EnableScheduling
public class ProductsFromUsaApplication {

    @Autowired
    private TelegramBotUtils telegramBotUtils;
    @Autowired
    private PostService postService;
    @Autowired
    private SendPostService sendPostService;
    @Autowired
    private CategoryService categoryService;

    public static void main(String[] args) {
        SpringApplication.run(ProductsFromUsaApplication.class, args);
    }

    @PostConstruct
    public void init() {
        System.out.println("HEHE");

        Post post = new Post();
        post.setPrice(1555);
        post.setOldPrice(15555);
        post.setImage("data/sources/images/b298e08a-4644-4fd7-b0c3-4601a53ad8d0_ava1.jpg");
        post.setRef("https://google.ru");
        post.setCategory(null);
        post.setText("seeex");
        post.setName("sex");
        Post post2 = postService.getLastPost(null);
        if (post2 != null) {
            post.setNumber(post2.getNumber() + 1);
        } else {
            post.setNumber(0);
        }
        postService.savePost(post);

        List<Post> list = postService.getAll();
        for (Post post1 : list) {
            if (post1 != null) {
                String url = post1.getRef();
                WatermarkImage watermarkImage = new WatermarkImage();
                watermarkImage.setSize(100);
                watermarkImage.setAlpha(0.7f);
                watermarkImage.setMode("full");
                watermarkImage.setPath("data/watermarks/eb9378e8-aad1-42de-aed5-04539084a70e.jpg");

                ChannelSettings channelSettings = new ChannelSettings();
                channelSettings.setReduction(1);
                channelSettings.setShowPrice(true);
                channelSettings.setShowOldPrice(true);
                channelSettings.setPostText("seeeeeex");
                channelSettings.setPriceNote("sex");
                channelSettings.setWatermarkImage(watermarkImage);
                SendMessagePost s = sendPostService.getPostByChannelSettings(post1, channelSettings);

                Channel channel = new Channel();
                channel.setTelegramId(-1002057112397L);
                channel.setChatId(-1002057112397L);
                channel.setChannelSettings(channelSettings);
                channel.setActive(true);
                channel.setName("test channel");
                telegramBotUtils.sendMessageForChannel(channel, s.getPhoto(), s.getText(), url);
            }
        }
    }
}