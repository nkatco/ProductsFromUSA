package com.example.productsfromusa.callbacks.channel;

import com.example.productsfromusa.DAO.StateDataDAO;
import com.example.productsfromusa.callbacks.CallbackHandler;
import com.example.productsfromusa.callbacks.CallbackType;
import com.example.productsfromusa.models.*;
import com.example.productsfromusa.services.data.ChannelService;
import com.example.productsfromusa.services.data.UserService;
import com.example.productsfromusa.states.States;
import com.example.productsfromusa.utils.GraphicUtils;
import com.vdurmont.emoji.EmojiParser;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Component
public class ChannelSettingsAddWatermarkCallback implements CallbackHandler {

    @Autowired
    public UserService userService;
    @Autowired
    public ChannelService channelService;
    @Autowired
    public StateDataDAO stateDataDAO;
    @Autowired
    public GraphicUtils graphicUtils;

    @Override
    public TelegramMessage apply(Update update) {
        long chatId = update.getCallbackQuery().getMessage().getChatId();
        SendPhoto message = new SendPhoto();
        message.setChatId(chatId);
        String callbackData = update.getCallbackQuery().getData();

        String id = callbackData.substring(CallbackType.ADD_WATERMARK.length());
        Channel channel = channelService.getChannelById(id);

        if (channel != null) {
            User user = userService.getUserByTelegramId(update.getCallbackQuery().getFrom().getId());
            if(user.getState().equals(States.CHANNEL_SETTINGS)) {
                user.setState(States.ADD_WATERMARK);
                userService.saveUser(user);

                ChannelSettings channelSettings = channel.getChannelSettings();

                String text = "";
                if(channelSettings.getWatermarkImage() != null && channelSettings.getWatermarkImage().getPath() != null) {
                    text = EmojiParser.parseToUnicode("Здесь вы можете:\n" +
                            "1. Изменить размер вотермарки\n" +
                            "2. Задать прозрачность вотермарки\n" +
                            "3. Задать положение водяного знака.\n\n" +
                            "Чтобы изменить вотермарку, отправь новую боту(изображением).");

                    BufferedImage mainImage = null;
                    BufferedImage watermarkImage = null;
                    try {
                        mainImage = ImageIO.read(new File("src/main/resources/images/watermark_test.jpg"));
                        watermarkImage = ImageIO.read(new File(channelSettings.getWatermarkImage().getPath()));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    if(mainImage != null && watermarkImage != null) {
                        int w = 150;
                        int h = 150;
                        if(channelSettings.getWatermarkImage().getSize() != 0) {
                            w = channelSettings.getWatermarkImage().getSize();
                            h = channelSettings.getWatermarkImage().getSize();
                        }
                        int x = 0;
                        int y = 0;
                        if(channelSettings.getWatermarkImage().getMode().equals("center")) {
                            x = (mainImage.getWidth() - w) / 2;
                            y = (mainImage.getHeight() - h) / 2;
                        } else if (channelSettings.getWatermarkImage().getMode().equals("corner")){
                            if(channelSettings.getWatermarkImage().getMode().equals("center")) {
                                x = (mainImage.getWidth() - w) / 2;
                                y = (mainImage.getHeight() - h) / 2;
                            }
                        }
                        float alpha = 0.5f;
                        if(channelSettings.getWatermarkImage().getAlpha() != 0) {
                            alpha = channelSettings.getWatermarkImage().getAlpha();
                        }
                        File tempFile = graphicUtils.addWatermarkToImage(mainImage, watermarkImage, alpha, channelSettings.getWatermarkImage().getMode(),  w, h);
                        message.setPhoto(new InputFile(tempFile));
                    }
                } else {
                    text = EmojiParser.parseToUnicode("В данный момент у тебя не установлен водяной знак. Чтобы установить его, отправь нужное изображение в этот чат.");

                    BufferedImage mainImage = null;
                    BufferedImage watermarkImage = null;
                    try {
                        mainImage = ImageIO.read(new File("src/main/resources/images/watermark_test.jpg"));
                        watermarkImage = ImageIO.read(new File("src/main/resources/images/banner.png"));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    File tempFile = graphicUtils.addWatermarkToImage(mainImage, watermarkImage, 0.5f, "center",  200, 200);
                    message.setPhoto(new InputFile(tempFile));

                    message.setPhoto(new InputFile(new File("src/main/resources/images/watermark_test.jpg")));
                }
                stateDataDAO.setStateData(user, "channel_id", channel.getId());

                InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();
                List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();

                List<InlineKeyboardButton> rowInLine = new ArrayList<>();
                var channelButton = new InlineKeyboardButton();

                if(channelSettings.getWatermarkImage() != null && channelSettings.getWatermarkImage().getPath() != null) {
                    rowInLine = new ArrayList<>();
                    channelButton = new InlineKeyboardButton();
                    channelButton.setText("По центру");
                    channelButton.setCallbackData(CallbackType.CENTER_WATERMARK + channel.getId());
                    rowInLine.add(channelButton);

                    channelButton = new InlineKeyboardButton();
                    channelButton.setText("По углам");
                    channelButton.setCallbackData(CallbackType.CORNER_WATERMARK + channel.getId());
                    rowInLine.add(channelButton);

                    channelButton = new InlineKeyboardButton();
                    channelButton.setText("Центр + углы");
                    channelButton.setCallbackData(CallbackType.FULL_WATERMARK + channel.getId());
                    rowInLine.add(channelButton);

                    rowsInLine.add(rowInLine);

                    rowInLine = new ArrayList<>();
                    channelButton = new InlineKeyboardButton();
                    channelButton.setText("Прозрачность 1/3");
                    channelButton.setCallbackData(CallbackType.LIGHT_WATERMARK + channel.getId());
                    rowInLine.add(channelButton);

                    channelButton = new InlineKeyboardButton();
                    channelButton.setText("Прозрачность 2/3");
                    channelButton.setCallbackData(CallbackType.MEDIUM_WATERMARK + channel.getId());
                    rowInLine.add(channelButton);

                    channelButton = new InlineKeyboardButton();
                    channelButton.setText("Прозрачность 3/3");
                    channelButton.setCallbackData(CallbackType.HARD_WATERMARK + channel.getId());
                    rowInLine.add(channelButton);

                    rowsInLine.add(rowInLine);

                    rowInLine = new ArrayList<>();
                    channelButton = new InlineKeyboardButton();
                    channelButton.setText("Маленькая.");
                    channelButton.setCallbackData(CallbackType.LITTLE_WATERMARK + channel.getId());
                    rowInLine.add(channelButton);

                    channelButton = new InlineKeyboardButton();
                    channelButton.setText("Средняя");
                    channelButton.setCallbackData(CallbackType.SMALL_WATERMARK + channel.getId());
                    rowInLine.add(channelButton);

                    channelButton = new InlineKeyboardButton();
                    channelButton.setText("Большая");
                    channelButton.setCallbackData(CallbackType.BIG_WATERMARK + channel.getId());
                    rowInLine.add(channelButton);

                    rowsInLine.add(rowInLine);

                    rowInLine = new ArrayList<>();
                    channelButton = new InlineKeyboardButton();
                    channelButton.setText("Удалить вотермарку.");
                    channelButton.setCallbackData(CallbackType.REMOVE_WATERMARK + channel.getId());
                    rowInLine.add(channelButton);
                    rowsInLine.add(rowInLine);
                } else {
                    rowInLine = new ArrayList<>();
                    channelButton = new InlineKeyboardButton();
                    channelButton.setText("Что такое вотермарка?");
                    channelButton.setCallbackData(CallbackType.GUIDE_WATERMARK);
                    rowInLine.add(channelButton);
                    rowsInLine.add(rowInLine);
                }

                rowInLine = new ArrayList<>();
                channelButton = new InlineKeyboardButton();
                channelButton.setText("Вернуться назад");
                channelButton.setCallbackData(CallbackType.CHANNEL_SETTINGS + channel.getId());
                rowInLine.add(channelButton);
                rowsInLine.add(rowInLine);

                markupInLine.setKeyboard(rowsInLine);
                message.setReplyMarkup(markupInLine);

                message.setCaption(text);
                return new TelegramSendPhoto(message, String.valueOf(chatId));
            }
        }
        return null;
    }
}
