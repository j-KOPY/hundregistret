import java.io.InputStream;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class InputReader {
//    private static InputStream sharedInputStream = System.in;
//    private static InputStream lastInputStream;
//    private static int instanceCount = 0;
//    private static boolean constructorCalled = false;
    private Scanner input;
    private static Set<InputStream> setOfInputStreams = new HashSet<InputStream>();

    public InputReader() throws IllegalStateException {
//        new InputReader(System.in);
        this(System.in);
//        this(sharedInputStream);
    }

    public InputReader(InputStream IS) throws IllegalStateException {
        if (setOfInputStreams.contains(IS)) {
            throw new IllegalStateException("An instance of InputReader already exists");
        }
//        if (instanceCount > 0) {
//            throw new IllegalStateException("An instance of InputReader already exists");
//        }
//        if (lastInputStream != null && lastInputStream == IS) {
//
//            throw new IllegalStateException("An instance of InputReader already exists for this InputStream.");
//        }
        this.input = new Scanner(IS);
        setOfInputStreams.add(IS);
//        lastInputStream = IS;
//        instanceCount++;
//        constructorCalled = true;
    }

    public int readInteger(String leadText) {
        int value = 0;
//        while (!input.hasNextInt()) {
//            try {
//                System.out.print(leadText + "?>");
//                value = input.nextInt();
//                input.nextLine();
//            }
//            catch (Exception e) {
//                System.out.println("Invalid input: Please enter an integer.");
//                input.nextInt();
//            }
//        }

        System.out.print(leadText + "?>");
        value = input.nextInt();
        input.nextLine();

//        resetStream();
//        resetConstructor();
//        input.close();
        return value;
    }

    public double readDecimal(String leadText) {
        double value = 0.0;
//        while (!input.hasNextDouble()) {
//            try {
//                System.out.print(leadText + "?>");
//                value = input.nextDouble();
//                input.nextLine();
//            }
//            catch (Exception e) {
//                System.out.println("Invalid input: Please enter a double.");
//                input.nextDouble();
//            }
//        }

        System.out.print(leadText + "?>");
        value = input.nextDouble();
        input.nextLine();

//        resetStream();
//        resetConstructor();
//        input.close();
        return value;
    }

    public String readString(String leadText) {
        String value;
//        boolean validInput = false;
//        while (!validInput) {
//            try {
//                System.out.print(leadText + "?>");
//                value = input.next();
//                // Enables reading from mixed types of inputs
//                value += input.nextLine();
////                value = input.nextLine();
//                validInput = true;
//            }
//            catch (Exception e) {
//                System.out.println("error : Input is empty.");
//                System.out.println("Please try again?>");
//                input.nextLine();
//            }
//        }

//        System.out.print(leadText + "?>");
//        String value = input.next();
//        // Enables reading from mixed types of inputs
//        value += input.nextLine();

        do {
            System.out.print(leadText + "?>");
//            value = input.next();
            // Enables reading from mixed types of inputs
//            value += input.nextLine();
            value = input.nextLine().trim();

            if (value.isEmpty() || value.isBlank()) {
                System.out.println("error : Input cannot be empty. Please try again.");
            }
        } while (value.isEmpty() || value.isBlank());

        return value;

//        resetStream();
//        resetConstructor();
//        input.close();

    }
//
//    private void resetStream() {
//        lastInputStream = null;
//    }
//
//    private void resetConstructor() {
//        constructorCalled = false;
//    }


}
