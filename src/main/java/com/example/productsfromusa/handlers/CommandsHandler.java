package com.example.productsfromusa.handlers;

import com.example.productsfromusa.commands.StartCommand;
import com.example.productsfromusa.models.Command;
import com.example.productsfromusa.models.TelegramMessage;
import com.example.productsfromusa.models.TelegramSendMessage;
import com.example.productsfromusa.utils.Consts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Map;

@Component
@Slf4j
public class CommandsHandler {

    private final Map<String, Command> commands;

    public CommandsHandler(@Autowired StartCommand startCommand) {
        this.commands = Map.of(
                "/start", startCommand
        );
    }

    public TelegramMessage handleCommands(Update update) {
        String messageText = update.getMessage().getText();
        String command = messageText.split(" ")[0];
        long chatId = update.getMessage().getChatId();

        var commandHandler = commands.get(command);
        if (commandHandler != null) {
            return commandHandler.apply(update);
        } else {
            return new TelegramSendMessage(new SendMessage(String.valueOf(chatId), Consts.UNKNOWN_COMMAND), String.valueOf(chatId));
        }
    }

}
