package com.example.productsfromusa.callbacks.profile;

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
public class TokensCallback implements CallbackHandler {

    @Autowired
    private UserService userService;
    @Autowired
    private TokenService tokenService;

    @Override
    public TelegramMessage apply(Update update) {
        long chatId = update.getCallbackQuery().getMessage().getChatId();
        long userId = update.getCallbackQuery().getFrom().getId();
        SendMessage message = new SendMessage();
        User user = userService.getUserByTelegramId(userId);
        user.setState(States.BASIC_STATE);
        userService.saveUser(user);

        List <Token> tokens = tokenService.getTokensByUserId(user.getId());
        StringBuilder text = new StringBuilder();
        for (Token token : tokens) {
            LocalDateTime dateTime = LocalDateTime.parse(token.getDateOfExpiration());
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            text.append("Токен: " + token.getName() +
                    " | " + " Дата окончания: " + dateTime.format(formatter) +
                    " | " + " Цена: " + token.getPrice() + "\n\n"
            );
        }
        message.setChatId(chatId);
        message.setText(text.toString() + " Здесь находятся все твои подписки. Ты можешь контроллировать их и знать, когда и что тебе потребуется оплатить.");
        InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();

        List<InlineKeyboardButton> rowInLine = new ArrayList<>();
        var balanceButton = new InlineKeyboardButton();

        balanceButton.setText("Назад");
        balanceButton.setCallbackData(CallbackType.PROFILE_BUTTON);

        rowInLine.add(balanceButton);

        var errorButton = new InlineKeyboardButton();

        errorButton.setText("Сообщить об ошибке");
        errorButton.setCallbackData(CallbackType.ERROR_BUTTON);

        rowInLine.add(errorButton);

        rowsInLine.add(rowInLine);

        // Формирование клавиатуры

        markupInLine.setKeyboard(rowsInLine);

        message.setReplyMarkup(markupInLine);
        return new TelegramSendMessage(message, String.valueOf(chatId));
    }
}
