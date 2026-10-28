package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.Messages.MESSAGE_QUOTED_VALUE_HINT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.PinCommand;
import seedu.address.model.person.ExactPersonMatchDescriptor;
import seedu.address.model.person.PersonMatchesExactFieldsPredicate;
import seedu.address.model.person.Phone;

public class PinCommandParserTest {

    private final PinCommandParser parser = new PinCommandParser();

    @Test
    public void parse_phoneWithoutName_returnsPinCommand() {
        ExactPersonMatchDescriptor descriptor = new ExactPersonMatchDescriptor();
        descriptor.setPhone(new Phone("91234567"));
        assertParseSuccess(parser, " p/91234567", new PinCommand(new PersonMatchesExactFieldsPredicate(descriptor)));
    }

    @Test
    public void parse_invalidArguments_usesPinSpecificMessages() {
        assertParseFailure(parser, "", String.format(MESSAGE_INVALID_COMMAND_FORMAT, PinCommand.MESSAGE_USAGE));
        assertParseFailure(parser, " g/", String.format(PinCommand.MESSAGE_EMPTY_FLAG_VALUE, CliSyntax.PREFIX_GROUP));
        assertParseFailure(parser, " x/foo", String.format(MESSAGE_INVALID_COMMAND_FORMAT, PinCommand.MESSAGE_USAGE)
                + "\n" + MESSAGE_QUOTED_VALUE_HINT);
    }

}
