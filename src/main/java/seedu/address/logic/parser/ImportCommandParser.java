package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import java.nio.file.InvalidPathException;
import java.nio.file.Path;

import seedu.address.logic.commands.ImportCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/** Parses the entire argument as a file path, retaining embedded spaces. */
public class ImportCommandParser implements Parser<ImportCommand> {
    @Override
    public ImportCommand parse(String args) throws ParseException {
        requireNonNull(args);
        String path = args.trim();
        if (path.isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, ImportCommand.MESSAGE_USAGE));
        }
        try {
            return new ImportCommand(Path.of(path));
        } catch (InvalidPathException e) {
            throw new ParseException(String.format(ImportCommand.MESSAGE_INVALID_PATH, e.getReason()), e);
        }
    }
}
