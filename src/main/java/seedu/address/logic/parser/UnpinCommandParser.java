package seedu.address.logic.parser;

import seedu.address.logic.commands.UnpinCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses exact field criteria for unpinning one matching contact.
 */
public class UnpinCommandParser implements Parser<UnpinCommand> {

    @Override
    public UnpinCommand parse(String args) throws ParseException {
        return new UnpinCommand(new ExactPersonMatchParser(
                UnpinCommand.MESSAGE_USAGE, UnpinCommand.MESSAGE_EMPTY_FLAG_VALUE).parse(args));
    }
}
