package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import seedu.address.model.Model;

/**
 * Restores the address book to its previous state.
 */
public class UndoCommand extends Command {

    public static final String COMMAND_WORD = "undo";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Undoes the previous data-changing command.\nExample: " + COMMAND_WORD;

    public static final String MESSAGE_SUCCESS = "The previous command was undone.";

    public static final String MESSAGE_NO_UNDO = "There is nothing to undo.";

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);

        if (!model.undo()) {
            return new CommandResult(MESSAGE_NO_UNDO);
        }

        return new CommandResult(MESSAGE_SUCCESS);
    }
}
