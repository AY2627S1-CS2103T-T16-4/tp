package seedu.address.logic.commands;

import seedu.address.model.Model;

/**
 * Formats full help instructions for every command for display.
 */
public class HelpCommand extends Command {

    public static final String COMMAND_WORD = "help";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Shows program usage instructions.\n"
            + "Example: " + COMMAND_WORD;

    public static final String SHOWING_HELP_MESSAGE = String.join("\n\n",
            AddCommand.MESSAGE_USAGE,
            EditCommand.MESSAGE_USAGE,
            DeleteCommand.MESSAGE_USAGE,
            FindCommand.MESSAGE_USAGE,
            SortCommand.MESSAGE_USAGE,
            "list: Shows all contacts.\nExample: list",
            "clear: Deletes all contacts.\nExample: clear",
            PinCommand.MESSAGE_USAGE,
            UnpinCommand.MESSAGE_USAGE,
            SortCommand.MESSAGE_USAGE,
            UndoCommand.MESSAGE_USAGE,
            MESSAGE_USAGE,
            "exit: Closes the application.\nExample: exit");

    @Override
    public CommandResult execute(Model model) {
        return new CommandResult(SHOWING_HELP_MESSAGE, false, false);
    }
}
