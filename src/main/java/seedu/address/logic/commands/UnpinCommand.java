package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Person;
import seedu.address.model.person.PersonMatchesExactFieldsPredicate;

/**
 * Unpins the unique contact matching all supplied exact field criteria.
 */
public class UnpinCommand extends Command {

    public static final String COMMAND_WORD = "unpin";
    public static final String ACTION_PAST_TENSE = "unpinned";
    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Unpins a contact only when exactly one contact matches all supplied values.\n"
            + PersonTarget.MESSAGE_PARAMETERS
            + "At least one prefix must be provided.\n"
            + "Example: " + COMMAND_WORD + " " + PREFIX_NAME + "John Doe " + PREFIX_PHONE + "81231234";
    public static final String MESSAGE_PERSON_NOT_FOUND = PersonTarget.MESSAGE_PERSON_NOT_FOUND;
    public static final String MESSAGE_AMBIGUOUS_PERSON =
            String.format(PersonTarget.MESSAGE_AMBIGUOUS_PERSON, ACTION_PAST_TENSE);
    public static final String MESSAGE_EMPTY_FLAG_VALUE = "Please provide a value for %1$s to unpin by.";
    public static final String MESSAGE_ALREADY_UNPINNED = "This contact is already unpinned.";
    public static final String MESSAGE_UNPIN_PERSON_SUCCESS = "Unpinned contact: %1$s";

    private static final String MESSAGE_IDENTITY_PRESERVED = "Unpinning must preserve contact identity";

    private final PersonTarget target;

    /**
     * Creates a command that unpins the unique person satisfying {@code predicate}.
     */
    public UnpinCommand(PersonMatchesExactFieldsPredicate predicate) {
        target = new PersonTarget(predicate);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        Person personToUnpin = target.resolve(model, MESSAGE_PERSON_NOT_FOUND, MESSAGE_AMBIGUOUS_PERSON);
        if (!personToUnpin.isPinned()) {
            throw new CommandException(MESSAGE_ALREADY_UNPINNED);
        }

        Person unpinnedPerson = new Person(personToUnpin, false);
        assert personToUnpin.isSamePerson(unpinnedPerson) : MESSAGE_IDENTITY_PRESERVED;
        model.setPerson(personToUnpin, unpinnedPerson);
        return new CommandResult(String.format(MESSAGE_UNPIN_PERSON_SUCCESS,
                Messages.format(unpinnedPerson)));
    }

    @Override
    public boolean isUndoable() {
        return true;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        return other instanceof UnpinCommand otherUnpinCommand && target.equals(otherUnpinCommand.target);
    }

    @Override
    public int hashCode() {
        return target.hashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("predicate", target.predicate())
                .toString();
    }
}
