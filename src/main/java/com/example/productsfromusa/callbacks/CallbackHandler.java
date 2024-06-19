package com.example.productsfromusa.callbacks;

import com.example.productsfromusa.models.TelegramMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface CallbackHandler {
    TelegramMessage apply(Update update);

}
