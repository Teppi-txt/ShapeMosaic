import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Main {
    public static void main(String[] args) {
        // CFrame frame = new CFrame(1200, 900);
        BufferedImage image = read_image("images/grid.jpeg");
        Vector2 dimensions = new Vector2(image.getWidth(), image.getHeight());

        BufferedImage recreation = new BufferedImage((int) dimensions.x, (int) dimensions.y, 5);
        var g = recreation.createGraphics();

        // for (int i = 0; i < 3; i++) {
        //     // create a test shape
        //     ShapeManager sm = new ShapeManager(dimensions.x, dimensions.y, image);
        //     Shape shape = sm.generateShape();
        //     shape.draw(g);

        //     System.out.println(shape.get_type());

        //     BoundingBox box = shape.get_bounding_box();
        //     dimensions = box.bot_right.subtracted(box.top_left);
        //     g.drawRect(box.top_left.x, box.top_left.y, dimensions.x, dimensions.y);

            
        //     save_image(ShapeManager.deepCopy(recreation), "images/copy" + i + ".jpg");
        // }

        ShapeManager sm = new ShapeManager(dimensions.x, dimensions.y, image);
        // Shape shape = sm.generateShape();

        dimensions.print();

        Shape shape = new Rect(350, 339, new Vector2(628, 215), 201);
        shape.get_bounding_box().print();
        shape.set_color(sm.getAverageColorOfShape(shape, image));

        System.out.println(ShapeManager.squared_evaluation(image, image, shape));

        shape.draw(g);
    
        save_image(recreation, "images/output.jpg");
    }

    static private BufferedImage read_image(String filepath) {
        try {
            File img = new File(filepath);
            BufferedImage image = ImageIO.read(img); 
            return image;
        } catch (IOException e) {
            System.err.println("");
        }
        return null;
    }
    static public void save_image(BufferedImage image, String filepath) {
        try {
            File outputfile = new File(filepath);
            ImageIO.write(image, "jpg", outputfile);
        } catch (IOException e) {}
    }
}