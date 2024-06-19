package com.example.productsfromusa.callbacks.token;

import com.example.productsfromusa.callbacks.CallbackHandler;
import com.example.productsfromusa.callbacks.CallbackType;
import com.example.productsfromusa.models.*;
import com.example.productsfromusa.services.data.ChannelService;
import com.example.productsfromusa.services.data.PreChannelService;
import com.example.productsfromusa.services.data.TokenService;
import com.example.productsfromusa.services.data.UserService;
import com.example.productsfromusa.states.States;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Component
public class BuyTokenCallback implements CallbackHandler {

    @Autowired
    public PreChannelService preChannelService;
    @Autowired
    public UserService userService;
    @Autowired
    public ChannelService channelService;
    @Autowired
    public TokenService tokenService;
    @Value("${bot.anons}")
    String anons;
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

        String id = callbackData.substring(CallbackType.BUY_TOKEN.length());
        Channel channel = channelService.getChannelById(id);

        if (channel != null) {
            Token token = tokenService.getTokenByChannelId(channel.getId());

            User user = userService.getUserByTelegramId(update.getCallbackQuery().getFrom().getId());

            if (token == null) {
                if (user.getState().equals(States.CHANNEL_SETTINGS)) {
                    user.setState(States.BUY_TOKEN);

                    userService.saveUser(user);
                    String text = "Подписка для канала" + channel.getName() + " стоит " + price + "₽ и будет действовать " + expirationDays + " дней. Подписка позволит тебе назначить " + anons + " анонса на канал.\n\nСпустя " + expirationDays + " дней(под конец подписки) убедись, что у тебя достаточно средств на балансе для проления подписки.\n\nЕсли вы согласны, нажмите кнопку ниже.";

                    InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();
                    List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();

                    List<InlineKeyboardButton> rowInLine = new ArrayList<>();
                    var channelButton = new InlineKeyboardButton();

                    channelButton.setText("Купить");
                    channelButton.setCallbackData(CallbackType.BUY_TOKEN_FINAL + channel.getId());
                    rowInLine.add(channelButton);
                    rowsInLine.add(rowInLine);
                    rowInLine = new ArrayList<>();

                    channelButton = new InlineKeyboardButton();
                    channelButton.setText("Назад");
                    channelButton.setCallbackData(CallbackType.CHANNEL_SETTINGS + channel.getId());
                    rowInLine.add(channelButton);
                    rowsInLine.add(rowInLine);

                    markupInLine.setKeyboard(rowsInLine);
                    message.setReplyMarkup(markupInLine);

                    message.setText(text);
                    return new TelegramSendMessage(message, String.valueOf(chatId));
                }
            }
        }
        return null;
    }
}
