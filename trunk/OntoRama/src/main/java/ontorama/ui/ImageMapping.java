package ontorama.ui;

import java.net.URL;

import javax.swing.ImageIcon;

/**
 * <p>Copyright: Copyright (c) DSTC 2002</p>
 * <p>Company: DSTC</p>
 * @todo  null pointer exception handling: need some useful error message (if img dir is not found).
 */

public class ImageMapping {
    private static String dstcLogoLocation = "img/dstcLogo.png";
    private static String kvoLogoLocation = "img/kvo-logo.gif";

    public static ImageIcon dstcLogoImage;
    public static ImageIcon kvoLogoImage;

    public static void loadImages() {
        dstcLogoImage = loadImage(dstcLogoLocation);
        kvoLogoImage = loadImage(kvoLogoLocation);
    }

    private static ImageIcon loadImage(String relativeUri) {
        URL url = ImageMapping.class.getClassLoader().getResource(relativeUri);
        System.out.println("image url = " + url + " for relativeUri = " + relativeUri);
        ImageIcon imageIcon = new ImageIcon(url);
        return imageIcon;
    }
}