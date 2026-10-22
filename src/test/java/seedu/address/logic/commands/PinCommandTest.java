package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BENSON;
import static seedu.address.testutil.TypicalPersons.JACK;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.util.List;
import java.util.function.Consumer;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.ExactPersonMatchDescriptor;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.PersonMatchesExactFieldsPredicate;

public class PinCommandTest {

    private final Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_phoneOnly_pinsContactsInAdditionOrder() {
        assertPinSuccess(commandWith(d -> d.setPhone(JACK.getPhone())), JACK);
        assertPinSuccess(commandWith(d -> d.setPhone(BENSON.getPhone())), BENSON);
        assertEquals(List.of(new Person(BENSON, true), new Person(JACK, true)),
                model.getFilteredPersonList().subList(0, 2));
        assertEquals(ALICE, model.getFilteredPersonList().get(2));
    }

    @Test
    public void execute_multipleCriteria_pinsUniqueMatch() {
        assertPinSuccess(commandWith(d -> {
            d.setGroups(JACK.getGroups());
            d.setTags(JACK.getTags());
        }), JACK);
    }

    @Test
    public void execute_noMatch_leavesModelUnchanged() {
        assertCommandFailure(commandWith(d -> d.setName(new Name("Unknown Person"))), model,
                PinCommand.MESSAGE_PERSON_NOT_FOUND);
    }

    @Test
    public void execute_ambiguousMatch_displaysMatchesWithoutPinning() {
        Person pinnedJack = new Person(JACK, true);
        model.setPerson(JACK, pinnedJack);
        AddressBook original = new AddressBook(model.getAddressBook());
        PinCommand command = commandWith(d -> d.setGroups(ALICE.getGroups()));

        assertThrows(CommandException.class, PinCommand.MESSAGE_AMBIGUOUS_PERSON, () -> command.execute(model));
        assertEquals(original, model.getAddressBook());
        assertEquals(List.of(pinnedJack, ALICE, BENSON), model.getFilteredPersonList());
    }

    @Test
    public void execute_alreadyPinned_leavesModelUnchanged() {
        model.setPerson(ALICE, new Person(ALICE, true));
        assertCommandFailure(commandWith(d -> d.setPhone(ALICE.getPhone())), model,
                PinCommand.MESSAGE_ALREADY_PINNED);
    }

    @Test
    public void isUndoable_returnsTrue() {
        assertTrue(commandWith(d -> d.setPhone(ALICE.getPhone())).isUndoable());
    }

    private void assertPinSuccess(PinCommand command, Person personToPin) {
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        Person pinnedPerson = new Person(personToPin, true);
        expectedModel.setPerson(personToPin, pinnedPerson);
        assertCommandSuccess(command, model,
                String.format(PinCommand.MESSAGE_PIN_PERSON_SUCCESS, Messages.format(pinnedPerson)), expectedModel);
    }

    private static PinCommand commandWith(Consumer<ExactPersonMatchDescriptor> setup) {
        ExactPersonMatchDescriptor descriptor = new ExactPersonMatchDescriptor();
        setup.accept(descriptor);
        return new PinCommand(new PersonMatchesExactFieldsPredicate(descriptor));
    }
}
