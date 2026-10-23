package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.commands.SortCommand.DIRECTION_ASCENDING;
import static seedu.address.logic.commands.SortCommand.DIRECTION_DESCENDING;

import seedu.address.logic.commands.SortCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.SortSettings.Field;

/** Parses the field, optional direction, and optional persistence mode. */
public class SortCommandParser implements Parser<SortCommand> {
    @Override
    public SortCommand parse(String args) throws ParseException {
        requireNonNull(args);
        String[] words = args.trim().split("\\s+");
        if (words.length > 3) {
            throw invalidFormat();
        }
        Field field = Field.fromKeyword(words[0]).orElseThrow(SortCommandParser::invalidFormat);
        boolean isDescending = false;
        boolean isContinuous = false;
        int nextWord = 1;
        if (nextWord < words.length && isValidDirection(words[nextWord])) {
            isDescending = words[nextWord].equals(DIRECTION_DESCENDING);
            nextWord++;
        }
        if (nextWord < words.length) {
            isContinuous = switch (words[nextWord]) {
                case "cont" -> true;
                case "none" -> false;
                default -> throw invalidFormat();
            };
            nextWord++;
        }
        if (nextWord != words.length) {
            throw invalidFormat();
        }
        return new SortCommand(field, isDescending, isContinuous);
    }

    private static boolean isValidDirection(String direction) {
        return direction.equals(DIRECTION_ASCENDING) || direction.equals(DIRECTION_DESCENDING);
    }

    private static ParseException invalidFormat() {
        return new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, SortCommand.MESSAGE_USAGE));
    }
}
