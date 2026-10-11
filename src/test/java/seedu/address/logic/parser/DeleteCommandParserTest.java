package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.DeleteCommand;
import seedu.address.model.person.Name;
import seedu.address.model.person.Phone;



/**
 * As we are only doing white-box testing, our test cases do not cover path variations
 * outside of the DeleteCommand code. For example, inputs "1" and "1 abc" take the
 * same path through the DeleteCommand, and therefore we test only one of them.
 * The path variation for those two cases occurs inside the ParserUtil, and
 * therefore should be covered by the ParserUtilTest.
 */
public class DeleteCommandParserTest {

    private DeleteCommandParser parser = new DeleteCommandParser();

    @Test
    public void parse_nameOnly_returnsDeleteCommand() {
        assertParseSuccess(parser, " n/John Doe",
                new DeleteCommand(new Name("John Doe"), Optional.empty()));
    }

    @Test
    public void parse_nameAndPhone_returnsDeleteCommand() {
        assertParseSuccess(parser, " n/John Doe p/81231234",
                new DeleteCommand(new Name("John Doe"),
                        Optional.of(new Phone("81231234"))));
    }

    @Test
    public void parse_reversedPrefixOrder_returnsDeleteCommand() {
        assertParseSuccess(parser, " p/81231234 n/John Doe",
                new DeleteCommand(new Name("John Doe"),
                        Optional.of(new Phone("81231234"))));
    }

    @Test
    public void parse_surroundingWhitespace_returnsDeleteCommand() {
        assertParseSuccess(parser, "   n/  John Doe   p/  81231234   ",
                new DeleteCommand(new Name("John Doe"),
                        Optional.of(new Phone("81231234"))));
    }

    @Test
    public void parse_missingName_throwsParseException() {
        String expectedMessage = String.format(
                MESSAGE_INVALID_COMMAND_FORMAT, DeleteCommand.MESSAGE_USAGE);

        assertParseFailure(parser, "", expectedMessage);
        assertParseFailure(parser, "   ", expectedMessage);
        assertParseFailure(parser, " p/81231234", expectedMessage);
    }

    @Test
    public void parse_oldIndexSyntax_throwsParseException() {
        assertParseFailure(parser, "1", String.format(
                MESSAGE_INVALID_COMMAND_FORMAT, DeleteCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_nonEmptyPreamble_throwsParseException() {
        assertParseFailure(parser, " unexpected n/John Doe", String.format(
                MESSAGE_INVALID_COMMAND_FORMAT, DeleteCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_invalidName_throwsParseException() {
        assertParseFailure(parser, " n/", Name.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, " n/   ", Name.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, " n/John&", Name.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_invalidPhone_throwsParseException() {
        assertParseFailure(parser, " n/John Doe p/", Phone.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, " n/John Doe p/abc", Phone.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, " n/John Doe p/12", Phone.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_duplicateNamePrefix_throwsParseException() {
        assertParseFailure(parser, " n/John Doe n/Jane Doe",
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_NAME));
    }

    @Test
    public void parse_duplicatePhonePrefix_throwsParseException() {
        assertParseFailure(parser, " n/John Doe p/81231234 p/91234567",
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_PHONE));
    }
}
