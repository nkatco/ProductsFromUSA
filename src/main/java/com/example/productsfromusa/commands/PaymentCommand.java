package com.example.productsfromusa.commands;

import com.example.productsfromusa.models.Command;
import com.example.productsfromusa.models.TelegramMessage;
import com.example.productsfromusa.models.TelegramSendMessage;
import com.example.productsfromusa.models.User;
import com.example.productsfromusa.services.data.UserService;
import com.example.productsfromusa.states.States;
import com.example.productsfromusa.utils.TelegramBotUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class PaymentCommand implements Command {

    private static final Logger logger = LoggerFactory.getLogger(PaymentCommand.class);

    @Autowired
    private UserService userService;

    @Lazy
    @Autowired
    public TelegramBotUtils telegramBotUtils;

    @Override
    public TelegramMessage apply(Update update) {
        long chatId = update.getMessage().getChatId();
        long userId = update.getMessage().getFrom().getId();
        SendMessage message = new SendMessage();

        try {
            int price = Integer.parseInt(update.getMessage().getText());
            logger.info("Received price: {}", price);

            try {
                User user = userService.getUserByTelegramId(userId);
                user.setState(States.BASIC_STATE);
                userService.saveUser(user);

                message.setText("Для пополнения баланса, нажмите кнопку выше.");
                telegramBotUtils.sendInvoice(chatId, price);
                logger.info("Invoice sent to chatId: {} with price: {}", chatId, price);
            } catch (Exception e) {
                message.setText("Произошла какая-то ошибка, введите /start и обратитесь к администратору.");
                logger.error("Error processing payment for user {}: {}", userId, e.getMessage(), e);
            }
        } catch (Exception e) {
            message.setText("Значение должно быть без запятых и пробелов, только число.");
            logger.error("Invalid price format received: {}", update.getMessage().getText(), e);
        }

        message.setChatId(chatId);
        return new TelegramSendMessage(message, String.valueOf(chatId));
    }
}