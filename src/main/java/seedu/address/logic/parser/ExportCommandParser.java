package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import java.nio.file.Path;

import seedu.address.logic.commands.ExportCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a new ExportCommand object.
 */
public class ExportCommandParser implements Parser<ExportCommand> {

    private final Path directory;

    /**
     * Creates a parser whose commands export into {@code directory}.
     */
    public ExportCommandParser(Path directory) {
        this.directory = requireNonNull(directory);
    }

    /**
     * Parses the given arguments in the context of the ExportCommand.
     *
     * @throws ParseException if any arguments are provided.
     */
    @Override
    public ExportCommand parse(String args) throws ParseException {
        requireNonNull(args);

        if (!args.trim().isEmpty()) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, ExportCommand.MESSAGE_USAGE));
        }

        return new ExportCommand(directory);
    }
}
