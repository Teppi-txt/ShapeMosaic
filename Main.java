import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Main {
    public static void main(String[] args) {
        // CFrame frame = new CFrame(1200, 900);
        BufferedImage image = read_image("images/test.jpg");
        Vector2 dimensions = new Vector2(image.getWidth(), image.getHeight());

        BufferedImage recreation = new BufferedImage((int) dimensions.x, (int) dimensions.y, 5);
        Graphics2D g = recreation.createGraphics();

        // create a test shape
        Shape shape = new Rect(100, 100, new Vector2(100, 100), Color.BLUE, 45);
        shape.draw(g);

        Shape shape2 = new Ellipse(100, 200, new Vector2(400, 100), Color.GREEN, 0);
        shape2.draw(g);


        save_image(recreation, "images/output.jpg");
    }

    static private BufferedImage read_image(String filepath) {
        try {
            File img = new File(filepath);
            BufferedImage image = ImageIO.read(img); 
            System.out.println(image);
            System.out.println("done");
            return image;
        } catch (IOException e) {
            System.err.println("");
        }
        return null;
    }

    static private void save_image(BufferedImage image, String filepath) {
        try {
            File outputfile = new File(filepath);
            ImageIO.write(image, "jpg", outputfile);
        } catch (IOException e) {}
    }
}