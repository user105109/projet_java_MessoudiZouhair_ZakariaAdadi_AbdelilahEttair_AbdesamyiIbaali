import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

/**
 * Représente une forme dessinée. Les points d'un crayon/gomme sont stockés
 * comme une liste de petits segments afin que le dessin puisse être redessiné.
 */
public class DrawingShape {
    private final Tool tool;
    private final Color color;
    private final float strokeWidth;
    private final List<Line2D.Double> freehandSegments;
    private final Shape shape;

    private DrawingShape(Tool tool, Color color, float strokeWidth,
                         List<Line2D.Double> segments, Shape shape) {
        this.tool = tool;
        this.color = color;
        this.strokeWidth = strokeWidth;
        this.freehandSegments = segments;
        this.shape = shape;
    }

    public static DrawingShape freehand(List<Line2D.Double> segments,
                                        Color color, float width, Tool tool) {
        return new DrawingShape(tool, color, width,
                new ArrayList<>(segments), null);
    }

    public static DrawingShape geometric(Tool tool, Color color,
                                          float width, Shape shape) {
        return new DrawingShape(tool, color, width, null, shape);
    }

    public void draw(Graphics2D g2) {
        g2.setColor(color);
        g2.setStroke(new BasicStroke(strokeWidth, BasicStroke.CAP_ROUND,
                BasicStroke.JOIN_ROUND));

        if (freehandSegments != null) {
            for (Line2D.Double segment : freehandSegments) {
                g2.draw(segment);
            }
        } else if (shape != null) {
            g2.draw(shape);
        }
    }

    public static Shape createShape(Tool tool, int x1, int y1, int x2, int y2) {
        int left = Math.min(x1, x2);
        int top = Math.min(y1, y2);
        int width = Math.abs(x2 - x1);
        int height = Math.abs(y2 - y1);

        switch (tool) {
            case LINE:
                return new Line2D.Double(x1, y1, x2, y2);
            case RECTANGLE:
                return new Rectangle2D.Double(left, top, width, height);
            case ELLIPSE:
                return new Ellipse2D.Double(left, top, width, height);
            default:
                return null;
        }
    }
}
