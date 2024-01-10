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
                counter++;
            }

        }
        return counter;
    }

    private static int nextDog(Comparator<Dog> dogComparator, ArrayList<Dog> dogArrayList, int i) {
        // int currentMin = dogArrayList.get(i);
        int currentMinIndex = i;
        // System.out.println("Dog på plats i: " + dogArrayList.get(currentMinIndex));
        for (int j = i + 1; j < dogArrayList.size(); j++) {
            // System.out.println("Dog på plats j: " + dogArrayList.get(j));

            if (dogComparator.compare(dogArrayList.get(currentMinIndex), dogArrayList.get(j)) > 0) {
                // Dog currentMin = dogArrayList.get(j);
                currentMinIndex = j;
                // System.out.println(currentMinIndex);

            }
        }

        return currentMinIndex;
    }

    private static void swapDogs(ArrayList<Dog> dogArrayList, int i, int ij) {
        try {
            Dog a = dogArrayList.get(i);
            Dog b = dogArrayList.get(ij);
            dogArrayList.set(i, b);
            dogArrayList.set(ij, a);
        }
            catch (Exception e) {
//            e.printStackTrace();
        }
    }
}
