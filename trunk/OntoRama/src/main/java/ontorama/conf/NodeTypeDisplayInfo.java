package ontorama.conf;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

public class NodeTypeDisplayInfo {

    private String _name;
    private Shape _shape;
    private boolean _forceUprightShape;
    private Image _image;
    private Image _cloneImage;
    private Color _color;
    private Color _cloneColor;

    public NodeTypeDisplayInfo(String name, Shape shape, boolean forceUprightShape, Color normalColor, Color cloneColor) {
    	_name = name;
        _shape = shape;
        _forceUprightShape = forceUprightShape;
        _image = makeNodeIcon(normalColor, Color.BLACK, shape);
        _cloneImage = makeNodeIcon(cloneColor, Color.BLACK, shape);
        _color = normalColor;
        _cloneColor = cloneColor;
    }

    public Color getColor() {
        return _color;
    }

    public Color getCloneColor() {
        return _cloneColor;
    }

    public Image getImage() {
        return _image;
    }

    public Image getCloneImage() {
        return _cloneImage;
    }

    /**
     * The shape used to display the node.
     *
     * The shape is centered around the (0,0) point, its size can be arbitrary.
     */
    public Shape getShape () {
    	return _shape;
    }

    /**
     * Returns true if the shape object has to be displayed upright.
     *
     * This shold be true for labels or text shapes, false otherwise.
     */
    public boolean forceUprightShape() {
    	return _forceUprightShape;
    }

    public String getName() {
    	return _name;
    }

    private static Image makeNodeIcon(Color color, Color outlineColor, Shape shape) {
        int width = ImageMaker.getWidth()/2;
        int height = ImageMaker.getHeight();

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2 = image.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Rectangle2D bounds = shape.getBounds2D();

        double xOffset = -bounds.getX();
        double yOffset = -bounds.getY();
        double scale;
        if(bounds.getWidth()/width > bounds.getHeight()/height) {
            scale = width/bounds.getWidth();
            yOffset += (bounds.getHeight() - scale * height)/2;
        } else {
            scale = height/bounds.getHeight();
            xOffset += (bounds.getWidth() - scale * width)/2;
        }

        g2.scale(scale,scale);
        g2.translate(xOffset, yOffset);

        g2.setColor(color);
        g2.fill(shape);
        g2.setColor(outlineColor);
        g2.draw(shape);

        return image;
    }
}
