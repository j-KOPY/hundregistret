// Belinda Johansson bejo1092

import java.util.Comparator;

public class DogNameComparator implements Comparator<Dog> {
    public int compare(Dog nameOne, Dog nameTwo) {
        String dogOne = nameOne.getName();
        String dogTwo = nameTwo.getName();

        int nameResult = dogOne.compareTo(dogTwo);

        return nameResult;
    }

}