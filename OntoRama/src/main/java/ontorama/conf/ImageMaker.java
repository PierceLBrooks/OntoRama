package ontorama.conf;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

public class ImageMaker {

    private static int height = 15;
    private static int width = 20;

    public static Image getImage(Color color, String symbol) {
        BufferedImage image = new BufferedImage(ImageMaker.width, ImageMaker.height, BufferedImage.TYPE_INT_RGB);

        Graphics2D g = image.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(color);
        g.fillRect(0, 0, ImageMaker.width, ImageMaker.height);
        g.setColor(Color.black);
        g.drawRect(0, 0, ImageMaker.width - 1, ImageMaker.height - 1);

        Font font = g.getFont();
        int fontSize = font.getSize();

        int y = (height - fontSize) / 2 + fontSize;
        g.drawString(symbol, 5, y);

        return image;
    }

    public static int getWidth() {
        return ImageMaker.width;
    }

    public static int getHeight() {
        return ImageMaker.height;
    }
}