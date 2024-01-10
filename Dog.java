// Belinda Johansson bejo1092

public class Dog {
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
    }
    public String toString() {
        return this.name + " " + this.breed + " " + this.age + " " + this.weight + " " +  this.tailLength + " " + this.owner.getName();
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

    public boolean setOwner(Owner owner) {
        if (owner == null) {
            this.owner.removeDog(this);
            this.owner = owner;
            return true;
        } else if (this.owner == owner || this.owner != null) {
            return false;
        }
        else {
            this.owner = owner;
            if (owner.getDogs().contains(this)) {
                return true;
            }
            else {
                owner.addDog(this);
                return true;
            }
        }
    }

    private double calcTail() {
        double tailLength;
        if (this.breed.equals("TAX") || this.breed.equals("DACHSHUND")) {
            tailLength = 3.7;
        }
        else {
            tailLength = this.age * (double) this.weight/10;
        }
        return tailLength;
    }

    public void increaseAge(int year) {
        if (year < Integer.MAX_VALUE && year > 0) {
            try {
                this.age = Math.addExact(this.age, year);
                this.tailLength = calcTail();
            }
            catch (ArithmeticException e) {
            }
        }

    }


}
