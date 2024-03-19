// Belinda Johansson bejo1092

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class OwnerCollection {
    private Owner[] ownerCollection = new Owner[0];

    public boolean addOwner(Owner owner) {
        boolean inList = containsOwner(owner.getName());
        if (!inList) {
            for (int i = 0; i < ownerCollection.length; i++) {
                if(ownerCollection[i] == null) {
                    ownerCollection[i] = owner;
                    return true;
                }
            }
            Owner[] tempOwnerCollection = new Owner[ownerCollection.length + 1];
            System.arraycopy(ownerCollection, 0, tempOwnerCollection, 0, ownerCollection.length);
            tempOwnerCollection[tempOwnerCollection.length - 1] = owner;
            ownerCollection = tempOwnerCollection;
            return true;
        }
        return false;
    }

    public boolean removeOwner(String name) {
        boolean inList = containsOwner(name);
        if (inList) {
            boolean dogFree = getOwner(name).getDogs().isEmpty();
            if (!dogFree) {
                return false;
            }
            else {
                for (int i = 0; i < ownerCollection.length; i++) {
                    if (ownerCollection[i] == null) continue;
                    if (ownerCollection[i].getName().equals(name)) {
                        ownerCollection[i] = null;
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean removeOwner(Owner owner) {
        boolean inList = containsOwner(owner.getName());
        if (inList) {
            boolean dogFree = owner.getDogs().isEmpty();
            if (!dogFree) {
                return false;
            }
            else {
                for (int i = 0; i < ownerCollection.length; i++) {
                    if (ownerCollection[i] == null) continue;
                    if (ownerCollection[i].equals(owner)) {
                        ownerCollection[i] = null;
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean containsOwner(String name) {
        return getOwner(name) != null;
    }

    public boolean containsOwner(Owner owner) {
        for (Owner listOwner : ownerCollection) {
            if (listOwner == null) continue;

            if(listOwner.equals(owner)) {
                return true;
            }
        }
        return false;
    }

    public Owner getOwner(String name) {
        for ( Owner owner : ownerCollection) {
            if (owner == null) continue;
            if ( owner.getName().equals(name)) {
                return owner;
            }
        }
        return null;
    }

    public ArrayList<Owner> getOwners() {
        if (ownerCollection.length > 1) {
            Arrays.sort(ownerCollection, Comparator.nullsLast(Comparator.naturalOrder()));
        }
        return new ArrayList<>(Arrays.asList(ownerCollection));
    }
}