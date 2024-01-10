// Belinda Johansson bejo1092

import java.util.*;

public class DogTailComparator implements Comparator<Dog> {
    public int compare(Dog svans1, Dog svans2) {
        if (svans1.getTailLength() < svans2.getTailLength())
            return -1;
        if (svans1.getTailLength() > svans2.getTailLength())
            return 1;
        return 0;
    }

}
