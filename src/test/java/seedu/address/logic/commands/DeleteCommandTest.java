package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BENSON;
import static seedu.address.testutil.TypicalPersons.DANIEL;
import static seedu.address.testutil.TypicalPersons.DANIEL_TWIN;
import static seedu.address.testutil.TypicalPersons.GEORGE;
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

/**
 * Contains integration tests (interaction with the Model) and unit tests for
 * {@code DeleteCommand}.
 */
public class DeleteCommandTest {

    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
    private Model expectedModel = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void constructor_nullPredicate_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new DeleteCommand(null));
    }

    @Test
    public void execute_nullModel_throwsNullPointerException() {
        DeleteCommand command = new DeleteCommand(
                predicateWith(d -> d.setName(ALICE.getName())));

        assertThrows(NullPointerException.class, () -> command.execute(null));
    }

    @Test
    public void execute_uniqueMatchHiddenByFilter_successAndPreservesFilter() {
        model.updateFilteredPersonList(person -> person.equals(BENSON));
        expectedModel.updateFilteredPersonList(person -> person.equals(BENSON));
        assertEquals(List.of(BENSON), model.getFilteredPersonList());

        PersonMatchesExactFieldsPredicate predicate =
                predicateWith(d -> d.setName(ALICE.getName()));

        assertDeleteSuccess(predicate, ALICE);

        assertEquals(List.of(BENSON), model.getFilteredPersonList());
    }

    @Test
    public void execute_multipleMatchesWithOnlyOneVisible_showsAllWithoutDeleting() {
        model.updateFilteredPersonList(person -> person.equals(DANIEL));
        assertEquals(List.of(DANIEL), model.getFilteredPersonList());

        PersonMatchesExactFieldsPredicate predicate =
                predicateWith(d -> d.setName(DANIEL.getName()));

        assertAmbiguousFailure(predicate, List.of(DANIEL, DANIEL_TWIN));
    }

    @Test
    public void execute_noMatchWithActiveFilter_preservesFilter() {
        model.updateFilteredPersonList(person -> person.equals(BENSON));
        assertEquals(List.of(BENSON), model.getFilteredPersonList());

        PersonMatchesExactFieldsPredicate predicate =
                predicateWith(d -> d.setName(new Name("Unknown Person")));

        assertCommandFailure(new DeleteCommand(predicate), model,
                DeleteCommand.MESSAGE_PERSON_NOT_FOUND);
    }

    @Test
    public void execute_groupOnlyOneMatch_success() {
        AddressBook addressBook = new AddressBook();
        addressBook.addPerson(ALICE);
        addressBook.addPerson(GEORGE);

        model = new ModelManager(addressBook, new UserPrefs());
        expectedModel = new ModelManager(
                new AddressBook(addressBook), new UserPrefs());

        PersonMatchesExactFieldsPredicate predicate =
                predicateWith(d -> d.setGroups(ALICE.getGroups()));

        assertDeleteSuccess(predicate, ALICE);
    }

    @Test
    public void execute_personWithAllOptionalFieldsBlank_success() {
        PersonMatchesExactFieldsPredicate predicate =
                predicateWith(d -> d.setPhone(GEORGE.getPhone()));

        assertDeleteSuccess(predicate, GEORGE);
    }

    @Test
    public void execute_nameOnlyOneMatch_success() {
        PersonMatchesExactFieldsPredicate predicate =
                predicateWith(d -> d.setName(ALICE.getName()));

        assertDeleteSuccess(predicate, ALICE);
    }

    @Test
    public void execute_phoneOnlyOneMatch_success() {
        PersonMatchesExactFieldsPredicate predicate =
                predicateWith(d -> d.setPhone(ALICE.getPhone()));

        assertDeleteSuccess(predicate, ALICE);
    }

    @Test
    public void execute_emailOnlyOneMatch_success() {
        PersonMatchesExactFieldsPredicate predicate =
                predicateWith(d -> d.setEmail(ALICE.getEmail()));

        assertDeleteSuccess(predicate, ALICE);
    }

    @Test
    public void execute_addressOnlyOneMatch_success() {
        PersonMatchesExactFieldsPredicate predicate =
                predicateWith(d -> d.setAddress(ALICE.getAddress()));

        assertDeleteSuccess(predicate, ALICE);
    }

    @Test
    public void execute_githubOnlyOneMatch_success() {
        PersonMatchesExactFieldsPredicate predicate =
                predicateWith(d -> d.setGithub(ALICE.getGithub()));

        assertDeleteSuccess(predicate, ALICE);
    }

    @Test
    public void execute_linkedinOnlyOneMatch_success() {
        PersonMatchesExactFieldsPredicate predicate =
                predicateWith(d -> d.setLinkedin(ALICE.getLinkedin()));

        assertDeleteSuccess(predicate, ALICE);
    }

    @Test
    public void execute_telegramOnlyOneMatch_success() {
        PersonMatchesExactFieldsPredicate predicate =
                predicateWith(d -> d.setTelegram(ALICE.getTelegram()));

        assertDeleteSuccess(predicate, ALICE);
    }

    @Test
    public void execute_multipleFlagsOneMatch_success() {
        // ALICE, BENSON and JACK share a group, but only JACK has this tag.
        PersonMatchesExactFieldsPredicate predicate = predicateWith(d -> {
            d.setGroups(JACK.getGroups());
            d.setTags(JACK.getTags());
        });

        assertDeleteSuccess(predicate, JACK);
    }

    @Test
    public void execute_multipleTagsOneMatch_success() {
        // Only BENSON has both "friends" and "owesMoney".
        PersonMatchesExactFieldsPredicate predicate =
                predicateWith(d -> d.setTags(BENSON.getTags()));

        assertDeleteSuccess(predicate, BENSON);
    }

    @Test
    public void execute_sameNameAndMatchingPhone_success() {
        // DANIEL and DANIEL_TWIN have the same name.
        PersonMatchesExactFieldsPredicate predicate = predicateWith(d -> {
            d.setName(DANIEL.getName());
            d.setPhone(DANIEL.getPhone());
        });

        assertDeleteSuccess(predicate, DANIEL);
        assertTrue(model.hasPerson(DANIEL_TWIN));
    }

    @Test
    public void execute_unknownName_throwsCommandException() {
        PersonMatchesExactFieldsPredicate predicate =
                predicateWith(d -> d.setName(new Name("Unknown Person")));

        assertCommandFailure(new DeleteCommand(predicate), model,
                DeleteCommand.MESSAGE_PERSON_NOT_FOUND);
    }

    @Test
    public void execute_fieldsBelongingToDifferentPersons_throwsCommandException() {
        PersonMatchesExactFieldsPredicate predicate = predicateWith(d -> {
            d.setName(ALICE.getName());
            d.setPhone(BENSON.getPhone());
        });

        assertCommandFailure(new DeleteCommand(predicate), model,
                DeleteCommand.MESSAGE_PERSON_NOT_FOUND);
    }

    @Test
    public void execute_partialName_throwsCommandException() {
        PersonMatchesExactFieldsPredicate predicate =
                predicateWith(d -> d.setName(new Name("Alice")));

        assertCommandFailure(new DeleteCommand(predicate), model,
                DeleteCommand.MESSAGE_PERSON_NOT_FOUND);
    }

    @Test
    public void execute_emptyAddressBook_throwsCommandException() {
        Model emptyModel = new ModelManager();
        PersonMatchesExactFieldsPredicate predicate =
                predicateWith(d -> d.setName(ALICE.getName()));

        assertCommandFailure(new DeleteCommand(predicate), emptyModel,
                DeleteCommand.MESSAGE_PERSON_NOT_FOUND);
    }

    @Test
    public void execute_multipleMatches_showsMatchesWithoutDeleting() {
        PersonMatchesExactFieldsPredicate predicate =
                predicateWith(d -> d.setGroups(ALICE.getGroups()));

        assertAmbiguousFailure(predicate, List.of(ALICE, BENSON, JACK));
    }

    @Test
    public void execute_sameNameMultipleMatches_showsBothWithoutDeleting() {
        PersonMatchesExactFieldsPredicate predicate =
                predicateWith(d -> d.setName(DANIEL.getName()));

        assertAmbiguousFailure(predicate, List.of(DANIEL, DANIEL_TWIN));
    }

    @Test
    public void hashCode_equalCommands_sameHashCode() {
        DeleteCommand firstCommand = new DeleteCommand(
                predicateWith(d -> d.setName(ALICE.getName())));
        DeleteCommand secondCommand = new DeleteCommand(
                predicateWith(d -> d.setName(ALICE.getName())));

        assertEquals(firstCommand, secondCommand);
        assertEquals(firstCommand.hashCode(), secondCommand.hashCode());
    }

    @Test
    public void equals() {
        PersonMatchesExactFieldsPredicate firstPredicate =
                predicateWith(d -> d.setName(ALICE.getName()));
        PersonMatchesExactFieldsPredicate samePredicate =
                predicateWith(d -> d.setName(ALICE.getName()));
        PersonMatchesExactFieldsPredicate secondPredicate =
                predicateWith(d -> d.setName(BENSON.getName()));

        DeleteCommand firstCommand = new DeleteCommand(firstPredicate);

        assertTrue(firstCommand.equals(firstCommand));
        assertTrue(firstCommand.equals(new DeleteCommand(samePredicate)));
        assertFalse(firstCommand.equals(null));
        assertFalse(firstCommand.equals(1));
        assertFalse(firstCommand.equals(new DeleteCommand(secondPredicate)));

        PersonMatchesExactFieldsPredicate additionalFieldPredicate =
                predicateWith(d -> {
                    d.setName(ALICE.getName());
                    d.setPhone(ALICE.getPhone());
                });

        assertFalse(firstCommand.equals(
                new DeleteCommand(additionalFieldPredicate)));
    }
    @Test
    public void isUndoable_returnsTrue() {
        DeleteCommand command = new DeleteCommand(
                predicateWith(d -> d.setName(ALICE.getName())));

        assertTrue(command.isUndoable());
    }

    @Test
    public void toStringMethod() {
        PersonMatchesExactFieldsPredicate predicate =
                predicateWith(d -> d.setName(ALICE.getName()));
        DeleteCommand command = new DeleteCommand(predicate);

        String expected = DeleteCommand.class.getCanonicalName()
                + "{predicate=" + predicate + "}";

        assertEquals(expected, command.toString());
    }

    /**
     * Asserts that exactly the expected person is deleted and the success
     * message uses the shared person formatter.
     */
    private void assertDeleteSuccess(
            PersonMatchesExactFieldsPredicate predicate, Person personToDelete) {
        DeleteCommand command = new DeleteCommand(predicate);
        expectedModel.deletePerson(personToDelete);

        String expectedMessage = String.format(
                DeleteCommand.MESSAGE_DELETE_PERSON_SUCCESS,
                Messages.format(personToDelete));

        assertCommandSuccess(command, model, expectedMessage, expectedModel);
        assertFalse(model.hasPerson(personToDelete));
    }

    /**
     * Asserts that an ambiguous deletion displays the matches and leaves
     * the address book unchanged.
     */
    private void assertAmbiguousFailure(
            PersonMatchesExactFieldsPredicate predicate, List<Person> expectedPersons) {
        AddressBook originalAddressBook = new AddressBook(model.getAddressBook());
        DeleteCommand command = new DeleteCommand(predicate);

        assertThrows(CommandException.class,
                DeleteCommand.MESSAGE_AMBIGUOUS_PERSON, () -> command.execute(model));

        assertEquals(originalAddressBook, model.getAddressBook());
        assertEquals(expectedPersons, model.getFilteredPersonList());
    }

    /**
     * Builds an exact-match predicate using the same pattern as FindCommandTest.
     */
    private static PersonMatchesExactFieldsPredicate predicateWith(
            Consumer<ExactPersonMatchDescriptor> setup) {
        ExactPersonMatchDescriptor descriptor = new ExactPersonMatchDescriptor();
        setup.accept(descriptor);
        return new PersonMatchesExactFieldsPredicate(descriptor);
    }
}
