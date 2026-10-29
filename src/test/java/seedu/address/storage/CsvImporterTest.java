package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BENSON;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

public class CsvImporterTest {
    private static final String EOL = "\r\n";
    private static final String SIMPLE_ROW = "Alice,81234567,,,,,,,,false";

    @TempDir
    public Path folder;

    @Test
    public void read_exportedContacts_roundTripsFieldsAndOrder() throws Exception {
        Person alice = new Person(new PersonBuilder(ALICE).withAddress("zoe, street")
                .withGroups("alpha", "beta").withTags("friends", "work").build(), true);
        Path file = folder.resolve("roundtrip.csv");
        List<Person> persons = List.of(alice, BENSON);
        CsvExporter.write(persons, file);
        assertEquals(persons, CsvImporter.read(file));
    }

    @Test
    public void read_paddedFields_trimsEdgesAndPreservesInternalSpaces() throws Exception {
        String row = "\" \tAlice  Tan \t\", 81234567 , alice@example.com ,\" 123  Main, Street \","
                + " team ; project  alpha , alice , alice tan , alice , friends ; work , true ";
        Person expected = new Person(new PersonBuilder().withName("Alice  Tan").withPhone("81234567")
                .withEmail("alice@example.com").withAddress("123  Main, Street")
                .withGroups("team", "project  alpha").withGithub("alice").withLinkedin("alice tan")
                .withTelegram("alice").withTags("friends", "work").build(), true);

        assertEquals(List.of(expected), read(CsvExporter.HEADER + EOL + row));
    }

    @Test
    public void read_pinnedValues_acceptsAnyCase() throws Exception {
        for (String value : List.of("true", "TRUE", "TrUe", " true ", " TRUE ")) {
            assertEquals(true, read(CsvExporter.HEADER + EOL + SIMPLE_ROW.replace("false", value))
                    .getFirst().isPinned());
        }
        for (String value : List.of("false", "FALSE", "FaLsE", " false ", " FALSE ")) {
            assertEquals(false, read(CsvExporter.HEADER + EOL + SIMPLE_ROW.replace("false", value))
                    .getFirst().isPinned());
        }
    }

    @Test
    public void read_whitespaceOnlyOptionalFields_treatsAsEmpty() throws Exception {
        assertEquals(read(CsvExporter.HEADER + EOL + SIMPLE_ROW),
                read(CsvExporter.HEADER + EOL + "Alice,81234567, ,\t, , , , , ,false"));
    }

    @Test
    public void read_blankListMember_rejectsRecord() {
        for (String row : List.of("Alice,81234567,,,alpha; ;beta,,,,,false",
                "Alice,81234567,,,,,,,friends; \t;work,false")) {
            IllegalValueException error = assertThrows(IllegalValueException.class, () ->
                    read(CsvExporter.HEADER + EOL + row));
            assertTrue(error.getMessage().contains(CsvImporter.MESSAGE_EMPTY_MEMBER));
        }
    }

    @Test
    public void read_optionalBomAndLineEndings_acceptsBoth() throws Exception {
        for (String bom : List.of("", "\ufeff")) {
            for (String separator : List.of("\n", EOL)) {
                List<Person> persons = read(bom + CsvExporter.HEADER + separator + SIMPLE_ROW);
                assertEquals(1, persons.size());
                assertTrue(persons.getFirst().getTags().isEmpty());
                assertTrue(persons.getFirst().getGroups().isEmpty());
            }
        }
    }

    @Test
    public void read_quotedLineBreak_keepsOneRecord() throws Exception {
        // Group currently permits line breaks; CSV must not split the quoted cell into records.
        String row = "Alice,81234567,,,\"alpha\r\nbeta\",,,,,true";
        assertThrows(IllegalValueException.class, () -> read(CsvExporter.HEADER + EOL + row));
    }

    @Test
    public void read_formulaProtection_preservesApostrophe() throws Exception {
        Path file = folder.resolve("formula.csv");
        CsvExporter.write(List.of(new PersonBuilder(ALICE).withAddress("sum(a1)").build()), file);
        assertEquals("sum(a1)", CsvImporter.read(file).getFirst().getAddress().value);
    }

    @Test
    public void read_headerOnly_returnsEmptyList() throws Exception {
        assertEquals(List.of(), read(CsvExporter.HEADER + EOL));
    }

    @Test
    public void read_wrongOrMissingHeader_rejectsFile() {
        for (String text : List.of("", "name,phone" + EOL + SIMPLE_ROW, SIMPLE_ROW)) {
            assertEquals(CsvImporter.MESSAGE_HEADER,
                    assertThrows(IllegalValueException.class, () -> read(text)).getMessage());
        }
    }

    @Test
    public void read_invalidRecords_reportsRecordNumber() {
        for (String row : List.of("Alice,81234567", SIMPLE_ROW + ",extra",
                SIMPLE_ROW.replace("false", "maybe"), SIMPLE_ROW.replace("81234567", "abc"),
                ",81234567,,,,,,,,false", "Alice,81234567,,,alpha;;beta,,,,,false")) {
            IllegalValueException error = assertThrows(IllegalValueException.class, () ->
                    read(CsvExporter.HEADER + EOL + SIMPLE_ROW + EOL + row));
            assertTrue(error.getMessage().startsWith("Invalid CSV record 3:"), error.getMessage());
        }
    }

    @Test
    public void read_malformedQuotes_rejectsRecord() {
        for (String row : List.of("\"Alice", "Al\"ice,81234567,,,,,,,,false",
                "\"Alice\"x,81234567,,,,,,,,false")) {
            assertEquals(String.format(CsvImporter.MESSAGE_RECORD, 2, CsvImporter.MESSAGE_QUOTES),
                    assertThrows(IllegalValueException.class, () ->
                            read(CsvExporter.HEADER + EOL + row)).getMessage());
        }
    }

    @Test
    public void read_missingFile_throwsIoException() {
        assertThrows(IOException.class, () -> CsvImporter.read(folder.resolve("missing.csv")));
    }

    @Test
    public void read_nullFile_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> CsvImporter.read(null));
    }

    private List<Person> read(String text) throws Exception {
        Path file = folder.resolve("input.csv");
        Files.writeString(file, text);
        return CsvImporter.read(file);
    }
}
