import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.*;
import java.util.*;
import java.util.stream.*;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Testfall för programskelettet i uppgift HR4.1. JUnit-testfallen i denna fil
 * är snabba, men de är inte speciellt bra på att ge feedback om något går fel.
 * Detta beror på att JUnit inte är avsett för testning av program som
 * kommunicerar med användaren.
 * <p>
 * En effekt av detta är att det <em>INTE</em> går att skriva ut direkt till
 * <code>System.out</code> och <code>System.err</code> från denna klass. Det
 * beror på att testen ställer om dessa till egna instanser som testen kan läsa
 * av istället för att det som skrivs ut kommer ut på skärmen. Det går att komma
 * runt detta genom att skriva ut till <code>originalSystemOut</code> och
 * <code>originalSystemErr</code> istället, men det är troligtvis en bättre idé
 * att skapa ditt eget testprogram istället om du vill se exakt vad som skrivs
 * ut.
 * <p>
 * För att underlätta felsökning kommer testerna dock att skriva ut allt som
 * programmet skrivit ut om de hittar något fel. Detta är normalt en dålig idé
 * när man använder JUnit eftersom felmeddelanden som hör ihop kommer på två
 * olika ställen. I det här fallet fanns det dock inte mycket val.
 * <p>
 * För denna första, enkla, version av programmet är detta antagligen inte något
 * större problem, men ju mer funktionalitet som läggs till när du kommer till
 * HR4.4, ju mer kommer du att märka detta. Testprogrammet som används i ilearn
 * har bättre möjligheter att testa program som kommunicerar med användaren, och
 * kör därför ytterligare tester. Tänk dock på att dessa inte heller är
 * kompletta, du <em>måste</em> testa ditt program själv, ordentligt.
 * <p>
 * Beskrivningen av testfallens uppgift, styrka och svagheter från
 * <code>{@link HR1_1_OwnerTest}</code> gäller (naturligvis) också för
 * testfallen i denna klass. Var speciellt uppmärksam på att testfallen kan
 * uppdateras när som helst, inklusive <em>efter</em> deadline.
 *
 * @author Henrik Bergström
 * @version 2024-01-28 11:14
 * @see HR1_1_OwnerTest
 */
@TestMethodOrder(OrderAnnotation.class)
@DisplayName("HR4.1: Testfall för programskelettet")
public class HR4_1_SkeletonTest {

    public static final String VERSION = "2024-01-28 11:14";

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
    @Order(10)
    @DisplayName("Ingen statisk Scanner eller inläsningsklass")
    public void noStaticScannerOrInputReader() {
        var classUnderTest = DogRegister.class;
        Optional<Field> field = Stream.of(classUnderTest.getDeclaredFields())
                .filter(f -> isStatic(f) && isOfInputClassType(f)).findAny();
        field.ifPresent(f -> fail(
                "Objektet som används för inläsning kan inte vara statiskt, detta kommer (troligen) att leda till att alla övriga test misslyckas"));
    }

    private boolean isStatic(Field f) {
        return Modifier.isStatic(f.getModifiers());
    }

    private boolean isOfInputClassType(Field f) {
        return f.getType() == Scanner.class || f.getType().getSimpleName().equals("InputReader");
    }

    @ParameterizedTest(name = "{index} Kommandot \"{0}\" används för att avsluta programmet")
    @Order(20)
    @DisplayName("Exit i olika format accepteras")
    @ValueSource(strings = { "exit", "EXIT", "Exit", "ExIt" })
    public void exitProgramImmediately(String exitCommand) {
        // Förbered testet
        var runner = new ProgramRunner("Exit-kommandot formaterat \"" + exitCommand + "\"");
        runner.start();
        runner.waitForNextInputPrompt();

        // Genomför testet
        runner.input(exitCommand);
        assertAll(() -> {
            runner.assertLastOutputContainedNoErrors();
        }, () -> {
            runner.assertNothingWrittenToSystemErr();
        });
    }

    @ParameterizedTest(name = "{index} Det felaktiga kommandot \"{0}\" följt av \"EXIT\"")
    @Order(30)
    @DisplayName("Ett felaktigt kommando innan programmet avslutas")
    @ValueSource(strings = { "Inget kommando", "ASDFGHJ", "exit poll", "Emergency exit" })
    public void wrongCommandFollowedByExit(String wrongCommand) {
        // Förbered testet
        var runner = new ProgramRunner("Det felaktiga kommandot \"" + wrongCommand + "\"");
        runner.start();
        runner.waitForNextInputPrompt();

        // Genomför testet
        runner.erroneousInputAssertLastOutputContainedExactlyOneErrorBetweenEach(wrongCommand);
        runner.input("EXIT");
        assertAll(() -> {
            runner.assertLastOutputContainedNoErrors();
        }, () -> {
            runner.assertNothingWrittenToSystemErr();
        });
    }

    @Test
    @Order(40)
    @DisplayName("Flera felaktiga kommandon i följd innan programmet avslutas")
    public void multipleWrongCommands() {
        // Förbered testet
        var runner = new ProgramRunner("Flera felaktiga kommandon i följd");
        runner.start();
        runner.waitForNextInputPrompt();

        // Genomför testet
        runner.erroneousInputAssertLastOutputContainedExactlyOneErrorBetweenEach("FIRST", "SECOND", "THIRD");
        runner.input("EXIT");
        assertAll(() -> {
            runner.assertLastOutputContainedNoErrors();
        }, () -> {
            runner.assertNothingWrittenToSystemErr();
        });

    }

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
