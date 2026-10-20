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
 * Pins the unique contact matching all supplied exact field criteria.
 */
public class PinCommand extends Command {

    public static final String COMMAND_WORD = "pin";
    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Pins a contact only when exactly one contact matches all supplied values.\n"
            + PersonTarget.MESSAGE_PARAMETERS
            + "At least one prefix must be provided.\n"
            + "Example: " + COMMAND_WORD + " " + PREFIX_NAME + "John Doe " + PREFIX_PHONE + "81231234";
    public static final String MESSAGE_PERSON_NOT_FOUND = PersonTarget.MESSAGE_PERSON_NOT_FOUND;
    public static final String MESSAGE_AMBIGUOUS_PERSON =
            String.format(PersonTarget.MESSAGE_AMBIGUOUS_PERSON, "pinned");
    public static final String MESSAGE_EMPTY_FLAG_VALUE = "Please provide a value for %1$s to pin by.";
    public static final String MESSAGE_ALREADY_PINNED = "This contact is already pinned.";
    public static final String MESSAGE_PIN_PERSON_SUCCESS = "Pinned contact: %1$s";

    private final PersonTarget target;

    /**
     * Creates a command that pins the unique person satisfying {@code predicate}.
     */
    public PinCommand(PersonMatchesExactFieldsPredicate predicate) {
        target = new PersonTarget(predicate);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        Person personToPin = target.resolve(model, MESSAGE_PERSON_NOT_FOUND, MESSAGE_AMBIGUOUS_PERSON);
        if (personToPin.isPinned()) {
            throw new CommandException(MESSAGE_ALREADY_PINNED);
        }

        Person pinnedPerson = new Person(personToPin, true);
        assert personToPin.isSamePerson(pinnedPerson) : "Pinning must preserve contact identity";
        model.setPerson(personToPin, pinnedPerson);
        return new CommandResult(String.format(MESSAGE_PIN_PERSON_SUCCESS,
                Messages.format(pinnedPerson)));
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
        return other instanceof PinCommand otherPinCommand && target.equals(otherPinCommand.target);
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
