import static org.junit.jupiter.api.Assertions.*;

import java.util.*;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.opentest4j.MultipleFailuresError;

/**
 * Testfall för sorteringsmetoden i uppgift HR2.8.
 * <p>
 * Beskrivningen av testfallens uppgift, styrka och svagheter från
 * <code>{@link HR1_1_OwnerTest}</code> gäller (naturligvis) också för
 * testfallen i denna klass. Var speciellt uppmärksam på att testfallen kan
 * uppdateras när som helst, inklusive <em>efter</em> deadline.
 *
 * @author Henrik Bergström
 * @version 2024-01-04 13:16
 * @see HR1_1_OwnerTest
 */
@TestMethodOrder(OrderAnnotation.class)
@DisplayName("HR2.8: Testfall för den fullständiga implementationen av urvalssortering")
public class HR2_8_SortDogsTest {

    public static final String VERSION = "2024-01-04 13:16";

    private static final Dog DOG_A_1 = new Dog("A", "Mops", 1, 1);
    private static final Dog DOG_B_2 = new Dog("B", "Mops", 1, 2);
    private static final Dog DOG_C_3 = new Dog("C", "Mops", 1, 3);
    private static final Dog DOG_D_3 = new Dog("D", "Mops", 1, 3);
    private static final Dog DOG_E_4 = new Dog("E", "Mops", 1, 4);
    private static final Dog DOG_F_4 = new Dog("F", "Mops", 1, 4);

    private static final Comparator<Dog> TAIL_COMPARATOR = new DogTailComparator();
    private static final Comparator<Dog> NAME_COMPARATOR = new DogNameComparator();
    private static final Comparator<Dog> TAIL_NAME_COMPARATOR = new DogTailNameComparator();

    private void assertDogListSorted(Comparator<Dog> cmp, ArrayList<Dog> dogs, ArrayList<Dog> expectedDogs,
                                     int expectedSwaps) {
        var originalDogs = new ArrayList<>(dogs);
        var actualSwaps = DogSorter.sortDogs(cmp, dogs);
        assertDogListSorted(cmp, dogs, expectedDogs, expectedSwaps, originalDogs, actualSwaps);
    }

    private void assertDogListSorted(Comparator<Dog> cmp, ArrayList<Dog> dogs, ArrayList<Dog> expectedDogs,
                                     int expectedSwaps, ArrayList<Dog> originalDogs, int actualSwaps) throws MultipleFailuresError {
        assertAll("Fel resultat när listan %s sorteras".formatted(originalDogs), () -> {
            assertEquals(expectedDogs, dogs,
                    "Listan är inte korrekt sorterad med %s".formatted(cmp.getClass().getName()));
        }, () -> {
            assertEquals(expectedSwaps, actualSwaps, "Fel antal jämförelser");
        });
    }

    private ArrayList<Dog> dogs(Dog... dogs) {
        return new ArrayList<>(Arrays.asList(dogs));
    }

    @Test
    @Order(10)
    @DisplayName("Implementerad enligt instruktionerna")
    public void validateImplementation() {
        new DogSorterImplementationValidator().execute();
    }

    @Test
    @Order(20)
    @DisplayName("Sortering av en redan sorterad lista med svanslängd som sorteringsordning")
    public void sortedList() {
        assertDogListSorted(TAIL_COMPARATOR, dogs(DOG_A_1, DOG_B_2, DOG_C_3, DOG_E_4),
                dogs(DOG_A_1, DOG_B_2, DOG_C_3, DOG_E_4), 0);
    }

    @Test
    @Order(30)
    @DisplayName("Sortering av en omvänt sorterad lista med svanslängd som sorteringsordning")
    public void reversedSortedList() {
        assertDogListSorted(TAIL_COMPARATOR, dogs(DOG_E_4, DOG_C_3, DOG_B_2, DOG_A_1),
                dogs(DOG_A_1, DOG_B_2, DOG_C_3, DOG_E_4), 2);
    }

    @Test
    @Order(40)
    @DisplayName("Sortering av en blandad lista med svanslängd som sorteringsordning")
    public void randomList() {
        assertDogListSorted(TAIL_COMPARATOR, dogs(DOG_C_3, DOG_A_1, DOG_E_4, DOG_B_2),
                dogs(DOG_A_1, DOG_B_2, DOG_C_3, DOG_E_4), 3);
    }

    @Test
    @Order(50)
    @DisplayName("Sortering av en tom lista")
    public void emptyList() {
        assertDogListSorted(TAIL_COMPARATOR, dogs(), dogs(), 0);
    }

    @Test
    @Order(60)
    @DisplayName("Sortering av en lista med en enda hund")
    public void singleElementList() {
        assertDogListSorted(TAIL_COMPARATOR, dogs(DOG_A_1), dogs(DOG_A_1), 0);
    }

    @Test
    @Order(70)
    @DisplayName("Sortering av en blandad lista med namn som sorteringsordning")
    public void sortByName() {
        assertDogListSorted(NAME_COMPARATOR, dogs(DOG_B_2, DOG_F_4, DOG_E_4, DOG_C_3, DOG_A_1, DOG_D_3),
                dogs(DOG_A_1, DOG_B_2, DOG_C_3, DOG_D_3, DOG_E_4, DOG_F_4), 5);
    }

    @Test
    @Order(80)
    @DisplayName("Sortering av en blandad lista med svanslängd och namn som sorteringsordning")
    public void sortByTailAndName() {
        assertDogListSorted(TAIL_NAME_COMPARATOR, dogs(DOG_D_3, DOG_A_1, DOG_F_4, DOG_C_3, DOG_E_4, DOG_B_2),
                dogs(DOG_A_1, DOG_B_2, DOG_C_3, DOG_D_3, DOG_E_4, DOG_F_4), 4);
    }

    @Test
    @Order(90)
    @DisplayName("3 slumpmässigt genererade hundar som jämförs med DogTailComparator")
    public void sort3DogsUsingDogTailComparator() {
        var cmp = new DogTailComparator();

        // Hundar i testet i förväntad ordning
        var expectedDogs = new ArrayList<Dog>();
        var charlie = new Dog("Charlie", "Dobermann", 4, 1); // tail=0,40
        expectedDogs.add(charlie);
        var snobben = new Dog("Snobben", "Border collie", 10, 9); // tail=9,00
        expectedDogs.add(snobben);
        var ratata = new Dog("Ratata", "Vinthund", 8, 16); // tail=12,80
        expectedDogs.add(ratata);

        // Osorterad lista
        var dogs = new ArrayList<Dog>();
        dogs.add(snobben);
        dogs.add(charlie);
        dogs.add(ratata);

        var originalDogs = new ArrayList<>(dogs);

        var actualSwaps = DogSorter.sortDogs(cmp, dogs);

        assertDogListSorted(cmp, dogs, expectedDogs, 1, originalDogs, actualSwaps);
    }

    @Test
    @Order(100)
    @DisplayName("3 slumpmässigt genererade hundar som jämförs med DogNameComparator")
    public void sort3DogsUsingDogNameComparator() {
        var cmp = new DogNameComparator();

        // Hundar i testet i förväntad ordning
        var expectedDogs = new ArrayList<Dog>();
        var bamse = new Dog("Bamse", "Tax", 11, 5); // tail=3,70
        expectedDogs.add(bamse);
        var lassie = new Dog("Lassie", "Pudel", 2, 10); // tail=2,00
        expectedDogs.add(lassie);
        var molly = new Dog("Molly", "Cocker spaniel", 3, 1); // tail=0,30
        expectedDogs.add(molly);

        // Osorterad lista
        var dogs = new ArrayList<Dog>();
        dogs.add(molly);
        dogs.add(bamse);
        dogs.add(lassie);

        var originalDogs = new ArrayList<>(dogs);

        var actualSwaps = DogSorter.sortDogs(cmp, dogs);

        assertDogListSorted(cmp, dogs, expectedDogs, 2, originalDogs, actualSwaps);
    }

    @Test
    @Order(110)
    @DisplayName("3 slumpmässigt genererade hundar som jämförs med DogTailNameComparator")
    public void sort3DogsUsingDogTailNameComparator() {
        var cmp = new DogTailNameComparator();

        // Hundar i testet i förväntad ordning
        var expectedDogs = new ArrayList<Dog>();
        var doris = new Dog("Doris", "Border collie", 4, 5); // tail=2,00
        expectedDogs.add(doris);
        var molly = new Dog("Molly", "Tax", 6, 15); // tail=3,70
        expectedDogs.add(molly);
        var wilma = new Dog("Wilma", "Bulldogg", 3, 20); // tail=6,00
        expectedDogs.add(wilma);

        // Osorterad lista
        var dogs = new ArrayList<Dog>();
        dogs.add(doris);
        dogs.add(wilma);
        dogs.add(molly);

        var originalDogs = new ArrayList<>(dogs);

        var actualSwaps = DogSorter.sortDogs(cmp, dogs);

        assertDogListSorted(cmp, dogs, expectedDogs, 1, originalDogs, actualSwaps);
    }

    @Test
    @Order(120)
    @DisplayName("5 slumpmässigt genererade hundar som jämförs med DogTailComparator")
    public void sort5DogsUsingDogTailComparator() {
        var cmp = new DogTailComparator();

        // Hundar i testet i förväntad ordning
        var expectedDogs = new ArrayList<Dog>();
        var milou = new Dog("Milou", "Yorkshireterrier", 6, 8); // tail=4,80
        expectedDogs.add(milou);
        var ronja = new Dog("Ronja", "Border collie", 14, 13); // tail=18,20
        expectedDogs.add(ronja);
        var bamse = new Dog("Bamse", "Border collie", 16, 12); // tail=19,20
        expectedDogs.add(bamse);
        var molly = new Dog("Molly", "Mops", 12, 17); // tail=20,40
        expectedDogs.add(molly);
        var snobben = new Dog("Snobben", "Puli", 13, 19); // tail=24,70
        expectedDogs.add(snobben);

        // Osorterad lista
        var dogs = new ArrayList<Dog>();
        dogs.add(ronja);
        dogs.add(snobben);
        dogs.add(milou);
        dogs.add(bamse);
        dogs.add(molly);

        var originalDogs = new ArrayList<>(dogs);

        var actualSwaps = DogSorter.sortDogs(cmp, dogs);

        assertDogListSorted(cmp, dogs, expectedDogs, 4, originalDogs, actualSwaps);
    }

    @Test
    @Order(130)
    @DisplayName("5 slumpmässigt genererade hundar som jämförs med DogNameComparator")
    public void sort5DogsUsingDogNameComparator() {
        var cmp = new DogNameComparator();

        // Hundar i testet i förväntad ordning
        var expectedDogs = new ArrayList<Dog>();
        var doris = new Dog("Doris", "Dachshund", 3, 16); // tail=3,70
        expectedDogs.add(doris);
        var milou = new Dog("Milou", "Tax", 10, 10); // tail=3,70
        expectedDogs.add(milou);
        var ronja = new Dog("Ronja", "Rottweiler", 14, 19); // tail=26,60
        expectedDogs.add(ronja);
        var sigge = new Dog("Sigge", "Beagle", 4, 5); // tail=2,00
        expectedDogs.add(sigge);
        var wilma = new Dog("Wilma", "Golden retriever", 14, 14); // tail=19,60
        expectedDogs.add(wilma);

        // Osorterad lista
        var dogs = new ArrayList<Dog>();
        dogs.add(doris);
        dogs.add(milou);
        dogs.add(wilma);
        dogs.add(sigge);
        dogs.add(ronja);

        var originalDogs = new ArrayList<>(dogs);

        var actualSwaps = DogSorter.sortDogs(cmp, dogs);

        assertDogListSorted(cmp, dogs, expectedDogs, 1, originalDogs, actualSwaps);
    }

    @Test
    @Order(140)
    @DisplayName("5 slumpmässigt genererade hundar som jämförs med DogTailNameComparator")
    public void sort5DogsUsingDogTailNameComparator() {
        var cmp = new DogTailNameComparator();

        // Hundar i testet i förväntad ordning
        var expectedDogs = new ArrayList<Dog>();
        var charlie = new Dog("Charlie", "Labrador", 13, 1); // tail=1,30
        expectedDogs.add(charlie);
        var doris = new Dog("Doris", "Tax", 1, 19); // tail=3,70
        expectedDogs.add(doris);
        var ratata = new Dog("Ratata", "Dachshund", 14, 7); // tail=3,70
        expectedDogs.add(ratata);
        var bella = new Dog("Bella", "Rottweiler", 5, 9); // tail=4,50
        expectedDogs.add(bella);
        var lassie = new Dog("Lassie", "Dvärgschnauzer, peppar & salt", 17, 14); // tail=23,80
        expectedDogs.add(lassie);

        // Osorterad lista
        var dogs = new ArrayList<Dog>();
        dogs.add(bella);
        dogs.add(charlie);
        dogs.add(lassie);
        dogs.add(ratata);
        dogs.add(doris);

        var originalDogs = new ArrayList<>(dogs);

        var actualSwaps = DogSorter.sortDogs(cmp, dogs);

        assertDogListSorted(cmp, dogs, expectedDogs, 4, originalDogs, actualSwaps);
    }

    @Test
    @Order(150)
    @DisplayName("7 slumpmässigt genererade hundar som jämförs med DogTailComparator")
    public void sort7DogsUsingDogTailComparator() {
        var cmp = new DogTailComparator();

        // Hundar i testet i förväntad ordning
        var expectedDogs = new ArrayList<Dog>();
        var bamse = new Dog("Bamse", "Golden retriever", 3, 6); // tail=1,80
        expectedDogs.add(bamse);
        var snobben = new Dog("Snobben", "Puli", 4, 16); // tail=6,40
        expectedDogs.add(snobben);
        var karo = new Dog("Karo", "Beagle", 15, 11); // tail=16,50
        expectedDogs.add(karo);
        var ratata = new Dog("Ratata", "Chihuahua", 12, 14); // tail=16,80
        expectedDogs.add(ratata);
        var ludde = new Dog("Ludde", "Cocker spaniel", 17, 10); // tail=17,00
        expectedDogs.add(ludde);
        var rex = new Dog("Rex", "Grand danois", 16, 20); // tail=32,00
        expectedDogs.add(rex);
        var wilma = new Dog("Wilma", "Chihuahua", 19, 18); // tail=34,20
        expectedDogs.add(wilma);

        // Osorterad lista
        var dogs = new ArrayList<Dog>();
        dogs.add(karo);
        dogs.add(ratata);
        dogs.add(snobben);
        dogs.add(bamse);
        dogs.add(wilma);
        dogs.add(rex);
        dogs.add(ludde);

        var originalDogs = new ArrayList<>(dogs);

        var actualSwaps = DogSorter.sortDogs(cmp, dogs);

        assertDogListSorted(cmp, dogs, expectedDogs, 4, originalDogs, actualSwaps);
    }

    @Test
    @Order(160)
    @DisplayName("7 slumpmässigt genererade hundar som jämförs med DogNameComparator")
    public void sort7DogsUsingDogNameComparator() {
        var cmp = new DogNameComparator();

        // Hundar i testet i förväntad ordning
        var expectedDogs = new ArrayList<Dog>();
        var bella = new Dog("Bella", "Bulldogg", 13, 19); // tail=24,70
        expectedDogs.add(bella);
        var devil = new Dog("Devil", "Dachshund", 8, 6); // tail=3,70
        expectedDogs.add(devil);
        var doris = new Dog("Doris", "Shih tzu", 1, 10); // tail=1,00
        expectedDogs.add(doris);
        var ludde = new Dog("Ludde", "Tax", 5, 5); // tail=3,70
        expectedDogs.add(ludde);
        var rex = new Dog("Rex", "Mops", 3, 13); // tail=3,90
        expectedDogs.add(rex);
        var ronja = new Dog("Ronja", "Boxer", 19, 16); // tail=30,40
        expectedDogs.add(ronja);
        var wilma = new Dog("Wilma", "Border collie", 15, 7); // tail=10,50
        expectedDogs.add(wilma);

        // Osorterad lista
        var dogs = new ArrayList<Dog>();
        dogs.add(bella);
        dogs.add(doris);
        dogs.add(rex);
        dogs.add(ludde);
        dogs.add(wilma);
        dogs.add(devil);
        dogs.add(ronja);

        var originalDogs = new ArrayList<>(dogs);

        var actualSwaps = DogSorter.sortDogs(cmp, dogs);

        assertDogListSorted(cmp, dogs, expectedDogs, 4, originalDogs, actualSwaps);
    }

    @Test
    @Order(170)
    @DisplayName("7 slumpmässigt genererade hundar som jämförs med DogTailNameComparator")
    public void sort7DogsUsingDogTailNameComparator() {
        var cmp = new DogTailNameComparator();

        // Hundar i testet i förväntad ordning
        var expectedDogs = new ArrayList<Dog>();
        var molly = new Dog("Molly", "Dachshund", 11, 7); // tail=3,70
        expectedDogs.add(molly);
        var ratata = new Dog("Ratata", "Tax", 7, 19); // tail=3,70
        expectedDogs.add(ratata);
        var bella = new Dog("Bella", "Boxer", 10, 5); // tail=5,00
        expectedDogs.add(bella);
        var doris = new Dog("Doris", "Boxer", 13, 9); // tail=11,70
        expectedDogs.add(doris);
        var lassie = new Dog("Lassie", "Grand danois", 14, 19); // tail=26,60
        expectedDogs.add(lassie);
        var ludde = new Dog("Ludde", "Labrador", 19, 14); // tail=26,60
        expectedDogs.add(ludde);
        var rex = new Dog("Rex", "Border collie", 19, 14); // tail=26,60
        expectedDogs.add(rex);

        // Osorterad lista
        var dogs = new ArrayList<Dog>();
        dogs.add(ratata);
        dogs.add(doris);
        dogs.add(rex);
        dogs.add(ludde);
        dogs.add(molly);
        dogs.add(lassie);
        dogs.add(bella);

        var originalDogs = new ArrayList<>(dogs);

        var actualSwaps = DogSorter.sortDogs(cmp, dogs);

        assertDogListSorted(cmp, dogs, expectedDogs, 5, originalDogs, actualSwaps);
    }

    @Test
    @Order(180)
    @DisplayName("10 slumpmässigt genererade hundar som jämförs med DogTailComparator")
    public void sort10DogsUsingDogTailComparator() {
        var cmp = new DogTailComparator();

        // Hundar i testet i förväntad ordning
        var expectedDogs = new ArrayList<Dog>();
        var ludde = new Dog("Ludde", "Golden retriever", 5, 4); // tail=2,00
        expectedDogs.add(ludde);
        var lassie = new Dog("Lassie", "Yorkshireterrier", 8, 8); // tail=6,40
        expectedDogs.add(lassie);
        var wilma = new Dog("Wilma", "Labrador", 7, 10); // tail=7,00
        expectedDogs.add(wilma);
        var sigge = new Dog("Sigge", "Border collie", 15, 6); // tail=9,00
        expectedDogs.add(sigge);
        var karo = new Dog("Karo", "Rottweiler", 14, 7); // tail=9,80
        expectedDogs.add(karo);
        var bamse = new Dog("Bamse", "Shih tzu", 20, 6); // tail=12,00
        expectedDogs.add(bamse);
        var bella = new Dog("Bella", "Rottweiler", 7, 20); // tail=14,00
        expectedDogs.add(bella);
        var fido = new Dog("Fido", "Dvärgschnauzer, peppar & salt", 17, 12); // tail=20,40
        expectedDogs.add(fido);
        var charlie = new Dog("Charlie", "Beagle", 17, 14); // tail=23,80
        expectedDogs.add(charlie);
        var rex = new Dog("Rex", "Shih tzu", 16, 17); // tail=27,20
        expectedDogs.add(rex);

        // Osorterad lista
        var dogs = new ArrayList<Dog>();
        dogs.add(bella);
        dogs.add(ludde);
        dogs.add(charlie);
        dogs.add(lassie);
        dogs.add(sigge);
        dogs.add(bamse);
        dogs.add(wilma);
        dogs.add(rex);
        dogs.add(fido);
        dogs.add(karo);

        var originalDogs = new ArrayList<>(dogs);

        var actualSwaps = DogSorter.sortDogs(cmp, dogs);

        assertDogListSorted(cmp, dogs, expectedDogs, 8, originalDogs, actualSwaps);
    }

    @Test
    @Order(190)
    @DisplayName("10 slumpmässigt genererade hundar som jämförs med DogNameComparator")
    public void sort10DogsUsingDogNameComparator() {
        var cmp = new DogNameComparator();

        // Hundar i testet i förväntad ordning
        var expectedDogs = new ArrayList<Dog>();
        var bamse = new Dog("Bamse", "Tax", 20, 9); // tail=3,70
        expectedDogs.add(bamse);
        var charlie = new Dog("Charlie", "Tax", 5, 12); // tail=3,70
        expectedDogs.add(charlie);
        var devil = new Dog("Devil", "Labrador", 7, 20); // tail=14,00
        expectedDogs.add(devil);
        var karo = new Dog("Karo", "Mops", 2, 7); // tail=1,40
        expectedDogs.add(karo);
        var milou = new Dog("Milou", "Grand danois", 12, 3); // tail=3,60
        expectedDogs.add(milou);
        var molly = new Dog("Molly", "Tax", 17, 15); // tail=3,70
        expectedDogs.add(molly);
        var ratata = new Dog("Ratata", "Golden retriever", 10, 20); // tail=20,00
        expectedDogs.add(ratata);
        var rex = new Dog("Rex", "Vinthund", 8, 15); // tail=12,00
        expectedDogs.add(rex);
        var ronja = new Dog("Ronja", "Bulldogg", 18, 15); // tail=27,00
        expectedDogs.add(ronja);
        var snobben = new Dog("Snobben", "Border collie", 16, 15); // tail=24,00
        expectedDogs.add(snobben);

        // Osorterad lista
        var dogs = new ArrayList<Dog>();
        dogs.add(ronja);
        dogs.add(bamse);
        dogs.add(snobben);
        dogs.add(rex);
        dogs.add(milou);
        dogs.add(devil);
        dogs.add(molly);
        dogs.add(charlie);
        dogs.add(karo);
        dogs.add(ratata);

        var originalDogs = new ArrayList<>(dogs);

        var actualSwaps = DogSorter.sortDogs(cmp, dogs);

        assertDogListSorted(cmp, dogs, expectedDogs, 7, originalDogs, actualSwaps);
    }

    @Test
    @Order(200)
    @DisplayName("10 slumpmässigt genererade hundar som jämförs med DogTailNameComparator")
    public void sort10DogsUsingDogTailNameComparator() {
        var cmp = new DogTailNameComparator();

        // Hundar i testet i förväntad ordning
        var expectedDogs = new ArrayList<Dog>();
        var ratata = new Dog("Ratata", "Grand danois", 4, 1); // tail=0,40
        expectedDogs.add(ratata);
        var charlie = new Dog("Charlie", "Tax", 8, 13); // tail=3,70
        expectedDogs.add(charlie);
        var rex = new Dog("Rex", "Dachshund", 2, 19); // tail=3,70
        expectedDogs.add(rex);
        var sigge = new Dog("Sigge", "Dachshund", 14, 3); // tail=3,70
        expectedDogs.add(sigge);
        var bamse = new Dog("Bamse", "Golden retriever", 20, 3); // tail=6,00
        expectedDogs.add(bamse);
        var milou = new Dog("Milou", "Boxer", 5, 12); // tail=6,00
        expectedDogs.add(milou);
        var ludde = new Dog("Ludde", "Rottweiler", 17, 5); // tail=8,50
        expectedDogs.add(ludde);
        var doris = new Dog("Doris", "Rottweiler", 10, 14); // tail=14,00
        expectedDogs.add(doris);
        var wilma = new Dog("Wilma", "Chihuahua", 19, 10); // tail=19,00
        expectedDogs.add(wilma);
        var molly = new Dog("Molly", "Dvärgschnauzer, peppar & salt", 12, 18); // tail=21,60
        expectedDogs.add(molly);

        // Osorterad lista
        var dogs = new ArrayList<Dog>();
        dogs.add(sigge);
        dogs.add(charlie);
        dogs.add(wilma);
        dogs.add(doris);
        dogs.add(rex);
        dogs.add(ratata);
        dogs.add(molly);
        dogs.add(milou);
        dogs.add(ludde);
        dogs.add(bamse);

        var originalDogs = new ArrayList<>(dogs);

        var actualSwaps = DogSorter.sortDogs(cmp, dogs);

        assertDogListSorted(cmp, dogs, expectedDogs, 7, originalDogs, actualSwaps);
    }

    /**
     * Denna record (klass) används för att kontrollera att bara de förväntade
     * metoderna finns i det publika gränssnittet i de klasser du implementerat.
     * Kopior av MethodHeader finns i de flesta testfalls-filerna. Duplicerad kod är
     * normalt en dålig idé eftersom det leder till svårigheter med underhåll när
     * man måste hitta alla ställen koden finns på och applicera ändringar överallt.
     * <p>
     * Här är detta acceptabelt eftersom det gör att varje testklass innehåller allt
     * som behövs för att kunna köra den, och för att alla ändringar automatiskt
     * läggs in i alla klasser.
     */
    public record MethodHeader(boolean isStatic, String returnType, java.util.regex.Pattern namePattern,
                               String[] parameters) {

        public MethodHeader(boolean isStatic, String returnType, String namePattern, String... parameters) {
            this(isStatic, returnType, java.util.regex.Pattern.compile(namePattern), parameters);
        }

        public boolean matches(java.lang.reflect.Method m) {
            if (isStatic != java.lang.reflect.Modifier.isStatic(m.getModifiers()))
                return false;

            if (!returnType.equals(m.getReturnType().getSimpleName()))
                return false;

            if (!namePattern.matcher(m.getName()).matches())
                return false;

            var actualParams = java.util.stream.Stream.of(m.getParameterTypes()).map(p -> p.getSimpleName()).toList()
                    .toArray(new String[] {});
            if (!java.util.Arrays.equals(parameters, actualParams))
                return false;

            return true;
        }

        @Override
        public String toString() {
            return "%s%s %s%s".formatted(isStatic ? "static " : "", returnType, namePattern,
                    java.util.Arrays.toString(parameters));
        }

    }

    // @formatter:off
    /**
     * Denna klass är automatiskt genererad. Ändringar i den kommer att skrivas
     * över vid nästa uppdatering.
     */
    public class DogSorterImplementationValidator {

        private final Class<?> cut = DogSorter.class;
        private static final java.util.Collection<MethodHeader> EXPECTED_PUBLIC_METHODS = new java.util.ArrayList<>();

        /**
         * Denna lista är automatiskt genererad från en referens-implementation.
         * Ordningen på metoderna är fast, men har inget med den förväntade ordningen
         * i klassen att göra.
         */
        static {
            EXPECTED_PUBLIC_METHODS.add(new MethodHeader(true, "int", "sortDogs", "Comparator", "ArrayList"));
        }

        public void execute() {
            org.junit.jupiter.api.Assertions.assertAll(this::onlyPrivateFields, this::allConstructorsPublicOrPrivate,
                    this::expectedNumberOfPublicConstructors, this::allMethodsPublicOrPrivate, this::onlyExpectedPublicMethods);
        }

        private void onlyPrivateFields() {
            for (var f : cut.getDeclaredFields()) {
                org.junit.jupiter.api.Assertions.assertTrue(isPrivate(f),
                        "Fältet %s i %s har inte en korrekt skyddsnivå. Endast privat är tillåten.".formatted(f.getName(),
                                cut.getSimpleName()));
            }
        }

        private void expectedNumberOfPublicConstructors() {
            org.junit.jupiter.api.Assertions.assertEquals(1, cut.getDeclaredConstructors().length,
                    "Fel antal publika konstruktorer i klassen %s".formatted(cut.getSimpleName()));
        }

        private void allConstructorsPublicOrPrivate() {
            for (var c : cut.getDeclaredConstructors()) {
                org.junit.jupiter.api.Assertions.assertTrue(isPublic(c) || isPrivate(c),
                        "Konstruktorn %s i %s har inte en korrekt skyddsnivå. Endast publik eller privat är tillåtna."
                                .formatted(c.getName(), cut.getSimpleName()));
            }
        }

        private void allMethodsPublicOrPrivate() {
            for (var m : cut.getDeclaredMethods()) {
                org.junit.jupiter.api.Assertions.assertTrue(isPublic(m) || isPrivate(m),
                        "Metoden %s i %s har inte en korrekt skyddsnivå. Endast publik eller privat är tillåtna."
                                .formatted(m.getName(), cut.getSimpleName()));
            }
        }

        public void onlyExpectedPublicMethods() {
            var matches = unexpectedPublicMethods();
            org.junit.jupiter.api.Assertions.assertTrue(matches.isEmpty(),
                    "Det finns publika metoder i %s som testen inte förväntar sig:\n   %s\n".formatted(cut.getSimpleName(),
                            String.join("\n   ", matches.stream().map(m -> m.toString()).toList())));
        }

        private java.util.List<java.lang.reflect.Method> unexpectedPublicMethods() {
            return java.util.stream.Stream.of(cut.getDeclaredMethods())
                    .filter(m -> isPublic(m) && !isExpectedPublicMethod(m)).toList();
        }

        private boolean isExpectedPublicMethod(java.lang.reflect.Method m) {
            return EXPECTED_PUBLIC_METHODS.stream().anyMatch(mh -> mh.matches(m));
        }

        private static boolean isPublic(java.lang.reflect.Member m) {
            return java.lang.reflect.Modifier.isPublic(m.getModifiers());
        }

        private static boolean isPrivate(java.lang.reflect.Member m) {
            return java.lang.reflect.Modifier.isPrivate(m.getModifiers());
        }

    }
    // @formatter:on

}
