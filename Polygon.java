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

    @Override
    public void print_info() {
        System.out.printf("Polygon Info:%nAngle: %d degrees%nVertices:%n", angle);
        for (int i = 0; i < vertices.length; i++) {
            System.out.printf("  Vertex %d: (%d, %d)%n", i + 1, vertices[i].x, vertices[i].y);
        }
    }

    @Override
    public Shape mutate(double mutation_factor) {
        BoundingBox box = get_bounding_box();
        Vector2 dimensions = box.bot_right.subtracted(box.top_left);
        Vector2[] new_vertices = new Vector2[this.vertices.length];

        for (int i = 0; i < this.vertices.length; i++) {
            new_vertices[i] = new Vector2(this.vertices[i].x + (int) (dimensions.x * 1 + random_factor(mutation_factor)), 
                                          this.vertices[i].y + (int) (dimensions.y * 1 + random_factor(mutation_factor)));                              
        }

        return new Polygon(new_vertices, this.angle, this.color);
    }
}