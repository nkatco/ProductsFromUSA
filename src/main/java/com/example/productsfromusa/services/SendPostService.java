package com.example.productsfromusa.services;

import com.example.productsfromusa.models.ChannelSettings;
import com.example.productsfromusa.models.Post;
import com.example.productsfromusa.models.SendMessagePost;
import com.example.productsfromusa.services.data.ChannelSettingsService;
import com.example.productsfromusa.services.data.PostService;
import com.example.productsfromusa.services.data.USDService;
import com.example.productsfromusa.utils.CurrencyUtils;
import com.example.productsfromusa.utils.GraphicUtils;
import com.vdurmont.emoji.EmojiParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.InputFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

@Component
public class SendPostService {
    private static final Logger logger = LoggerFactory.getLogger(SendPostService.class);
    @Autowired
    public USDService usdService;
    @Autowired
    public ChannelSettingsService channelSettingsService;
    @Autowired
    public CurrencyUtils currencyUtils;
    @Autowired
    public GraphicUtils graphicUtils;
    public SendMessagePost getPostByChannelSettings(Post post, ChannelSettings channelSettings) {
        logger.info("Edit post for {}", post.getId());
        String text = "";
        try {
            if(channelSettings.getCourse() == null) {
                channelSettings.setCourse("USD");
            }
            if(channelSettings.getReduction() == 0) {
                channelSettings.setReduction(10);
            }
            channelSettingsService.saveChannelSettings(channelSettings);
            text = "<b>" + post.getName() + "</b>" + "\n\n";
            text += post.getText() + "\n\n";
            if(channelSettings.getPostText() != null) {
                text += channelSettings.getPostText() + "\n\n";
            }
            if(channelSettings.isShowPrice()) {
                if(channelSettings.isShowOldPrice()) {
                    double sm = post.getOldPrice();
                    double mn = 0;
                    String txt = "";
                    String txtt = "";
                    if(channelSettings.getCourse().equals("USD")) {
                        mn = sm / (usdService.getCourse().getCourse()); // конечная цена в долларах
                        txt = "$";
                        mn = currencyUtils.convertUSD(mn, channelSettings.getReduction());
                        String formattedNumber = String.format("%02.2f", mn);
                        txtt += "<s>" + formattedNumber + "</s>" + txt;
                    } else if (channelSettings.getCourse().equals("RUB")){
                        mn = sm; // конечная цена в рублях
                        txt = "₽";
                        int intValue = (int) mn;
                        intValue = currencyUtils.convertRub(intValue, channelSettings.getReduction());
                        txtt += "<s>" + intValue + "</s>" + txt;
                    }
                    text += EmojiParser.parseToUnicode(txtt + " :arrow_right: ");
                }
                if(channelSettings.getAddCourse() != 0) {
                    double sm = (post.getPrice() * (1 + channelSettings.getAddCourse() / 100));
                    double mn = 0;
                    String txt = "";
                    if(channelSettings.getCourse().equals("USD")) {
                        mn = sm / (usdService.getCourse().getCourse()); // конечная цена в долларах
                        txt = "$";
                        mn = currencyUtils.convertUSD(mn, channelSettings.getReduction());
                        String formattedNumber = String.format("%02.2f", mn);
                        text += formattedNumber + txt;
                    } else if (channelSettings.getCourse().equals("RUB")){
                        mn = sm; // конечная цена в рублях
                        txt = "₽";
                        int intValue = (int) mn;
                        intValue = currencyUtils.convertRub(intValue, channelSettings.getReduction());
                        text += intValue + txt;
                    }
                } else {
                    double sm = post.getPrice();
                    double mn = 0;
                    String txt = "";
                    if(channelSettings.getCourse().equals("USD")) {
                        mn = sm / (usdService.getCourse().getCourse()); // конечная цена в долларах
                        txt = "$";
                        mn = currencyUtils.convertUSD(mn, channelSettings.getReduction());
                        String formattedNumber = String.format("%02.2f", mn);
                        text += formattedNumber + txt;
                    } else if (channelSettings.getCourse().equals("RUB")){
                        mn = sm; // конечная цена в рублях
                        txt = "₽";
                        int intValue = (int) mn;
                        intValue = currencyUtils.convertRub(intValue, channelSettings.getReduction());
                        text += intValue + txt;
                    }
                }
                if(channelSettings.getPriceNote() != null) {
                    text += channelSettings.getPriceNote() + "\n\n";
                } else {
                    text += "\n\n";
                }
            }
        } catch (Exception e) {
            logger.error("Failed to edit post", e);
            e.printStackTrace();
        }
        if(channelSettings.getWatermarkImage() != null && channelSettings.getWatermarkImage().getPath() != null) {
            try {
                BufferedImage mainImage = ImageIO.read(new File(post.getImage()));
                BufferedImage watermarkImage = ImageIO.read(new File(channelSettings.getWatermarkImage().getPath()));

                int w = 150;
                int h = 150;
                if (channelSettings.getWatermarkImage().getSize() != 0) {
                    w = channelSettings.getWatermarkImage().getSize();
                    h = channelSettings.getWatermarkImage().getSize();
                }
                int x = 0;
                int y = 0;
                if (channelSettings.getWatermarkImage().getMode().equals("center")) {
                    x = (mainImage.getWidth() - w) / 2;
                    y = (mainImage.getHeight() - h) / 2;
                } else if (channelSettings.getWatermarkImage().getMode().equals("corner")) {
                    if (channelSettings.getWatermarkImage().getMode().equals("center")) {
                        x = (mainImage.getWidth() - w) / 2;
                        y = (mainImage.getHeight() - h) / 2;
                    }
                }
                float alpha = 0.5f;
                if (channelSettings.getWatermarkImage().getAlpha() != 0) {
                    alpha = channelSettings.getWatermarkImage().getAlpha();
                }
                System.out.println(channelSettings.getWatermarkImage().getPath());
                File tempFile = graphicUtils.addWatermarkToImage(mainImage, watermarkImage, alpha, channelSettings.getWatermarkImage().getMode(), w, h);
                if(tempFile != null) {
                    InputFile file = new InputFile(tempFile);
                    return new SendMessagePost(text, file);
                }
            } catch (Exception e) {
                logger.error("Failed to edit post", e);
                e.printStackTrace();
            }
        }
        InputFile file = new InputFile(new File(post.getImage()));
        return new SendMessagePost(text, file);
    }
    public SendMessagePost getPostByChannelSettingsWithoutWatermark(Post post, ChannelSettings channelSettings) {
        String text = "";
        try {
            if(channelSettings.getCourse() == null) {
                channelSettings.setCourse("USD");
            }
            if(channelSettings.getReduction() == 0) {
                channelSettings.setReduction(10);
            }
            channelSettingsService.saveChannelSettings(channelSettings);
            text = "<b>" + post.getName() + "</b>" + "\n\n";
            text += post.getText() + "\n\n";
            if(channelSettings.getPostText() != null) {
                text += channelSettings.getPostText() + "\n\n";
            }
            if(channelSettings.isShowPrice()) {
                if(channelSettings.isShowOldPrice()) {
                    double sm = post.getOldPrice();
                    double mn = 0;
                    String txt = "";
                    String txtt = "";
                    if(channelSettings.getCourse().equals("USD")) {
                        mn = sm / (usdService.getCourse().getCourse()); // конечная цена в долларах
                        txt = "$";
                        mn = currencyUtils.convertUSD(mn, channelSettings.getReduction());
                        String formattedNumber = String.format("%02.2f", mn);
                        txtt += "<s>" + formattedNumber + "</s>" + txt;
                    } else if (channelSettings.getCourse().equals("RUB")){
                        mn = sm; // конечная цена в рублях
                        txt = "₽";
                        int intValue = (int) mn;
                        intValue = currencyUtils.convertRub(intValue, channelSettings.getReduction());
                        txtt += "<s>" + intValue + "</s>" + txt;
                    }
                    text += EmojiParser.parseToUnicode(txtt + " :arrow_right: ");
                }
                if(channelSettings.getAddCourse() != 0) {
                    double sm = (post.getPrice() * (1 + channelSettings.getAddCourse() / 100));
                    double mn = 0;
                    String txt = "";
                    if(channelSettings.getCourse().equals("USD")) {
                        mn = sm / (usdService.getCourse().getCourse()); // конечная цена в долларах
                        txt = "$";
                        mn = currencyUtils.convertUSD(mn, channelSettings.getReduction());
                        String formattedNumber = String.format("%02.2f", mn);
                        text += formattedNumber + txt;
                    } else if (channelSettings.getCourse().equals("RUB")){
                        mn = sm; // конечная цена в рублях
                        txt = "₽";
                        int intValue = (int) mn;
                        intValue = currencyUtils.convertRub(intValue, channelSettings.getReduction());
                        text += intValue + txt;
                    }
                } else {
                    double sm = post.getPrice();
                    double mn = 0;
                    String txt = "";
                    if(channelSettings.getCourse().equals("USD")) {
                        mn = sm / (usdService.getCourse().getCourse()); // конечная цена в долларах
                        txt = "$";
                        mn = currencyUtils.convertUSD(mn, channelSettings.getReduction());
                        String formattedNumber = String.format("%02.2f", mn);
                        text += formattedNumber + txt;
                    } else if (channelSettings.getCourse().equals("RUB")){
                        mn = sm; // конечная цена в рублях
                        txt = "₽";
                        int intValue = (int) mn;
                        intValue = currencyUtils.convertRub(intValue, channelSettings.getReduction());
                        text += intValue + txt;
                    }
                }
                if(channelSettings.getPriceNote() != null) {
                    text += channelSettings.getPriceNote() + "\n\n";
                } else {
                    text += "\n\n";
                }
            }
        } catch (Exception e) {
            logger.error("Failed to edit post", e);
        }
        if(channelSettings.getWatermarkImage() != null && channelSettings.getWatermarkImage().getPath() != null) {
            try {
                BufferedImage mainImage = ImageIO.read(new File(post.getImage()));

                File tempFile = new File(post.getImage());
                InputFile file = new InputFile(tempFile);
                return new SendMessagePost(text, file);
            } catch (Exception e) {
                logger.error("Failed to edit post", e);
            }
        }
        InputFile file = new InputFile(new File(post.getImage()));
        return new SendMessagePost(text, file);
    }
}
