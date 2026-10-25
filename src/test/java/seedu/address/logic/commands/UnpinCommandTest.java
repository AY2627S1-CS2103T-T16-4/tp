package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BENSON;
import static seedu.address.testutil.TypicalPersons.JACK;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.util.List;
import java.util.function.Consumer;

import org.junit.jupiter.api.Test;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.SortSettings.Field;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.ExactPersonMatchDescriptor;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.PersonMatchesExactFieldsPredicate;

public class UnpinCommandTest {
    private final Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_phoneOnly_preservesFieldsAndStoredOrder() throws Exception {
        model.setPerson(ALICE, new Person(ALICE, true));
        Person pinnedBenson = new Person(BENSON, true);
        model.setPerson(BENSON, pinnedBenson);
        AddressBook expected = new AddressBook(model.getAddressBook());
        expected.setPerson(new Person(ALICE, true), ALICE);

        CommandResult result = commandWith(d -> d.setPhone(ALICE.getPhone())).execute(model);

        assertEquals(String.format(UnpinCommand.MESSAGE_UNPIN_PERSON_SUCCESS, Messages.format(ALICE)),
                result.getFeedbackToUser());
        assertEquals(expected, model.getAddressBook());
        assertEquals(List.of(pinnedBenson, ALICE), model.getFilteredPersonList().subList(0, 2));
    }

    @Test
    public void execute_multipleCriteria_unpinsUniqueMatch() throws Exception {
        model.setPerson(JACK, new Person(JACK, true));
        commandWith(d -> {
            d.setGroups(JACK.getGroups());
            d.setTags(JACK.getTags());
        }).execute(model);
        assertEquals(getTypicalAddressBook(), model.getAddressBook());
    }

    @Test
    public void execute_noMatch_leavesModelUnchanged() {
        assertCommandFailure(commandWith(d -> d.setName(new Name("Unknown Person"))), model,
                UnpinCommand.MESSAGE_PERSON_NOT_FOUND);
    }

    @Test
    public void execute_alreadyUnpinned_leavesModelUnchanged() {
        assertCommandFailure(commandWith(d -> d.setPhone(ALICE.getPhone())), model,
                UnpinCommand.MESSAGE_ALREADY_UNPINNED);
    }

    @Test
    public void execute_ambiguousMatch_displaysAllMatchesWithoutUnpinning() {
        Person pinnedJack = new Person(JACK, true);
        model.setPerson(JACK, pinnedJack);
        AddressBook original = new AddressBook(model.getAddressBook());
        UnpinCommand command = commandWith(d -> d.setGroups(ALICE.getGroups()));

        assertThrows(CommandException.class, UnpinCommand.MESSAGE_AMBIGUOUS_PERSON, () -> command.execute(model));
        assertEquals(original, model.getAddressBook());
        assertEquals(List.of(pinnedJack, ALICE, BENSON), model.getFilteredPersonList());
    }

    @Test
    public void execute_hiddenContact_preservesFilter() throws Exception {
        model.setPerson(ALICE, new Person(ALICE, true));
        model.updateFilteredPersonList(person -> person.equals(BENSON));
        commandWith(d -> d.setPhone(ALICE.getPhone())).execute(model);
        assertEquals(List.of(BENSON), model.getFilteredPersonList());
        assertEquals(getTypicalAddressBook(), model.getAddressBook());
    }

    @Test
    public void execute_sortedView_movesContactIntoSortedUnpinnedSection() throws Exception {
        Model sortedModel = new ModelManager();
        sortedModel.addPerson(new Person(ALICE, true));
        Person pinnedBenson = new Person(BENSON, true);
        sortedModel.addPerson(pinnedBenson);
        sortedModel.addPerson(JACK);
        new SortCommand(Field.NAME, true).execute(sortedModel);

        commandWith(d -> d.setPhone(ALICE.getPhone())).execute(sortedModel);

        assertEquals(List.of(pinnedBenson, JACK, ALICE), sortedModel.getFilteredPersonList());
    }

    @Test
    public void isUndoable_returnsTrue() {
        assertTrue(commandWith(d -> d.setPhone(ALICE.getPhone())).isUndoable());
    }

    @Test
    public void equals() {
        UnpinCommand command = commandWith(d -> d.setPhone(ALICE.getPhone()));
        UnpinCommand sameCommand = commandWith(d -> d.setPhone(ALICE.getPhone()));
        assertEquals(command, command);
        assertEquals(command, sameCommand);
        assertEquals(command.hashCode(), sameCommand.hashCode());
        assertNotEquals(command, commandWith(d -> d.setPhone(BENSON.getPhone())));
        assertNotEquals(command, null);
        assertNotEquals(command, new Object());
    }

    @Test
    public void constructor_nullPredicate_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new UnpinCommand(null));
    }

    @Test
    public void execute_nullModel_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> commandWith(d -> d.setPhone(ALICE.getPhone())).execute(null));
    }

    @Test
    public void toStringMethod() {
        ExactPersonMatchDescriptor descriptor = new ExactPersonMatchDescriptor();
        descriptor.setPhone(ALICE.getPhone());
        PersonMatchesExactFieldsPredicate predicate = new PersonMatchesExactFieldsPredicate(descriptor);
        UnpinCommand command = new UnpinCommand(predicate);
        assertEquals(new ToStringBuilder(command).add("predicate", predicate).toString(), command.toString());
    }

    private static UnpinCommand commandWith(Consumer<ExactPersonMatchDescriptor> setup) {
        ExactPersonMatchDescriptor descriptor = new ExactPersonMatchDescriptor();
        setup.accept(descriptor);
        return new UnpinCommand(new PersonMatchesExactFieldsPredicate(descriptor));
    }
}
