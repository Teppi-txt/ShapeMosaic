import java.awt.Color;
import java.awt.Graphics2D;

public class Ellipse extends Shape {
    int width; int height;
    Vector2 top_left;
    int angle;

    public Ellipse(int width, int height, Vector2 top_left, Color color, int angle) { 
        this.type = "Ellipse";
        this.color = color;

        // position data
        this.width = width;
        this.height = height;
        this.top_left = top_left;
        this.angle = angle;
    }

    public Ellipse(int width, int height, Vector2 top_left, int angle) { 
        this.type = "Ellipse";
        this.color = Color.BLACK;

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

    @Override
    public BoundingBox get_bounding_box() {
        Vector2[] v_list = new Vector2[4];
        Vector2 center = new Vector2(top_left.x + width / 2, top_left.y + height / 2);

        v_list[0] = rotate_point(top_left, center, angle);
        v_list[1] = rotate_point(new Vector2(top_left.x, top_left.y + height), center, angle);
        v_list[2] = rotate_point(new Vector2(top_left.x + width, top_left.y + height), center, angle);
        v_list[3] = rotate_point(new Vector2(top_left.x + width, top_left.y), center, angle);

        Vector2 min_point = v_list[0].copy();
        Vector2 max_point = v_list[0].copy();

        for (int i = 1; i < v_list.length; i++) {
            if (v_list[i].x < min_point.x) {
                min_point.x = v_list[i].x;
            } else if (v_list[i].x > max_point.x) {
                max_point.x = v_list[i].x;
            }

            if (v_list[i].y < min_point.y) {
                min_point.y = v_list[i].y;
            } else if (v_list[i].y > max_point.y) {
                max_point.y = v_list[i].y;
            }
        }

        return new BoundingBox(min_point, max_point);
    }
}