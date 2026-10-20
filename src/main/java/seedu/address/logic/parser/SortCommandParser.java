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
        if (words.length > 2 || !(words[0].equals("name") || words[0].equals("group"))
                || words.length == 2 && !(words[1].equals("asc") || words[1].equals("desc"))) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, SortCommand.MESSAGE_USAGE));
        }
        return new SortCommand(words[0].equals("name") ? Field.NAME : Field.GROUP,
                words.length == 2 && words[1].equals("desc"));
    }
}
