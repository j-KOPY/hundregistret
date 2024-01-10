import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.*;
import java.util.*;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * Testfall för metoden för att hitta "nästa" hund i uppgift HR2.7.
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
@DisplayName("HR2.7: Testfall för metoden för att hitta \"nästa\" hund")
public class HR2_7_NextDogTest {

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

    private void test(Comparator<Dog> cmp, int index, int expected, Dog... dogs) {
        var dogList = new ArrayList<>(Arrays.asList(dogs));

        var actual = callNextDogMethod(cmp, dogList, index);
        assertEquals(expected, actual, """
				Fel index returnerades av nextDog-metoden.
				Comparatortyp: %s
				Hundlistan: %s
				Index: %d
				""".formatted(cmp.getClass().getName(), dogList, index));
    }

    private int callNextDogMethod(Comparator<Dog> cmp, ArrayList<Dog> dogs, int index) {
        // TODO: acceptera andra typer av listor
        try {
            Method nextDogMethod = DogSorter.class.getDeclaredMethod("nextDog", Comparator.class, ArrayList.class,
                    int.class);
            nextDogMethod.setAccessible(true);
            return (int) nextDogMethod.invoke(null, cmp, dogs, index);
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | InvocationTargetException e) {
            fail("Fel vid anrop på nextDog", e);
            // Detta kan aldrig ske eftersom fail avbryter metoden. Kompilatorn kan dock
            // inte se detta, så retursatsen är nödvändig.
            return -1;
        }
    }

    @Test
    @Order(10)
    @DisplayName("Implementerad enligt instruktionerna")
    public void validateImplementation() {
        new DogSorterImplementationValidator().execute();
    }

    @ParameterizedTest(name = "{index} hunden på index {1} är den \"minsta\" av hundarna på index {0}-2")
    @CsvSource({ "0,0", "1,1", "2,2" })
    @Order(20)
    @DisplayName("Nästa hund finns på det angivna indexet")
    public void atGivenIndex(int index, int expected) {
        test(TAIL_COMPARATOR, index, expected, DOG_A_1, DOG_B_2, DOG_C_3);
    }

    @ParameterizedTest(name = "{index} hunden på index {1} är den \"minsta\" av hundarna på index {0}-3")
    @CsvSource({ "0,1", "2,3" })
    @Order(30)
    @DisplayName("Nästa hund finns på indexet direkt efter")
    public void atNextIndex(int index, int expected) {
        test(TAIL_COMPARATOR, index, expected, DOG_E_4, DOG_A_1, DOG_C_3, DOG_B_2);
    }

    @ParameterizedTest(name = "{index} hunden på index {1} är den \"minsta\" av hundarna på index {0}-5")
    @CsvSource({ "0,2", "3,5" })
    @Order(40)
    @DisplayName("Nästa hund finns på ett index minst två platser bort ")
    public void atLaterIndex(int index, int expected) {
        test(TAIL_COMPARATOR, index, expected, DOG_E_4, DOG_C_3, DOG_A_1, DOG_F_4, DOG_D_3, DOG_B_2);
    }

    @ParameterizedTest(name = "{index} hunden på index {1} är den \"minsta\" av hundarna på index {0}-5")
    @CsvSource({ "0,2", "1,2", "2,2", "3,5" })
    @Order(50)
    @DisplayName("Jämförelse av namn")
    public void atGivenIndexByName(int index, int expected) {
        test(NAME_COMPARATOR, index, expected, DOG_E_4, DOG_C_3, DOG_A_1, DOG_F_4, DOG_D_3, DOG_B_2);
    }

    @ParameterizedTest(name = "{index} hunden på index {1} är den \"minsta\" av hundarna på index {0}-5")
    @CsvSource({ "0,1", "1,1", "2,4" })
    @Order(60)
    @DisplayName("Jämförelse av svans och namn")
    public void atGivenIndexByTailAndName(int index, int expected) {
        test(TAIL_NAME_COMPARATOR, index, expected, DOG_B_2, DOG_A_1, DOG_F_4, DOG_D_3, DOG_C_3, DOG_E_4);
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