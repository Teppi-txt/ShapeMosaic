import java.awt.Color;
import java.awt.Graphics2D;

public class Polygon extends Shape {
    String name;
    Vector2[] vertices;
    int angle;

    public Polygon(Vector2[] vertices, Color color, int angle) { 
        this.name = "Rect";
        this.color = color;

        // position data
        this.vertices = vertices;
        this.angle = angle;
    }

    @Override
    public void draw(Graphics2D g) {
        g.setColor(this.color);

         int[] xPoints = new int[4];
        int[] yPoints = new int[4];

        for (int i = 0; i < 4; i++) {
            xPoints[i] = vertices[i].x;
            yPoints[i] = vertices[i].y;
        }

        g.fillPolygon(xPoints, yPoints, angle);
    }
}