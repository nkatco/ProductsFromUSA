package com.example.productsfromusa.callbacks.statistic;

import com.example.productsfromusa.callbacks.CallbackHandler;
import com.example.productsfromusa.callbacks.CallbackType;
import com.example.productsfromusa.models.*;
import com.example.productsfromusa.services.data.ChannelService;
import com.example.productsfromusa.services.data.StatisticService;
import com.example.productsfromusa.services.data.UserService;
import com.example.productsfromusa.states.States;
import com.example.productsfromusa.utils.ChartUtils;
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

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@Component
public class StatisticInfoCallback implements CallbackHandler {

    @Autowired
    private UserService userService;
    @Autowired
    private ChannelService channelService;
    @Autowired
    private StatisticService statisticService;
    @Autowired
    private ChartUtils chartUtils;

    @Override
    public TelegramMessage apply(Update update) {
        long chatId = update.getCallbackQuery().getMessage().getChatId();
        long userId = update.getCallbackQuery().getFrom().getId();
        SendPhoto message = new SendPhoto();
        String callbackData = update.getCallbackQuery().getData();

        User user = userService.getUserByTelegramId(userId);
        user.setState(States.STATISTIC_INFO);
        userService.saveUser(user);

        InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();

        List<InlineKeyboardButton> rowInLine = new ArrayList<>();
        var statButton = new InlineKeyboardButton();

        String id = callbackData.substring(CallbackType.INFO_STATISTIC.length());
        Channel channel = channelService.getChannelById(id);

        LocalDateTime now = LocalDateTime.now();

        int year = now.getYear();
        int month = now.getMonthValue();

        YearMonth currentMonth = YearMonth.of(year, month);

        LocalDateTime startOfMonth = currentMonth.atDay(1).atStartOfDay();
        LocalDateTime endOfMonth = currentMonth.atEndOfMonth().atTime(23, 59, 59);

        List<Statistic> list = statisticService.getStatisticsByChannelAndDate(channel, startOfMonth, endOfMonth);

        if (list != null) {
            message.setCaption("Статистика для канала " + channel.getName());

            statButton = new InlineKeyboardButton();
            statButton.setText(EmojiParser.parseToUnicode(":arrows_counterclockwise:" + " Обновить"));
            statButton.setCallbackData(CallbackType.INFO_STATISTIC + channel.getId());
            rowInLine.add(statButton);
            rowsInLine.add(rowInLine);
            rowInLine = new ArrayList<>();

            statButton = new InlineKeyboardButton();
            statButton.setText(EmojiParser.parseToUnicode(":arrow_left:" + " Назад"));
            statButton.setCallbackData(CallbackType.SHOW_STATISTIC);
            rowInLine.add(statButton);
            rowsInLine.add(rowInLine);
            rowInLine = new ArrayList<>();

            markupInLine.setKeyboard(rowsInLine);
            message.setReplyMarkup(markupInLine);

            try {
                message.setPhoto(new InputFile(chartUtils.generateChart(list)));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            return new TelegramSendPhoto(message, String.valueOf(chatId));
        } else if (list == null) {
            SendMessage message1 = new SendMessage();
            message1.setChatId(chatId);
            message1.setText("Статистики на данный канал ещё нет.");
            return new TelegramSendMessage(message1, String.valueOf(chatId));
        }
        return null;
    }
}
