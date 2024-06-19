package com.example.productsfromusa.callbacks.channel;

import com.example.productsfromusa.DAO.StateDataDAO;
import com.example.productsfromusa.callbacks.CallbackHandler;
import com.example.productsfromusa.callbacks.CallbackType;
import com.example.productsfromusa.models.*;
import com.example.productsfromusa.services.SendPostService;
import com.example.productsfromusa.services.data.ChannelService;
import com.example.productsfromusa.services.data.PreChannelService;
import com.example.productsfromusa.services.data.TokenService;
import com.example.productsfromusa.services.data.UserService;
import com.example.productsfromusa.states.States;
import com.example.productsfromusa.utils.TextUtils;
import com.vdurmont.emoji.EmojiParser;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Component
public class ChannelSettingsCallback implements CallbackHandler {

    @Autowired
    public UserService userService;
    @Autowired
    public TokenService tokenService;
    @Autowired
    public ChannelService channelService;
    @Autowired
    public StateDataDAO stateDataDAO;
    @Autowired
    public SendPostService sendPostService;

    @Override
    public TelegramMessage apply(Update update) {
        long chatId = update.getCallbackQuery().getMessage().getChatId();
        SendPhoto message = new SendPhoto();
        message.setChatId(chatId);
        String callbackData = update.getCallbackQuery().getData();

        User user = userService.getUserByTelegramId(update.getCallbackQuery().getFrom().getId());
        user.setState(States.CHANNEL_SETTINGS);
        userService.saveUser(user);

        stateDataDAO.removeStateDataByUserId("channel_id" + "_" + user.getId());

        String id = callbackData.substring(CallbackType.CHANNEL_SETTINGS.length());
        Channel channel = channelService.getChannelById(id);
        ChannelSettings channelSettings = channel.getChannelSettings();

        Post post = new Post();
        post.setPrice(12000);
        post.setNumber(0);
        post.setName("Кроссовки Noke");
        post.setText("Современный внешний вид, который подходит для любых ситуаций, а специально разработанная подошва и мягкая внутренняя стелька обеспечивают максимальный комфорт и поддержку для ваших ног в течение всего дня." +
                "\n\nНе упустите шанс стать обладателем этих великолепных кроссовок!");
        post.setOldPrice(5400);
        post.setRef("https://google.ru");
        post.setImage("src/main/resources/images/settings_test.jpg");

        SendMessagePost s = sendPostService.getPostByChannelSettingsWithoutWatermark(post, channel.getChannelSettings());

        String text = "Настройки канала " + channel.getName() + ". Ниже находится пример поста.\n\n" + s.getText();

        InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();

        List<InlineKeyboardButton> rowInLine = new ArrayList<>();
        var channelButton = new InlineKeyboardButton();

        Token token = tokenService.getTokenByChannelId(channel.getId());

        if(token == null) {
            channelButton = new InlineKeyboardButton();
            channelButton.setText("Сомневаетесь?");
            channelButton.setCallbackData(CallbackType.GUIDE_TOKEN);
            rowInLine.add(channelButton);
            rowsInLine.add(rowInLine);
            rowInLine = new ArrayList<>();

            channelButton = new InlineKeyboardButton();
            channelButton.setText("Купить подписку");
            channelButton.setCallbackData(CallbackType.BUY_TOKEN + channel.getId());
            rowInLine.add(channelButton);
            rowsInLine.add(rowInLine);
            rowInLine = new ArrayList<>();
        } else {
            if(channelSettings.isShowPrice()) {
                channelButton.setText(EmojiParser.parseToUnicode("Отображать цену :white_check_mark:"));
            } else {
                channelButton.setText(EmojiParser.parseToUnicode("Отображать цену :no_entry_sign:"));
            }
            channelButton.setCallbackData(CallbackType.ADD_SHOW_PRICE + channel.getId());
            rowInLine.add(channelButton);
            rowsInLine.add(rowInLine);
            rowInLine = new ArrayList<>();

            channelButton = new InlineKeyboardButton();
            if(channelSettings.isShowOldPrice()) {
                channelButton.setText(EmojiParser.parseToUnicode("Отображать старую цену :white_check_mark:"));
            } else {
                channelButton.setText(EmojiParser.parseToUnicode("Отображать старую цену :no_entry_sign:"));
            }
            channelButton.setCallbackData(CallbackType.ADD_SHOW_OLD_PRICE + channel.getId());
            rowInLine.add(channelButton);
            rowsInLine.add(rowInLine);
            rowInLine = new ArrayList<>();

            channelButton = new InlineKeyboardButton();
            if(channelSettings.getWatermarkImage() != null && channelSettings.getWatermarkImage().getPath() != null) {
                channelButton.setText(EmojiParser.parseToUnicode("Установить вотермарку :white_check_mark:"));
            } else {
                channelButton.setText(EmojiParser.parseToUnicode("Установить вотермарку :no_entry_sign:"));
            }
            channelButton.setCallbackData(CallbackType.ADD_WATERMARK + channel.getId());
            rowInLine.add(channelButton);
            rowsInLine.add(rowInLine);
            rowInLine = new ArrayList<>();

            channelButton = new InlineKeyboardButton();
            if(channelSettings.getPriceNote() != null) {
                channelButton.setText(EmojiParser.parseToUnicode("Установить приписку к цене :white_check_mark:"));
            } else {
                channelButton.setText(EmojiParser.parseToUnicode("Установить приписку к цене :no_entry_sign:"));
            }
            channelButton.setCallbackData(CallbackType.ADD_PRICE_NOTE + channel.getId());
            rowInLine.add(channelButton);
            rowsInLine.add(rowInLine);
            rowInLine = new ArrayList<>();

            channelButton = new InlineKeyboardButton();
            if(channelSettings.getPostText() != null) {
                channelButton.setText(EmojiParser.parseToUnicode("Установить доп. текст :white_check_mark:"));
            } else {
                channelButton.setText(EmojiParser.parseToUnicode("Установить доп. текст :no_entry_sign:"));
            }
            channelButton.setCallbackData(CallbackType.ADD_POST_TEXT + channel.getId());
            rowInLine.add(channelButton);
            rowsInLine.add(rowInLine);
            rowInLine = new ArrayList<>();

            channelButton = new InlineKeyboardButton();
            channelButton.setText(EmojiParser.parseToUnicode("Установить округление цены :money_with_wings:"));
            channelButton.setCallbackData(CallbackType.SET_REDUCTION + channel.getId());
            rowInLine.add(channelButton);
            rowsInLine.add(rowInLine);
            rowInLine = new ArrayList<>();

            channelButton = new InlineKeyboardButton();
            if(channelSettings.getAddCourse() != 1) {
                channelButton.setText(EmojiParser.parseToUnicode("Установить свой курс :white_check_mark:"));
            } else {
                channelButton.setText(EmojiParser.parseToUnicode("Установить свой курс :no_entry_sign:"));
            }
            channelButton.setCallbackData(CallbackType.ADD_COURSE + channel.getId());
            rowInLine.add(channelButton);
            rowsInLine.add(rowInLine);
            rowInLine = new ArrayList<>();

            channelButton = new InlineKeyboardButton();
            channelButton.setText("Удалить канал");
            channelButton.setCallbackData(CallbackType.CHANNEL_REMOVE1 + channel.getId());
            rowInLine.add(channelButton);
            rowsInLine.add(rowInLine);
            rowInLine = new ArrayList<>();

            channelButton = new InlineKeyboardButton();
            channelButton.setText("Информация о подписке");
            channelButton.setCallbackData(CallbackType.INFO_TOKEN + token.getId());
            rowInLine.add(channelButton);
            rowsInLine.add(rowInLine);
            rowInLine = new ArrayList<>();
        }

        channelButton = new InlineKeyboardButton();
        channelButton.setText("Главное меню");
        channelButton.setCallbackData(CallbackType.MENU_BUTTON);
        rowInLine.add(channelButton);
        rowsInLine.add(rowInLine);

        markupInLine.setKeyboard(rowsInLine);
        message.setReplyMarkup(markupInLine);

        message.setCaption(text);
        message.setPhoto(s.getPhoto());
        message.setParseMode(ParseMode.HTML);
        return new TelegramSendPhoto(message, String.valueOf(chatId));
    }
}
