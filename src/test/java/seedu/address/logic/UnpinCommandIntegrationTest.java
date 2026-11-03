package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.PHONE_DESC_AMY;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.AMY;
import static seedu.address.testutil.TypicalPersons.BENSON;

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
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;
import seedu.address.testutil.PersonBuilder;

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

    @Test
    public void execute_ambiguousUnpin_preservesPreviousUndoEntry() throws Exception {
        Person other = new PersonBuilder(BENSON).withName(AMY.getName().fullName).build();
        assertRejectedUnpinPreservesUndo("unpin n/" + AMY.getName().fullName,
                UnpinCommand.MESSAGE_AMBIGUOUS_PERSON, other, List.of(new Person(other, true), AMY));
    }

    @Test
    public void execute_alreadyUnpinned_preservesPreviousUndoEntry() throws Exception {
        assertRejectedUnpinPreservesUndo("unpin" + PHONE_DESC_AMY,
                UnpinCommand.MESSAGE_ALREADY_UNPINNED, BENSON, List.of(AMY));
    }

    @Test
    public void execute_noMatch_preservesPreviousUndoEntry() throws Exception {
        assertRejectedUnpinPreservesUndo("unpin n/Missing Person",
                UnpinCommand.MESSAGE_PERSON_NOT_FOUND, BENSON, List.of(AMY));
    }

    @Test
    public void execute_unpin_reloadPreservesDetailsAndPinnedPriority() throws Exception {
        Person pinnedBenson = new Person(BENSON, true);
        model.addPerson(new Person(AMY, true));
        model.addPerson(pinnedBenson);

        logic.execute("unpin" + PHONE_DESC_AMY);

        ModelManager reloaded = new ModelManager(addressBookStorage.readAddressBook().orElseThrow(), new UserPrefs());
        assertEquals(List.of(AMY, pinnedBenson), reloaded.getAddressBook().getPersonList());
        assertEquals(List.of(pinnedBenson, AMY), reloaded.getFilteredPersonList());
    }

    /** Checks a rejected request against a nonempty undo stack and the JSON saved by the preceding unpin. */
    private void assertRejectedUnpinPreservesUndo(String command, String message, Person other,
            List<Person> expectedVisible) throws Exception {
        Person pinnedAmy = new Person(AMY, true);
        Person pinnedOther = new Person(other, true);
        model.addPerson(pinnedAmy);
        model.addPerson(pinnedOther);
        logic.execute("unpin" + PHONE_DESC_AMY);
        logic.execute("find p/" + AMY.getPhone().value);
        assertEquals(List.of(AMY), model.getFilteredPersonList());

        // Matching uses all contacts: the ambiguous case must include the hidden, still-pinned contact.
        assertThrows(CommandException.class, message, () -> logic.execute(command));

        assertEquals(List.of(AMY, pinnedOther), model.getAddressBook().getPersonList());
        assertEquals(model.getAddressBook(), addressBookStorage.readAddressBook().orElseThrow());
        assertEquals(expectedVisible, model.getFilteredPersonList());
        assertEquals(UndoCommand.MESSAGE_SUCCESS, logic.execute("undo").getFeedbackToUser());
        assertEquals(List.of(pinnedAmy, pinnedOther), model.getAddressBook().getPersonList());
        assertEquals(model.getAddressBook(), addressBookStorage.readAddressBook().orElseThrow());
        assertEquals(UndoCommand.MESSAGE_NO_UNDO, logic.execute("undo").getFeedbackToUser());
    }
}
