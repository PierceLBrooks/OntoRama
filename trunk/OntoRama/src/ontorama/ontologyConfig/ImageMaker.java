package ontorama.ontologyConfig;

import java.awt.image.*;
import java.awt.*;

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
        Image image = new BufferedImage(ImageMaker.width,ImageMaker.height,BufferedImage.TYPE_INT_RGB);

        Graphics g = image.getGraphics();
        g.setColor(color);
        g.fillRect(0,0,ImageMaker.width,ImageMaker.height);
        g.setColor(Color.black);
        g.drawRect(0,0,ImageMaker.width-1,ImageMaker.height-1);
        g.drawString(symbol,5,10);

        return image;
    }
}