package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.Objects;

import seedu.address.commons.util.ToStringBuilder;

/**
 * Represents the result of a command execution.
 */
public class CommandResult {

    private final String feedbackToUser;

    /** Help information should be shown to the user. */
    private final boolean shouldShowHelp;

    /** The application should exit. */
    private final boolean shouldExit;

    /** The UI should select the first contact in the resulting list. */
    private final boolean shouldResetContactSelection;

    /**
     * Constructs a {@code CommandResult} with the specified fields.
     */
    public CommandResult(String feedbackToUser, boolean shouldShowHelp, boolean shouldExit) {
        this(feedbackToUser, shouldShowHelp, shouldExit, false);
    }

    /**
     * Constructs a result with feedback and instructions for the UI.
     */
    public CommandResult(String feedbackToUser, boolean shouldShowHelp, boolean shouldExit,
            boolean shouldResetContactSelection) {
        this.feedbackToUser = requireNonNull(feedbackToUser);
        this.shouldShowHelp = shouldShowHelp;
        this.shouldExit = shouldExit;
        this.shouldResetContactSelection = shouldResetContactSelection;
    }

    /**
     * Constructs a {@code CommandResult} with the specified {@code feedbackToUser},
     * and other fields set to their default value.
     */
    public CommandResult(String feedbackToUser) {
        this(feedbackToUser, false, false);
    }

    public String getFeedbackToUser() {
        return feedbackToUser;
    }

    public boolean isShowHelp() {
        return shouldShowHelp;
    }

    public boolean isExit() {
        return shouldExit;
    }

    /**
     * Returns whether the UI should select the first contact in the resulting list.
     */
    public boolean shouldResetContactSelection() {
        return shouldResetContactSelection;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof CommandResult otherCommandResult)) {
            return false;
        }

        return feedbackToUser.equals(otherCommandResult.feedbackToUser)
                && shouldShowHelp == otherCommandResult.shouldShowHelp
                && shouldExit == otherCommandResult.shouldExit
                && shouldResetContactSelection == otherCommandResult.shouldResetContactSelection;
    }

    @Override
    public int hashCode() {
        return Objects.hash(feedbackToUser, shouldShowHelp, shouldExit, shouldResetContactSelection);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("feedbackToUser", feedbackToUser)
                .add("shouldShowHelp", shouldShowHelp)
                .add("shouldExit", shouldExit)
                .add("shouldResetContactSelection", shouldResetContactSelection)
                .toString();
    }

}
