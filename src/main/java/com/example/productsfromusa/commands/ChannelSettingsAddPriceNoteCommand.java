package com.example.productsfromusa.commands;

import com.example.productsfromusa.DAO.StateDataDAO;
import com.example.productsfromusa.callbacks.CallbackType;
import com.example.productsfromusa.models.*;
import com.example.productsfromusa.services.data.ChannelService;
import com.example.productsfromusa.services.data.ChannelSettingsService;
import com.example.productsfromusa.services.data.UserService;
import com.example.productsfromusa.states.States;
import com.vdurmont.emoji.EmojiParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

@Component
public class ChannelSettingsAddPriceNoteCommand implements Command {

    @Autowired
    private UserService userService;
    @Autowired
    public StateDataDAO stateDataDAO;
    @Autowired
    public ChannelService channelService;
    @Autowired
    public ChannelSettingsService channelSettingsService;

    @Override
    public TelegramMessage apply(Update update) {
        long chatId = update.getMessage().getChatId();
        long userId = update.getMessage().getFrom().getId();
        SendMessage message = new SendMessage();

        try {
            String text = EmojiParser.parseToUnicode(update.getMessage().getText());
            try {
                User user = userService.getUserByTelegramId(userId);
                user.setState(States.BASIC_STATE);
                String channel_id = (String) stateDataDAO.getStateDataByUserId("channel_id" + "_" + user.getId()).getData();
                stateDataDAO.removeStateDataByUserId("channel_id" + "_" + user.getId());
                userService.saveUser(user);

                Channel channel = channelService.getChannelById(channel_id);
                ChannelSettings channelSettings = channel.getChannelSettings();

                channelSettings.setPriceNote(text);

                channelSettingsService.saveChannelSettings(channelSettings);

                message.setText("Приписка к цене успешно установлена.");
                InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();
                List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
                List<InlineKeyboardButton> rowInLine = new ArrayList<>();
                var channelButton = new InlineKeyboardButton();

                rowInLine = new ArrayList<>();
                channelButton = new InlineKeyboardButton();
                channelButton.setText("Вернуться назад");

                channelButton.setCallbackData(CallbackType.CHANNEL_SETTINGS + channel_id);
                rowInLine.add(channelButton);
                channelButton = new InlineKeyboardButton();
                rowsInLine.add(rowInLine);

                markupInLine.setKeyboard(rowsInLine);
                message.setReplyMarkup(markupInLine);
            } catch (Exception e) {
                message.setText("Произошла какая-то ошибка, введите /start и обратитесь к администратору.");
                System.out.println(e);

            }
        } catch (Exception e) {
            message.setText("Неверный ввод.");
        }

        message.setChatId(chatId);
        return new TelegramSendMessage(message, String.valueOf(chatId));
    }
}