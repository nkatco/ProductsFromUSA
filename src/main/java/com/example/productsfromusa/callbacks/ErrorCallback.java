package com.example.productsfromusa.callbacks;

import com.example.productsfromusa.models.TelegramMessage;
import com.example.productsfromusa.models.TelegramSendMessage;
import com.vdurmont.emoji.EmojiParser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Component
public class ErrorCallback implements CallbackHandler {

    @Override
    public TelegramMessage apply(Update update) {
        long chatId = update.getCallbackQuery().getMessage().getChatId();
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("По системным вопросам - к администратору.\n\nДля консультации - к ассистенту.");
        InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();

        List<InlineKeyboardButton> rowInLine = new ArrayList<>();

        rowInLine = new ArrayList<>();

        var assistentButton = new InlineKeyboardButton();

        assistentButton.setText(EmojiParser.parseToUnicode(":bust_in_silhouette:" + " Администратор"));
        assistentButton.setUrl("https://t.me/webcrabs");

        var administratorButton = new InlineKeyboardButton();

        administratorButton.setText(EmojiParser.parseToUnicode(":ok_woman:" + " Ассистент"));
        administratorButton.setUrl("https://t.me/webcrabs");

        rowInLine.add(assistentButton);
        rowInLine.add(administratorButton);

        rowsInLine.add(rowInLine);

        rowInLine = new ArrayList<>();

        var backButton = new InlineKeyboardButton();

        backButton.setText(EmojiParser.parseToUnicode(":house:" + " Вернуться назад"));
        backButton.setCallbackData(CallbackType.MENU_BUTTON);

        rowInLine.add(backButton);

        rowsInLine.add(rowInLine);

        markupInLine.setKeyboard(rowsInLine);

        message.setReplyMarkup(markupInLine);
        return new TelegramSendMessage(message, String.valueOf(chatId));
    }
}
