package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_GITHUB;
import static seedu.address.logic.parser.CliSyntax.PREFIX_GROUP;
import static seedu.address.logic.parser.CliSyntax.PREFIX_LINKEDIN;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TELEGRAM;

import java.util.List;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Person;
import seedu.address.model.person.PersonMatchesExactFieldsPredicate;

/**
 * Deletes a person identified using the person's name and optionally their phone number.
 */
public class DeleteCommand extends Command {

    public static final String COMMAND_WORD = "delete";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Deletes a contact only when exactly one contact matches "
            + "all supplied values.\n"
            + "Parameters: "
            + "[" + PREFIX_NAME + "NAME] "
            + "[" + PREFIX_PHONE + "PHONE] "
            + "[" + PREFIX_ADDRESS + "ADDRESS] "
            + "[" + PREFIX_EMAIL + "EMAIL] "
            + "[" + PREFIX_GITHUB + "GITHUB] "
            + "[" + PREFIX_LINKEDIN + "LINKEDIN] "
            + "[" + PREFIX_TELEGRAM + "TELEGRAM] "
            + "[" + PREFIX_GROUP + "GROUP]... "
            + "[" + PREFIX_TAG + "TAG]...\n"
            + "At least one prefix must be provided.\n"
            + "Example: " + COMMAND_WORD + " "
            + PREFIX_NAME + "John Doe " + PREFIX_PHONE + "81231234";

    public static final String MESSAGE_PERSON_NOT_FOUND =
            "No contact matches the supplied criteria.";

    public static final String MESSAGE_AMBIGUOUS_PERSON =
            "Multiple contacts match your criteria.\n"
                    + "No contact was deleted.\n"
                    + "Please provide additional prefixes to identify exactly one contact.\n"
                    + "Parameters: "
                    + "[" + PREFIX_NAME + "NAME] "
                    + "[" + PREFIX_PHONE + "PHONE] "
                    + "[" + PREFIX_ADDRESS + "ADDRESS] "
                    + "[" + PREFIX_EMAIL + "EMAIL] "
                    + "[" + PREFIX_GITHUB + "GITHUB] "
                    + "[" + PREFIX_LINKEDIN + "LINKEDIN] "
                    + "[" + PREFIX_TELEGRAM + "TELEGRAM] "
                    + "[" + PREFIX_GROUP + "GROUP]... "
                    + "[" + PREFIX_TAG + "TAG]...\n";

    public static final String MESSAGE_EMPTY_FLAG_VALUE =
            "Please provide a value for %1$s to delete by.";

    public static final String MESSAGE_DELETE_PERSON_SUCCESS =
            "Deleted person: %1$s";

    private final PersonMatchesExactFieldsPredicate predicate;

    /**
     * Creates a command that deletes the unique person satisfying {@code predicate}.
     */
    public DeleteCommand(PersonMatchesExactFieldsPredicate predicate) {
        this.predicate = requireNonNull(predicate);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        List<Person> matches = model.getAddressBook().getPersonList().stream()
                .filter(predicate)
                .toList();

        if (matches.isEmpty()) {
            throw new CommandException(MESSAGE_PERSON_NOT_FOUND);
        }

        if (matches.size() > 1) {
            model.updateFilteredPersonList(predicate);
            throw new CommandException(MESSAGE_AMBIGUOUS_PERSON);
        }

        Person personToDelete = matches.getFirst();
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

        return predicate.equals(otherDeleteCommand.predicate);
    }

    @Override
    public boolean isUndoable() {
        return true;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("predicate", predicate)
                .toString();
    }
}
