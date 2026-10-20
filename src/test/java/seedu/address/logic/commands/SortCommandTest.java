package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

import javafx.collections.ObservableList;
import seedu.address.logic.commands.SortCommand.Field;
import seedu.address.logic.parser.AddressBookParser;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

public class SortCommandTest {
    private final Model model = new ModelManager();
    private final Person amy = new PersonBuilder().withName("amy").withPhone("81111111")
            .withGroups("t02", "t01").build();
    private final Person bob = new PersonBuilder().withName("Bob").withPhone("82222222").withGroups("t01").build();
    private final Person carl = new PersonBuilder().withName("Carl").withPhone("83333333").withGroups("t02").build();
    private final Person dan = new PersonBuilder().withName("Dan").withPhone("84444444").withGroups().build();

    private void addContacts() {
        model.addPerson(dan);
        model.addPerson(carl);
        model.addPerson(bob);
        model.addPerson(amy);
    }

    @Test
    public void execute_name_sortsLiveViewWithoutChangingStoredOrder() {
        addContacts();
        ObservableList<Person> view = model.getFilteredPersonList();
        CommandResult result = new SortCommand(Field.NAME, false).execute(model);
        assertEquals("Sorted contacts by name (ascending).", result.getFeedbackToUser());
        assertEquals(List.of(amy, bob, carl, dan), view);
        new SortCommand(Field.NAME, true).execute(model);
        assertEquals(List.of(dan, carl, bob, amy), view);
        assertEquals(List.of(dan, carl, bob, amy), model.getAddressBook().getPersonList());
        assertFalse(model.undo());
    }

    @Test
    public void execute_group_handlesMultipleGroupsTiesAndUngroupedContacts() {
        addContacts();
        new SortCommand(Field.GROUP, false).execute(model);
        assertEquals(List.of(amy, bob, carl, dan), model.getFilteredPersonList());
        new SortCommand(Field.GROUP, true).execute(model);
        assertEquals(List.of(carl, amy, bob, dan), model.getFilteredPersonList());
    }

    @Test
    public void execute_afterFind_preservesMatches() throws Exception {
        addContacts();
        AddressBookParser parser = new AddressBookParser();
        parser.parseCommand("find g/t01").execute(model);
        parser.parseCommand("sort name asc").execute(model);
        assertEquals(List.of(amy, bob), model.getFilteredPersonList());
        parser.parseCommand("sort group desc").execute(model);
        assertEquals(List.of(amy, bob), model.getFilteredPersonList());
    }

    @Test
    public void execute_partialMatchFilter_preservesFilterAcrossUpdatesAndList() {
        addContacts();
        model.updateFilteredPersonList(person -> person.getName().fullName.toLowerCase().contains("a"));
        new SortCommand(Field.NAME, false).execute(model);
        assertEquals(List.of(amy, carl, dan), model.getFilteredPersonList());
        Person aaron = new PersonBuilder(bob).withName("Aaron").build();
        model.setPerson(bob, aaron);
        assertEquals(List.of(aaron, amy, carl, dan), model.getFilteredPersonList());
        model.deletePerson(carl);
        assertEquals(List.of(aaron, amy, dan), model.getFilteredPersonList());
        new ListCommand().execute(model);
        assertEquals(List.of(aaron, amy, dan), model.getFilteredPersonList());
    }

    @Test
    public void execute_editAfterSort_usesDisplayedIndex() throws Exception {
        addContacts();
        AddressBookParser parser = new AddressBookParser();
        parser.parseCommand("sort name").execute(model);
        parser.parseCommand("edit 1 n/Zoe").execute(model);
        assertEquals(List.of(bob, carl, dan, new PersonBuilder(amy).withName("Zoe").build()),
                model.getFilteredPersonList());
    }

    @Test
    public void execute_emptyAndSingleContact_success() {
        new SortCommand(Field.GROUP, true).execute(model);
        assertEquals(List.of(), model.getFilteredPersonList());
        model.addPerson(amy);
        new SortCommand(Field.NAME, false).execute(model);
        assertEquals(List.of(amy), model.getFilteredPersonList());
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
