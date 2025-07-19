import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

public class ShapeManager {
    int maxX; int maxY; BufferedImage target;
    enum SHAPE {
        Rect,
        Polygon,
        Ellipse,
        Line
    }

    public ShapeManager (int maxX, int maxY, BufferedImage target) {
        this.maxX = maxX;
        this.maxY = maxY;
        this.target = target;
    }

    public Shape generateShape() {
        
        SHAPE shape = SHAPE.values()[new Random().nextInt(SHAPE.values().length)];
        Shape returnShape = null;

        // first 3 ids are shapes
        if (shape == SHAPE.Line) {
            int x = (int) (Math.random() * maxX);
            int y = (int) (Math.random() * maxY);
            Vector2 start = new Vector2(x, y);

            int x2 = (int) (Math.random() * maxX);
            int y2 = (int) (Math.random() * maxY);
            Vector2 end = new Vector2(x2, y2);

            returnShape = new Line(start, end);
            
        } else {
            int angle = (int) (Math.random() * 360);
            boolean filled = Math.random() * 2 > 0.3; //code for generating a skewed boolean

            //generates random dimensions for the shape, making sure it doesnt surpass the bounds of the image
            int width = (int) (Math.random() * maxX);
            int height = (int) (Math.random() * maxY);

            //this set of operations ensures the shape can be anywhere on the screen
            int x = (int) (Math.random() * maxX - (width / 2));
            int y = (int) (Math.random() * maxX - (height / 2));
            Vector2 position = new Vector2(x, y);
            
            // System.out.println(width + ", " + height + ", " + x + ", " + y + ", " + angle);

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
        }
        
        assert returnShape != null;
        returnShape.set_color(getAverageColorOfShape(returnShape, target));
        return returnShape;
    }

    public Color getAverageColorOfShape(Shape shape, BufferedImage target) {
        //sets up a blank image with the same dimensions as the target

        // TODO: Optimisations here with bounding box
        BufferedImage tempImage = new BufferedImage(this.maxX, this.maxY, 5);
        int redSum = 0; int blueSum = 0; int greenSum = 0; int count = 0; //counts the sums of colors and n

        shape.set_color(Color.WHITE); //sets a temp color for the shape
        shape.draw(tempImage.createGraphics()); //draws the shape onto the tempImage

        BoundingBox box = shape.get_bounding_box();

        // long startTime = System.nanoTime();

        for (int x = Math.max(0, box.top_left.x); x < Math.min(box.bot_right.x, this.maxX); x++) {
            for (int y = Math.max(0, box.top_left.y); y < Math.min(box.bot_right.y, this.maxY); y++) {
                //checks if the pixel color of the temp image isnt equal to 0, the default value
                //means the shape that was drawn includes that pixel
                if (tempImage.getRGB(x, y) != 0xFF000000) {
                    int pixelColor = target.getRGB(x, y);
                    count += 1;
                    redSum += (pixelColor & 0xff0000) >> 16;
                    greenSum += (pixelColor & 0xff00) >> 8;
                    blueSum += (pixelColor & 0xff);
                    tempImage.setRGB(x, y, 0xFFFFC0CB);
                }
            }
        }

        Main.save_image(tempImage, "images/pink.jpg");
        // System.out.println("Optimised: " + (System.nanoTime() - startTime));

        // startTime = System.nanoTime();

        // for (int x = 0; x < this.maxX; x++) {
        //     for (int y = 0; y < this.maxY; y++) {
        //         //checks if the pixel color of the temp image isnt equal to 0, the default value
        //         //means the shape that was drawn includes that pixel
        //         if (tempImage.getRGB(x, y) != 0) {
        //             int pixelColor = target.getRGB(x, y);
        //             count += 1;
        //             redSum += (pixelColor & 0xff0000) >> 16;
        //             greenSum += (pixelColor & 0xff00) >> 8;
        //             blueSum += (pixelColor & 0xff);
        //         }
        //     }
        // }

        // System.out.println("Unoptimised: " + (System.nanoTime() - startTime));

        if (count > 0) return new Color(redSum/count, greenSum/count, blueSum/count, 255);
        return Color.BLUE;
    }

    // add a bias later perhaps
    public Map<Shape, Double> generate_shape_list(int length, BufferedImage current) {
        Map<Shape, Double> returnList = new LinkedHashMap<>();

        for (int i = 0; i < length; i++) {
            Shape shape = generateShape();
            returnList.put(shape, squared_evaluation(target, current, shape));
        }
        return returnList;
    }


    public static BufferedImage deepCopy(BufferedImage source) {

        BufferedImage bi = new BufferedImage(source.getWidth(), source.getHeight(), source.getType());
        byte[] sourceData = ((DataBufferByte)source.getRaster().getDataBuffer()).getData();
        byte[] biData = ((DataBufferByte)bi.getRaster().getDataBuffer()).getData();
        System.arraycopy(sourceData, 0, biData, 0, sourceData.length);

        return bi;
    }

    public static double squared_evaluation(BufferedImage target, BufferedImage current, Shape shape) {
        
        if (target.getWidth() != current.getWidth() || target.getHeight() != current.getHeight()) {
            System.out.println("Unable to compare images with different dimensions.");
            return Double.MAX_VALUE;
        }

        double improvement = 0;
        BufferedImage after = deepCopy(current);
        shape.draw(after.createGraphics());
        // loop over the bounding box of the shape in the image

        BoundingBox box = shape.get_bounding_box();

        // long startTime = System.nanoTime();
        int pixels_checked = 0;
    

        for (int x = Math.max(0, box.top_left.x); x < Math.min(box.bot_right.x, target.getWidth()); x++) {
            for (int y = Math.max(0, box.top_left.y); y < Math.min(box.bot_right.y, target.getHeight()); y++) {
                //checks if the pixel color of the temp image isnt equal to 0, the default value
                //means the shape that was drawn includes that pixel

                //positive is closer, negative is further

                //TODO: optimise this to use getRGB array
                int beforePixel = current.getRGB(x, y);
                int afterPixel = after.getRGB(x, y);
                int targetPixel = target.getRGB(x, y);
                
                if (beforePixel != afterPixel) {
                    pixels_checked += 1;

                    // get the difference (improvement)
                   improvement += eucl_distance(beforePixel, targetPixel) - eucl_distance(afterPixel, targetPixel);
                }
            }
        }
        return improvement;
    }

    public static double manh_difference(int pixel1, int pixel2) {
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

        // System.out.printf("rgb1: (%d,%d,%d), rgb2: (%d,%d,%d)%n", r1, g1, b1, r2, g2, b2);

        return Math.sqrt(
            Math.pow(r1 - r2, 2) + Math.pow(g1 - g2, 2) + Math.pow(b1 - b2, 2)
        );
    }
}