package ontorama.ontologyConfig;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author
 * @version 1.0
 */

public class ImageMaker {

    private static int height = 15;
    private static int width = 20;


    public static Image getImage(Color color, String symbol) {
        Image image = new BufferedImage(ImageMaker.width, ImageMaker.height, BufferedImage.TYPE_INT_RGB);

        Graphics g = image.getGraphics();
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

    /**
     *
     */
    public static int getWidth() {
        return ImageMaker.width;
    }

    /**
     *
     */
    public static int getHeight() {
        return ImageMaker.height;
    }
}