// Belinda Johansson bejo1092

public class Dog {
    private static final String TAX = "TAX";
    private static final String DACHSHUND = "DACHSHUND";
    private static final double DEFAULT_TAIL_LENGTH = 3.7;
    
    private String name;
    private String breed;
    private int age;
    private int weight;
    private double tailLength;
    private Owner owner;

    public Dog(String name, String breed, int age, int weight) {
        this.name = name.toUpperCase();
        this.breed = breed.toUpperCase();
        this.age = age;
        this.weight = weight;
        this.tailLength = this.calcTail();
        // Should constructor set owner?
    }
    public String toString() {
        if (this.owner != null) {
            return this.name + " " + this.breed + " " + this.age + " " + this.weight + " " +  this.tailLength + " " + this.owner.getName();
        }
        else {
            return this.name + " " + this.breed + " " + this.age + " " + this.weight + " " +  this.tailLength;
        }
    }
    public String getName() {
        return this.name;
    }
    public String getBreed() {
        return this.breed;
    }
    public int getAge() {
        return this.age;
    }
    public int getWeight() {
        return this.weight;
    }
    public double getTailLength() {
        return this.tailLength;
    }
    public Owner getOwner() {
        return this.owner;
    }

    public boolean setOwner(Owner newOwner) {
        // Do dog have an owner?
        // No
        if (this.owner == null && newOwner != null) {
            this.owner = newOwner;
            newOwner.addDog(this);
            return true;
        }
        // Yes
        else {
            if (this.owner != null && newOwner == null) {
                this.owner.removeDog(this);
                this.owner = newOwner;
                return true;
            }
            return false;
        }
    }

//    public boolean setOwner(Owner owner) {
//        if (owner == null) {
//            this.owner.removeDog(this);
//            this.owner = owner;
//            return true;
//        } else if (this.owner == owner || this.owner != null) {
//            return false;
//        }
//        else {
//            this.owner = owner;
//            if (owner.getDogs().contains(this)) {
//                return true;
//            }
//            else {
//                owner.addDog(this);
//                return true;
//            }
//        }
//    }
    
    private double calcTail() {
        double tailLength;
        if (this.breed.equals(TAX) || this.breed.equals(DACHSHUND)) {
            tailLength = DEFAULT_TAIL_LENGTH;
        }
        else {
            tailLength = this.age * (double) this.weight/10;
        }
        return tailLength;
    }

    public void increaseAge(int year) {
        if (year > 0) {
            int maxAge = Integer.MAX_VALUE - this.age;
            if (year <= maxAge) {
                this.age += year;
                this.tailLength = calcTail();
            } else {
                System.err.println("Ålder ökning orsakade overflow: " + this.age);
                this.age = Integer.MAX_VALUE;
            }
        }
    }


}