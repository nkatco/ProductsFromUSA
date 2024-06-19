package com.example.productsfromusa.callbacks.profile;

import com.example.productsfromusa.callbacks.CallbackHandler;
import com.example.productsfromusa.callbacks.CallbackType;
import com.example.productsfromusa.models.TelegramMessage;
import com.example.productsfromusa.models.TelegramSendMessage;
import com.example.productsfromusa.models.User;
import com.example.productsfromusa.services.data.UserService;
import com.example.productsfromusa.states.States;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Component
public class MoneyCallback implements CallbackHandler {

    @Autowired
    private UserService userService;

    @Override
    public TelegramMessage apply(Update update) {
        long chatId = update.getCallbackQuery().getMessage().getChatId();
        long userId = update.getCallbackQuery().getFrom().getId();
        SendMessage message = new SendMessage();
        User user = userService.getUserByTelegramId(userId);
        user.setState(States.ADD_PAYMENT);
        userService.saveUser(user);

        message.setChatId(chatId);
        message.setText("Внимание! После пополнения кошелька, деньги НЕЛЬЗЯ будет вывести. Отправляя деньги, вы соглашаетесь с политикой сервиса." +
                "\n\nВведите сумму, на которую вы хотите пополнить кошелек.");
        InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();

        List<InlineKeyboardButton> rowInLine = new ArrayList<>();
        var balanceButton = new InlineKeyboardButton();

        balanceButton.setText("Назад");
        balanceButton.setCallbackData(CallbackType.PROFILE_BUTTON);

        rowInLine.add(balanceButton);

        rowsInLine.add(rowInLine);

        // Формирование клавиатуры

        markupInLine.setKeyboard(rowsInLine);

        message.setReplyMarkup(markupInLine);
        return new TelegramSendMessage(message, String.valueOf(chatId));
    }
}
