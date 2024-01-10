import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;

/**
 * Testfall för jämförelsefunktionen för svanslängd i uppgift HR2.1.
 * <p>
 * Beskrivningen av testfallens uppgift, styrka och svagheter från
 * <code>{@link HR1_1_OwnerTest}</code> gäller (naturligvis) också för
 * testfallen i denna klass. Var speciellt uppmärksam på att testfallen kan
 * uppdateras när som helst, inklusive <em>efter</em> deadline.
 *
 * @author Henrik Bergström
 * @version 2023-12-27 10:47
 * @see HR1_1_OwnerTest
 */
@TestMethodOrder(OrderAnnotation.class)
@DisplayName("HR2.1: Testfall för jämförelsefunktionen för svanslängd")
public class HR2_1_DogTailComparatorTest {

    public static final String VERSION = "2023-12-27 10:47";

    private static final String DEFAULT_NAME = "Name";
    private static final String DEFAULT_BREED = "Breed";
    private static final Dog SHORT_TAIL_DOG = new Dog(DEFAULT_NAME, DEFAULT_BREED, 2, 3);
    private static final Dog LONG_TAIL_DOG = new Dog(DEFAULT_NAME, DEFAULT_BREED, 4, 5);
    private static final Dog ANY_DOG = SHORT_TAIL_DOG;

    @Test
    @Order(10)
    @DisplayName("Implementerad enligt instruktionerna")
    public void validateImplementation() {
        new DogTailComparatorImplementationValidator().execute();
    }

    @Test
    @Order(20)
    @DisplayName("Jämförelse med samma hund ger resultatet 0")
    public void aDogIsEqualToItSelf() {
        DogTailComparator sut = new DogTailComparator();
        assertEquals(0, sut.compare(ANY_DOG, ANY_DOG));
    }

    @Test
    @Order(30)
    @DisplayName("Jämförelse med annan hund med samma svanslängd ger resultatet 0")
    public void aDogIsEqualToAnotherDogWithTheSameTailLength() {
        DogTailComparator sut = new DogTailComparator();
        assertEquals(0,
                sut.compare(ANY_DOG, new Dog("Another Name", DEFAULT_BREED, ANY_DOG.getAge(), ANY_DOG.getWeight())));
    }

    @Test
    @Order(40)
    @DisplayName("Jämförelse med en kort och en lång svans ger ett resultat under 0")
    public void theFirstDogHasShorterTailThanTheSecondDog() {
        DogTailComparator sut = new DogTailComparator();
        assertTrue(sut.compare(SHORT_TAIL_DOG, LONG_TAIL_DOG) < 0);
    }

    @Test
    @Order(50)
    @DisplayName("Jämförelse med en lång och en kort svans ger ett resultat över 0")
    public void theFirstDogHasLongerTailThanTheSecondDog() {
        DogTailComparator sut = new DogTailComparator();
        assertTrue(sut.compare(LONG_TAIL_DOG, SHORT_TAIL_DOG) > 0);
    }

    @Test
    @Order(60)
    @DisplayName("Sortera hundar med hjälp av comparatorn")
    public void sortDogsUsingComparator() {
        var fido = new Dog("Fido", "Shih tzu", 3, 2); // svans=0,6
        var devil = new Dog("Devil", "Dvärgschnauzer, peppar & salt", 2, 11); // svans=2,2
        var molly = new Dog("Molly", "Dobermann", 11, 3); // svans=3,3
        var milou = new Dog("Milou", "Vinthund", 3, 13); // svans=3,9
        var ronja = new Dog("Ronja", "Cocker spaniel", 6, 7); // svans=4,2
        var lassie = new Dog("Lassie", "Bulldogg", 5, 11); // svans=5,5
        var ratata = new Dog("Ratata", "Golden retriever", 8, 7); // svans=5,6
        var charlie = new Dog("Charlie", "Border collie", 5, 18); // svans=9,0
        var sigge = new Dog("Sigge", "Bulldogg", 6, 20); // svans=12,0
        var karo = new Dog("Karo", "Yorkshireterrier", 18, 11); // svans=19,8

        Dog[] expected = { fido, devil, molly, milou, ronja, lassie, ratata, charlie, sigge, karo };

        Dog[] actual = { lassie, ratata, fido, devil, ronja, milou, charlie, sigge, molly, karo };

        var sut = new DogTailComparator();
        Arrays.sort(actual, sut);

        assertArrayEquals(expected, actual);
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
    public class DogTailComparatorImplementationValidator {

        private final Class<?> cut = DogTailComparator.class;
        private static final java.util.Collection<MethodHeader> EXPECTED_PUBLIC_METHODS = new java.util.ArrayList<>();

        /**
         * Denna lista är automatiskt genererad från en referens-implementation.
         * Ordningen på metoderna är fast, men har inget med den förväntade ordningen
         * i klassen att göra.
         */
        static {
            EXPECTED_PUBLIC_METHODS.add(new MethodHeader(false, "int", "compare", "Dog", "Dog"));
            EXPECTED_PUBLIC_METHODS.add(new MethodHeader(false, "int", "compare", "Object", "Object"));
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