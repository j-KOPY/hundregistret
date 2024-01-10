import java.util.*;

public class Main {
    public static void main(String[] args) {
        Owner owner = new Owner("Ägare");
        Dog dog = new Dog("Joppe", "Hamiltonstövare", 13, 20);
        Dog dog1 = new Dog("Allan", "Border Collie", 4, 12);
        Dog dog2 = new Dog("Roger", "Tax", 9, 8);
        Dog dog3 = new Dog("Ruby", "Terrier", 4, 8);
        Dog dog4 = new Dog("Stoja", "Welsh", 14, 16);

        ArrayList<Dog> dogs = new ArrayList<>();
        dogs.add(dog);
        dogs.add(dog1);
        dogs.add(dog2);
        dogs.add(dog3);
        dogs.add(dog4);

        System.out.println(dogs);

//        DogSorter ds = new DogSorter();
        DogNameComparator dnc = new DogNameComparator();
        DogTailComparator dtc = new DogTailComparator();
        DogTailNameComparator dtnc = new DogTailNameComparator();

        int startIndex = 0;
        DogSorter.sortDogs(dnc, dogs);

        System.out.println(dogs);

    }
}
