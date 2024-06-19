package com.example.productsfromusa.commands;

import com.example.productsfromusa.callbacks.CallbackType;
import com.example.productsfromusa.models.*;
import com.example.productsfromusa.services.data.UserService;
import com.example.productsfromusa.states.States;
import com.vdurmont.emoji.EmojiParser;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Component
public class StartCommand implements Command {

    @Autowired
    private UserService userService;

    public static SendPhoto getStartCommand() {
        SendPhoto message = new SendPhoto();
        String answer = EmojiParser.parseToUnicode("Добро пожаловать в ProductsFromUSA!" + " :blush:" + "\n\nБот на данный момент находится в режиме альфа-тестировании. По всем вопросам обращаться к администратору по кнопке \"Сообщить об ошибке\"");

        message.setCaption(answer);

        InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();

        // Первая линия

        List<InlineKeyboardButton> rowInLine = new ArrayList<>();
        var anonsButton = new InlineKeyboardButton();

        anonsButton.setText(EmojiParser.parseToUnicode(":crown:" + " Анонсы"));
        anonsButton.setCallbackData(CallbackType.ANONS_BUTTON);

        rowInLine.add(anonsButton);

        rowsInLine.add(rowInLine);

        // Вторая линия

        rowInLine = new ArrayList<>();

        var profileButton = new InlineKeyboardButton();

        profileButton.setText(EmojiParser.parseToUnicode(":ok_woman:" + " Профиль"));
        profileButton.setCallbackData(CallbackType.PROFILE_BUTTON);

        var tokensButton = new InlineKeyboardButton();

        tokensButton.setText(EmojiParser.parseToUnicode(":desktop_computer:" + " Мои каналы"));
        tokensButton.setCallbackData(CallbackType.CHANNELS_BUTTON);

        rowInLine.add(profileButton);
        rowInLine.add(tokensButton);

        rowsInLine.add(rowInLine);

        // Третья линия

        rowInLine = new ArrayList<>();

        var channelsButton = new InlineKeyboardButton();

        channelsButton.setText(EmojiParser.parseToUnicode(":closed_book:" + " Видео-гайд"));
        channelsButton.setCallbackData(CallbackType.GUIDE_BUTTON);

        var errorButton = new InlineKeyboardButton();

        errorButton.setText(EmojiParser.parseToUnicode(":telephone:" + " Сообщить об ошибке"));
        errorButton.setCallbackData(CallbackType.ERROR_BUTTON);

        rowInLine.add(channelsButton);
        rowInLine.add(errorButton);

        rowsInLine.add(rowInLine);

        // Формирование клавиатуры

        markupInLine.setKeyboard(rowsInLine);

        message.setReplyMarkup(markupInLine);
        message.setPhoto(new InputFile(new File("src/main/resources/images/banner.png")));
        return message;
    }

    @Override
    public TelegramMessage apply(Update update) {
        long chatId = update.getMessage().getChatId();
        long userId = update.getMessage().getFrom().getId();
        SendPhoto message = getStartCommand();

        User user = userService.getUserByTelegramId(userId);
        user.setState(States.BASIC_STATE);
        userService.saveUser(user);

        message.setChatId(String.valueOf(chatId));

        return new TelegramSendPhoto(message, String.valueOf(chatId));
    }
}