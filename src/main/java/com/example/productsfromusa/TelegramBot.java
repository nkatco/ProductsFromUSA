package com.example.productsfromusa;

import com.example.productsfromusa.configs.BotConfig;
import com.example.productsfromusa.handlers.*;
import com.example.productsfromusa.models.*;
import com.example.productsfromusa.services.data.PreChannelService;
import com.example.productsfromusa.services.data.UserService;
import com.example.productsfromusa.states.StateData;
import com.example.productsfromusa.states.States;
import com.example.productsfromusa.utils.Consts;
import com.example.productsfromusa.utils.TelegramBotUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerPreCheckoutQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMember;
import org.telegram.telegrambots.meta.api.objects.payments.PreCheckoutQuery;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class TelegramBot extends TelegramLongPollingBot {
    private static final Logger logger = LoggerFactory.getLogger(TelegramBotUtils.class);
    @Autowired
    public UserService userService;
    @Autowired
    public PreChannelService preChannelService;
    @Autowired
    public StateData stateData;

    public final BotConfig botProperties;

    public final CommandsHandler commandsHandler;

    public final CallbacksHandler callbacksHandler;
    public final TextHandler textHandler;

    public final PhoneHandler phoneHandler;
    public final PhotoHandler photoHandler;

    @Override
    public String getBotUsername() {
        return botProperties.getName();
    }

    @Override
    public String getBotToken() {
        return botProperties.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        SendMessage sendMessage = new SendMessage();
        String chatId = null;
        long userId = 0;
        if (update.hasMessage() && update.getMessage().hasText()) {
            chatId = update.getMessage().getChatId().toString();
            userId = update.getMessage().getFrom().getId();
            if(chatId != null && userId != 0 && !update.getMessage().hasContact()) {
                if(userService.existsByTelegramId(userId)) {
                    if (update.hasMessage() && update.getMessage().hasText()) {
                        if (update.getMessage().getText().startsWith("/")) {
                            sendMessage(commandsHandler.handleCommands(update));
                        } else {
                            sendMessage(textHandler.handleCommands(update));
                        }
                    } else if (update.hasCallbackQuery()) {
                        sendMessage(callbacksHandler.handleCallbacks(update));
                    } else if (update.hasMessage() && update.getMessage().hasContact()) {
                        sendMessage(phoneHandler.handlePhone(update));
                    }
                } else {
                    sendMessage.setChatId(String.valueOf(chatId));
                    sendMessage.setText("Чтобы взаимодействовать с сервисом, требуется регистрация.\n\nОтправьте свой номер, чтобы начать.");

                    ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
                    replyKeyboardMarkup.setSelective(true);
                    replyKeyboardMarkup.setResizeKeyboard(true);
                    replyKeyboardMarkup.setOneTimeKeyboard(true);

                    List<KeyboardRow> keyboard = new ArrayList<>();

                    KeyboardRow keyboardFirstRow = new KeyboardRow();
                    KeyboardButton keyboardButton = new KeyboardButton();

                    keyboardButton.setText("Отправить номер >");
                    keyboardButton.setRequestContact(true);
                    keyboardFirstRow.add(keyboardButton);

                    keyboard.add(keyboardFirstRow);
                    replyKeyboardMarkup.setKeyboard(keyboard);

                    stateData.setCurrentState(States.ADD_PHONE_USER);

                    sendMessage.setReplyMarkup(replyKeyboardMarkup);
                    sendMessage(new TelegramSendMessage(sendMessage, chatId));
                }
            } else if (update.getMessage().hasContact()) {
                sendMessage(phoneHandler.handlePhone(update));
            } else {
                System.out.println("Отсутствуют UserId и TelegramId");
            }
        } else if (update.hasCallbackQuery()) {
            chatId = update.getCallbackQuery().getMessage().getChatId().toString();
            userId = update.getCallbackQuery().getFrom().getId();
            if(chatId != null &&userId != 0) {
                if(userService.existsByTelegramId(userId)) {
                    if (update.hasMessage() && update.getMessage().hasText()) {
                        if (update.getMessage().getText().startsWith("/")) {
                            sendMessage(commandsHandler.handleCommands(update));
                        } else {
                            sendMessage(new TelegramSendMessage(new SendMessage(chatId, Consts.CANT_UNDERSTAND), chatId));
                        }
                    } else if (update.hasCallbackQuery()) {
                        sendMessage(callbacksHandler.handleCallbacks(update));
                    } else if (update.hasMessage() && update.getMessage().hasContact()) {
                        sendMessage(phoneHandler.handlePhone(update));
                    }
                } else {
                    sendMessage.setChatId(String.valueOf(chatId));
                    sendMessage.setText("Чтобы взаимодействовать с сервисом, требуется регистрация.\n\nОтправьте свой номер, чтобы начать.");

                    ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
                    replyKeyboardMarkup.setSelective(true);
                    replyKeyboardMarkup.setResizeKeyboard(true);
                    replyKeyboardMarkup.setOneTimeKeyboard(true);

                    List<KeyboardRow> keyboard = new ArrayList<>();

                    KeyboardRow keyboardFirstRow = new KeyboardRow();
                    KeyboardButton keyboardButton = new KeyboardButton();

                    keyboardButton.setText("Отправить номер >");
                    keyboardButton.setRequestContact(true);
                    keyboardFirstRow.add(keyboardButton);

                    keyboard.add(keyboardFirstRow);
                    replyKeyboardMarkup.setKeyboard(keyboard);

                    stateData.setCurrentState(States.ADD_PHONE_USER);

                    sendMessage.setReplyMarkup(replyKeyboardMarkup);
                    sendMessage(new TelegramSendMessage(sendMessage, chatId));
                }
            } else {
                System.out.println("Отсутствуют UserId и TelegramId");
            }
        } else if (update.hasMessage() && update.getMessage().hasContact()) {
            chatId = update.getMessage().getChatId().toString();
            userId = update.getMessage().getFrom().getId();
            if (chatId != null && userId != 0 && !update.getMessage().hasContact()) {
                if (userService.existsByTelegramId(userId)) {
                    if (update.hasMessage() && update.getMessage().hasText()) {
                        if (update.getMessage().getText().startsWith("/")) {
                            sendMessage(commandsHandler.handleCommands(update));
                        } else {
                            sendMessage(new TelegramSendMessage(new SendMessage(chatId, Consts.CANT_UNDERSTAND), chatId));
                        }
                    } else if (update.hasCallbackQuery()) {
                        sendMessage(callbacksHandler.handleCallbacks(update));
                    } else if (update.hasMessage() && update.getMessage().hasContact()) {
                        sendMessage(phoneHandler.handlePhone(update));
                    }
                } else {
                    sendMessage.setChatId(String.valueOf(chatId));
                    sendMessage.setText("Чтобы взаимодействовать с сервисом, требуется регистрация.\n\nОтправьте свой номер, чтобы начать.");

                    ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
                    replyKeyboardMarkup.setSelective(true);
                    replyKeyboardMarkup.setResizeKeyboard(true);
                    replyKeyboardMarkup.setOneTimeKeyboard(true);

                    List<KeyboardRow> keyboard = new ArrayList<>();

                    KeyboardRow keyboardFirstRow = new KeyboardRow();
                    KeyboardButton keyboardButton = new KeyboardButton();

                    keyboardButton.setText("Отправить номер >");
                    keyboardButton.setRequestContact(true);
                    keyboardFirstRow.add(keyboardButton);

                    keyboard.add(keyboardFirstRow);
                    replyKeyboardMarkup.setKeyboard(keyboard);

                    stateData.setCurrentState(States.ADD_PHONE_USER);

                    sendMessage.setReplyMarkup(replyKeyboardMarkup);
                    sendMessage(new TelegramSendMessage(sendMessage, chatId));
                }
            } else if (update.getMessage().hasContact()) {
                sendMessage(phoneHandler.handlePhone(update));
            } else {
                System.out.println("Отсутствуют UserId и TelegramId");
            }
        } else if (update.hasMyChatMember()) {
            ChatMember member = update.getMyChatMember().getNewChatMember();
            if(member.getStatus().equals("administrator") && member.getUser().getIsBot() && member.getUser().getUserName().equals(botProperties.getName())) {
                // Бот добавлен в канал
                if(preChannelService.getPreChannelByTelegramId(update.getMyChatMember().getChat().getId()) == null) {
                    logger.info("Bot has been added to channel {}", update.getMyChatMember().getChat().getTitle());
                    PreChannel preChannel = new PreChannel();
                    preChannel.setName(update.getMyChatMember().getChat().getTitle());
                    preChannel.setTelegramId(update.getMyChatMember().getChat().getId());
                    preChannel.setChatId(update.getMyChatMember().getChat().getId());
                    preChannelService.savePreChannel(preChannel);
                }
            } else if (member.getStatus().equals("kicked") && member.getUser().getIsBot() && member.getUser().getUserName().equals(botProperties.getName())) {
                // Бот удален из канала
                if(preChannelService.getPreChannelByTelegramId(update.getMyChatMember().getChat().getId()) != null) {
                    logger.info("Bot has been deleted from channel {}", update.getMyChatMember().getChat().getTitle());
                    preChannelService.removePreChannelByTelegramId(update.getMyChatMember().getChat().getId());
                }
            }
        }  else if (update.hasPreCheckoutQuery()) {
            PreCheckoutQuery preCheckoutQuery = update.getPreCheckoutQuery();
            AnswerPreCheckoutQuery answerPreCheckoutQuery = new AnswerPreCheckoutQuery(preCheckoutQuery.getId(), true);
            try {
                execute(answerPreCheckoutQuery);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        } else if (update.hasMessage() && update.getMessage().hasSuccessfulPayment()) {
            try {
                logger.info("Replenishment of the {} wallet with the amount {}", update.getMessage().getFrom().getUserName(), update.getMessage().getSuccessfulPayment().getTotalAmount() / 100);
                User user = userService.getUserByTelegramId(update.getMessage().getFrom().getId());
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
                user.getWallet().setHistory(user.getWallet().getHistory() + "\nПополнение на сумму: " + update.getMessage().getSuccessfulPayment().getTotalAmount() + " | " + timeStamp);
                user.getWallet().setMoney(user.getWallet().getMoney() + update.getMessage().getSuccessfulPayment().getTotalAmount() / 100);
                userService.saveUser(user);
            } catch (Exception e) {
                logger.error("Failed to up balance", e);
            }
        } else if (update.hasMessage() && update.getMessage().hasDocument()) {
            chatId = update.getMessage().getChatId().toString();
            userId = update.getMessage().getFrom().getId();
            if(userService.existsByTelegramId(userId)) {
                sendMessage(photoHandler.handleCommands(update));
            } else {
                sendMessage.setChatId(String.valueOf(chatId));
                sendMessage.setText("Чтобы взаимодействовать с сервисом, требуется регистрация.\n\nОтправьте свой номер, чтобы начать.");

                ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
                replyKeyboardMarkup.setSelective(true);
                replyKeyboardMarkup.setResizeKeyboard(true);
                replyKeyboardMarkup.setOneTimeKeyboard(true);

                List<KeyboardRow> keyboard = new ArrayList<>();

                KeyboardRow keyboardFirstRow = new KeyboardRow();
                KeyboardButton keyboardButton = new KeyboardButton();

                keyboardButton.setText("Отправить номер >");
                keyboardButton.setRequestContact(true);
                keyboardFirstRow.add(keyboardButton);

                keyboard.add(keyboardFirstRow);
                replyKeyboardMarkup.setKeyboard(keyboard);

                stateData.setCurrentState(States.ADD_PHONE_USER);

                sendMessage.setReplyMarkup(replyKeyboardMarkup);
                sendMessage(new TelegramSendMessage(sendMessage, chatId));
            }
        }
    }

    public void sendMessage(TelegramMessage telegramMessage) {
        try {
            if(telegramMessage instanceof TelegramSendMessage telegramSendMessage) {
                execute(telegramSendMessage.getSendMessage());
            } else if (telegramMessage instanceof TelegramSendPhoto telegramSendPhoto) {
                execute(telegramSendPhoto.getSendPhoto());
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
