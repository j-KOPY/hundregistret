import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;

/**
 * Testfall för jämförelsefunktionen för svanslängd och namn i uppgift HR2.3.
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
@DisplayName("HR2.3: Testfall för jämförelsefunktionen för svanslängd och namn")
public class HR2_3_DogTailNameComparatorTest {

    public static final String VERSION = "2023-12-27 10:47";

    private static final String DEFAULT_NAME = "Name";
    private static final String DEFAULT_BREED = "Breed";
    private static final int DEFAULT_AGE = 3;
    private static final int DEFAULT_WEIGHT = 7;

    @Test
    @Order(10)
    @DisplayName("Implementerad enligt instruktionerna")
    public void validateImplementation() {
        new DogTailNameComparatorImplementationValidator().execute();
    }

    @Test
    @Order(20)
    @DisplayName("Jämförelse med samma hund ger resultatet 0")
    public void aDogIsEqualToItSelf() {
        DogTailNameComparator sut = new DogTailNameComparator();
        Dog dog = new Dog(DEFAULT_NAME, DEFAULT_BREED, DEFAULT_AGE, DEFAULT_WEIGHT);
        assertEquals(0, sut.compare(dog, dog));
    }

    @Test
    @Order(30)
    @DisplayName("Jämförelse med annan hund med samma svanslängd och namn ger resultatet 0")
    public void aDogIsEqualToAnotherDogWithTheSameTailLengthAndName() {
        DogTailNameComparator sut = new DogTailNameComparator();
        Dog firstDog = new Dog(DEFAULT_NAME, DEFAULT_BREED, DEFAULT_AGE, DEFAULT_WEIGHT);
        Dog secondDog = new Dog(new String(DEFAULT_NAME), DEFAULT_BREED, DEFAULT_AGE, DEFAULT_WEIGHT);
        assertEquals(0, sut.compare(firstDog, secondDog));
    }

    @Test
    @Order(40)
    @DisplayName("Jämförelse med en kort och en lång svans ger ett resultat under 0 om bägge har samma namn")
    public void theFirstDogHasShorterTailThanTheSecondDogNamesAreTheSame() {
        DogTailNameComparator sut = new DogTailNameComparator();
        Dog firstDog = new Dog(DEFAULT_NAME, DEFAULT_BREED, 1, 1);
        Dog secondDog = new Dog(DEFAULT_NAME, DEFAULT_BREED, 5, 5);
        assertTrue(sut.compare(firstDog, secondDog) < 0);
    }

    @Test
    @Order(50)
    @DisplayName("Jämförelse med en kort och en lång svans ger ett resultat under 0 om de har olika namn")
    public void theFirstDogHasShorterTailThanTheSecondDogNamesAreDifferent() {
        DogTailNameComparator sut = new DogTailNameComparator();
        // Ordningen på namnen vald för att ge fel om villkoren utvärderas i fel ordning
        Dog firstDog = new Dog("Karo", DEFAULT_BREED, 1, 1);
        Dog secondDog = new Dog("Fido", DEFAULT_BREED, 5, 5);
        assertTrue(sut.compare(firstDog, secondDog) < 0);
    }

    @Test
    @Order(60)
    @DisplayName("Jämförelse med en lång och en kort svans ger ett resultat över 0 om de har samma namn")
    public void theFirstDogHasLongerTailThanTheSecondDogNamesAreTheSame() {
        DogTailNameComparator sut = new DogTailNameComparator();
        Dog firstDog = new Dog(DEFAULT_NAME, DEFAULT_BREED, 5, 5);
        Dog secondDog = new Dog(DEFAULT_NAME, DEFAULT_BREED, 1, 1);
        assertTrue(sut.compare(firstDog, secondDog) > 0);
    }

    @Test
    @Order(70)
    @DisplayName("Jämförelse med en lång och en kort svans ger ett resultat över 0 om de har olika namn")
    public void theFirstDogHasLongerTailThanTheSecondDogNamesAreDifferent() {
        DogTailNameComparator sut = new DogTailNameComparator();
        // Ordningen på namnen vald för att ge fel om villkoren utvärderas i fel ordning
        Dog firstDog = new Dog("Fido", DEFAULT_BREED, 5, 5);
        Dog secondDog = new Dog("Karo", DEFAULT_BREED, 1, 1);
        assertTrue(sut.compare(firstDog, secondDog) > 0);
    }

    @Test
    @Order(80)
    @DisplayName("Jämförelse med ett namn tidigare i bokstavsordning och ett efter ger ett resultat under 0 när svanlängden är densamma")
    public void theFirstDogComesBeforeTheSecondInAlphabeticOrderTailLengthsAreTheSame() {
        DogTailNameComparator sut = new DogTailNameComparator();
        Dog firstDog = new Dog("Fido", DEFAULT_BREED, DEFAULT_AGE, DEFAULT_WEIGHT);
        Dog secondDog = new Dog("Karo", DEFAULT_BREED, DEFAULT_AGE, DEFAULT_WEIGHT);
        assertTrue(sut.compare(firstDog, secondDog) < 0);
    }

    @Test
    @Order(90)
    @DisplayName("Jämförelse med ett namn efter i bokstavsordning och ett tidigare ger ett resultat över 0 när svanlängden är densamma")
    public void theFirstDogComesAfterTheSecondInAlphabeticOrderTailLengthsAreTheSame() {
        DogTailNameComparator sut = new DogTailNameComparator();
        Dog firstDog = new Dog("Karo", DEFAULT_BREED, DEFAULT_AGE, DEFAULT_WEIGHT);
        Dog secondDog = new Dog("Fido", DEFAULT_BREED, DEFAULT_AGE, DEFAULT_WEIGHT);
        assertTrue(sut.compare(firstDog, secondDog) > 0);
    }

    @Test
    @Order(100)
    @DisplayName("Jämförelse med en kortare och en längre version av samma namn ger ett resultat under 0 när svanlängden är densamma")
    public void theFirstDogHasShorterVersionOfTheNameOfTheSecondTailLengthsAreTheSame() {
        DogTailNameComparator sut = new DogTailNameComparator();
        Dog fido = new Dog("Fido", DEFAULT_BREED, DEFAULT_AGE, DEFAULT_WEIGHT);
        Dog fidolina = new Dog("Fidolina", DEFAULT_BREED, DEFAULT_AGE, DEFAULT_WEIGHT);
        assertTrue(sut.compare(fido, fidolina) < 0);
    }

    @Test
    @Order(110)
    @DisplayName("Jämförelse med en längre och en kortare version av samma namn ger ett resultat över 0 när svanlängden är densamma")
    public void theFirstDogHasLongerVersionOfTheNameOfTheSecondTailLengthsAreTheSame() {
        DogTailNameComparator sut = new DogTailNameComparator();
        Dog fido = new Dog("Fido", DEFAULT_BREED, DEFAULT_AGE, DEFAULT_WEIGHT);
        Dog fidolina = new Dog("Fidolina", DEFAULT_BREED, DEFAULT_AGE, DEFAULT_WEIGHT);
        assertTrue(sut.compare(fidolina, fido) > 0);
    }

    @Test
    @Order(120)
    @DisplayName("Normaliserade namn används vid jämförelsen när svanlängden är densamma")
    public void normalizedNamesUsedTailLengthsAreTheSame() {
        DogTailNameComparator sut = new DogTailNameComparator();
        Dog firstDog = new Dog("Fido", DEFAULT_BREED, DEFAULT_AGE, DEFAULT_WEIGHT);
        Dog secondDog = new Dog("fido", DEFAULT_BREED, DEFAULT_AGE, DEFAULT_WEIGHT);
        assertEquals(0, sut.compare(firstDog, secondDog));
    }

    @Test
    @Order(130)
    @DisplayName("Sortera hundar med hjälp av comparatorn")
    public void sortDogsUsingComparator() {
        var molly = new Dog("Molly", "Rottweiler", 7, 5); // svans=3,5
        var lassie = new Dog("Lassie", "Dachshund", 9, 14); // svans=3,7
        var ronja = new Dog("Ronja", "Dachshund", 11, 2); // svans=3,7
        var snobben = new Dog("Snobben", "Dachshund", 13, 7); // svans=3,7
        var sigge = new Dog("Sigge", "Grand danois", 4, 17); // svans=6,8
        var karo = new Dog("Karo", "Pudel", 18, 6); // svans=10,8
        var ludde = new Dog("Ludde", "Boxer", 18, 6); // svans=10,8
        var ratata = new Dog("Ratata", "Shih tzu", 7, 16); // svans=11,2
        var charlie = new Dog("Charlie", "Golden retriever", 12, 14); // svans=16,8
        var wilma = new Dog("Wilma", "Dobermann", 19, 14); // svans=26,6

        Dog[] expected = { molly, lassie, ronja, snobben, sigge, karo, ludde, ratata, charlie, wilma };

        Dog[] actual = { ludde, sigge, ratata, lassie, karo, snobben, ronja, molly, charlie, wilma };

        var sut = new DogTailNameComparator();
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
    public class DogTailNameComparatorImplementationValidator {

        private final Class<?> cut = DogTailNameComparator.class;
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