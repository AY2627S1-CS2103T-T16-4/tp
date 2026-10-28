package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.Messages.MESSAGE_QUOTED_VALUE_HINT;
import static seedu.address.logic.Messages.getErrorMessageForDuplicatePrefixes;
import static seedu.address.logic.commands.CommandTestUtil.NAME_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.PHONE_DESC_AMY;
import static seedu.address.logic.parser.CliSyntax.PREFIX_GROUP;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.AMY;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.UnpinCommand;
import seedu.address.model.person.ExactPersonMatchDescriptor;
import seedu.address.model.person.PersonMatchesExactFieldsPredicate;

public class UnpinCommandParserTest {
    private static final String UNKNOWN_PREFIX_ARGUMENT = " x/foo";
    private static final String MESSAGE_INVALID_FORMAT =
            String.format(MESSAGE_INVALID_COMMAND_FORMAT, UnpinCommand.MESSAGE_USAGE);

    private final UnpinCommandParser parser = new UnpinCommandParser();

    @Test
    public void parse_phoneOnly_returnsUnpinCommand() {
        ExactPersonMatchDescriptor descriptor = new ExactPersonMatchDescriptor();
        descriptor.setPhone(AMY.getPhone());
        assertParseSuccess(parser, PHONE_DESC_AMY,
                new UnpinCommand(new PersonMatchesExactFieldsPredicate(descriptor)));
    }

    @Test
    public void parse_multipleCriteria_returnsUnpinCommand() {
        ExactPersonMatchDescriptor descriptor = new ExactPersonMatchDescriptor();
        descriptor.setName(AMY.getName());
        descriptor.setPhone(AMY.getPhone());
        assertParseSuccess(parser, NAME_DESC_AMY + PHONE_DESC_AMY,
                new UnpinCommand(new PersonMatchesExactFieldsPredicate(descriptor)));
    }

    @Test
    public void parseCommand_unpin_dispatchesToParser() throws Exception {
        assertEquals(parser.parse(PHONE_DESC_AMY),
                new AddressBookParser().parseCommand(UnpinCommand.COMMAND_WORD + PHONE_DESC_AMY));
    }

    @Test
    public void parse_missingOrUnknownArguments_usesUnpinUsage() {
        assertParseFailure(parser, "", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, UNKNOWN_PREFIX_ARGUMENT,
                MESSAGE_INVALID_FORMAT + "\n" + MESSAGE_QUOTED_VALUE_HINT);
        assertParseFailure(parser, NAME_DESC_AMY + UNKNOWN_PREFIX_ARGUMENT,
                MESSAGE_INVALID_FORMAT + "\n" + MESSAGE_QUOTED_VALUE_HINT);
    }

    @Test
    public void parse_emptyValue_usesUnpinMessage() {
        assertParseFailure(parser, " " + PREFIX_GROUP,
                String.format(UnpinCommand.MESSAGE_EMPTY_FLAG_VALUE, PREFIX_GROUP));
    }

    @Test
    public void parse_duplicatePhone_rejectsInput() {
        assertParseFailure(parser, PHONE_DESC_AMY + PHONE_DESC_AMY,
                getErrorMessageForDuplicatePrefixes(PREFIX_PHONE));
    }

    @Test
    public void parse_nullArguments_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> parser.parse(null));
    }
}
