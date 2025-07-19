import java.awt.Color;
import java.awt.Graphics2D;

public class Polygon extends Shape {
    Vector2[] vertices;
    int angle;

    public Polygon(Vector2[] vertices, int angle, Color color) { 
        this.type = "Polygon";
        this.color = color;

        // position data
        this.vertices = vertices;
        this.angle = angle;
    }

    public Polygon(Vector2[] vertices, int angle) { 
        this.type = "Polygon";
        this.color = Color.BLACK;

        // position data
        this.vertices = vertices;
        this.angle = angle;
    }

    @Override
    public void draw(Graphics2D g) {
        g.setColor(this.color);

        int n = vertices.length;

        int[] xPoints = new int[n];
        int[] yPoints = new int[n];

        for (int i = 0; i < n; i++) {
            xPoints[i] = vertices[i].x;
            yPoints[i] = vertices[i].y;
        }

        g.fillPolygon(xPoints, yPoints, n);
    }

    @Override
    public BoundingBox get_bounding_box() {
        Vector2 min_point = new Vector2(Integer.MAX_VALUE, Integer.MAX_VALUE);
        Vector2 max_point = new Vector2(Integer.MIN_VALUE, Integer.MIN_VALUE);

        for (Vector2 v : this.vertices) {
            if (v.x < min_point.x) {min_point.x = v.x;}

            if (v.x > max_point.x) {max_point.x = v.x;}

            if (v.y < min_point.y) {min_point.y = v.y;}

            if (v.y > max_point.y) {max_point.y = v.y;}
        }

        return new BoundingBox(min_point, max_point);
    }
}