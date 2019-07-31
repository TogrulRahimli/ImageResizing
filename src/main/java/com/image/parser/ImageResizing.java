package com.image.parser;

import com.image.model.Size;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static java.lang.Double.*;

public class ImageResizing {

    public static void main(String[] args) throws IOException {
        BufferedImage image = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        byte[] array = convertBufferedImageToByteArray(image);
    }

    public static BufferedImage createBufferedImageByArray(byte[] imageArray) throws IOException {
        try (InputStream inputStream = new ByteArrayInputStream(imageArray)) {
            BufferedImage bufferedImage = ImageIO.read(inputStream);
            return bufferedImage;
        }
    }

    private static Size calculateCompatibleResizeingSize(BufferedImage bufferedImage, Size imageSize) {
        double ratio = Math.min(valueOf(imageSize.getWidth()) / valueOf(bufferedImage.getWidth())
                , valueOf(imageSize.getHeight()) / valueOf(bufferedImage.getHeight()));

        int width = valueOf(bufferedImage.getWidth() * ratio).intValue();
        int height = valueOf(bufferedImage.getHeight() * ratio).intValue();
        return new Size(width, height);
    }

    public static byte[] resizeImage(BufferedImage img, Size targetSize) throws IOException {

        // this line for resize proporsional
        targetSize = calculateCompatibleResizeingSize(img, targetSize);

        int type = (img.getTransparency() == Transparency.OPAQUE) ? BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB;
        BufferedImage ret = img;
        BufferedImage scratchImage = null;
        Graphics2D g2 = null;

        int w = img.getWidth();
        int h = img.getHeight();

        int prevW = w;
        int prevH = h;

        do {
            if (w > targetSize.getWidth()) {
                w /= 2;
                w = (w < targetSize.getWidth()) ? targetSize.getWidth() : w;
            }

            if (h > targetSize.getHeight()) {
                h /= 2;
                h = (h < targetSize.getHeight()) ? targetSize.getHeight() : h;
            }

            if (scratchImage == null) {
                scratchImage = new BufferedImage(w, h, type);
                g2 = scratchImage.createGraphics();
            }

            g2.drawImage(ret, 0, 0, w, h, 0, 0, prevW, prevH, null);
            g2.setComposite(AlphaComposite.Src);
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            prevW = w;
            prevH = h;
            ret = scratchImage;
        } while (w != targetSize.getWidth() || h != targetSize.getHeight());

        if (g2 != null) {
            g2.dispose();
        }

        if (targetSize.getWidth() != ret.getWidth() || targetSize.getHeight() != ret.getHeight()) {
            scratchImage = new BufferedImage(targetSize.getWidth(), targetSize.getHeight(), type);
            g2 = scratchImage.createGraphics();
            g2.drawImage(ret, 0, 0, null);
            g2.dispose();
            ret = scratchImage;
        }

        return convertBufferedImageToByteArray(ret);
    }

    private static byte[] convertBufferedImageToByteArray(BufferedImage image) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(image, "jpg", baos);
            return baos.toByteArray();
        }
    }
}
