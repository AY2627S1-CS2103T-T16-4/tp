package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.SortCommand;
import seedu.address.logic.commands.SortCommand.Field;

public class SortCommandParserTest {
    private final SortCommandParser parser = new SortCommandParser();

    @Test
    public void parse_validArguments_success() throws Exception {
        assertParseSuccess(parser, " name ", new SortCommand(Field.NAME, false));
        assertParseSuccess(parser, "name asc", new SortCommand(Field.NAME, false));
        assertParseSuccess(parser, "\tname \t desc ", new SortCommand(Field.NAME, true));
        assertParseSuccess(parser, "group", new SortCommand(Field.GROUP, false));
        assertParseSuccess(parser, "group asc", new SortCommand(Field.GROUP, false));
        assertParseSuccess(parser, "group desc", new SortCommand(Field.GROUP, true));
        assertEquals(new SortCommand(Field.NAME, true), new AddressBookParser().parseCommand("sort name desc"));
        assertEquals(new SortCommand(Field.GROUP, false), new AddressBookParser().parseCommand("sort group"));
    }

    @Test
    public void parse_invalidArguments_failure() {
        for (String args : List.of("", " ", "phone", "asc", "name ascending", "group down", "name asc extra")) {
            assertParseFailure(parser, args, String.format(MESSAGE_INVALID_COMMAND_FORMAT, SortCommand.MESSAGE_USAGE));
        }
    }
}
