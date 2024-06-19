package com.example.productsfromusa.callbacks.anons;

import com.example.productsfromusa.DAO.StateDataDAO;
import com.example.productsfromusa.callbacks.CallbackHandler;
import com.example.productsfromusa.callbacks.CallbackType;
import com.example.productsfromusa.models.*;
import com.example.productsfromusa.services.data.AnonsService;
import com.example.productsfromusa.services.data.TokenService;
import com.example.productsfromusa.services.data.UserService;
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

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Component
public class AddAnons4Callback implements CallbackHandler {

    @Autowired
    public UserService userService;
    @Autowired
    public AnonsService anonsService;
    @Autowired
    public TokenService tokenService;
    @Autowired
    public StateDataDAO stateDataDAO;
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
            Token token = (Token) stateDataDAO.getStateDataByUserId("token_" + user.getId()).getData();
            String minute = callbackData.substring(CallbackType.CREATE_ANONS4.length());
            stateDataDAO.removeStateDataByUserId(user.getId());
            if(token != null && token.getAnons() > 0) {
                if (user.getState().equals(States.ADD_ANONS)) {
                    stateDataDAO.setStateData(user, "minute", minute);
                    String text = "Выбери количество постов в анонсе.";

                    user.setState(States.ADD_ANONS);
                    userService.saveUser(user);

                    InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();
                    List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();

                    List<InlineKeyboardButton> rowInLine = new ArrayList<>();
                    var postsButton = new InlineKeyboardButton();

                    postsButton = new InlineKeyboardButton();
                    postsButton.setText("1");
                    postsButton.setCallbackData(CallbackType.CREATE_ANONS5 + "1");
                    rowInLine.add(postsButton);

                    postsButton = new InlineKeyboardButton();
                    postsButton.setText("2");
                    postsButton.setCallbackData(CallbackType.CREATE_ANONS5 + "2");
                    rowInLine.add(postsButton);

                    postsButton = new InlineKeyboardButton();
                    postsButton.setText("3");
                    postsButton.setCallbackData(CallbackType.CREATE_ANONS5 + "3");
                    rowInLine.add(postsButton);

                    postsButton = new InlineKeyboardButton();
                    postsButton.setText("4");
                    postsButton.setCallbackData(CallbackType.CREATE_ANONS5 + "4");
                    rowInLine.add(postsButton);

                    postsButton = new InlineKeyboardButton();
                    postsButton.setText("5");
                    postsButton.setCallbackData(CallbackType.CREATE_ANONS5 + "5");
                    rowInLine.add(postsButton);

                    rowsInLine.add(rowInLine);

                    rowInLine = new ArrayList<>();
                    postsButton = new InlineKeyboardButton();
                    postsButton.setText("Главное меню");
                    postsButton.setCallbackData(CallbackType.MENU_BUTTON);
                    rowInLine.add(postsButton);
                    rowsInLine.add(rowInLine);

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
