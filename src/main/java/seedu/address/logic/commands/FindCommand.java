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
import java.util.stream.Collectors;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.parser.Prefix;
import seedu.address.model.Model;
import seedu.address.model.person.PersonMatchesFlagsPredicate;

/**
 * Finds and lists all persons in the address book whose fields start with every given flag value.
 * Matching is by prefix, and is case-sensitive only for GitHub usernames.
 */
public class FindCommand extends Command {

    public static final String COMMAND_WORD = "find";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Finds all persons whose fields start with "
            + "all of the specified values and displays them as a list with index numbers.\n"
            + "The search is case-insensitive, except for GITHUB, which is case-sensitive.\n"
            + "Parameters: "
            + "[" + PREFIX_NAME + "NAME] "
            + "[" + PREFIX_PHONE + "PHONE] "
            + "[" + PREFIX_ADDRESS + "ADDRESS] "
            + "[" + PREFIX_EMAIL + "EMAIL] "
            + "[" + PREFIX_GITHUB + "GITHUB] "
            + "[" + PREFIX_LINKEDIN + "LINKEDIN] "
            + "[" + PREFIX_TELEGRAM + "TELEGRAM] "
            + "[" + PREFIX_GROUP + "GROUP] "
            + "[" + PREFIX_TAG + "TAG]...\n"
            + "At least one flag must be provided.\n"
            + "Example: " + COMMAND_WORD + " " + PREFIX_NAME + "LIM JUN HAN " + PREFIX_GROUP + "T09";

    public static final String MESSAGE_SUCCESS = "Here is the contact that matches your search.";

    public static final String MESSAGE_NOT_FOUND = "No contact was found. Please check the %1$s provided.";

    public static final String MESSAGE_EMPTY_FLAG_VALUE = "Please provide a value for %1$s to search for.";

    private final PersonMatchesFlagsPredicate predicate;
    private final List<Prefix> usedPrefixes;

    /**
     * @param predicate the predicate that a person must satisfy to be listed.
     * @param usedPrefixes the flags the user searched by, in the order they are shown in error messages.
     */
    public FindCommand(PersonMatchesFlagsPredicate predicate, List<Prefix> usedPrefixes) {
        requireNonNull(predicate);
        requireNonNull(usedPrefixes);
        this.predicate = predicate;
        this.usedPrefixes = List.copyOf(usedPrefixes);
    }

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        model.updateFilteredPersonList(predicate);
        if (model.getFilteredPersonList().isEmpty()) {
            String flags = usedPrefixes.stream().map(Prefix::toString).collect(Collectors.joining(" "));
            return new CommandResult(String.format(MESSAGE_NOT_FOUND, flags));
        }
        return new CommandResult(MESSAGE_SUCCESS);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof FindCommand otherFindCommand)) {
            return false;
        }

        return predicate.equals(otherFindCommand.predicate)
                && usedPrefixes.equals(otherFindCommand.usedPrefixes);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("predicate", predicate)
                .add("usedPrefixes", usedPrefixes)
                .toString();
    }
}
