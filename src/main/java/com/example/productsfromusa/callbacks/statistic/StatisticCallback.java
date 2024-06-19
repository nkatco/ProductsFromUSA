package com.example.productsfromusa.callbacks.statistic;

import com.example.productsfromusa.callbacks.CallbackHandler;
import com.example.productsfromusa.callbacks.CallbackType;
import com.example.productsfromusa.models.*;
import com.example.productsfromusa.services.data.ChannelService;
import com.example.productsfromusa.services.data.UserService;
import com.example.productsfromusa.states.States;
import com.vdurmont.emoji.EmojiParser;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Component
public class StatisticCallback implements CallbackHandler {

    @Autowired
    private UserService userService;
    @Autowired
    private ChannelService channelService;

    @Override
    public TelegramMessage apply(Update update) {
        long chatId = update.getCallbackQuery().getMessage().getChatId();
        long userId = update.getCallbackQuery().getFrom().getId();
        SendMessage message = new SendMessage();

        User user = userService.getUserByTelegramId(userId);
        user.setState(States.STATISTIC);
        userService.saveUser(user);

        List<Channel> channels = channelService.getChannelsByUserId(user.getId());

        if (channels.size() != 0) {
            InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();
            List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();

            List<InlineKeyboardButton> rowInLine = new ArrayList<>();
            var statButton = new InlineKeyboardButton();

            message.setText("Выбери канал, по которому ты хочешь посмотреть статистику.");

            for (Channel channel : channels) {
                statButton = new InlineKeyboardButton();
                statButton.setText(channel.getName());
                statButton.setCallbackData(CallbackType.INFO_STATISTIC + channel.getId());
                rowInLine.add(statButton);
                rowsInLine.add(rowInLine);
                rowInLine = new ArrayList<>();
            }

            statButton = new InlineKeyboardButton();
            statButton.setText(EmojiParser.parseToUnicode(":arrow_backward:" + " Назад"));
            statButton.setCallbackData(CallbackType.MENU_BUTTON);
            rowInLine.add(statButton);
            rowsInLine.add(rowInLine);
            rowInLine = new ArrayList<>();

            markupInLine.setKeyboard(rowsInLine);
            message.setReplyMarkup(markupInLine);

            return new TelegramSendMessage(message, String.valueOf(chatId));
        }
        return null;
    }
}
