// Belinda Johansson bejo1092

import java.util.*;

public class DogTailNameComparator implements Comparator<Dog> {

    public int compare(Dog dogOne, Dog dogTwo) {
        DogTailComparator dogTailComparator = new DogTailComparator();
        int tailResult = dogTailComparator.compare(dogOne, dogTwo);

        if (tailResult == 0) {
            DogNameComparator dogNameComparator = new DogNameComparator();
            int nameResult = dogNameComparator.compare(dogOne, dogTwo);
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
