import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.*;

public class ShapeManager {
    int maxX; int maxY; BufferedImage target;
    static final int EMPTY_PIXEL = 0xFF000000;
    Random random;

    enum SHAPE {
        Rect,
        Polygon,
        Ellipse
    }

    public ShapeManager (int maxX, int maxY, BufferedImage target) {
        this.maxX = maxX;
        this.maxY = maxY;
        this.target = target;
        this.random = new Random();
    }

    public Shape generateShape(Integer area, int[] position_mask) {
        SHAPE shape = SHAPE.values()[new Random().nextInt(SHAPE.values().length)];
        Shape returnShape = null;
        int width; int height;

        int angle = (int) (Math.random() * 360);

        //generates random dimensions for the shape, making sure it doesnt surpass the bounds of the image
        if (area != null) {
            int weighted_area = (int) (random.nextGaussian() * (area/4) + area);
            width = Math.clamp((int) (Math.random() * weighted_area), 1, this.maxX);
            height = Math.clamp((int) (weighted_area / width), 1, this.maxY);
        } else {
            width = Math.max(1, (int) (Math.random() * maxX));
            height = Math.max(1, (int) (Math.random() * maxY));
        }

        // this set of operations ensures the shape can be anywhere on the screen
        // so position mask is a series of increasing values corresponding to pixels

        // get a random value within the mask
        int mask_value = (int) (Math.random() * position_mask[position_mask.length - 1]);
        int index = Main.find_first_greater_than(position_mask, mask_value);

        // isolate coordinates based on array index
        int x = index % maxX;
        int y = index / maxX;

        Vector2 position = new Vector2(x - width/2, y - height/2);

        switch (shape) {
            case SHAPE.Rect -> {
                //rectangle case
                returnShape = new Rect(width, height, position, angle);
            }
            case SHAPE.Ellipse -> {
                returnShape = new Ellipse(width, height, position, angle);
            }
            case SHAPE.Polygon -> {
                int polygonSize = (int) (Math.random() * 2) + 3;
                Vector2[] vertices = new Vector2[polygonSize];

                for (int point = 0; point < polygonSize; point++) {
                    vertices[point] = new Vector2((int) (Math.random() * maxX), (int) (Math.random() * maxY));
                }

                returnShape = new Polygon(vertices, angle);
            }
        }
        
        assert returnShape != null;
        returnShape.set_color(getAverageColorOfShape(returnShape, target));
        return returnShape;
    }

    public Color getAverageColorOfShape(Shape shape, BufferedImage target) {
        //sets up a blank image with the same dimensions as the target

        BufferedImage tempImage = new BufferedImage(this.maxX, this.maxY, 5);
        int redSum = 0; int blueSum = 0; int greenSum = 0; int count = 0; //counts the sums of colors and n

        shape.set_color(Color.WHITE); //sets a temp color for the shape
        shape.draw(tempImage.createGraphics()); //draws the shape onto the tempImage

        BoundingBox box = shape.get_bounding_box();

        // TIL: loop bounds are calculated during each loop iteration
        int startX = Math.max(0, box.top_left.x);
        int endX = Math.min(box.bot_right.x, this.maxX);
        int startY = Math.max(0, box.top_left.y);
        int endY = Math.min(box.bot_right.y, this.maxY);

        for (int x = startX; x < endX; x++) {
            for (int y = startY; y < endY; y++) {
                //checks if the pixel color of the temp image isnt equal to 0, the default value
                //means the shape that was drawn includes that pixel
                if (tempImage.getRGB(x, y) != EMPTY_PIXEL) {
                    int pixelColor = target.getRGB(x, y);
                    count += 1;
                    redSum += (pixelColor & 0xff0000) >> 16;
                    greenSum += (pixelColor & 0xff00) >> 8;
                    blueSum += (pixelColor & 0xff);
                }
            }
        }

        if (count > 0) return new Color(redSum/count, greenSum/count, blueSum/count, 255);
        return Color.BLUE;
    }

    public ArrayList<Individual> generate_shape_list(int length, BufferedImage current, Integer area, int[] mask_array) {
        ArrayList<Individual> returnList = new ArrayList<>();

        if (target.getWidth() != current.getWidth() || target.getHeight() != current.getHeight()) {
            System.out.println("Unable to compare images with different dimensions.");
            return returnList;
        }

        // pure random (20%)
        // we need some pure randomness for generation diversity, otherwise we could be finding local maxima
        for (int i = 0; i < length * 0.2; i++) {
            Shape shape = generateShape(null, mask_array);
            double eval = squared_evaluation(target, current, shape);

            if (eval > 0) {
                returnList.add(new Individual(shape, squared_evaluation(target, current, shape)));
            }
        }

        // skewed to average size
        for (int i = 0; i < length * 0.8; i++) {
            Shape shape = generateShape(area, mask_array);
            double eval = squared_evaluation(target, current, shape);

            if (eval > 0) {
                returnList.add(new Individual(shape, squared_evaluation(target, current, shape)));
            }
        }

        return returnList;
    }

    public void prune_list(ArrayList<Individual> list, int survivors) {
        if (list.size() > survivors) {
            list.subList(survivors, list.size()).clear();
        }
    }

    public void mutate_list(ArrayList<Individual> list, double mutation_factor, int mutants_per_individual, BufferedImage current) {
        ArrayList<Individual> mutants_list = new ArrayList<>();
        for (Individual i : list) {
            for (int m = 0; m < mutants_per_individual; m++) {
                Shape mutated_shape = i.shape.mutate(mutation_factor);
                mutated_shape.set_color(getAverageColorOfShape(mutated_shape, target));
                double eval = squared_evaluation(target, current, mutated_shape);

                if (eval > i.fitness) {
                    mutants_list.add(new Individual(mutated_shape, squared_evaluation(target, current, mutated_shape)));
                }
            }
        }
        list.addAll(mutants_list);
    }

    public static double squared_evaluation(BufferedImage target, BufferedImage current, Shape shape) {

        double improvement = 0;
        BufferedImage after = new BufferedImage(target.getWidth(), target.getHeight(), 5);
        shape.draw(after.createGraphics());
        // loop over the bounding box of the shape in the image

        BoundingBox box = shape.get_bounding_box();

        // long startTime = System.nanoTime();
    
        int startX = Math.max(0, box.top_left.x);
        int endX = Math.min(box.bot_right.x, target.getWidth());
        int startY = Math.max(0, box.top_left.y);
        int endY = Math.min(box.bot_right.y, target.getHeight());

        for (int x = startX; x < endX; x++) {
            for (int y = startY; y < endY; y++) {
                //checks if the pixel color of the temp image isnt equal to 0, the default value
                //means the shape that was drawn includes that pixel

                //positive is closer, negative is further

                //TODO: optimise this to use getRGB array (this doesnt work idk why)
                int afterPixel = after.getRGB(x, y);
                
                if (afterPixel != EMPTY_PIXEL) {
                    int beforePixel = current.getRGB(x, y);
                    int targetPixel = target.getRGB(x, y);

                    // get the difference (improvement)
                   improvement += eucl_distance(beforePixel, targetPixel) - eucl_distance(afterPixel, targetPixel);
                }
            }
        }
        return improvement;
    }

    public static Vector2 generate_arbitrary_dimensions(int area) {
        int width = (int) (Math.random() * area);
        int length = (int) (area / width);
        return new Vector2(width, length);
    }

    public static double manh_distance(int pixel1, int pixel2) {
        int r1 = (pixel1 >> 16) & 0xFF;
        int g1 = (pixel1 >> 8) & 0xFF;
        int b1 = pixel1 & 0xFF;

        int r2 = (pixel2 >> 16) & 0xFF;
        int g2 = (pixel2 >> 8) & 0xFF;
        int b2 = pixel2 & 0xFF;

        return Math.abs(r1 - r2) + Math.abs(g1 - g2) + Math.abs(b1 - b2);
    }

    public static double eucl_distance(int pixel1, int pixel2) {
        int r1 = (pixel1 >> 16) & 0xFF;
        int g1 = (pixel1 >> 8) & 0xFF;
        int b1 = pixel1 & 0xFF;

        int r2 = (pixel2 >> 16) & 0xFF;
        int g2 = (pixel2 >> 8) & 0xFF;
        int b2 = pixel2 & 0xFF;
        
        return Math.sqrt(
            Math.pow(r1 - r2, 2) + Math.pow(g1 - g2, 2) + Math.pow(b1 - b2, 2)
        );
    }
}