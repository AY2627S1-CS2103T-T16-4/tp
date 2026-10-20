package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BENSON;
import static seedu.address.testutil.TypicalPersons.CARL;
import static seedu.address.testutil.TypicalPersons.DANIEL;

import java.util.List;

import org.junit.jupiter.api.Test;

import javafx.collections.ObservableList;
import seedu.address.logic.commands.SortCommand.Field;
import seedu.address.logic.parser.AddressBookParser;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

public class SortCommandTest {
    private final Model model = new ModelManager();

    private void addContacts() {
        model.addPerson(DANIEL);
        model.addPerson(CARL);
        model.addPerson(BENSON);
        model.addPerson(ALICE);
    }

    @Test
    public void execute_name_sortsLiveViewWithoutChangingStoredOrder() {
        addContacts();
        ObservableList<Person> view = model.getFilteredPersonList();
        CommandResult result = new SortCommand(Field.NAME, false).execute(model);
        assertEquals("Sorted contacts by name (ascending).", result.getFeedbackToUser());
        assertEquals(List.of(ALICE, BENSON, CARL, DANIEL), view);
        assertEquals(List.of(DANIEL, CARL, BENSON, ALICE), model.getAddressBook().getPersonList());
        new SortCommand(Field.NAME, true).execute(model);
        assertEquals(List.of(DANIEL, CARL, BENSON, ALICE), view);
        assertEquals(List.of(DANIEL, CARL, BENSON, ALICE), model.getAddressBook().getPersonList());
        assertFalse(model.undo());
    }

    @Test
    public void execute_group_handlesMultipleGroupsTiesAndUngroupedContacts() {
        addContacts();
        // These variants exercise edge cases that the shared fixtures do not cover.
        Person multipleGroups = new PersonBuilder(ALICE).withGroups("t10", "t09").build();
        Person ungrouped = new PersonBuilder(DANIEL).withGroups().build();
        model.setPerson(ALICE, multipleGroups);
        model.setPerson(DANIEL, ungrouped);
        new SortCommand(Field.GROUP, false).execute(model);
        assertEquals(List.of(multipleGroups, BENSON, CARL, ungrouped), model.getFilteredPersonList());
        new SortCommand(Field.GROUP, true).execute(model);
        assertEquals(List.of(CARL, multipleGroups, BENSON, ungrouped), model.getFilteredPersonList());
    }

    @Test
    public void execute_name_ignoresCase() {
        Person lowercaseAlice = new PersonBuilder(ALICE).withName("alice Pauline").build();
        model.addPerson(BENSON);
        model.addPerson(lowercaseAlice);
        new SortCommand(Field.NAME, false).execute(model);
        assertEquals(List.of(lowercaseAlice, BENSON), model.getFilteredPersonList());
        new SortCommand(Field.NAME, true).execute(model);
        assertEquals(List.of(BENSON, lowercaseAlice), model.getFilteredPersonList());
    }

    @Test
    public void execute_sortThenUndo_restoresDataAndKeepsSortOrder() {
        addContacts();
        model.saveStateForUndo(new AddressBook(model.getAddressBook()));
        model.deletePerson(ALICE);

        SortCommand command = new SortCommand(Field.NAME, false);
        assertFalse(command.isUndoable());
        command.execute(model);

        assertTrue(model.undo());
        assertEquals(List.of(ALICE, BENSON, CARL, DANIEL), model.getFilteredPersonList());
        assertFalse(model.undo());
    }

    @Test
    public void execute_afterFind_preservesMatches() throws Exception {
        addContacts();
        AddressBookParser parser = new AddressBookParser();
        parser.parseCommand("find g/t09").execute(model);
        parser.parseCommand("sort name asc").execute(model);
        assertEquals(List.of(ALICE, BENSON), model.getFilteredPersonList());
        parser.parseCommand("sort group desc").execute(model);
        assertEquals(List.of(ALICE, BENSON), model.getFilteredPersonList());
    }

    @Test
    public void execute_partialMatchFilter_preservesFilterAcrossUpdatesAndList() {
        addContacts();
        model.updateFilteredPersonList(person -> person.getName().fullName.toLowerCase().contains("a"));
        new SortCommand(Field.NAME, false).execute(model);
        assertEquals(List.of(ALICE, CARL, DANIEL), model.getFilteredPersonList());
        Person aaron = new PersonBuilder(BENSON).withName("Aaron").build();
        model.setPerson(BENSON, aaron);
        assertEquals(List.of(aaron, ALICE, CARL, DANIEL), model.getFilteredPersonList());
        model.deletePerson(CARL);
        assertEquals(List.of(aaron, ALICE, DANIEL), model.getFilteredPersonList());
        new ListCommand().execute(model);
        assertEquals(List.of(aaron, ALICE, DANIEL), model.getFilteredPersonList());
    }

    @Test
    public void execute_editAfterSort_usesDisplayedIndex() throws Exception {
        addContacts();
        AddressBookParser parser = new AddressBookParser();
        parser.parseCommand("sort name").execute(model);
        parser.parseCommand("edit 1 n/Zoe").execute(model);
        assertEquals(List.of(BENSON, CARL, DANIEL, new PersonBuilder(ALICE).withName("Zoe").build()),
                model.getFilteredPersonList());
    }

    @Test
    public void execute_emptyAndSingleContact_success() {
        new SortCommand(Field.GROUP, true).execute(model);
        assertEquals(List.of(), model.getFilteredPersonList());
        model.addPerson(ALICE);
        new SortCommand(Field.NAME, false).execute(model);
        assertEquals(List.of(ALICE), model.getFilteredPersonList());
        model.updateFilteredPersonList(person -> false);
        new SortCommand(Field.NAME, true).execute(model);
        assertEquals(List.of(), model.getFilteredPersonList());
    }

    @Test
    public void equalsAndNulls() {
        SortCommand command = new SortCommand(Field.NAME, false);
        assertEquals(command, new SortCommand(Field.NAME, false));
        assertNotEquals(command, new SortCommand(Field.NAME, true));
        assertNotEquals(command, new SortCommand(Field.GROUP, false));
        assertNotEquals(command, null);
        assertThrows(NullPointerException.class, () -> new SortCommand(null, false));
        assertThrows(NullPointerException.class, () -> command.execute(null));
        assertThrows(NullPointerException.class, () -> model.sortFilteredPersonList(null));
    }
}
