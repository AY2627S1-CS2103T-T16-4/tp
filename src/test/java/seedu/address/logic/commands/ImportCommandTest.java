package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BENSON;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.AddressBook;
import seedu.address.model.ModelManager;
import seedu.address.model.SortSettings;
import seedu.address.model.SortSettings.Field;
import seedu.address.model.person.Person;
import seedu.address.storage.CsvExporter;

public class ImportCommandTest {
    @TempDir
    public Path folder;

    private final ModelManager model = new ModelManager();

    @Test
    public void execute_validBatch_preservesFilterSortPreferenceAndPins() throws Exception {
        Person pinnedAlice = new Person(ALICE, true);
        SortSettings settings = new SortSettings(Field.NAME, true);
        model.sortFilteredPersonList(settings, true);
        model.updateFilteredPersonList(person -> person.isSamePerson(BENSON));
        Path file = write(List.of(BENSON, pinnedAlice));

        CommandResult result = new ImportCommand(file).execute(model);

        assertEquals(String.format(ImportCommand.MESSAGE_SUCCESS, 2, file.toAbsolutePath().normalize()),
                result.getFeedbackToUser());
        assertEquals(List.of(BENSON, pinnedAlice), model.getAddressBook().getPersonList());
        assertEquals(List.of(BENSON), model.getFilteredPersonList());
        assertEquals(settings, model.getUserPrefs().getSavedSortSettings());
        model.updateFilteredPersonList(person -> true);
        assertEquals(List.of(pinnedAlice, BENSON), model.getFilteredPersonList());
    }

    @Test
    public void execute_existingDuplicate_rejectsWholeBatch() throws Exception {
        model.addPerson(ALICE);
        AddressBook before = new AddressBook(model.getAddressBook());
        Path file = write(List.of(BENSON, ALICE));
        CommandException error = assertThrows(CommandException.class, () -> new ImportCommand(file).execute(model));
        assertEquals(String.format(ImportCommand.MESSAGE_DUPLICATE, 3, ALICE.getPhone().value), error.getMessage());
        assertEquals(before, model.getAddressBook());
    }

    @Test
    public void execute_duplicateWithinCsv_rejectsWholeBatch() throws Exception {
        Path file = write(List.of(ALICE, ALICE));
        assertThrows(CommandException.class, () -> new ImportCommand(file).execute(model));
        assertTrue(model.getAddressBook().getPersonList().isEmpty());
    }

    @Test
    public void execute_invalidLaterRecord_rejectsWholeBatch() throws Exception {
        Path file = write(List.of(ALICE, BENSON));
        Files.writeString(file, Files.readString(file).replace(BENSON.getPhone().value, "invalid"));
        assertThrows(CommandException.class, () -> new ImportCommand(file).execute(model));
        assertTrue(model.getAddressBook().getPersonList().isEmpty());
    }

    @Test
    public void execute_headerOnly_rejectsEmptyBatch() throws Exception {
        Path file = write(List.of());
        assertEquals(ImportCommand.MESSAGE_EMPTY,
                assertThrows(CommandException.class, () -> new ImportCommand(file).execute(model)).getMessage());
        assertFalse(model.undo());
    }

    @Test
    public void execute_unreadablePath_throwsCommandException() {
        assertThrows(CommandException.class, () -> new ImportCommand(folder.resolve("missing.csv")).execute(model));
        assertThrows(CommandException.class, () -> new ImportCommand(folder).execute(model));
    }

    @Test
    public void equalsAndHashCode() {
        ImportCommand command = new ImportCommand(folder.resolve("input.csv"));
        ImportCommand same = new ImportCommand(folder.resolve("input.csv"));
        assertEquals(command, same);
        assertEquals(command.hashCode(), same.hashCode());
        assertNotEquals(command, new ImportCommand(folder.resolve("other.csv")));
        assertNotEquals(command, null);
        assertTrue(command.isUndoable());
    }

    @Test
    public void constructor_nullFile_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new ImportCommand(null));
    }

    @Test
    public void execute_nullModel_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new ImportCommand(folder).execute(null));
    }

    private Path write(List<Person> persons) throws Exception {
        Path file = folder.resolve("input.csv");
        CsvExporter.write(persons, file);
        return file;
    }
}
