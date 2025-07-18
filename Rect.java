import java.awt.Color;
import java.awt.Graphics;

public class Rect extends Shape {
    String name;
    int width; int height;
    Vector2 top_left;

    public Rect(int width, int height, Vector2 top_left, Color color) { 
        this.name = "Rect";
        this.color = color;

        // position data
        this.width = width;
        this.height = height;
        this.top_left = top_left;
    }

    public void draw(Graphics g) {
        g.setColor(this.color);
        g.fillRect(this.top_left.x, this.top_left.y, this.width, this.height);
    }
    
}