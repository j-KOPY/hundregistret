import java.util.Scanner;

public class DogRegister {

    private static final String SHUTDOWN = "EXIT";
    private InputReader input = new InputReader();
    private void start() {
//        setUp();
        runCommandLoop();
        shutDown();
    }

    private void runCommandLoop() {
        String command;

        do {
            command = readCommand();
            handleCommand(command);
        }
        while (!command.equals(SHUTDOWN));
    }

    private void handleCommand(String command) {
        switch (command) {
            case SHUTDOWN:
                // SHUTDOWN is handled in command loop and in shutDown()
                break;
            default:
                System.out.println("error : Wrong command!");
        }
    }

    private String readCommand() {
//        System.out.print("?>");
        return input.readString("?>").toUpperCase();
    }

    private void shutDown() {

    }

    public static void main(String[] args) {

        new DogRegister().start();
    }
}
