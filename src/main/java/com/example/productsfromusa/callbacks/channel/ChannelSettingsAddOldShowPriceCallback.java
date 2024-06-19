package com.example.productsfromusa.callbacks.channel;

import com.example.productsfromusa.callbacks.CallbackHandler;
import com.example.productsfromusa.callbacks.CallbackType;
import com.example.productsfromusa.models.Channel;
import com.example.productsfromusa.models.TelegramMessage;
import com.example.productsfromusa.models.TelegramSendMessage;
import com.example.productsfromusa.models.User;
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
public class ChannelSettingsAddOldShowPriceCallback implements CallbackHandler {

    @Autowired
    public UserService userService;
    @Autowired
    public ChannelService channelService;

    @Override
    public TelegramMessage apply(Update update) {
        long chatId = update.getCallbackQuery().getMessage().getChatId();
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        String callbackData = update.getCallbackQuery().getData();

        String id = callbackData.substring(CallbackType.ADD_SHOW_OLD_PRICE.length());
        Channel channel = channelService.getChannelById(id);

        if (channel != null) {
            User user = userService.getUserByTelegramId(update.getCallbackQuery().getFrom().getId());
            if (user.getState().equals(States.CHANNEL_SETTINGS)) {
                user.setState(States.SHOW_OLD_PRICE);

                userService.saveUser(user);

                String text = EmojiParser.parseToUnicode(":heavy_dollar_sign: Отображать старую цену?");

                InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();
                List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();

                List<InlineKeyboardButton> rowInLine = new ArrayList<>();
                var channelButton = new InlineKeyboardButton();

                if (channel.getChannelSettings().isShowOldPrice()) {
                    channelButton.setText(EmojiParser.parseToUnicode("Да [:white_check_mark:]"));
                } else {
                    channelButton.setText(EmojiParser.parseToUnicode("Да [ ]"));
                }
                channelButton.setCallbackData(CallbackType.ADD_SHOW_OLD_PRICE2 + channel.getId());
                rowInLine.add(channelButton);

                channelButton = new InlineKeyboardButton();
                if (!channel.getChannelSettings().isShowOldPrice()) {
                    channelButton.setText(EmojiParser.parseToUnicode("Нет [:white_check_mark:]"));
                } else {
                    channelButton.setText(EmojiParser.parseToUnicode("Нет [ ]"));
                }
                channelButton.setCallbackData(CallbackType.ADD_SHOW_OLD_PRICE3 + channel.getId());
                rowInLine.add(channelButton);
                rowsInLine.add(rowInLine);

                rowInLine = new ArrayList<>();
                channelButton = new InlineKeyboardButton();
                channelButton.setText("Вернуться назад");
                channelButton.setCallbackData(CallbackType.CHANNEL_SETTINGS + channel.getId());
                rowInLine.add(channelButton);
                channelButton = new InlineKeyboardButton();
                rowsInLine.add(rowInLine);

                markupInLine.setKeyboard(rowsInLine);
                message.setReplyMarkup(markupInLine);

                message.setText(text);
                return new TelegramSendMessage(message, String.valueOf(chatId));
            }
        }
        return null;
    }
}
