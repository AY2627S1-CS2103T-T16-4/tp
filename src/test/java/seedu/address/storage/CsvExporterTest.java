package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

public class CsvExporterTest {

    private static final String BOM = "\uFEFF";
    private static final String EOL = "\r\n";

    @TempDir
    public Path testFolder;

    @Test
    public void toCsv_emptyList_headerOnly() {
        assertEquals(BOM + CsvExporter.HEADER + EOL, CsvExporter.toCsv(Collections.emptyList()));
    }

    @Test
    public void toCsv_singlePerson_allColumnsInHeaderOrder() {
        Person person = new PersonBuilder().withName("Amy Bee").withPhone("85355255").withEmail("amy@gmail.com")
                .withAddress("Block 123").withGroups("CS2103").withGithub("amybee").withLinkedin("amy-bee")
                .withTelegram("amy_bee").withTags("friend").build();

        assertEquals(BOM + CsvExporter.HEADER + EOL
                + "AMY BEE,85355255,amy@gmail.com,block 123,cs2103,amybee,amy-bee,amy_bee,friend,false" + EOL,
                CsvExporter.toCsv(List.of(person)));
    }

    @Test
    public void toCsv_pinnedPerson_pinnedColumnTrue() {
        Person pinned = new Person(new PersonBuilder().build(), true);

        assertTrue(CsvExporter.toCsv(List.of(pinned)).endsWith(",true" + EOL));
    }

    @Test
    public void toCsv_multipleValues_joinedAlphabetically() {
        Person person = new PersonBuilder().withGroups("Beta", "Alpha").withTags("zeta", "alpha").build();

        String csv = CsvExporter.toCsv(List.of(person));

        assertTrue(csv.contains(",alpha;beta,"));
        assertTrue(csv.contains(",alpha;zeta,"));
    }

    @Test
    public void toCsv_preservesInputOrder() {
        Person first = new PersonBuilder().withName("Zed").build();
        Person second = new PersonBuilder().withName("Abe").build();

        String[] lines = CsvExporter.toCsv(List.of(first, second)).split(EOL);

        assertTrue(lines[1].startsWith("ZED,"));
        assertTrue(lines[2].startsWith("ABE,"));
    }

    @Test
    public void toCsv_formulaCharacterNotAtStart_unchanged() {
        Person person = new PersonBuilder().withAddress("ab").build();

        assertTrue(CsvExporter.toCsv(List.of(person)).contains(",ab,"));
    }

    @Test
    public void write_validList_writesUtf8FileWithBom() throws IOException {
        Path file = testFolder.resolve("contacts.csv");
        List<Person> persons = List.of(new PersonBuilder().withAddress("junhan funky street").build());

        CsvExporter.write(persons, file);

        String content = Files.readString(file, StandardCharsets.UTF_8);
        assertEquals(CsvExporter.toCsv(persons), content);
        assertTrue(content.contains("junhan funky street"));
        assertEquals(0xEF, Files.readAllBytes(file)[0] & 0xFF);
    }

    @Test
    public void write_missingParentFolder_createsIt() throws IOException {
        Path file = testFolder.resolve("data").resolve("nested").resolve("contacts.csv");

        CsvExporter.write(Collections.emptyList(), file);

        assertTrue(Files.exists(file));
    }

    @Test
    public void write_fileAlreadyExists_throwsAndKeepsOriginal() throws IOException {
        Path file = testFolder.resolve("contacts.csv");
        Files.writeString(file, "original");

        assertThrows(FileAlreadyExistsException.class, () -> CsvExporter.write(Collections.emptyList(), file));
        assertEquals("original", Files.readString(file));
    }

    @Test
    public void write_parentIsRegularFile_throwsIoException() throws IOException {
        Path blocker = testFolder.resolve("data");
        Files.writeString(blocker, "not a folder");

        assertThrows(IOException.class, () ->
                CsvExporter.write(Collections.emptyList(), blocker.resolve("contacts.csv")));
        assertFalse(Files.isDirectory(blocker));
    }

    @Test
    public void write_nullArguments_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> CsvExporter.write(null, testFolder.resolve("a.csv")));
        assertThrows(NullPointerException.class, () -> CsvExporter.write(Collections.emptyList(), null));
    }
}
