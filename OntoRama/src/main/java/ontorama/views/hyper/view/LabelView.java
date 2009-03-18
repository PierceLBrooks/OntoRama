package ontorama.views.hyper.view;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

import org.tockit.canvas.CanvasItem;

/**
 * For now labels are just drawn on top of nodes.
 * This layer simply enables labels to be drawn last.
 */
public class LabelView extends CanvasItem {

    /**
     * Hold the model for this ui.
     */
    private HyperNodeView hyperNodeView = null;

    /**
     * The maximum number of fonts.
     */
    private static final int MAXFONTS = 10;
    
	private static final int MIN_FONT_SIZE = 3;

	private static final int MAX_FONT_SIZE = 15;

    /**
     * Stores the different size fonts to enable the labels
     * to get smaller as the node move away from the center.
     *
     * Fonts sizes are stored to improve performance.
     * Calculating the font sizes by using a scale factor
     * seems to be very expencive in Java.
     */
    private static Font[] fonts = new Font[MAXFONTS];

    /**
     * Load new fonts into array.
     */
    static {
        for (int i = 0; i < MAXFONTS; i++) {
            fonts[i] = new Font("Arial", Font.PLAIN, (MIN_FONT_SIZE + i * ((MAX_FONT_SIZE - MIN_FONT_SIZE) / (MAXFONTS-1))));
        }
    }

    public LabelView(HyperNodeView hyperNodeView) {
        this.hyperNodeView = hyperNodeView;
    }

    public boolean hasHyperNodeView(HyperNodeView hyperNodeView) {
        return this.hyperNodeView == hyperNodeView;
    }

    @Override
    public boolean containsPoint(Point2D point) {
        return false;
    }

    @Override
    public Rectangle2D getCanvasBounds(Graphics2D g2d) {
    	double x = hyperNodeView.getProjectedX();
    	double y = hyperNodeView.getProjectedY();
    	if (!this.hyperNodeView.getVisible()) {
    		return new Rectangle2D.Double(x,y,0,0);
    	}

        g2d.setFont(getFontToDisplay(hyperNodeView.getScale()));
        FontMetrics fm = g2d.getFontMetrics();
        String label = getContentString();
        double labelWidth = fm.stringWidth(label);
        labelWidth = labelWidth + fm.getLeading() + fm.getDescent();
        double labelHeight = fm.getHeight();
        double xPos = x - labelWidth / 2;
        double yPos = y - labelHeight / 2;

        Rectangle2D retVal = new Rectangle2D.Double(xPos, yPos, labelWidth, labelHeight);

        return retVal;
    }

    private String getContentString() {
		return hyperNodeView.getName();
    }

    /**
     * Get the font size to display.
     */
    private Font getFontToDisplay(double scale) {
        if (scale == 1) {
            return fonts[MAXFONTS - 1];
        }
        int size = (int) ((scale * MAXFONTS) % MAXFONTS);
        return fonts[size];
    }

    @Override
    public void draw(Graphics2D g2d) {
    	if (!this.hyperNodeView.getVisible()) {
    		return;
    	}

        Rectangle2D rect = getCanvasBounds(g2d);
        double scale = hyperNodeView.getScale();
        if (scale < .1) {
            return;
        }

        if (!(SimpleHyperView.getSelectedLabelView() == this)) {
            AlphaComposite myAlpha = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f);
            g2d.setComposite(myAlpha);
        }
        double xPos = rect.getX();
        double yPos = rect.getY();
        double labelWidth = rect.getWidth();
        double labelHeight = rect.getHeight();
        RoundRectangle2D.Double roundRect = new RoundRectangle2D.Double(xPos, yPos, labelWidth, labelHeight, 8, 8);
        g2d.setColor(Color.white);
        g2d.fill(roundRect);
        g2d.setComposite(AlphaComposite.SrcOver);
        g2d.setColor(hyperNodeView.getNodeFadeColor());
        g2d.draw(roundRect);
        g2d.setColor(Color.black);
        g2d.drawString(getContentString(), (int) (xPos), (int) (hyperNodeView.getProjectedY() + labelHeight / 4));
    }

    @Override
    public Point2D getPosition() {
        return this.hyperNodeView.getPosition();
    }
}