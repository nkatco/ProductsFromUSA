package com.example.productsfromusa.callbacks.channel;

import com.example.productsfromusa.callbacks.CallbackHandler;
import com.example.productsfromusa.callbacks.CallbackType;
import com.example.productsfromusa.models.Channel;
import com.example.productsfromusa.models.TelegramMessage;
import com.example.productsfromusa.models.TelegramSendMessage;
import com.example.productsfromusa.models.User;
import com.example.productsfromusa.services.data.ChannelService;
import com.example.productsfromusa.services.data.PreChannelService;
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
public class ChannelsCallback implements CallbackHandler {

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

        User user = userService.getUserByTelegramId(update.getCallbackQuery().getFrom().getId());
        user.setState(States.BASIC_STATE);

        userService.saveUser(user);

        String text = "Здесь отображаются все ваши каналы.\nДля настройки постов в канале нажмите на него.\n\nЧтобы добавить новый канал, выберите соответствующий пункт ниже.";

        List<Channel> channels = channelService.getChannelsByUserId(user.getId());

        InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();

        List<InlineKeyboardButton> rowInLine = new ArrayList<>();
        var channelButton = new InlineKeyboardButton();

        for(Channel channel : channels) {
            channelButton = new InlineKeyboardButton();
            channelButton.setText(channel.getName());
            channelButton.setCallbackData(CallbackType.CHANNEL_SETTINGS + channel.getId());
            rowInLine.add(channelButton);
            rowsInLine.add(rowInLine);
            rowInLine = new ArrayList<>();
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
        rowInLine = new ArrayList<>();

        markupInLine.setKeyboard(rowsInLine);
        message.setReplyMarkup(markupInLine);

        message.setText(text);
        return new TelegramSendMessage(message, String.valueOf(chatId));
    }
}
