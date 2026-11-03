package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.HelpCommand.SHOWING_HELP_MESSAGE;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.model.Model;
import seedu.address.model.ModelManager;

public class HelpCommandTest {
    private Model model = new ModelManager();
    private Model expectedModel = new ModelManager();

    @Test
    public void execute_help_success() {
        // Help is displayed as command feedback, so it must not request a separate help window.
        CommandResult expectedCommandResult = new CommandResult(SHOWING_HELP_MESSAGE, false, false);
        assertCommandSuccess(new HelpCommand(), model, expectedCommandResult, expectedModel);
    }

    @Test
    public void execute_help_containsEveryCommandUsage() {
        String feedback = new HelpCommand().execute(model).getFeedbackToUser();
        // Compare individual sections, not the aggregate constant used by the implementation:
        // an accidentally omitted section would otherwise disappear from both expected and actual values.
        for (String usage : List.of(AddCommand.MESSAGE_USAGE, EditCommand.MESSAGE_USAGE,
                DeleteCommand.MESSAGE_USAGE, FindCommand.MESSAGE_USAGE, SortCommand.MESSAGE_USAGE,
                "list: Shows all contacts.\nExample: list", "clear: Deletes all contacts.\nExample: clear",
                PinCommand.MESSAGE_USAGE, UnpinCommand.MESSAGE_USAGE, UndoCommand.MESSAGE_USAGE,
                ExportCommand.MESSAGE_USAGE, ImportCommand.MESSAGE_USAGE, HelpCommand.MESSAGE_USAGE,
                "exit: Closes the application.\nExample: exit")) {
            assertTrue(feedback.contains(usage), "Missing help section: " + usage);
        }
    }
}
