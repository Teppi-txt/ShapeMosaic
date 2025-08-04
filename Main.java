
import java.util.Scanner;


public class Main {

    public static void main(String[] args) {

        // COMMAND LIST:
        // set SHAPE_LIMIT, MUTATION_FACTOR, GENERATION_COUNT
        // generate TARGET_NAME OUTPUT_NAME 
        // generate TARGET_NAME OUTPUT_NAME STARTING_IMAGE_PATH
        // loadtimeline PATH OUTPUT

        Generator generator = new Generator();
        
        while (true) {
            Scanner scanner = new Scanner(System.in);
            String command = scanner.nextLine();
            String[] arguments = command.split(" ");

            switch (arguments[0]) {
                case "set":
                    generator.set_const(arguments);
                    break;
                case "generate":
                    // what is the name of the input image, placed in images/inputs
                    // for example, "input.jpg" for a photo at "images/inputs/input.jpg"

                    // name the output directory/file
                    
                    // starting image, press enter if none

                    generator.generate("images/inputs/input.jpg", "newimage.png");

                case "loadtimeline":
                    System.out.println("bleh");

                default:
                    System.out.println("Unknown command entered.");
            }
        }
    }
}