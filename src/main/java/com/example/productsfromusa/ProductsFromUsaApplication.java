package com.example.productsfromusa;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.time.LocalDateTime;

@SpringBootApplication
@EnableScheduling
public class ProductsFromUsaApplication {

    private static final Logger logger = LoggerFactory.getLogger(ProductsFromUsaApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(ProductsFromUsaApplication.class, args);
    }

    @PostConstruct
    public void init() {
        LocalDateTime dateTime = LocalDateTime.now();
        logger.info("Bot started in {}", dateTime.toString());
    }
}