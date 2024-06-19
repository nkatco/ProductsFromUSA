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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Component
public class ChannelSettingsRemoveWatermarkCallback implements CallbackHandler {

    @Autowired
    public UserService userService;
    @Autowired
    public ChannelService channelService;
    @Autowired
    public ChannelSettingsService channelSettingsService;
    @Autowired
    public ChannelSettingsCallback channelSettingsCallback;

    @Override
    public TelegramMessage apply(Update update) {
        long chatId = update.getCallbackQuery().getMessage().getChatId();
        String callbackData = update.getCallbackQuery().getData();

        String id = callbackData.substring(CallbackType.REMOVE_WATERMARK.length());
        Channel channel = channelService.getChannelById(id);

        try {
            if (channel != null) {
                User user = userService.getUserByTelegramId(update.getCallbackQuery().getFrom().getId());
                if(user.getState().equals(States.ADD_WATERMARK)) {
                    user.setState(States.CHANNEL_SETTINGS);

                    ChannelSettings channelSettings = channel.getChannelSettings();
                    if(channelSettings.getWatermarkImage() != null && channelSettings.getWatermarkImage().getPath() != null) {
                        File file = new File(channelSettings.getWatermarkImage().getPath());
                        file.delete();
                    }
                    channelSettings.setWatermarkImage(null);

                    channelSettingsService.saveChannelSettings(channelSettings);
                    userService.saveUser(user);

                    update.getCallbackQuery().setData(CallbackType.CHANNEL_SETTINGS + channel.getId());
                    return channelSettingsCallback.apply(update);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
