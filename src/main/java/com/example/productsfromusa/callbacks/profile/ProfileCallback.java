package com.example.productsfromusa.callbacks.profile;

import com.example.productsfromusa.callbacks.CallbackHandler;
import com.example.productsfromusa.callbacks.CallbackType;
import com.example.productsfromusa.models.TelegramMessage;
import com.example.productsfromusa.models.TelegramSendMessage;
import com.example.productsfromusa.models.User;
import com.example.productsfromusa.models.Wallet;
import com.example.productsfromusa.services.data.UserService;
import com.example.productsfromusa.states.States;
import com.vdurmont.emoji.EmojiParser;
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
public class ProfileCallback implements CallbackHandler {

    @Autowired
    private UserService userService;

    @Override
    public TelegramMessage apply(Update update) {
        long chatId = update.getCallbackQuery().getMessage().getChatId();
        long userId = update.getCallbackQuery().getFrom().getId();
        SendMessage message = new SendMessage();
        User user = userService.getUserByTelegramId(userId);
        user.setState(States.BASIC_STATE);
        userService.saveUser(user);
        message.setChatId(chatId);
        Wallet wallet = user.getWallet();
        int mon = Math.toIntExact(wallet.getMoney());
        message.setText("Имя: " + user.getName() + "\n"
                + "Телефон: " + user.getPhone().getNumber() + "\n" +
                "ID: " + user.getTelegramId() + "\n" +
                "Баланс: " + mon);
        InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();

        // Первая линия

        List<InlineKeyboardButton> rowInLine = new ArrayList<>();
        var balanceButton = new InlineKeyboardButton();

        balanceButton.setText(EmojiParser.parseToUnicode(":crown:" + " Пополнить баланс"));
        balanceButton.setCallbackData(CallbackType.UP_BALANCE);

        rowInLine.add(balanceButton);

        rowsInLine.add(rowInLine);

        // Вторая линия

        rowInLine = new ArrayList<>();

        var userTokensButton = new InlineKeyboardButton();

        userTokensButton.setText(EmojiParser.parseToUnicode(":moneybag:" + " Действующие подписки"));
        userTokensButton.setCallbackData(CallbackType.USER_TOKENS);

        var historyButton = new InlineKeyboardButton();

        historyButton.setText(EmojiParser.parseToUnicode(":spiral_note_pad:" + " История кошелька"));
        historyButton.setCallbackData(CallbackType.HISTORY_BALANCE);

        rowInLine.add(historyButton);
        rowInLine.add(userTokensButton);

        rowsInLine.add(rowInLine);

        // Третья линия

        rowInLine = new ArrayList<>();

        var menuButton = new InlineKeyboardButton();

        menuButton.setText(EmojiParser.parseToUnicode(":house:" + " Назад"));
        menuButton.setCallbackData(CallbackType.MENU_BUTTON);

        rowInLine.add(menuButton);

        rowsInLine.add(rowInLine);

        // Формирование клавиатуры

        markupInLine.setKeyboard(rowsInLine);

        message.setReplyMarkup(markupInLine);
        return new TelegramSendMessage(message, String.valueOf(chatId));
    }
}
