package com.example.productsfromusa.callbacks.channel;

import com.example.productsfromusa.callbacks.CallbackHandler;
import com.example.productsfromusa.callbacks.CallbackType;
import com.example.productsfromusa.models.*;
import com.example.productsfromusa.services.data.ChannelService;
import com.example.productsfromusa.services.data.ChannelSettingsService;
import com.example.productsfromusa.services.data.UserService;
import com.example.productsfromusa.states.States;
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
public class ChannelSettingsSetWatermarkCallback implements CallbackHandler {

    @Autowired
    public UserService userService;
    @Autowired
    public ChannelService channelService;
    @Autowired
    public ChannelSettingsService channelSettingsService;
    @Autowired
    public ChannelSettingsAddWatermarkCallback channelSettingsAddWatermarkCallback;

    @Override
    public TelegramMessage apply(Update update) {
        long chatId = update.getCallbackQuery().getMessage().getChatId();
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        String callbackData = update.getCallbackQuery().getData();

        String id = "";
        if(callbackData.startsWith(CallbackType.CENTER_WATERMARK)) {
            id = callbackData.substring(CallbackType.CENTER_WATERMARK.length());
        } else if(callbackData.startsWith(CallbackType.CORNER_WATERMARK)) {
            id = callbackData.substring(CallbackType.CORNER_WATERMARK.length());
        } else if(callbackData.startsWith(CallbackType.FULL_WATERMARK)) {
            id = callbackData.substring(CallbackType.FULL_WATERMARK.length());
        } else if(callbackData.startsWith(CallbackType.LIGHT_WATERMARK)) {
            id = callbackData.substring(CallbackType.LIGHT_WATERMARK.length());
        } else if(callbackData.startsWith(CallbackType.MEDIUM_WATERMARK)) {
            id = callbackData.substring(CallbackType.MEDIUM_WATERMARK.length());
        } else if(callbackData.startsWith(CallbackType.HARD_WATERMARK)) {
            id = callbackData.substring(CallbackType.HARD_WATERMARK.length());
        } else if(callbackData.startsWith(CallbackType.LITTLE_WATERMARK)) {
            id = callbackData.substring(CallbackType.LITTLE_WATERMARK.length());
        } else if(callbackData.startsWith(CallbackType.SMALL_WATERMARK)) {
            id = callbackData.substring(CallbackType.SMALL_WATERMARK.length());
        } else if(callbackData.startsWith(CallbackType.BIG_WATERMARK)) {
            id = callbackData.substring(CallbackType.BIG_WATERMARK.length());
        }
        Channel channel = channelService.getChannelById(id);

        if (channel != null) {
            User user = userService.getUserByTelegramId(update.getCallbackQuery().getFrom().getId());
            if(user.getState().equals(States.ADD_WATERMARK)) {
                user.setState(States.CHANNEL_SETTINGS);

                ChannelSettings channelSettings = channel.getChannelSettings();

                if(callbackData.startsWith(CallbackType.CENTER_WATERMARK)) {
                    channelSettings.getWatermarkImage().setMode("center");
                } else if(callbackData.startsWith(CallbackType.CORNER_WATERMARK)) {
                    channelSettings.getWatermarkImage().setMode("corner");
                } else if(callbackData.startsWith(CallbackType.FULL_WATERMARK)) {
                    channelSettings.getWatermarkImage().setMode("full");
                } else if(callbackData.startsWith(CallbackType.LIGHT_WATERMARK)) {
                    channelSettings.getWatermarkImage().setAlpha(0.3f);
                } else if(callbackData.startsWith(CallbackType.MEDIUM_WATERMARK)) {
                    channelSettings.getWatermarkImage().setAlpha(0.5f);
                } else if(callbackData.startsWith(CallbackType.HARD_WATERMARK)) {
                    channelSettings.getWatermarkImage().setAlpha(0.7f);
                } else if(callbackData.startsWith(CallbackType.LITTLE_WATERMARK)) {
                    channelSettings.getWatermarkImage().setSize(100);
                } else if(callbackData.startsWith(CallbackType.SMALL_WATERMARK)) {
                    channelSettings.getWatermarkImage().setSize(175);
                } else if(callbackData.startsWith(CallbackType.BIG_WATERMARK)) {
                    channelSettings.getWatermarkImage().setSize(250);
                }

                channelSettingsService.saveChannelSettings(channelSettings);
                userService.saveUser(user);

                String text = "Настройки успешно применены.";

                InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();
                List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();

                List<InlineKeyboardButton> rowInLine = new ArrayList<>();
                var channelButton = new InlineKeyboardButton();

                rowInLine = new ArrayList<>();
                channelButton = new InlineKeyboardButton();
                channelButton.setText("К настройкам");
                channelButton.setCallbackData(CallbackType.CHANNEL_SETTINGS + channel.getId());
                rowInLine.add(channelButton);
                channelButton = new InlineKeyboardButton();
                rowsInLine.add(rowInLine);

                markupInLine.setKeyboard(rowsInLine);
                message.setReplyMarkup(markupInLine);
                message.setChatId(chatId);
                message.setText(text);

                update.getCallbackQuery().setData(CallbackType.ADD_WATERMARK + id);
                return channelSettingsAddWatermarkCallback.apply(update);
            }
        }
        return null;
    }
}
