package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import seedu.address.logic.commands.SortCommand;
import seedu.address.logic.commands.SortCommand.Field;
import seedu.address.logic.parser.exceptions.ParseException;

/** Parses the field and optional direction for a sort command. */
public class SortCommandParser implements Parser<SortCommand> {
    @Override
    public SortCommand parse(String args) throws ParseException {
        String[] words = args.trim().split("\\s+");
        if (words.length > 2 || !isValidField(words[0])) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, SortCommand.MESSAGE_USAGE));
        }
        if (words.length == 2 && !isValidDirection(words[1])) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, SortCommand.MESSAGE_USAGE));
        }
        return new SortCommand(words[0].equals("name") ? Field.NAME : Field.GROUP,
                words.length == 2 && words[1].equals("desc"));
    }

    private static boolean isValidField(String field) {
        return field.equals("name") || field.equals("group");
    }

    private static boolean isValidDirection(String direction) {
        return direction.equals("asc") || direction.equals("desc");
    }
}
