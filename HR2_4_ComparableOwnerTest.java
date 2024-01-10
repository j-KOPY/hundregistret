import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;

/**
 * Testfall för den naturliga sorteringsordningen för ägare i uppgift HR2.4.
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
@DisplayName("HR2.4: Testfall för den naturliga sorteringsordningen för ägare")
public class HR2_4_ComparableOwnerTest {

    public static final String VERSION = "2024-01-04 13:16";

    private static final Owner FIRST_OWNER_IN_ALPHABETIC_ORDER = new Owner("Henrik");
    private static final Owner SECOND_OWNER_IN_ALPHABETIC_ORDER = new Owner("Olle");
    private static final Owner ANY_OWNER = FIRST_OWNER_IN_ALPHABETIC_ORDER;

    @Test
    @Order(10)
    @DisplayName("Implementerad enligt instruktionerna")
    public void validateImplementation() {
        new OwnerImplementationValidator().execute();
    }

    @Test
    @Order(20)
    @DisplayName("Jämförelse med samma ägare ger resultatet 0")
    public void aOwnerIsEqualToItSelf() {
        assertEquals(0, ANY_OWNER.compareTo(ANY_OWNER));
    }

    @Test
    @Order(30)
    @DisplayName("Jämförelse med annan ägare med samma namn ger resultatet 0")
    public void aOwnerIsEqualToAnotherOwnerWithTheSameName() {
        assertEquals(0, ANY_OWNER.compareTo(new Owner(new String(ANY_OWNER.getName()))));
    }

    @Test
    @Order(40)
    @DisplayName("Jämförelse med ett namn tidigare i bokstavsordning och ett efter ger ett resultat under 0")
    public void theFirstOwnerComesBeforeTheSecondInAlphabeticOrder() {
        assertTrue(FIRST_OWNER_IN_ALPHABETIC_ORDER.compareTo(SECOND_OWNER_IN_ALPHABETIC_ORDER) < 0);
    }

    @Test
    @Order(50)
    @DisplayName("Jämförelse med ett namn efter i bokstavsordning och ett tidigare ger ett resultat över 0")
    public void theFirstOwnerComesAfterTheSecondInAlphabeticOrder() {
        assertTrue(SECOND_OWNER_IN_ALPHABETIC_ORDER.compareTo(FIRST_OWNER_IN_ALPHABETIC_ORDER) > 0);
    }

    @Test
    @Order(60)
    @DisplayName("Jämförelse med en kortare och en längre version av samma namn ger ett resultat under 0")
    public void theFirstOwnerHasShorterVersionOfTheNameOfTheSecond() {
        Owner anna = new Owner("Anna");
        Owner annalena = new Owner("Anna-Lena");
        assertTrue(anna.compareTo(annalena) < 0);
    }

    @Test
    @Order(70)
    @DisplayName("Jämförelse med en längre och en kortare version av samma namn ger ett resultat över 0")
    public void theFirstOwnerHasLongerVersionOfTheNameOfTheSecond() {
        Owner anna = new Owner("Anna");
        Owner annalena = new Owner("Anna-Lena");
        assertTrue(annalena.compareTo(anna) > 0);
    }

    @Test
    @Order(80)
    @DisplayName("Normaliserade namn används vid jämförelsen")
    public void normalizedNamesUsed() {
        assertEquals(0, ANY_OWNER.compareTo(new Owner(ANY_OWNER.getName().toLowerCase())));
    }

    @Test
    @Order(90)
    @DisplayName("Sortera ägare")
    public void sortDogsUsingComparator() {
        var ann = new Owner("Ann");
        var cecilia = new Owner("Cecilia");
        var gustav = new Owner("Gustav");
        var helga = new Owner("Helga");
        var henrik = new Owner("Henrik");
        var jenny = new Owner("Jenny");
        var johanna = new Owner("Johanna");
        var kalle = new Owner("Kalle");
        var louise = new Owner("Louise");
        var ulla = new Owner("Ulla");

        Owner[] expected = { ann, cecilia, gustav, helga, henrik, jenny, johanna, kalle, louise, ulla };
        Owner[] actual = { johanna, jenny, ulla, ann, louise, henrik, helga, kalle, cecilia, gustav };
        Arrays.sort(actual);

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
    public class OwnerImplementationValidator {

        private final Class<?> cut = Owner.class;
        private static final java.util.Collection<MethodHeader> EXPECTED_PUBLIC_METHODS = new java.util.ArrayList<>();

        /**
         * Denna lista är automatiskt genererad från en referens-implementation.
         * Ordningen på metoderna är fast, men har inget med den förväntade ordningen
         * i klassen att göra.
         */
        static {
            EXPECTED_PUBLIC_METHODS.add(new MethodHeader(false, "ArrayList", "getDogs"));
            EXPECTED_PUBLIC_METHODS.add(new MethodHeader(false, "String", "getName"));
            EXPECTED_PUBLIC_METHODS.add(new MethodHeader(false, "String", "toString"));
            EXPECTED_PUBLIC_METHODS.add(new MethodHeader(false, "boolean", "addDog", "Dog"));
            EXPECTED_PUBLIC_METHODS.add(new MethodHeader(false, "boolean", "removeDog", "Dog"));
            EXPECTED_PUBLIC_METHODS.add(new MethodHeader(false, "int", "compareTo", "Object"));
            EXPECTED_PUBLIC_METHODS.add(new MethodHeader(false, "int", "compareTo", "Owner"));
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