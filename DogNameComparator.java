// Belinda Johansson bejo1092

import java.util.Comparator;

public class DogNameComparator implements Comparator<Dog> {
    public int compare(Dog name1, Dog name2) {
        String dog1 = name1.getName();
        String dog2 = name2.getName();

        int nameResult = dog1.compareTo(dog2);

        return nameResult;
    }

}