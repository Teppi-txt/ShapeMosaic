import java.awt.Color;
import java.awt.Graphics2D;

public class Rect extends Shape {
    String name;
    int width; int height;
    Vector2 top_left;
    int angle;

    public Rect(int width, int height, Vector2 top_left, Color color, int angle) { 
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

        long startTime = System.nanoTime();

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

        long endTime = System.nanoTime();

        long durationInNano = endTime - startTime;
        long durationInMillis = durationInNano / 1_000_000;

        System.out.println("Execution time in nanoseconds: " + durationInNano);
    }

    // methods no longer in use but i want to keep them
    public Vector2[] generate_vertices() {
        Vector2[] v_list = new Vector2[4];
        Vector2 center = new Vector2(top_left.x + width / 2, top_left.y + height / 2);

        v_list[0] = rotate_point(top_left, center, angle);
        v_list[1] = rotate_point(new Vector2(top_left.x, top_left.y + height), center, angle);
        v_list[2] = rotate_point(new Vector2(top_left.x + width, top_left.y + height), center, angle);
        v_list[3] = rotate_point(new Vector2(top_left.x + width, top_left.y), center, angle);

        return v_list;
    }

    public Vector2 rotate_point(Vector2 point, Vector2 pivot, int angle) {
        double sin = Math.sin(Math.toRadians(angle));
        double cos = Math.cos(Math.toRadians(angle));

        // translate point to origin
        double ox = point.x - pivot.x;
        double oy = point.y - pivot.y;

        Vector2 rotated = new Vector2((int) (ox * cos - oy * sin) + pivot.x, (int) (ox * sin + oy * cos) + pivot.y);
        return rotated;
    }
}