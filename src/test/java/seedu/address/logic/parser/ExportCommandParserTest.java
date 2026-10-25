package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.ExportCommand;
import seedu.address.logic.parser.exceptions.ParseException;

public class ExportCommandParserTest {

    private static final Path DIRECTORY = Paths.get("data");

    private final ExportCommandParser parser = new ExportCommandParser(DIRECTORY);

    @Test
    public void parse_noArguments_returnsExportCommand() throws Exception {
        assertEquals(new ExportCommand(DIRECTORY), parser.parse(""));
    }

    @Test
    public void parse_whitespaceOnly_returnsExportCommand() throws Exception {
        assertEquals(new ExportCommand(DIRECTORY), parser.parse("   \t "));
    }

    @Test
    public void parse_extraArguments_throwsParseException() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, ExportCommand.MESSAGE_USAGE);

        ParseException exception = assertThrows(ParseException.class, () -> parser.parse("contacts.csv"));
        assertEquals(expectedMessage, exception.getMessage());
        assertThrows(ParseException.class, () -> parser.parse(" fn/team"));
    }

    @Test
    public void parse_nullArguments_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> parser.parse(null));
    }

    @Test
    public void constructor_nullDirectory_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new ExportCommandParser(null));
    }
}
