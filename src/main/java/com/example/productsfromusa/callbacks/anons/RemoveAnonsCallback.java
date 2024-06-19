package com.example.productsfromusa.callbacks.anons;

import com.example.productsfromusa.callbacks.CallbackHandler;
import com.example.productsfromusa.callbacks.CallbackType;
import com.example.productsfromusa.models.Anons;
import com.example.productsfromusa.models.TelegramMessage;
import com.example.productsfromusa.models.TelegramSendMessage;
import com.example.productsfromusa.models.User;
import com.example.productsfromusa.services.data.AnonsService;
import com.example.productsfromusa.services.data.UserService;
import com.example.productsfromusa.states.States;
import com.example.productsfromusa.utils.Consts;
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
public class RemoveAnonsCallback implements CallbackHandler {

    @Autowired
    public UserService userService;
    @Autowired
    public AnonsService anonsService;

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
                String text = "ВНИМАНИЕ: От удаления анонса, у вас не прибавится счетчик кол-ва анонсов на подписке.\n\nВы уверены, что хотите удалить анонс" + anons.getDate() + "?";

                user.setState(States.ANONS_SETTINGS);
                userService.saveUser(user);

                InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();
                List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();

                List<InlineKeyboardButton> rowInLine = new ArrayList<>();
                var anonsButton = new InlineKeyboardButton();

                anonsButton = new InlineKeyboardButton();
                anonsButton.setText("Удалить анонс");
                anonsButton.setCallbackData(CallbackType.REMOVE_ANONS2 + anons.getId());
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
