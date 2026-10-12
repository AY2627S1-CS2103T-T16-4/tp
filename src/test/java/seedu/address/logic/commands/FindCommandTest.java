package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.parser.CliSyntax.PREFIX_GITHUB;
import static seedu.address.logic.parser.CliSyntax.PREFIX_GROUP;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;

import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import org.junit.jupiter.api.Test;

import seedu.address.logic.parser.Prefix;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.FindPersonDescriptor;
import seedu.address.model.person.Github;
import seedu.address.model.person.Group;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.PersonMatchesFlagsPredicate;
import seedu.address.model.tag.Tag;
import seedu.address.testutil.PersonBuilder;

/**
 * Contains integration tests (interaction with the Model) for {@code FindCommand}.
 */
public class FindCommandTest {
    private static final Person ALICE = new PersonBuilder().withName("Alice Pauline").withPhone("91111111")
            .withGroup("T09").withGithub("AliceGh").withTags("friend").build();
    private static final Person BOB = new PersonBuilder().withName("Bob Choo").withPhone("92222222")
            .withGroup("T09").withGithub("bobgh").withTags("friend", "classmate").build();
    private static final Person CARL = new PersonBuilder().withName("Carl Kurz").withPhone("93333333")
            .withGroup("T10").build();

    private Model model = new ModelManager(getAddressBook(), new UserPrefs());
    private Model expectedModel = new ModelManager(getAddressBook(), new UserPrefs());

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
        PersonMatchesFlagsPredicate predicate = predicateWith(d -> d.setName(new Name("Alice Pauline")));
        FindCommand command = new FindCommand(predicate, List.of(PREFIX_NAME));
        expectedModel.updateFilteredPersonList(predicate);
        assertCommandSuccess(command, model,
                FindCommand.MESSAGE_SUCCESS, expectedModel);
        assertEquals(List.of(ALICE), model.getFilteredPersonList());
    }

    @Test
    public void execute_singleFlagMultipleMatches_allMatchesFound() {
        PersonMatchesFlagsPredicate predicate = predicateWith(d -> d.setGroup(new Group("t09")));
        FindCommand command = new FindCommand(predicate, List.of(PREFIX_GROUP));
        expectedModel.updateFilteredPersonList(predicate);
        assertCommandSuccess(command, model,
                FindCommand.MESSAGE_SUCCESS, expectedModel);
        assertEquals(List.of(ALICE, BOB), model.getFilteredPersonList());
    }

    @Test
    public void execute_multipleFlags_onlyPersonsMatchingAllFlagsFound() {
        PersonMatchesFlagsPredicate predicate = predicateWith(d -> {
            d.setGroup(new Group("T09"));
            d.setTags(Set.of(new Tag("classmate")));
        });
        FindCommand command = new FindCommand(predicate, List.of(PREFIX_GROUP, PREFIX_TAG));
        expectedModel.updateFilteredPersonList(predicate);
        assertCommandSuccess(command, model,
                FindCommand.MESSAGE_SUCCESS, expectedModel);
        assertEquals(List.of(BOB), model.getFilteredPersonList());
    }

    @Test
    public void execute_noMatch_notFoundMessageNamesFlags() {
        // GitHub usernames are case-sensitive
        PersonMatchesFlagsPredicate predicate = predicateWith(d -> {
            d.setGithub(new Github("alicegh"));
            d.setGroup(new Group("T09"));
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
        PersonMatchesFlagsPredicate predicate = predicateWith(d -> d.setName(new Name("Alice")));
        List<Prefix> usedPrefixes = List.of(PREFIX_NAME);
        FindCommand findCommand = new FindCommand(predicate, usedPrefixes);
        String expected = FindCommand.class.getCanonicalName()
                + "{predicate=" + predicate + ", usedPrefixes=" + usedPrefixes + "}";
        assertEquals(expected, findCommand.toString());
    }

    private static AddressBook getAddressBook() {
        AddressBook addressBook = new AddressBook();
        addressBook.addPerson(ALICE);
        addressBook.addPerson(BOB);
        addressBook.addPerson(CARL);
        return addressBook;
    }

    private static PersonMatchesFlagsPredicate predicateWith(Consumer<FindPersonDescriptor> setup) {
        FindPersonDescriptor descriptor = new FindPersonDescriptor();
        setup.accept(descriptor);
        return new PersonMatchesFlagsPredicate(descriptor);
    }
}
