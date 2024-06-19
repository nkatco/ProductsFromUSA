package com.example.productsfromusa.callbacks.channel;

import com.example.productsfromusa.DAO.StateDataDAO;
import com.example.productsfromusa.callbacks.CallbackHandler;
import com.example.productsfromusa.callbacks.CallbackType;
import com.example.productsfromusa.models.*;
import com.example.productsfromusa.services.data.ChannelService;
import com.example.productsfromusa.services.data.ChannelSettingsService;
import com.example.productsfromusa.services.data.USDService;
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
public class ChannelSettingsAddSurchargeCallback implements CallbackHandler {

    @Autowired
    public UserService userService;
    @Autowired
    public ChannelService channelService;
    @Autowired
    public StateDataDAO stateDataDAO;
    @Autowired
    public ChannelSettingsService channelSettingsService;
    @Autowired
    public USDService usdService;

    @Override
    public TelegramMessage apply(Update update) {
        long chatId = update.getCallbackQuery().getMessage().getChatId();
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        String callbackData = update.getCallbackQuery().getData();

        String id = callbackData.substring(CallbackType.ADD_COURSE.length());
        Channel channel = channelService.getChannelById(id);

        if (channel != null) {
            User user = userService.getUserByTelegramId(update.getCallbackQuery().getFrom().getId());
            if (user.getState().equals(States.CHANNEL_SETTINGS)) {
                user.setState(States.SET_SURCHARGE);

                userService.saveUser(user);

                ChannelSettings channelSettings = channel.getChannelSettings();
                if(channelSettings.getCourse() == null) {
                    channelSettings.setCourse("USD");
                }
                channelSettingsService.saveChannelSettings(channelSettings);

                double vl = usdService.getCourse().getCourse();
                String formattedNumber = String.format("%02.2f", vl);
                double prz = 5;
                if (channelSettings.getAddCourse() != 0) {
                    prz = channel.getChannelSettings().getAddCourse();
                }
                String text = EmojiParser.parseToUnicode("Это надбавка к курсу. Цена товара высчитывается по курсу доллара с ЦБ РФ (https://cbr.ru/) + ваша надбавка  процентах. " +
                        "Наример:\nПри курсе " + formattedNumber + " цена товара составляет 1000 рубелй + ваша надбавка " + prz + "% = " + (1000 * (1 + prz / 100)) + " - это будет конечная цена для покупателя\n\n" +
                        "Отправь боту свой процент надбавки, чтобы изменить её." +
                        "\n\nТакже вы можете сменить режим отображения цены (валюту) кнопками ниже. Цена будет отображаться в выбранной валюте.");

                stateDataDAO.setStateData(user, "channel_id", channel.getId());

                InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();
                List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();

                List<InlineKeyboardButton> rowInLine = new ArrayList<>();
                var channelButton = new InlineKeyboardButton();

                rowInLine = new ArrayList<>();
                channelButton = new InlineKeyboardButton();
                if (!channelSettings.getCourse().equals("USD")) {
                    channelButton.setText("USD");
                } else {
                    channelButton.setText(EmojiParser.parseToUnicode("USD :white_check_mark:"));
                }
                channelButton.setCallbackData(CallbackType.SET_USD + channel.getId());
                rowInLine.add(channelButton);

                channelButton = new InlineKeyboardButton();
                if (!channelSettings.getCourse().equals("RUB")) {
                    channelButton.setText("RUB");
                } else {
                    channelButton.setText(EmojiParser.parseToUnicode("RUB :white_check_mark:"));
                }
                channelButton.setCallbackData(CallbackType.SET_RUB + channel.getId());
                rowInLine.add(channelButton);
                rowsInLine.add(rowInLine);

                rowInLine = new ArrayList<>();
                channelButton = new InlineKeyboardButton();
                channelButton.setText("Установить стандартный курс");
                channelButton.setCallbackData(CallbackType.REMOVE_COURSE + channel.getId());
                rowInLine.add(channelButton);
                channelButton = new InlineKeyboardButton();
                rowsInLine.add(rowInLine);

                rowInLine = new ArrayList<>();
                channelButton = new InlineKeyboardButton();
                channelButton.setText("Вернуться назад");
                channelButton.setCallbackData(CallbackType.CHANNEL_SETTINGS + channel.getId());
                rowInLine.add(channelButton);
                channelButton = new InlineKeyboardButton();
                rowsInLine.add(rowInLine);

                markupInLine.setKeyboard(rowsInLine);
                message.setReplyMarkup(markupInLine);

                message.setText(text);
                return new TelegramSendMessage(message, String.valueOf(chatId));
            }
        }
        return null;
    }
}
