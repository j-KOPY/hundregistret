import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;

/**
 * Testfall för ägarklassen i HR1.1. De funktionella testerna kontrollerar bara
 * kraven i HR1.1, men stiltesten accepterar även tillägg och ändringar från
 * senare uppgifter, liksom alternativa varianter på vissa metoder.
 * <p>
 * Det första man måste förstå om mjukvarutestning är att testing
 * <em>aldrig</em> kan visa att koden man testar är fri från fel, utan bara om
 * man har hittat några fel eller inte. Det finns alltså <em>inga</em> garantier
 * att din kod är felfri bara för att testfallen går igenom.
 * <p>
 * Det ska också poängteras att JUnit är avsett för enhetstestning via ett API,
 * något det är <em>mycket</em> bra på. Det är inte alls lika bra på andra typer
 * av testning. Enhetstestning är den första nivån av testning där man tittar på
 * om de enskilda komponenterna fungerar isolerat. I verkligheten följs detta av
 * tester på större och större delar av systemet.
 * <p>
 * Hundregistret är ett mycket litet program, och de ingående delarna är i många
 * fall oberoende av varandra. Stora delar av det kan därför testas relativt väl
 * med hjälp av JUnit. Det kommer dock att finnas tillfällen i senare uppgifter
 * där JUnit inte är riktigt rätt verktyg, och där JUnit-testfallen därför
 * kompletteras med andra test via användargränssnittet.
 * <p>
 * De test som finns gör <em>inga</em> försök att vara kompletta. Som redan
 * sagts är detta omöjligt. De är bara till för att fånga uppenbara fel, och
 * undvika onödiga rester. Det är du själv, och ingen annan, som ansvarar för
 * att koden du lämnar in uppfyller kraven i uppgiften.
 * <p>
 * Du måste därför gå igenom din kod noga, och testa ordentligt med egna tester
 * också. Dessa egna tester kan vara i form av JUnit-test om du vill, formatet
 * för dem är ganska enkelt, men du får lika gärna skriva testprogram om du
 * föredrar det. Det viktiga är att du övertygar dig själv om att koden är
 * korrekt. Detta gäller även de ickefunktionella kraven. Här är
 * JUnit-testfallen bara till begränsad hjälp eftersom de inte kan kontrollera
 * speciellt mycket. Lite mer kontrolleras av checkstyle (ett verktyg för
 * statisk analys av kod) vid inlämning i ilearn, men i slutändan kan många av
 * dessa krav bara kontrolleras genom granskning av koden.
 * <p>
 * Tänk också på att testfallen är kod, och liksom all kod kan de innehålla fel.
 * Detta är speciellt relevant i år eftersom testfallen har skrivits om från
 * grunden när uppgiften strukturerades om inför årets kursomgång. Kursledningen
 * har implementerat flera versioner av hundregistret och testat dem med
 * testfallen, så det allra mesta är säkerligen korrekt. De ställen där det
 * finns störst risk att något missats är där det finns alternativa varianter på
 * uppgifter.
 * <p>
 * Om du tror dig ha hittat ett problem med testfallen, så börja med att gå
 * igenom uppgiften en extra gång för att säkerställa att din tolkning är den
 * korrekta. Skicka sedan ett meddelande till handledningsforumet så att
 * testerna kan uppdateras. De uppdaterade testfallen läggs upp i ilearn på
 * samma ställe. Du kan se om du har den senaste versionen av ett testfall från
 * versionsinformationen sist i denna JavaDoc-kommentar.
 * <p>
 * Med detta sagt ska det dock också sägas att det <em>inte</em> gjorts några
 * medvetna försök att dölja fel i din kod eller skriva felaktiga testfall.
 * Testfallen gör ett ärligt försök att fånga så mycket som möjligt. Detta
 * betyder också att testfallen kan komma att uppdateras om vi ser att många gör
 * fel på ett visst sätt. <strong>DESSA ÄNDRINGAR KAN KOMMA NÄR SOM HELST.
 * INKLUSIVE EFTER DEADLINE</strong>. Det sistnämnda är inte så konstigt som det
 * kan låta. All rättning av uppgiften sker efter deadline, och det är ofta då
 * vi hittar saker som behöver läggas till till testfallen.
 * <p>
 * När testfall uppdateras så körs ofta en utvärdering av de inlämnade uppgifter
 * som berörs. Det är alltså fullt möjligt att en uppgift som tidigare var
 * markerad som godkänd av de automatiska testen underkänns om den innehåller
 * ett fel som de tidigare testen inte letade efter. Kom ihåg att det är du
 * själv som ansvarar för att koden du lämnar in uppfyller kraven i uppgiften,
 * inte testen.
 *
 * @author Henrik Bergström
 * @version 2024-01-04 13:16
 */
@TestMethodOrder(OrderAnnotation.class)
@DisplayName("HR1.1: Testfall för ägarklassen")
public class HR1_1_OwnerTest {

    public static final String VERSION = "2024-01-04 13:16";

    @Test
    @Order(10)
    @DisplayName("Implementerad enligt instruktionerna")
    public void validateImplementation() {
        new OwnerImplementationValidator().execute();
    }

    @Test
    @Order(20)
    @DisplayName("Ett enda namn")
    public void singleName() {
        assertNameIsNormalized(new Owner("Adam"), "Adam", "ADAM");
        assertNameIsNormalized(new Owner("beata"), "Beata", "BEATA");
        assertNameIsNormalized(new Owner("CaEsAr"), "Caesar", "CAESAR");
        assertNameIsNormalized(new Owner("DIANA"), "Diana", "DIANA");
    }

    @Test
    @Order(30)
    @DisplayName("Fullständiga namn")
    public void fullNames() {
        // Mindre bra, men accepterade versioner av det normaliserade namnet läggs i
        // slutet av de tillåtna alternativen
        assertNameIsNormalized(new Owner("Adam Adamson"), "Adam Adamson", "ADAM ADAMSON", "Adam adamson");
        assertNameIsNormalized(new Owner("gaius Julius CAESAR"), "Gaius Julius Caesar", "GAIUS JULIUS CAESAR",
                "Gaius julius caesar", "Gaius Julius caesar", "Gaius julius Caesar");
    }

    @Test
    @Order(40)
    @DisplayName("Dubbelnamn")
    public void doubleNames() {
        // Mindre bra, men accepterade versioner av det normaliserade namnet läggs i
        // slutet av de tillåtna alternativen
        assertNameIsNormalized(new Owner("Anna-Karin"), "Anna-Karin", "ANNA-KARIN", "Anna-karin");
        assertNameIsNormalized(new Owner("Mary-JANE ek-BeRg"), "Mary-Jane Ek-Berg", "MARY-JANE EK-BERG",
                "Mary-jane ek-berg", "Mary-jane Ek-berg");
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
    public void unicodeChars() {
        assertNameIsNormalized(new Owner("Åsa"), "Åsa", "ÅSA");
        assertNameIsNormalized(new Owner("Örjan"), "Örjan", "ÖRJAN");
        assertNameIsNormalized(new Owner("Jürgen"), "Jürgen", "JÜRGEN");
        assertNameIsNormalized(new Owner("Niño"), "Niño", "NIÑO");
    }

    @Test
    @Order(60)
    @DisplayName("Enbokstavsnamn")
    public void singleLetterName() {
        assertNameIsNormalized(new Owner("Z"), "Z");
        assertNameIsNormalized(new Owner("a"), "A");
    }

    private void assertNameIsNormalized(Owner owner, String... normalizedNameFormats) {
        for (String name : normalizedNameFormats) {
            if (name.equals(owner.getName()))
                return;
        }
        fail("Namnet '%s' är inte normaliserat".formatted(owner.getName()));
    }

    @Test
    @Order(70)
    @DisplayName("Namnet finns med i strängrepresentationen")
    public void nameInStringRepresentation() {
        Owner owner = new Owner("Henrik");
        String str = owner.toString();
        assertTrue(str.contains("Henrik") || str.contains("HENRIK"),
                "Strängrepresentationen för ägaren ('%s') innehåller inte dennes namn".formatted(str));
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