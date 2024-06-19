package com.example.productsfromusa.services;

import com.example.productsfromusa.models.Token;
import com.example.productsfromusa.models.User;
import com.example.productsfromusa.services.data.UserService;
import com.example.productsfromusa.schedulers.TokenScheduler;
import com.example.productsfromusa.services.data.TokenService;
import com.example.productsfromusa.utils.TelegramBotUtils;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class TokenAnonsService {
    private static final Logger logger = LoggerFactory.getLogger(TokenAnonsService.class);

    @Autowired
    private TokenScheduler tokenScheduler;

    @Lazy
    @Autowired
    private TelegramBotUtils telegramBotUtils;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UserService userService;

    @Value("${token.expiration_days}")
    private String expirationDays;

    @Value("${token.price}")
    private String price;

    public void updateToken(Token token) {
        logger.info("Updating token with ID: {}", token.getId());

        if(token.isActive()) {
            if (token.getUser().getWallet().getMoney() >= Integer.parseInt(price)) {
                try {
                    LocalDateTime currentDateTime = LocalDateTime.now();
                    LocalDateTime futureDateTime = currentDateTime.plusDays(Integer.parseInt(expirationDays));
                    User user = token.getUser();
                    user.getWallet().setMoney(user.getWallet().getMoney() - Integer.parseInt(price));
                    token.setDateOfExpiration(futureDateTime.toString());

                    tokenScheduler.scheduleTokenTask("token_expiration_date_group1", futureDateTime, token);
                    tokenService.saveToken(token);
                    userService.saveUser(user);

                    telegramBotUtils.sendMessageForUser(token.getUser(), "Ваша подписка для канала " + token.getChannel().getName() + " была продлена. По итогам ваш баланс составляет: " + user.getWallet().getMoney() + "₽");

                    logger.info("Token updated and extended for user with ID: {}", user.getId());
                } catch (SchedulerException e) {
                    logger.error("SchedulerException occurred while scheduling token task for token ID: {}", token.getId(), e);
                    throw new RuntimeException(e);
                }
            } else {
                LocalDateTime currentDateTime = LocalDateTime.now();
                LocalDateTime futureDateTime = currentDateTime.plusHours(1);
                try {
                    tokenScheduler.schedulePreTokenTask("token_hour_expiration_date_group2", futureDateTime, token);
                } catch (SchedulerException e) {
                    logger.error("SchedulerException occurred while scheduling pre-token task for token ID: {}", token.getId(), e);
                    throw new RuntimeException(e);
                }

                telegramBotUtils.sendMessageForUser(token.getUser(), "Ваша подписка для канала " + token.getChannel().getName() + " истечет в течении часа. На вашем балансе недостаточно средств для продления токена.\n\nПожалуйста, пополните баланс.");
                logger.warn("User with ID: {} does not have enough funds to extend token ID: {}", token.getUser().getId(), token.getId());
            }
        } else {
            telegramBotUtils.sendMessageForUser(token.getUser(), "Ваша подписка для канала " + token.getChannel().getName() + " была удалена. Спасибо за то, что пользуетесь нашим сервисом.");
            tokenService.removeTokenById(token.getId());
            logger.info("Inactive token with ID: {} was removed", token.getId());
        }
    }

    public void updatePreToken(Token token) {
        logger.info("Updating pre-token with ID: {}", token.getId());

        if(token.isActive()) {
            if(token.getUser().getWallet().getMoney() >= Integer.parseInt(price)) {
                try {
                    LocalDateTime currentDateTime = LocalDateTime.now();
                    LocalDateTime futureDateTime = currentDateTime.plusDays(Integer.parseInt(expirationDays));
                    User user = token.getUser();
                    user.getWallet().setMoney(user.getWallet().getMoney() - Integer.parseInt(price));
                    token.setDateOfExpiration(futureDateTime.toString());

                    tokenScheduler.scheduleTokenTask("token_expiration_date_group1", futureDateTime, token);
                    tokenService.saveToken(token);
                    userService.saveUser(user);

                    logger.info("Pre-token updated and extended for user with ID: {}", user.getId());
                } catch (SchedulerException e) {
                    logger.error("SchedulerException occurred while scheduling token task for pre-token ID: {}", token.getId(), e);
                    throw new RuntimeException(e);
                }
            } else {
                tokenService.removeTokenById(token.getId());
                telegramBotUtils.sendMessageForUser(token.getUser(), "Ваша подписка для канала " + token.getChannel().getName() + " истекла.");
                logger.warn("Pre-token with ID: {} was removed due to insufficient funds for user ID: {}", token.getId(), token.getUser().getId());
            }
        } else {
            telegramBotUtils.sendMessageForUser(token.getUser(), "Ваша подписка для канала " + token.getChannel().getName() + " была удалена. Спасибо за то, что пользуетесь нашим сервисом.");
            tokenService.removeTokenById(token.getId());
            logger.info("Inactive pre-token with ID: {} was removed", token.getId());
        }
    }
}