/*
 * Created by IntelliJ IDEA.
 * User: nataliya
 * Date: 7/08/2002
 * Time: 09:55:48
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package ontorama.hyper.view.simple;

import org.tockit.canvas.CanvasItem;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Point2D;

public class SphereView  extends CanvasItem {

    private double _sphereRadius;

    private double _x;
    private double _y;
    private double _width;
    private double _height;
    private Ellipse2D _sphere;

    public SphereView (double sphereRadius) {
        _sphereRadius = sphereRadius;
        initSphere();
    }

    private void initSphere () {
        _x = -_sphereRadius;
        _y = -_sphereRadius;
        _width = _sphereRadius*2;
        _height = _sphereRadius*2;
        _sphere = new Ellipse2D.Double(_x, _y, _width, _height);
    }

    public void draw(Graphics2D g2d) {
        g2d.setColor(new Color(244, 244, 244));
        g2d.fill(_sphere);
    }

   public Rectangle2D getCanvasBounds(Graphics2D g2d) {
       return _sphere.getBounds2D();
   }

    public boolean containsPoint(Point2D point) {
        return _sphere.contains(point);
    }

    public boolean hasAutoRaise() {
            return false;
        }


}
