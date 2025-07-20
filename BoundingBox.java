

public class BoundingBox {
    Vector2 top_left;
    Vector2 bot_right;

    public BoundingBox(Vector2 top_left, Vector2 bot_right) {
        this.top_left = top_left;
        this.bot_right = bot_right;
    }

    public void print() {
        this.top_left.print();
        this.bot_right.print();
    }

    public int get_area() {
        return (bot_right.x - top_left.x) * (bot_right.y - top_left.y);
    }
}