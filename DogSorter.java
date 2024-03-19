// Belinda Johansson bejo1092

import java.util.ArrayList;
import java.util.Comparator;

public class DogSorter {
    public static int sortDogs(Comparator<Dog> dogComparator, ArrayList<Dog> dogArrayList) {
        int counter = 0;
        for (int i = 0; i < dogArrayList.size() - 1; i++) {
            int currentMinIndex = nextDog(dogComparator, dogArrayList, i);
            if (currentMinIndex != i) {
                swapDogs(dogArrayList, i, currentMinIndex);
                counter += 1;
            }
        }
        return counter;
    }

    private static int nextDog(Comparator<Dog> dogComparator, ArrayList<Dog> dogArrayList, int i) {
        int currentMinIndex = i;

        for (int j = i + 1; j < dogArrayList.size(); j++) {
            if (dogComparator.compare(dogArrayList.get(currentMinIndex), dogArrayList.get(j)) > 0) {
                currentMinIndex = j;
            }
            else {
            }
        }
        return currentMinIndex;
    }

    private static void swapDogs(ArrayList<Dog> dogArrayList, int i, int j) {
        Dog a = dogArrayList.get(i);
        Dog b = dogArrayList.get(j);
        dogArrayList.set(i,b);
        dogArrayList.set(j,a);
    }
}