package ontorama.view;

import java.net.URL;

import javax.swing.ImageIcon;

import ontorama.OntoramaConfig;


/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) DSTC 2002</p>
 * <p>Company: DSTC</p>
 *  unascribed
 * @version 1.0
 * @todo  null pointer exception handling: need some usefull errormessage (if img dir is not found).
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
        URL url = OntoramaConfig.getClassLoader().getResource(relativeUri);
        System.out.println("image url = " + url + " for relativeUri = " + relativeUri);
        ImageIcon imageIcon = new ImageIcon(url);
        return imageIcon;
    }
}