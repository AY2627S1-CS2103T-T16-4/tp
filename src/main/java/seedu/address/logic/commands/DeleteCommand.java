package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;

/**
 * Deletes a person identified using the person's name and optionally their phone number.
 */
public class DeleteCommand extends Command {

    public static final String COMMAND_WORD = "delete";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Deletes the person identified by their name.\n"
            + "Parameters: "
            + PREFIX_NAME + "NAME "
            + "[" + PREFIX_PHONE + "PHONE_NUMBER ]\n"
            + String.format("Example 1: %s %sJohn Doe\n", COMMAND_WORD, PREFIX_NAME)
            + String.format("Example 2: %s %sJohn Doe %s81231234", COMMAND_WORD, PREFIX_NAME, PREFIX_PHONE);

    public static final String MESSAGE_PERSON_NOT_FOUND =
            "No contact matches the supplied name and phone number.";

    public static final String MESSAGE_AMBIGUOUS_PERSON =
            "Multiple contacts match:\n%1$s\n"
                    + "Please specify a phone number using p/PHONE_NUMBER.";

    public static final String MESSAGE_DELETE_PERSON_SUCCESS =
            "Deleted person: %1$s, Phone: %2$s";

    private final Name targetName;
    private final Optional<Phone> targetPhone;


    public DeleteCommand(Name targetName, Optional<Phone> targetPhone) {
        this.targetName = requireNonNull(targetName);
        this.targetPhone = requireNonNull(targetPhone);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Person> matches = model.getAddressBook().getPersonList().stream()
                .filter(person -> person.getName().equals(targetName))
                .filter(person -> targetPhone
                        .map(phone -> person.getPhone().equals(phone))
                        .orElse(true))
                .toList();

        if (matches.isEmpty()) {
            throw new CommandException(MESSAGE_PERSON_NOT_FOUND);
        }

        if (matches.size() > 1) {
            String matchingContacts = matches.stream()
                    .map(person -> String.format("%s, Phone: %s",
                            person.getName(), person.getPhone()))
                    .collect(Collectors.joining("\n"));

            throw new CommandException(
                    String.format(MESSAGE_AMBIGUOUS_PERSON, matchingContacts));
        }

        Person personToDelete = matches.getFirst();
        model.deletePerson(personToDelete);

        return new CommandResult(String.format(
                MESSAGE_DELETE_PERSON_SUCCESS,
                personToDelete.getName(),
                personToDelete.getPhone()));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof DeleteCommand otherDeleteCommand)) {
            return false;
        }

        return targetName.equals(otherDeleteCommand.targetName)
                && targetPhone.equals(otherDeleteCommand.targetPhone);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("targetName", targetName)
                .add("targetPhone", targetPhone)
                .toString();
    }
}
