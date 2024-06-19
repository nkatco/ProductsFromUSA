package com.example.productsfromusa.callbacks.token;

import com.example.productsfromusa.callbacks.CallbackHandler;
import com.example.productsfromusa.callbacks.CallbackType;
import com.example.productsfromusa.models.TelegramMessage;
import com.example.productsfromusa.models.TelegramSendMessage;
import com.example.productsfromusa.models.Token;
import com.example.productsfromusa.models.User;
import com.example.productsfromusa.services.data.TokenService;
import com.example.productsfromusa.services.data.UserService;
import com.example.productsfromusa.states.States;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Component
public class TokenInfoCallback implements CallbackHandler {


    @Autowired
    public TokenService tokenService;
    @Autowired
    public UserService userService;
    @Value("${bot.anons}")
    String anons;

    @Override
    public TelegramMessage apply(Update update) {
        long chatId = update.getCallbackQuery().getMessage().getChatId();
        SendMessage message = new SendMessage();
        message.setChatId(chatId);

        String callbackData = update.getCallbackQuery().getData();

        User user = userService.getUserByTelegramId(update.getCallbackQuery().getFrom().getId());

        if (user.getState().equals(States.CHANNEL_SETTINGS)) {
            user.setState(States.INFO_TOKEN);
            String id = callbackData.substring(CallbackType.INFO_TOKEN.length());
            Token token = tokenService.getTokenById(id);

            LocalDateTime dateTime = LocalDateTime.parse(token.getDateOfExpiration());
            LocalDateTime purDateTime = LocalDateTime.parse(token.getDateOfPurchase());
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            message.setText("Подписка для канала " + token.getChannel().getName() + "\n\nИстечет " + dateTime.format(formatter) + "\n\nВы приобрели его " + purDateTime.format(formatter) + " за " + token.getPrice() + "\n\nНа данный момент доступно " + token.getAnons() + "/" + anons + " анонсов");

            InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();
            List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();

            List<InlineKeyboardButton> rowInLine = new ArrayList<>();
            var tokenButton = new InlineKeyboardButton();

            tokenButton = new InlineKeyboardButton();
            tokenButton.setText("Отказаться от подписки");
            tokenButton.setCallbackData(CallbackType.REMOVE_TOKEN1 + token.getId());
            rowInLine.add(tokenButton);
            rowsInLine.add(rowInLine);
            rowInLine = new ArrayList<>();

            tokenButton = new InlineKeyboardButton();
            tokenButton.setText("Главное меню");
            tokenButton.setCallbackData(CallbackType.MENU_BUTTON);
            rowInLine.add(tokenButton);
            rowsInLine.add(rowInLine);
            rowInLine = new ArrayList<>();

            markupInLine.setKeyboard(rowsInLine);
            message.setReplyMarkup(markupInLine);

            return new TelegramSendMessage(message, String.valueOf(chatId));
        }
        return null;
    }
}
