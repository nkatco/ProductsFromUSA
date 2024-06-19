package com.example.productsfromusa.utils;

import com.example.productsfromusa.TelegramBot;
import com.example.productsfromusa.callbacks.channel.PreChannelsCallback;
import com.example.productsfromusa.models.Channel;
import com.example.productsfromusa.models.TelegramSendMessage;
import com.example.productsfromusa.models.TelegramSendPhoto;
import com.example.productsfromusa.models.User;
import com.example.productsfromusa.services.PreAnonsService;
import io.jsonwebtoken.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChatAdministrators;
import org.telegram.telegrambots.meta.api.methods.invoices.SendInvoice;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.File;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMember;
import org.telegram.telegrambots.meta.api.objects.payments.LabeledPrice;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.*;
import java.net.URL;
import java.nio.file.Paths;
import java.util.*;

@Component
@Slf4j
public class TelegramBotUtils {
    private static final Logger logger = LoggerFactory.getLogger(TelegramBotUtils.class);
    private final TelegramBot telegramBot;
    private final PreChannelsCallback preChannelsCallback;
    @Value("${images.url.watermark}")
    String pathToSavePhotoWatermarks;
    @Autowired
    public TelegramBotUtils(TelegramBot telegramBot, PreChannelsCallback preChannelsCallback) {
        this.telegramBot = telegramBot;
        this.preChannelsCallback = preChannelsCallback;
    }

    public List<ChatMember> getChatAdministrators(Long chatId){
        List<ChatMember> chatAdministrators = Collections.emptyList();
        try {
            chatAdministrators = telegramBot.execute(new GetChatAdministrators(String.valueOf(chatId)));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
        return chatAdministrators;
    }

    public void sendInvoice(Long chatId, int price) {
        logger.info("Send invoice post for {} with price {}", chatId, price);

        try {
            telegramBot.execute(SendInvoice.builder()
                    .chatId(chatId)
                    .currency("RUB")
                    .providerToken("381764678:TEST:83309")
                    .title("Пополнение кошелька")
                    .description("Пополнение кошелька на " + price)
                    .payload("Тест")
                    .startParameter("StartParameter")
                    .price(new LabeledPrice(price + " RUB",price * 100))
                    .build());
            logger.info("Send invoice for {} in {} sent successfully", chatId, price);
        } catch (TelegramApiException e) {
            logger.error("Failed to send invoice", e);
            throw new RuntimeException(e);
        }
    }

    public void sendMessageForUser(User user, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(user.getChatId());
        message.setText(text);
        telegramBot.sendMessage(new TelegramSendMessage(message, String.valueOf(user.getChatId())));
    }

    public void sendMessageForChannel(Channel channel, InputFile file, String text, String url) {
        SendPhoto message = new SendPhoto();
        message.setChatId(channel.getChatId());
        message.setCaption(text + "\n\n" + url);
        message.setParseMode(ParseMode.HTML);
//        Выключено, пока не подключен домен
//        InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();
//        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
//
//        List<InlineKeyboardButton> rowInLine = new ArrayList<>();
//        var balanceButton = new InlineKeyboardButton();
//
//        balanceButton.setText("Посмотреть товар");
//        balanceButton.setUrl(url);
//        rowInLine.add(balanceButton);
//        rowsInLine.add(rowInLine);
//        markupInLine.setKeyboard(rowsInLine);
//        message.setReplyMarkup(markupInLine);
        message.setPhoto(file);
        logger.info("Send message for {}", channel.getName());
        telegramBot.sendMessage(new TelegramSendPhoto(message, String.valueOf(channel.getChatId())));
    }

    public void sendMessageAnonsPost(long chatId, InputFile file, String text, InlineKeyboardMarkup markupInLine) {
        SendPhoto message = new SendPhoto();
        message.setChatId(chatId);
        message.setCaption(text);
        message.setReplyMarkup(markupInLine);
        message.setParseMode(ParseMode.HTML);

        message.setPhoto(file);
        logger.info("Send messageAnonsPost for {}", chatId);
        telegramBot.sendMessage(new TelegramSendPhoto(message, String.valueOf(chatId)));
    }
    public void sendMessageNullAnonsPost(long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);
        logger.info("Send messageNullAnonsPost for {}", chatId);
        telegramBot.sendMessage(new TelegramSendMessage(message, String.valueOf(chatId)));
    }
    public String downloadPhoto(String fileId) {
        try {
            logger.info("Download photo with fileId {}", fileId);
            GetFile getFileRequest = new GetFile();
            getFileRequest.setFileId(fileId);
            File file = telegramBot.execute(getFileRequest);
            String filePath = file.getFilePath();

            java.io.File dir = new java.io.File(pathToSavePhotoWatermarks);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            String fileUrl = "https://api.telegram.org/file/bot" + telegramBot.getBotToken() + "/" + filePath;

            String generatedFileName = generateFileName() + ".jpg";
            String destinationPath = Paths.get(pathToSavePhotoWatermarks, generatedFileName).toString();

            downloadFile(fileUrl, destinationPath);
            logger.info("Download photo with fileId {} successfully", fileId);
            return destinationPath;
        } catch (TelegramApiException e) {
            logger.error("Failed to download photo", e);
        }
        return null;
    }

    public String generateFileName() {
        return UUID.randomUUID().toString();
    }

    public void downloadFile(String fileUrl, String destinationFile) {
        try (BufferedInputStream in = new BufferedInputStream(new URL(fileUrl).openStream());
             FileOutputStream fileOutputStream = new FileOutputStream(destinationFile)) {
            byte[] dataBuffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                fileOutputStream.write(dataBuffer, 0, bytesRead);
            }
        } catch (IOException e) {
            logger.error("Failed to download file", e);
        } catch (java.io.IOException e) {
            logger.error("Failed to download file", e);
            throw new RuntimeException(e);
        }
    }
}
