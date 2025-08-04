import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import javax.imageio.ImageIO;

public class Generator {
    static double MAX_PIXEL_DISTANCE = Math.sqrt(255*255 + 255*255 + 255*255);
    int timeline_size = 5;

    int shape_limit = 3000;
    double mutation_factor = 0.4;
    int generation_count = 8;

    // enable if you want to continue from a previous render/image, with filename "output.png".
    boolean use_existing_image = false;
    boolean generate_mask = false;

    // default settings
    public Generator() {
        
    }

    public Generator(int timeline_size, int shape_limit, double mutation_factor, int generation_count) {
        this.timeline_size = timeline_size;
        this.shape_limit = shape_limit;
        this.mutation_factor = mutation_factor;
        this.generation_count = generation_count;
    }

    public void set_const(String[] arguments) {
        // index 0 = set, index 1 = var_name, index 2 = value
        try {
            switch (arguments[1].toLowerCase()) {
                case "shape_limit" -> {
                    this.shape_limit = Integer.parseInt(arguments[2]);
                    System.err.println("Set shape_limit to " + this.shape_limit + ".");
                }
                case "timeline_size" -> {
                    this.timeline_size = Integer.parseInt(arguments[2]);
                    System.err.println("Set timeline_size to " + this.timeline_size + ".");
                }
                case "mutation_factor" -> {
                    this.mutation_factor = Double.parseDouble(arguments[2]);
                    System.err.println("Set mutation_factor to " + this.mutation_factor + ".");
                }
                case "generation_count" -> {
                    this.generation_count = Integer.parseInt(arguments[2]);
                    System.err.println("Set generation_count to " + this.generation_count + ".");
                }
                default -> throw new AssertionError();
            }
        } catch (NumberFormatException e) {
            System.out.println("Unable to set constant " + arguments[1] + " to " + arguments[2] + '.');
        }
    }

    public void generate(String target_path, String output_path) {
        BufferedImage image = read_image(target_path);
        Vector2 dimensions = new Vector2(image.getWidth(), image.getHeight());

        //timelines
        ArrayList<Integer> size_queue = new ArrayList<>();
        ArrayList<Shape> shape_timeline = new ArrayList<>();

        BufferedImage recreation = use_existing_image ? read_image("images/output.png") : new BufferedImage((int) dimensions.x, (int) dimensions.y, 5);
        Graphics2D g = recreation.createGraphics();

        RenderingHints hints = new RenderingHints(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        hints.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHints(hints);

        ShapeManager sm = new ShapeManager(dimensions.x, dimensions.y, image);
        

        for (int i = 0; i < shape_limit; i++) {
            long start = System.nanoTime();

            if (generate_mask) {save_image(generate_image_mask(image, recreation), "images/mask.png");}

            int[] mask_array = generate_mask_array(image, recreation);

            // lst will be sorted already
            ArrayList<Individual> lst = sm.generate_shape_list(300, recreation, get_average_size(size_queue), mask_array);

            if (lst.isEmpty()) {
                // if the algorithm fails to find a shape, perhaps the size queue is inaccurate (got stuck at a wayyy too high value)
                // so we pop them, if size queue becomes empty, the system goes back to random

                if (!size_queue.isEmpty()) { size_queue.remove(0); }
                System.out.println("Failed to generate shape in: " + (System.nanoTime() - start) / 1000000 + "ms.");
                continue;
            }

            // handling shape mutations
            for (int n = 0; n < generation_count; n++) {
                sm.prune_list(lst, 10);
                sm.mutate_list(lst, mutation_factor, 10, recreation);

                // we can sort at the end since shape list is presorted during generation
                lst.sort(Comparator.comparingDouble(ind -> -ind.fitness));
            }

            // postprocessing
            Shape shape = lst.get(0).shape;
            System.out.println("---------------------------------------------------------------------");
            System.out.println(shape.to_string());
            shape.draw(g);

            update_size_queue(size_queue, shape);

            if (get_average_size(size_queue) != null) { 
                System.out.println("The average shape size was: " + get_average_size(size_queue));
            }
            System.out.println("Generated shape " + i + " in: " + (System.nanoTime() - start) / 1000000 + "ms.");
            
            // save image and shape list data
            save_timeline(shape_timeline, "timeline.txt");
            save_image(recreation, output_path);
        }
    }

    static void save_timeline(ArrayList<Shape> shapes, String filepath) {
        try {
            FileWriter writer = new FileWriter("renderdata/" + filepath);
            for (Shape s : shapes) {
                writer.write(s.to_string());
                writer.write('\n');
            }
            writer.close();
            System.out.println("Successfully wrote to the file.");
        } catch (IOException e) {
            System.out.println("An error occurred.");
        }
    }

    public void update_size_queue(ArrayList<Integer> size_queue, Shape shape) {
        int size = shape.get_approximate_size();
        size_queue.add(Math.max(size, 4)); //add a minimum size to prevent having shapes of 1 pixel

        // keep only the most recent 5
        if (size_queue.size() > timeline_size) {
            size_queue.remove(0);
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
}