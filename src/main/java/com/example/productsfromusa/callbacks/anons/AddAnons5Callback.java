package com.example.productsfromusa.callbacks.anons;

import com.example.productsfromusa.DAO.StateDataDAO;
import com.example.productsfromusa.callbacks.CallbackHandler;
import com.example.productsfromusa.callbacks.CallbackType;
import com.example.productsfromusa.models.*;
import com.example.productsfromusa.schedulers.AnonsScheduler;
import com.example.productsfromusa.services.data.*;
import com.example.productsfromusa.states.States;
import com.example.productsfromusa.utils.Consts;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@Component
public class AddAnons5Callback implements CallbackHandler {

    @Autowired
    public UserService userService;
    @Autowired
    public StateDataDAO stateDataDAO;
    @Autowired
    public CategoryService categoryService;
    @Value("${bot.anons}")
    String anons;

    @Override
    public TelegramMessage apply(Update update) {
        long chatId = update.getCallbackQuery().getMessage().getChatId();
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        User user = userService.getUserByTelegramId(update.getCallbackQuery().getFrom().getId());
        String callbackData = update.getCallbackQuery().getData();
        try {
            Token token1 = (Token) stateDataDAO.getStateDataByUserId("token_" + user.getId()).getData();
            String hour = (String) stateDataDAO.getStateDataByUserId("hour_" + user.getId()).getData();
            String minute = (String) stateDataDAO.getStateDataByUserId("minute_" + user.getId()).getData();
            int posts = Integer.parseInt(callbackData.substring(CallbackType.CREATE_ANONS5.length()));
            stateDataDAO.removeStateDataByUserId(user.getId());
            if(token1 != null && token1.getAnons() > 0) {
                if (user.getState().equals(States.ADD_ANONS)) {
                    stateDataDAO.setStateData(user, "minute", minute);
                    stateDataDAO.setStateData(user, "hour", hour);
                    stateDataDAO.setStateData(user, "token", token1);
                    stateDataDAO.setStateData(user, "posts", posts);

                    List<Category> categories = categoryService.getAll();

                    InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();
                    List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();

                    List<InlineKeyboardButton> rowInLine = new ArrayList<>();
                    var catButton = new InlineKeyboardButton();

                    String text = "Выбери категорию анонса.";
                    if(categories.size() != 0) {
                        for(Category category : categories) {
                            catButton = new InlineKeyboardButton();
                            catButton.setText(category.getName());
                            catButton.setCallbackData(CallbackType.CREATE_ANONS6 + category.getId());
                            rowInLine.add(catButton);
                            rowsInLine.add(rowInLine);
                            rowInLine = new ArrayList<>();
                        }
                    } else {
                        text = "Произошла какая-то ошибка, напишите /start и обратитесь к администратору.";
                    }

                    user.setState(States.ADD_ANONS);
                    userService.saveUser(user);

                    markupInLine.setKeyboard(rowsInLine);
                    message.setReplyMarkup(markupInLine);

                    message.setText(text);
                    return new TelegramSendMessage(message, String.valueOf(chatId));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        message.setChatId(chatId);
        message.setText(Consts.ERROR);
        return new TelegramSendMessage(message, String.valueOf(chatId));
    }
}
