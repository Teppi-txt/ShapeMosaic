
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        // COMMAND LIST:
        // set SHAPE_LIMIT, MUTATION_FACTOR, GENERATION_COUNT
        // generate TARGET_NAME OUTPUT_NAME 
        // generate TARGET_NAME OUTPUT_NAME STARTING_IMAGE_PATH
        // loadtimeline PATH OUTPUT
        Generator generator = new Generator();

        System.out.println("Command line started:");

        while (true) {
            Scanner scanner = new Scanner(System.in);
            String command = scanner.nextLine();
            String[] arguments = command.split(" ");

            System.out.print("> ");

            switch (arguments[0]) {
                case "set" -> generator.set_const(arguments);

                case "generate" -> {
                    // what is the name of the input image, placed in images/inputs
                    // for example, "input.jpg" for a photo at "images/inputs/input.jpg"
                    System.out.println("Input image name (ie. input.jpg for images/inputs/input.jpg):");
                    String start = scanner.nextLine();

                    // name the output directory/file
                    System.out.println("Output directory name:");
                    String output = scanner.nextLine();

                    // starting image, press enter if none
                    new File("images/outputs/" + output).mkdirs(); // make the output directory
                    generator.generate("images/inputs/" + start, "images/outputs/" + output, null);
                }

                case "loadtimeline" -> {
                    System.out.println("What is the filepath of the timeline file? (ie.'images/outputs/cat/timeline.txt')");
                    System.out.println("The timeline render will be generated within the same directory.");
                    String timeline_path = scanner.nextLine();
                    
                    BufferedImage tlrender = TimelineLoader.generate_image(timeline_path);

                    String parent_directory = new File(timeline_path).getParent();

                    // if the timeline file is immediately in the project folder
                    if (parent_directory == null) {
                        parent_directory = "";
                    }
                    
                    Generator.save_image(tlrender, parent_directory + "/timeline.png");

                    System.out.println("Finished generating timeline render.");
                }

                default -> System.out.println("Unknown command entered.");
            }
        }
    }
}
