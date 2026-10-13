package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.parser.CliSyntax.PREFIX_GITHUB;
import static seedu.address.logic.parser.CliSyntax.PREFIX_GROUP;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BENSON;
import static seedu.address.testutil.TypicalPersons.CARL;
import static seedu.address.testutil.TypicalPersons.JACK;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.util.List;
import java.util.function.Consumer;

import org.junit.jupiter.api.Test;

import seedu.address.logic.parser.Prefix;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.FindPersonDescriptor;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.PersonMatchesFlagsPredicate;

/**
 * Contains integration tests (interaction with the Model) for {@code FindCommand}.
 */
public class FindCommandTest {
    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
    private Model expectedModel = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void equals() {
        PersonMatchesFlagsPredicate firstPredicate = predicateWith(d -> d.setName(new Name("first")));
        PersonMatchesFlagsPredicate secondPredicate = predicateWith(d -> d.setName(new Name("second")));

        FindCommand findFirstCommand = new FindCommand(firstPredicate, List.of(PREFIX_NAME));
        FindCommand findSecondCommand = new FindCommand(secondPredicate, List.of(PREFIX_NAME));

        // same object -> returns true
        assertTrue(findFirstCommand.equals(findFirstCommand));

        // same values -> returns true
        FindCommand findFirstCommandCopy = new FindCommand(firstPredicate, List.of(PREFIX_NAME));
        assertTrue(findFirstCommand.equals(findFirstCommandCopy));

        // different types -> returns false
        assertFalse(findFirstCommand.equals(1));

        // null -> returns false
        assertFalse(findFirstCommand.equals(null));

        // different predicate -> returns false
        assertFalse(findFirstCommand.equals(findSecondCommand));

        // different used prefixes -> returns false
        assertFalse(findFirstCommand.equals(new FindCommand(firstPredicate, List.of(PREFIX_GROUP))));
    }

    @Test
    public void execute_singleFlagOneMatch_personFound() {
        PersonMatchesFlagsPredicate predicate = predicateWith(d -> d.setName(ALICE.getName()));
        assertFindSuccess(predicate, List.of(PREFIX_NAME), List.of(ALICE));
    }

    @Test
    public void execute_singleFlagMultipleMatches_allMatchesFound() {
        PersonMatchesFlagsPredicate predicate = predicateWith(d -> d.setGroup(ALICE.getGroup()));
        assertFindSuccess(predicate, List.of(PREFIX_GROUP), List.of(ALICE, BENSON, JACK));
    }

    @Test
    public void execute_githubDifferingOnlyByCase_onlyExactCaseFound() {
        // ALICE and JACK have github usernames that differ only by case
        PersonMatchesFlagsPredicate alicePredicate = predicateWith(d -> d.setGithub(ALICE.getGithub()));
        assertFindSuccess(alicePredicate, List.of(PREFIX_GITHUB), List.of(ALICE));

        PersonMatchesFlagsPredicate jackPredicate = predicateWith(d -> d.setGithub(JACK.getGithub()));
        assertFindSuccess(jackPredicate, List.of(PREFIX_GITHUB), List.of(JACK));
    }

    @Test
    public void execute_multipleFlags_onlyPersonsMatchingAllFlagsFound() {
        // ALICE, BENSON and JACK share a group, but only JACK has the searched tag
        PersonMatchesFlagsPredicate predicate = predicateWith(d -> {
            d.setGroup(JACK.getGroup());
            d.setTags(JACK.getTags());
        });
        assertFindSuccess(predicate, List.of(PREFIX_GROUP, PREFIX_TAG), List.of(JACK));
    }

    @Test
    public void execute_noMatch_notFoundMessageNamesFlags() {
        // each value is held by someone, but not by the same person
        PersonMatchesFlagsPredicate predicate = predicateWith(d -> {
            d.setGithub(ALICE.getGithub());
            d.setGroup(CARL.getGroup());
        });
        FindCommand command = new FindCommand(predicate, List.of(PREFIX_GITHUB, PREFIX_GROUP));
        String expectedMessage = String.format(FindCommand.MESSAGE_NOT_FOUND, "gh/ g/");
        expectedModel.updateFilteredPersonList(predicate);
        assertCommandSuccess(command, model,
                expectedMessage, expectedModel);
        assertEquals(List.of(), model.getFilteredPersonList());
    }

    @Test
    public void toStringMethod() {
        PersonMatchesFlagsPredicate predicate = predicateWith(d -> d.setName(ALICE.getName()));
        List<Prefix> usedPrefixes = List.of(PREFIX_NAME);
        FindCommand findCommand = new FindCommand(predicate, usedPrefixes);
        String expected = FindCommand.class.getCanonicalName()
                + "{predicate=" + predicate + ", usedPrefixes=" + usedPrefixes + "}";
        assertEquals(expected, findCommand.toString());
    }

    /**
     * Executes a {@code FindCommand} with the given {@code predicate} and {@code usedPrefixes}, and asserts that it
     * succeeds and leaves exactly {@code expectedPersons} in the filtered list, in order.
     */
    private void assertFindSuccess(PersonMatchesFlagsPredicate predicate, List<Prefix> usedPrefixes,
            List<Person> expectedPersons) {
        FindCommand command = new FindCommand(predicate, usedPrefixes);
        expectedModel.updateFilteredPersonList(predicate);
        assertCommandSuccess(command, model,
                FindCommand.MESSAGE_SUCCESS, expectedModel);
        assertEquals(expectedPersons, model.getFilteredPersonList());
    }

    private static PersonMatchesFlagsPredicate predicateWith(Consumer<FindPersonDescriptor> setup) {
        FindPersonDescriptor descriptor = new FindPersonDescriptor();
        setup.accept(descriptor);
        return new PersonMatchesFlagsPredicate(descriptor);
    }
}
