// Belinda Johansson bejo1092

import java.util.*;

public class DogTailComparator implements Comparator<Dog> {
    public int compare(Dog tailOne, Dog tailTwo) {
        if (tailOne.getTailLength() < tailTwo.getTailLength())
            return -1;
        if (tailOne.getTailLength() > tailTwo.getTailLength())
            return 1;
        return 0;
    }

}
