import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
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

    static final int SHAPE_LIMIT = 3000;
    static final double MUTATION_FACTOR = 0.4;
    static final int GENERATION_COUNT = 8;

    // enable if you want to continue from a previous render/image, with filename "output.png".
    static boolean use_existing_image = true;
    static boolean generate_mask = false;


    public static void main(String[] args) {
        BufferedImage image = read_image("images/inputs/cacti.jpg");
        Vector2 dimensions = new Vector2(image.getWidth(), image.getHeight());

        //timelines
        ArrayList<Integer> size_queue = new ArrayList<>();
        BufferedImage recreation = use_existing_image ? read_image("images/output.png") : new BufferedImage((int) dimensions.x, (int) dimensions.y, 5);
        Graphics2D g = recreation.createGraphics();

        RenderingHints hints = new RenderingHints(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        hints.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHints(hints);

        ShapeManager sm = new ShapeManager(dimensions.x, dimensions.y, image);

        for (int i = 0; i < SHAPE_LIMIT; i++) {
            long start = System.nanoTime();

            if (generate_mask) {
                BufferedImage mask = generate_image_mask(image, recreation);
                save_image(mask, "images/mask.png");
            }

            int[] mask_array = generate_mask_array(image, recreation);

            ArrayList<Individual> lst = sm.generate_shape_list(300, recreation, get_average_size(size_queue), mask_array);

            if (!lst.isEmpty()) {

                // preprocessing
                for (int n = 0; n < GENERATION_COUNT; n++) {
                    lst.sort(Comparator.comparingDouble(ind -> -ind.fitness));
                    sm.prune_list(lst, 10);
                    sm.mutate_list(lst, MUTATION_FACTOR, 10, recreation);
                }

                lst.sort(Comparator.comparingDouble(ind -> -ind.fitness));

                // postprocessing
                Shape shape = lst.get(0).shape;

                int size = shape.get_approximate_size();
                size_queue.add(Math.max(size, 4)); //add a minimum size to prevent having shapes of 1 pixel
                shape.draw(g);

                System.out.println("---------------------------------------------------------------------");
                shape.print_info();

                // keep only the most recent 5
                if (size_queue.size() > TIMELINE_SIZE) {
                    size_queue.remove(0);
                }

                System.out.println(size_queue.toString());

                if (get_average_size(size_queue) != null) {
                    System.out.println("The average shape size was: " + get_average_size(size_queue));
                }
                System.out.println("Generated shape " + i + " in: " + (System.nanoTime() - start) / 1000000 + "ms.");

            } else {
                // if the algorithm fails to find a shape, perhaps the size queue is inaccurate (got stuck at a wayyy too high value)
                // so we pop them, if size queue becomes empty, the system goes back to random

                if (!size_queue.isEmpty()) {
                    size_queue.remove(0);
                }
                System.out.println("Failed to generate shape in: " + (System.nanoTime() - start) / 1000000 + "ms.");
            }
            

            save_image(recreation, "images/output.png");

            if (i % 500 == 0 || i == 250) {
                save_image(recreation, "images/output" + i + ".png");
            }
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
        BufferedImage new_image = new BufferedImage((int) dimensions.x, (int) dimensions.y, BufferedImage.TYPE_INT_ARGB);

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

        for (int y = 0; y < dimensions.y; y++) {
            for (int x = 0; x < dimensions.x; x++) {
                //checks if the pixel color of the temp image isnt equal to 0, the default value
                //means the shape that was drawn includes that pixel
                int distance = (int) ShapeManager.eucl_distance(img1.getRGB(x, y), img2.getRGB(x, y));
                total += distance;
                mask_array[y * dimensions.x + x] = total;
            }
        }

        return mask_array;
    }

    static public BufferedImage read_image(String filepath) {
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
            ImageIO.write(image, "png", outputfile);
        } catch (IOException e) {}
    }

    public static BufferedImage deepCopy(BufferedImage source) {
        BufferedImage bi = new BufferedImage(source.getWidth(), source.getHeight(), source.getType());
        byte[] sourceData = ((DataBufferByte)source.getRaster().getDataBuffer()).getData();
        byte[] biData = ((DataBufferByte)bi.getRaster().getDataBuffer()).getData();
        System.arraycopy(sourceData, 0, biData, 0, sourceData.length);

        return bi;
    }

    public static Integer find_first_greater_than(int[] array, int x) {
        int left = 0;
        int right = array.length - 1;
        Integer result = 0; // default if no greater element is found

        while (left <= right) {
            int mid = left + (right - left) / 2;

            if (array[mid] > x) {
                result = mid;       // candidate for first greater
                right = mid - 1;    // look on the left side
            } else {
                left = mid + 1;     // look on the right side
            }
        }
        return result;
    }  
}