
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TimelineLoader {
    public TimelineLoader() {

    }

    // public static void main(String[] args) {
    //     // String test = "prop1: 0 | prop2: 15 | prop3: (0, 2)";
    //     // System.out.println(extract_properties(test));
    //     BufferedImage image = generate_image("images/outputs/cat2/timeline.txt");
    //     Generator.save_image(image, "images/outputs/cat2/timeline.png");
    // }

    public static BufferedImage generate_image(String timeline_path) {
        try (var scanner = new Scanner(new File(timeline_path))) {
            String header = scanner.nextLine();
            Map<String, String> header_values = extract_properties(header);

            BufferedImage image = new BufferedImage(Integer.parseInt(header_values.get("width")), 
                                                    Integer.parseInt(header_values.get("width")), 
                                                    5);
            Graphics2D image_graphics = image.createGraphics();

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                // You can further process the line here, e.g., using scanner.next()
                Shape current_shape = get_shape_from_properties(extract_properties(line));

                if (current_shape != null) {
                    current_shape.draw(image_graphics);
                } 
            }

            return image;
        } catch (FileNotFoundException e) {
            System.err.println("File not found: " + e.getMessage());
        }
        
        return null;
        
    }

    public static Map<String, String> extract_properties(String input) {
        String[] split = input.split("[|]");
        Map<String, String> properties = new HashMap<>();


        for (String s : split) {
            s = s.trim();
            
            // split again
            String[] split_property = s.split("[:]");
            properties.put(split_property[0].trim(), split_property[1].trim());
        }
        return properties;
    }

    public static Shape get_shape_from_properties(Map<String, String> properties) {
        String shape = properties.get("shape");
        Color color = get_color_from_string(properties.get("color"));

        if (!"Polygon".equals(shape)) {
            // ellipse or rect (same structure)
            int width = Integer.parseInt(properties.get("width"));
            int height = Integer.parseInt(properties.get("height"));
            int angle = Integer.parseInt(properties.get("angle"));
            Vector2 top_left = get_vector2_from_string(properties.get("top_left"));

            if ("Rect".equals(shape)) {
                return new Rect(width, height, top_left, angle, color);
            }
            
            if ("Ellipse".equals(shape)) {
                return new Ellipse(width, height, top_left, angle, color);
            }
        
        } else {
            // polygon looks like this: 
            // shape: Polygon | vertices: [(537, 948), (39, 139), (634, 1106), (441, 1177)] | color: java.awt.Color[r=185,g=152,b=127]
            // 1. access vertices property
            String vertices = properties.get("vertices");

            // 2. access each vertex
            // remove the square brackets
            vertices = vertices.substring(1, vertices.length() - 1);
            String[] vstring_array = vertices.split("\\),");

            Vector2[] vertices_array = new Vector2[vstring_array.length];
            
            // 3. use get_vector2_from_string
            for (int v_index = 0; v_index < vstring_array.length; v_index++) {
                vertices_array[v_index] = get_vector2_from_string(vstring_array[v_index].strip() + ")");
            }

            return new Polygon(vertices_array, 0, color);
        }
        return null;
    }

    public static Vector2 get_vector2_from_string(String vector2_string) {
        Pattern pattern = Pattern.compile("\\(([^,]+),\\s*([^\\)]+)\\)");
        Matcher matcher = pattern.matcher(vector2_string);

        if (matcher.find()) {
            String x = matcher.group(1).trim();
            String y = matcher.group(2).trim();
            return new Vector2(Integer.parseInt(x), Integer.parseInt(y));
        } else {
            throw new IllegalArgumentException("Invalid format: " + vector2_string);
        }
    }

    public static Color get_color_from_string(String color_string) {
        Pattern pattern = Pattern.compile("r=(\\d+),g=(\\d+),b=(\\d+)");
        Matcher matcher = pattern.matcher(color_string);

        if (matcher.find()) {
            int r = Integer.parseInt(matcher.group(1));
            int g = Integer.parseInt(matcher.group(2));
            int b = Integer.parseInt(matcher.group(3));
            return new Color(r, g, b);
        } else {
            throw new IllegalArgumentException("Invalid color format: " + color_string);
        }
    }
}