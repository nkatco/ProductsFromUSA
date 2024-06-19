package com.example.productsfromusa.models;

import lombok.Data;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;

@Data
public class TelegramSendPhoto implements TelegramMessage {

    private SendPhoto sendPhoto;
    private String chatId;

    public TelegramSendPhoto(SendPhoto sendPhoto, String chatId) {
        this.sendPhoto = sendPhoto;
        sendPhoto.setChatId(chatId);
        this.chatId = chatId;
    }

    @Override
    public String getChatId() {
        return chatId;
    }

    @Override
    public void setChatId(String chatId) {
        this.chatId = chatId;
        if (sendPhoto != null) {
            this.sendPhoto.setChatId(chatId);
        }
    }
}
