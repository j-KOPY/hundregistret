import static org.junit.jupiter.api.Assertions.*;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.*;
import java.util.*;
import java.util.stream.Stream;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;

/**
 * Testfall för hundklassen i uppgift HR1.2. <p> Beskrivningen av testfallens
 * uppgift, styrka och svagheter från <code>{@link HR1_1_OwnerTest}</code>
 * gäller (naturligvis) också för testfallen i denna klass. Var speciellt
 * uppmärksam på att testfallen kan uppdateras när som helst, inklusive
 * <em>efter</em> deadline. <p> Testet
 * <code>{@link #randomDogHasCorrectAttribute(Dog, String, String, int, int, double)}</code>
 * är lite speciellt. Det är det sista funktionella testet i denna klass, och
 * testar egentligen inget nytt utan bara saker som de tidigare testen redan
 * täckt. Så, varför finns det överhuvudtaget? <p> Svaret ligger i hur testen är
 * framtagna. De tidigare testen är medvetet designade för att för att försöka
 * hitta ett visst potentiellt fel. Testdatat i detta test är istället skapat
 * med en slumpgenerator. Testfall skapade med hjälp av slump har många
 * nackdelar jämfört med manuellt designade, inte minst att det kan vara svårt
 * att veta vad som testats och att köra om samma test igen. En nackdel med
 * manuellt designade test är dock att det är lätt att fastna i ett visst sätt
 * att tänka, och på så sätt missa att testa vissa saker. Test baserade på slump
 * kan, om man har tur, undvika detta. <p> Liknande test finns i många av de
 * kommande uppgifterna. De är alltid de sista av de funktionella testen, och
 * testar ofta lite större scenarier.
 *
 * @author Henrik Bergström
 * @version 2023-12-27 10:47
 * @see HR1_1_OwnerTest
 */
@TestMethodOrder(OrderAnnotation.class)
@DisplayName("HR1.2: Testfall för hundklassen")
public class HR1_2_DogTest {

    public static final String VERSION = "2023-12-27 10:47";

    /*
     * Konstanter och hjälpmetoder för dessa
     */

    private static final String DEFAULT_NAME = "Name";
    private static final String DEFAULT_BREED = "Breed";
    private static final int DEFAULT_AGE = 3;
    private static final int DEFAULT_WEIGHT = 7;
    private static final double DEFAULT_TAIL_LENGTH = 2.1;

    private static final Method ASSUMED_METHOD_FOR_INCREASING_AGE_OF_DOG = guessUnnamedMethod(asArray(void.class, int.class), asArray(Void.class, int.class));

    private static Method guessUnnamedMethod(Class<?>[] allowedReturnTypes, Class<?>[] allowedParamTypes) {
        var possibleMatches = getMatchingPublicMethodsInDog(true, allowedReturnTypes, null, allowedParamTypes);
        possibleMatches.removeAll(completelySpecifiedMethodsInDog());
        return possibleMatches.size() == 1 ? possibleMatches.stream().findAny().get() : null;
    }

    private static void callAssumedMethodForIncreasingAgeOfDog(Dog dog, int years) {
        if (ASSUMED_METHOD_FOR_INCREASING_AGE_OF_DOG == null) fail("""
                Ingen metod för att öka hundens ålder har kunnat identifieras av testen.
                Detta kan bero på att metoden inte finns ännu, på att dess gränssnitt är
                felaktigt, eller på att det finns flera metoder som matchar det förväntade
                gränssnittet. Det sista är inte möjligt om instruktionerna för vilka
                publika metoder som ska finnas följts, så det är troligt att det är någon
                av de två första anledningarna.
                """);

        if (ASSUMED_METHOD_FOR_INCREASING_AGE_OF_DOG.getParameterCount() == 0) {
            try {
                for (int i = 0; i < years; i++) {
                    ASSUMED_METHOD_FOR_INCREASING_AGE_OF_DOG.invoke(dog);
                }
            } catch (Exception e) {
                fail("""
                        Fel vid anrop på den (antagna) metoden för att öka en hunds ålder. Mer information om vad som gick fel ges i "Caused by"-delen av felmeddelandet.
                        Metoden som anropades var: \t%s
                        """.formatted(ASSUMED_METHOD_FOR_INCREASING_AGE_OF_DOG), e);
            }
        } else {
            try {
                ASSUMED_METHOD_FOR_INCREASING_AGE_OF_DOG.invoke(dog, years);
            } catch (Exception e) {
                fail("""
                        Fel vid anrop på den (antagna) metoden för att öka en hunds ålder. Mer information om vad som gick fel ges i "Caused by"-delen av felmeddelandet.
                        Metoden som anropades var: \t%s
                        Argumentet till metoden var: \t%s
                        """.formatted(ASSUMED_METHOD_FOR_INCREASING_AGE_OF_DOG, years), e);
            }
        }
    }

    @Test
    @Order(10)
    @DisplayName("Implementerad enligt instruktionerna")
    public void validateImplementation() {
        new DogImplementationValidator().execute();
    }

    /*
     * Test för funktionaliten i hundklassen enligt HR1.2
     */

    @Test
    @Order(20)
    @DisplayName("Ett enda namn")
    public void singleName() {
        Dog dog;

        dog = new Dog("Fido", DEFAULT_BREED, DEFAULT_AGE, DEFAULT_WEIGHT);
        assertNameIsNormalized(dog, "Fido", "FIDO");

        dog = new Dog("karo", DEFAULT_BREED, DEFAULT_AGE, DEFAULT_WEIGHT);
        assertNameIsNormalized(dog, "Karo", "KARO");

        dog = new Dog("LaSSiE", DEFAULT_BREED, DEFAULT_AGE, DEFAULT_WEIGHT);
        assertNameIsNormalized(dog, "Lassie", "LASSIE");
    }

    @Test
    @Order(30)
    @DisplayName("Namn separerade av mellanslag")
    public void fullNames() {
        Dog dog;

        dog = new Dog("Kennelnamn Hundnamn", DEFAULT_BREED, DEFAULT_AGE, DEFAULT_WEIGHT);
        assertNameIsNormalized(dog, "Kennelnamn hundnamn", "Kennelnamn Hundnamn", "KENNELNAMN HUNDNAMN");

        dog = new Dog("fluffy destroyer of worlds", DEFAULT_BREED, DEFAULT_AGE, DEFAULT_WEIGHT);
        assertNameIsNormalized(dog, "Fluffy destroyer of worlds", "Fluffy Destroyer Of Worlds", "FLUFFY DESTROYER OF WORLDS");

    }

    @Test
    @Order(40)
    @DisplayName("Dubbelnamn")
    public void doubleNames() {
        Dog dog;

        dog = new Dog("Barks-A-Lot", DEFAULT_BREED, DEFAULT_AGE, DEFAULT_WEIGHT);
        assertNameIsNormalized(dog, "Barks-a-lot", "Barks-A-Lot", "BARKS-A-LOT");
    }

    /**
     * Om specialtecknen i detta test ser konstiga ut beror detta nästan säkert på
     * att du inte sparat filen i UFT-8-format. Hur man gör detta varierar från
     * editor till editor, och från webbläsare till webbläsare. En snabb googling
     * bör ge dig svaret på hur man gör, annars rekommenderas den fysiska
     * handledningen. Handledningsforumet är inte ett lämpligt ställe för denna typ
     * av frågor där man behöver se precis vad du gör.
     */
    @Test
    @Order(50)
    @DisplayName("Namn med andra bokstäver än A-Z")
    public void unicodeCharsInName() {
        Dog dog;

        // Kom inte på några vanliga hundnamn med tex ÅÄÖ eller accenter.
        // Förslag mottages tacksamt.

        dog = new Dog("Å#42", DEFAULT_BREED, DEFAULT_AGE, DEFAULT_WEIGHT);
        assertNameIsNormalized(dog, "Å#42");
    }

    @Test
    @Order(60)
    @DisplayName("Enbokstavsnamn")
    public void singleLetterName() {
        Dog dog;

        dog = new Dog("a", DEFAULT_BREED, DEFAULT_AGE, DEFAULT_WEIGHT);
        assertNameIsNormalized(dog, "A");
        dog = new Dog("Z", DEFAULT_BREED, DEFAULT_AGE, DEFAULT_WEIGHT);
        assertNameIsNormalized(dog, "Z");
    }

    @Test
    @Order(70)
    @DisplayName("Rasen består av ett ord")
    public void singleWordBreed() {
        Dog dog;

        dog = new Dog(DEFAULT_NAME, "Tax", DEFAULT_AGE, DEFAULT_WEIGHT);
        assertBreedIsNormalized(dog, "Tax", "TAX");

        dog = new Dog(DEFAULT_NAME, "pudel", DEFAULT_AGE, DEFAULT_WEIGHT);
        assertBreedIsNormalized(dog, "Pudel", "PUDEL");

        dog = new Dog(DEFAULT_NAME, "PuLi", DEFAULT_AGE, DEFAULT_WEIGHT);
        assertBreedIsNormalized(dog, "Puli", "PULI");
    }

    @Test
    @Order(80)
    @DisplayName("Rasen består av flera ord")
    public void multiWordBreed() {
        Dog dog;

        dog = new Dog(DEFAULT_NAME, "Golden retriever", DEFAULT_AGE, DEFAULT_WEIGHT);
        assertBreedIsNormalized(dog, "Golden retriever", "Golden Retriever", "GOLDEN RETRIEVER");

        dog = new Dog(DEFAULT_NAME, "Shih tzu", DEFAULT_AGE, DEFAULT_WEIGHT);
        assertBreedIsNormalized(dog, "Shih tzu", "Shih Tzu", "SHIH TZU");

    }

    @Test
    @Order(90)
    @DisplayName("Rasen innehåller bindestreck")
    public void doubleNameBreed() {
        Dog dog;

        // Källa: https://www.skk.se/sv/hundraser/?group=6
        dog = new Dog(DEFAULT_NAME, "Anglo-russkaja gontjaja", DEFAULT_AGE, DEFAULT_WEIGHT);
        assertBreedIsNormalized(dog, "Anglo-russkaja gontjaja", "Anglo-russkaja Gontjaja", "Anglo-Russkaja Gontjaja", "ANGLO-RUSSKAJA GONTJAJA");
    }

    /**
     * Om specialtecknen i detta test ser konstiga ut beror detta nästan säkert på
     * att du inte sparat filen i UFT-8-format. Hur man gör detta varierar från
     * editor till editor, och från webbläsare till webbläsare. En snabb googling
     * bör ge dig svaret på hur man gör, annars rekommenderas den fysiska
     * handledningen. Handledningsforumet är inte ett lämpligt ställe för denna typ
     * av frågor där man behöver se precis vad du gör.
     */
    @Test
    @Order(100)
    @DisplayName("Namn med andra bokstäver och tecken än A-Z")
    public void unicodeCharsInBreed() {
        Dog dog;

        // Källa: https://www.skk.se/sv/hundraser/?group=2
        dog = new Dog(DEFAULT_NAME, "Dvärgschnauzer, peppar & salt", DEFAULT_AGE, DEFAULT_WEIGHT);
        assertBreedIsNormalized(dog, "Dvärgschnauzer, peppar & salt", "Dvärgschnauzer, Peppar & Salt", "DVÄRGSCHNAUZER, PEPPAR & SALT");

        // Källa: https://www.skk.se/sv/hundraser/?group=7
        dog = new Dog(DEFAULT_NAME, "Slovenský hrubosrsty stavac (ohar)", DEFAULT_AGE, DEFAULT_WEIGHT);
        assertBreedIsNormalized(dog, "Slovenský hrubosrsty stavac (ohar)", "Slovenský Hrubosrsty Stavac (ohar)", "Slovenský Hrubosrsty Stavac (Ohar)", "SLOVENSKÝ HRUBOSRSTY STAVAC (OHAR)");
    }

    @Test
    @Order(110)
    @DisplayName("Enbokstavsras")
    public void singleLetterBreed() {
        Dog dog;

        dog = new Dog(DEFAULT_NAME, "a", DEFAULT_AGE, DEFAULT_WEIGHT);
        assertBreedIsNormalized(dog, "A");
        dog = new Dog(DEFAULT_NAME, "Z", DEFAULT_AGE, DEFAULT_WEIGHT);
        assertBreedIsNormalized(dog, "Z");
    }

    private void assertNameIsNormalized(Dog dog, String... normalizedNameFormats) {
        assertStringIsNormalized("Namnet", dog.getName(), normalizedNameFormats);
    }

    private void assertBreedIsNormalized(Dog dog, String... normalizedBreedFormats) {
        assertStringIsNormalized("Rasen", dog.getBreed(), normalizedBreedFormats);
    }

    private void assertStringIsNormalized(String id, String str, String... normalizedFormats) {
        for (String name : normalizedFormats) {
            if (name.equals(str)) return;
        }
        fail("%s '%s' är inte normaliserat".formatted(id, str));
    }

    @Test
    @Order(120)
    @DisplayName("Åldern sätts av konstruktorn")
    public void ageSetByCtr() {
        Dog dog;

        dog = new Dog(DEFAULT_NAME, DEFAULT_BREED, 1, DEFAULT_WEIGHT);
        assertEquals(1, dog.getAge());

        dog = new Dog(DEFAULT_NAME, DEFAULT_BREED, 10, DEFAULT_WEIGHT);
        assertEquals(10, dog.getAge());
    }

    @Test
    @Order(130)
    @DisplayName("Åldern kan uppdateras")
    public void ableToIncreaseAge() {
        Dog dog;

        dog = new Dog(DEFAULT_NAME, DEFAULT_BREED, DEFAULT_AGE, DEFAULT_WEIGHT);
        callAssumedMethodForIncreasingAgeOfDog(dog, 1);
        assertEquals(DEFAULT_AGE + 1, dog.getAge());

        dog = new Dog(DEFAULT_NAME, DEFAULT_BREED, DEFAULT_AGE, DEFAULT_WEIGHT);
        callAssumedMethodForIncreasingAgeOfDog(dog, 3);
        assertEquals(DEFAULT_AGE + 3, dog.getAge());
    }

    @Test
    @Order(140)
    @DisplayName("Åldern kan inte sänkas")
    public void unableToDecreaseAge() {
        Dog dog;

        dog = new Dog(DEFAULT_NAME, DEFAULT_BREED, DEFAULT_AGE, DEFAULT_WEIGHT);
        callAssumedMethodForIncreasingAgeOfDog(dog, -1);
        assertEquals(DEFAULT_AGE, dog.getAge());
    }

    @Test
    @Order(150)
    @DisplayName("Åldern kan inte sänkas på grund av överslag")
    public void unableToDecreaseAgeThroughOverflow() {
        Dog dog;

        dog = new Dog(DEFAULT_NAME, DEFAULT_BREED, Integer.MAX_VALUE, DEFAULT_WEIGHT);
        callAssumedMethodForIncreasingAgeOfDog(dog, 1);
        assertEquals(Integer.MAX_VALUE, dog.getAge());
    }

    @Test
    @Order(160)
    @DisplayName("Vikten sätts av konstruktorn")
    public void weightSetByCtr() {
        Dog dog;

        dog = new Dog(DEFAULT_NAME, DEFAULT_BREED, DEFAULT_AGE, 1);
        assertEquals(1, dog.getWeight());

        dog = new Dog(DEFAULT_NAME, DEFAULT_BREED, DEFAULT_AGE, 10);
        assertEquals(10, dog.getWeight());
    }

    @Test
    @Order(170)
    @DisplayName("Svanslängden räknas ut korrekt för icke taxar")
    public void taillengthCorrectForNonDachshunds() {
        Dog dog;

        dog = new Dog(DEFAULT_NAME, DEFAULT_BREED, 4, 5);
        assertEquals(2.0, dog.getTailLength(), 0.01, "(±0.01)");

        dog = new Dog(DEFAULT_NAME, DEFAULT_BREED, DEFAULT_AGE, DEFAULT_WEIGHT);
        assertEquals(DEFAULT_TAIL_LENGTH, dog.getTailLength(), 0.01, "(±0.01)");

    }

    @Test
    @Order(180)
    @DisplayName("Svanslängden räknas ut korrekt för svenska taxar")
    public void taillengthCorrectForSwedishDachshunds() {
        Dog dog;

        dog = new Dog(DEFAULT_NAME, "Tax", DEFAULT_AGE, DEFAULT_WEIGHT);
        assertEquals(3.7, dog.getTailLength(), 0.01, "(±0.01)");

        dog = new Dog(DEFAULT_NAME, "tax", 12, 14);
        assertEquals(3.7, dog.getTailLength(), 0.01, "(±0.01)");

        dog = new Dog(DEFAULT_NAME, "tAx", 12, 14);
        assertEquals(3.7, dog.getTailLength(), 0.01, "(±0.01)");
    }

    @Test
    @Order(190)
    @DisplayName("Svanslängden räknas ut korrekt för engelska taxar")
    public void taillengthCorrectForEnglishDachshunds() {
        Dog dog;

        dog = new Dog(DEFAULT_NAME, "Dachshund", DEFAULT_AGE, DEFAULT_WEIGHT);
        assertEquals(3.7, dog.getTailLength(), 0.01, "(±0.01)");

        dog = new Dog(DEFAULT_NAME, "dachshund", 12, 14);
        assertEquals(3.7, dog.getTailLength(), 0.01, "(±0.01)");

        dog = new Dog(DEFAULT_NAME, "DachsHUND", 5, 1);
        assertEquals(3.7, dog.getTailLength(), 0.01, "(±0.01)");
    }

    @Test
    @Order(200)
    @DisplayName("Svanslängden korrekt för icke taxar efter ändring av åldern")
    public void taillengthCorrectForNonDachshundsAfterIncreasingAge() {
        Dog dog;

        dog = new Dog(DEFAULT_NAME, DEFAULT_BREED, 3, 5);
        callAssumedMethodForIncreasingAgeOfDog(dog, 1);
        assertEquals(2.0, dog.getTailLength(), 0.01, "(±0.01)");

        dog = new Dog(DEFAULT_NAME, DEFAULT_BREED, 12, 5);
        callAssumedMethodForIncreasingAgeOfDog(dog, 10);
        assertEquals(11.0, dog.getTailLength(), 0.01, "(±0.01)");
    }

    @Test
    @Order(210)
    @DisplayName("Svanslängden korrekt för taxar efter ändring av åldern")
    public void taillengthCorrectForDachshundsAfterIncreasingAge() {
        Dog dog;

        dog = new Dog(DEFAULT_NAME, "Tax", 3, 5);
        callAssumedMethodForIncreasingAgeOfDog(dog, 1);
        assertEquals(3.7, dog.getTailLength(), 0.01, "(±0.01)");

        dog = new Dog(DEFAULT_NAME, "Dachshund", 12, 5);
        callAssumedMethodForIncreasingAgeOfDog(dog, 10);
        assertEquals(3.7, dog.getTailLength(), 0.01, "(±0.01)");
    }

    /**
     * Detta test skulle (antagligen) vara onödigt i ett riktigt system eftersom det
     * bara testar samma saker som testerna ovan. Testet är baserat på slumpat data,
     * och syftet med det är bara att ge en något ökad chans att fånga saker som
     * eventuellt inte tänkts på när de övriga testen designades.
     */
    @ParameterizedTest(name = "{index} {1} har alla attribut korrekt satta")
    @Order(220)
    @MethodSource("randomDogs")
    public void randomDogHasCorrectAttribute(Dog dog, String expectedName, String expectedBreed, int expectedAge, int expectedWeight, double expectedTailLength) {
        // Jämförelsen av namn och ras görs med små bokstäver. Detta är ett svagare test
        // än i de specifika testen för dessa attribut, men det mest praktiska när vi
        // inte vet vilken version av normaliserade namn som implementeras.
        assertAll(() -> assertEquals(expectedName.toLowerCase(), dog.getName().toLowerCase(), "Namnet är fel"), () -> assertEquals(expectedBreed.toLowerCase(), dog.getBreed().toLowerCase(), "Rasen är fel"), () -> assertEquals(expectedAge, dog.getAge(), "Åldern är fel"), () -> assertEquals(expectedWeight, dog.getWeight(), "Vikten är fel"), () -> assertEquals(expectedTailLength, dog.getTailLength(), 0.01, "Svanslängden är fel (±0.01)"));
    }

    private static Stream<Arguments> randomDogs() {
        return Stream.of(Arguments.of(new Dog("Snobben", "Pudel", 14, 18), "Snobben", "Pudel", 14, 18, 25.2), Arguments.of(new Dog("Devil", "Dachshund", 15, 11), "Devil", "Dachshund", 15, 11, 3.7), Arguments.of(new Dog("Sigge", "Golden retriever", 1, 12), "Sigge", "Golden retriever", 1, 12, 1.2), Arguments.of(new Dog("Wilma", "Beagle", 19, 13), "Wilma", "Beagle", 19, 13, 24.7), Arguments.of(new Dog("Doris", "Dachshund", 14, 5), "Doris", "Dachshund", 14, 5, 3.7), Arguments.of(new Dog("Ronja", "Shih tzu", 10, 6), "Ronja", "Shih tzu", 10, 6, 6.0), Arguments.of(new Dog("Rex", "Chihuahua", 11, 14), "Rex", "Chihuahua", 11, 14, 15.4), Arguments.of(new Dog("Bamse", "Tax", 10, 8), "Bamse", "Tax", 10, 8, 3.7), Arguments.of(new Dog("Ludde", "Border collie", 13, 12), "Ludde", "Border collie", 13, 12, 15.6), Arguments.of(new Dog("Lassie", "Yorkshireterrier", 1, 6), "Lassie", "Yorkshireterrier", 1, 6, 0.6));
    }

    @Test
    @Order(230)
    @DisplayName("Namnet finns med i strängrepresentationen")
    public void nameInStringRepresentation() {
        Dog dog;

        dog = new Dog("Milou", "Foxterrier", 9, 3);
        assertStringContainsAnyOf(dog.toString(), "Milou", "MILOU");
    }

    @Test
    @Order(240)
    @DisplayName("Rasen finns med i strängrepresentationen")
    public void breedInStringRepresentation() {
        Dog dog;

        dog = new Dog("Milou", "Foxterrier", 9, 3);
        assertStringContainsAnyOf(dog.toString(), "Foxterrier", "FOXTERRIER");
    }

    @Test
    @Order(250)
    @DisplayName("Ålder finns med i strängrepresentationen")
    public void ageInStringRepresentation() {
        Dog dog;

        dog = new Dog("Milou", "Foxterrier", 9, 3);
        assertStringContainsAnyOf(dog.toString(), "9");
    }

    @Test
    @Order(260)
    @DisplayName("Vikt finns med i strängrepresentationen")
    public void weightInStringRepresentation() {
        Dog dog;

        dog = new Dog("Milou", "Foxterrier", 9, 3);
        assertStringContainsAnyOf(dog.toString(), "3");
    }

    @Test
    @Order(270)
    @DisplayName("Svanslängden finns med i strängrepresentationen")
    public void taillengthInStringRepresentation() {
        Dog dog;

        dog = new Dog("Milou", "Foxterrier", 9, 3);
        assertStringContainsAnyOf(dog.toString(), "2,7", "2.7");
    }

    private void assertStringContainsAnyOf(String str, String... alternatives) {
        for (String alt : alternatives) {
            if (str.contains(alt)) return;
        }

        fail("Kunde inte hitta någon av strängarna %s i \"%s\"".formatted(Arrays.toString(alternatives), str));
    }

    /*
     * Testen nedanför denna kommentar kontrollerar olika stilfrågor. Dessa test
     * (liksom de ytterligare stiltest som görs av checkstyle vid inlämning i
     * ilearn) är viktiga, men de kan aldrig kontrollera allt.
     *
     * Det är alltid du själv som ansvarar för kvaliten hos det du lämnar in, så var
     * noga med saker som namngivning, skyddsnivåer, etc.
     */

    @Test
    @Order(280)
    @DisplayName("Är allt data i Dog privat?")
    public void onlyPrivateFieldsInDog() {
        Class<?> c = Dog.class;
        for (var f : c.getDeclaredFields()) {
            assertTrue(isPrivate(f), "Fältet %s i %s är inte privat".formatted(f.getName(), c.getSimpleName()));
        }
    }

    @Test
    @Order(290)
    @DisplayName("Är alla metoder i Dog publika eller privata?")
    public void onlyPublicOrPrivateMethodsInDog() {
        Class<?> c = Dog.class;
        for (var m : c.getDeclaredMethods()) {
            assertTrue(isPublic(m) || isPrivate(m), "Metoden %s i %s är inte publik eller privat".formatted(m.getName(), c.getSimpleName()));
        }
    }

    @Test
    @Order(300)
    @DisplayName("Är alla publika metoder i Dog tillåtna enligt uppgiften?")
    public void onlyAllowedPublicMethodsInDog() {
        var matches = unexpectedPublicMethodsInDog();
        assertTrue(matches.isEmpty(), "Det finns publika metoder i Dog som testen inte förväntar sig:\n%s".formatted(matches));
    }

    /*
     * Metoderna nedanför denna kommentar är hjälpmetoder för de olika testfallen.
     * Det är starkt rekommenderat att du *INTE* kommenterar ut dem när du
     * kommenterar ut testfall eftersom det kan vara svårt att hitta dem igen när de
     * behövs.
     */

    private static Collection<Method> unexpectedPublicMethodsInDog() {
        var matches = allPublicMethodsInDog();
        matches.removeAll(expectedPublicMethodsInDog());
        return matches;
    }

    private static Collection<Method> allPublicMethodsInDog() {
        return new HashSet<>(Stream.of(Dog.class.getDeclaredMethods()).filter(m -> isPublic(m)).toList());
    }

    private static Collection<Method> completelySpecifiedMethodsInDog() {
        var matches = new HashSet<Method>();

        /*
         * Listan på metoder nedan är automatiskt genererad från en
         * referensimplementation av hela hundregistret.
         *
         * Detta betyder bland annat att listan *INTE* är sorterad, utan att metoderna
         * kommer huller om buller, och att den kan innehålla metoder som kommer att
         * tillkomma i senare uppgifter, eller som är frivilliga.
         */

        matches.addAll(getMatchingPublicMethodsInDog(true, String.class, "getName", Void.class));
        matches.addAll(getMatchingPublicMethodsInDog(true, String.class, "toString", Void.class));
        matches.addAll(getMatchingPublicMethodsInDog(true, asArray(Owner.class, Optional.class), "getOwner", asArray(Void.class)));
        matches.addAll(getMatchingPublicMethodsInDog(true, asArray(boolean.class, void.class), "setOwner", asArray(Owner.class)));
        matches.addAll(getMatchingPublicMethodsInDog(true, int.class, "getWeight", Void.class));
        matches.addAll(getMatchingPublicMethodsInDog(true, double.class, "getTailLength", Void.class));
        matches.addAll(getMatchingPublicMethodsInDog(true, String.class, "getBreed", Void.class));
        matches.addAll(getMatchingPublicMethodsInDog(true, int.class, "getAge", Void.class));

        return matches;
    }

    private static Collection<Method> expectedPublicMethodsInDog() {
        var matches = completelySpecifiedMethodsInDog();

        for (var field : MethodHandles.lookup().lookupClass().getDeclaredFields()) {
            try {
                field.setAccessible(true);
                if (isStatic(field) && field.getType() == Method.class && field.get(null) != null) {
                    matches.add((Method) field.get(null));
                }
            } catch (IllegalArgumentException | IllegalAccessException e) {
                fail(e);
            }
        }

        return matches;
    }

    private static Collection<Method> getMatchingPublicMethodsInDog(boolean instanceMethod, Class<?> allowedReturnType, String name, Class<?> allowedParamType) {
        return getMatchingPublicMethodsInDog(instanceMethod, asArray(allowedReturnType), name, asArray(allowedParamType));
    }

    /**
     * @param name              null matches everything
     * @param allowedParamTypes Void matches no param
     */
    private static Collection<Method> getMatchingPublicMethodsInDog(boolean instanceMethod, Class<?>[] allowedReturnTypes, String name, Class<?>[] allowedParamTypes) {
        var matches = new HashSet<Method>();
        for (var method : allPublicMethodsInDog()) {
            var instanceMethodMatch = instanceMethod == !isStatic(method);
            var returnTypeMatch = matchingType(method.getReturnType(), allowedReturnTypes);
            var nameMatch = name == null || name.equals(method.getName());
            var paramTypeMatch = (method.getParameterCount() == 0 && matchingType(Void.class, allowedParamTypes)) || (method.getParameterCount() == 1 && matchingType(method.getParameters()[0].getType(), allowedParamTypes));

            if (instanceMethodMatch && returnTypeMatch && nameMatch && paramTypeMatch) {
                matches.add(method);
            }
        }
        return matches;
    }

    private static boolean isPublic(Member m) {
        return Modifier.isPublic(m.getModifiers());
    }

    private static boolean isPrivate(Member m) {
        return Modifier.isPrivate(m.getModifiers());
    }

    private static boolean isStatic(Member m) {
        return Modifier.isStatic(m.getModifiers());
    }

    private static Class<?>[] asArray(Class<?>... types) {
        return types;
    }

    private static boolean matchingType(Class<?> type, Class<?>[] allowedTypes) {
        for (var allowed : allowedTypes) {
            if (type == allowed) return true;
        }

        return false;
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
    public record MethodHeader(boolean isStatic, String returnType, java.util.regex.Pattern namePattern, String[] parameters) {

        public MethodHeader( boolean isStatic, String returnType, String namePattern, String...parameters){
            this(isStatic, returnType, java.util.regex.Pattern.compile(namePattern), parameters);
        }

        public boolean matches (java.lang.reflect.Method m){
            if (isStatic != java.lang.reflect.Modifier.isStatic(m.getModifiers())) return false;

            if (!returnType.equals(m.getReturnType().getSimpleName())) return false;

            if (!namePattern.matcher(m.getName()).matches()) return false;

            var actualParams = java.util.stream.Stream.of(m.getParameterTypes()).map(p -> p.getSimpleName()).toList().toArray(new String[]{});
            if (!java.util.Arrays.equals(parameters, actualParams)) return false;

            return true;
        }

        @Override public String toString () {
            return "%s%s %s%s".formatted(isStatic ? "static " : "", returnType, namePattern, java.util.Arrays.toString(parameters));
        }

    }

    // @formatter:off
    /**
     * Denna klass är automatiskt genererad. Ändringar i den kommer att skrivas
     * över vid nästa uppdatering.
     */
    public class DogImplementationValidator {

        private final Class<?> cut = Dog.class;
        private static final java.util.Collection<MethodHeader> EXPECTED_PUBLIC_METHODS = new java.util.ArrayList<>();

        /**
         * Denna lista är automatiskt genererad från en referens-implementation.
         * Ordningen på metoderna är fast, men har inget med den förväntade ordningen
         * i klassen att göra.
         */
        static {
            EXPECTED_PUBLIC_METHODS.add(new MethodHeader(false, "Optional", "getOwner"));
            EXPECTED_PUBLIC_METHODS.add(new MethodHeader(false, "Owner", "getOwner"));
            EXPECTED_PUBLIC_METHODS.add(new MethodHeader(false, "String", "getBreed"));
            EXPECTED_PUBLIC_METHODS.add(new MethodHeader(false, "String", "getName"));
            EXPECTED_PUBLIC_METHODS.add(new MethodHeader(false, "String", "toString"));
            EXPECTED_PUBLIC_METHODS.add(new MethodHeader(false, "boolean", "setOwner", "Owner"));
            EXPECTED_PUBLIC_METHODS.add(new MethodHeader(false, "double", "getTailLength"));
            EXPECTED_PUBLIC_METHODS.add(new MethodHeader(false, "int", ".*Age.*"));
            EXPECTED_PUBLIC_METHODS.add(new MethodHeader(false, "int", ".*Age.*", "int"));
            EXPECTED_PUBLIC_METHODS.add(new MethodHeader(false, "int", ".*Year.*"));
            EXPECTED_PUBLIC_METHODS.add(new MethodHeader(false, "int", ".*Year.*", "int"));
            EXPECTED_PUBLIC_METHODS.add(new MethodHeader(false, "int", "getAge"));
            EXPECTED_PUBLIC_METHODS.add(new MethodHeader(false, "int", "getWeight"));
            EXPECTED_PUBLIC_METHODS.add(new MethodHeader(false, "void", ".*Age.*"));
            EXPECTED_PUBLIC_METHODS.add(new MethodHeader(false, "void", ".*Age.*", "int"));
            EXPECTED_PUBLIC_METHODS.add(new MethodHeader(false, "void", ".*Year.*"));
            EXPECTED_PUBLIC_METHODS.add(new MethodHeader(false, "void", ".*Year.*", "int"));
            EXPECTED_PUBLIC_METHODS.add(new MethodHeader(false, "void", "setOwner", "Owner"));
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