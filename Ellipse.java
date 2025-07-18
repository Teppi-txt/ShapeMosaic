import java.awt.Color;
import java.awt.Graphics2D;

public class Ellipse extends Shape {
    String name;
    int width; int height;
    Vector2 top_left;
    int angle;

    public Ellipse(int width, int height, Vector2 top_left, Color color, int angle) { 
        this.name = "Rect";
        this.color = color;

        // position data
        this.width = width;
        this.height = height;
        this.top_left = top_left;
        this.angle = angle;
    }

    @Override
    public void draw(Graphics2D g) {
        g.setColor(this.color);
        
        g.rotate(Math.toRadians(angle), top_left.x + width/2.0, top_left.y + height/2.0);
        g.fillOval(top_left.x, top_left.y, width, height);
        g.rotate(Math.toRadians(-angle), top_left.x + width/2.0, top_left.y + height/2.0);
    }
}