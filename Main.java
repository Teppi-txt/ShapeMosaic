import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import javax.imageio.ImageIO;

public class Main {
    public static void main(String[] args) {
        // CFrame frame = new CFrame(1200, 900);
        BufferedImage image = read_image("images/grid.jpeg");
        Vector2 dimensions = new Vector2(image.getWidth(), image.getHeight());

        BufferedImage recreation = new BufferedImage((int) dimensions.x, (int) dimensions.y, 5);
        Graphics2D g = recreation.createGraphics();
        ShapeManager sm = new ShapeManager(dimensions.x, dimensions.y, image);

        Map map = sm.generate_shape_list(200, recreation);

        map.forEach((key, value) -> System.out.println(key + " " + value));
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