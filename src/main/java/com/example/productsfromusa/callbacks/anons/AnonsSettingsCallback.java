package com.example.productsfromusa.callbacks.anons;

import com.example.productsfromusa.callbacks.CallbackHandler;
import com.example.productsfromusa.callbacks.CallbackType;
import com.example.productsfromusa.models.*;
import com.example.productsfromusa.services.SendPostService;
import com.example.productsfromusa.services.data.AnonsService;
import com.example.productsfromusa.services.data.UserService;
import com.example.productsfromusa.states.States;
import com.example.productsfromusa.utils.Consts;
import com.example.productsfromusa.utils.TelegramBotUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@Component
public class AnonsSettingsCallback implements CallbackHandler {

    @Autowired
    public UserService userService;
    @Autowired
    public AnonsService anonsService;
    @Autowired
    public SendPostService sendPostService;
    @Lazy
    @Autowired
    public TelegramBotUtils telegramBotUtils;

    @Override
    public TelegramMessage apply(Update update) {
        long chatId = update.getCallbackQuery().getMessage().getChatId();
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        User user = userService.getUserByTelegramId(update.getCallbackQuery().getFrom().getId());
        String callbackData = update.getCallbackQuery().getData();
        String id = callbackData.substring(CallbackType.ANONS_SETTINGS.length());
        Anons anons = anonsService.getAnonsById(id);
        if(anons != null) {
            if (user.getState().equals(States.ANONS)) {
                String text = "Это анонс для канала " + anons.getToken().getChannel().getName() + ".\n\nВремя публикации: " + anons.getDate() + "\n\nКатегория: " + anons.getCategory().getName();

                user.setState(States.ANONS_SETTINGS);
                userService.saveUser(user);
                int count = 1;
                Set<AnonsPost> anonsPosts = anons.getAnonsPosts();
                for(AnonsPost anonsPost : anonsPosts) {
                    Post post = anonsPost.getPost();
                    if(post != null) {
                        InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();
                        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();

                        List<InlineKeyboardButton> rowInLine = new ArrayList<>();
                        var anonsButton = new InlineKeyboardButton();

                        anonsButton = new InlineKeyboardButton();
                        anonsButton.setText("Ссылка на товар");
                        anonsButton.setUrl(post.getRef());
                        rowInLine.add(anonsButton);
                        rowsInLine.add(rowInLine);
                        rowInLine = new ArrayList<>();

                        anonsButton = new InlineKeyboardButton();
                        anonsButton.setText("Поменять анонс");
                        anonsButton.setCallbackData(CallbackType.REMOVE_ANONS_POST + anonsPost.getId());
                        rowInLine.add(anonsButton);
                        rowsInLine.add(rowInLine);
                        rowInLine = new ArrayList<>();

                        markupInLine.setKeyboard(rowsInLine);
                        SendMessagePost s = sendPostService.getPostByChannelSettingsWithoutWatermark(post, anons.getToken().getChannel().getChannelSettings());
                        String postText = "Пост: №" + count + "\n\n" + s.getText();
                        telegramBotUtils.sendMessageAnonsPost(chatId, s.getPhoto(), postText, markupInLine);
                    } else {
                        String postText = "Пост: №" + count + "\n\nЕщё не готов.";
                        telegramBotUtils.sendMessageNullAnonsPost(chatId, postText);
                    }
                    count++;
                }

                InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();
                List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();

                List<InlineKeyboardButton> rowInLine = new ArrayList<>();
                var anonsButton = new InlineKeyboardButton();

                anonsButton = new InlineKeyboardButton();
                anonsButton.setText("Изменить категорию");
                anonsButton.setCallbackData(CallbackType.CHANGE_ANONS_CATEGORY + anons.getId());
                rowInLine.add(anonsButton);
                rowsInLine.add(rowInLine);
                rowInLine = new ArrayList<>();

                anonsButton = new InlineKeyboardButton();
                anonsButton.setText("Удалить анонс");
                anonsButton.setCallbackData(CallbackType.REMOVE_ANONS1 + anons.getId());
                rowInLine.add(anonsButton);
                rowsInLine.add(rowInLine);
                rowInLine = new ArrayList<>();

                anonsButton = new InlineKeyboardButton();
                anonsButton.setText("Назад");
                anonsButton.setCallbackData(CallbackType.ANONS_BUTTON);
                rowInLine.add(anonsButton);
                rowsInLine.add(rowInLine);
                rowInLine = new ArrayList<>();

                markupInLine.setKeyboard(rowsInLine);
                message.setReplyMarkup(markupInLine);

                message.setText(text);
                return new TelegramSendMessage(message, String.valueOf(chatId));
            }
        }
        message.setChatId(chatId);
        message.setText(Consts.ERROR);
        return new TelegramSendMessage(message, String.valueOf(chatId));
    }
}
