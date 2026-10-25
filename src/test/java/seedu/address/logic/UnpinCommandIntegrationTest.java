package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.PHONE_DESC_AMY;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.AMY;

import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.UndoCommand;
import seedu.address.logic.commands.UnpinCommand;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.ModelManager;
import seedu.address.model.person.Person;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;

/** Verifies unpin through command dispatch, undo history, and real JSON storage. */
public class UnpinCommandIntegrationTest {
    @TempDir
    public Path temporaryFolder;

    private final ModelManager model = new ModelManager();
    private JsonAddressBookStorage addressBookStorage;
    private Logic logic;

    @BeforeEach
    public void setUp() {
        addressBookStorage = new JsonAddressBookStorage(temporaryFolder.resolve("addressbook.json"));
        StorageManager storage = new StorageManager(addressBookStorage,
                new JsonUserPrefsStorage(temporaryFolder.resolve("preferences.json")));
        logic = new LogicManager(model, storage);
    }

    @Test
    public void execute_unpin_savesDataAndRequestsSelectionReset() throws Exception {
        model.addPerson(new Person(AMY, true));

        CommandResult result = logic.execute(UnpinCommand.COMMAND_WORD + PHONE_DESC_AMY);

        assertTrue(result.shouldResetContactSelection());
        assertEquals(List.of(AMY), model.getAddressBook().getPersonList());
        assertEquals(model.getAddressBook(), addressBookStorage.readAddressBook().orElseThrow());
    }

    @Test
    public void execute_unpinThenUndo_restoresPinnedStatusAndSaves() throws Exception {
        Person pinnedAmy = new Person(AMY, true);
        model.addPerson(pinnedAmy);
        logic.execute(UnpinCommand.COMMAND_WORD + PHONE_DESC_AMY);

        CommandResult result = logic.execute(UndoCommand.COMMAND_WORD);

        assertEquals(UndoCommand.MESSAGE_SUCCESS, result.getFeedbackToUser());
        assertEquals(List.of(pinnedAmy), model.getAddressBook().getPersonList());
        assertEquals(model.getAddressBook(), addressBookStorage.readAddressBook().orElseThrow());
        assertFalse(model.undo());
    }

    @Test
    public void execute_alreadyUnpinned_doesNotCreateUndoHistory() {
        model.addPerson(AMY);
        assertThrows(CommandException.class, UnpinCommand.MESSAGE_ALREADY_UNPINNED, () ->
                logic.execute(UnpinCommand.COMMAND_WORD + PHONE_DESC_AMY));
        assertFalse(model.undo());
        assertEquals(List.of(AMY), model.getAddressBook().getPersonList());
    }
}
