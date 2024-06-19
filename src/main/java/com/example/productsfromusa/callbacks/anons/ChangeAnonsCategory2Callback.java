package com.example.productsfromusa.callbacks.anons;

import com.example.productsfromusa.DAO.StateDataDAO;
import com.example.productsfromusa.callbacks.CallbackHandler;
import com.example.productsfromusa.callbacks.CallbackType;
import com.example.productsfromusa.models.*;
import com.example.productsfromusa.services.data.AnonsPostService;
import com.example.productsfromusa.services.data.AnonsService;
import com.example.productsfromusa.services.data.CategoryService;
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
import java.util.Set;

@RequiredArgsConstructor
@Component
public class ChangeAnonsCategory2Callback implements CallbackHandler {

    @Autowired
    public UserService userService;
    @Autowired
    public AnonsService anonsService;
    @Autowired
    public StateDataDAO stateDataDAO;
    @Autowired
    public CategoryService categoryService;
    @Autowired
    public AnonsPostService anonsPostService;

    @Override
    public TelegramMessage apply(Update update) {
        long chatId = update.getCallbackQuery().getMessage().getChatId();
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        User user = userService.getUserByTelegramId(update.getCallbackQuery().getFrom().getId());
        String callbackData = update.getCallbackQuery().getData();
        String id = callbackData.substring(CallbackType.CHANGE_ANONS_CATEGORY2.length());
        Anons anons = (Anons) stateDataDAO.getStateDataByUserId("anons_" + user.getId()).getData();
        if(anons != null) {
            if (user.getState().equals(States.ANONS_CHANGE_CATEGORY)) {
                stateDataDAO.removeStateDataByUserId(user.getId());

                Category category = categoryService.getCategoryById(id);
                anons.setCategory(category);

                for(AnonsPost anonsPost : anons.getAnonsPosts()) {
                    AnonsPost anonsP = anonsPostService.getAnonsById(anonsPost.getId());
                    anonsP.setPost(null);
                    anonsPostService.saveAnons(anonsP);
                }
                anons.setLastPost(null);
                anonsService.saveAnons(anons);
                String text = "Вы успешно поменяли категорию";

                user.setState(States.ANONS);
                userService.saveUser(user);

                InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();
                List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();

                List<InlineKeyboardButton> rowInLine = new ArrayList<>();
                var catButton = new InlineKeyboardButton();

                catButton = new InlineKeyboardButton();
                catButton.setText("К анонсам");
                catButton.setCallbackData(CallbackType.ANONS_BUTTON);
                rowInLine.add(catButton);
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
