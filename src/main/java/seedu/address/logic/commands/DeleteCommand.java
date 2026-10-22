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
 * Deletes a contact identified by exact field criteria.
 */
public class DeleteCommand extends Command {

    public static final String COMMAND_WORD = "delete";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Deletes a contact only when exactly one contact matches "
            + "all supplied values.\n"
            + PersonTarget.MESSAGE_PARAMETERS
            + "At least one prefix must be provided.\n"
            + "Example: " + COMMAND_WORD + " "
            + PREFIX_NAME + "John Doe " + PREFIX_PHONE + "81231234";

    public static final String MESSAGE_PERSON_NOT_FOUND = PersonTarget.MESSAGE_PERSON_NOT_FOUND;

    public static final String MESSAGE_AMBIGUOUS_PERSON =
            String.format(PersonTarget.MESSAGE_AMBIGUOUS_PERSON, "deleted");

    public static final String MESSAGE_EMPTY_FLAG_VALUE =
            "Please provide a value for %1$s to delete by.";

    public static final String MESSAGE_DELETE_PERSON_SUCCESS =
            "Deleted person: %1$s";

    private final PersonTarget target;

    /**
     * Creates a command that deletes the unique person satisfying {@code predicate}.
     */
    public DeleteCommand(PersonMatchesExactFieldsPredicate predicate) {
        target = new PersonTarget(predicate);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        Person personToDelete = target.resolve(model, MESSAGE_PERSON_NOT_FOUND, MESSAGE_AMBIGUOUS_PERSON);
        model.deletePerson(personToDelete);

        return new CommandResult(String.format(
                MESSAGE_DELETE_PERSON_SUCCESS,
                Messages.format(personToDelete)));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof DeleteCommand otherDeleteCommand)) {
            return false;
        }

        return target.equals(otherDeleteCommand.target);
    }

    @Override
    public int hashCode() {
        return target.hashCode();
    }

    @Override
    public boolean isUndoable() {
        return true;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("predicate", target.predicate())
                .toString();
    }
}
