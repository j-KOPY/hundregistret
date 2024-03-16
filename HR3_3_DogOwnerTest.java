import static org.junit.jupiter.api.Assertions.*;

import java.util.*;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;

/**
 * Testfall för att sätta/ta bort ägaren för en hund i uppgift HR3.3. Testfallen
 * i denna klass förutsätter alla att det är hunden som kan initiera en
 * ägarförändring, dvs att <code>setOwner</code> alltid är den första metoden
 * som anropas. <p> Om du vill implementera det bättre alternativet där även
 * ägaren, via <code>setOwner</code> och <code>removeOwner</code>, kan initiera
 * en ägarförändring så finns det ytterligare tester för detta i
 * <code>{@link HR3_3_ExtraTest}</code>. <p> Beskrivningen av testfallens
 * uppgift, styrka och svagheter från <code>{@link HR1_1_OwnerTest}</code>
 * gäller (naturligvis) också för testfallen i denna klass. Var speciellt
 * uppmärksam på att testfallen kan uppdateras när som helst, inklusive
 * <em>efter</em> deadline.
 *
 * @author Henrik Bergström
 * @version 2024-03-03 10:51
 * @see HR1_1_OwnerTest
 * @see HR3_3_ExtraTest
 */
@TestMethodOrder(OrderAnnotation.class)
@DisplayName("HR3.3: Testfall för att hundens ägare sätts korrekt")
public class HR3_3_DogOwnerTest {

    public static final String VERSION = "2024-03-03 10:51";

    private static final String DEFAULT_DOG_NAME = "Dogname";
    private static final String DEFAULT_OWNER_NAME = "Ownername";

    /**
     * Hjälpmetod för att skapa nya hundar med defaultnamnet.
     */
    private Dog newDog() {
        return newDog(DEFAULT_DOG_NAME);
    }

    /**
     * Hjälpmetod för att skapa nya hundar utan att behöva ange mer än namnet.
     */
    private Dog newDog(String name) {
        return new Dog(name, "Breed", 1, 1);
    }

    /**
     * Hjälpmetod för att skapa nya ägare med defaultnamnet.
     */
    private Owner newOwner() {
        return newOwner(DEFAULT_OWNER_NAME);
    }

    /**
     * Hjälpmetod för att skapa nya ägare. Metoden är egentligen onödig eftersom
     * ägaren bara tar namnet som argument till konstruktorn, men dess användning
     * gör att ägare skapas på samma sätt som hundar i testen.
     */
    private Owner newOwner(String name) {
        return new Owner(name);
    }

    private void assertDogsOwned(Owner owner, Dog... expectedDogs) {
        var owned = owner.getDogs();
        var expected = Arrays.asList(expectedDogs);

        var missing = new ArrayList<>(expected);
        missing.removeAll(owned);

        var extra = new ArrayList<>(owned);
        extra.removeAll(expected);

        assertAll("""
				Fel hundar ägs av %s.
				Förväntade: %s
				Ägda: %s

				""".formatted(owner.getName(), expected, owned),
                () -> assertTrue(missing.isEmpty(), missing + " saknas"),
                () -> assertTrue(extra.isEmpty(), extra + " borde inte finnas med"));
    }

    @Test
    @Order(10)
    @DisplayName("Hundklassen implementerad enligt instruktionerna")
    public void validateDogImplementation() {
        new DogImplementationValidator().execute();
    }

    @Test
    @Order(20)
    @DisplayName("Ägarklassen implementerad enligt instruktionerna")
    public void validateOwnerImplementation() {
        new OwnerImplementationValidator().execute();
    }

    ////////////////////////////////////////////
    //
    // Tester för att hundens ägare sätts
    //
    ////////////////////////////////////////////

    @Test
    @Order(30)
    @DisplayName("En ny hund har ingen ägare")
    public void aNewlyCreatedDogHaveNoOwner() {
        var dog = newDog();
        assertNull(dog.getOwner());
    }

    @Test
    @Order(40)
    @DisplayName("Ägaren sparas hos hunden")
    public void settingTheOwnerUpdatesOwnershipOfDog() {
        var dog = newDog();
        var owner = newOwner();

        dog.setOwner(owner);

        assertEquals(owner, dog.getOwner());
    }

    @Test
    @Order(50)
    @DisplayName("Ägaren kan tas bort från hunden")
    public void settingTheOwnerToNullClearsTheOwner() {
        var dog = newDog();
        var owner = newOwner();

        dog.setOwner(owner);
        dog.setOwner(null);

        assertNull(dog.getOwner());
    }

    @Test
    @Order(60)
    @DisplayName("Att försöka ta bort ägaren från en hund utan ägare ska inte gå")
    public void settingTheOwnerToNullForDogWithoutOwnerDoesNothing() {
        var dog = newDog();
        // Både sant och falskt returvärde accepteras här eftersom det inte är specat.
        dog.setOwner(null);
        assertNull(dog.getOwner());
    }

    @Test
    @Order(70)
    @DisplayName("Att sätta ägaren för en redan ägd hund gör inget")
    public void settingTheOwnerOfAnAlreadyOwnedDogDoesNothing() {
        var dog = newDog();
        var owner = newOwner();
        var otherOwner = newOwner("Other owner");

        dog.setOwner(owner);
        dog.setOwner(otherOwner);

        assertEquals(owner, dog.getOwner());
    }

    @Test
    @Order(80)
    @DisplayName("Sätta och ta bort ägaren flera gånger")
    public void settingAndRemovingOwnerMultipleTimes() {
        var dog = newDog();
        var firstOwner = newOwner("First owner");
        var secondOwner = newOwner("Second owner");

        dog.setOwner(firstOwner);
        assertEquals(firstOwner, dog.getOwner());

        dog.setOwner(null);
        assertNull(dog.getOwner());

        dog.setOwner(secondOwner);
        assertEquals(secondOwner, dog.getOwner());

        dog.setOwner(null);
        assertNull(dog.getOwner());
    }

    ////////////////////////////////////////////
    //
    // Tester för att ägarens samling med hundar
    // uppdateras
    //
    ////////////////////////////////////////////

    @Test
    @Order(90)
    @DisplayName("En ny ägare äger inga hundar")
    public void aNewlyCreatedOwnerHaveNoDogs() {
        var owner = newOwner();
        assertTrue(owner.getDogs().isEmpty());
    }

    @Test
    @Order(100)
    @DisplayName("Hunden sparas hos ägaren")
    public void settingTheOwnerAddsTheDogToTheOwner() {
        var dog = newDog();
        var owner = newOwner();

        var rv = dog.setOwner(owner);

        assertDogsOwned(owner, dog);
        assertTrue(rv, "Fel returvärde från Dog.setOwner");
    }

    @Test
    @Order(110)
    @DisplayName("Hundarna sparas hos ägaren")
    public void settingTheOwnerOfMultipleDogsAddsThemToTheOwner() {
        var firstDog = newDog("First dog");
        var secondDog = newDog("Second dog");
        var owner = newOwner();

        firstDog.setOwner(owner);
        secondDog.setOwner(owner);

        assertDogsOwned(owner, firstDog, secondDog);
    }

    @Test
    @Order(120)
    @DisplayName("Hunden tas bort från ägaren")
    public void settingTheOwnerToNullRemovesTheDogFromTheOwner() {
        var dog = newDog();
        var owner = newOwner();

        dog.setOwner(owner);
        dog.setOwner(null);

        assertDogsOwned(owner);
    }

    @Test
    @Order(130)
    @DisplayName("Hundarna tas bort från ägaren i samma ordning de lades till")
    public void settingTheOwnerToNullForMultipleDogsInTheSameOrderTheyWereAddedRemovesThemFromTheOwner() {
        var firstDog = newDog("First dog");
        var secondDog = newDog("Second dog");
        var owner = newOwner();

        firstDog.setOwner(owner);
        secondDog.setOwner(owner);

        firstDog.setOwner(null);
        secondDog.setOwner(null);

        assertDogsOwned(owner);
    }

    @Test
    @Order(140)
    @DisplayName("Hundarna tas bort från ägaren i omvänd ordning från den de lades till")
    public void settingTheOwnerToNullForMultipleDogsInReverseOrderToThatTheyWereAddedRemovesThemFromTheOwner() {
        var firstDog = newDog("First dog");
        var secondDog = newDog("Second dog");
        var owner = newOwner();

        firstDog.setOwner(owner);
        secondDog.setOwner(owner);

        secondDog.setOwner(null);
        firstDog.setOwner(null);

        assertDogsOwned(owner);
    }

    @Test
    @Order(150)
    @DisplayName("Att sätta ägaren för en redan ägd hund påverkar inte den ursprungliga ägaren")
    public void settingTheOwnerOfAnAlreadyOwnedDogDoesNothingToTheOriginalOwner() {
        var dog = newDog();
        var owner = newOwner();
        var otherOwner = newOwner("Other owner");

        var rvFirstOwner = dog.setOwner(owner);
        var rvSecondOwner = dog.setOwner(otherOwner);

        assertDogsOwned(owner, dog);
        assertTrue(rvFirstOwner, "Fel returvärde från Dog.setOwner för den första ägaren");
        assertFalse(rvSecondOwner, "Fel returvärde från Dog.setOwner för den andra ägaren");
    }

    @Test
    @Order(160)
    @DisplayName("Att sätta ägaren för en redan ägd hund påverkar inte den \"nya\" ägaren")
    public void settingTheOwnerOfAnAlreadyOwnedDogDoesNothingToTheNewOwner() {
        var dog = newDog();
        var owner = newOwner();
        var otherOwner = newOwner("Other owner");

        dog.setOwner(owner);
        dog.setOwner(otherOwner);

        assertDogsOwned(otherOwner);
    }

    @Test
    @Order(170)
    @DisplayName("Sätta och ta bort ägaren flera gånger")
    public void settingAndRemovingOwnerMultipleTimesUpdatesTheOwners() {
        // De "vanliga" namnen på hundarna och ägarna gjorde testet mer lättläst
        var fido = newDog("Fido");
        var karo = newDog("Karo");
        var henrik = newOwner("Henrik");
        var olle = newOwner("Olle");

        assertDogsOwned(henrik);
        assertDogsOwned(olle);

        fido.setOwner(henrik);
        assertDogsOwned(henrik, fido);
        assertDogsOwned(olle);

        karo.setOwner(olle);
        assertDogsOwned(henrik, fido);
        assertDogsOwned(olle, karo);

        fido.setOwner(null);
        assertDogsOwned(henrik);
        assertDogsOwned(olle, karo);

        fido.setOwner(olle);
        assertDogsOwned(henrik);
        assertDogsOwned(olle, karo, fido);

        fido.setOwner(null);
        karo.setOwner(null);
        assertDogsOwned(henrik);
        assertDogsOwned(olle);

        fido.setOwner(henrik);
        karo.setOwner(henrik);
        assertDogsOwned(henrik, fido, karo);
        assertDogsOwned(olle);
    }

    ////////////////////////////////////////
    //
    // Övriga test för hundar och ägare
    //
    ////////////////////////////////////////

    @Test
    @Order(180)
    @DisplayName("Ägaren finns med i strängrepresentationen för hunden")
    public void ownerInStringRepresentation() {
        var dog = newDog();
        var owner = newOwner("Olle");

        dog.setOwner(owner);

        String str = dog.toString();
        assertTrue(str.contains("Olle") || str.contains("OLLE"),
                "Strängrepresentationen för hunden ('%s') innehåller inte dennes ägare".formatted(str));
    }

    @Test
    @Order(190)
    @DisplayName("Ägda hundar finns med i strängrepresentationen för ägaren")
    public void dogInStringRepresentation() {
        var fido = newDog("Fido");
        var karo = newDog("Karo");
        var owner = newOwner();

        fido.setOwner(owner);
        karo.setOwner(owner);

        String str = owner.toString();
        assertAll("Strängrepresentationen för ägaren ('%s') innehåller inte dennes hundar".formatted(str),
                () -> assertTrue(str.contains("Fido") || str.contains("FIDO"), "Fido saknas"),
                () -> assertTrue(str.contains("Karo") || str.contains("KARO"), "Karo saknas"));
    }

    @Test
    @Order(200)
    @DisplayName("Förändringar i den avlästa samlingen påverkar inte orginalet")
    public void changesToCollectionReturnedByGetDogsDoesNotAffectOriginal() {
        var dog = newDog();
        var owner = newOwner();

        dog.setOwner(owner);

        try {
            owner.getDogs().remove(dog);
        } catch (UnsupportedOperationException e) {
            // Förväntat möjligt undantag, inget att göra här.
        }

        assertDogsOwned(owner, dog);
    }

    ////////////////////////////////////////
    //
    // Test för remove-metoderna i samlings-
    // klasserna
    //
    ////////////////////////////////////////

    @Test
    @Order(210)
    @DisplayName("Det går inte att ta bort en hund med en ägare från samlingsklassen")
    public void attemptingToRemoveDogWithOwnerFromDogCollection() {
        var dog = newDog();
        var owner = newOwner();
        var collection = new DogCollection();

        dog.setOwner(owner);
        collection.addDog(dog);

        var rv = collection.removeDog(dog);

        assertAll("Fel resultat vid försök att ta bort en hund med en ägare från samlingen",
                () -> assertFalse(rv, "Fel returvärde för remove-metoden"),
                () -> assertTrue(collection.containsDog(dog), "Fel returvärde för contains-metoden"));
    }

    @Test
    @Order(220)
    @DisplayName("Det går inte att ta bort en hund med en ägare från samlingsklassen via namn")
    public void attemptingToRemoveDogWithOwnerFromDogCollectionByName() {
        var dog = newDog();
        var owner = newOwner();
        var collection = new DogCollection();

        dog.setOwner(owner);
        collection.addDog(dog);

        var rv = collection.removeDog(dog.getName());

        assertAll("Fel resultat vid försök att ta bort en hund med en ägare från samlingen",
                () -> assertFalse(rv, "Fel returvärde för remove-metoden"),
                () -> assertTrue(collection.containsDog(dog), "Fel returvärde för contains-metoden"));
    }

    @Test
    @Order(230)
    @DisplayName("Det går inte att ta bort en ägare med hundar från samlingsklassen")
    public void attemptingToRemoveOwnerWithDogsFromOwnerCollection() {
        var dog = newDog();
        var owner = newOwner();
        var collection = new OwnerCollection();

        dog.setOwner(owner);
        collection.addOwner(owner);

        var rv = collection.removeOwner(owner);

        assertAll("Fel resultat vid försök att ta bort en ägare med hund från samlingen",
                () -> assertFalse(rv, "Fel returvärde för remove-metoden"),
                () -> assertTrue(collection.containsOwner(owner), "Fel returvärde för contains-metoden"));
    }

    @Test
    @Order(240)
    @DisplayName("Det går inte att ta bort en ägare med hundar från samlingsklassen via namn")
    public void attemptingToRemoveOwnerWithDogsFromOwnerCollectionByName() {
        var dog = newDog();
        var owner = newOwner();
        var collection = new OwnerCollection();

        dog.setOwner(owner);
        collection.addOwner(owner);

        var rv = collection.removeOwner(owner.getName());

        assertAll("Fel resultat vid försök att ta bort en ägare med hund från samlingen",
                () -> assertFalse(rv, "Fel returvärde för remove-metoden"),
                () -> assertTrue(collection.containsOwner(owner), "Fel returvärde för contains-metoden"));
    }

    @ParameterizedTest(name = "Försök ta bort hunden och ägaren som finns på index {0}")
    @ValueSource(ints = { 0, 1, 2 })
    @Order(250)
    @DisplayName("Det går inte att ta bort en hund med ägare eller en  ägare med hundar från samlingsklassen oavsett position")
    public void attemptingToRemoveDogWithOwnerAndOwnerWithDogsFromOwnerCollection(int index) {
        var dogCollection = new DogCollection();
        var ownerCollection = new OwnerCollection();

        for (int i = 0; i < 3; i++) {
            var dog = new Dog("Dog" + i, "Breed", 1, 2);
            var owner = new Owner("Owner" + i);
            assertTrue(dog.setOwner(owner));

            assertTrue(dogCollection.addDog(dog));
            assertTrue(ownerCollection.addOwner(owner));
        }

        var dog = dogCollection.getDogs().get(index);
        var owner = ownerCollection.getOwners().get(index);

        var rvDCD = dogCollection.removeDog(dog);
        var rvDCN = dogCollection.removeDog(dog.getName());
        var rvOCO = ownerCollection.removeOwner(owner);
        var rvOCN = ownerCollection.removeOwner(owner.getName());

        assertAll(
                "Fel resultat vid försök att ta bort en hund med en ägare eller en ägare med en hund från samlingarna",
                () -> assertFalse(rvDCD, "Fel returvärde för removeDog(Dog)"),
                () -> assertFalse(rvDCN, "Fel returvärde för removeDog(String)"),
                () -> assertFalse(rvOCO, "Fel returvärde för removeOwner(Owner)"),
                () -> assertFalse(rvOCN, "Fel returvärde för removeOwner(Owner"),
                () -> assertTrue(dogCollection.containsDog(dog), "Fel returvärde för contains-metoden i DogCollection"),
                () -> assertTrue(ownerCollection.containsOwner(owner),
                        "Fel returvärde för contains-metoden i OwnerCollection"));
    }

    ////////////////////////////////////////
    //
    // Minimala test för Owner.addDog, bara
    // det man kan förvänta sig om man utgår
    // från att hunden initierar ägarskapet
    //
    ////////////////////////////////////////

    @Test
    @Order(260)
    @DisplayName("Det går inte att lägga till en hund som redan har en annan ägare")
    public void addingDogAlreadyOwnedByOtherOwner() {
        var dog = newDog();
        var owner = newOwner();
        dog.setOwner(owner);

        var otherOwner = newOwner("Otherowner");
        var rv = otherOwner.addDog(dog);

        assertAll( //
                () -> assertFalse(rv, "Fel returvärde från addDog"), //
                () -> assertDogsOwned(owner, dog), //
                () -> assertDogsOwned(otherOwner), //
                () -> assertEquals(owner, dog.getOwner(), "Fel ägare av hunden")//
        );
    }

    @Test
    @Order(270)
    @DisplayName("Det går inte att lägga till en hund två gånger hos en ägare")
    public void addingDogAlreadyOwned() {
        var dog = newDog();
        var owner = newOwner();
        dog.setOwner(owner);

        var rv = owner.addDog(dog);

        assertAll( //
                () -> assertFalse(rv, "Fel returvärde från addDog"), //
                () -> assertDogsOwned(owner, dog), //
                () -> assertEquals(owner, dog.getOwner(), "Fel ägare av hunden"), //
                () -> assertEquals(1, owner.getDogs().size(), "Fel antal hundar ägda") //
        );
    }

    ////////////////////////////////////////
    //
    // Owner.getDogs ska också sortera...
    //
    ////////////////////////////////////////

    @Test
    @Order(280)
    @DisplayName("Ägarens hundar sorteras")
    public void ownersDogsSorted() {
        Dog dogA = newDog("A");
        Dog dogB = newDog("B");
        Dog dogC = newDog("C");
        Dog dogD = newDog("D");

        Owner owner = newOwner();

        dogC.setOwner(owner);
        dogD.setOwner(owner);
        dogA.setOwner(owner);
        dogB.setOwner(owner);

        var expected = Arrays.asList(dogA, dogB, dogC, dogD);
        var actual = owner.getDogs();

        assertEquals(expected, actual, "Ägarens hundar sorteras inte");
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
            EXPECTED_PUBLIC_METHODS.add(new MethodHeader(false, "int", ".*Year.*"));
            EXPECTED_PUBLIC_METHODS.add(new MethodHeader(false, "int", ".*Year.*", "int"));
            EXPECTED_PUBLIC_METHODS.add(new MethodHeader(false, "int", ".*[aA]ge.*"));
            EXPECTED_PUBLIC_METHODS.add(new MethodHeader(false, "int", ".*[aA]ge.*", "int"));
            EXPECTED_PUBLIC_METHODS.add(new MethodHeader(false, "int", "getAge"));
            EXPECTED_PUBLIC_METHODS.add(new MethodHeader(false, "int", "getWeight"));
            EXPECTED_PUBLIC_METHODS.add(new MethodHeader(false, "void", ".*Year.*"));
            EXPECTED_PUBLIC_METHODS.add(new MethodHeader(false, "void", ".*Year.*", "int"));
            EXPECTED_PUBLIC_METHODS.add(new MethodHeader(false, "void", ".*[aA]ge.*"));
            EXPECTED_PUBLIC_METHODS.add(new MethodHeader(false, "void", ".*[aA]ge.*", "int"));
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
