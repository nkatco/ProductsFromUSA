package com.example.productsfromusa.handlers;

import com.example.productsfromusa.callbacks.*;
import com.example.productsfromusa.callbacks.anons.*;
import com.example.productsfromusa.callbacks.channel.*;
import com.example.productsfromusa.callbacks.profile.MoneyCallback;
import com.example.productsfromusa.callbacks.profile.ProfileCallback;
import com.example.productsfromusa.callbacks.profile.TokensCallback;
import com.example.productsfromusa.callbacks.profile.WalletHistoryCallback;
import com.example.productsfromusa.callbacks.statistic.StatisticCallback;
import com.example.productsfromusa.callbacks.statistic.StatisticInfoCallback;
import com.example.productsfromusa.callbacks.token.*;
import com.example.productsfromusa.commands.ChannelSettingsAddPriceNoteCommand;
import com.example.productsfromusa.models.TelegramMessage;
import com.example.productsfromusa.models.TelegramSendMessage;
import com.example.productsfromusa.utils.Consts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import java.util.HashMap;
import java.util.Map;

@Component
public class CallbacksHandler {

    private final Map<String, CallbackHandler> callbacks;

    public CallbacksHandler(@Autowired AnonsCallback anonsCallback,
                            @Autowired ProfileCallback profileCallback,
                            @Autowired WalletHistoryCallback walletHistoryCallback,
                            @Autowired TokensCallback tokensCallback,
                            @Autowired MoneyCallback moneyCallback,
                            @Autowired GuideCallback guideCallback,
                            @Autowired ErrorCallback errorCallback,
                            @Autowired StartCallback startCallback,
                            @Autowired ChannelsCallback channelsCallback,
                            @Autowired PreChannelsCallback preChannelsCallback,
                            @Autowired AddChannelsCallback addChannelsCallback,
                            @Autowired ChannelSettingsCallback channelSettingsCallback,
                            @Autowired ChannelRemove1Callback channelRemove1Callback,
                            @Autowired ChannelRemove2Callback channelRemove2Callback,
                            @Autowired BuyTokenCallback buyTokenCallback,
                            @Autowired BuyToken2Callback buyToken2Callback,
                            @Autowired TokenInfoCallback tokenInfoCallback,
                            @Autowired TokenGuideCallback tokenGuideCallback,
                            @Autowired TokenRemove1Callback tokenRemove1Callback,
                            @Autowired TokenRemove2Callback tokenRemove2Callback,
                            @Autowired AddAnonsCallback addAnonsCallback,
                            @Autowired AddAnons2Callback addAnons2Callback,
                            @Autowired AddAnons3Callback addAnons3Callback,
                            @Autowired AddAnons4Callback addAnons4Callback,
                            @Autowired AddAnons5Callback addAnons5Callback,
                            @Autowired AddAnons6Callback addAnons6Callback,
                            @Autowired ChangeAnonsCategoryCallback changeAnonsCategoryCallback,
                            @Autowired ChangeAnonsCategory2Callback changeAnonsCategory2Callback,
                            @Autowired AnonsSettingsCallback anonsSettingsCallback,
                            @Autowired RemoveAnonsPostCallback removeAnonsPostCallback,
                            @Autowired ChannelSettingsAddShowPriceCallback channelSettingsAddShowPriceCallback,
                            @Autowired ChannelSettingsAddShowPriceCallback2 channelSettingsAddShowPriceCallback2,
                            @Autowired ChannelSettingsAddShowPriceCallback3 channelSettingsAddShowPriceCallback3,
                            @Autowired ChannelSettingsAddTextNoteCallback channelSettingsAddTextNoteCallback,
                            @Autowired ChannelSettingsAddPriceNoteCallback channelSettingsAddPriceNoteCallback,
                            @Autowired ChannelSettingsAddSurchargeCallback channelSettingsAddSurchargeCallback,
                            @Autowired ChannelSettingsRemoveSurchargeCallback channelSettingsRemoveSurchargeCallback,
                            @Autowired ChannelSettingsSetRUBCallback channelSettingsSetRUBCallback,
                            @Autowired ChannelSettingsSetUSDCallback channelSettingsSetUSDCallback,
                            @Autowired ChannelSettingsRemovePriceNoteCallback channelSettingsRemovePriceNoteCallback,
                            @Autowired ChannelSettingsRemovePostTextCallback channelSettingsRemovePostTextCallback,
                            @Autowired ChannelSettingsSetFinalReductionCallback channelSettingsSetFinalReductionCallback,
                            @Autowired ChannelSettingsSetReductionCallback channelSettingsSetReductionCallback,
                            @Autowired ChannelSettingsAddWatermarkCallback channelSettingsAddWatermarkCallback,
                            @Autowired ChannelSettingsSetWatermarkCallback channelSettingsSetWatermarkCallback,
                            @Autowired ChannelSettingsRemoveWatermarkCallback channelSettingsRemoveWatermarkCallback,
                            @Autowired ChannelSettingsAddOldShowPriceCallback channelSettingsAddOldShowPriceCallback,
                            @Autowired ChannelSettingsAddOldShowPriceCallback2 channelSettingsAddOldShowPriceCallback2,
                            @Autowired ChannelSettingsAddOldShowPriceCallback3 channelSettingsAddOldShowPriceCallback3,
                            @Autowired StatisticCallback statisticCallback,
                            @Autowired StatisticInfoCallback statisticInfoCallback) {
        this.callbacks = new HashMap<>();
        callbacks.put(CallbackType.ANONS_BUTTON, anonsCallback);
        callbacks.put(CallbackType.PROFILE_BUTTON, profileCallback);
        callbacks.put(CallbackType.HISTORY_BALANCE, walletHistoryCallback);
        callbacks.put(CallbackType.USER_TOKENS, tokensCallback);
        callbacks.put(CallbackType.UP_BALANCE, moneyCallback);
        callbacks.put(CallbackType.GUIDE_BUTTON, guideCallback);
        callbacks.put(CallbackType.CHANNELS_BUTTON, channelsCallback);
        callbacks.put(CallbackType.ERROR_BUTTON, errorCallback);
        callbacks.put(CallbackType.MENU_BUTTON, startCallback);
        callbacks.put(CallbackType.ADD_CHANNELS, preChannelsCallback);
        callbacks.put(CallbackType.CHANNEL_ADD, addChannelsCallback);
        callbacks.put(CallbackType.CHANNEL_SETTINGS, channelSettingsCallback);
        callbacks.put(CallbackType.CHANNEL_REMOVE1, channelRemove1Callback);
        callbacks.put(CallbackType.CHANNEL_REMOVE2, channelRemove2Callback);
        callbacks.put(CallbackType.BUY_TOKEN, buyTokenCallback);
        callbacks.put(CallbackType.BUY_TOKEN_FINAL, buyToken2Callback);
        callbacks.put(CallbackType.INFO_TOKEN, tokenInfoCallback);
        callbacks.put(CallbackType.REMOVE_TOKEN1, tokenRemove1Callback);
        callbacks.put(CallbackType.REMOVE_TOKEN2, tokenRemove2Callback);
        callbacks.put(CallbackType.CREATE_ANONS1, addAnonsCallback);
        callbacks.put(CallbackType.CREATE_ANONS2, addAnons2Callback);
        callbacks.put(CallbackType.CREATE_ANONS3, addAnons3Callback);
        callbacks.put(CallbackType.CREATE_ANONS4, addAnons4Callback);
        callbacks.put(CallbackType.CREATE_ANONS5, addAnons5Callback);
        callbacks.put(CallbackType.CREATE_ANONS6, addAnons6Callback);
        callbacks.put(CallbackType.CHANGE_ANONS_CATEGORY, changeAnonsCategoryCallback);
        callbacks.put(CallbackType.CHANGE_ANONS_CATEGORY2, changeAnonsCategory2Callback);
        callbacks.put(CallbackType.ANONS_SETTINGS, anonsSettingsCallback);
        callbacks.put(CallbackType.REMOVE_ANONS_POST, removeAnonsPostCallback);
        callbacks.put(CallbackType.ADD_SHOW_PRICE, channelSettingsAddShowPriceCallback);
        callbacks.put(CallbackType.ADD_SHOW_PRICE2, channelSettingsAddShowPriceCallback2);
        callbacks.put(CallbackType.ADD_SHOW_PRICE3, channelSettingsAddShowPriceCallback3);
        callbacks.put(CallbackType.ADD_POST_TEXT, channelSettingsAddTextNoteCallback);
        callbacks.put(CallbackType.ADD_PRICE_NOTE, channelSettingsAddPriceNoteCallback);
        callbacks.put(CallbackType.ADD_COURSE, channelSettingsAddSurchargeCallback);
        callbacks.put(CallbackType.SET_RUB, channelSettingsSetRUBCallback);
        callbacks.put(CallbackType.SET_USD, channelSettingsSetUSDCallback);
        callbacks.put(CallbackType.REMOVE_POST_TEXT, channelSettingsRemovePostTextCallback);
        callbacks.put(CallbackType.REMOVE_PRICE_NOTE, channelSettingsRemovePriceNoteCallback);
        callbacks.put(CallbackType.SET_REDUCTION, channelSettingsSetReductionCallback);
        callbacks.put(CallbackType.SET_REDUCTION_10, channelSettingsSetFinalReductionCallback);
        callbacks.put(CallbackType.SET_REDUCTION_100, channelSettingsSetFinalReductionCallback);
        callbacks.put(CallbackType.SET_REDUCTION_9, channelSettingsSetFinalReductionCallback);
        callbacks.put(CallbackType.SET_REDUCTION_99, channelSettingsSetFinalReductionCallback);
        callbacks.put(CallbackType.ADD_WATERMARK, channelSettingsAddWatermarkCallback);
        callbacks.put(CallbackType.CENTER_WATERMARK, channelSettingsSetWatermarkCallback);
        callbacks.put(CallbackType.CORNER_WATERMARK, channelSettingsSetWatermarkCallback);
        callbacks.put(CallbackType.FULL_WATERMARK, channelSettingsSetWatermarkCallback);
        callbacks.put(CallbackType.LIGHT_WATERMARK, channelSettingsSetWatermarkCallback);
        callbacks.put(CallbackType.MEDIUM_WATERMARK, channelSettingsSetWatermarkCallback);
        callbacks.put(CallbackType.HARD_WATERMARK, channelSettingsSetWatermarkCallback);
        callbacks.put(CallbackType.LITTLE_WATERMARK, channelSettingsSetWatermarkCallback);
        callbacks.put(CallbackType.SMALL_WATERMARK, channelSettingsSetWatermarkCallback);
        callbacks.put(CallbackType.BIG_WATERMARK, channelSettingsSetWatermarkCallback);
        callbacks.put(CallbackType.REMOVE_WATERMARK, channelSettingsRemoveWatermarkCallback);
        callbacks.put(CallbackType.ADD_SHOW_OLD_PRICE, channelSettingsAddOldShowPriceCallback);
        callbacks.put(CallbackType.ADD_SHOW_OLD_PRICE2, channelSettingsAddOldShowPriceCallback2);
        callbacks.put(CallbackType.ADD_SHOW_OLD_PRICE3, channelSettingsAddOldShowPriceCallback3);
        callbacks.put(CallbackType.GUIDE_TOKEN, tokenGuideCallback);
        callbacks.put(CallbackType.REMOVE_COURSE, channelSettingsRemoveSurchargeCallback);
        callbacks.put(CallbackType.SHOW_STATISTIC, statisticCallback);
        callbacks.put(CallbackType.INFO_STATISTIC, statisticInfoCallback);
    }

    public TelegramMessage handleCallbacks(Update update) {
        String callbackData = update.getCallbackQuery().getData();
        long chatId = update.getCallbackQuery().getMessage().getChatId();

        SendMessage answer = null;
        TelegramMessage telegramMessage = null;
        try {
            CallbackHandler callbackBiFunction = callbacks.get(callbackData);
            if (callbackBiFunction != null) {
                telegramMessage = callbackBiFunction.apply(update);
            } else {
                for (String key : callbacks.keySet()) {
                    if (callbackData.startsWith(key)) {
                        callbackBiFunction = callbacks.get(key);
                        break;
                    }
                }
                if(callbackBiFunction != null) {
                    telegramMessage = callbackBiFunction.apply(update);
                    telegramMessage.setChatId(String.valueOf(chatId));
                } else {
                    answer = new SendMessage();
                    answer.setChatId(chatId);
                    answer.setText(Consts.ERROR);
                    telegramMessage = new TelegramSendMessage(answer, String.valueOf(chatId));
                    telegramMessage.setChatId(String.valueOf(chatId));
                }
            }
        } catch (Exception e) {
            answer = new SendMessage();
            answer.setChatId(chatId);
            answer.setText(Consts.ERROR);
            System.out.println(e);
        }
        if(telegramMessage != null) {
            telegramMessage.setChatId(String.valueOf(chatId));
            assert answer != null;
            return telegramMessage;
        } else {
            answer = new SendMessage();
            answer.setText(Consts.ERROR);
            answer.setChatId(update.getCallbackQuery().getMessage().getChatId());
            return new TelegramSendMessage(answer, String.valueOf(chatId));
        }
    }
}
