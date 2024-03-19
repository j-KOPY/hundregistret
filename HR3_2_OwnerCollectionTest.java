import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.*;
import java.util.*;
import java.util.stream.Stream;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;

/**
 * Testfall för ägarsamlingen i uppgift HR3.2. <p> Beskrivningen av testfallens
 * uppgift, styrka och svagheter från <code>{@link HR1_1_OwnerTest}</code>
 * gäller (naturligvis) också för testfallen i denna klass. Var speciellt
 * uppmärksam på att testfallen kan uppdateras när som helst, inklusive
 * <em>efter</em> deadline.
 *
 * @author Henrik Bergström
 * @version 2024-01-28 11:14
 * @see HR1_1_OwnerTest
 */
@TestMethodOrder(OrderAnnotation.class)
@DisplayName("HR3.2: Testfall för ägarsamlingen")
public class HR3_2_OwnerCollectionTest {

    public static final String VERSION = "2024-01-28 11:14";

    private static final Owner FIRST_OWNER = new Owner("First");
    private static final Owner SECOND_OWNER = new Owner("Second");
    private static final Owner THIRD_OWNER = new Owner("Third");

    private static final Owner A_OWNER = SECOND_OWNER;

    private static final Owner OTHER_OWNER = new Owner("Other");

    private static final Field ASSUMED_FIELD_CONTAINING_OWNERS = identifyFieldContainingOwners();

    private OwnerCollection collection = new OwnerCollection();

    private static Field identifyFieldContainingOwners() {
        var fields = Stream.of(OwnerCollection.class.getDeclaredFields())
                .filter(f -> Owner[].class.isAssignableFrom(f.getType())).toList();

        if (fields.size() != 1)
            return null;

        fields.get(0).setAccessible(true);
        return fields.get(0);
    }

    private Owner[] ownersInCollection() {
        if (ASSUMED_FIELD_CONTAINING_OWNERS == null)
            fail("Inget fält innehållandes ägare har kunnat identifieras i samlingsklassen. Detta kan bero på att det inte finns något sådant fält, på att det finns flera möjliga fält, eller på att datatypen är fel");

        try {
            return (Owner[]) ASSUMED_FIELD_CONTAINING_OWNERS.get(collection);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            fail("""
					Fel vid försök att accessa ägarna i samlingsklassen. Mer information om vad som gick fel ges i "Caused by"-delen av felmeddelandet.
					Fältet som accessades var: \t%s
					"""
                    .formatted(ASSUMED_FIELD_CONTAINING_OWNERS), e);

            // Kan inte inträffa på grund av fail-satsen ovan, men kompilatorn kan inte se
            // detta, så return-satsen är nödvändig
            return null;
        }
    }

    private void addAllThreeTestOwnersInOrder() {
        collection.addOwner(FIRST_OWNER);
        collection.addOwner(SECOND_OWNER);
        collection.addOwner(THIRD_OWNER);
    }

    private void assertCollectionUpdatedCorrectly(boolean expectedReturnValue, boolean actualReturnValue,
                                                  Owner... expected) {
        var expectedAsList = Arrays.asList(expected);
        var actualAsList = Arrays.stream(ownersInCollection()).filter(e -> e != null).toList();
        var extra = new ArrayList<>(actualAsList);
        extra.removeAll(expectedAsList);
        var missing = new ArrayList<>(expectedAsList);
        missing.removeAll(actualAsList);

        assertAll(() -> assertEquals(expectedReturnValue, actualReturnValue, "Fel returvärde"),
                () -> assertEquals(expected.length, actualAsList.size(), "Fel antal ägare i samlingen"),
                () -> assertTrue(extra.isEmpty(), "Dessa ägare borde inte finnas i samlingen: " + extra),
                () -> assertTrue(missing.isEmpty(), "Dessa ägare saknas i samlingen: " + missing));
    }

    @Test
    @Order(10)
    @DisplayName("Ägarsamlingsklassen implementerad enligt instruktionerna")
    public void validateOwnerCollectionImplementation() {
        new OwnerCollectionImplementationValidator().execute();
    }

    @Test
    @Order(20)
    @DisplayName("Ägarklassen implementerad enligt instruktionerna")
    public void validateOwnerImplementation() {
        new OwnerImplementationValidator().execute();
    }

    @Test
    @Order(30)
    @DisplayName("En ny samling ska inte innehålla några ägare")
    public void noOwnersInNewCollection() {
        assertEquals(0, Arrays.stream(ownersInCollection()).filter(o -> o != null).count(),
                "En ny samling ska inte innehålla några ägare");
    }

    ////////////////////////////////////////////
    //
    // Tester för addOwner-metoden
    //
    ////////////////////////////////////////////

    @Test
    @Order(40)
    @DisplayName("Lägg till en ägare")
    public void addingOneOwner() {
        var rv = collection.addOwner(FIRST_OWNER);
        assertCollectionUpdatedCorrectly(true, rv, FIRST_OWNER);
    }

    @Test
    @Order(50)
    @DisplayName("Lägg till två ägare")
    public void addingTwoOwners() {
        var rv1 = collection.addOwner(FIRST_OWNER);
        assertCollectionUpdatedCorrectly(true, rv1, FIRST_OWNER);
        var rv2 = collection.addOwner(SECOND_OWNER);
        assertCollectionUpdatedCorrectly(true, rv2, FIRST_OWNER, SECOND_OWNER);
    }

    @Test
    @Order(60)
    @DisplayName("Lägg till tre ägare")
    public void addingThreeOwners() {
        var rv1 = collection.addOwner(FIRST_OWNER);
        assertCollectionUpdatedCorrectly(true, rv1, FIRST_OWNER);
        var rv2 = collection.addOwner(SECOND_OWNER);
        assertCollectionUpdatedCorrectly(true, rv2, FIRST_OWNER, SECOND_OWNER);
        var rv3 = collection.addOwner(THIRD_OWNER);
        assertCollectionUpdatedCorrectly(true, rv3, FIRST_OWNER, SECOND_OWNER, THIRD_OWNER);
    }

    @Test
    @Order(70)
    @DisplayName("Lägg till två ägare med samma namn")
    public void addingTwoOwnersWithTheSameName() {
        collection.addOwner(FIRST_OWNER);
        var rv = collection.addOwner(new Owner(FIRST_OWNER.getName()));
        assertCollectionUpdatedCorrectly(false, rv, FIRST_OWNER);
    }

    @Test
    @Order(80)
    @DisplayName("Lägg till två ägare med namn som delvis överlappar")
    public void addingTwoOwnersWithOverlappingName() {
        Owner pernilla = new Owner("Pernilla");
        Owner per = new Owner("Per");

        collection.addOwner(pernilla);
        var rv = collection.addOwner(per);
        assertCollectionUpdatedCorrectly(true, rv, pernilla, per);
    }
    ////////////////////////////////////////////
    //
    // Tester för containsOwner-metoden som tar en
    // ägare som parameter
    //
    ////////////////////////////////////////////

    @Test
    @Order(90)
    @DisplayName("Sökning i en tom samling")
    public void attemptingToSearchForNonexistingOwnerInEmptyCollection() {
        assertFalse(collection.containsOwner(OTHER_OWNER));
    }

    @Test
    @Order(100)
    @DisplayName("Sökning efter en ägare som inte existerar")
    public void attemptingToSearchForNonexistingOwner() {
        addAllThreeTestOwnersInOrder();
        assertFalse(collection.containsOwner(OTHER_OWNER));
    }

    @Test
    @Order(110)
    @DisplayName("Sökning efter den första ägaren")
    public void searchingForFirstOwner() {
        addAllThreeTestOwnersInOrder();
        assertTrue(collection.containsOwner(FIRST_OWNER));
    }

    @Test
    @Order(120)
    @DisplayName("Sökning efter den andra ägaren")
    public void searchingForSecondOwner() {
        addAllThreeTestOwnersInOrder();
        assertTrue(collection.containsOwner(SECOND_OWNER));
    }

    @Test
    @Order(130)
    @DisplayName("Sökning efter den sista ägaren")
    public void searchingForLastOwner() {
        addAllThreeTestOwnersInOrder();
        assertTrue(collection.containsOwner(THIRD_OWNER));
    }

    @Test
    @Order(140)
    @DisplayName("Sökning förändrar inte innehållet i samlingen")
    public void searchingDoesNotChangeContent() {
        addAllThreeTestOwnersInOrder();
        var rv = collection.containsOwner(A_OWNER);
        assertCollectionUpdatedCorrectly(true, rv, FIRST_OWNER, SECOND_OWNER, THIRD_OWNER);
    }

    ////////////////////////////////////////////
    //
    // Tester för containsOwner-metoden som tar
    // ett namn som parameter
    //
    ////////////////////////////////////////////

    @Test
    @Order(150)
    @DisplayName("Sökning i en tom samling via namn")
    public void attemptingToSearchForNonexistingOwnerInEmptyCollectionByName() {
        assertFalse(collection.containsOwner(OTHER_OWNER.getName()));
    }

    @Test
    @Order(160)
    @DisplayName("Sökning efter en ägare som inte existerar via namn")
    public void attemptingToSearchForNonexistingOwnerByName() {
        addAllThreeTestOwnersInOrder();
        assertFalse(collection.containsOwner(OTHER_OWNER.getName()));
    }

    @Test
    @Order(170)
    @DisplayName("Sökning efter den första ägaren via namn")
    public void searchingForFirstOwnerByName() {
        addAllThreeTestOwnersInOrder();
        assertTrue(collection.containsOwner(FIRST_OWNER.getName()));
    }

    @Test
    @Order(180)
    @DisplayName("Sökning efter den andra ägaren via namn")
    public void searchingForSecondOwnerByName() {
        addAllThreeTestOwnersInOrder();
        assertTrue(collection.containsOwner(SECOND_OWNER.getName()));
    }

    @Test
    @Order(190)
    @DisplayName("Sökning efter den sista ägaren via namn")
    public void searchingForLastOwnerByName() {
        addAllThreeTestOwnersInOrder();
        assertTrue(collection.containsOwner(THIRD_OWNER.getName()));
    }

    @Test
    @Order(200)
    @DisplayName("Sökning efter ägare via namn, nytt strängobjekt för namnet")
    public void searchingForOwnerByNameNewStringObject() {
        addAllThreeTestOwnersInOrder();
        assertTrue(collection.containsOwner(new String(A_OWNER.getName())));
    }

    @Test
    @Order(210)
    @DisplayName("Sökning via namn förändrar inte innehållet i samlingen")
    public void searchingDoesNotChangeContentByName() {
        addAllThreeTestOwnersInOrder();
        var rv = collection.containsOwner(A_OWNER.getName());
        assertCollectionUpdatedCorrectly(true, rv, FIRST_OWNER, SECOND_OWNER, THIRD_OWNER);
    }

    ////////////////////////////////////////////
    //
    // Tester för getOwner-metoden
    //
    ////////////////////////////////////////////

    @Test
    @Order(220)
    @DisplayName("Försök att hämta ut en ägare ur en tom samling")
    public void attemptingToGetOwnerFromEmptyCollection() {
        // TODO: hantera Optional
        var dog = collection.getOwner(OTHER_OWNER.getName());
        assertNull(dog, "Hunden borde inte finnas");
    }

    @Test
    @Order(230)
    @DisplayName("Försök att hämta en ägare som inte existerar")
    public void attemptingToGetNonexistingOwner() {
        addAllThreeTestOwnersInOrder();
        var dog = collection.getOwner(OTHER_OWNER.getName());
        assertNull(dog, "Hunden borde inte finnas");
    }

    @Test
    @Order(240)
    @DisplayName("Hämta den enda ägaren")
    public void getOnlyOwner() {
        collection.addOwner(A_OWNER);
        var dog = collection.getOwner(A_OWNER.getName());
        assertEquals(A_OWNER, dog);
    }

    @Test
    @Order(250)
    @DisplayName("Hämta den första ägaren")
    public void getFirstOwner() {
        addAllThreeTestOwnersInOrder();
        var dog = collection.getOwner(FIRST_OWNER.getName());
        assertEquals(FIRST_OWNER, dog);
    }

    @Test
    @Order(260)
    @DisplayName("Hämta den andra ägaren")
    public void getSecondOwner() {
        addAllThreeTestOwnersInOrder();
        var dog = collection.getOwner(SECOND_OWNER.getName());
        assertEquals(SECOND_OWNER, dog);
    }

    @Test
    @Order(270)
    @DisplayName("Hämta den sista ägaren")
    public void getLastOwner() {
        addAllThreeTestOwnersInOrder();
        var dog = collection.getOwner(THIRD_OWNER.getName());
        assertEquals(THIRD_OWNER, dog);
    }

    @Test
    @Order(280)
    @DisplayName("Hämta en ägare, nytt strängobjekt för namnet")
    public void getOwnerNewStringObject() {
        addAllThreeTestOwnersInOrder();
        var dog = collection.getOwner(new String(A_OWNER.getName()));
        assertEquals(A_OWNER, dog);
    }

    @Test
    @Order(290)
    @DisplayName("Att hämta en ägare förändrar inte innehållet i samlingen")
    public void getOwnerDoesNotChangeContent() {
        addAllThreeTestOwnersInOrder();
        var dog = collection.getOwner(A_OWNER.getName());
        assertEquals(A_OWNER, dog);
    }

    ////////////////////////////////////////////
    //
    // Tester för getOwners-metoden
    //
    ////////////////////////////////////////////

    @Test
    @Order(300)
    @DisplayName("Hämta ägarna från en tom samling")
    public void getOwnersFromEmptyCollection() {
        var owners = new HashSet<Owner>(collection.getOwners());
        assertEquals(new HashSet<>(), owners);
    }

    @Test
    @Order(310)
    @DisplayName("Hämta ägarna från en samling innehållandes ägare")
    public void getOwnersFromNonEmptyCollection() {
        addAllThreeTestOwnersInOrder();
        var owners = new HashSet<Owner>(collection.getOwners());
        assertEquals(new HashSet<>(Arrays.asList(FIRST_OWNER, SECOND_OWNER, THIRD_OWNER)), owners);
    }

    @Test
    @Order(320)
    @DisplayName("Att hämta ägarna förändrar inte innehållet i samlingen")
    public void getOwnersDoesNotChangeContent() {
        addAllThreeTestOwnersInOrder();
        new HashSet<Owner>(collection.getOwners());
        // De första argumenten är irrelevanta i detta test
        assertCollectionUpdatedCorrectly(true, true, FIRST_OWNER, SECOND_OWNER, THIRD_OWNER);
    }

    @Test
    @Order(330)
    @DisplayName("Förändringar i samlingen med de hämtade ägarna påverkar inte orginalet")
    public void changesToCollectionFromGetOwnersDoesNotChangeOriginal() {
        addAllThreeTestOwnersInOrder();
        var owners = collection.getOwners();
        try {
            owners.remove(A_OWNER);
        } catch (UnsupportedOperationException e) {
            // Förväntat möjligt undantag, inget att göra här.
        }
        // De första argumenten är irrelevanta i detta test
        assertCollectionUpdatedCorrectly(true, true, FIRST_OWNER, SECOND_OWNER, THIRD_OWNER);
    }

    @Test
    @Order(340)
    @DisplayName("Ägarna kommer i rätt ordning")
    public void getOwnersReturnsSortedList() {
        Owner adam = new Owner("Adam");
        Owner beata = new Owner("Beata");
        Owner caesar = new Owner("Caesar");
        Owner daniella = new Owner("Daniella");

        var dogs = Arrays.asList(beata, daniella, adam, caesar);
        for (Owner dog : dogs) {
            collection.addOwner(dog);
        }

        assertEquals(Arrays.asList(adam, beata, caesar, daniella), collection.getOwners());
    }

    @RepeatedTest(5)
    @Order(350)
    @DisplayName("Ägarna kommer i rätt ordning oavsett hur de sätts in")
    public void getOwnersReturnsSortedListIndependenOfInsertionOrder() {
        Owner adam = new Owner("Adam");
        Owner beata = new Owner("Beata");
        Owner caesar = new Owner("Caesar");
        Owner daniella = new Owner("Daniella");

        var owners = Arrays.asList(adam, beata, caesar, daniella);
        Collections.shuffle(owners);
        for (Owner owner : owners) {
            collection.addOwner(owner);
        }

        assertEquals(Arrays.asList(adam, beata, caesar, daniella), collection.getOwners());

    }

    ////////////////////////////////////////////
    //
    // Tester för removeOwner-metoden som tar en
    // ägare som parameter
    //
    ////////////////////////////////////////////

    @Test
    @Order(360)
    @DisplayName("Ta bort den enda ägaren i samlingen")
    public void removingOnlyOwner() {
        collection.addOwner(FIRST_OWNER);
        var rv = collection.removeOwner(FIRST_OWNER);
        assertCollectionUpdatedCorrectly(true, rv);
    }

    @Test
    @Order(370)
    @DisplayName("Ta bort den först tillagda ägaren från samlingen")
    public void removingFirstOwner() {
        addAllThreeTestOwnersInOrder();
        var rv = collection.removeOwner(FIRST_OWNER);
        assertCollectionUpdatedCorrectly(true, rv, SECOND_OWNER, THIRD_OWNER);
    }

    @Test
    @Order(380)
    @DisplayName("Ta bort den andra tillagda ägaren från samlingen")
    public void removingSecondOwner() {
        addAllThreeTestOwnersInOrder();
        var rv = collection.removeOwner(SECOND_OWNER);
        assertCollectionUpdatedCorrectly(true, rv, FIRST_OWNER, THIRD_OWNER);
    }

    @Test
    @Order(390)
    @DisplayName("Ta bort den sist tillagda ägaren från samlingen")
    public void removingLastOwner() {
        addAllThreeTestOwnersInOrder();
        var rv = collection.removeOwner(THIRD_OWNER);
        assertCollectionUpdatedCorrectly(true, rv, FIRST_OWNER, SECOND_OWNER);
    }

    @Test
    @Order(400)
    @DisplayName("Ta bort alla ägare från samlingen i insättningsordning")
    public void removingAllOwnersInOrder() {
        addAllThreeTestOwnersInOrder();
        var rv1 = collection.removeOwner(FIRST_OWNER);
        assertCollectionUpdatedCorrectly(true, rv1, SECOND_OWNER, THIRD_OWNER);
        var rv2 = collection.removeOwner(SECOND_OWNER);
        assertCollectionUpdatedCorrectly(true, rv2, THIRD_OWNER);
        var rv3 = collection.removeOwner(THIRD_OWNER);
        assertCollectionUpdatedCorrectly(true, rv3);
    }

    @Test
    @Order(410)
    @DisplayName("Ta bort alla ägare från samlingen i omvänd insättningsordning")
    public void removingAllOwnersInReverseOrder() {
        addAllThreeTestOwnersInOrder();
        var rv1 = collection.removeOwner(THIRD_OWNER);
        assertCollectionUpdatedCorrectly(true, rv1, FIRST_OWNER, SECOND_OWNER);
        var rv2 = collection.removeOwner(SECOND_OWNER);
        assertCollectionUpdatedCorrectly(true, rv2, FIRST_OWNER);
        var rv3 = collection.removeOwner(FIRST_OWNER);
        assertCollectionUpdatedCorrectly(true, rv3);
    }

    @Test
    @Order(420)
    @DisplayName("Försök ta bort en ägare som inte existerar från en tom samling")
    public void attemptingToRemoveNonexistingOwnerFromEmptyCollection() {
        var rv = collection.removeOwner(OTHER_OWNER);
        assertCollectionUpdatedCorrectly(false, rv);
    }

    @Test
    @Order(430)
    @DisplayName("Försök ta bort en ägare som inte existerar i samlingen")
    public void attemptingToRemoveNonexistingOwner() {
        addAllThreeTestOwnersInOrder();
        var rv = collection.removeOwner(OTHER_OWNER);
        assertCollectionUpdatedCorrectly(false, rv, FIRST_OWNER, SECOND_OWNER, THIRD_OWNER);
    }

    ////////////////////////////////////////////
    //
    // Tester för removeOwner-metoden som tar ett
    // namn som parameter
    //
    ////////////////////////////////////////////

    @Test
    @Order(440)
    @DisplayName("Ta bort den enda ägaren i samlingen via namn")
    public void removingOnlyOwnerByName() {
        collection.addOwner(FIRST_OWNER);
        var rv = collection.removeOwner(FIRST_OWNER.getName());
        assertCollectionUpdatedCorrectly(true, rv);
    }

    @Test
    @Order(450)
    @DisplayName("Ta bort den enda ägaren i samlingen via namn, nytt strängobjekt för namnet")
    public void removingOnlyOwnerByNameNewStringObject() {
        collection.addOwner(FIRST_OWNER);
        var rv = collection.removeOwner(new String(FIRST_OWNER.getName()));
        assertCollectionUpdatedCorrectly(true, rv);
    }

    @Test
    @Order(460)
    @DisplayName("Ta bort den först tillagda ägaren från samlingen via namn")
    public void removingFirstOwnerByName() {
        addAllThreeTestOwnersInOrder();
        var rv = collection.removeOwner(FIRST_OWNER.getName());
        assertCollectionUpdatedCorrectly(true, rv, SECOND_OWNER, THIRD_OWNER);
    }

    @Test
    @Order(470)
    @DisplayName("Ta bort den andra tillagda ägaren från samlingen via namn")
    public void removingSecondOwnerByName() {
        addAllThreeTestOwnersInOrder();
        var rv = collection.removeOwner(SECOND_OWNER.getName());
        assertCollectionUpdatedCorrectly(true, rv, FIRST_OWNER, THIRD_OWNER);
    }

    @Test
    @Order(480)
    @DisplayName("Ta bort den sist tillagda ägaren från samlingen via namn")
    public void removingLastOwnerByName() {
        addAllThreeTestOwnersInOrder();
        var rv = collection.removeOwner(THIRD_OWNER.getName());
        assertCollectionUpdatedCorrectly(true, rv, FIRST_OWNER, SECOND_OWNER);
    }

    @Test
    @Order(490)
    @DisplayName("Ta bort alla ägare från samlingen i insättningsordning via namn")
    public void removingAllOwnersInOrderByName() {
        addAllThreeTestOwnersInOrder();
        var rv1 = collection.removeOwner(FIRST_OWNER.getName());
        assertCollectionUpdatedCorrectly(true, rv1, SECOND_OWNER, THIRD_OWNER);
        var rv2 = collection.removeOwner(SECOND_OWNER.getName());
        assertCollectionUpdatedCorrectly(true, rv2, THIRD_OWNER);
        var rv3 = collection.removeOwner(THIRD_OWNER.getName());
        assertCollectionUpdatedCorrectly(true, rv3);
    }

    @Test
    @Order(500)
    @DisplayName("Ta bort alla ägare från samlingen i omvänd insättningsordning via namn")
    public void removingAllOwnersInReverseOrderByName() {
        addAllThreeTestOwnersInOrder();
        var rv1 = collection.removeOwner(THIRD_OWNER.getName());
        assertCollectionUpdatedCorrectly(true, rv1, FIRST_OWNER, SECOND_OWNER);
        var rv2 = collection.removeOwner(SECOND_OWNER.getName());
        assertCollectionUpdatedCorrectly(true, rv2, FIRST_OWNER);
        var rv3 = collection.removeOwner(FIRST_OWNER.getName());
        assertCollectionUpdatedCorrectly(true, rv3);
    }

    @Test
    @Order(510)
    @DisplayName("Försök ta bort en ägare som inte existerar från en tom samling via namn")
    public void attemptingToRemoveNonexistingOwnerFromEmptyCollectionByName() {
        var rv = collection.removeOwner(OTHER_OWNER.getName());
        assertCollectionUpdatedCorrectly(false, rv);
    }

    @Test
    @Order(520)
    @DisplayName("Försök ta bort en ägare som inte existerar i samlingen via namn")
    public void attemptingToRemoveNonexistingOwnerByName() {
        addAllThreeTestOwnersInOrder();
        var rv = collection.removeOwner(OTHER_OWNER.getName());
        assertCollectionUpdatedCorrectly(false, rv, FIRST_OWNER, SECOND_OWNER, THIRD_OWNER);
    }

    ////////////////////////////////////////////
    //
    // Tester för att arrayen växer om
    // nödvändigt
    //
    ////////////////////////////////////////////

    @Test
    @Order(530)
    @DisplayName("Arrayen växer om nödvändigt")
    public void arrayGrowsIfNecessary() {
        var expected = new ArrayList<Owner>();
        for (int i = 0; i < 3; i++) {
            Owner[] owners = ownersInCollection();
            int ownersToAdd = owners == null || owners.length == 0 ? 3 : owners.length + 2;
            for (int j = 0; j < ownersToAdd; j++) {
                var owner = new Owner("%d%04d".formatted(i, j));
                collection.addOwner(owner);
                expected.add(owner);
            }
            assertEquals(expected, collection.getOwners(), "Fel ägare i samlingen");
        }
    }

    @Test
    @Order(540)
    @DisplayName("Arrayen växer bara om nödvändigt")
    public void arrayGrowsOnlyIfNecessary() {
        var expected = new ArrayList<Owner>();

        for (int i = 0; i < 5; i++) {
            var owner = new Owner("%04d".formatted(expected.size()));
            collection.addOwner(owner);
            expected.add(owner);
        }

        Owner[] owners = ownersInCollection();
        int ownersToAdd = owners.length - expected.size();

        for (int i = 0; i < ownersToAdd; i++) {
            var owner = new Owner("%04d".formatted(expected.size()));
            collection.addOwner(owner);
            expected.add(owner);
        }

        owners = ownersInCollection();
        assertEquals(expected.size(), owners.length, "Arrayen ändrar storlek när så inte behövs");

        // Ska inte läggas till eftersom en annan person med detta namn redan finns
        collection.addOwner(new Owner(expected.get(3).getName()));

        owners = ownersInCollection();
        assertEquals(expected.size(), owners.length, "Arrayen ändrar storlek när så inte behövs");

        assertEquals(expected, collection.getOwners(), "Fel ägare i samlingen");
    }

    ////////////////////////////////////////////
    //
    // Tester för att eventuella tomma platser
    // återanvänds istället för att arrayen
    // växer i onödan.
    //
    ////////////////////////////////////////////

    @Test
    @Order(550)
    @DisplayName("Eventuella tomma platser efter borttag återanvänds")
    public void emptyPlacesReused() {
        var expected = new ArrayList<Owner>();
        for (int i = 0; i < 3; i++) {
            var owner = new Owner("%04d".formatted(expected.size()));
            collection.addOwner(owner);
            expected.add(owner);
        }

        Owner[] owners = ownersInCollection();
        int ownersToAdd = owners.length - expected.size();

        for (int i = 0; i < ownersToAdd; i++) {
            var owner = new Owner("%04d".formatted(expected.size()));
            collection.addOwner(owner);
            expected.add(owner);
        }

        int expectedArrayLength = ownersInCollection().length;

        var removed = expected.remove(1);
        collection.removeOwner(removed);

        var owner = new Owner("LAST ADDED");
        collection.addOwner(owner);
        expected.add(owner);

        assertEquals(expectedArrayLength, ownersInCollection().length,
                "Storleken på arrayen är fel efter att ha tagit bort en ägare och direkt lagt till en ny.");
        assertEquals(expected, collection.getOwners(), "Fel ägare i samlingen");
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

    // @formatter:off
    /**
     * Denna klass är automatiskt genererad. Ändringar i den kommer att skrivas
     * över vid nästa uppdatering.
     */
    public class OwnerCollectionImplementationValidator {

        private final Class<?> cut = OwnerCollection.class;
        private static final java.util.Collection<MethodHeader> EXPECTED_PUBLIC_METHODS = new java.util.ArrayList<>();

        /**
         * Denna lista är automatiskt genererad från en referens-implementation.
         * Ordningen på metoderna är fast, men har inget med den förväntade ordningen
         * i klassen att göra.
         */
        static {
            EXPECTED_PUBLIC_METHODS.add(new MethodHeader(false, "ArrayList", "getOwners"));
            EXPECTED_PUBLIC_METHODS.add(new MethodHeader(false, "Owner", "getOwner", "String"));
            EXPECTED_PUBLIC_METHODS.add(new MethodHeader(false, "boolean", "addOwner", "Owner"));
            EXPECTED_PUBLIC_METHODS.add(new MethodHeader(false, "boolean", "containsOwner", "Owner"));
            EXPECTED_PUBLIC_METHODS.add(new MethodHeader(false, "boolean", "containsOwner", "String"));
            EXPECTED_PUBLIC_METHODS.add(new MethodHeader(false, "boolean", "isEmpty"));
            EXPECTED_PUBLIC_METHODS.add(new MethodHeader(false, "boolean", "removeOwner", "Owner"));
            EXPECTED_PUBLIC_METHODS.add(new MethodHeader(false, "boolean", "removeOwner", "String"));
            EXPECTED_PUBLIC_METHODS.add(new MethodHeader(false, "int", "size"));
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
