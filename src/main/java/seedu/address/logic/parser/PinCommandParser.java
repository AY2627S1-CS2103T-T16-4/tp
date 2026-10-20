package seedu.address.logic.parser;

import seedu.address.logic.commands.PersonTarget;
import seedu.address.logic.commands.PinCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses a full name and optional complete phone number for pinning a contact.
 */
public class PinCommandParser implements Parser<PinCommand> {

    @Override
    public PinCommand parse(String args) throws ParseException {
        PersonTarget target = ParserUtil.parsePersonTarget(args, PinCommand.MESSAGE_USAGE);
        return new PinCommand(target.name(), target.phone());
    }
}
