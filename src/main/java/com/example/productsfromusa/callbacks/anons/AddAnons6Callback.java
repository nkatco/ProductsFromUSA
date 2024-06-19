package com.example.productsfromusa.callbacks.anons;

import com.example.productsfromusa.DAO.StateDataDAO;
import com.example.productsfromusa.callbacks.CallbackHandler;
import com.example.productsfromusa.callbacks.CallbackType;
import com.example.productsfromusa.models.*;
import com.example.productsfromusa.schedulers.AnonsScheduler;
import com.example.productsfromusa.services.data.*;
import com.example.productsfromusa.states.States;
import com.example.productsfromusa.utils.Consts;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@Component
public class AddAnons6Callback implements CallbackHandler {

    private static final Logger logger = LoggerFactory.getLogger(AddAnons6Callback.class);

    @Autowired
    public UserService userService;
    @Autowired
    public AnonsService anonsService;
    @Autowired
    public TokenService tokenService;
    @Autowired
    public AnonsPostService anonsPostService;
    @Autowired
    public StateDataDAO stateDataDAO;
    @Autowired
    public AnonsScheduler anonsScheduler;
    @Autowired
    public CategoryService categoryService;
    @Value("${bot.anons}")
    String anons;

    @Override
    public TelegramMessage apply(Update update) {
        long chatId = update.getCallbackQuery().getMessage().getChatId();
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        User user = userService.getUserByTelegramId(update.getCallbackQuery().getFrom().getId());
        String callbackData = update.getCallbackQuery().getData();

        try {
            Token token = (Token) stateDataDAO.getStateDataByUserId("token_" + user.getId()).getData();
            String hour = (String) stateDataDAO.getStateDataByUserId("hour_" + user.getId()).getData();
            String minute = (String) stateDataDAO.getStateDataByUserId("minute_" + user.getId()).getData();
            int posts = (int) stateDataDAO.getStateDataByUserId("posts_" + user.getId()).getData();
            String category_id = callbackData.substring(CallbackType.CREATE_ANONS6.length());

            stateDataDAO.removeStateDataByUserId(user.getId());
            if (token != null && token.getAnons() > 0) {
                if (user.getState().equals(States.ADD_ANONS)) {
                    logger.info("Creating Anons for user {}", user.getId());

                    Anons anons1 = new Anons();
                    anons1.setToken(token);
                    anons1.setUser(user);
                    anons1.setDate(hour + ":" + minute);
                    anons1.setPosts(posts);
                    anons1.setCategory(categoryService.getCategoryById(category_id));

                    Anons anons2 = anonsService.saveAnons(anons1);
                    logger.info("Anons created with ID {}", anons2.getId());

                    Set<AnonsPost> anonsPosts = new HashSet<>();
                    for (int i = 0; i < posts; i++) {
                        AnonsPost anonsPost = new AnonsPost();
                        anonsPost.setAnons(anons2);
                        AnonsPost anonsPost1 = anonsPostService.saveAnons(anonsPost);
                        anonsPosts.add(anonsPost1);
                    }

                    Anons anons3 = anonsService.saveAnons(anons2);
                    token.setAnons(token.getAnons() - 1);
                    tokenService.saveToken(token);

                    LocalDateTime currentDateTime = LocalDateTime.now();
                    LocalDateTime futureDateTime = currentDateTime.plusSeconds(10);
                    LocalDateTime newDateTime = currentDateTime;
                    newDateTime = newDateTime.withHour(Integer.parseInt(hour)).withMinute(Integer.parseInt(minute));
                    if (newDateTime.isBefore(currentDateTime)) {
                        newDateTime = newDateTime.plusDays(1);
                    }
                    anonsScheduler.scheduleCheckAnonsTask("anons_check_group1", futureDateTime, anons3);
                    anonsScheduler.scheduleAnonsTask("anons_group1", newDateTime, anons3);

                    logger.info("Scheduled tasks for Anons ID {}", anons3.getId());

                    String text = "Анонс был успешно создан.";

                    user.setState(States.BASIC_STATE);
                    userService.saveUser(user);

                    InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();
                    List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();

                    List<InlineKeyboardButton> rowInLine = new ArrayList<>();
                    InlineKeyboardButton timeButton = new InlineKeyboardButton();

                    timeButton.setText("К анонсам");
                    timeButton.setCallbackData(CallbackType.ANONS_BUTTON);
                    rowInLine.add(timeButton);
                    rowsInLine.add(rowInLine);

                    markupInLine.setKeyboard(rowsInLine);
                    message.setReplyMarkup(markupInLine);

                    message.setText(text);
                    return new TelegramSendMessage(message, String.valueOf(chatId));
                }
            }
        } catch (Exception e) {
            logger.error("Error processing AddAnons6 callback for user {}: {}", user.getId(), e.getMessage(), e);
        }

        message.setChatId(chatId);
        message.setText(Consts.ERROR);
        return new TelegramSendMessage(message, String.valueOf(chatId));
    }
}