package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.Objects;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.Model;
import seedu.address.model.SortSettings;
import seedu.address.model.SortSettings.Field;

/** Sorts the current view while preserving its search filter. */
public class SortCommand extends Command {
    public static final String COMMAND_WORD = "sort";
    public static final String DIRECTION_ASCENDING = "asc";
    public static final String DIRECTION_DESCENDING = "desc";
    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Sorts the currently displayed contacts.\n"
            + "Parameters: " + Field.getUsage() + " [" + DIRECTION_ASCENDING + "|" + DIRECTION_DESCENDING
            + "] [cont|none]\n"
            + "Defaults to ascending and none. Sorting stays active during this session.\n"
            + "Use cont to restore sorting after restarting; none clears any saved sort preference.\n"
            + "Example: sort name asc cont or sort tag desc";
    public static final String MESSAGE_SUCCESS = "Sorted contacts by %1$s (%2$s).";
    public static final String MESSAGE_PERSISTENT = " Sort preference will be saved on exit.";
    public static final String MESSAGE_SESSION_ONLY =
            " Sorting applies to this session only; any saved sort preference will be cleared on exit.";

    private final SortSettings settings;
    private final boolean isContinuous;

    /** Creates a session-only sort command. */
    public SortCommand(Field field, boolean isDescending) {
        this(field, isDescending, false);
    }

    /** Creates a sort command with the given persistence choice. */
    public SortCommand(Field field, boolean isDescending, boolean isContinuous) {
        settings = new SortSettings(field, isDescending);
        this.isContinuous = isContinuous;
    }

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        int visibleContactCount = model.getFilteredPersonList().size();
        model.sortFilteredPersonList(settings, isContinuous);
        assert model.getFilteredPersonList().size() == visibleContactCount
                : "Sorting must preserve the number of visible contacts";
        return new CommandResult(String.format(MESSAGE_SUCCESS, settings.getField().getKeyword(),
                settings.isDescending() ? "descending" : "ascending")
                + (isContinuous ? MESSAGE_PERSISTENT : MESSAGE_SESSION_ONLY));
    }

    @Override
    public boolean equals(Object other) {
        return other == this || other instanceof SortCommand otherCommand
                && settings.equals(otherCommand.settings) && isContinuous == otherCommand.isContinuous;
    }

    @Override
    public int hashCode() {
        return Objects.hash(settings, isContinuous);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("settings", settings).add("isContinuous", isContinuous).toString();
    }
}
