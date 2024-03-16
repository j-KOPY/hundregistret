// Belinda Johansson bejo1092

import java.util.ArrayList;

public class Owner implements Comparable<Owner> {
    private String name;
    private ArrayList<Dog> dogsOwned;

    public Owner(String name) {
        this.name = name.toUpperCase();
        this.dogsOwned = new ArrayList<Dog>();
    }
    public String toString() {
        return this.name + " " + this.dogsOwned.toString();
    }
    public String getName() {
        return this.name;
    }

    public boolean addDog(Dog dog) {
        if (this != dog.getOwner()) return false;
        else if (dogsOwned.contains(dog)) return false;
        else {
//            DogSorter ds = new DogSorter();
            DogNameComparator dnc = new DogNameComparator();
            dogsOwned.add(dog);
            DogSorter.sortDogs(dnc, dogsOwned);
            return true;
        }
    }

//    public boolean addDog(Dog dog) {
//        if (dog.getOwner() != this) {
//            return false;
//        }
//        else {
//            if (dogsOwned.contains(dog)) {
//                return false;
//            }
//            else {
//                dogsOwned.add(dog);
//                if (dog.getOwner() == this) {
//                    return true;
//                }
//                else {
//                    dog.setOwner(this);
//                    return true;
//                }
//            }
//        }
//    }

    public boolean removeDog(Dog dog) {
        return dogsOwned.remove(dog);
    }

    public ArrayList<Dog> getDogs() {
        ArrayList<Dog> immutableDogsOwned = new ArrayList<>();
        for (Dog dog : dogsOwned) {
            immutableDogsOwned.add(dog);
        }
        return immutableDogsOwned;
    }

    public int compareTo(Owner ettNamn) {
        int nameResult = this.name.compareTo(ettNamn.name);

        return nameResult;

    }

}