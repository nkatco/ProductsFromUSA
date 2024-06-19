package com.example.productsfromusa.callbacks.channel;

import com.example.productsfromusa.callbacks.CallbackHandler;
import com.example.productsfromusa.callbacks.CallbackType;
import com.example.productsfromusa.models.*;
import com.example.productsfromusa.services.data.ChannelService;
import com.example.productsfromusa.services.data.PreChannelService;
import com.example.productsfromusa.services.data.UserService;
import com.example.productsfromusa.states.States;
import com.example.productsfromusa.utils.Consts;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class AddChannelsCallback implements CallbackHandler {

    private static final Logger logger = LoggerFactory.getLogger(AddChannelsCallback.class);

    @Autowired
    public PreChannelService preChannelService;
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
        User user = userService.getUserByTelegramId(update.getCallbackQuery().getFrom().getId());

        if (user.getState().equals(States.ADD_CHANNEL_STATE)) {
            String text = "Канал был успешно добавлен.";
            String id = callbackData.substring(CallbackType.ADD_CHANNELS.length() - 1);
            PreChannel preChannel = preChannelService.getPreChannelById(id);

            logger.info("Adding new channel with preChannel ID {}", id);

            Channel newChannel = new Channel();
            newChannel.setName(preChannel.getName());
            newChannel.setActive(true);
            newChannel.setTelegramId(preChannel.getTelegramId());
            newChannel.setUser(user);
            newChannel.setChatId(preChannel.getChatId());

            ChannelSettings channelSettings = new ChannelSettings();
            channelSettings.setReduction(1);
            newChannel.setChannelSettings(channelSettings);

            channelService.saveChannel(newChannel);

            logger.info("New channel {} saved for user {}", newChannel.getName(), user.getId());

            user.setState(States.BASIC_STATE);
            user.getChannels().add(newChannel);
            userService.saveUser(user);

            text = "Здесь отображаются все ваши каналы.\nДля настройки постов в канале нажмите на него.\n\nЧтобы добавить новый канал, выберите соответствующий пункт ниже.";

            List<Channel> channels = channelService.getChannelsByUserId(user.getId());

            InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();
            List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();

            List<InlineKeyboardButton> rowInLine = new ArrayList<>();
            InlineKeyboardButton channelButton;

            if (channels.size() != 0 && channels.size() != 1) {
                for (int i = 0; i < channels.size() / 2; i += 2) {
                    channelButton = new InlineKeyboardButton();
                    channelButton.setText(channels.get(i).getName());
                    channelButton.setCallbackData(CallbackType.CHANNEL_SETTINGS + channels.get(i).getId());
                    rowInLine.add(channelButton);
                    channelButton = new InlineKeyboardButton();
                    channelButton.setText(channels.get(i + 1).getName());
                    channelButton.setCallbackData(CallbackType.CHANNEL_SETTINGS + channels.get(i).getId());
                    rowInLine.add(channelButton);
                    rowsInLine.add(rowInLine);
                    rowInLine = new ArrayList<>();
                }
            } else if (channels.size() == 1) {
                for (Channel channel : channels) {
                    channelButton = new InlineKeyboardButton();
                    channelButton.setText(channel.getName());
                    channelButton.setCallbackData(CallbackType.CHANNEL_SETTINGS + channel.getId());
                    rowInLine.add(channelButton);
                    rowsInLine.add(rowInLine);
                    rowInLine = new ArrayList<>();
                }
            }
            channelButton = new InlineKeyboardButton();
            channelButton.setText("Главное меню");
            channelButton.setCallbackData(CallbackType.MENU_BUTTON);
            rowInLine.add(channelButton);
            channelButton = new InlineKeyboardButton();
            channelButton.setText("Добавить канал");
            channelButton.setCallbackData(CallbackType.ADD_CHANNELS);
            rowInLine.add(channelButton);
            rowsInLine.add(rowInLine);

            markupInLine.setKeyboard(rowsInLine);
            message.setReplyMarkup(markupInLine);

            message.setText(text);
            return new TelegramSendMessage(message, String.valueOf(chatId));
        }

        message.setChatId(chatId);
        message.setText(Consts.ERROR);
        logger.warn("User {} in wrong state: {}", user.getId(), user.getState());
        return new TelegramSendMessage(message, String.valueOf(chatId));
    }
}