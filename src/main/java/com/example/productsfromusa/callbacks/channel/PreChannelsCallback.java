package com.example.productsfromusa.callbacks.channel;

import com.example.productsfromusa.callbacks.CallbackHandler;
import com.example.productsfromusa.callbacks.CallbackType;
import com.example.productsfromusa.models.*;
import com.example.productsfromusa.services.data.ChannelService;
import com.example.productsfromusa.services.data.PreChannelService;
import com.example.productsfromusa.services.data.UserService;
import com.example.productsfromusa.states.States;
import com.example.productsfromusa.utils.TelegramBotUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMember;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Component
public class PreChannelsCallback implements CallbackHandler {

    @Autowired
    public PreChannelService preChannelService;
    @Lazy
    @Autowired
    public TelegramBotUtils telegramBotUtils;
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
        user.setState(States.ADD_CHANNEL_STATE);

        userService.saveUser(user);

        String text = "Для того, чтобы добавить свой канал, нажми на него ниже.\n\nЕсли нужный канал отстутвует, значит вы не добавили бота или добавили его без статуса администратора.";

        List<PreChannel> preChannels = preChannelService.getAll();

        List<PreChannel> preChannelList = new ArrayList<>();
        for(PreChannel preChannel : preChannels) {
            List<ChatMember> members = telegramBotUtils.getChatAdministrators(preChannel.getTelegramId());
            if(members != null) {
                for (ChatMember member : members) {
                    if(member.getUser().getId().equals(update.getCallbackQuery().getFrom().getId())) {
                        List<Channel> channels = channelService.getAllChannelsByTelegramId(preChannel.getTelegramId());
                        boolean a = false;
                        for (Channel channel : channels) {
                            if(channel.getTelegramId() == (preChannel.getTelegramId())) {
                                a = true;
                            }
                        }
                        if (!a) {
                            preChannelList.add(preChannel);
                        }
                    }
                }
            }
        }
        InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();

        List<InlineKeyboardButton> rowInLine = new ArrayList<>();
        var preChannelButton = new InlineKeyboardButton();

        if(preChannelList.size() != 0 && preChannelList.size() != 1) {
            for (int i = 0; i < preChannelList.size() / 2; i += 2) {
                preChannelButton = new InlineKeyboardButton();
                preChannelButton.setText(preChannelList.get(i).getName());
                preChannelButton.setCallbackData(CallbackType.CHANNEL_ADD + preChannelList.get(i).getId());
                rowInLine.add(preChannelButton);
                preChannelButton = new InlineKeyboardButton();
                preChannelButton.setText(preChannelList.get(i + 1).getName());
                preChannelButton.setCallbackData(CallbackType.CHANNEL_ADD + preChannelList.get(i).getId());
                rowInLine.add(preChannelButton);
                rowsInLine.add(rowInLine);
                rowInLine = new ArrayList<>();
            }
        } else if (preChannelList.size() == 1) {
            for(PreChannel preChannel : preChannelList) {
                preChannelButton = new InlineKeyboardButton();
                preChannelButton.setText(preChannel.getName());
                preChannelButton.setCallbackData(CallbackType.CHANNEL_ADD + preChannel.getId());
                rowInLine.add(preChannelButton);
                rowsInLine.add(rowInLine);
                rowInLine = new ArrayList<>();
            }
        }
        preChannelButton = new InlineKeyboardButton();
        preChannelButton.setText("Назад");
        preChannelButton.setCallbackData(CallbackType.CHANNELS_BUTTON);
        rowInLine.add(preChannelButton);
        rowsInLine.add(rowInLine);

        markupInLine.setKeyboard(rowsInLine);
        message.setReplyMarkup(markupInLine);

        message.setText(text);
        return new TelegramSendMessage(message, String.valueOf(chatId));
    }
}
