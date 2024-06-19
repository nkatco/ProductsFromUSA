package com.example.productsfromusa.callbacks.anons;

import com.example.productsfromusa.callbacks.CallbackHandler;
import com.example.productsfromusa.callbacks.CallbackType;
import com.example.productsfromusa.models.Anons;
import com.example.productsfromusa.models.TelegramMessage;
import com.example.productsfromusa.models.TelegramSendMessage;
import com.example.productsfromusa.models.User;
import com.example.productsfromusa.services.data.AnonsService;
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
public class AnonsCallback implements CallbackHandler {

    @Autowired
    public UserService userService;
    @Autowired
    public AnonsService anonsService;

    @Override
    public TelegramMessage apply(Update update) {
        long chatId = update.getCallbackQuery().getMessage().getChatId();
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        User user = userService.getUserByTelegramId(update.getCallbackQuery().getFrom().getId());
        List<Anons> anonsList = new ArrayList<>(anonsService.getAllAnonsByUserId(user.getId()));
        String text = "Здесь находятся все твои анонсы.\n\nЧтобы добавить новый анонс, нажми соответствующую кнопку ниже.";

        user.setState(States.ANONS);
        userService.saveUser(user);

        InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();

        List<InlineKeyboardButton> rowInLine = new ArrayList<>();
        var anonsButton = new InlineKeyboardButton();
        if(!anonsList.isEmpty()) {
            for (Anons anons : anonsList) {
                anonsButton = new InlineKeyboardButton();
                anonsButton.setText(anons.getDate());
                anonsButton.setCallbackData(CallbackType.ANONS_SETTINGS + anons.getId());
                rowInLine.add(anonsButton);
                rowsInLine.add(rowInLine);
                rowInLine = new ArrayList<>();
            }
        }

        anonsButton = new InlineKeyboardButton();
        anonsButton.setText("Статистика");
        anonsButton.setCallbackData(CallbackType.SHOW_STATISTIC);
        rowInLine.add(anonsButton);
        rowsInLine.add(rowInLine);
        rowInLine = new ArrayList<>();

        anonsButton = new InlineKeyboardButton();
        anonsButton.setText("Новый анонс");
        anonsButton.setCallbackData(CallbackType.CREATE_ANONS1);
        rowInLine.add(anonsButton);
        rowsInLine.add(rowInLine);
        rowInLine = new ArrayList<>();

        anonsButton = new InlineKeyboardButton();
        anonsButton.setText("Главное меню");
        anonsButton.setCallbackData(CallbackType.MENU_BUTTON);
        rowInLine.add(anonsButton);
        rowsInLine.add(rowInLine);
        rowInLine = new ArrayList<>();

        markupInLine.setKeyboard(rowsInLine);
        message.setReplyMarkup(markupInLine);

        message.setText(text);
        return new TelegramSendMessage(message, String.valueOf(chatId));
    }
}
