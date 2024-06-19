package com.example.productsfromusa.utils;

import com.example.productsfromusa.services.SendPostService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;

@Component
public class GraphicUtils {
    private static final Logger logger = LoggerFactory.getLogger(GraphicUtils.class);

    public File addWatermarkToImage(BufferedImage mainImage, BufferedImage watermarkImage, float alpha, String mode, int w, int h) {
        try {
            Graphics2D g2d = (Graphics2D) mainImage.getGraphics();

            AlphaComposite alphaChannel = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha);
            g2d.setComposite(alphaChannel);

            int mainImageWidth = mainImage.getWidth();
            int mainImageHeight = mainImage.getHeight();
            int padding = 20;

            if (mode.equals("center") || mode.equals("full")) {
                int centerX = (mainImageWidth - w) / 2;
                int centerY = (mainImageHeight - h) / 2;
                g2d.drawImage(watermarkImage, centerX, centerY, w, h, null);
            }

            if (mode.equals("corner") || mode.equals("full")) {
                // Верхний левый угол
                g2d.drawImage(watermarkImage, padding, padding, w, h, null);
                // Верхний правый угол
                g2d.drawImage(watermarkImage, mainImageWidth - w - padding, padding, w, h, null);
                // Нижний левый угол
                g2d.drawImage(watermarkImage, padding, mainImageHeight - h - padding, w, h, null);
                // Нижний правый угол
                g2d.drawImage(watermarkImage, mainImageWidth - w - padding, mainImageHeight - h - padding, w, h, null);
            }

            g2d.dispose();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(mainImage, "jpg", baos);

            byte[] imageBytes = baos.toByteArray();

            File outputImageFile = File.createTempFile("watermarked_image", ".jpg");
            java.nio.file.Files.write(outputImageFile.toPath(), imageBytes);

            return outputImageFile;
        } catch (Exception e) {
            logger.error("Failed to add watermark", e);
        }
        return null;
    }
}
