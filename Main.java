import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import javax.imageio.ImageIO;

public class Main {
    public static void main(String[] args) {
        // CFrame frame = new CFrame(1200, 900);
        BufferedImage image = read_image("images/test.jpg");
        Vector2 dimensions = new Vector2(image.getWidth(), image.getHeight());

        BufferedImage recreation = new BufferedImage((int) dimensions.x, (int) dimensions.y, 5);
        Graphics2D g = recreation.createGraphics();
        ShapeManager sm = new ShapeManager(dimensions.x, dimensions.y, image);

        for (int i = 0; i < 1000; i++) {
            long start = System.nanoTime();
            ArrayList<Individual> lst = sm.generate_shape_list(200, recreation);

            if (!lst.isEmpty()) {

                for (int n = 0; n < 3; n++) {
                    lst.sort(Comparator.comparingDouble(ind -> -ind.fitness));
                    sm.prune_list(lst, 10);
                    sm.mutate_list(lst, 0.1, 4, recreation);
                    System.out.println(lst.size());
                }
                lst.sort(Comparator.comparingDouble(ind -> -ind.fitness));
                Shape shape = lst.get(0).shape;
                shape.draw(g);
            }
            System.out.println("Generated shape in: " + (System.nanoTime() - start) / 1000000 + "ms");

            save_image(recreation, "images/output.jpg");
        }
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