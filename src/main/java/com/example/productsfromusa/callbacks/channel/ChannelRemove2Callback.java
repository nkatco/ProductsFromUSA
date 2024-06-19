package com.example.productsfromusa.callbacks.channel;

import com.example.productsfromusa.callbacks.CallbackHandler;
import com.example.productsfromusa.callbacks.CallbackType;
import com.example.productsfromusa.models.*;
import com.example.productsfromusa.services.data.*;
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
import java.util.Set;

@RequiredArgsConstructor
@Component
public class ChannelRemove2Callback implements CallbackHandler {

    @Autowired
    public PreChannelService preChannelService;
    @Autowired
    public AnonsPostService anonsPostService;
    @Autowired
    public UserService userService;
    @Autowired
    public ChannelService channelService;
    @Autowired
    public TokenService tokenService;
    @Autowired
    public AnonsService anonsService;
    @Autowired
    public LastPostService lastPostService;

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
            user.setState(States.BASIC_STATE);

            userService.saveUser(user);

            Token token = tokenService.getTokenByChannelId(channel.getId());

            if (token != null) {
                Set<Anons> anonses = anonsService.getAllAnonsByTokenId(token.getId());
                for (Anons anons : anonses) {
                    lastPostService.removeLastPostById(anons.getLastPost().getId());
                    anonsPostService.removeAnonsPostsByAnonsId(anons.getId());
                }
                anonsService.deleteAnonsByTokenId(token.getId());
                tokenService.removeTokenById(token.getId());
            }

            String text = "Канал " + channel.getName() + " был удален.";

            channelService.removeChannelByTelegramId(channel.getTelegramId());

            InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();
            List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();

            List<InlineKeyboardButton> rowInLine = new ArrayList<>();
            var channelButton = new InlineKeyboardButton();

            rowInLine = new ArrayList<>();
            channelButton = new InlineKeyboardButton();
            channelButton.setText("Главное меню");
            channelButton.setCallbackData(CallbackType.MENU_BUTTON);
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
