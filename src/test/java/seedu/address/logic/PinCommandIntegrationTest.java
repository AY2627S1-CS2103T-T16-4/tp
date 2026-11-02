package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BENSON;
import static seedu.address.testutil.TypicalPersons.CARL;

import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.PinCommand;
import seedu.address.logic.commands.UndoCommand;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.AddressBook;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;

/**
 * Verifies pinning through command dispatch, real JSON storage, filtered views, and undo history.
 */
public class PinCommandIntegrationTest {
    private static final Person PINNED_ALICE = new Person(ALICE, true);
    private static final Person PINNED_BENSON = new Person(BENSON, true);
    private static final String PIN_BENSON = PinCommand.COMMAND_WORD + " p/" + BENSON.getPhone().value;
    private static final String FIND_ALICE = "find p/" + ALICE.getPhone().value;

    @TempDir
    public Path temporaryFolder;

    private final ModelManager model = new ModelManager();
    private JsonAddressBookStorage addressBookStorage;
    private Logic logic;

    @BeforeEach
    public void setUp() throws Exception {
        addressBookStorage = new JsonAddressBookStorage(temporaryFolder.resolve("addressbook.json"));
        StorageManager storage = new StorageManager(addressBookStorage,
                new JsonUserPrefsStorage(temporaryFolder.resolve("preferences.json")));
        logic = new LogicManager(model, storage);
        model.addPerson(ALICE);
        model.addPerson(BENSON);
        addressBookStorage.saveAddressBook(model.getAddressBook());
    }

    @Test
    public void execute_pin_savesDetailsAndRestoresPinnedOrderOnReload() throws Exception {
        CommandResult result = logic.execute(PIN_BENSON);

        assertEquals(String.format(PinCommand.MESSAGE_PIN_PERSON_SUCCESS, Messages.format(PINNED_BENSON)),
                result.getFeedbackToUser());
        assertTrue(result.shouldResetContactSelection());
        assertSavedPersons(ALICE, PINNED_BENSON);
        assertEquals(List.of(PINNED_BENSON, ALICE), model.getFilteredPersonList());

        ModelManager reloadedModel = new ModelManager(addressBookStorage.readAddressBook().orElseThrow(),
                new UserPrefs());
        assertEquals(List.of(ALICE, PINNED_BENSON), reloadedModel.getAddressBook().getPersonList());
        assertEquals(List.of(PINNED_BENSON, ALICE), reloadedModel.getFilteredPersonList());
    }

    @Test
    public void execute_pinThenUndo_restoresDetailsAndOrderAndSaves() throws Exception {
        logic.execute(PIN_BENSON);

        assertOriginalStateAfterUndo();
        assertEquals(List.of(ALICE, BENSON), model.getFilteredPersonList());
    }

    @Test
    public void execute_pinHiddenContact_preservesFilterAndSaves() throws Exception {
        logic.execute(FIND_ALICE);
        assertEquals(List.of(ALICE), model.getFilteredPersonList());

        logic.execute(PIN_BENSON);

        assertEquals(List.of(ALICE), model.getFilteredPersonList());
        assertSavedPersons(ALICE, PINNED_BENSON);
        logic.execute("list");
        assertEquals(List.of(PINNED_BENSON, ALICE), model.getFilteredPersonList());
    }

    @Test
    public void execute_pinThenUndo_preservesActiveFilterAndSort() throws Exception {
        model.addPerson(CARL);
        logic.execute("sort name desc");
        logic.execute("find t/friends");
        assertEquals(List.of(BENSON, ALICE), model.getFilteredPersonList());

        logic.execute(PinCommand.COMMAND_WORD + " p/" + ALICE.getPhone().value);

        assertEquals(List.of(PINNED_ALICE, BENSON), model.getFilteredPersonList());
        assertSavedPersons(PINNED_ALICE, BENSON, CARL);

        logic.execute(UndoCommand.COMMAND_WORD);

        assertEquals(List.of(BENSON, ALICE), model.getFilteredPersonList());
        assertSavedPersons(ALICE, BENSON, CARL);
        logic.execute("list");
        assertEquals(List.of(CARL, BENSON, ALICE), model.getFilteredPersonList());
        assertFalse(model.undo());
    }

    @Test
    public void execute_alreadyPinned_preservesDataAndPreviousUndoState() throws Exception {
        assertRejectedPinPreservesUndo(PIN_BENSON, PinCommand.MESSAGE_ALREADY_PINNED, List.of(ALICE));
    }

    @Test
    public void execute_unmatchedPin_preservesDataAndPreviousUndoState() throws Exception {
        assertRejectedPinPreservesUndo("pin n/Missing Person", PinCommand.MESSAGE_PERSON_NOT_FOUND, List.of(ALICE));
    }

    @Test
    public void execute_ambiguousPin_displaysMatchesAndPreservesPreviousUndoState() throws Exception {
        assertRejectedPinPreservesUndo("pin g/t09", PinCommand.MESSAGE_AMBIGUOUS_PERSON,
                List.of(PINNED_BENSON, ALICE));
    }

    @Test
    public void execute_invalidPin_preservesDataAndPreviousUndoState() throws Exception {
        logic.execute(PIN_BENSON);
        logic.execute(FIND_ALICE);

        assertThrows(ParseException.class, () -> logic.execute("pin p/" + ALICE.getPhone().value
                + " p/" + BENSON.getPhone().value));

        assertSavedPersons(ALICE, PINNED_BENSON);
        assertEquals(List.of(ALICE), model.getFilteredPersonList());
        assertOriginalStateAfterUndo();
    }

    private void assertRejectedPinPreservesUndo(String command, String expectedMessage,
            List<Person> expectedDisplayedPersons) throws Exception {
        logic.execute(PIN_BENSON);
        logic.execute(FIND_ALICE);
        AddressBook previousData = new AddressBook(model.getAddressBook());

        assertThrows(CommandException.class, expectedMessage, () -> logic.execute(command));

        assertEquals(previousData, model.getAddressBook());
        assertSavedPersons(ALICE, PINNED_BENSON);
        assertEquals(expectedDisplayedPersons, model.getFilteredPersonList());
        assertOriginalStateAfterUndo();
    }

    private void assertOriginalStateAfterUndo() throws Exception {
        CommandResult result = logic.execute(UndoCommand.COMMAND_WORD);

        assertEquals(UndoCommand.MESSAGE_SUCCESS, result.getFeedbackToUser());
        assertTrue(result.shouldResetContactSelection());
        assertSavedPersons(ALICE, BENSON);
        assertFalse(model.undo());
    }

    private void assertSavedPersons(Person... expectedPersons) throws Exception {
        assertEquals(List.of(expectedPersons), model.getAddressBook().getPersonList());
        assertEquals(List.of(expectedPersons), addressBookStorage.readAddressBook().orElseThrow().getPersonList());
    }
}
