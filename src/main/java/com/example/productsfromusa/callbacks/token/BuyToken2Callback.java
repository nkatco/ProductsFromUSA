package com.example.productsfromusa.callbacks.token;

import com.example.productsfromusa.callbacks.CallbackHandler;
import com.example.productsfromusa.callbacks.CallbackType;
import com.example.productsfromusa.models.*;
import com.example.productsfromusa.schedulers.TokenScheduler;
import com.example.productsfromusa.services.data.ChannelService;
import com.example.productsfromusa.services.data.PreChannelService;
import com.example.productsfromusa.services.data.TokenService;
import com.example.productsfromusa.services.data.UserService;
import com.example.productsfromusa.states.States;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Component
public class BuyToken2Callback implements CallbackHandler {

    private static final Logger logger = LoggerFactory.getLogger(BuyToken2Callback.class);

    @Autowired
    public PreChannelService preChannelService;
    @Autowired
    public TokenService tokenService;
    @Autowired
    public UserService userService;
    @Autowired
    public ChannelService channelService;
    @Autowired
    private TokenScheduler tokenScheduler;
    @Value("${token.price}")
    String price;
    @Value("${token.expiration_days}")
    String expirationDays;

    @Override
    public TelegramMessage apply(Update update) {
        long chatId = update.getCallbackQuery().getMessage().getChatId();
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        String callbackData = update.getCallbackQuery().getData();

        String id = callbackData.substring(CallbackType.BUY_TOKEN_FINAL.length());
        Channel channel = channelService.getChannelById(id);
        if (channel != null) {
            Token token = tokenService.getTokenByChannelId(channel.getId());

            User user = userService.getUserByTelegramId(update.getCallbackQuery().getFrom().getId());
            if (token == null) {
                if (user.getState().equals(States.BUY_TOKEN)) {
                    user.setState(States.BUY_TOKEN2);

                    userService.saveUser(user);

                    if (user.getWallet().getMoney() >= 3000) {
                        try {
                            LocalDateTime currentDateTime = LocalDateTime.now();
                            LocalDateTime futureDateTime = currentDateTime.plusDays(Integer.parseInt(expirationDays));
                            token = new Token();
                            token.setActive(true);
                            token.setAnons(3);
                            token.setChannel(channel);
                            token.setName(channel.getName() + "_token");
                            token.setDateOfPurchase(currentDateTime.toString());
                            token.setDateOfExpiration(futureDateTime.toString());
                            token.setPrice(Integer.parseInt(price));
                            token.setUser(user);

                            user.getWallet().setMoney(user.getWallet().getMoney() - 3000);

                            String jobId = UUID.randomUUID().toString();
                            String triggerId = UUID.randomUUID().toString();

                            Token token1 = tokenService.mergeToken(token);
                            userService.saveUser(user);

                            tokenScheduler.scheduleTokenTask("token_expiration_date_group1", futureDateTime, token1);

                            String text = "Подписка для канала " + channel.getName() + " успешно приобретена.";
                            logger.info("Subscription purchased for channel {} by user {}", channel.getName(), user.getId());

                            InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();
                            List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();

                            List<InlineKeyboardButton> rowInLine = new ArrayList<>();
                            var channelButton = new InlineKeyboardButton();

                            channelButton.setText("В главное меню");
                            channelButton.setCallbackData(CallbackType.MENU_BUTTON);
                            rowInLine.add(channelButton);
                            rowsInLine.add(rowInLine);

                            markupInLine.setKeyboard(rowsInLine);
                            message.setReplyMarkup(markupInLine);

                            message.setText(text);
                        } catch (Exception e) {
                            logger.error("Error while processing token purchase for user {}: {}", user.getId(), e.getMessage(), e);
                            e.printStackTrace();
                        }
                    } else {
                        String text = "У вас не хватает средств на балансе.\n\nНа данный момент у вас: " + user.getWallet().getMoney() + " рублей.";
                        logger.warn("User {} does not have enough funds: {}", user.getId(), user.getWallet().getMoney());

                        InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();
                        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();

                        List<InlineKeyboardButton> rowInLine = new ArrayList<>();
                        var channelButton = new InlineKeyboardButton();

                        channelButton.setText("Пополнить баланс");
                        channelButton.setCallbackData(CallbackType.PROFILE_BUTTON);
                        rowInLine.add(channelButton);
                        rowsInLine.add(rowInLine);
                        rowInLine = new ArrayList<>();

                        channelButton = new InlineKeyboardButton();
                        channelButton.setText("В главное меню");
                        channelButton.setCallbackData(CallbackType.MENU_BUTTON);
                        rowInLine.add(channelButton);
                        rowsInLine.add(rowInLine);

                        markupInLine.setKeyboard(rowsInLine);
                        message.setReplyMarkup(markupInLine);

                        message.setText(text);
                    }
                    return new TelegramSendMessage(message, String.valueOf(chatId));
                } else {
                    logger.warn("User {} is not in state BUY_TOKEN", user.getId());
                }
            } else {
                logger.info("Token already exists for channel {}", channel.getId());
            }
        } else {
            logger.warn("Channel not found with id: {}", id);
        }
        return null;
    }
}