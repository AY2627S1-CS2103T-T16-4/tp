package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.Comparator;
import java.util.Locale;

import seedu.address.model.Model;
import seedu.address.model.person.Person;

/**
 * Sorts the current view while preserving its search filter.
 */
public class SortCommand extends Command {
    public static final String COMMAND_WORD = "sort";
    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Sorts the currently displayed contacts.\n"
            + "Parameters: name|group [asc|desc]\n"
            + "Defaults to ascending. Example: sort name asc or sort group desc";
    public static final String MESSAGE_SUCCESS = "Sorted contacts by %1$s (%2$s).";

    /** Supported sort fields. */
    public enum Field {
        NAME, GROUP
    }

    private final Field field;
    private final boolean descending;

    /**
     * Creates a command for the given field and direction.
     */
    public SortCommand(Field field, boolean descending) {
        this.field = requireNonNull(field);
        this.descending = descending;
    }

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        Comparator<Person> byName = Comparator.comparing(person -> person.getName().fullName,
                String.CASE_INSENSITIVE_ORDER);
        Comparator<Person> comparator;
        if (field == Field.NAME) {
            comparator = descending ? byName.reversed() : byName;
        } else {
            Comparator<String> groupOrder = descending
                    ? String.CASE_INSENSITIVE_ORDER.reversed() : String.CASE_INSENSITIVE_ORDER;
            comparator = Comparator.comparing(SortCommand::firstGroup, Comparator.nullsLast(groupOrder))
                    .thenComparing(byName);
        }
        model.sortFilteredPersonList(comparator);
        return new CommandResult(String.format(MESSAGE_SUCCESS, field.name().toLowerCase(Locale.ROOT),
                descending ? "descending" : "ascending"));
    }

    private static String firstGroup(Person person) {
        return person.getGroups().stream().map(group -> group.value)
                .min(String.CASE_INSENSITIVE_ORDER).orElse(null);
    }

    @Override
    public boolean equals(Object other) {
        return other == this || other instanceof SortCommand otherCommand
                && field == otherCommand.field && descending == otherCommand.descending;
    }
}
