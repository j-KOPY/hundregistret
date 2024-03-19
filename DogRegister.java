import java.util.ArrayList;
import java.util.Scanner;

public class DogRegister {

    private final String SHUTDOWN = "EXIT";
    private final String REGISTER_NEW_OWNER = "REGISTER NEW OWNER";
    private final String REMOVE_OWNER = "REMOVE OWNER";
    private final String LIST_OWNERS = "LIST OWNERS";
    private final String REGISTER_NEW_DOG = "REGISTER NEW DOG";
    private final String REMOVE_DOG = "REMOVE DOG";
    private final String LIST_DOGS = "LIST DOGS";
    private final String INCREASE_AGE = "INCREASE AGE";
    private final String GIVE_DOG_TO_OWNER = "GIVE DOG TO OWNER";
    private final String REMOVE_DOG_FROM_OWNER = "REMOVE DOG FROM OWNER";


    private InputReader input = new InputReader();
    private OwnerCollection ownerCollection;
    private DogCollection dogCollection;
    private void start() {
        setUp();
        runCommandLoop();
        shutDown();
    }

    private void setUp() {
        ownerCollection = new OwnerCollection();
        dogCollection = new DogCollection();

//        ownerCollection.addOwner(new Owner("JAKOB"));
//        ownerCollection.addOwner(new Owner("BELINDA"));

//        dogCollection.addDog(new Dog("Allie", "Border", 3, 15));
//        dogCollection.addDog(new Dog("Joppe", "Hamiltonstövare", 12, 13));
//        dogCollection.addDog(new Dog("Roger", "Tax", 10, 5));
//        dogCollection.addDog(new Dog("Ruby", "Mix", 4, 10));
//        dogCollection.addDog(new Dog("Stoja", "Welsh", 13, 16));
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

            case REGISTER_NEW_OWNER:
                String newOwner = input.readString("Enter owner name").toUpperCase();
                if (ownerCollection.addOwner(new Owner(newOwner))) {
                    System.out.println(newOwner + " added as a new owner.");
                }
                else {
                    System.out.println("error : " + newOwner + " already exists as an owner.");
                }
                break;

            case REMOVE_OWNER:
                if (ownerCollection.getOwners().isEmpty()) {
                    System.out.println("error : Collection of owners is empty.");
                    break;
                }
                String removeOwner = input.readString("Enter owner name").toUpperCase();
                if (ownerCollection.getOwner(removeOwner) == null) {
                    System.out.println("error : Owner with name " + removeOwner + " does not exist in collection.");
                    break;
                }
                if (!ownerCollection.getOwner(removeOwner).getDogs().isEmpty()) {
                    for (Dog dog : ownerCollection.getOwner(removeOwner).getDogs()) {
                        dog.setOwner(null);
                        dogCollection.removeDog(dog);
                    }
                    ownerCollection.removeOwner(removeOwner);
                    break;
                }
                if (ownerCollection.removeOwner(removeOwner)) {
                    System.out.println(removeOwner + " has been removed.");
                }
                else {
                    System.out.println("error : Something went wrong.");
                }

                break;

            case LIST_OWNERS:
                if (ownerCollection.getOwners().isEmpty()) {
                    System.out.println("error : Collection of owners is empty");
                    break;
                }
                System.out.println(ownerCollection.getOwners().toString());
                break;

            case REGISTER_NEW_DOG:
                String newDogName = input.readString("Enter dog name").toUpperCase();
                if (dogCollection.containsDog(newDogName)) {
                    System.out.println("error : Dog already exists in collection");
                    break;
                }
                String newDogBreed = input.readString("Enter dog breed");
                int newDogAge = input.readInteger("Enter dog age");
                int newDogWeight = input.readInteger("Enter dog weight");

                if (dogCollection.addDog(new Dog(newDogName, newDogBreed, newDogAge, newDogWeight))) {
                    System.out.println( newDogName + " added as a new dog");
                }
                else {
                    System.out.println("error : Something went wrong.");
                }

                break;

            case REMOVE_DOG:
                if (dogCollection.getDogs().isEmpty()) {
                    System.out.println("error : Collection of dogs is empty.");
                    break;
                }
                String removeDog = input.readString("Enter dog name").toUpperCase();
                if (dogCollection.getDog(removeDog) == null) {
                    System.out.println("error : " + removeDog + "does not exist in dog collection");
                    break;
                }
                if (dogCollection.getDog(removeDog).getOwner() != null) {
                    dogCollection.getDog(removeDog).setOwner(null);
                    dogCollection.removeDog(removeDog);
                    System.out.println(removeDog + " has been removed.");
                    break;
                }
                if (dogCollection.removeDog(removeDog)) {
                    System.out.println(removeDog + " has been removed.");
                }
                else System.out.println("error : Something went wrong");
                break;

            case LIST_DOGS:
                if (dogCollection.getDogs().isEmpty()) {
                    System.out.println("error : Collection of dogs is empty");
                    break;
                }
                double minTail = input.readDecimal("Enter minmum taillength");
                ArrayList<Dog> filterDogs = dogCollection.filterByTailLength(minTail);
                System.out.println(filterDogs.toString());
                break;

            case INCREASE_AGE:
                if (dogCollection.getDogs().isEmpty()) {
                    System.out.println("error : Collection of dogs is empty");
                    break;
                }
                String dogName = input.readString("Enter dog name").toUpperCase();
                if (dogCollection.getDog(dogName) == null) {
                    System.out.println("error : Dog with name " + dogName + "does not exist in collection");
                    break;
                }
                dogCollection.getDog(dogName).increaseAge(1);
                System.out.println(dogName + " is now one year older");
                break;

            case GIVE_DOG_TO_OWNER:
                if (dogCollection.getDogs().isEmpty()) {
                    System.out.println("error : Collection of dogs is empty");
                    break;
                }
                if (ownerCollection.getOwners().isEmpty()) {
                    System.out.println("error : Collection of owners is empty");
                    break;
                }
                String giveDog = input.readString("Enter dog name").toUpperCase();
                if (dogCollection.getDog(giveDog) == null) {
                    System.out.println("error : Dog with name " + giveDog + "does not exist in collection");
                    break;
                }
                if (dogCollection.getDog(giveDog).getOwner() != null) {
                    System.out.println("error : Dog with name " + giveDog + "already have an owner");
                    break;
                }
                String ownerName = input.readString("Enter owner name").toUpperCase();
                Owner ownerNewDog = ownerCollection.getOwner(ownerName);
                if (dogCollection.getDog(giveDog).setOwner(ownerNewDog)){
                    System.out.println(giveDog + " is now owned by " + ownerNewDog.getName());
                }
                else System.out.println("error : Something went wrong");
                break;

            case REMOVE_DOG_FROM_OWNER:
                if (dogCollection.getDogs().isEmpty()) {
                    System.out.println("error : Collection of dogs is empty");
                    break;
                }
                if (ownerCollection.getOwners().isEmpty()) {
                    System.out.println("error : Collection of owners is empty");
                    break;
                }
                String dogNameRemove = input.readString("Enter dog name").toUpperCase();
                if (dogCollection.getDog(dogNameRemove) == null) {
                    System.out.println("error : Dog with name " + dogNameRemove + "does not exist in collection");
                    break;
                }
                if (dogCollection.getDog(dogNameRemove).setOwner(null)) {
                    System.out.println(dogNameRemove + " now have no owner");
                }
                else System.out.println("error : Something went wrong");
                break;

            case SHUTDOWN:
                // SHUTDOWN is handled in command loop and in shutDown()
                break;
            default:
                System.out.println("error : Wrong command!");
        }
    }

    private String readCommand() {
        return input.readString("Enter command").toUpperCase();
    }

    private void shutDown() {

    }


    public static void main(String[] args) {

        new DogRegister().start();
    }
}
