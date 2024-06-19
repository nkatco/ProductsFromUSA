package com.example.productsfromusa.callbacks.token;

import com.example.productsfromusa.callbacks.CallbackHandler;
import com.example.productsfromusa.callbacks.CallbackType;
import com.example.productsfromusa.models.TelegramMessage;
import com.example.productsfromusa.models.TelegramSendMessage;
import com.example.productsfromusa.models.Token;
import com.example.productsfromusa.models.User;
import com.example.productsfromusa.services.data.TokenService;
import com.example.productsfromusa.services.data.UserService;
import com.example.productsfromusa.states.States;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Component
public class TokenRemove2Callback implements CallbackHandler {

    private static final Logger logger = LoggerFactory.getLogger(TokenRemove2Callback.class);

    @Autowired
    public TokenService tokenService;
    @Autowired
    public UserService userService;

    @Override
    public TelegramMessage apply(Update update) {
        long chatId = update.getCallbackQuery().getMessage().getChatId();
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        String callbackData = update.getCallbackQuery().getData();

        String id = callbackData.substring(CallbackType.REMOVE_TOKEN2.length());
        Token token = tokenService.getTokenById(id);

        if (token == null) {
            logger.warn("Token with id {} not found", id);
            return null;
        }

        User user = userService.getUserByTelegramId(update.getCallbackQuery().getFrom().getId());

        if (user == null) {
            logger.warn("User with Telegram ID {} not found", update.getCallbackQuery().getFrom().getId());
            return null;
        }

        logger.info("Processing token deactivation for token id: {} and user id: {}", id, user.getTelegramId());

        if (token.isActive() && user.getState().equals(States.INFO_TOKEN)) {
            user.setState(States.BASIC_STATE);
            userService.saveUser(user);
            token.setActive(false);
            tokenService.saveToken(token);

            logger.info("Token {} deactivated and user state set to BASIC_STATE", id);

            LocalDateTime dateTime = LocalDateTime.parse(token.getDateOfExpiration());
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            message.setText("Токен был успешно деактивирован, " + dateTime.format(formatter) + " он удалится полностью.");

            InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();
            List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();

            List<InlineKeyboardButton> rowInLine = new ArrayList<>();
            InlineKeyboardButton tokenButton = new InlineKeyboardButton();

            tokenButton.setText("Вернуться в меню");
            tokenButton.setCallbackData(CallbackType.MENU_BUTTON);
            rowInLine.add(tokenButton);
            rowsInLine.add(rowInLine);

            markupInLine.setKeyboard(rowsInLine);
            message.setReplyMarkup(markupInLine);

            return new TelegramSendMessage(message, String.valueOf(chatId));
        }

        logger.warn("Token {} is already inactive or user state is not INFO_TOKEN", id);
        return null;
    }
}