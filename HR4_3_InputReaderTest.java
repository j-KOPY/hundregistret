import static org.junit.jupiter.api.Assertions.*;

import java.io.*;
import java.lang.reflect.*;
import java.text.*;
import java.time.Duration;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.*;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * Testfall för inläsningsklassen i uppgift HR4.3.
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
@DisplayName("HR4.3: Testfall för inläsningsklassen")
public class HR4_3_InputReaderTest {

    public static final String VERSION = "2024-01-28 11:14";

    private static final Duration TIMEOUT = Duration.ofSeconds(1);
    private static final double ACCEPTED_DECIMAL_NUMBER_DIFFERENCE = 0.0001;
    private static final Locale ORIGINAL_DEFAULT_LOCALE = Locale.getDefault();

    private static final Method ASSUMED_METHOD_FOR_READING_INTEGER_NUMBER = identifyInputMethod(short.class, int.class,
            long.class);
    private static final Method ASSUMED_METHOD_FOR_READING_DECIMAL_NUMBER = identifyInputMethod(float.class,
            double.class);
    private static final Method ASSUMED_METHOD_FOR_READING_LINE_OF_TEXT = identifyInputMethod(String.class);

    private static final String PROMPT_MARKER = "?>";
    private static final String PROMPT_MARKER_REGEX = "\\?>";
    private static final Pattern PROMPT_MARKER_PATTERN = Pattern.compile(PROMPT_MARKER_REGEX);

    private static final Pattern ERROR_PATTERN = Pattern.compile("([Ff][Ee][Ll])|([Ee][Rr][Rr][Oo][Rr])");

    private static final String WHITE_SPACE_REGEX = "[ \t\r\n]*";

    private ByteArrayOutputStream systemOut = new ByteArrayOutputStream();
    private ByteArrayOutputStream systemErr = new ByteArrayOutputStream();

    private InputStream originalSystemIn = System.in;
    private PrintStream originalSystemOut = System.out;
    private PrintStream originalSystemErr = System.err;

    private static Method identifyInputMethod(Class<?>... possibleReturnTypes) {
        var possibleReturnTypesAsList = Arrays.asList(possibleReturnTypes);
        var methods = Stream.of(InputReader.class.getDeclaredMethods()).filter(
                        m -> Modifier.isPublic(m.getModifiers()) && possibleReturnTypesAsList.contains(m.getReturnType()))
                .toList();

        if (methods.size() != 1)
            return null;

        return methods.get(0);
    }

    private void setDefaultLocale(String language, String country) {
        var locale = new Locale.Builder().setLanguage(language).setRegion(country).build();
        Locale.setDefault(locale);
    }

    private void setSystemIn(String... userInputLines) {
        System.setIn(createUserInputStream(userInputLines));
    }

    private InputStream createUserInputStream(String... userInputLines) {
        var input = Arrays.stream(userInputLines).collect(Collectors.joining("\n")) + "\n";
        return new ByteArrayInputStream(input.getBytes());
    }

    private String systemOut() {
        return systemOut.toString();
    }

    private String lastSystemOut() {
        String content = systemOut();
        var first = content.indexOf(PROMPT_MARKER);
        var last = content.lastIndexOf(PROMPT_MARKER);

        while (first != last) {
            content = content.substring(first + 2).trim();
            first = content.indexOf(PROMPT_MARKER);
            last = content.lastIndexOf(PROMPT_MARKER);
        }

        return content;
    }

    private String systemErr() {
        return systemErr.toString();
    }

    private void assertNoErrorMessage() {
        var actual = ERROR_PATTERN.matcher(systemOut()).results().count();
        assertEquals(0, actual, "Fel antal felmeddelanden (\"fel\" eller \"error\") från programmet");
    }

    private void assertOutputContainsPrompt(String expected) {
        var pattern = Pattern.compile(expected + WHITE_SPACE_REGEX + PROMPT_MARKER_REGEX);
        var content = lastSystemOut().trim();
        if (!pattern.matcher(content).matches())
            fail("""
					Den förväntade ledtexten \"%s\" (följt av ?>) saknas.
					Det sista som skrivits ut på System.out är:
					%s
					Allt som skrivits ut på System.out är:
					%s
					""".formatted(expected, lastSystemOut(), systemOut()));
    }

    private void assertNumberOfPrompts(int expected) {
        var actual = PROMPT_MARKER_PATTERN.matcher(systemOut()).results().count();
        assertEquals(expected, actual, """
				Fel antal inläsningsmarkeringar (?>) från programmet.
				Allt som skrivits ut på System.out är:
				%s
				""".formatted(systemOut()));
    }

    private void assertNothingWrittenToSystemErr() {
        String content = systemErr();
        assertTrue(content.isEmpty(), "Fel eller debugg-meddelanden finns på System.err: \"%s\"".formatted(content));
    }

    private void assertMethodForReadingIntegerNumber(InputReader reader, String expectedPrompt, long expectedValue) {
        assertTimeoutPreemptively(TIMEOUT, () -> {
            try {
                Number actualValue = (Number) ASSUMED_METHOD_FOR_READING_INTEGER_NUMBER.invoke(reader, expectedPrompt);
                assertAll(() -> assertEquals(expectedValue, actualValue.longValue()),
                        () -> assertOutputContainsPrompt(expectedPrompt), () -> assertNoErrorMessage(),
                        () -> assertNothingWrittenToSystemErr());
            } catch (IllegalAccessException | InvocationTargetException e) {
                fail(e);
            }
        });
    }

    private void assertMethodForReadingDecimalNumber(InputReader reader, String expectedPrompt, double expectedValue) {
        assertTimeoutPreemptively(TIMEOUT, () -> {
            try {
                Number actualValue = (Number) ASSUMED_METHOD_FOR_READING_DECIMAL_NUMBER.invoke(reader, expectedPrompt);
                assertAll(
                        () -> assertEquals(expectedValue, actualValue.doubleValue(),
                                ACCEPTED_DECIMAL_NUMBER_DIFFERENCE),
                        () -> assertOutputContainsPrompt(expectedPrompt), () -> assertNoErrorMessage(),
                        () -> assertNothingWrittenToSystemErr());
            } catch (IllegalAccessException | InvocationTargetException e) {
                fail(e);
            }
        });
    }

    private void assertMethodForReadingLineOfText(InputReader reader, String expectedPrompt, String expectedLine) {
        assertTimeoutPreemptively(TIMEOUT, () -> {
            try {
                String actualLine = (String) ASSUMED_METHOD_FOR_READING_LINE_OF_TEXT.invoke(reader, expectedPrompt);
                assertAll(() -> assertEquals(expectedLine.trim(), actualLine.trim()),
                        () -> assertOutputContainsPrompt(expectedPrompt), () -> assertNoErrorMessage(),
                        () -> assertNothingWrittenToSystemErr());
            } catch (IllegalAccessException | InvocationTargetException e) {
                fail(e);
            }
        });
    }

    @BeforeEach
    public void setupSystemOutputStreams() {
        System.setOut(new PrintStream(systemOut));
        System.setErr(new PrintStream(systemErr));
    }

    @AfterEach
    public void restoreSystemStreams() {
        System.setIn(originalSystemIn);
        System.setOut(originalSystemOut);
        System.setErr(originalSystemErr);
    }

    @AfterEach
    public void restoreDefaultLocale() {
        Locale.setDefault(ORIGINAL_DEFAULT_LOCALE);
    }

    @Test
    @Order(10)
    @DisplayName("Implementerad enligt instruktionerna")
    public void validateDogImplementation() {
        new InputReaderImplementationValidator().execute();
    }

    /**
     * Anledningen till detta test är att statiska Scanner:s eller inläsningsklasser
     * skapas <em>innan</em> testen har haft möjlighet att sätta om inläsnings och
     * utskriftsströmmarna (<code>System.in</code> och <code>System.out</code>).
     * Detta betyder att de läser från och programmet skriver till fel ställen, så
     * testen fungerar inte alls.
     * <p>
     * HR4.2 kommer att ta upp detta och illustrera problemet.
     */
    @Test
    @Order(20)
    @DisplayName("Ingen statisk Scanner eller inläsningsklass")
    public void noStaticScannerOrInputReader() {
        var clazz = InputReader.class;
        Optional<Field> field = Stream.of(clazz.getDeclaredFields()).filter(f -> isStatic(f) && isOfInputClassType(f))
                .findAny();
        field.ifPresent(f -> fail(
                "Objektet som används för inläsning kan inte vara statiskt, detta kommer (troligen) att leda till att alla övriga test misslyckas"));
    }

    private boolean isStatic(Field f) {
        return Modifier.isStatic(f.getModifiers());
    }

    private boolean isOfInputClassType(Field f) {
        return f.getType() == Scanner.class || f.getType().getSimpleName().equals("InputReader");
    }

    @Test
    @Order(30)
    @DisplayName("Läs in ett heltal från användaren")
    public void readingSingleIntegerNumber() {
        setSystemIn("123");
        var reader = new InputReader();
        assertMethodForReadingIntegerNumber(reader, "Integer prompt", 123);
    }

    @Test
    @Order(40)
    @DisplayName("Läs in ett negativt heltal från användaren")
    public void readingSingleNegativeIntegerNumber() {
        setSystemIn("-123");
        var reader = new InputReader();
        assertMethodForReadingIntegerNumber(reader, "Integer prompt", -123);
    }

    @Test
    @Order(50)
    @DisplayName("Läs in flera heltal från användaren")
    public void readingMultipleIntegerNumbers() {
        setSystemIn("123", "45", "6");
        var reader = new InputReader();
        assertMethodForReadingIntegerNumber(reader, "First number", 123);
        assertMethodForReadingIntegerNumber(reader, "Second number", 45);
        assertMethodForReadingIntegerNumber(reader, "Third number", 6);
    }

    private String doubleToString(double d) {
        DecimalFormatSymbols dfs = DecimalFormatSymbols.getInstance();
        dfs.setMinusSign('-'); // Hanterar att - ibland ersätts med ett längre minustecken vid formatering
        DecimalFormat df = new DecimalFormat("#.#######", dfs); // Minimalt antal decimaler

        return df.format(d);
    }

    private String[] doublesToStrings(double... doubles) {
        String[] result = new String[doubles.length];
        for (int i = 0; i < doubles.length; i++) {
            result[i] = doubleToString(doubles[i]);
        }
        return result;
    }

    @ParameterizedTest(name = "{index} språk={0} land={1}")
    @CsvSource(value = { "sv,SE", "en,US" })
    @Order(60)
    @DisplayName("Läs in ett decimaltal från användaren")
    public void readingSingleDecimalNumber(String language, String country) {
        setDefaultLocale(language, country);
        setSystemIn(doublesToStrings(12.3));
        var reader = new InputReader();
        assertMethodForReadingDecimalNumber(reader, "Decimal prompt", 12.3);
    }

    @ParameterizedTest(name = "{index} språk={0} land={1}")
    @CsvSource(value = { "sv,SE", "en,US" })
    @Order(70)
    @DisplayName("Läs in ett negativt decimaltal från användaren")
    public void readingSingleNegativeDecimalNumber(String language, String country) {
        setDefaultLocale(language, country);
        setSystemIn(doublesToStrings(-12.3));
        var reader = new InputReader();
        assertMethodForReadingDecimalNumber(reader, "Decimal prompt", -12.3);
    }

    @ParameterizedTest
    @CsvSource(value = { "sv,SE", "en,US" })
    @Order(80)
    @DisplayName("Läs in flera decimaltal från användaren")
    public void readingMultipleDecimalNumbers(String language, String country) {
        setDefaultLocale(language, country);
        setSystemIn(doublesToStrings(12.3, 4.567));
        var reader = new InputReader();
        assertMethodForReadingDecimalNumber(reader, "First number", 12.3);
        assertMethodForReadingDecimalNumber(reader, "Second number", 4.567);
    }

    @Test
    @Order(90)
    @DisplayName("Läs in ett heltal som ett decimaltal från användaren")
    public void readingIntegerAsDecimalNumbers() {
        setSystemIn("123");
        var reader = new InputReader();
        assertMethodForReadingDecimalNumber(reader, "Decimal prompt", 123.0);
    }

    @Test
    @Order(100)
    @DisplayName("Läs in en rad med text från användaren")
    public void readingSingleLineOfText() {
        setSystemIn("A line of text");
        var reader = new InputReader();
        assertMethodForReadingLineOfText(reader, "Text prompt", "A line of text");
    }

    @Test
    @Order(110)
    @DisplayName("Läs in flera rader med text från användaren")
    public void readingMultipleLinesOfText() {
        setSystemIn("First line of text", "Second line of text");
        var reader = new InputReader();
        assertMethodForReadingLineOfText(reader, "First line", "First line of text");
        assertMethodForReadingLineOfText(reader, "Second line", "Second line of text");
    }

    @Test
    @Order(120)
    @DisplayName("Läs in ett heltal följt av en rad med text från användaren")
    public void readingIntegerNumberFollowedByLineOfText() {
        setSystemIn("123", "A line of text");
        var reader = new InputReader();
        assertMethodForReadingIntegerNumber(reader, "Integer prompt", 123);
        assertMethodForReadingLineOfText(reader, "Text prompt", "A line of text");
    }

    @ParameterizedTest
    @CsvSource(value = { "sv,SE", "en,US" })
    @Order(130)
    @DisplayName("Läs in ett decimaltal följt av en rad med text från användaren")
    public void readingDecimalNumberFollowedByLineOfText(String language, String country) {
        setDefaultLocale(language, country);
        setSystemIn(doubleToString(123.4), "A line of text");
        var reader = new InputReader();
        assertMethodForReadingDecimalNumber(reader, "Integer prompt", 123.4);
        assertMethodForReadingLineOfText(reader, "Text prompt", "A line of text");
    }

    @ParameterizedTest
    @CsvSource(value = { "sv,SE", "en,US" })
    @Order(140)
    @DisplayName("Läs in olika typer av värden från användaren")
    public void readingSeveralDifferentTypesOfValues(String language, String country) {
        setDefaultLocale(language, country);
        setSystemIn("1", doubleToString(-2345.67), "First line of text", "Second line of text", doubleToString(8.9),
                "Third line of text", "-10", "11", "12131415");
        var reader = new InputReader();

        assertMethodForReadingIntegerNumber(reader, "Single digit integer", 1);
        assertMethodForReadingDecimalNumber(reader, "Negative mid size decimal", -2345.67);
        assertMethodForReadingLineOfText(reader, "First line", "First line of text");
        assertMethodForReadingLineOfText(reader, "Second line", "Second line of text");
        assertMethodForReadingDecimalNumber(reader, "Small decimal, after line of text", 8.9);
        assertMethodForReadingLineOfText(reader, "Third line", "Third line of text");
        assertMethodForReadingIntegerNumber(reader, "Negative small integer, after line of text", -10);
        assertMethodForReadingDecimalNumber(reader, "Small decimal", 11.0);
        assertMethodForReadingIntegerNumber(reader, "Large integer", 12131415);
    }

    @Test
    @Order(150)
    @DisplayName("Bara en ledtext per inläsning")
    public void onlyOnePromptPerInput() {
        setSystemIn("123", doubleToString(12.3), "Text");
        var reader = new InputReader();
        assertMethodForReadingIntegerNumber(reader, "Integer prompt", 123);
        assertNumberOfPrompts(1);
        assertMethodForReadingDecimalNumber(reader, "Decimal prompt", 12.3);
        assertNumberOfPrompts(2);
        assertMethodForReadingLineOfText(reader, "Text prompt", "Text");
        assertNumberOfPrompts(3);
    }

    @Test
    @Order(160)
    @DisplayName("Läs in från en annan InputStream än System.in")
    public void readingFromOtherInputStream() {
        var otherStream = createUserInputStream("data");
        var reader = new InputReader(otherStream);
        assertMethodForReadingLineOfText(reader, "Data from other stream", "data");
    }

    @Test
    @Order(170)
    @DisplayName("Försök att skapa två instanser av inläsningsklassen med konstruktorn utan parametrar")
    public void cantCreateTwoInstancesUsingTheDefaultConstructor() {
        setSystemIn();
        new InputReader();
        assertThrows(IllegalStateException.class, () -> new InputReader());
    }

    @Test
    @Order(180)
    @DisplayName("Försök att skapa två instanser av inläsningsklassen som läser från samma InputStream med konstruktorn som tar en parameter")
    public void cantCreateTwoInstancesReadingFromTheSameInputStream() {
        var otherStream = createUserInputStream();
        new InputReader(otherStream);
        assertThrows(IllegalStateException.class, () -> new InputReader(otherStream));
    }

    @Test
    @Order(190)
    @DisplayName("Försök att skapa två instanser av inläsningsklassen som läser från System.in, den första med konstruktorn utan parameter ")
    public void cantCreateTwoInstancesReadingFromSystemInStartingWithTheDefaultConstructor() {
        setSystemIn();
        new InputReader();
        assertThrows(IllegalStateException.class, () -> new InputReader(System.in));
    }

    @Test
    @Order(200)
    @DisplayName("Försök att skapa två instanser av inläsningsklassen som läser från System.in, den andra med konstruktorn utan parameter")
    public void cantCreateTwoInstancesReadingFromSystemInEndingWithTheDefaultConstructor() {
        setSystemIn();
        new InputReader(System.in);
        assertThrows(IllegalStateException.class, () -> new InputReader());
    }

    @Test
    @Order(210)
    @DisplayName("Försök att skapa fler än två instanser av inläsningsklassen som läser från samma ström, med andra instanser mellan")
    public void cantCreateTwoInstancesReadingFromtheSameStreamWithOthersInbetween() {
        setSystemIn();
        new InputReader(System.in);
        new InputReader(createUserInputStream());
        new InputReader(createUserInputStream());
        new InputReader(createUserInputStream());
        assertThrows(IllegalStateException.class, () -> new InputReader(System.in));
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

}
