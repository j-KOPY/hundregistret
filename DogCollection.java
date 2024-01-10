// Belinda Johansson bejo1092

import java.util.ArrayList;

public class DogCollection {
    private ArrayList<Dog> hundar = new ArrayList<>();

    public boolean addDog(Dog dog) {
        boolean inList = containsDog(dog.getName());
        if (!inList) {
            hundar.add(dog);
            return true;
        }
        else {
            return false;
        }
    }

    public boolean removeDog(String dogName) {
        boolean inList = containsDog(dogName);
        if (inList) {
            hundar.remove(getDog(dogName));
            return true;
        }
        else {
            return false;
        }
    }

    public boolean removeDog(Dog dog) {
        boolean inList = containsDog(dog.getName());
        if (inList && dog.getOwner() == null) {
            hundar.remove(dog);
            return true;
        }
        else {
            return false;
        }
    }

    public boolean containsDog(String dogName) {
        if (getDog(dogName) == null) {
            return false;
        }
        else {
            return true;
        }
    }

    public boolean containsDog(Dog dog) {
        return hundar.contains(dog);
    }

    public Dog getDog(String dogName) {
        for ( Dog dog : hundar) {
            if ( dog.getName().equals(dogName)) {
                return dog;
            }
        }
        return null;
    }

    public ArrayList<Dog> getDogs() {
        DogSorter.sortDogs(new DogNameComparator(), hundar);
        ArrayList<Dog> kopia = new ArrayList<>(hundar);

        return kopia;
    }

    public ArrayList filterByTailLength(double tailLength) {
        DogSorter.sortDogs(new DogTailNameComparator(), hundar);
        ArrayList<Dog> dogsWithTail = new ArrayList<>();
        for (Dog dog : hundar) {
            if (dog.getTailLength() >= tailLength) {
                dogsWithTail.add(dog);
            }
        }
        return dogsWithTail;
    }
}