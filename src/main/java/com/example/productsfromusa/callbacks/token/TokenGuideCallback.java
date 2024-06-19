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
import com.vdurmont.emoji.EmojiParser;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
public class TokenGuideCallback implements CallbackHandler {

    @Autowired
    public UserService userService;

    @Override
    public TelegramMessage apply(Update update) {
        long chatId = update.getCallbackQuery().getMessage().getChatId();
        SendMessage message = new SendMessage();
        message.setChatId(chatId);

        User user = userService.getUserByTelegramId(update.getCallbackQuery().getFrom().getId());

        if (user.getState().equals(States.CHANNEL_SETTINGS)) {
            user.setState(States.BASIC_STATE);

            message.setText(EmojiParser.parseToUnicode("Ролик со всем функционалом бота :point_down:"));

            InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();
            List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();

            List<InlineKeyboardButton> rowInLine = new ArrayList<>();
            var tokenButton = new InlineKeyboardButton();

            tokenButton = new InlineKeyboardButton();
            tokenButton.setText("Вернуться назад");
            tokenButton.setCallbackData(CallbackType.MENU_BUTTON);
            rowInLine.add(tokenButton);
            rowsInLine.add(rowInLine);
            rowInLine = new ArrayList<>();

            markupInLine.setKeyboard(rowsInLine);
            message.setReplyMarkup(markupInLine);

            return new TelegramSendMessage(message, String.valueOf(chatId));
        }
        return null;
    }
}
