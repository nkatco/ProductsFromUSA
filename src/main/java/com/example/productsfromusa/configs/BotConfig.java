package com.example.productsfromusa.configs;

import com.example.productsfromusa.TelegramBot;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "bot")
@Data
@PropertySource("application.properties")
public class BotConfig {
    @Value("${bot.name}")
    String name;

    @Value("${bot.token}")
    String token;
}
