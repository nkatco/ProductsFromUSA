package com.example.productsfromusa.callbacks.anons;

import com.example.productsfromusa.DAO.StateDataDAO;
import com.example.productsfromusa.callbacks.CallbackHandler;
import com.example.productsfromusa.callbacks.CallbackType;
import com.example.productsfromusa.models.TelegramMessage;
import com.example.productsfromusa.models.TelegramSendMessage;
import com.example.productsfromusa.models.Token;
import com.example.productsfromusa.models.User;
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
public class AddAnons3Callback implements CallbackHandler {

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
        String hour = callbackData.substring(CallbackType.CREATE_ANONS3.length());
        System.out.println(hour + " | " + callbackData);
        try {
            Token token = (Token) stateDataDAO.getStateDataByUserId("token_" + user.getId()).getData();
            if(token != null && token.getAnons() > 0) {
                if (user.getState().equals(States.ADD_ANONS)) {
                    String text = "Выбери минуту, во сколько будет происходить публикация анонса.";

                    user.setState(States.ADD_ANONS);
                    userService.saveUser(user);
                    stateDataDAO.setStateData(user, "hour", hour);

                    InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();
                    List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();

                    List<InlineKeyboardButton> rowInLine = new ArrayList<>();
                    var timeButton = new InlineKeyboardButton();

                    rowInLine = new ArrayList<>();
                    timeButton = new InlineKeyboardButton();
                    timeButton.setText("10");
                    timeButton.setCallbackData(CallbackType.CREATE_ANONS4 + "10");
                    rowInLine.add(timeButton);

                    timeButton = new InlineKeyboardButton();
                    timeButton.setText("20");
                    timeButton.setCallbackData(CallbackType.CREATE_ANONS4 + "20");
                    rowInLine.add(timeButton);
                    rowsInLine.add(rowInLine);

                    rowInLine = new ArrayList<>();
                    timeButton = new InlineKeyboardButton();
                    timeButton.setText("30");
                    timeButton.setCallbackData(CallbackType.CREATE_ANONS4 + "30");
                    rowInLine.add(timeButton);

                    timeButton = new InlineKeyboardButton();
                    timeButton.setText("40");
                    timeButton.setCallbackData(CallbackType.CREATE_ANONS4 + "40");
                    rowInLine.add(timeButton);
                    rowsInLine.add(rowInLine);

                    rowInLine = new ArrayList<>();
                    timeButton = new InlineKeyboardButton();
                    timeButton.setText("50");
                    timeButton.setCallbackData(CallbackType.CREATE_ANONS4 + "50");
                    rowInLine.add(timeButton);

                    timeButton = new InlineKeyboardButton();
                    timeButton.setText("60");
                    timeButton.setCallbackData(CallbackType.CREATE_ANONS4 + "59");
                    rowInLine.add(timeButton);
                    rowsInLine.add(rowInLine);


                    // Конец часов
                    rowInLine = new ArrayList<>();
                    timeButton = new InlineKeyboardButton();
                    timeButton.setText("Главное меню");
                    timeButton.setCallbackData(CallbackType.MENU_BUTTON);
                    rowInLine.add(timeButton);
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
