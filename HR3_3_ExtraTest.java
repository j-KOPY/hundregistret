import static org.junit.jupiter.api.Assertions.*;

import java.util.*;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;

/**
 * Testfall för att lägga till och ta bort till en hund från en ägare i uppgift
 * HR3.3. Dessa testfall förutsätter att <code>setOwner</code> i hundklassen
 * fungerar korrekt, och täcker bara de varianter där <code>addDog</code> eller
 * <code>removeDog</code> i ägarklassen anropas direkt för att ändra
 * ägarförhållandet. De täcker alltså de delar av den första fungerande
 * alternativet som presenteras i uppgiften som inte täcktes av testfallen i
 * <code>{@link HR3_3_DogOwnerTest}</code>.
 * <p>
 * Dessa testfall ska <em>bara</em> användas om du implementerar denna variant
 * av uppgiften, inte om du implementerar det andra. De körs därför
 * <em>inte</em> på testservern i ilearn.
 * <p>
 * Beskrivningen av testfallens uppgift, styrka och svagheter från
 * <code>{@link HR1_1_OwnerTest}</code> gäller (naturligvis) också för
 * testfallen i denna klass. Var speciellt uppmärksam på att testfallen kan
 * uppdateras när som helst, inklusive <em>efter</em> deadline.
 *
 * @author Henrik Bergström
 * @version 2024-01-04 13:16
 * @see HR1_1_OwnerTest
 * @see HR3_3_DogOwnerTest
 */
@TestMethodOrder(OrderAnnotation.class)
@DisplayName("HR3.3: Testfall för de extra fall som finns när även ägaren kan initiera en förändring")
public class HR3_3_ExtraTest {

    public static final String VERSION = "2024-01-04 13:16";

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
        assertDogsOwned(false, false, owner, expectedDogs);
    }

    private void assertDogsOwned(boolean expectedReturnValue, boolean actualReturnValue, Owner owner,
                                 Dog... expectedDogs) {
        var owned = owner.getDogs();
        var expected = Arrays.asList(expectedDogs);

        var missing = new ArrayList<>(expected);
        missing.removeAll(owned);

        var extra = new ArrayList<>(owned);
        extra.removeAll(expected);

        assertAll("Fel resultat vid uppdatering av %s".formatted(owner.getName()),
                () -> assertEquals(expectedReturnValue, actualReturnValue, "Fel returvärde"),
                () -> assertTrue(missing.isEmpty(), "Dessa hundar saknas hos ägaren: " + missing),
                () -> assertTrue(extra.isEmpty(), "Dessa hundar borde inte finnas hos ägaren: " + extra));
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

    @Test
    @Order(30)
    @DisplayName("Det går att lägga till en hund till ägaren")
    public void addingDogWithoutOwner() {
        var dog = newDog();
        var owner = newOwner();

        var rv = owner.addDog(dog);

        assertDogsOwned(true, rv, owner, dog);
    }

    @Test
    @Order(40)
    @DisplayName("När en hund läggs till sätts också ägaren hos hunden")
    public void addingDogSetsOwner() {
        var dog = newDog();
        var owner = newOwner();

        owner.addDog(dog);

        assertEquals(owner, dog.getOwner());
    }

    @Test
    @Order(50)
    @DisplayName("Det går att lägga till flera hundar")
    public void addingMultipleDogs() {
        var firstDog = newDog();
        var secondDog = newDog();
        var owner = newOwner();

        owner.addDog(firstDog);
        var rv = owner.addDog(secondDog);

        assertDogsOwned(true, rv, owner, firstDog, secondDog);
    }

    @Test
    @Order(60)
    @DisplayName("Det går inte att lägga till en hund som redan ägs av samma ägare igen")
    public void addingDogAlreadyOwnedBySameOwner() {
        var dog = newDog();
        var owner = newOwner();

        owner.addDog(dog);
        var rv = owner.addDog(dog);

        assertDogsOwned(false, rv, owner, dog);
    }

    @Test
    @Order(70)
    @DisplayName("Det går inte att lägga till en hund som redan har en annan ägare")
    public void addingDogAlreadyOwnedByOtherOwner() {
        var dog = newDog();
        var owner = newOwner();
        var otherOwner = newOwner("Otherowner");

        otherOwner.addDog(dog);
        var rv = owner.addDog(dog);

        assertDogsOwned(false, rv, owner);
    }

    @Test
    @Order(80)
    @DisplayName("Det går att ta bort en hund från ägaren")
    public void removingDogOwnedBySameOwner() {
        var dog = newDog();
        var owner = newOwner();

        owner.addDog(dog);
        var rv = owner.removeDog(dog);

        assertDogsOwned(true, rv, owner);
    }

    @Test
    @Order(90)
    @DisplayName("När en hund tas bort tas också ägaren bort från hunden")
    public void removingDogAlsoRemovesOwner() {
        var dog = newDog();
        var owner = newOwner();

        owner.addDog(dog);
        owner.removeDog(dog);

        assertNull(dog.getOwner());
    }

    @Test
    @Order(100)
    @DisplayName("Det går att ta bort flera hundar från ägaren i samma ordning som de sattes in")
    public void removingMultipleDogsInInsertionOrder() {
        var firstDog = newDog();
        var secondDog = newDog();
        var owner = newOwner();

        owner.addDog(firstDog);
        owner.addDog(secondDog);

        var rv1 = owner.removeDog(firstDog);
        assertDogsOwned(true, rv1, owner, secondDog);

        var rv2 = owner.removeDog(secondDog);
        assertDogsOwned(true, rv2, owner);
    }

    @Test
    @Order(110)
    @DisplayName("Det går att ta bort flera hundar från ägaren i omvänd ordning mot den de sattes in i")
    public void removingMultipleDogsInReverseInsertionOrder() {
        var firstDog = newDog();
        var secondDog = newDog();
        var owner = newOwner();

        owner.addDog(firstDog);
        owner.addDog(secondDog);

        var rv1 = owner.removeDog(secondDog);
        assertDogsOwned(true, rv1, owner, firstDog);

        var rv2 = owner.removeDog(firstDog);
        assertDogsOwned(true, rv2, owner);
    }

    @Test
    @Order(120)
    @DisplayName("Det går inte att ta bort en hund som inte har en ägare när man själv inte äger några hundar")
    public void removingDogWithoutOwnerWhenOwningNoDogs() {
        var dog = newDog();
        var owner = newOwner();

        var rv = owner.removeDog(dog);

        assertDogsOwned(false, rv, owner);
    }

    @Test
    @Order(130)
    @DisplayName("Det går inte att ta bort en hund som inte har en ägare när man själv äger minst en hund")
    public void removingDogWithoutOwnerWhenOwningAtLeastOneOtherDog() {
        var dog = newDog();
        var otherDog = newDog("Otherdog");
        var owner = newOwner();

        owner.addDog(otherDog);
        var rv = owner.removeDog(dog);

        assertDogsOwned(false, rv, owner, otherDog);
    }

    @Test
    @Order(140)
    @DisplayName("Det går inte att ta bort en hund som har en annan ägare när man själv inte äger några hundar")
    public void removingDogOwnedByOtherOwnerWhenOwningNoDogs() {
        var dog = newDog();
        var owner = newOwner();
        var otherOwner = newOwner("Otherowner");

        otherOwner.addDog(dog);
        var rv = owner.addDog(dog);

        assertDogsOwned(false, rv, owner);
    }

    @Test
    @Order(150)
    @DisplayName("Det går inte att ta bort en hund som har en annan ägare när man själv äger minst en hund")
    public void removingDogOwnedByOtherOwnerWhenOwningAtLeastOneOtherDog() {
        var dog = newDog();
        var otherDog = newDog("Otherdog");
        var owner = newOwner();
        var otherOwner = newOwner("Otherowner");

        owner.addDog(otherDog);
        otherOwner.addDog(dog);
        var rv = owner.addDog(dog);

        assertDogsOwned(false, rv, owner, otherDog);
    }

    @Test
    @Order(160)
    @DisplayName("Det går inte att lägga till en hund som är null")
    public void addingNullDog() {
        var owner = newOwner();

        var rv = owner.addDog(null);

        assertDogsOwned(false, rv, owner);
    }

    @Test
    @Order(170)
    @DisplayName("Det går inte att ta bort en hund som är null")
    public void removingNullDog() {
        var dog = newDog();
        var owner = newOwner();

        owner.addDog(dog);
        var rv = owner.addDog(null);

        assertDogsOwned(false, rv, owner, dog);
    }

    @Test
    @Order(180)
    @DisplayName("Flera hundar kan läggas till och tas bort på olika sätt")
    public void multipleDogsAddedAndRemovedFromDifferentOwners() {
        // De "vanliga" namnen på hundarna och ägarna gjorde testet mer lättläst
        var fido = newDog("Fido");
        var karo = newDog("Karo");
        var henrik = newOwner("Henrik");
        var olle = newOwner("Olle");

        assertDogsOwned(henrik);
        assertDogsOwned(olle);

        henrik.addDog(fido);
        assertDogsOwned(henrik, fido);
        assertDogsOwned(olle);

        karo.setOwner(olle);
        assertDogsOwned(henrik, fido);
        assertDogsOwned(olle, karo);

        fido.setOwner(null);
        assertDogsOwned(henrik);
        assertDogsOwned(olle, karo);

        olle.addDog(fido);
        assertDogsOwned(henrik);
        assertDogsOwned(olle, karo, fido);

        olle.removeDog(fido);
        karo.setOwner(null);
        assertDogsOwned(henrik);
        assertDogsOwned(olle);

        fido.setOwner(henrik);
        henrik.addDog(karo);
        assertDogsOwned(henrik, fido, karo);
        assertDogsOwned(olle);
    }

    @Test
    @Order(190)
    @DisplayName("Förändringar i den avlästa samlingen påverkar inte orginalet")
    public void changesToCollectionReturnedByGetDogsDoesNotAffectOriginal() {
        var dog = newDog();
        var owner = newOwner();

        owner.addDog(dog);

        try {
            owner.getDogs().remove(dog);
        } catch (UnsupportedOperationException e) {
            // Förväntat möjligt undantag, inget att göra här.
        }

        assertDogsOwned(owner, dog);
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
