package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;

import java.util.Optional;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;

/**
 * Pins a contact identified by full name and optional complete phone number.
 */
public class PinCommand extends Command {

    public static final String COMMAND_WORD = "pin";
    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Pins the contact identified by their name.\n"
            + "Parameters: " + PREFIX_NAME + "NAME [" + PREFIX_PHONE + "PHONE_NUMBER]\n"
            + "Example: " + COMMAND_WORD + " " + PREFIX_NAME + "John Doe " + PREFIX_PHONE + "81231234";
    public static final String MESSAGE_ALREADY_PINNED = "This contact is already pinned.";
    public static final String MESSAGE_PIN_PERSON_SUCCESS = "Pinned contact: %1$s, Phone: %2$s";

    private final PersonTarget target;

    /**
     * Creates a command targeting a full name and optional complete phone number.
     */
    public PinCommand(Name targetName, Optional<Phone> targetPhone) {
        target = new PersonTarget(targetName, targetPhone);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        Person personToPin = target.resolve(model.getAddressBook());
        if (personToPin.isPinned()) {
            throw new CommandException(MESSAGE_ALREADY_PINNED);
        }

        model.setPerson(personToPin, new Person(personToPin, true));
        return new CommandResult(String.format(MESSAGE_PIN_PERSON_SUCCESS,
                personToPin.getName(), personToPin.getPhone()));
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
                .add("targetName", target.name())
                .add("targetPhone", target.phone())
                .toString();
    }
}
