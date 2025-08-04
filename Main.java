
import java.util.Scanner;


public class Main {

    public static void main(String[] args) {
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
                    generator.generate("images/inputs/input.jpg", "newimage.png");
                default:
                    System.out.println("Unknown command entered.");
            }
        }
    }
}