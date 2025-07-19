import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
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

    public static Color getAverageColorOfShape(Shape shape, BufferedImage target) {
        //sets up a blank image with the same dimensions as the target

        // TODO: Optimisations here with bounding box
        BufferedImage tempImage = new BufferedImage(target.getWidth(), target.getHeight(), BufferedImage.TYPE_INT_ARGB);
        int redSum = 0; int blueSum = 0; int greenSum = 0; int count = 0; //counts the sums of colors and n
        
        shape.set_color(Color.BLACK); //sets a temp color for the shape
        shape.draw(tempImage.createGraphics()); //draws the shape onto the tempImage

        for (int x = 0; x < target.getWidth(); x++) {
            for (int y = 0; y < target.getHeight(); y++) {
                //checks if the pixel color of the temp image isnt equal to 0, the default value
                //means the shape that was drawn includes that pixel
                if (tempImage.getRGB(x, y) != 0) {
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


    public static BufferedImage deepCopy(BufferedImage bi) {
        ColorModel cm = bi.getColorModel();
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        WritableRaster raster = bi.copyData(bi.getRaster().createCompatibleWritableRaster());
        return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
    }
}