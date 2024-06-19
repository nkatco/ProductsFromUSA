package com.example.productsfromusa.callbacks.anons;

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
public class AddAnonsCallback implements CallbackHandler {

    @Autowired
    public UserService userService;
    @Autowired
    public AnonsService anonsService;
    @Autowired
    public TokenService tokenService;
    @Value("${bot.anons}")
    String anons;

    @Override
    public TelegramMessage apply(Update update) {
        long chatId = update.getCallbackQuery().getMessage().getChatId();
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        User user = userService.getUserByTelegramId(update.getCallbackQuery().getFrom().getId());
        List<Token> tokensList = tokenService.getTokensByUserId(user.getId());
        if(tokensList != null) {
            if (user.getState().equals(States.ANONS)) {
                String text = "Выбери доступный токен для создания анонса.";

                user.setState(States.ADD_ANONS);
                userService.saveUser(user);

                InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();
                List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();

                List<InlineKeyboardButton> rowInLine = new ArrayList<>();
                var tokenButton = new InlineKeyboardButton();

                if(tokensList.size() != 0 && tokensList.size() != 1) {
                    for (int i = 0; i < tokensList.size() / 2; i += 2) {
                        if(tokensList.get(i).getAnons() > 0) {
                            tokenButton = new InlineKeyboardButton();
                            tokenButton.setText(tokensList.get(i).getName());
                            tokenButton.setCallbackData(CallbackType.CREATE_ANONS2 + tokensList.get(i).getId());
                            rowInLine.add(tokenButton);
                            tokenButton = new InlineKeyboardButton();
                            tokenButton.setText(tokensList.get(i + 1).getName());
                            tokenButton.setCallbackData(CallbackType.CREATE_ANONS2 + tokensList.get(i).getId());
                            rowInLine.add(tokenButton);
                            rowsInLine.add(rowInLine);
                            rowInLine = new ArrayList<>();
                        }
                    }
                } else if (tokensList.size() == 1) {
                    for(Token token : tokensList) {
                        if(token.getAnons() > 0) {
                            tokenButton = new InlineKeyboardButton();
                            tokenButton.setText(token.getName());
                            tokenButton.setCallbackData(CallbackType.CREATE_ANONS2 + token.getId());
                            rowInLine.add(tokenButton);
                            rowsInLine.add(rowInLine);
                            rowInLine = new ArrayList<>();
                        }
                    }
                } else {
                    text = "У тебя нету ни одной подписки на канал (либо превышено кол-во анонсов). Перейди в один из каналов и купи подписку.";
                }

                tokenButton = new InlineKeyboardButton();
                tokenButton.setText("Главное меню");
                tokenButton.setCallbackData(CallbackType.MENU_BUTTON);
                rowInLine.add(tokenButton);
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
