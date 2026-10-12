package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.CommandTestUtil.showPersonAtIndex;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BENSON;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;

/**
 * Contains integration tests (interaction with the Model) and unit tests for
 * {@code DeleteCommand}.
 */
public class DeleteCommandTest {

    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_validNameUnfilteredList_success() {
        Person personToDelete = ALICE;
        DeleteCommand deleteCommand =
                new DeleteCommand(personToDelete.getName(), Optional.empty());

        String expectedMessage = String.format(
                DeleteCommand.MESSAGE_DELETE_PERSON_SUCCESS,
                personToDelete.getName(), personToDelete.getPhone());

        ModelManager expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.deletePerson(personToDelete);

        assertCommandSuccess(deleteCommand, model,
                expectedMessage, expectedModel);
    }

    @Test
    public void execute_unknownName_throwsCommandException() {
        DeleteCommand deleteCommand =
                new DeleteCommand(new Name("Unknown Person"), Optional.empty());

        assertCommandFailure(
                deleteCommand, model, DeleteCommand.MESSAGE_PERSON_NOT_FOUND);
    }

    @Test
    public void execute_existingNameOutsideFilteredList_success() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON); // Shows Alice

        DeleteCommand deleteCommand =
                new DeleteCommand(BENSON.getName(), Optional.empty());

        Model expectedModel =
                new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.deletePerson(BENSON);
        showPersonAtIndex(expectedModel, INDEX_FIRST_PERSON);

        String expectedMessage = String.format(
                DeleteCommand.MESSAGE_DELETE_PERSON_SUCCESS,
                BENSON.getName(), BENSON.getPhone());

        assertCommandSuccess(deleteCommand, model,
                expectedMessage, expectedModel);
    }

    @Test
    public void execute_matchingNameAndPhone_success() {
        DeleteCommand deleteCommand = new DeleteCommand(
                ALICE.getName(), Optional.of(ALICE.getPhone()));

        String expectedMessage = String.format(
                DeleteCommand.MESSAGE_DELETE_PERSON_SUCCESS,
                ALICE.getName(), ALICE.getPhone());

        Model expectedModel =
                new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.deletePerson(ALICE);

        assertCommandSuccess(deleteCommand, model,
                expectedMessage, expectedModel);
    }

    @Test
    public void execute_matchingNameWrongPhone_throwsCommandException() {
        DeleteCommand deleteCommand = new DeleteCommand(
                ALICE.getName(), Optional.of(BENSON.getPhone()));

        assertCommandFailure(
                deleteCommand, model, DeleteCommand.MESSAGE_PERSON_NOT_FOUND);
    }

    @Test
    public void execute_wrongNameMatchingPhone_throwsCommandException() {
        DeleteCommand deleteCommand = new DeleteCommand(
                new Name("Unknown Person"), Optional.of(ALICE.getPhone()));

        assertCommandFailure(
                deleteCommand, model, DeleteCommand.MESSAGE_PERSON_NOT_FOUND);
    }

    @Test
    public void execute_visibleNameFilteredList_success() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);

        DeleteCommand deleteCommand =
                new DeleteCommand(ALICE.getName(), Optional.empty());

        String expectedMessage = String.format(
                DeleteCommand.MESSAGE_DELETE_PERSON_SUCCESS,
                ALICE.getName(), ALICE.getPhone());

        Model expectedModel =
                new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.deletePerson(ALICE);
        showNoPerson(expectedModel);

        assertCommandSuccess(deleteCommand, model,
                expectedMessage, expectedModel);
    }

    @Test
    public void execute_emptyAddressBook_throwsCommandException() {
        Model emptyModel = new ModelManager();
        DeleteCommand deleteCommand =
                new DeleteCommand(ALICE.getName(), Optional.empty());

        assertCommandFailure(
                deleteCommand, emptyModel, DeleteCommand.MESSAGE_PERSON_NOT_FOUND);
    }

    @Test
    public void execute_partialName_throwsCommandException() {
        DeleteCommand deleteCommand =
                new DeleteCommand(new Name("Alice"), Optional.empty());

        assertCommandFailure(
                deleteCommand, model, DeleteCommand.MESSAGE_PERSON_NOT_FOUND);
    }

    @Test
    public void execute_differentNameCase_success() {
        DeleteCommand deleteCommand =
                new DeleteCommand(new Name("alice pauline"), Optional.empty());

        String expectedMessage = String.format(
                DeleteCommand.MESSAGE_DELETE_PERSON_SUCCESS,
                ALICE.getName(), ALICE.getPhone());

        Model expectedModel =
                new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.deletePerson(ALICE);

        assertCommandSuccess(deleteCommand, model,
                expectedMessage, expectedModel);
    }

    @Test
    public void equals() {
        DeleteCommand deleteFirstCommand =
                new DeleteCommand(ALICE.getName(), Optional.empty());
        DeleteCommand deleteSecondCommand =
                new DeleteCommand(BENSON.getName(), Optional.empty());

        // same object -> returns true
        assertTrue(deleteFirstCommand.equals(deleteFirstCommand));

        // same values -> returns true
        DeleteCommand deleteFirstCommandCopy =
                new DeleteCommand(ALICE.getName(), Optional.empty());
        assertTrue(deleteFirstCommand.equals(deleteFirstCommandCopy));

        // different types -> returns false
        assertFalse(deleteFirstCommand.equals(1));

        // null -> returns false
        assertFalse(deleteFirstCommand.equals(null));

        // different person -> returns false
        assertFalse(deleteFirstCommand.equals(deleteSecondCommand));

        DeleteCommand withPhone = new DeleteCommand(
                ALICE.getName(), Optional.of(ALICE.getPhone()));

        // Same name and phone
        assertTrue(withPhone.equals(new DeleteCommand(
                ALICE.getName(), Optional.of(ALICE.getPhone()))));

        // Same name, phone omitted
        assertFalse(withPhone.equals(new DeleteCommand(
                ALICE.getName(), Optional.empty())));

        // Same name, different phone
        assertFalse(withPhone.equals(new DeleteCommand(
                ALICE.getName(), Optional.of(BENSON.getPhone()))));

        // Different name, same phone
        assertFalse(withPhone.equals(new DeleteCommand(
                BENSON.getName(), Optional.of(ALICE.getPhone()))));
    }

    @Test
    public void toStringMethod() {
        Name targetName = ALICE.getName();
        Optional<Phone> targetPhone = Optional.of(ALICE.getPhone());
        DeleteCommand deleteCommand = new DeleteCommand(targetName, targetPhone);

        String expected = DeleteCommand.class.getCanonicalName()
                + "{targetName=" + targetName
                + ", targetPhone=" + targetPhone + "}";

        assertEquals(expected, deleteCommand.toString());
    }

    /**
     * Updates {@code model}'s filtered list to show no one.
     */
    private void showNoPerson(Model model) {
        model.updateFilteredPersonList(p -> false);

        assertTrue(model.getFilteredPersonList().isEmpty());
    }
}
