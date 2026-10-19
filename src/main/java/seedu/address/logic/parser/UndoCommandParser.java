package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import seedu.address.logic.commands.UndoCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a new UndoCommand object.
 */
public class UndoCommandParser implements Parser<UndoCommand> {

    /**
     * Parses the given arguments in the context of the UndoCommand.
     *
     * @throws ParseException if any arguments are provided.
     */
    public UndoCommand parse(String args) throws ParseException {
        requireNonNull(args);

        if (!args.trim().isEmpty()) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, UndoCommand.MESSAGE_USAGE));
        }

        return new UndoCommand();
    }
}
