package com.example.productsfromusa.models;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface Command {
    TelegramMessage apply(Update update);
}
