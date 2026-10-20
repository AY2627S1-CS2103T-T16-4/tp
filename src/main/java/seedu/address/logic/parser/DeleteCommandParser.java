package seedu.address.logic.parser;

import seedu.address.logic.commands.DeleteCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses exact field criteria for deleting one matching contact.
 */
public class DeleteCommandParser implements Parser<DeleteCommand> {

    @Override
    public DeleteCommand parse(String args) throws ParseException {
        return new DeleteCommand(new ExactPersonMatchParser(
                DeleteCommand.MESSAGE_USAGE, DeleteCommand.MESSAGE_EMPTY_FLAG_VALUE).parse(args));
    }
}
