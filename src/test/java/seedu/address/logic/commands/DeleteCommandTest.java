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

    // EP: null search predicate; constructor rejects an absent dependency.
    @Test
    public void constructor_nullPredicate_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new DeleteCommand(null));
    }

    // EP: null model; execution rejects an absent dependency.
    @Test
    public void execute_nullModel_throwsNullPointerException() {
        DeleteCommand command = new DeleteCommand(
                createPredicateWith(d -> d.setName(ALICE.getName())));

        assertThrows(NullPointerException.class, () -> command.execute(null));
    }

    // EP: exactly one match in the address book, hidden by the display filter; deletion preserves the filter.
    // Boundary value: 1 match, the only count allowing deletion.
    @Test
    public void execute_uniqueMatchHiddenByFilter_successAndPreservesFilter() {
        model.updateFilteredPersonList(person -> person.equals(BENSON));
        expectedModel.updateFilteredPersonList(person -> person.equals(BENSON));
        assertEquals(List.of(BENSON), model.getFilteredPersonList());

        PersonMatchesExactFieldsPredicate predicate =
                createPredicateWith(d -> d.setName(ALICE.getName()));

        assertDeleteSuccess(predicate, ALICE);

        assertEquals(List.of(BENSON), model.getFilteredPersonList());
    }

    // EP: ambiguous criteria despite only one visible match; hidden matches must also prevent deletion.
    // Boundary value: 2 matches, the smallest ambiguous count above the required count of 1.
    @Test
    public void execute_multipleMatchesWithOnlyOneVisible_showsAllWithoutDeleting() {
        model.updateFilteredPersonList(person -> person.equals(DANIEL));
        assertEquals(List.of(DANIEL), model.getFilteredPersonList());

        PersonMatchesExactFieldsPredicate predicate =
                createPredicateWith(d -> d.setName(DANIEL.getName()));

        assertAmbiguousFailure(predicate, List.of(DANIEL, DANIEL_TWIN));
    }

    // EP: no matching contact with an active display filter; failure preserves data and the filter.
    // Boundary value: 0 matches, immediately below the required count of 1.
    @Test
    public void execute_noMatchWithActiveFilter_preservesFilter() {
        model.updateFilteredPersonList(person -> person.equals(BENSON));
        assertEquals(List.of(BENSON), model.getFilteredPersonList());

        PersonMatchesExactFieldsPredicate predicate =
                createPredicateWith(d -> d.setName(new Name("Unknown Person")));

        assertCommandFailure(new DeleteCommand(predicate), model,
                DeleteCommand.MESSAGE_PERSON_NOT_FOUND);
    }

    // EP: a group criterion alone identifies one contact; contacts outside that group remain.
    @Test
    public void execute_groupOnlyOneMatch_success() {
        AddressBook addressBook = new AddressBook();
        addressBook.addPerson(ALICE);
        addressBook.addPerson(GEORGE);

        model = new ModelManager(addressBook, new UserPrefs());
        expectedModel = new ModelManager(
                new AddressBook(addressBook), new UserPrefs());

        PersonMatchesExactFieldsPredicate predicate =
                createPredicateWith(d -> d.setGroups(ALICE.getGroups()));

        assertDeleteSuccess(predicate, ALICE);
    }

    // EP: uniquely identified contact has blank optional fields; optional data is not required for deletion.
    @Test
    public void execute_personWithAllOptionalFieldsBlank_success() {
        PersonMatchesExactFieldsPredicate predicate =
                createPredicateWith(d -> d.setPhone(GEORGE.getPhone()));

        assertDeleteSuccess(predicate, GEORGE);
    }

    // EP: one exact name criterion identifies one contact.
    @Test
    public void execute_nameOnlyOneMatch_success() {
        PersonMatchesExactFieldsPredicate predicate =
                createPredicateWith(d -> d.setName(ALICE.getName()));

        assertDeleteSuccess(predicate, ALICE);
    }

    // EP: one exact phone criterion identifies one contact.
    @Test
    public void execute_phoneOnlyOneMatch_success() {
        PersonMatchesExactFieldsPredicate predicate =
                createPredicateWith(d -> d.setPhone(ALICE.getPhone()));

        assertDeleteSuccess(predicate, ALICE);
    }

    // EP: one exact email criterion identifies one contact.
    @Test
    public void execute_emailOnlyOneMatch_success() {
        PersonMatchesExactFieldsPredicate predicate =
                createPredicateWith(d -> d.setEmail(ALICE.getEmail()));

        assertDeleteSuccess(predicate, ALICE);
    }

    // EP: one exact address criterion identifies one contact.
    @Test
    public void execute_addressOnlyOneMatch_success() {
        PersonMatchesExactFieldsPredicate predicate =
                createPredicateWith(d -> d.setAddress(ALICE.getAddress()));

        assertDeleteSuccess(predicate, ALICE);
    }

    // EP: one exact GitHub criterion identifies one contact; case differences must not introduce another match.
    @Test
    public void execute_githubOnlyOneMatch_success() {
        PersonMatchesExactFieldsPredicate predicate =
                createPredicateWith(d -> d.setGithub(ALICE.getGithub()));

        assertDeleteSuccess(predicate, ALICE);
    }

    // EP: one exact LinkedIn criterion identifies one contact.
    @Test
    public void execute_linkedinOnlyOneMatch_success() {
        PersonMatchesExactFieldsPredicate predicate =
                createPredicateWith(d -> d.setLinkedin(ALICE.getLinkedin()));

        assertDeleteSuccess(predicate, ALICE);
    }

    // EP: one exact Telegram criterion identifies one contact.
    @Test
    public void execute_telegramOnlyOneMatch_success() {
        PersonMatchesExactFieldsPredicate predicate =
                createPredicateWith(d -> d.setTelegram(ALICE.getTelegram()));

        assertDeleteSuccess(predicate, ALICE);
    }

    // EP: different search fields jointly identify one contact; all supplied criteria must match.
    @Test
    public void execute_multipleFlagsOneMatch_success() {
        // ALICE, BENSON and JACK share a group, but only JACK has this tag.
        PersonMatchesExactFieldsPredicate predicate = createPredicateWith(d -> {
            d.setGroups(JACK.getGroups());
            d.setTags(JACK.getTags());
        });

        assertDeleteSuccess(predicate, JACK);
    }

    // EP: a contact has all requested tags; matching only a subset must not be sufficient.
    @Test
    public void execute_multipleTagsOneMatch_success() {
        // Only BENSON has both "friends" and "owesMoney".
        PersonMatchesExactFieldsPredicate predicate =
                createPredicateWith(d -> d.setTags(BENSON.getTags()));

        assertDeleteSuccess(predicate, BENSON);
    }

    // EP: a phone criterion disambiguates contacts sharing the same name; only the joint match is deleted.
    @Test
    public void execute_sameNameAndMatchingPhone_success() {
        // DANIEL and DANIEL_TWIN have the same name.
        PersonMatchesExactFieldsPredicate predicate = createPredicateWith(d -> {
            d.setName(DANIEL.getName());
            d.setPhone(DANIEL.getPhone());
        });

        assertDeleteSuccess(predicate, DANIEL);
        assertTrue(model.hasPerson(DANIEL_TWIN));
    }

    // EP: syntactically valid name is absent from a nonempty address book.
    // Boundary value: 0 matches, immediately below the required count of 1.
    @Test
    public void execute_unknownName_throwsCommandException() {
        PersonMatchesExactFieldsPredicate predicate =
                createPredicateWith(d -> d.setName(new Name("Unknown Person")));

        assertCommandFailure(new DeleteCommand(predicate), model,
                DeleteCommand.MESSAGE_PERSON_NOT_FOUND);
    }

    // EP: each criterion matches a different contact, but no contact satisfies all criteria together.
    @Test
    public void execute_fieldsBelongingToDifferentPersons_throwsCommandException() {
        PersonMatchesExactFieldsPredicate predicate = createPredicateWith(d -> {
            d.setName(ALICE.getName());
            d.setPhone(BENSON.getPhone());
        });

        assertCommandFailure(new DeleteCommand(predicate), model,
                DeleteCommand.MESSAGE_PERSON_NOT_FOUND);
    }

    // EP: name is only a substring of an existing name; exact matching must reject it.
    @Test
    public void execute_partialName_throwsCommandException() {
        PersonMatchesExactFieldsPredicate predicate =
                createPredicateWith(d -> d.setName(new Name("Alice")));

        assertCommandFailure(new DeleteCommand(predicate), model,
                DeleteCommand.MESSAGE_PERSON_NOT_FOUND);
    }

    // EP: no contacts exist, so otherwise valid criteria cannot identify a deletion target.
    // Boundary value: address book size 0.
    @Test
    public void execute_emptyAddressBook_throwsCommandException() {
        Model emptyModel = new ModelManager();
        PersonMatchesExactFieldsPredicate predicate =
                createPredicateWith(d -> d.setName(ALICE.getName()));

        assertCommandFailure(new DeleteCommand(predicate), emptyModel,
                DeleteCommand.MESSAGE_PERSON_NOT_FOUND);
    }

    // EP: a shared group matches several contacts; ambiguity displays all matches without deleting any.
    @Test
    public void execute_multipleMatches_showsMatchesWithoutDeleting() {
        PersonMatchesExactFieldsPredicate predicate =
                createPredicateWith(d -> d.setGroups(ALICE.getGroups()));

        assertAmbiguousFailure(predicate, List.of(ALICE, BENSON, JACK));
    }

    // EP: a shared name matches more than one contact; ambiguity prevents deletion.
    // Boundary value: 2 matches, the smallest ambiguous count.
    @Test
    public void execute_sameNameMultipleMatches_showsBothWithoutDeleting() {
        PersonMatchesExactFieldsPredicate predicate =
                createPredicateWith(d -> d.setName(DANIEL.getName()));

        assertAmbiguousFailure(predicate, List.of(DANIEL, DANIEL_TWIN));
    }

    // EP: distinct commands with equal criteria; equality requires equal hash codes.
    @Test
    public void hashCode_equalCommands_sameHashCode() {
        DeleteCommand firstCommand = new DeleteCommand(
                createPredicateWith(d -> d.setName(ALICE.getName())));
        DeleteCommand secondCommand = new DeleteCommand(
                createPredicateWith(d -> d.setName(ALICE.getName())));

        assertEquals(firstCommand, secondCommand);
        assertEquals(firstCommand.hashCode(), secondCommand.hashCode());
    }

    // EPs: identical instance, equal criteria, null, different type, and unequal criteria (labelled below).
    @Test
    public void equals() {
        PersonMatchesExactFieldsPredicate firstPredicate =
                createPredicateWith(d -> d.setName(ALICE.getName()));
        PersonMatchesExactFieldsPredicate samePredicate =
                createPredicateWith(d -> d.setName(ALICE.getName()));
        PersonMatchesExactFieldsPredicate secondPredicate =
                createPredicateWith(d -> d.setName(BENSON.getName()));

        DeleteCommand firstCommand = new DeleteCommand(firstPredicate);

        // EP: same command instance -> true.
        assertTrue(firstCommand.equals(firstCommand));
        // EP: distinct commands with equal criteria -> true.
        assertTrue(firstCommand.equals(new DeleteCommand(samePredicate)));
        // EP: comparison with null -> false.
        assertFalse(firstCommand.equals(null));
        // EP: comparison with another type -> false.
        assertFalse(firstCommand.equals(1));
        // EP: same search field with different values -> false.
        assertFalse(firstCommand.equals(new DeleteCommand(secondPredicate)));

        PersonMatchesExactFieldsPredicate additionalFieldPredicate =
                createPredicateWith(d -> {
                    d.setName(ALICE.getName());
                    d.setPhone(ALICE.getPhone());
                });

        // EP: additional search field, even when it identifies the same contact -> false.
        assertFalse(firstCommand.equals(
                new DeleteCommand(additionalFieldPredicate)));
    }
    // Contract: a valid delete command reports that its data change can be undone.
    @Test
    public void isUndoable_returnsTrue() {
        DeleteCommand command = new DeleteCommand(
                createPredicateWith(d -> d.setName(ALICE.getName())));

        assertTrue(command.isUndoable());
    }

    // EP: command with a name criterion; its text representation includes the stored predicate.
    @Test
    public void toStringMethod() {
        PersonMatchesExactFieldsPredicate predicate =
                createPredicateWith(d -> d.setName(ALICE.getName()));
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
    private static PersonMatchesExactFieldsPredicate createPredicateWith(
            Consumer<ExactPersonMatchDescriptor> setup) {
        ExactPersonMatchDescriptor descriptor = new ExactPersonMatchDescriptor();
        setup.accept(descriptor);
        return new PersonMatchesExactFieldsPredicate(descriptor);
    }
}
