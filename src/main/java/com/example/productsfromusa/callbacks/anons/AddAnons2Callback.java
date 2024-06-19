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
public class AddAnons2Callback implements CallbackHandler {

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
        String id = callbackData.substring(CallbackType.CREATE_ANONS2.length());
        Token token = tokenService.getTokenById(id);
        if(token != null && token.getAnons() > 0) {
            if (user.getState().equals(States.ADD_ANONS)) {
                String text = "Выбери час, во сколько будет происходить публикация анонса.";

                user.setState(States.ADD_ANONS);
                userService.saveUser(user);
                stateDataDAO.setStateData(user, "token", token);

                InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();
                List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();

                List<InlineKeyboardButton> rowInLine = new ArrayList<>();
                var timeButton = new InlineKeyboardButton();

                // 1 линия
                timeButton = new InlineKeyboardButton();
                timeButton.setText("1");
                timeButton.setCallbackData(CallbackType.CREATE_ANONS3 + "01");
                rowInLine.add(timeButton);

                timeButton = new InlineKeyboardButton();
                timeButton.setText("2");
                timeButton.setCallbackData(CallbackType.CREATE_ANONS3 + "02");
                rowInLine.add(timeButton);

                timeButton = new InlineKeyboardButton();
                timeButton.setText("3");
                timeButton.setCallbackData(CallbackType.CREATE_ANONS3 + "03");
                rowInLine.add(timeButton);

                timeButton = new InlineKeyboardButton();
                timeButton.setText("4");
                timeButton.setCallbackData(CallbackType.CREATE_ANONS3 + "04");
                rowInLine.add(timeButton);

                timeButton = new InlineKeyboardButton();
                timeButton.setText("5");
                timeButton.setCallbackData(CallbackType.CREATE_ANONS3 + "05");
                rowInLine.add(timeButton);

                timeButton = new InlineKeyboardButton();
                timeButton.setText("6");
                timeButton.setCallbackData(CallbackType.CREATE_ANONS3 + "06");
                rowInLine.add(timeButton);

                timeButton = new InlineKeyboardButton();
                timeButton.setText("7");
                timeButton.setCallbackData(CallbackType.CREATE_ANONS3 + "07");
                rowInLine.add(timeButton);

                timeButton = new InlineKeyboardButton();
                timeButton.setText("8");
                timeButton.setCallbackData(CallbackType.CREATE_ANONS3 + "08");
                rowInLine.add(timeButton);
                rowsInLine.add(rowInLine);


                // 2 линия
                rowInLine = new ArrayList<>();
                timeButton = new InlineKeyboardButton();
                timeButton.setText("23");
                timeButton.setCallbackData(CallbackType.CREATE_ANONS3 + "23");
                rowInLine.add(timeButton);

                for (int i = 0; i < 6; i++) {
                    timeButton = new InlineKeyboardButton();
                    timeButton.setText("ㅤ");
                    timeButton.setCallbackData(" ");
                    rowInLine.add(timeButton);
                }

                timeButton = new InlineKeyboardButton();
                timeButton.setText("9");
                timeButton.setCallbackData(CallbackType.CREATE_ANONS3 + "09");
                rowInLine.add(timeButton);

                rowsInLine.add(rowInLine);

                // 3 линия
                rowInLine = new ArrayList<>();
                timeButton = new InlineKeyboardButton();
                timeButton.setText("22");
                timeButton.setCallbackData(CallbackType.CREATE_ANONS3 + "22");
                rowInLine.add(timeButton);

                for (int i = 0; i < 6; i++) {
                    timeButton = new InlineKeyboardButton();
                    timeButton.setText("ㅤ");
                    timeButton.setCallbackData(" ");
                    rowInLine.add(timeButton);
                }

                timeButton = new InlineKeyboardButton();
                timeButton.setText("10");
                timeButton.setCallbackData(CallbackType.CREATE_ANONS3 + "10");
                rowInLine.add(timeButton);

                rowsInLine.add(rowInLine);

                // 4 линия
                rowInLine = new ArrayList<>();
                timeButton = new InlineKeyboardButton();
                timeButton.setText("21");
                timeButton.setCallbackData(CallbackType.CREATE_ANONS3 + "21");
                rowInLine.add(timeButton);

                for (int i = 0; i < 6; i++) {
                    timeButton = new InlineKeyboardButton();
                    timeButton.setText("ㅤ");
                    timeButton.setCallbackData(" ");
                    rowInLine.add(timeButton);
                }

                timeButton = new InlineKeyboardButton();
                timeButton.setText("11");
                timeButton.setCallbackData(CallbackType.CREATE_ANONS3 + "11");
                rowInLine.add(timeButton);

                rowsInLine.add(rowInLine);

                // 5 линия
                rowInLine = new ArrayList<>();
                timeButton = new InlineKeyboardButton();
                timeButton.setText("20");
                timeButton.setCallbackData(CallbackType.CREATE_ANONS3 + "20");
                rowInLine.add(timeButton);

                for (int i = 0; i < 6; i++) {
                    timeButton = new InlineKeyboardButton();
                    timeButton.setText("ㅤ");
                    timeButton.setCallbackData(" ");
                    rowInLine.add(timeButton);
                }

                timeButton = new InlineKeyboardButton();
                timeButton.setText("12");
                timeButton.setCallbackData(CallbackType.CREATE_ANONS3 + "12");
                rowInLine.add(timeButton);

                rowsInLine.add(rowInLine);

                // 6 линия
                rowInLine = new ArrayList<>();

                timeButton = new InlineKeyboardButton();
                timeButton.setText("19");
                timeButton.setCallbackData(CallbackType.CREATE_ANONS3 + "19");
                rowInLine.add(timeButton);

                timeButton = new InlineKeyboardButton();
                timeButton.setText("18");
                timeButton.setCallbackData(CallbackType.CREATE_ANONS3 + "18");
                rowInLine.add(timeButton);

                timeButton = new InlineKeyboardButton();
                timeButton.setText("17");
                timeButton.setCallbackData(CallbackType.CREATE_ANONS3 + "17");
                rowInLine.add(timeButton);

                timeButton = new InlineKeyboardButton();
                timeButton.setText("16");
                timeButton.setCallbackData(CallbackType.CREATE_ANONS3 + "16");
                rowInLine.add(timeButton);

                timeButton = new InlineKeyboardButton();
                timeButton.setText("15");
                timeButton.setCallbackData(CallbackType.CREATE_ANONS3 + "15");
                rowInLine.add(timeButton);

                timeButton = new InlineKeyboardButton();
                timeButton.setText("14");
                timeButton.setCallbackData(CallbackType.CREATE_ANONS3 + "14");
                rowInLine.add(timeButton);

                timeButton = new InlineKeyboardButton();
                timeButton.setText("13");
                timeButton.setCallbackData(CallbackType.CREATE_ANONS3 + "13");
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
        message.setChatId(chatId);
        message.setText(Consts.ERROR);
        return new TelegramSendMessage(message, String.valueOf(chatId));
    }
}
