package com.example.productsfromusa.callbacks.channel;

import com.example.productsfromusa.DAO.StateDataDAO;
import com.example.productsfromusa.callbacks.CallbackHandler;
import com.example.productsfromusa.callbacks.CallbackType;
import com.example.productsfromusa.models.*;
import com.example.productsfromusa.services.data.ChannelService;
import com.example.productsfromusa.services.data.ChannelSettingsService;
import com.example.productsfromusa.services.data.USDService;
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
public class ChannelSettingsSetReductionCallback implements CallbackHandler {

    @Autowired
    public UserService userService;
    @Autowired
    public ChannelService channelService;
    @Autowired
    public StateDataDAO stateDataDAO;
    @Autowired
    public ChannelSettingsService channelSettingsService;

    @Override
    public TelegramMessage apply(Update update) {
        long chatId = update.getCallbackQuery().getMessage().getChatId();
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        String callbackData = update.getCallbackQuery().getData();

        String id = callbackData.substring(CallbackType.SET_REDUCTION.length());
        Channel channel = channelService.getChannelById(id);

        if (channel != null) {
            User user = userService.getUserByTelegramId(update.getCallbackQuery().getFrom().getId());
            if (user.getState().equals(States.CHANNEL_SETTINGS)) {
                user.setState(States.SET_REDUCTION);

                userService.saveUser(user);

                ChannelSettings channelSettings = channel.getChannelSettings();
                if(channelSettings.getReduction() == 0) {
                    channelSettings.setReduction(10);
                }
                channelSettingsService.saveChannelSettings(channelSettings);

                String text = EmojiParser.parseToUnicode("Здесь можно установить сокращение цены.\n" +
                        "10 - до десятых\n" +
                        "100 - до сотых\n" +
                        "9 - почти до десятых\n" +
                        "99 - почти до сотых");

                InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();
                List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();

                List<InlineKeyboardButton> rowInLine = new ArrayList<>();
                var channelButton = new InlineKeyboardButton();

                rowInLine = new ArrayList<>();
                channelButton = new InlineKeyboardButton();
                if (channelSettings.getReduction() == 10) {
                    channelButton.setText(EmojiParser.parseToUnicode("10 :white_check_mark:"));
                } else {
                    channelButton.setText("10");
                }
                channelButton.setCallbackData(CallbackType.SET_REDUCTION_10 + channel.getId());
                rowInLine.add(channelButton);

                channelButton = new InlineKeyboardButton();
                if (channelSettings.getReduction() == 100) {
                    channelButton.setText(EmojiParser.parseToUnicode("100 :white_check_mark:"));
                } else {
                    channelButton.setText("100");
                }
                channelButton.setCallbackData(CallbackType.SET_REDUCTION_100 + channel.getId());
                rowInLine.add(channelButton);

                channelButton = new InlineKeyboardButton();
                if (channelSettings.getReduction() == 9) {
                    channelButton.setText(EmojiParser.parseToUnicode("9 :white_check_mark:"));
                } else {
                    channelButton.setText("9");
                }
                channelButton.setCallbackData(CallbackType.SET_REDUCTION_9 + channel.getId());
                rowInLine.add(channelButton);

                channelButton = new InlineKeyboardButton();
                if (channelSettings.getReduction() == 99) {
                    channelButton.setText(EmojiParser.parseToUnicode("99 :white_check_mark:"));
                } else {
                    channelButton.setText("99");
                }
                channelButton.setCallbackData(CallbackType.SET_REDUCTION_99 + channel.getId());
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
