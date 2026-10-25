package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BENSON;
import static seedu.address.testutil.TypicalPersons.CARL;

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
import seedu.address.storage.CsvExporter;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;

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
}
