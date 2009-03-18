/*
 * Created by IntelliJ IDEA.
 * User: nataliya
 * Date: 7/08/2002
 * Time: 09:55:48
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package ontorama.views.hyper.view;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import org.tockit.canvas.CanvasItem;

public class SphereView  extends CanvasItem {

    private final double _sphereRadius;
    private Ellipse2D _sphere;
    private static final Color _color = new Color(244, 244, 244);

    public SphereView (double sphereRadius) {
        _sphereRadius = sphereRadius;
        initSphere();
    }

    private void initSphere () {
        double x = -_sphereRadius;
        double y = -_sphereRadius;
        double width = _sphereRadius*2;
        double height = _sphereRadius*2;
        _sphere = new Ellipse2D.Double(x, y, width, height);
    }

    @Override
    public void draw(final Graphics2D g2d) {
        g2d.setColor(_color);
        g2d.fill(_sphere);
    }

    @Override
    public Rectangle2D getCanvasBounds(Graphics2D g2d) {
       return _sphere.getBounds2D();
    }

    @Override
    public boolean containsPoint(Point2D point) {
        return _sphere.contains(point);
    }

    @Override
    public boolean hasAutoRaise() {
            return false;
        }

    @Override
    public Point2D getPosition() {
        return new Point2D.Double(0,0);
    }
}
