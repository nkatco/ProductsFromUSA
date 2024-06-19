package com.example.productsfromusa.callbacks;

import com.example.productsfromusa.commands.StartCommand;
import com.example.productsfromusa.models.TelegramMessage;
import com.example.productsfromusa.models.TelegramSendMessage;
import com.example.productsfromusa.models.TelegramSendPhoto;
import com.example.productsfromusa.models.User;
import com.example.productsfromusa.services.data.UserService;
import com.example.productsfromusa.states.States;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Update;

@RequiredArgsConstructor
@Component
public class StartCallback implements CallbackHandler {

    @Autowired
    private UserService userService;

    @Override
    public TelegramMessage apply(Update update) {
        long chatId = update.getCallbackQuery().getMessage().getChatId();
        long userId = update.getCallbackQuery().getFrom().getId();
        SendPhoto message = StartCommand.getStartCommand();
        message.setChatId(chatId);
        User user = userService.getUserByTelegramId(userId);
        user.setState(States.BASIC_STATE);
        userService.saveUser(user);
        return new TelegramSendPhoto(message, String.valueOf(chatId));
    }
}
