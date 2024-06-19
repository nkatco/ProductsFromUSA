package com.example.productsfromusa.callbacks.channel;

import com.example.productsfromusa.DAO.StateDataDAO;
import com.example.productsfromusa.callbacks.CallbackHandler;
import com.example.productsfromusa.callbacks.CallbackType;
import com.example.productsfromusa.models.*;
import com.example.productsfromusa.services.data.ChannelService;
import com.example.productsfromusa.services.data.ChannelSettingsService;
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
public class ChannelSettingsSetFinalReductionCallback implements CallbackHandler {

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

        String id = "";
        if(callbackData.startsWith(CallbackType.SET_REDUCTION_10)) {
            id = callbackData.substring(CallbackType.SET_REDUCTION_10.length());
        } else if (callbackData.startsWith(CallbackType.SET_REDUCTION_100)) {
            id = callbackData.substring(CallbackType.SET_REDUCTION_100.length());
        } else if (callbackData.startsWith(CallbackType.SET_REDUCTION_9)) {
            id = callbackData.substring(CallbackType.SET_REDUCTION_9.length());
        } else if (callbackData.startsWith(CallbackType.SET_REDUCTION_99)) {
            id = callbackData.substring(CallbackType.SET_REDUCTION_99.length());
        }
        Channel channel = channelService.getChannelById(id);

        if (channel != null) {
            User user = userService.getUserByTelegramId(update.getCallbackQuery().getFrom().getId());
            if(user.getState().equals(States.SET_REDUCTION)) {
                user.setState(States.CHANNEL_SETTINGS);

                ChannelSettings channelSettings = channel.getChannelSettings();

                if(callbackData.startsWith(CallbackType.SET_REDUCTION_10)) {
                    channelSettings.setReduction(10);
                } else if (callbackData.startsWith(CallbackType.SET_REDUCTION_100)) {
                    channelSettings.setReduction(100);
                } else if (callbackData.startsWith(CallbackType.SET_REDUCTION_9)) {
                    channelSettings.setReduction(9);
                } else if (callbackData.startsWith(CallbackType.SET_REDUCTION_99)) {
                    channelSettings.setReduction(99);
                }
                channelSettingsService.saveChannelSettings(channelSettings);

                userService.saveUser(user);

                update.getCallbackQuery().setData(CallbackType.CHANNEL_SETTINGS + channel.getId());
                return channelSettingsCallback.apply(update);
            }
        }
        return null;
    }
}
