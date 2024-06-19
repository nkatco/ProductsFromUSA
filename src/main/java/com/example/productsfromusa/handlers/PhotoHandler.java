package com.example.productsfromusa.handlers;

import com.example.productsfromusa.commands.*;
import com.example.productsfromusa.models.Command;
import com.example.productsfromusa.models.TelegramMessage;
import com.example.productsfromusa.models.TelegramSendMessage;
import com.example.productsfromusa.models.User;
import com.example.productsfromusa.services.data.UserService;
import com.example.productsfromusa.states.States;
import com.example.productsfromusa.utils.Consts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class PhotoHandler {

    @Autowired
    private UserService userService;
    private final Map<String, Command> states;

    public PhotoHandler(@Autowired WatermarkCommand watermarkCommand) {
        this.states = new HashMap<>();
        states.put(States.ADD_WATERMARK, watermarkCommand);
    }

    public TelegramMessage handleCommands(Update update) {
        String messageText = update.getMessage().getText();
        String chatId = update.getMessage().getChatId().toString();

        User user = userService.getUserByTelegramId(update.getMessage().getFrom().getId());

        var commandHandler = states.get(user.getState());
        if (commandHandler != null) {
            return commandHandler.apply(update);
        } else {
            return new TelegramSendMessage(new SendMessage(chatId, Consts.CANT_UNDERSTAND), chatId);
        }
    }
}
