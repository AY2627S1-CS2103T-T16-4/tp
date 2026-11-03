package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BENSON;
import static seedu.address.testutil.TypicalPersons.CARL;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.HelpCommand;
import seedu.address.logic.commands.ImportCommand;
import seedu.address.logic.commands.UndoCommand;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.ModelManager;
import seedu.address.model.SortSettings;
import seedu.address.model.SortSettings.Field;
import seedu.address.model.person.Person;
import seedu.address.storage.CsvExporter;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;
import seedu.address.testutil.PersonBuilder;

public class ImportCommandIntegrationTest {
    @TempDir
    public Path folder;

    private final ModelManager model = new ModelManager();
    private JsonAddressBookStorage storage;
    private Logic logic;

    @BeforeEach
    public void setUp() {
        storage = new JsonAddressBookStorage(folder.resolve("addressbook.json"));
        logic = new LogicManager(model, new StorageManager(storage,
                new JsonUserPrefsStorage(folder.resolve("preferences.json"))));
    }

    @Test
    public void execute_importThenUndo_savesBatchAndRestoresPreviousData() throws Exception {
        model.addPerson(CARL);
        Path file = folder.resolve("contacts with spaces.csv");
        CsvExporter.write(List.of(ALICE, BENSON), file);

        CommandResult result = logic.execute(ImportCommand.COMMAND_WORD + " " + file);

        assertTrue(result.shouldResetContactSelection());
        assertEquals(List.of(CARL, ALICE, BENSON), storage.readAddressBook().orElseThrow().getPersonList());
        logic.execute(UndoCommand.COMMAND_WORD);
        assertEquals(List.of(CARL), model.getAddressBook().getPersonList());
        assertEquals(model.getAddressBook(), storage.readAddressBook().orElseThrow());
        assertFalse(model.undo());
    }

    @Test
    public void execute_importPaddedGroup_findMatchesTrimmedGroup() throws Exception {
        Path file = folder.resolve("padded.csv");
        Files.writeString(file, CsvExporter.HEADER + "\nAlice,81234567,,, team ; other ,,,,,false");

        logic.execute(ImportCommand.COMMAND_WORD + " " + file);
        logic.execute("find g/team");

        assertEquals(1, model.getFilteredPersonList().size());
        assertEquals("ALICE", model.getFilteredPersonList().getFirst().getName().fullName);
        assertEquals(model.getAddressBook(), storage.readAddressBook().orElseThrow());
    }

    @Test
    public void execute_failedImport_preservesDataAndUndoHistory() throws Exception {
        model.addPerson(ALICE);
        storage.saveAddressBook(model.getAddressBook());
        Path file = folder.resolve("duplicate.csv");
        CsvExporter.write(List.of(BENSON, ALICE), file);

        assertThrows(CommandException.class, () -> logic.execute(ImportCommand.COMMAND_WORD + " " + file));

        assertEquals(List.of(ALICE), model.getAddressBook().getPersonList());
        assertEquals(model.getAddressBook(), storage.readAddressBook().orElseThrow());
        assertFalse(model.undo());
    }

    @Test
    public void execute_help_includesImportUsage() throws Exception {
        assertTrue(logic.execute(HelpCommand.COMMAND_WORD).getFeedbackToUser().contains(ImportCommand.MESSAGE_USAGE));
    }

    @Test
    public void execute_duplicateImport_preservesPreviousUndoEntry() throws Exception {
        // Seed undo history through command dispatch; direct model setup does not create an undo entry.
        logic.execute("add n/Existing p/91000009");
        List<Person> before = List.copyOf(model.getAddressBook().getPersonList());
        Path file = folder.resolve("duplicate.csv");
        CsvExporter.write(List.of(ALICE, before.getFirst()), file);

        assertThrows(CommandException.class, () -> logic.execute("import " + file));

        assertEquals(before, model.getAddressBook().getPersonList());
        assertEquals(before, storage.readAddressBook().orElseThrow().getPersonList());
        assertEquals(UndoCommand.MESSAGE_SUCCESS, logic.execute("undo").getFeedbackToUser());
        assertTrue(model.getAddressBook().getPersonList().isEmpty());
        assertEquals(model.getAddressBook(), storage.readAddressBook().orElseThrow());
        assertEquals(UndoCommand.MESSAGE_NO_UNDO, logic.execute("undo").getFeedbackToUser());
    }

    @Test
    public void execute_invalidLaterRow_preservesDataViewPreferencesAndUndo() throws Exception {
        model.addPerson(CARL);
        logic.execute("add n/Existing p/91000009");
        logic.execute("sort name desc cont");
        logic.execute("find n/carl");
        List<Person> before = List.copyOf(model.getAddressBook().getPersonList());
        Path file = folder.resolve("invalid.csv");
        CsvExporter.write(List.of(ALICE, BENSON), file);
        // ALICE is valid: rejecting BENSON must also prevent a partial import of ALICE.
        Files.writeString(file, Files.readString(file).replace(BENSON.getPhone().value, "invalid"));

        assertThrows(CommandException.class, () -> logic.execute("import " + file));

        assertEquals(before, model.getAddressBook().getPersonList());
        assertEquals(before, storage.readAddressBook().orElseThrow().getPersonList());
        assertEquals(List.of(CARL), model.getFilteredPersonList());
        assertEquals(new SortSettings(Field.NAME, true), model.getUserPrefs().getSavedSortSettings());
        assertEquals(UndoCommand.MESSAGE_SUCCESS, logic.execute("undo").getFeedbackToUser());
        assertEquals(List.of(CARL), model.getAddressBook().getPersonList());
        assertEquals(List.of(CARL), model.getFilteredPersonList());
        assertEquals(model.getAddressBook(), storage.readAddressBook().orElseThrow());
        assertEquals(UndoCommand.MESSAGE_NO_UNDO, logic.execute("undo").getFeedbackToUser());
    }

    @Test
    public void execute_twoImportsThenUndo_restoresOneBatchAtATime() throws Exception {
        model.addPerson(CARL);
        Path first = folder.resolve("first.csv");
        Path second = folder.resolve("second.csv");
        CsvExporter.write(List.of(ALICE, BENSON), first);
        CsvExporter.write(List.of(new PersonBuilder()
                .withName("Zed").withPhone("91000009").build()), second);
        logic.execute("import " + first);
        logic.execute("import " + second);
        assertEquals(4, model.getAddressBook().getPersonList().size());
        assertEquals(model.getAddressBook(), storage.readAddressBook().orElseThrow());

        // The first batch contains two contacts, but must occupy only one undo entry.
        assertEquals(UndoCommand.MESSAGE_SUCCESS, logic.execute("undo").getFeedbackToUser());
        assertEquals(List.of(CARL, ALICE, BENSON), model.getAddressBook().getPersonList());
        assertEquals(model.getAddressBook(), storage.readAddressBook().orElseThrow());
        assertEquals(UndoCommand.MESSAGE_SUCCESS, logic.execute("undo").getFeedbackToUser());
        assertEquals(List.of(CARL), model.getAddressBook().getPersonList());
        assertEquals(model.getAddressBook(), storage.readAddressBook().orElseThrow());
        assertEquals(UndoCommand.MESSAGE_NO_UNDO, logic.execute("undo").getFeedbackToUser());
    }
}
