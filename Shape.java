import java.awt.Color;
import java.awt.Graphics2D;

public class Shape {
    String name;
    Color color;


    // overides
    public void draw(Graphics2D g){
        throw new Error("Invalid draw call on empty shape.");
    }

    public void print_info(){
        throw new Error("Invalid print call on empty shape.");
    }

    public void bounding_box() {
        throw new Error("Invalid bounding box call on empty shape.");
    }

    public String get_name() {
        return this.name;
    }

    public Color get_color() {
        return this.color;
    }
}

