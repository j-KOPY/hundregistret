//Belinda Johansson bejo1092

import java.util.ArrayList;
import java.util.Collections;

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
        if (dog.getOwner() != this) {
            return false;
        }
        else {
            if (dogsOwned.contains(dog)) {
                return false;
            }
            else {
                dogsOwned.add(dog);
                if (dog.getOwner() == this) {
                    return true;
                }
                else {
                    dog.setOwner(this);
                    return true;
                }
            }
        }
    }

    public boolean removeDog(Dog dog) {
        return dogsOwned.remove(dog);
    }

    public ArrayList<Dog> getDogs() {
        ArrayList<Dog> immutableDogsOwned = new ArrayList<>();
        for (Dog dog : dogsOwned) {
            // Assuming Dog class has a copy constructor or is immutable
            immutableDogsOwned.add(dog);
        }
        return immutableDogsOwned;
//        return Collections.unmodifiableCollection(dogsOwned);
    }

    public int compareTo(Owner ettNamn) {
        int nameResult = this.name.compareTo(ettNamn.name);

        return nameResult;

    }

}