import java.awt.Color;
import java.awt.Graphics2D;

public class Line extends Shape {
    Vector2 start; Vector2 end;

    public Line(Vector2 start, Vector2 end, Color color) { 
        this.type = "Line";
        this.color = color;
        this.start = start;
        this.end = end;
    }

    public Line(Vector2 start, Vector2 end) { 
        this.type = "Line";
        this.start = start;
        this.end = end;
    }

    @Override
    public void draw(Graphics2D g) {
        g.setColor(this.color);
        g.drawLine(start.x, start.y, end.x, end.y);
    }

    @Override
    public BoundingBox get_bounding_box() {

        Vector2 min_point = new Vector2(Math.min(this.start.x, this.end.x), Math.min(this.start.y, this.end.y));
        Vector2 max_point = new Vector2(Math.max(this.start.x, this.end.x), Math.max(this.start.y, this.end.y));

        return new BoundingBox(min_point, max_point);
    }
}
