import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.*;
import java.util.*;
import java.util.stream.*;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Testfall för det fullständiga hundregistret i uppgift HR4.4. Dessa testfall
 * är <em>mycket</em> grundläggande, och du kommer att behöva testa programmet
 * manuellt också. Tips på hur du kan göra detta på ett effektivt sätt finns i
 * ilearn.
 * <p>
 * Vid inlämning i ilearn kommer ytterligare tester att köras med fler, och mer
 * komplicerade, scenarier som JUnit har svårt att hantera. Det är dock ingen
 * mening med att försöka köra dessa test innan du är rätt säker på att
 * programmet fungerar som avsett. Om det inte gör det riskerar du att drunkna i
 * felmeddelanden. När du försöker tolka felmeddelanden för dessa test är det
 * <em>mycket</em> viktigt att du läser <em>hela</em> felmeddelandet ordentligt,
 * beskrivningarna av vad som upptäcks är kompakta, och varje ord har betydelse.
 * För ett exempel se instruktionerna.
 * <p>
 * Testen i denna klass har samma restriktioner på utskrifter som i
 * <code>HR4_1_SkeletonTest</code>.
 * <p>
 * Beskrivningen av testfallens uppgift, styrka och svagheter från
 * <code>{@link HR1_1_OwnerTest}</code> gäller (naturligvis) också för
 * testfallen i denna klass. Var speciellt uppmärksam på att testfallen kan
 * uppdateras när som helst, inklusive <em>efter</em> deadline.
 *
 * @author Henrik Bergström
 * @version 2024-01-28 11:14
 * @see HR1_1_OwnerTest
 * @see HR4_1_SkeletonTest
 */
@TestMethodOrder(OrderAnnotation.class)
@DisplayName("HR4.4: Testfall för det fullständiga hundregistret")
public class HR4_4_DogRegisterTest {

    public static final String VERSION = "2024-01-28 11:14";

    @Test
    @Order(10)
    @DisplayName("Hundklassens gränssnitt implementerad enligt instruktionerna")
    public void validateDogImplementation() {
        new DogImplementationValidator().execute();
    }

    @Test
    @Order(20)
    @DisplayName("Ägarklassens gränssnitt implementerad enligt instruktionerna")
    public void validateOwnerImplementation() {
        new OwnerImplementationValidator().execute();
    }

    @Test
    @Order(30)
    @DisplayName("Sorteringsklassens gränssnitt implementerad enligt instruktionerna")
    public void validateDogSorterImplementation() {
        new DogSorterImplementationValidator().execute();
    }

    @Test
    @Order(40)
    @DisplayName("Jämförelseklassen för svanslängd implementerad enligt instruktionerna")
    public void validateDogTailComparatorImplementation() {
        new DogTailComparatorImplementationValidator().execute();
    }

    @Test
    @Order(50)
    @DisplayName("Jämförelseklassen för hundnamn implementerad enligt instruktionerna")
    public void validateDogNameComparatorImplementation() {
        new DogNameComparatorImplementationValidator().execute();
    }

    @Test
    @Order(60)
    @DisplayName("Jämförelseklassen för svanslängd och hundnamn implementerad enligt instruktionerna")
    public void validateDogTailNameComparatorImplementation() {
        new DogTailNameComparatorImplementationValidator().execute();
    }

    @Test
    @Order(70)
    @DisplayName("Ägarsamlingsklassen implementerad enligt instruktionerna")
    public void validateOwnerCollectionImplementation() {
        new OwnerCollectionImplementationValidator().execute();
    }

    @Test
    @Order(80)
    @DisplayName("Hundsamlingsklassen implementerad enligt instruktionerna")
    public void validateDogCollectionImplementation() {
        new DogCollectionImplementationValidator().execute();
    }

    @Test
    @Order(90)
    @DisplayName("Inläsningsklassen implementerad enligt instruktionerna")
    public void validateInputReaderImplementation() {
        new InputReaderImplementationValidator().execute();
    }

    /**
     * Anledningen till detta test är att statiska Scanner:s eller inläsningsklasser
     * skapas <em>innan</em> testen har haft möjlighet att sätta om inläsnings och
     * utskriftsströmmarna (<code>System.in</code> och <code>System.out</code>).
     * Detta betyder att de läser från och programmet skriver till fel ställen, så
     * testen fungerar inte alls.
     * <p>
     * HR4.2 tar upp detta och illustrerar problemet.
     */
    @ParameterizedTest
    @ValueSource(classes = { DogRegister.class, InputReader.class })
    @Order(100)
    @DisplayName("Ingen statisk Scanner eller inläsningsklass")
    public void noStaticScannerOrInputReader(Class<?> classUnderTest) {
        Optional<Field> field = Stream.of(classUnderTest.getDeclaredFields())
                .filter(f -> isStatic(f) && isOfInputClassType(f)).findAny();
        field.ifPresent(f -> fail(
                "Objektet som används för inläsning kan inte vara statiskt, detta kommer (troligen) att leda till att alla övriga test misslyckas"));
    }

    @Test
    @Order(110)
    @DisplayName("Inga statiska variabler")
    public void noStaticvariables() {
        // TODO: testet är alltför svagt, det släpper igenom saker som inte borde
        // accepteras, men som är svåra att kontrollera, tex "static String command;"

        Class<?> classUnderTest = DogRegister.class;
        Optional<Field> field = Stream.of(classUnderTest.getDeclaredFields())
                .filter(f -> isStatic(f) && isntExpectedConstant(f)).findAny();
        field.ifPresent(f -> fail(
                "De finns inget behov av några statiska variabler i DogRegister. (Konstanter för kommandon odyl går bra.)"));
    }

    private boolean isStatic(Field f) {
        return Modifier.isStatic(f.getModifiers());
    }

    private boolean isOfInputClassType(Field f) {
        return f.getType() == Scanner.class || f.getType().getSimpleName().equals("InputReader");
    }

    private boolean isntExpectedConstant(Field f) {
        return !Modifier.isFinal(f.getModifiers()) || f.getType().getSimpleName().equals("DogCollection")
                || f.getType().getSimpleName().equals("OwnerCollection");
    }

    /**
     * Detta test kan bara hitta Scanners på klassnivå, eller som skickas som
     * parametrar. Det kommer alltså att missa om du (felaktigt) skapar en scanner i
     * en metod. Det kontrollera dessutom bara de klasser som uppgiften explicit
     * namnger. Eventuella egna klasser som du lagt till kontrolleras inte.
     */
    @ParameterizedTest
    @ValueSource(classes = { Owner.class, Dog.class, DogTailComparator.class, DogNameComparator.class,
            DogTailNameComparator.class, DogSorter.class, DogCollection.class, OwnerCollection.class,
            DogRegister.class })
    @Order(120)
    @DisplayName("Försök kontrollera om några Scanners används någon annanstans i programmet än i InputReader")
    public void noScannerUsedInProgram(Class<?> classUnderTest) {
        Optional<Field> field = Stream.of(classUnderTest.getDeclaredFields()).filter(f -> f.getType() == Scanner.class)
                .findAny();
        field.ifPresent(f -> fail("""
				Det enda stället en Scanner får användas i ditt program är i inläsningsklassen.
				Klassen %s har en Scanner deklarerad på klassnivå, vilket verkar bryta mot detta krav.
				""".formatted(classUnderTest.getName())));

        Optional<Method> method = Stream.of(classUnderTest.getDeclaredMethods())
                .filter(m -> Stream.of(m.getParameterTypes()).anyMatch(p -> p == Scanner.class)).findAny();

        method.ifPresent(m -> fail("""
				Det enda stället en Scanner får användas i ditt program är i inläsningsklassen.
				Metoden %s tar en Scanner som parameter, vilket verkar bryta mot detta krav.
				""".formatted(m)));
    }

    /**
     * Hjälpmetod som lägger till standardhundarna som används
     * av många av testfallen nedan. För att hundarna ska läggas
     * till <em>måste</em> kommandot "REGISTER NEW DOG" fungera
     * korrekt.
     */
    private void addStandardDogs(ProgramRunner runner){
        runner.input("REGISTER NEW DOG", "Rex", "Labrador", "15", "10");
        runner.input("REGISTER NEW DOG", "Kisen", "Puli", "10", "20");
        runner.input("REGISTER NEW DOG", "Karo", "Shih tzu", "10", "10");
        runner.input("REGISTER NEW DOG", "Fido", "Schäfer", "10", "15");
        runner.input("REGISTER NEW DOG", "Max", "Tax", "7", "8");
        runner.clearLog();
    }

    /**
     * Hjälpmetod som lägger till standardägarna som används
     * av många av testfallen nedan. För att ägarna ska läggas
     * till <em>måste</em> kommandot "REGISTER NEW OWNER" fungera
     * korrekt.
     */
    private void addStandardOwners(ProgramRunner runner){
        runner.input("REGISTER NEW OWNER", "Beata");
        runner.input("REGISTER NEW OWNER", "Adam");
        runner.input("REGISTER NEW OWNER", "Daniella");
        runner.input("REGISTER NEW OWNER", "Caesar");
        runner.clearLog();
    }

    /**
     * Hjälpmetod som lägger till standardhundar till standardägare.
     * Används av många av testfallen nedan. För att hundarna ska
     * läggas till hos ägarna <em>måste</em> kommandot "GIVE DOG TO OWNER"
     * fungera korrekt.
     */
    private void addStandardDogsToOwners(ProgramRunner runner){
        runner.input("GIVE DOG TO OWNER", "Rex", "Adam");
        runner.input("GIVE DOG TO OWNER", "Karo", "Beata");
        runner.input("GIVE DOG TO OWNER", "Fido", "Beata");
        runner.clearLog();
    }

    /////////////////////////////////////////////////
    //
    // Testfall för kommandoexemplen för kommmandot
    // "REGISTER NEW OWNER".
    // Dessa testfall är desamma som de exempel som
    // finns på hur kommandot ska fungera i ilearn.
    //
    /////////////////////////////////////////////////

    @Test
    @Order(500)
    @DisplayName("REGISTER NEW OWNER: Registrering av ny ägare")
    public void registerNewOwnerCommandNoProblems() {
        // Förbered testet
        var runner = new ProgramRunner("Registrering av ny ägare");
        runner.start();
        runner.waitForNextInputPrompt();

        // Genomför testet
        runner.input("REGISTER NEW OWNER", "Stefan");
        assertAll(
                ()->{runner.assertLastOutputContainedNoErrors();},
                ()->{runner.assertNothingWrittenToSystemErr();}
        );
        runner.input("LIST OWNERS");
        assertAll(
                ()->{runner.assertLastOutputContains("Stefan");},
                ()->{runner.assertLastOutputContainedNoErrors();},
                ()->{runner.assertNothingWrittenToSystemErr();}
        );

        // Avsluta testet
        runner.input("EXIT");
        runner.assertStoppedCorrectly();
    }

    @Test
    @Order(501)
    @DisplayName("REGISTER NEW OWNER: Försök att registrera en ägare som redan finns")
    public void registerNewOwnerCommandOwnerAlreadyExists() {
        // Förbered testet
        var runner = new ProgramRunner("Försök att registrera en ägare som redan finns");
        runner.start();
        runner.waitForNextInputPrompt();
        addStandardOwners(runner);

        // Genomför testet
        runner.input("REGISTER NEW OWNER", "Caesar");
        assertAll(
                ()->{runner.assertLastOutputContainedExactlyOneError();},
                ()->{runner.assertNothingWrittenToSystemErr();}
        );
        runner.input("LIST OWNERS");
        assertAll(
                ()->{runner.assertLastOutputContains("Caesar");},
                ()->{runner.assertLastOutputContainedNoErrors();},
                ()->{runner.assertNothingWrittenToSystemErr();}
        );

        // Avsluta testet
        runner.input("EXIT");
        runner.assertStoppedCorrectly();
    }

    @Test
    @Order(502)
    @DisplayName("REGISTER NEW OWNER: Tomma och blanka namn ger fel")
    public void registerNewOwnerCommandBlankNamesGiveError() {
        // Förbered testet
        var runner = new ProgramRunner("Tomma och blanka namn ger fel");
        runner.start();
        runner.waitForNextInputPrompt();

        // Genomför testet
        runner.input("REGISTER NEW OWNER");
        assertAll(
                ()->{runner.assertLastOutputContainedNoErrors();},
                ()->{runner.assertNothingWrittenToSystemErr();}
        );
        runner.erroneousInputAssertLastOutputContainedExactlyOneErrorBetweenEach("", " ", "	", "  		 ");
        runner.input("Henrik");
        assertAll(
                ()->{runner.assertLastOutputContains("Henrik");},
                ()->{runner.assertLastOutputContainedNoErrors();},
                ()->{runner.assertNothingWrittenToSystemErr();}
        );
        runner.input("LIST OWNERS");
        assertAll(
                ()->{runner.assertLastOutputContains("Henrik");},
                ()->{runner.assertLastOutputContainedNoErrors();},
                ()->{runner.assertNothingWrittenToSystemErr();}
        );

        // Avsluta testet
        runner.input("EXIT");
        runner.assertStoppedCorrectly();
    }

    @Test
    @Order(503)
    @DisplayName("REGISTER NEW OWNER: Kommandot skrivet på annat sätt")
    public void registerNewOwnerCommandAcceptedInLowerCase() {
        // Förbered testet
        var runner = new ProgramRunner("Kommandot skrivet på annat sätt");
        runner.start();
        runner.waitForNextInputPrompt();

        // Genomför testet
        runner.input("register new owner", "Olle");
        assertAll(
                ()->{runner.assertLastOutputContains("Olle");},
                ()->{runner.assertLastOutputContainedNoErrors();},
                ()->{runner.assertNothingWrittenToSystemErr();}
        );
        runner.input("LIST OWNERS");
        assertAll(
                ()->{runner.assertLastOutputContains("Olle");},
                ()->{runner.assertLastOutputContainedNoErrors();},
                ()->{runner.assertNothingWrittenToSystemErr();}
        );

        // Avsluta testet
        runner.input("EXIT");
        runner.assertStoppedCorrectly();
    }

    /////////////////////////////////////////////////
    //
    // Testfall för kommandoexemplen för kommmandot
    // "REMOVE OWNER".
    // Dessa testfall är desamma som de exempel som
    // finns på hur kommandot ska fungera i ilearn.
    //
    /////////////////////////////////////////////////

    @Test
    @Order(504)
    @DisplayName("REMOVE OWNER: Borttag av ägare utan ägda hundar")
    public void removeOwnerCommandNoProblems() {
        // Förbered testet
        var runner = new ProgramRunner("Borttag av ägare utan ägda hundar");
        runner.start();
        runner.waitForNextInputPrompt();
        addStandardDogs(runner);
        addStandardOwners(runner);
        addStandardDogsToOwners(runner);

        // Genomför testet
        runner.input("REMOVE OWNER", "Caesar");
        assertAll(
                ()->{runner.assertLastOutputContainedNoErrors();},
                ()->{runner.assertNothingWrittenToSystemErr();}
        );
        runner.input("LIST OWNERS");
        assertAll(
                ()->{runner.assertLastOutputDoesNotContains("Caesar");},
                ()->{runner.assertLastOutputContainedNoErrors();},
                ()->{runner.assertNothingWrittenToSystemErr();}
        );
        runner.input("LIST DOGS", "0");
        assertAll(
                ()->{runner.assertLastOutputContains("Rex", "Kisen", "Karo", "Fido", "Max");},
                ()->{runner.assertLastOutputContainedNoErrors();},
                ()->{runner.assertNothingWrittenToSystemErr();}
        );

        // Avsluta testet
        runner.input("EXIT");
        runner.assertStoppedCorrectly();
    }

    @Test
    @Order(505)
    @DisplayName("REMOVE OWNER: Borttag av ägare med hundar")
    public void removeOwnerCommandOwnerOfDogs() {
        // Förbered testet
        var runner = new ProgramRunner("Borttag av ägare med hundar");
        runner.start();
        runner.waitForNextInputPrompt();
        addStandardDogs(runner);
        addStandardOwners(runner);
        addStandardDogsToOwners(runner);

        // Genomför testet
        runner.input("REMOVE OWNER", "Beata");
        assertAll(
                ()->{runner.assertLastOutputContainedNoErrors();},
                ()->{runner.assertNothingWrittenToSystemErr();}
        );
        runner.input("LIST OWNERS");
        assertAll(
                ()->{runner.assertLastOutputDoesNotContains("Beata", "Karo", "Fido");},
                ()->{runner.assertLastOutputContainedNoErrors();},
                ()->{runner.assertNothingWrittenToSystemErr();}
        );
        runner.input("LIST DOGS", "0");
        assertAll(
                ()->{runner.assertLastOutputDoesNotContains("Beata", "Karo", "Fido");},
                ()->{runner.assertLastOutputContainedNoErrors();},
                ()->{runner.assertNothingWrittenToSystemErr();}
        );
        runner.input("LIST OWNERS");
        assertAll(
                ()->{runner.assertLastOutputContains("Adam", "Daniella", "Caesar");},
                ()->{runner.assertLastOutputContainedNoErrors();},
                ()->{runner.assertNothingWrittenToSystemErr();}
        );

        // Avsluta testet
        runner.input("EXIT");
        runner.assertStoppedCorrectly();
    }

    @Test
    @Order(506)
    @DisplayName("REMOVE OWNER: Borttag av ägare som inte finns")
    public void removeOwnerCommandNoSuchOwner() {
        // Förbered testet
        var runner = new ProgramRunner("Borttag av ägare som inte finns");
        runner.start();
        runner.waitForNextInputPrompt();
        addStandardDogs(runner);
        addStandardOwners(runner);
        addStandardDogsToOwners(runner);

        // Genomför testet
        runner.input("REMOVE OWNER", "Lena");
        assertAll(
                ()->{runner.assertLastOutputContainedExactlyOneError();},
                ()->{runner.assertNothingWrittenToSystemErr();}
        );
        runner.input("LIST OWNERS");
        assertAll(
                ()->{runner.assertLastOutputContains("Adam", "Beata", "Daniella", "Caesar");},
                ()->{runner.assertLastOutputContainedNoErrors();},
                ()->{runner.assertNothingWrittenToSystemErr();}
        );

        // Avsluta testet
        runner.input("EXIT");
        runner.assertStoppedCorrectly();
    }

    @Test
    @Order(507)
    @DisplayName("REMOVE OWNER: Borttag av ägare när det inte finns några")
    public void removeOwnerCommandNoOwners() {
        // Förbered testet
        var runner = new ProgramRunner("Borttag av ägare när det inte finns några");
        runner.start();
        runner.waitForNextInputPrompt();

        // Genomför testet
        runner.input("REMOVE OWNER");
        assertAll(
                ()->{runner.assertLastOutputContainedExactlyOneError();},
                ()->{runner.assertNothingWrittenToSystemErr();}
        );

        // Avsluta testet
        runner.input("EXIT");
        runner.assertStoppedCorrectly();
    }

    /////////////////////////////////////////////////
    //
    // Testfall för kommandoexemplen för kommmandot
    // "REGISTER NEW DOG".
    // Dessa testfall är desamma som de exempel som
    // finns på hur kommandot ska fungera i ilearn.
    //
    /////////////////////////////////////////////////

    @Test
    @Order(508)
    @DisplayName("REGISTER NEW DOG: Registrering av ny hund")
    public void registerNewDogCommandNoProblems() {
        // Förbered testet
        var runner = new ProgramRunner("Registrering av ny hund");
        runner.start();
        runner.waitForNextInputPrompt();

        // Genomför testet
        runner.input("REGISTER NEW DOG", "Ratata", "Blandras", "63", "10");
        assertAll(
                ()->{runner.assertLastOutputContainedNoErrors();},
                ()->{runner.assertNothingWrittenToSystemErr();}
        );
        runner.input("LIST DOGS", "0");
        assertAll(
                ()->{runner.assertLastOutputContains("Ratata");},
                ()->{runner.assertLastOutputContainedNoErrors();},
                ()->{runner.assertNothingWrittenToSystemErr();}
        );

        // Avsluta testet
        runner.input("EXIT");
        runner.assertStoppedCorrectly();
    }

    @Test
    @Order(509)
    @DisplayName("REGISTER NEW DOG: Försök att registrera en hund som redan finns")
    public void registerNewDogCommandDogAlreadyExists() {
        // Förbered testet
        var runner = new ProgramRunner("Försök att registrera en hund som redan finns");
        runner.start();
        runner.waitForNextInputPrompt();
        addStandardDogs(runner);

        // Genomför testet
        runner.input("REGISTER NEW DOG", "Fido");
        assertAll(
                ()->{runner.assertLastOutputContainedExactlyOneError();},
                ()->{runner.assertNothingWrittenToSystemErr();}
        );
        runner.input("LIST DOGS", "0");
        assertAll(
                ()->{runner.assertLastOutputContains("Rex", "Kisen", "Karo", "Fido", "Max");},
                ()->{runner.assertLastOutputContainedNoErrors();},
                ()->{runner.assertNothingWrittenToSystemErr();}
        );

        // Avsluta testet
        runner.input("EXIT");
        runner.assertStoppedCorrectly();
    }

    @Test
    @Order(510)
    @DisplayName("REGISTER NEW DOG: Tomma och blanka namn ger fel")
    public void registerNewDogCommandBlankNamesGiveError() {
        // Förbered testet
        var runner = new ProgramRunner("Tomma och blanka namn ger fel");
        runner.start();
        runner.waitForNextInputPrompt();

        // Genomför testet
        runner.input("REGISTER NEW DOG");
        assertAll(
                ()->{runner.assertLastOutputContainedNoErrors();},
                ()->{runner.assertNothingWrittenToSystemErr();}
        );
        runner.erroneousInputAssertLastOutputContainedExactlyOneErrorBetweenEach("", " ", "	", "  		 ");
        runner.input("Fido", "Tax", "1", "2");
        assertAll(
                ()->{runner.assertLastOutputContains("Fido");},
                ()->{runner.assertLastOutputContainedNoErrors();},
                ()->{runner.assertNothingWrittenToSystemErr();}
        );
        runner.input("LIST DOGS", "0");
        assertAll(
                ()->{runner.assertLastOutputContains("Fido");},
                ()->{runner.assertLastOutputContainedNoErrors();},
                ()->{runner.assertNothingWrittenToSystemErr();}
        );

        // Avsluta testet
        runner.input("EXIT");
        runner.assertStoppedCorrectly();
    }

    @Test
    @Order(511)
    @DisplayName("REGISTER NEW DOG: Tomma och blanka raser ger fel")
    public void registerNewDogCommandBlankBreedsGiveError() {
        // Förbered testet
        var runner = new ProgramRunner("Tomma och blanka raser ger fel");
        runner.start();
        runner.waitForNextInputPrompt();

        // Genomför testet
        runner.input("REGISTER NEW DOG", "Rex");
        assertAll(
                ()->{runner.assertLastOutputContainedNoErrors();},
                ()->{runner.assertNothingWrittenToSystemErr();}
        );
        runner.erroneousInputAssertLastOutputContainedExactlyOneErrorBetweenEach("", " ", "	", "  		 ");
        runner.input("Puli", "3", "4");
        assertAll(
                ()->{runner.assertLastOutputContains("Rex");},
                ()->{runner.assertLastOutputContainedNoErrors();},
                ()->{runner.assertNothingWrittenToSystemErr();}
        );
        runner.input("LIST DOGS", "0");
        assertAll(
                ()->{runner.assertLastOutputContains("Rex");},
                ()->{runner.assertLastOutputContainedNoErrors();},
                ()->{runner.assertNothingWrittenToSystemErr();}
        );

        // Avsluta testet
        runner.input("EXIT");
        runner.assertStoppedCorrectly();
    }

    @Test
    @Order(512)
    @DisplayName("REGISTER NEW DOG: Kommandot skrivet på annat sätt")
    public void registerNewDogCommandAcceptedInLowerCase() {
        // Förbered testet
        var runner = new ProgramRunner("Kommandot skrivet på annat sätt");
        runner.start();
        runner.waitForNextInputPrompt();

        // Genomför testet
        runner.input("register new dog", "Karo", "Blandras", "63", "10");
        assertAll(
                ()->{runner.assertLastOutputContains("Karo");},
                ()->{runner.assertLastOutputContainedNoErrors();},
                ()->{runner.assertNothingWrittenToSystemErr();}
        );
        runner.input("LIST DOGS", "0");
        assertAll(
                ()->{runner.assertLastOutputContains("Karo");},
                ()->{runner.assertLastOutputContainedNoErrors();},
                ()->{runner.assertNothingWrittenToSystemErr();}
        );

        // Avsluta testet
        runner.input("EXIT");
        runner.assertStoppedCorrectly();
    }

    /////////////////////////////////////////////////
    //
    // Testfall för kommandoexemplen för kommmandot
    // "REMOVE DOG".
    // Dessa testfall är desamma som de exempel som
    // finns på hur kommandot ska fungera i ilearn.
    //
    /////////////////////////////////////////////////

    @Test
    @Order(513)
    @DisplayName("REMOVE DOG: Borttag av hund utan ägare")
    public void removeDogCommandDogHasNoOwner() {
        // Förbered testet
        var runner = new ProgramRunner("Borttag av hund utan ägare");
        runner.start();
        runner.waitForNextInputPrompt();
        addStandardDogs(runner);
        addStandardOwners(runner);
        addStandardDogsToOwners(runner);

        // Genomför testet
        runner.input("REMOVE DOG", "Kisen");
        assertAll(
                ()->{runner.assertLastOutputContains("Kisen");},
                ()->{runner.assertLastOutputContainedNoErrors();},
                ()->{runner.assertNothingWrittenToSystemErr();}
        );
        runner.input("LIST DOGS", "0");
        assertAll(
                ()->{runner.assertLastOutputDoesNotContains("Kisen");},
                ()->{runner.assertLastOutputContainedNoErrors();},
                ()->{runner.assertNothingWrittenToSystemErr();}
        );
        runner.input("LIST DOGS", "0");
        assertAll(
                ()->{runner.assertLastOutputContains("Rex", "Karo", "Fido", "Max");},
                ()->{runner.assertLastOutputContainedNoErrors();},
                ()->{runner.assertNothingWrittenToSystemErr();}
        );
        runner.input("LIST OWNERS");
        assertAll(
                ()->{runner.assertLastOutputContains("Adam", "Beata", "Daniella", "Caesar");},
                ()->{runner.assertLastOutputContainedNoErrors();},
                ()->{runner.assertNothingWrittenToSystemErr();}
        );

        // Avsluta testet
        runner.input("EXIT");
        runner.assertStoppedCorrectly();
    }

    @Test
    @Order(514)
    @DisplayName("REMOVE DOG: Borttag av hund med ägare")
    public void removeDogCommandDogHasOwner() {
        // Förbered testet
        var runner = new ProgramRunner("Borttag av hund med ägare");
        runner.start();
        runner.waitForNextInputPrompt();
        addStandardDogs(runner);
        addStandardOwners(runner);
        addStandardDogsToOwners(runner);

        // Genomför testet
        runner.input("REMOVE DOG", "Rex");
        assertAll(
                ()->{runner.assertLastOutputContains("Rex");},
                ()->{runner.assertLastOutputContainedNoErrors();},
                ()->{runner.assertNothingWrittenToSystemErr();}
        );
        runner.input("LIST DOGS", "0");
        assertAll(
                ()->{runner.assertLastOutputDoesNotContains("Rex");},
                ()->{runner.assertLastOutputContainedNoErrors();},
                ()->{runner.assertNothingWrittenToSystemErr();}
        );
        runner.input("LIST DOGS", "0");
        assertAll(
                ()->{runner.assertLastOutputContains("Kisen", "Karo", "Fido", "Max");},
                ()->{runner.assertLastOutputContainedNoErrors();},
                ()->{runner.assertNothingWrittenToSystemErr();}
        );
        runner.input("LIST OWNERS");
        assertAll(
                ()->{runner.assertLastOutputContains("Adam", "Beata", "Daniella", "Caesar");},
                ()->{runner.assertLastOutputContainedNoErrors();},
                ()->{runner.assertNothingWrittenToSystemErr();}
        );

        // Avsluta testet
        runner.input("EXIT");
        runner.assertStoppedCorrectly();
    }

    @Test
    @Order(515)
    @DisplayName("REMOVE DOG: Borttag av hund som inte finns")
    public void removeDogCommandNoSuchDog() {
        // Förbered testet
        var runner = new ProgramRunner("Borttag av hund som inte finns");
        runner.start();
        runner.waitForNextInputPrompt();
        addStandardDogs(runner);
        addStandardOwners(runner);
        addStandardDogsToOwners(runner);

        // Genomför testet
        runner.input("REMOVE DOG", "Fluffy");
        assertAll(
                ()->{runner.assertLastOutputContainedExactlyOneError();},
                ()->{runner.assertNothingWrittenToSystemErr();}
        );
        runner.input("LIST DOGS", "0");
        assertAll(
                ()->{runner.assertLastOutputContains("Rex", "Kisen", "Karo", "Fido", "Max");},
                ()->{runner.assertLastOutputContainedNoErrors();},
                ()->{runner.assertNothingWrittenToSystemErr();}
        );

        // Avsluta testet
        runner.input("EXIT");
        runner.assertStoppedCorrectly();
    }

    @Test
    @Order(516)
    @DisplayName("REMOVE DOG: Borttag av hund när det inte finns några")
    public void removeDogCommandNoDogs() {
        // Förbered testet
        var runner = new ProgramRunner("Borttag av hund när det inte finns några");
        runner.start();
        runner.waitForNextInputPrompt();

        // Genomför testet
        runner.input("REMOVE DOG");
        assertAll(
                ()->{runner.assertLastOutputContainedExactlyOneError();},
                ()->{runner.assertNothingWrittenToSystemErr();}
        );

        // Avsluta testet
        runner.input("EXIT");
        runner.assertStoppedCorrectly();
    }

    /////////////////////////////////////////////////
    //
    // Testfall för kommandoexemplen för kommmandot
    // "LIST DOGS".
    // Dessa testfall är desamma som de exempel som
    // finns på hur kommandot ska fungera i ilearn.
    //
    /////////////////////////////////////////////////

    @Test
    @Order(517)
    @DisplayName("LIST DOGS: Lista alla hundar")
    public void listDogsCommandAllDogs() {
        // Förbered testet
        var runner = new ProgramRunner("Lista alla hundar");
        runner.start();
        runner.waitForNextInputPrompt();
        addStandardDogs(runner);
        addStandardOwners(runner);
        addStandardDogsToOwners(runner);

        // Genomför testet
        runner.input("LIST DOGS", "0");
        assertAll(
                ()->{runner.assertLastOutputContainsInOrder("3.7", "10", "15", "15", "20");},
                ()->{runner.assertLastOutputDoesNotContains("Caesar", "Daniella");},
                ()->{runner.assertLastOutputContainedNoErrors();},
                ()->{runner.assertNothingWrittenToSystemErr();}
        );

        // Avsluta testet
        runner.input("EXIT");
        runner.assertStoppedCorrectly();
    }

    @Test
    @Order(518)
    @DisplayName("LIST DOGS: Lista bara vissa hundar hundar")
    public void listDogsCommandOnlySomeDogs() {
        // Förbered testet
        var runner = new ProgramRunner("Lista bara vissa hundar hundar");
        runner.start();
        runner.waitForNextInputPrompt();
        addStandardDogs(runner);
        addStandardOwners(runner);
        addStandardDogsToOwners(runner);

        // Genomför testet
        runner.input("LIST DOGS", "15.0");
        assertAll(
                ()->{runner.assertLastOutputContainsInOrder("15", "15", "20");},
                ()->{runner.assertLastOutputDoesNotContains("Caesar", "Daniella");},
                ()->{runner.assertLastOutputContainedNoErrors();},
                ()->{runner.assertNothingWrittenToSystemErr();}
        );

        // Avsluta testet
        runner.input("EXIT");
        runner.assertStoppedCorrectly();
    }

    @Test
    @Order(519)
    @DisplayName("LIST DOGS: Lista hundar när det inte finns några registrerade")
    public void listDogsCommandNoDogs() {
        // Förbered testet
        var runner = new ProgramRunner("Lista hundar när det inte finns några registrerade");
        runner.start();
        runner.waitForNextInputPrompt();
        addStandardOwners(runner);

        // Genomför testet
        runner.input("LIST DOGS");
        assertAll(
                ()->{runner.assertLastOutputContainedExactlyOneError();},
                ()->{runner.assertNothingWrittenToSystemErr();}
        );

        // Avsluta testet
        runner.input("EXIT");
        runner.assertStoppedCorrectly();
    }

    /////////////////////////////////////////////////
    //
    // Testfall för kommandoexemplen för kommmandot
    // "LIST OWNERS".
    // Dessa testfall är desamma som de exempel som
    // finns på hur kommandot ska fungera i ilearn.
    //
    /////////////////////////////////////////////////

    @Test
    @Order(520)
    @DisplayName("LIST OWNERS: Lista ägare")
    public void listOwnersCommandNoProblems() {
        // Förbered testet
        var runner = new ProgramRunner("Lista ägare");
        runner.start();
        runner.waitForNextInputPrompt();
        addStandardDogs(runner);
        addStandardOwners(runner);
        addStandardDogsToOwners(runner);

        // Genomför testet
        runner.input("LIST OWNERS");
        assertAll(
                ()->{runner.assertLastOutputContainsInOrder("Adam", "Rex", "Beata", "Fido", "Caesar", "Daniella");},
                ()->{runner.assertLastOutputDoesNotContains("Kisen", "Max");},
                ()->{runner.assertLastOutputContainedNoErrors();},
                ()->{runner.assertNothingWrittenToSystemErr();}
        );

        // Avsluta testet
        runner.input("EXIT");
        runner.assertStoppedCorrectly();
    }

    @Test
    @Order(521)
    @DisplayName("LIST OWNERS: Lista ägare när det inte finns några registrerade")
    public void listOwnersCommandNoOwners() {
        // Förbered testet
        var runner = new ProgramRunner("Lista ägare när det inte finns några registrerade");
        runner.start();
        runner.waitForNextInputPrompt();
        addStandardDogs(runner);

        // Genomför testet
        runner.input("LIST OWNERS");
        assertAll(
                ()->{runner.assertLastOutputContainedExactlyOneError();},
                ()->{runner.assertNothingWrittenToSystemErr();}
        );

        // Avsluta testet
        runner.input("EXIT");
        runner.assertStoppedCorrectly();
    }

    /////////////////////////////////////////////////
    //
    // Testfall för kommandoexemplen för kommmandot
    // "INCREASE AGE".
    // Dessa testfall är desamma som de exempel som
    // finns på hur kommandot ska fungera i ilearn.
    //
    /////////////////////////////////////////////////

    @Test
    @Order(522)
    @DisplayName("INCREASE AGE: Öka åldern på en hund")
    public void increaseAgeCommandNoProblems() {
        // Förbered testet
        var runner = new ProgramRunner("Öka åldern på en hund");
        runner.start();
        runner.waitForNextInputPrompt();
        addStandardDogs(runner);

        // Genomför testet
        runner.input("INCREASE AGE", "Kisen");
        assertAll(
                ()->{runner.assertLastOutputContainedNoErrors();},
                ()->{runner.assertNothingWrittenToSystemErr();}
        );
        runner.input("LIST DOGS", "20");
        assertAll(
                ()->{runner.assertLastOutputContains("Kisen", "11", "20");},
                ()->{runner.assertLastOutputContainedNoErrors();},
                ()->{runner.assertNothingWrittenToSystemErr();}
        );

        // Avsluta testet
        runner.input("EXIT");
        runner.assertStoppedCorrectly();
    }

    @Test
    @Order(523)
    @DisplayName("INCREASE AGE: Öka åldern på en hund på en hund som inte finns")
    public void increaseAgeCommandNoSuchDog() {
        // Förbered testet
        var runner = new ProgramRunner("Öka åldern på en hund på en hund som inte finns");
        runner.start();
        runner.waitForNextInputPrompt();
        addStandardDogs(runner);

        // Genomför testet
        runner.input("INCREASE AGE", "Fluffy");
        assertAll(
                ()->{runner.assertLastOutputContainedExactlyOneError();},
                ()->{runner.assertNothingWrittenToSystemErr();}
        );

        // Avsluta testet
        runner.input("EXIT");
        runner.assertStoppedCorrectly();
    }

    @Test
    @Order(524)
    @DisplayName("INCREASE AGE: Öka åldern på en hund när det inte finns några registrerade")
    public void increaseAgeCommandNoDogs() {
        // Förbered testet
        var runner = new ProgramRunner("Öka åldern på en hund när det inte finns några registrerade");
        runner.start();
        runner.waitForNextInputPrompt();

        // Genomför testet
        runner.input("INCREASE AGE");
        assertAll(
                ()->{runner.assertLastOutputContainedExactlyOneError();},
                ()->{runner.assertNothingWrittenToSystemErr();}
        );

        // Avsluta testet
        runner.input("EXIT");
        runner.assertStoppedCorrectly();
    }

    /////////////////////////////////////////////////
    //
    // Testfall för kommandoexemplen för kommmandot
    // "GIVE DOG TO OWNER".
    // Dessa testfall är desamma som de exempel som
    // finns på hur kommandot ska fungera i ilearn.
    //
    /////////////////////////////////////////////////

    @Test
    @Order(525)
    @DisplayName("GIVE DOG TO OWNER: Ge en hund till en ägare")
    public void giveDogToOwnerCommandNoProblems() {
        // Förbered testet
        var runner = new ProgramRunner("Ge en hund till en ägare");
        runner.start();
        runner.waitForNextInputPrompt();
        addStandardDogs(runner);
        addStandardOwners(runner);
        addStandardDogsToOwners(runner);

        // Genomför testet
        runner.input("GIVE DOG TO OWNER", "Kisen", "Daniella");
        assertAll(
                ()->{runner.assertLastOutputContainedNoErrors();},
                ()->{runner.assertNothingWrittenToSystemErr();}
        );
        runner.input("LIST DOGS", "20");
        assertAll(
                ()->{runner.assertLastOutputContains("Kisen", "Daniella");},
                ()->{runner.assertLastOutputContainedNoErrors();},
                ()->{runner.assertNothingWrittenToSystemErr();}
        );
        runner.input("LIST OWNERS");
        assertAll(
                ()->{runner.assertLastOutputContains("Kisen");},
                ()->{runner.assertLastOutputContainedNoErrors();},
                ()->{runner.assertNothingWrittenToSystemErr();}
        );

        // Avsluta testet
        runner.input("EXIT");
        runner.assertStoppedCorrectly();
    }

    @Test
    @Order(526)
    @DisplayName("GIVE DOG TO OWNER: Ge en hund som redan har en ägare till en ägare")
    public void giveDogToOwnerCommandDogAlreadyOwned() {
        // Förbered testet
        var runner = new ProgramRunner("Ge en hund som redan har en ägare till en ägare");
        runner.start();
        runner.waitForNextInputPrompt();
        addStandardDogs(runner);
        addStandardOwners(runner);
        addStandardDogsToOwners(runner);

        // Genomför testet
        runner.input("GIVE DOG TO OWNER", "Rex");
        assertAll(
                ()->{runner.assertLastOutputContainedExactlyOneError();},
                ()->{runner.assertNothingWrittenToSystemErr();}
        );
        runner.input("LIST DOGS", "0");
        assertAll(
                ()->{runner.assertLastOutputContains("Adam");},
                ()->{runner.assertLastOutputContainedNoErrors();},
                ()->{runner.assertNothingWrittenToSystemErr();}
        );

        // Avsluta testet
        runner.input("EXIT");
        runner.assertStoppedCorrectly();
    }

    @Test
    @Order(527)
    @DisplayName("GIVE DOG TO OWNER: Ge en hund som inte finns till en ägare")
    public void giveDogToOwnerCommandNoSuchDog() {
        // Förbered testet
        var runner = new ProgramRunner("Ge en hund som inte finns till en ägare");
        runner.start();
        runner.waitForNextInputPrompt();
        addStandardDogs(runner);
        addStandardOwners(runner);
        addStandardDogsToOwners(runner);

        // Genomför testet
        runner.input("GIVE DOG TO OWNER", "Fluffy");
        assertAll(
                ()->{runner.assertLastOutputContainedExactlyOneError();},
                ()->{runner.assertNothingWrittenToSystemErr();}
        );
        runner.input("LIST DOGS", "0");
        assertAll(
                ()->{runner.assertLastOutputDoesNotContains("Fluffy");},
                ()->{runner.assertLastOutputContainedNoErrors();},
                ()->{runner.assertNothingWrittenToSystemErr();}
        );

        // Avsluta testet
        runner.input("EXIT");
        runner.assertStoppedCorrectly();
    }

    @Test
    @Order(528)
    @DisplayName("GIVE DOG TO OWNER: Ge en hund till en ägare som inte finns")
    public void giveDogToOwnerCommandNoSuchOwner() {
        // Förbered testet
        var runner = new ProgramRunner("Ge en hund till en ägare som inte finns");
        runner.start();
        runner.waitForNextInputPrompt();
        addStandardDogs(runner);
        addStandardOwners(runner);
        addStandardDogsToOwners(runner);

        // Genomför testet
        runner.input("GIVE DOG TO OWNER", "Max", "Beatrice");
        assertAll(
                ()->{runner.assertLastOutputContainedExactlyOneError();},
                ()->{runner.assertNothingWrittenToSystemErr();}
        );
        runner.input("LIST DOGS", "0");
        assertAll(
                ()->{runner.assertLastOutputDoesNotContains("Beatrice");},
                ()->{runner.assertLastOutputContainedNoErrors();},
                ()->{runner.assertNothingWrittenToSystemErr();}
        );

        // Avsluta testet
        runner.input("EXIT");
        runner.assertStoppedCorrectly();
    }

    @Test
    @Order(529)
    @DisplayName("GIVE DOG TO OWNER: Ge en hund till en ägare när det inte finns några hundar")
    public void giveDogToOwnerCommandNoDogs() {
        // Förbered testet
        var runner = new ProgramRunner("Ge en hund till en ägare när det inte finns några hundar");
        runner.start();
        runner.waitForNextInputPrompt();
        addStandardOwners(runner);

        // Genomför testet
        runner.input("GIVE DOG TO OWNER");
        assertAll(
                ()->{runner.assertLastOutputContainedExactlyOneError();},
                ()->{runner.assertNothingWrittenToSystemErr();}
        );

        // Avsluta testet
        runner.input("EXIT");
        runner.assertStoppedCorrectly();
    }

    @Test
    @Order(530)
    @DisplayName("GIVE DOG TO OWNER: Ge en hund till en ägare när det inte finns några ägare")
    public void giveDogToOwnerCommandNoOwners() {
        // Förbered testet
        var runner = new ProgramRunner("Ge en hund till en ägare när det inte finns några ägare");
        runner.start();
        runner.waitForNextInputPrompt();
        addStandardDogs(runner);

        // Genomför testet
        runner.input("GIVE DOG TO OWNER");
        assertAll(
                ()->{runner.assertLastOutputContainedExactlyOneError();},
                ()->{runner.assertNothingWrittenToSystemErr();}
        );

        // Avsluta testet
        runner.input("EXIT");
        runner.assertStoppedCorrectly();
    }

    /////////////////////////////////////////////////
    //
    // Testfall för kommandoexemplen för kommmandot
    // "REMOVE DOG FROM OWNER".
    // Dessa testfall är desamma som de exempel som
    // finns på hur kommandot ska fungera i ilearn.
    //
    /////////////////////////////////////////////////

    @Test
    @Order(531)
    @DisplayName("REMOVE DOG FROM OWNER: Ta bort en hund från en ägare")
    public void removeDogFromOwnerCommandNoProblems() {
        // Förbered testet
        var runner = new ProgramRunner("Ta bort en hund från en ägare");
        runner.start();
        runner.waitForNextInputPrompt();
        addStandardDogs(runner);
        addStandardOwners(runner);
        addStandardDogsToOwners(runner);

        // Genomför testet
        runner.input("REMOVE DOG FROM OWNER", "Rex");
        assertAll(
                ()->{runner.assertLastOutputContainedNoErrors();},
                ()->{runner.assertNothingWrittenToSystemErr();}
        );
        runner.input("LIST OWNERS");
        assertAll(
                ()->{runner.assertLastOutputContainsInOrder("Adam", "Beata");},
                ()->{runner.assertLastOutputDoesNotContains("Rex");},
                ()->{runner.assertLastOutputContainedNoErrors();},
                ()->{runner.assertNothingWrittenToSystemErr();}
        );
        runner.input("LIST DOGS", "0");
        assertAll(
                ()->{runner.assertLastOutputContains("Rex");},
                ()->{runner.assertLastOutputDoesNotContains("Adam");},
                ()->{runner.assertLastOutputContainedNoErrors();},
                ()->{runner.assertNothingWrittenToSystemErr();}
        );

        // Avsluta testet
        runner.input("EXIT");
        runner.assertStoppedCorrectly();
    }

    @Test
    @Order(532)
    @DisplayName("REMOVE DOG FROM OWNER: Ta bort en hund som inte finns från en ägare")
    public void removeDogFromOwnerCommandNoSuchDog() {
        // Förbered testet
        var runner = new ProgramRunner("Ta bort en hund som inte finns från en ägare");
        runner.start();
        runner.waitForNextInputPrompt();
        addStandardDogs(runner);
        addStandardOwners(runner);
        addStandardDogsToOwners(runner);

        // Genomför testet
        runner.input("REMOVE DOG FROM OWNER", "Fluffy");
        assertAll(
                ()->{runner.assertLastOutputContainedExactlyOneError();},
                ()->{runner.assertNothingWrittenToSystemErr();}
        );

        // Avsluta testet
        runner.input("EXIT");
        runner.assertStoppedCorrectly();
    }

    @Test
    @Order(533)
    @DisplayName("REMOVE DOG FROM OWNER: Ta bort en hund från en ägare när det inte finns några hundar")
    public void removeDogFromOwnerCommandNoDogs() {
        // Förbered testet
        var runner = new ProgramRunner("Ta bort en hund från en ägare när det inte finns några hundar");
        runner.start();
        runner.waitForNextInputPrompt();
        addStandardOwners(runner);

        // Genomför testet
        runner.input("REMOVE DOG FROM OWNER");
        assertAll(
                ()->{runner.assertLastOutputContainedExactlyOneError();},
                ()->{runner.assertNothingWrittenToSystemErr();}
        );

        // Avsluta testet
        runner.input("EXIT");
        runner.assertStoppedCorrectly();
    }

    @Test
    @Order(534)
    @DisplayName("REMOVE DOG FROM OWNER: Ta bort en hund från en ägare när det inte finns några ägare")
    public void removeDogFromOwnerCommandNoOwners() {
        // Förbered testet
        var runner = new ProgramRunner("Ta bort en hund från en ägare när det inte finns några ägare");
        runner.start();
        runner.waitForNextInputPrompt();
        addStandardDogs(runner);

        // Genomför testet
        runner.input("REMOVE DOG FROM OWNER");
        assertAll(
                ()->{runner.assertLastOutputContainedExactlyOneError();},
                ()->{runner.assertNothingWrittenToSystemErr();}
        );

        // Avsluta testet
        runner.input("EXIT");
        runner.assertStoppedCorrectly();
    }

    /////////////////////////////////////////////////
    //
    // Testfall för kommandoexemplen för kommmandot
    // "EXIT".
    // Dessa testfall är desamma som de exempel som
    // finns på hur kommandot ska fungera i ilearn.
    //
    /////////////////////////////////////////////////

    @Test
    @Order(535)
    @DisplayName("EXIT: Normalt avslut")
    public void exitCommandNormalShutdown() {
        // Förbered testet
        var runner = new ProgramRunner("Normalt avslut");
        runner.start();
        runner.waitForNextInputPrompt();

        // Genomför testet
        runner.input("EXIT");
        assertAll(
                ()->{runner.assertLastOutputContainedNoErrors();},
                ()->{runner.assertNothingWrittenToSystemErr();}
        );
        runner.assertStoppedCorrectly();
    }

    @Test
    @Order(536)
    @DisplayName("EXIT: Kommandot skrivet på annat sätt")
    public void exitCommandAcceptedInLowerCase() {
        // Förbered testet
        var runner = new ProgramRunner("Kommandot skrivet på annat sätt");
        runner.start();
        runner.waitForNextInputPrompt();

        // Genomför testet
        runner.input("exit");
        assertAll(
                ()->{runner.assertLastOutputContainedNoErrors();},
                ()->{runner.assertNothingWrittenToSystemErr();}
        );
        runner.assertStoppedCorrectly();
    }


    // TODO: test för att dubbletter av hundar och ägare inte läggs till

    // TODO: större test med flera delar

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

    // @formatter:off
    /**
     * Denna klass är automatiskt genererad. Ändringar i den kommer att skrivas
     * över vid nästa uppdatering.
     */
    public class DogNameComparatorImplementationValidator {

        private final Class<?> cut = DogNameComparator.class;
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

    // @formatter:off
    /**
     * Denna klass är automatiskt genererad. Ändringar i den kommer att skrivas
     * över vid nästa uppdatering.
     */
    public class InputReaderImplementationValidator {

        private final Class<?> cut = InputReader.class;
        private static final java.util.Collection<MethodHeader> EXPECTED_PUBLIC_METHODS = new java.util.ArrayList<>();

        /**
         * Denna lista är automatiskt genererad från en referens-implementation.
         * Ordningen på metoderna är fast, men har inget med den förväntade ordningen
         * i klassen att göra.
         */
        static {
            EXPECTED_PUBLIC_METHODS.add(new MethodHeader(false, "String", ".*", "String"));
            EXPECTED_PUBLIC_METHODS.add(new MethodHeader(false, "double", ".*", "String"));
            EXPECTED_PUBLIC_METHODS.add(new MethodHeader(false, "float", ".*", "String"));
            EXPECTED_PUBLIC_METHODS.add(new MethodHeader(false, "int", ".*", "String"));
            EXPECTED_PUBLIC_METHODS.add(new MethodHeader(false, "long", ".*", "String"));
            EXPECTED_PUBLIC_METHODS.add(new MethodHeader(false, "short", ".*", "String"));
            EXPECTED_PUBLIC_METHODS.add(new MethodHeader(false, "void", "close.*"));
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
            org.junit.jupiter.api.Assertions.assertEquals(2, cut.getDeclaredConstructors().length,
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
    public class DogCollectionImplementationValidator {

        private final Class<?> cut = DogCollection.class;
        private static final java.util.Collection<MethodHeader> EXPECTED_PUBLIC_METHODS = new java.util.ArrayList<>();

        /**
         * Denna lista är automatiskt genererad från en referens-implementation.
         * Ordningen på metoderna är fast, men har inget med den förväntade ordningen
         * i klassen att göra.
         */
        static {
            EXPECTED_PUBLIC_METHODS.add(new MethodHeader(false, "ArrayList", ".*Tail.*", "double"));
            EXPECTED_PUBLIC_METHODS.add(new MethodHeader(false, "ArrayList", "getDogs"));
            EXPECTED_PUBLIC_METHODS.add(new MethodHeader(false, "Dog", "getDog", "String"));
            EXPECTED_PUBLIC_METHODS.add(new MethodHeader(false, "Optional", "getDog", "String"));
            EXPECTED_PUBLIC_METHODS.add(new MethodHeader(false, "boolean", "addDog", "Dog"));
            EXPECTED_PUBLIC_METHODS.add(new MethodHeader(false, "boolean", "containsDog", "Dog"));
            EXPECTED_PUBLIC_METHODS.add(new MethodHeader(false, "boolean", "containsDog", "String"));
            EXPECTED_PUBLIC_METHODS.add(new MethodHeader(false, "boolean", "isEmpty"));
            EXPECTED_PUBLIC_METHODS.add(new MethodHeader(false, "boolean", "removeDog", "Dog"));
            EXPECTED_PUBLIC_METHODS.add(new MethodHeader(false, "boolean", "removeDog", "String"));
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

    //import java.util.regex.*;

    /**
     * Denna klass används för att köra ditt program, och kontrollera att outputen
     * från det är korrekt. Kopior av ProgramRunner finns i flera testfalls-filerna.
     * Duplicerad kod är normalt en dålig idé eftersom det leder till svårigheter
     * med underhåll när man måste hitta alla ställen koden finns på och applicera
     * ändringar överallt. <p> Här är detta acceptabelt eftersom det gör att varje
     * testklass innehåller allt som behövs för att kunna köra den, och för att alla
     * ändringar automatiskt läggs in i alla klasser.
     */
    public class ProgramRunner extends Thread {

        private static final java.time.Duration TIMEOUT = java.time.Duration.ofSeconds(2);
        private static final int EXTRA_STOP_TIME_MILLIS = 250;

        private static final java.util.regex.Pattern TOKEN_PATTERN = java.util.regex.Pattern.compile("(([a-zA-ZåäöÅÄÖ]+)|(-?[0-9]+(\\.[0-9])?))");

        private java.io.PipedOutputStream systemInPipedOutputStream = new java.io.PipedOutputStream();
        private java.io.PipedInputStream systemIn;
        private java.io.ByteArrayOutputStream systemOut = new java.io.ByteArrayOutputStream();
        private java.io.ByteArrayOutputStream systemErr = new java.io.ByteArrayOutputStream();

        private String scenario;

        private StringBuilder ioLog = new StringBuilder();
        private String lastProgramOutput = "";

        private boolean stoppedCorrectly;
        private org.opentest4j.AssertionFailedError error;

        public ProgramRunner(String scenario) {
            super(scenario);
            this.scenario = scenario;
            setDaemon(true);
        }

        public String log() {
            return ioLog.toString();
        }

        public String lastLinesOfLog() {
            var lines = Arrays.asList(log().split("\\R"));
            var content = lines.subList(Math.max(0, lines.size() - 15), lines.size()).stream()
                    .collect(Collectors.joining("\n"));
            // return content.substring(content.indexOf("?>"));
            return content;
        }

        public void clearLog() {
            ioLog = new StringBuilder();
        }

        public void clearLogExceptLastLine() {
            var log = ioLog.toString().split("[\n\r]+");
            ioLog = new StringBuilder(log[log.length - 1]);
        }

        @Override
        public void run() {
            var originalDefaultLocale = Locale.getDefault();
            Locale.setDefault(Locale.ENGLISH);

            var originalSystemIn = System.in;
            var originalSystemErr = System.err;

            try {
                systemIn = new java.io.PipedInputStream(systemInPipedOutputStream);
            } catch (java.io.IOException e) {
                throw new RuntimeException(scenario, e);
            }
            System.setIn(systemIn);

            System.setOut(new java.io.PrintStream(systemOut));
            System.setErr(new java.io.PrintStream(systemErr));

            try {
                assertTimeoutPreemptively(TIMEOUT, () -> DogRegister.main(new String[] {}),
                        scenario + ". Programmet avslutades inte inom rimlig tid");
                stoppedCorrectly = true;
            } catch (org.opentest4j.AssertionFailedError e) {
                error = e;
            } finally {
                Locale.setDefault(originalDefaultLocale);
                System.setIn(originalSystemIn);
                System.setErr(originalSystemErr);
            }
        }

        public void input(String... inputLines) {
            for (var input : inputLines) {
                try {
                    systemInPipedOutputStream.write(input.getBytes());
                    systemInPipedOutputStream.write("\n".getBytes());
                    systemInPipedOutputStream.flush();

                    if (input.isBlank()) {
                        var description = switch (input) {
                            case "" -> "EN TOM STRÄNG, DVS ANVÄNDAREN TRYCKTE PÅ ENTER/RETURN DIREKT";
                            case " " -> "ETT MELLANSLAG";
                            case "\t" -> "EN TAB";
                            default -> "EN BLANK STRÄNG MED MER ÄN ETT TECKEN (MELLANSLAG, TAB, ODYL.)";
                        };
                        input = """
								%s
								[INPUT PÅ OVANSTÅENDE RAD VAR %s]""".formatted(input, description);
                    }

                    ioLog.append("%s\n".formatted(input));
                    waitForNextInputPrompt();
                } catch (java.io.IOException e) {
                    throw new RuntimeException(scenario, e);
                }
            }
        }

        public void erroneousInputAssertLastOutputContainedExactlyOneErrorBetweenEach(String... inputLines) {
            for (var input : inputLines) {
                input(input);
                assertLastOutputContainedExactlyOneError();
            }
        }

        public void waitForNextInputPrompt() {
            while (isAlive() && !systemOut.toString().trim().endsWith("?>")) {
                waitAShortWhile();
            }

            lastProgramOutput = systemOut.toString();
            systemOut.reset();
            ioLog.append(lastProgramOutput);
        }

        private void waitAShortWhile() {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                throw new RuntimeException(scenario, e);
            }
        }

        private Stream<Token> lastOutputStreamUpperCase() {
            var matcher = TOKEN_PATTERN.matcher(lastProgramOutput);
            return matcher.results().map(mr -> Token.get(mr.group()));
        }

        public void assertLastOutputContainedNoErrors() {
            assertFalse(lastOutputStreamUpperCase().anyMatch(token -> token instanceof Error),
                    """
                            Den sista programmet skrev ut innehöll ett felmeddelanden (något av orden "fel" eller "error") vilket testet inte förväntade sig.
    
                            %s
                            """
                            .formatted(lastLinesOfLog()));
        }

        public void assertLastOutputContainedExactlyOneError() {
            var errorMessages = lastOutputStreamUpperCase().filter(token -> token instanceof Error).count();
            assertEquals(1, errorMessages,
                    """
                            Den sista programmet skrev ut innehöll fel antal felmeddelanden	(något av orden "fel" eller "error"). Testet förväntade sig exakt ett sådant felmeddelande, men det var %d.
    
                            %s
                            """
                            .formatted(errorMessages, lastLinesOfLog()));
        }

        public void assertLastOutputContains(String... strings) {
            var output = lastOutputStreamUpperCase().collect(Collectors.toSet());
            var expected = new HashSet<Token>(Stream.of(strings).map(s -> Token.get(s)).toList());
            expected.removeAll(output);

            if (!expected.isEmpty())
                fail("""
						Det sista programmet skrev ut innehöll inte %s
						som testet förväntade sig. Jämförelsen skedde utan hänsyn till stora och små bokstäver.
	
						%s
						""".formatted(expected, lastLinesOfLog()));
        }

        public void assertLastOutputContainsInOrder(String... strings) {
            var output = lastOutputStreamUpperCase().iterator();
            for (String str : strings) {
                assertRemainsInStream(Token.get(str), output, strings);
            }
        }

        private void assertRemainsInStream(Token token, Iterator<Token> output, String[] strings) {
            while (output.hasNext()) {
                if (token.equals(output.next()))
                    return;
            }
            fail("""
					Det sista programmet skrev ut innehöll inte:
					%s
					Testet förväntar sig att samtliga dessa ska finnas med *I DEN ANGIVNA ORDNINGEN*.
					Jämförelsen skedde utan hänsyn till stora och små bokstäver.
	
					%s
					""".formatted(Arrays.toString(strings), lastLinesOfLog()));
        }

        public void assertLastOutputDoesNotContains(String... strings) {
            var output = lastOutputStreamUpperCase().collect(Collectors.toSet());
            var unexpected = new HashSet<Token>(Stream.of(strings).map(s -> Token.get(s)).toList());
            unexpected.retainAll(output);

            if (!unexpected.isEmpty())
                fail("""
						Det sista programmet skrev ut innehöll %s som testet
						förväntade sig inte skulle finnas. Jämförelsen skedde
						utan hänsyn till stora och små bokstäver.
	
						%s
						""".formatted(unexpected, lastLinesOfLog()));
        }

        public void assertNothingWrittenToSystemErr() {
            String content = systemErr.toString();
            assertTrue(content.isEmpty(), "Fel eller debugg-meddelanden finns på System.err:\n" + content);
        }

        public void assertStoppedCorrectly() {
            var endAtMillis = System.currentTimeMillis() + EXTRA_STOP_TIME_MILLIS;

            while (isAlive() && System.currentTimeMillis() < endAtMillis) {
                waitAShortWhile();
            }

            if (stoppedCorrectly && error == null)
                return;

            var msg = """
					Test: %s.
					Programmet har inte avslutats korrekt. Detta kan bero på många olika saker.
					En möjlighet är att något av kommandona inte som användes av testet inte
					fungerade korrekt, och programmet hängde sig av någon anledning. En annan
					är det helt enkelt tog mer tid än vad testerna förväntade sig. Det sistnämnda
					borde inte vara ett problem, men kan hända om många saker körs samtidigt.
					Du kan testa om det är det sistnämnda genom att köra bara detta test, eller
					genom att öka konstanten TIMEOUT.
					""".formatted(scenario);

            if (error != null)
                fail(msg, error);

            fail(msg);
        }

        private static interface Token {

            static Token get(String text) {
                if (text.equalsIgnoreCase("FEL") || text.equalsIgnoreCase("ERROR"))
                    return new Error();

                if (Character.isLetter(text.charAt(0)))
                    return new Word(text);

                return new Number(Double.parseDouble(text.replace(",", ".")));
            }

        }

        private static record Word(String text) implements Token {

            @Override
            public int hashCode() {
                return Objects.hash(text.toUpperCase());
            }

            @Override
            public boolean equals(Object obj) {
                if (this == obj) {
                    return true;
                }
                if (!(obj instanceof Word)) {
                    return false;
                }
                Word other = (Word) obj;
                return text.equalsIgnoreCase(other.text);
            }

        }

        private static record Number(double d) implements Token {

            @Override
            public boolean equals(Object obj) {
                if (this == obj) {
                    return true;
                }
                if (!(obj instanceof Number)) {
                    return false;
                }
                Number other = (Number) obj;
                double difference = Math.abs(d - other.d);

                return difference < 0.001;
            }

        }

        private static record Error() implements Token {

        }

    }

}
