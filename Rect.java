import java.awt.Color;
import java.awt.Graphics2D;

public class Rect extends Shape {
    int width; int height;
    Vector2 top_left;
    int angle;

    public Rect(int width, int height, Vector2 top_left, int angle, Color color) { 
        this.type = "Rect";
        this.color = color;

        // position data
        this.width = width;
        this.height = height;
        this.top_left = top_left;
        this.angle = angle;
    }

    public Rect(int width, int height, Vector2 top_left, int angle) { 
        this.type = "Rect";

        // position data
        this.width = width;
        this.height = height;
        this.top_left = top_left;
        this.angle = angle;

        this.color = Color.BLACK;
    }

    @Override
    public void draw(Graphics2D g) {
        g.setColor(this.color);

        // Vector2[] vertices = generate_vertices();

        // int[] xPoints = new int[4];
        // int[] yPoints = new int[4];

        // for (int i = 0; i < 4; i++) {
        //     xPoints[i] = vertices[i].x;
        //     yPoints[i] = vertices[i].y;
        // }

        // g.fillPolygon(xPoints, yPoints, 4);
        
        g.rotate(Math.toRadians(angle), top_left.x + width/2.0, top_left.y + height/2.0);
        g.fillRect(top_left.x, top_left.y, width, height);
        g.rotate(Math.toRadians(-angle), top_left.x + width/2.0, top_left.y + height/2.0);
    }

    // methods no longer in use but i want to keep them
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

    @Override
    public void print_info() {
        System.out.printf("Rectangle Info:%nWidth: %d%nHeight: %d%nTop Left: (%d, %d)%nAngle: %d degrees%n", 
        this.width, this.height, this.top_left.x, this.top_left.y, this.angle);
    }

    @Override
    public String to_string() {
        String template = "Rect(width: " + this.width + ", height: " + this.height + ", top_left: (" + this.top_left.x + 
                          ", " + this.top_left.y + "), angle: " + this.angle + ", color: " + this.color.toString() + "))";
        return template;
    }

    @Override
    public Shape mutate(double mutation_factor) {
        int new_width = (int) (this.width * 1 + random_factor(mutation_factor));
        int new_height = (int) (this.height * 1 + random_factor(mutation_factor));
        Vector2 new_position = new Vector2(this.top_left.x + (int) (this.width * random_factor(mutation_factor)), 
                                           this.top_left.y + (int) (this.height * random_factor(mutation_factor)));
        int new_angle = (int) (this.angle + random_factor(mutation_factor) * 10);
        // 0.1 mutation factor results in angle variance of 10*
        return new Rect(new_width, new_height, new_position, new_angle, this.color);
    }

    @Override
    public int get_approximate_size() {
        return this.width * this.height;
    }
}