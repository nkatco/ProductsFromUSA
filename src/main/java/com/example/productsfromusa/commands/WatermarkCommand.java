package com.example.productsfromusa.commands;

import com.example.productsfromusa.DAO.StateDataDAO;
import com.example.productsfromusa.callbacks.CallbackType;
import com.example.productsfromusa.models.*;
import com.example.productsfromusa.services.data.ChannelService;
import com.example.productsfromusa.services.data.ChannelSettingsService;
import com.example.productsfromusa.services.data.UserService;
import com.example.productsfromusa.states.States;
import com.example.productsfromusa.utils.GraphicUtils;
import com.example.productsfromusa.utils.TelegramBotUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Component
public class WatermarkCommand implements Command {

    private static final Logger logger = LoggerFactory.getLogger(WatermarkCommand.class);

    @Autowired
    private UserService userService;
    @Autowired
    public StateDataDAO stateDataDAO;
    @Autowired
    public ChannelService channelService;
    @Autowired
    public ChannelSettingsService channelSettingsService;
    @Lazy
    @Autowired
    public TelegramBotUtils telegramBotUtils;

    @Override
    public TelegramMessage apply(Update update) {
        long chatId = update.getMessage().getChatId();
        long userId = update.getMessage().getFrom().getId();
        SendMessage message = new SendMessage();

        try {
            Document document = update.getMessage().getDocument();
            try {
                User user = userService.getUserByTelegramId(userId);
                user.setState(States.CHANNEL_SETTINGS);
                String channel_id = (String) stateDataDAO.getStateDataByUserId("channel_id" + "_" + user.getId()).getData();
                stateDataDAO.removeStateDataByUserId("channel_id" + "_" + user.getId());
                userService.saveUser(user);

                Channel channel = channelService.getChannelById(channel_id);
                ChannelSettings channelSettings = channel.getChannelSettings();

                if (document != null) {
                    logger.info("Downloading watermark image for user: {}", userId);
                    String wtr = (telegramBotUtils.downloadPhoto(document.getFileId()));
                    if (wtr != null) {
                        if (channelSettings.getWatermarkImage() != null && channelSettings.getWatermarkImage().getPath() != null) {
                            File file = new File(channelSettings.getWatermarkImage().getPath());
                            file.delete();
                        }
                        WatermarkImage watermarkImage = new WatermarkImage();
                        watermarkImage.setAlpha(0.5f);
                        watermarkImage.setMode("center");
                        watermarkImage.setPath(wtr);
                        channelSettings.setWatermarkImage(watermarkImage);
                    }
                }

                channelSettingsService.saveChannelSettings(channelSettings);

                InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();
                List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
                List<InlineKeyboardButton> rowInLine = new ArrayList<>();
                var channelButton = new InlineKeyboardButton();

                rowInLine = new ArrayList<>();
                channelButton = new InlineKeyboardButton();
                channelButton.setText("Настроить вотермарку");

                channelButton.setCallbackData(CallbackType.ADD_WATERMARK + channel_id);
                rowInLine.add(channelButton);
                rowsInLine.add(rowInLine);

                markupInLine.setKeyboard(rowsInLine);

                message.setText("Watermark has been set.");
                message.setReplyMarkup(markupInLine);
                message.setChatId(chatId);

                logger.info("Watermark set successfully for channel: {}", channel_id);
                return new TelegramSendMessage(message, String.valueOf(chatId));
            } catch (Exception e) {
                logger.error("Error while setting watermark for user: {}", userId, e);
                message.setText("An error occurred, please enter /start and contact the administrator.");
            }
        } catch (Exception e) {
            logger.error("Invalid input from user: {}", userId, e);
            message.setText("Невалидный ввод.");
        }

        message.setChatId(chatId);
        return new TelegramSendMessage(message, String.valueOf(chatId));
    }
}