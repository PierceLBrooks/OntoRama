package ontorama.hyper.view.simple;

/**
 * For now labels are just drawn on top of nodes.
 * This layer simple enables labels to be drawn last.
 */

import ontorama.hyper.canvas.CanvasItem;
import ontorama.hyper.canvas.CanvasManager;
import ontorama.hyper.view.simple.HyperNodeView;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

public class LabelView extends CanvasItem{

    /**
     * Hold the model for this view.
     */
    private HyperNodeView hyperNodeView;

    public LabelView( HyperNodeView hyperNodeView ) {
        this.hyperNodeView = hyperNodeView;
    }
    /**
     * The maximum number of fonts.
     */
    private static final int MAXFONTS = 10;

    /**
     * Stores the different size fonts to enable the labels
     * to get smaller as the node move away from the center.
     *
     * Fonts sizes are stored to improve performance.
     * Calculating the font sizes by using a scale factor
     * seems to be very expencive in Java.
     */
    private static Font[] fonts = new Font[ MAXFONTS ];

    /**
     * Load new fonts into array.
     */
    static {
        for( int i = 0; i < MAXFONTS; i++) {
            fonts[i] = new Font("Arial", Font.PLAIN, i + 3);
        }
    }

    /**
     * Get the font size to display.
     */
    private Font getFontToDisplay( double scale ) {
        if(scale == 1) {
            return fonts[MAXFONTS-1];
        }
        int size = (int)((scale * MAXFONTS ) % MAXFONTS);
        return fonts[size];
    }

    public void draw( Graphics2D g2d ) {
        double scale = hyperNodeView.getScale();
//        System.out.println( "scale: " + scale );
        if( scale < .1 ) {
            return;
        }
//      removed for now to improve performance
//      float fontSize = (float)(14 * scale);
//      g2d.setFont(g2d.getFont().deriveFont(fontSize));
        g2d.setFont( getFontToDisplay( scale ) );
        FontMetrics fm = g2d.getFontMetrics();
        String label = this.hyperNodeView.getName();
        double x = hyperNodeView.getProjectedX();
        double y = hyperNodeView.getProjectedY();
        double labelWidth = fm.stringWidth( label );
        labelWidth = labelWidth + fm.getLeading() + fm.getDescent();
        double labelHeight = fm.getHeight();
        double xPos = x - labelWidth/2;
        double yPos = y - labelHeight/2;
        AlphaComposite myAlpha = AlphaComposite.getInstance( AlphaComposite.SRC_OVER,0.6f);
        g2d.setComposite(myAlpha);
        g2d.setColor( Color.white);
        g2d.fill( new RoundRectangle2D.Double( xPos, yPos, labelWidth, labelHeight, 8, 8 ) );
        g2d.setComposite(AlphaComposite.SrcOver);
        g2d.setColor( hyperNodeView.getNodeFadeColor() );
        g2d.draw( new RoundRectangle2D.Double( xPos, yPos, labelWidth, labelHeight, 8, 8 ) );
        g2d.setColor( Color.black );
        g2d.drawString( this.hyperNodeView.getName(), (int)(xPos ), (int)(y + labelHeight/4));
    }
}