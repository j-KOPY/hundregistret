// Belinda Johansson bejo1092

import java.util.*;

public class DogTailNameComparator implements Comparator<Dog> {

    public int compare(Dog dog1, Dog dog2) {
        DogTailComparator dogTailComparator = new DogTailComparator();
        int tailResult = dogTailComparator.compare(dog1, dog2);

        if (tailResult == 0) {
            DogNameComparator dogNameComparator = new DogNameComparator();
            int nameResult = dogNameComparator.compare(dog1, dog2);
            if (nameResult < 0) {
                return -1;
            }
            if (nameResult > 0) {
                return 1;
            }
        }

        return tailResult;
    }

}
