package com.example.productsfromusa.callbacks.anons;

import com.example.productsfromusa.callbacks.CallbackHandler;
import com.example.productsfromusa.callbacks.CallbackType;
import com.example.productsfromusa.models.*;
import com.example.productsfromusa.services.data.AnonsPostService;
import com.example.productsfromusa.services.data.AnonsService;
import com.example.productsfromusa.services.data.UserService;
import com.example.productsfromusa.states.States;
import com.example.productsfromusa.utils.Consts;
import com.example.productsfromusa.utils.TelegramBotUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Component
public class RemoveAnonsPostCallback implements CallbackHandler {

    @Autowired
    public UserService userService;
    @Autowired
    public AnonsService anonsService;
    @Autowired
    public AnonsPostService anonsPostService;
    @Lazy
    @Autowired
    public TelegramBotUtils telegramBotUtils;

    @Override
    public TelegramMessage apply(Update update) {
        long chatId = update.getCallbackQuery().getMessage().getChatId();
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        User user = userService.getUserByTelegramId(update.getCallbackQuery().getFrom().getId());
        String callbackData = update.getCallbackQuery().getData();
        String id = callbackData.substring(CallbackType.REMOVE_ANONS_POST.length());
        AnonsPost anonsPost = anonsPostService.getAnonsById(id);
        if(anonsPost != null) {
            if (user.getState().equals(States.ANONS_SETTINGS)) {
                String text = "Анонс " + anonsPost.getPost().getName() + " был удален. Ожидайте назначение нового анонса.";

                anonsPost.setPost(null);
                anonsPostService.saveAnons(anonsPost);

                user.setState(States.ANONS);
                userService.saveUser(user);

                InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();
                List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();

                List<InlineKeyboardButton> rowInLine = new ArrayList<>();
                var anonsButton = new InlineKeyboardButton();

                anonsButton = new InlineKeyboardButton();
                anonsButton.setText("К анонсу");
                anonsButton.setCallbackData(CallbackType.ANONS_SETTINGS + anonsPost.getAnons().getId());
                rowInLine.add(anonsButton);
                rowsInLine.add(rowInLine);
                rowInLine = new ArrayList<>();

                anonsButton = new InlineKeyboardButton();
                anonsButton.setText("Главное меню");
                anonsButton.setCallbackData(CallbackType.ANONS_BUTTON);
                rowInLine.add(anonsButton);
                rowsInLine.add(rowInLine);
                rowInLine = new ArrayList<>();

                markupInLine.setKeyboard(rowsInLine);
                message.setReplyMarkup(markupInLine);

                message.setText(text);
                return new TelegramSendMessage(message, String.valueOf(chatId));
            }
        }
        message.setChatId(chatId);
        message.setText(Consts.ERROR);
        return new TelegramSendMessage(message, String.valueOf(chatId));
    }
}
