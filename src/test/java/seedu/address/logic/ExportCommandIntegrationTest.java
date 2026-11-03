package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BENSON;
import static seedu.address.testutil.TypicalPersons.CARL;
import static seedu.address.testutil.TypicalPersons.JACK;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.UndoCommand;
import seedu.address.model.ModelManager;
import seedu.address.model.person.Person;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;
import seedu.address.testutil.PersonBuilder;

/** Verifies export together with other commands, using the real parser, model and JSON storage. */
public class ExportCommandIntegrationTest {
    @TempDir
    public Path temporaryFolder;

    private final ModelManager model = new ModelManager();
    private Logic logic;

    @BeforeEach
    public void setUp() {
        StorageManager storage = new StorageManager(
                new JsonAddressBookStorage(temporaryFolder.resolve("addressbook.json")),
                new JsonUserPrefsStorage(temporaryFolder.resolve("preferences.json")));
        logic = new LogicManager(model, storage);
        for (Person person : List.of(ALICE, BENSON, CARL, JACK)) {
            model.addPerson(person);
        }
    }

    @Test
    public void execute_exportAfterFind_exportsAllContactsAndKeepsFilter() throws Exception {
        logic.execute("find n/alice");

        CommandResult result = logic.execute("export");

        assertTrue(result.getFeedbackToUser().startsWith("Exported 4 contact(s) to "));
        assertEquals(4 + 1, Files.readAllLines(exportedCsv()).size());
        assertEquals(List.of(ALICE), model.getFilteredPersonList());
    }

    @Test
    public void execute_exportThenUndo_undoesPreviousChangeAndKeepsFile() throws Exception {
        logic.execute("add n/Zed p/91000009");
        logic.execute("export");
        Path csv = exportedCsv();

        CommandResult result = logic.execute("undo");

        assertEquals(UndoCommand.MESSAGE_SUCCESS, result.getFeedbackToUser());
        assertEquals(4, model.getAddressBook().getPersonList().size());
        assertTrue(Files.exists(csv));
        // The export itself left nothing to undo.
        assertEquals(UndoCommand.MESSAGE_NO_UNDO, logic.execute("undo").getFeedbackToUser());
    }

    @Test
    public void execute_exportAfterPinAndSort_rowsFollowDisplayOrder() throws Exception {
        logic.execute("pin p/" + CARL.getPhone().value);
        logic.execute("sort name desc");

        logic.execute("export");

        List<String> exportedNames = Files.readAllLines(exportedCsv()).stream()
                .skip(1)
                .map(row -> row.substring(0, row.indexOf(',')))
                .toList();
        List<String> displayedNames = model.getFilteredPersonList().stream()
                .map(person -> person.getName().fullName)
                .toList();
        assertEquals(displayedNames, exportedNames);
        assertEquals("CARL KURZ", exportedNames.get(0));
    }

    @Test
    public void execute_exportClearImport_restoresContactsAndPinStatus() throws Exception {
        logic.execute("pin p/" + BENSON.getPhone().value);
        Set<Person> expected = new HashSet<>(model.getAddressBook().getPersonList());
        logic.execute("export");
        Path csv = exportedCsv();

        logic.execute("clear");
        assertTrue(model.getAddressBook().getPersonList().isEmpty());
        logic.execute("import " + csv);

        assertEquals(expected, new HashSet<>(model.getAddressBook().getPersonList()));
    }

    @Test
    public void execute_exportThenImportGithubWithCommaAndQuote_restoresValues() throws Exception {
        model.addPerson(new PersonBuilder().withName("Zed One").withPhone("91000001").withGithub("a,b").build());
        model.addPerson(new PersonBuilder().withName("Zed Two").withPhone("91000002").withGithub("x\"y").build());

        assertExportClearImportRestoresContacts();
    }

    @Test
    public void execute_exportThenImportValuesStartingWithFormulaCharacters_restoresValues() throws Exception {
        model.addPerson(new PersonBuilder().withName("Zed One").withPhone("91000001").withGithub("=cmd")
                .withLinkedin("-alice").withAddress("-1 main street").build());
        model.addPerson(new PersonBuilder().withName("Zed Two").withPhone("91000002").withGithub("@cmd")
                .withLinkedin("-bob").withAddress("-2 main street").build());

        assertExportClearImportRestoresContacts();
    }

    private void assertExportClearImportRestoresContacts() throws Exception {
        Set<Person> expected = new HashSet<>(model.getAddressBook().getPersonList());
        logic.execute("export");
        Path csv = exportedCsv();

        logic.execute("clear");
        logic.execute("import " + csv);

        assertEquals(expected, new HashSet<>(model.getAddressBook().getPersonList()));
    }

    /** Returns the only CSV file next to the data file. */
    private Path exportedCsv() throws IOException {
        try (Stream<Path> files = Files.list(temporaryFolder)) {
            List<Path> csvFiles = files.filter(file -> file.toString().endsWith(".csv")).toList();
            assertEquals(1, csvFiles.size());
            return csvFiles.get(0);
        }
    }
}
