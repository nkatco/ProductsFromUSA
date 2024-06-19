package com.example.productsfromusa.models;

import lombok.Data;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@Data
public class TelegramSendMessage implements TelegramMessage {

    private SendMessage sendMessage;
    private String chatId;

    public TelegramSendMessage(SendMessage sendMessage, String chatId) {
        this.sendMessage = sendMessage;
        this.chatId = chatId;
    }

    @Override
    public String getChatId() {
        return chatId;
    }

    @Override
    public void setChatId(String chatId) {
        this.chatId = chatId;
        if (sendMessage != null) {
            this.sendMessage.setChatId(chatId);
        }
    }
}
