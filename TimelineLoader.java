
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TimelineLoader {
    public TimelineLoader() {

    }

    public static void main(String[] args) {
        String test = "prop1: 0 | prop2: 15 | prop3: (0, 2)";
        System.out.println(extract_properties(test));
    }

    public static BufferedImage generate_image(File timeline_path) {
        
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
        System.out.println(properties);
        return properties;
    }

    public static Shape get_shape_from_properties(Map<String, String> properties) {
        String name = properties.get("name");
        Color color = get_color_from_string(properties.get("color"));

        if (name != "Polygon") {
            // ellipse or rect (same structure)
            int width = Integer.parseInt(properties.get("width"));
            int height = Integer.parseInt(properties.get("height"));
            int angle = Integer.parseInt(properties.get("angle"));
            Vector2 top_left = get_vector2_from_string(properties.get("top_left"));

        } else {
            // polygon
        }
        return null;
    }

    public static Vector2 get_vector2_from_string(String vector2_string) {
        return null;
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