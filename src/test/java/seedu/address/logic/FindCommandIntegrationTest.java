package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BENSON;
import static seedu.address.testutil.TypicalPersons.CARL;
import static seedu.address.testutil.TypicalPersons.JACK;

import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.FindCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.AddressBook;
import seedu.address.model.ModelManager;
import seedu.address.model.person.Person;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;

/** Verifies find through command dispatch, the displayed list, undo history, and real JSON storage. */
public class FindCommandIntegrationTest {
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
    public void execute_find_listsMatchesReportsCountAndRequestsSelectionReset() throws Exception {
        CommandResult result = logic.execute("find g/t09");

        assertEquals(String.format(FindCommand.MESSAGE_SUCCESS, 3), result.getFeedbackToUser());
        assertEquals(List.of(ALICE, BENSON, JACK), model.getFilteredPersonList());
        assertTrue(result.shouldResetContactSelection());
    }

    @Test
    public void execute_findNoMatch_reportsFlagsAndShowsEmptyList() throws Exception {
        CommandResult result = logic.execute("find n/zzz");

        assertEquals(String.format(FindCommand.MESSAGE_NOT_FOUND, "n/"), result.getFeedbackToUser());
        assertTrue(model.getFilteredPersonList().isEmpty());
    }

    @Test
    public void execute_findTwice_secondFindReplacesFirstFilter() throws Exception {
        logic.execute("find g/t09");
        logic.execute("find n/carl");

        // CARL is not in group t09, so a filter that narrowed the first one would leave an empty list.
        assertEquals(List.of(CARL), model.getFilteredPersonList());
    }

    @Test
    public void execute_findThenAddThenUndo_newContactStaysHiddenAndFilterIsKept() throws Exception {
        logic.execute("find n/alice");

        logic.execute("add n/Zed p/91000009");
        assertEquals(List.of(ALICE), model.getFilteredPersonList());
        assertEquals(5, model.getAddressBook().getPersonList().size());

        logic.execute("undo");
        assertEquals(List.of(ALICE), model.getFilteredPersonList());
        assertEquals(4, model.getAddressBook().getPersonList().size());
    }

    @Test
    public void execute_findAfterPinAndSort_pinnedFirstThenSortOrder() throws Exception {
        logic.execute("pin p/" + BENSON.getPhone().value);
        logic.execute("sort name desc");

        logic.execute("find g/t09");

        assertEquals(List.of(new Person(BENSON, true), JACK, ALICE), model.getFilteredPersonList());
    }

    @Test
    public void execute_invalidFindAfterValidFind_throwsParseExceptionAndKeepsFilter() throws Exception {
        logic.execute("find n/alice");

        assertThrows(ParseException.class, () -> logic.execute("find x/foo"));

        assertEquals(List.of(ALICE), model.getFilteredPersonList());
    }

    @Test
    public void execute_find_doesNotChangeDataOrCreateUndoHistory() throws Exception {
        AddressBook before = new AddressBook(model.getAddressBook());

        logic.execute("find n/alice");

        assertEquals(before, model.getAddressBook());
        assertFalse(model.undo());
    }

    @Test
    public void execute_findThenList_showsAllContactsAgain() throws Exception {
        logic.execute("find n/alice");

        logic.execute("list");

        assertEquals(List.of(ALICE, BENSON, CARL, JACK), model.getFilteredPersonList());
    }
}
