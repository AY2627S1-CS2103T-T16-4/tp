package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BENSON;
import static seedu.address.testutil.TypicalPersons.CARL;

import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.ListCommand;
import seedu.address.logic.commands.UndoCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.ModelManager;
import seedu.address.model.SortSettings;
import seedu.address.model.SortSettings.Field;
import seedu.address.model.person.Person;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;

/** Verifies help, list and sort interactions with filters, preferences, undo history and saved contacts. */
public class ViewCommandIntegrationTest {
    @TempDir
    public Path temporaryFolder;

    private final ModelManager model = new ModelManager();
    private JsonAddressBookStorage storage;
    private Logic logic;

    @BeforeEach
    public void setUp() {
        storage = new JsonAddressBookStorage(temporaryFolder.resolve("addressbook.json"));
        logic = new LogicManager(model, new StorageManager(storage,
                new JsonUserPrefsStorage(temporaryFolder.resolve("preferences.json"))));
        model.addPerson(ALICE);
    }

    @Test
    public void execute_repeatedHelp_preservesFilterAndPreviousUndoEntry() throws Exception {
        logic.execute("add n/Zed p/91000009");
        logic.execute("find n/alice");
        List<Person> before = List.copyOf(model.getAddressBook().getPersonList());

        for (int invocation = 0; invocation < 2; invocation++) {
            CommandResult result = logic.execute("help");
            assertFalse(result.shouldResetContactSelection());
            assertEquals(List.of(ALICE), model.getFilteredPersonList());
            assertEquals(before, model.getAddressBook().getPersonList());
            assertEquals(before, storage.readAddressBook().orElseThrow().getPersonList());
        }

        // Both help calls must leave the add as the next and only action to undo.
        assertEquals(UndoCommand.MESSAGE_SUCCESS, logic.execute("undo").getFeedbackToUser());
        assertEquals(List.of(ALICE), model.getAddressBook().getPersonList());
        assertEquals(List.of(ALICE), model.getFilteredPersonList());
        assertEquals(model.getAddressBook(), storage.readAddressBook().orElseThrow());
        assertEquals(UndoCommand.MESSAGE_NO_UNDO, logic.execute("undo").getFeedbackToUser());
    }

    @Test
    public void execute_repeatedList_restoresAllContactsAndPreservesPersistentSort() throws Exception {
        Person pinnedBenson = new Person(BENSON, true);
        model.addPerson(pinnedBenson);
        model.addPerson(CARL);
        logic.execute("sort name desc cont");
        logic.execute("find n/unknown");
        assertEquals(List.of(), model.getFilteredPersonList());

        // An empty filtered view is not an empty address book. The second list must be idempotent.
        for (int invocation = 0; invocation < 2; invocation++) {
            assertEquals(ListCommand.MESSAGE_SUCCESS, logic.execute("list").getFeedbackToUser());
            assertEquals(List.of(pinnedBenson, CARL, ALICE), model.getFilteredPersonList());
            assertEquals(List.of(ALICE, pinnedBenson, CARL), model.getAddressBook().getPersonList());
            assertEquals(model.getAddressBook(), storage.readAddressBook().orElseThrow());
            assertEquals(new SortSettings(Field.NAME, true), model.getUserPrefs().getSavedSortSettings());
        }
    }

    @Test
    public void execute_listThenUndo_undoesPreviousAddAndKeepsViewUnfiltered() throws Exception {
        logic.execute("add n/Zed p/91000009");
        logic.execute("find n/zed");
        logic.execute("list");
        assertEquals(2, model.getFilteredPersonList().size());

        assertEquals(UndoCommand.MESSAGE_SUCCESS, logic.execute("undo").getFeedbackToUser());
        assertEquals(List.of(ALICE), model.getAddressBook().getPersonList());
        // If undo restored the old Zed filter, the remaining ALICE contact would be hidden.
        assertEquals(List.of(ALICE), model.getFilteredPersonList());
        assertEquals(model.getAddressBook(), storage.readAddressBook().orElseThrow());
        assertEquals(UndoCommand.MESSAGE_NO_UNDO, logic.execute("undo").getFeedbackToUser());
    }

    @Test
    public void execute_invalidSort_preservesFilterOrderAndSavedPreference() throws Exception {
        model.addPerson(BENSON);
        model.addPerson(CARL);
        logic.execute("sort name desc cont");
        logic.execute("find g/t09");

        assertThrows(ParseException.class, () -> logic.execute("sort name sideways cont"));

        assertEquals(List.of(BENSON, ALICE), model.getFilteredPersonList());
        assertEquals(List.of(ALICE, BENSON, CARL), model.getAddressBook().getPersonList());
        assertEquals(model.getAddressBook(), storage.readAddressBook().orElseThrow());
        assertEquals(new SortSettings(Field.NAME, true), model.getUserPrefs().getSavedSortSettings());
        // Listing also proves the prior descending comparator still applies to previously hidden contacts.
        logic.execute("list");
        assertEquals(List.of(CARL, BENSON, ALICE), model.getFilteredPersonList());
    }
}
