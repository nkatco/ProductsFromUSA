package com.example.productsfromusa.callbacks.anons;

import com.example.productsfromusa.DAO.StateDataDAO;
import com.example.productsfromusa.callbacks.CallbackHandler;
import com.example.productsfromusa.callbacks.CallbackType;
import com.example.productsfromusa.models.*;
import com.example.productsfromusa.services.data.AnonsService;
import com.example.productsfromusa.services.data.CategoryService;
import com.example.productsfromusa.services.data.UserService;
import com.example.productsfromusa.states.States;
import com.example.productsfromusa.utils.Consts;
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
public class ChangeAnonsCategoryCallback implements CallbackHandler {

    @Autowired
    public UserService userService;
    @Autowired
    public AnonsService anonsService;
    @Autowired
    public StateDataDAO stateDataDAO;
    @Autowired
    public CategoryService categoryService;

    @Override
    public TelegramMessage apply(Update update) {
        long chatId = update.getCallbackQuery().getMessage().getChatId();
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        User user = userService.getUserByTelegramId(update.getCallbackQuery().getFrom().getId());
        String callbackData = update.getCallbackQuery().getData();
        String id = callbackData.substring(CallbackType.CHANGE_ANONS_CATEGORY.length());
        Anons anons = anonsService.getAnonsById(id);
        if(anons != null) {
            if (user.getState().equals(States.ANONS_SETTINGS)) {
                stateDataDAO.removeStateDataByUserId(user.getId());
                stateDataDAO.setStateData(user, "anons", anons);
                String text = "Посты, назначенные сейчас будут очищены.\n\nВыберите новую категорию для анонса.";

                user.setState(States.ANONS_CHANGE_CATEGORY);
                userService.saveUser(user);

                InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();
                List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();

                List<InlineKeyboardButton> rowInLine = new ArrayList<>();
                var catButton = new InlineKeyboardButton();

                List<Category> categories = categoryService.getAll();

                if(categories.size() != 0) {
                    for(Category category : categories) {
                        catButton = new InlineKeyboardButton();
                        catButton.setText(category.getName());
                        catButton.setCallbackData(CallbackType.CHANGE_ANONS_CATEGORY2 + category.getId());
                        rowInLine.add(catButton);
                        rowsInLine.add(rowInLine);
                        rowInLine = new ArrayList<>();
                    }
                } else {
                    text = "Произошла какая-то ошибка, напишите /start и обратитесь к администратору.";
                }

                catButton = new InlineKeyboardButton();
                catButton.setText("Назад");
                catButton.setCallbackData(CallbackType.ANONS_BUTTON);
                rowInLine.add(catButton);
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
