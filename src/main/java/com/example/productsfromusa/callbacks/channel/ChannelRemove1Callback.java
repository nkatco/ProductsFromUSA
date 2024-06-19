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
public class ChannelRemove1Callback implements CallbackHandler {

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

        String id = callbackData.substring(CallbackType.CHANNEL_SETTINGS.length() - 1);
        Channel channel = channelService.getChannelById(id);

        if (channel != null) {
            User user = userService.getUserByTelegramId(update.getCallbackQuery().getFrom().getId());
            user.setState(States.REMOVE_CHANNEL);

            userService.saveUser(user);

            String text = "Если удалить канал, подписка на него слетит и средства не будут возвращены.\n\nВы уверены, что хотите удалить канал?";

            InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();
            List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();

            List<InlineKeyboardButton> rowInLine = new ArrayList<>();
            var channelButton = new InlineKeyboardButton();

            channelButton.setText("Да, удалить канал.");
            channelButton.setCallbackData(CallbackType.CHANNEL_REMOVE2 + channel.getId());
            rowInLine.add(channelButton);
            rowsInLine.add(rowInLine);
            rowInLine = new ArrayList<>();
            channelButton = new InlineKeyboardButton();
            channelButton.setText("Вернуться назад");
            channelButton.setCallbackData(CallbackType.CHANNELS_BUTTON);
            rowInLine.add(channelButton);
            channelButton = new InlineKeyboardButton();
            rowsInLine.add(rowInLine);

            markupInLine.setKeyboard(rowsInLine);
            message.setReplyMarkup(markupInLine);

            message.setText(text);
            return new TelegramSendMessage(message, String.valueOf(chatId));
        }
        return null;
    }
}
