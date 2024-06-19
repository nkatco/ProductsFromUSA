package com.example.productsfromusa.handlers;

import com.example.productsfromusa.commands.UserAddCommand;
import com.example.productsfromusa.models.Command;
import com.example.productsfromusa.models.TelegramMessage;
import com.example.productsfromusa.models.TelegramSendMessage;
import com.example.productsfromusa.states.StateData;
import com.example.productsfromusa.states.States;
import com.example.productsfromusa.utils.Consts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Map;

@Component
@Slf4j
public class PhoneHandler {

    private final Map<String, Command> commands;
    @Autowired
    private StateData stateData;

    public PhoneHandler(@Autowired UserAddCommand userAddCallback) {
        this.commands = Map.of(
                States.ADD_PHONE_USER, userAddCallback
        );
    }
    public TelegramMessage handlePhone(Update update) {
        String state = stateData.getCurrentState();
        long chatId = update.getMessage().getChatId();

        var commandHandler = commands.get(state);
        if (commandHandler != null) {
            return commandHandler.apply(update);
        } else {
            return new TelegramSendMessage(new SendMessage(String.valueOf(chatId), Consts.UNKNOWN_COMMAND), String.valueOf(chatId));
        }
    }
}
