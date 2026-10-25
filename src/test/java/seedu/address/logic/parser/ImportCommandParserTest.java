package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.ImportCommand;
import seedu.address.logic.parser.exceptions.ParseException;

public class ImportCommandParserTest {
    private static final String FILE_PATH = "data/my contacts.csv";
    private final ImportCommandParser parser = new ImportCommandParser();

    @Test
    public void parse_pathWithSpaces_preservesPath() {
        assertParseSuccess(parser, " " + FILE_PATH + " ", new ImportCommand(Path.of(FILE_PATH)));
        String absolutePath = Path.of(FILE_PATH).toAbsolutePath().toString();
        assertParseSuccess(parser, absolutePath, new ImportCommand(Path.of(absolutePath)));
    }

    @Test
    public void parseCommand_import_dispatchesToParser() throws Exception {
        assertEquals(new ImportCommand(Path.of(FILE_PATH)),
                new AddressBookParser().parseCommand(ImportCommand.COMMAND_WORD + " " + FILE_PATH));
    }

    @Test
    public void parse_emptyArguments_rejectsInput() {
        assertParseFailure(parser, "  ", String.format(MESSAGE_INVALID_COMMAND_FORMAT, ImportCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_invalidPath_throwsParseException() {
        assertThrows(ParseException.class, () -> parser.parse("bad\u0000path"));
    }

    @Test
    public void parse_nullArguments_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> parser.parse(null));
    }
}
