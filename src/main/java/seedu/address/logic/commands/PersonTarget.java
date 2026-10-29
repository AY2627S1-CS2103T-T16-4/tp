package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;
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

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Person;
import seedu.address.model.person.PersonMatchesExactFieldsPredicate;

/**
 * Resolves exact field criteria to one contact, displaying the matches when the target is ambiguous.
 */
public record PersonTarget(PersonMatchesExactFieldsPredicate predicate) {

    public static final String MESSAGE_PARAMETERS =
            "Parameters: "
            + "[" + PREFIX_NAME + "NAME] "
            + "[" + PREFIX_PHONE + "PHONE_NUMBER] "
            + "[" + PREFIX_ADDRESS + "ADDRESS] "
            + "[" + PREFIX_EMAIL + "EMAIL] "
            + "[" + PREFIX_GITHUB + "GITHUB] "
            + "[" + PREFIX_LINKEDIN + "LINKEDIN] "
            + "[" + PREFIX_TELEGRAM + "TELEGRAM] "
            + "[" + PREFIX_GROUP + "GROUP]... "
            + "[" + PREFIX_TAG + "TAG]...\n";
    public static final String MESSAGE_PERSON_NOT_FOUND = "No contact matches the supplied criteria.";
    public static final String MESSAGE_AMBIGUOUS_PERSON =
            "Multiple contacts match your criteria.\n"
                    + "No contact was %1$s.\n"
                    + "Please provide additional prefixes to identify exactly one contact.\n"
                    + MESSAGE_PARAMETERS;

    public PersonTarget {
        requireNonNull(predicate);
    }

    /**
     * Resolves the target against all contacts, including those hidden by the current filter.
     *
     * @throws CommandException if no contact matches or multiple contacts match.
     */
    public Person resolve(Model model, String messageNotFound, String messageAmbiguous) throws CommandException {
        requireAllNonNull(model, messageNotFound, messageAmbiguous);
        List<Person> matches = model.getAddressBook().getPersonList().stream().filter(predicate).toList();
        if (matches.isEmpty()) {
            throw new CommandException(messageNotFound);
        }
        if (matches.size() > 1) {
            model.updateFilteredPersonList(predicate);
            throw new CommandException(messageAmbiguous);
        }
        return matches.getFirst();
    }
}
