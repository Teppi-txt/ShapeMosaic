import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import javax.imageio.ImageIO;

public class Main {
    static final double MAX_PIXEL_DISTANCE = Math.sqrt(255*255 + 255*255 + 255*255);
    static final int TIMELINE_SIZE = 5;


    public static void main(String[] args) {
        // CFrame frame = new CFrame(1200, 900);
        BufferedImage image = read_image("images/test.jpg");
        Vector2 dimensions = new Vector2(image.getWidth(), image.getHeight());

        //timelines
        ArrayList<Integer> size_queue = new ArrayList<>();

        // BufferedImage recreation = new BufferedImage((int) dimensions.x, (int) dimensions.y, 5);
        BufferedImage recreation = read_image("images/output.jpg");
        Graphics2D g = recreation.createGraphics();

        ShapeManager sm = new ShapeManager(dimensions.x, dimensions.y, image);

        for (int i = 0; i < 10000; i++) {
            // calculate average size of shape

            long start = System.nanoTime();
            ArrayList<Individual> lst = sm.generate_shape_list(200, recreation, get_average_size(size_queue));
            BufferedImage mask = generate_image_mask(image, recreation);

            if (!lst.isEmpty()) {

                // preprocessing
                for (int n = 0; n < 5; n++) {
                    lst.sort(Comparator.comparingDouble(ind -> -ind.fitness));
                    sm.prune_list(lst, 10);
                    sm.mutate_list(lst, 0.4, 4, recreation);
                    System.out.println(lst.size());
                }

                lst.sort(Comparator.comparingDouble(ind -> -ind.fitness));

                // postprocessing
                Shape shape = lst.get(0).shape;

                int size = shape.get_approximate_size();
                size_queue.add(size);
                shape.draw(g);
                shape.print_info();

                // keep only the most recent 5
                if (size_queue.size() > TIMELINE_SIZE) {
                    size_queue.remove(0);
                }

                System.out.println(size_queue.toString());
                if (get_average_size(size_queue) != null) {
                    System.out.println("The average shape size was: " + get_average_size(size_queue));
                }
            } else {
                // if the algorithm fails to find a shape, perhaps the size queue is inaccurate (got stuck at a wayyy too high value)
                // so we pop them, if size queue becomes empty, the system goes back to random
                if (!size_queue.isEmpty()) {
                    size_queue.remove(0);
                }
            }
            
            System.out.println("Generated shape in: " + (System.nanoTime() - start) / 1000000 + "ms.");

            save_image(recreation, "images/output.jpg");
            save_image(mask, "images/mask.jpg");
        }
    }

    static Integer get_average_size(ArrayList<Integer> lst) {
        int size_sum = 0;
        for (Integer size : lst) {
            size_sum += size;
        }
        if (!lst.isEmpty()) {
            return size_sum / lst.size();
        } 
        return null;
    }

    static BufferedImage generate_image_mask(BufferedImage img1, BufferedImage img2) {
        // loop through every pixel in the image
        // compare the distance between 2 pixel rgbs, 0 is no difference, so write to black
        // max difference is sqrt(255^2 + 255^2 + 255^2)

        Vector2 dimensions = new Vector2(img1.getWidth(), img1.getHeight());
        BufferedImage new_image = new BufferedImage((int) dimensions.x, (int) dimensions.y, 5);

        for (int x = 0; x < dimensions.x; x++) {
            for (int y = 0; y < dimensions.y; y++) {
                //checks if the pixel color of the temp image isnt equal to 0, the default value
                //means the shape that was drawn includes that pixel
                double distance = ShapeManager.eucl_distance(img1.getRGB(x, y), img2.getRGB(x, y));
                double brightness = distance / MAX_PIXEL_DISTANCE;

                new_image.setRGB((int) x, (int) y, new Color((int) (255 * brightness), (int) (255 * brightness), (int) (255 * brightness)).getRGB());
            }
        }
        return new_image;
    }

    static int[] generate_mask_array(BufferedImage img1, BufferedImage img2) {
        // loop through every pixel in the image
        // compare the distance between 2 pixel rgbs, 0 is no difference, so write to black
        // max difference is sqrt(255^2 + 255^2 + 255^2)

        Vector2 dimensions = new Vector2(img1.getWidth(), img1.getHeight());
        int[] mask_array = new int[dimensions.x * dimensions.y];
        int total = 0;

        for (int x = 0; x < dimensions.x; x++) {
            for (int y = 0; y < dimensions.y; y++) {
                //checks if the pixel color of the temp image isnt equal to 0, the default value
                //means the shape that was drawn includes that pixel
                double distance = ShapeManager.eucl_distance(img1.getRGB(x, y), img2.getRGB(x, y));
                total += distance;
                mask_array[y * dimensions.x + x] = total;
            }
        }
        return mask_array;
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

    public static BufferedImage deepCopy(BufferedImage source) {
        BufferedImage bi = new BufferedImage(source.getWidth(), source.getHeight(), source.getType());
        byte[] sourceData = ((DataBufferByte)source.getRaster().getDataBuffer()).getData();
        byte[] biData = ((DataBufferByte)bi.getRaster().getDataBuffer()).getData();
        System.arraycopy(sourceData, 0, biData, 0, sourceData.length);

        return bi;
    }
}