import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.*;
import java.util.*;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;

/**
 * Testfall för metoden för att byta plats på två hundar i uppgift HR2.6.
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
@DisplayName("HR2.6: Testfall för metoden för att byta plats på två hundar")
public class HR2_6_SwapDogsTest {

    public static final String VERSION = "2024-01-04 13:16";

    private static final String DEFAULT_BREED = "Breed";
    private static final int DEFAULT_AGE = 3;
    private static final int DEFAULT_WEIGHT = 7;

    private static final Dog FIRST_DOG = new Dog("First", DEFAULT_BREED, DEFAULT_AGE, DEFAULT_WEIGHT);
    private static final Dog SECOND_DOG = new Dog("Second", DEFAULT_BREED, DEFAULT_AGE, DEFAULT_WEIGHT);

    private ArrayList<Dog> dogs = new ArrayList<>(Arrays.asList(FIRST_DOG, SECOND_DOG));

    private void callSwapMethod(ArrayList<Dog> dogs, int i, int j) {
        // TODO: acceptera andra typer av listor
        try {
            Method swapMethod = DogSorter.class.getDeclaredMethod("swapDogs", ArrayList.class, int.class, int.class);
            swapMethod.setAccessible(true);
            swapMethod.invoke(null, dogs, i, j);
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | InvocationTargetException e) {
            fail("Fel vid anrop på swapDogs", e);
        }
    }

    @Test
    @Order(10)
    @DisplayName("Implementerad enligt instruktionerna")
    public void validateImplementation() {
        new DogSorterImplementationValidator().execute();
    }

    @Test
    @Order(20)
    @DisplayName("Byt plats på två element, indexen i \"rätt\" ordning")
    public void swapFirstAndSecondDog() {
        callSwapMethod(dogs, 0, 1);

        var expected = new ArrayList<>(Arrays.asList(SECOND_DOG, FIRST_DOG));
        assertEquals(expected, dogs);
    }

    @Test
    @Order(30)
    @DisplayName("Byt plats på två element, indexen i omvänd ordning")
    public void swapSecondAndFirstDog() {
        callSwapMethod(dogs, 1, 0);

        var expected = new ArrayList<>(Arrays.asList(SECOND_DOG, FIRST_DOG));
        assertEquals(expected, dogs);
    }

    @Test
    @Order(40)
    @DisplayName("Byta plats på ett element med sig själv ")
    public void swapFirstAndFirstDog() {
        callSwapMethod(dogs, 0, 0);

        var expected = new ArrayList<>(Arrays.asList(FIRST_DOG, SECOND_DOG));
        assertEquals(expected, dogs);
    }

    @Test
    @Order(50)
    @DisplayName("Serie av slumpmässiga byten")
    public void multipleSwaps() {
        var snobben = new Dog("Snobben", "Boxer", 2, 5); // svans=1,0
        var fido = new Dog("Fido", "Labrador", 7, 3); // svans=2,1
        var karo = new Dog("Karo", "Dvärgschnauzer, peppar & salt", 2, 16); // svans=3,2
        var bella = new Dog("Bella", "Dachshund", 17, 9); // svans=3,7
        var ratata = new Dog("Ratata", "Tax", 11, 2); // svans=3,7
        var wilma = new Dog("Wilma", "Dachshund", 17, 15); // svans=3,7
        var ronja = new Dog("Ronja", "Boxer", 2, 20); // svans=4,0
        var doris = new Dog("Doris", "Pudel", 13, 7); // svans=9,1
        var rex = new Dog("Rex", "Grand danois", 20, 8); // svans=16,0
        var lassie = new Dog("Lassie", "Dvärgschnauzer, peppar & salt", 14, 15); // svans=21,0

        var actual = new ArrayList<Dog>(
                Arrays.asList(snobben, fido, karo, bella, ratata, wilma, ronja, doris, rex, lassie));
        var expected = new ArrayList<Dog>(actual);

        var rnd = new Random();

        for (int n = 0; n < 25; n++) {
            int i = rnd.nextInt(actual.size());
            int j = rnd.nextInt(actual.size());
            callSwapMethod(actual, i, j);
            Collections.swap(expected, i, j);
            assertEquals(expected, actual);
        }
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