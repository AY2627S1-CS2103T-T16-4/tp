package seedu.address.logic.parser;

import seedu.address.logic.commands.PinCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses exact field criteria for pinning one matching contact.
 */
public class PinCommandParser implements Parser<PinCommand> {

    @Override
    public PinCommand parse(String args) throws ParseException {
        return new PinCommand(new ExactPersonMatchParser(
                PinCommand.MESSAGE_USAGE, PinCommand.MESSAGE_EMPTY_FLAG_VALUE).parse(args));
    }
}
