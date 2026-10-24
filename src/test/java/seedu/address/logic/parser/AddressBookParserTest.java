package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.Messages.MESSAGE_UNKNOWN_COMMAND;
import static seedu.address.logic.commands.CommandTestUtil.NAME_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.NAME_DESC_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_NAME_AMY;
import static seedu.address.logic.commands.CommandTestUtil.VALID_NAME_BOB;
import static seedu.address.logic.commands.EditCommand.EDIT_SEPARATOR;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.testutil.Assert.assertThrows;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.commands.ClearCommand;
import seedu.address.logic.commands.DeleteCommand;
import seedu.address.logic.commands.EditCommand;
import seedu.address.logic.commands.EditCommand.EditPersonDescriptor;
import seedu.address.logic.commands.ExitCommand;
import seedu.address.logic.commands.ExportCommand;
import seedu.address.logic.commands.FindCommand;
import seedu.address.logic.commands.HelpCommand;
import seedu.address.logic.commands.ListCommand;
import seedu.address.logic.commands.UndoCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.ExactPersonMatchDescriptor;
import seedu.address.model.person.FindPersonDescriptor;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.PersonMatchesExactFieldsPredicate;
import seedu.address.model.person.PersonMatchesFlagsPredicate;
import seedu.address.testutil.EditPersonDescriptorBuilder;
import seedu.address.testutil.PersonBuilder;
import seedu.address.testutil.PersonUtil;

public class AddressBookParserTest {

    private final AddressBookParser parser = new AddressBookParser();

    @Test
    public void parseCommand_add() throws Exception {
        Person person = new PersonBuilder().build();
        AddCommand command = (AddCommand) parser.parseCommand(PersonUtil.getAddCommand(person));
        assertEquals(new AddCommand(person), command);
    }

    @Test
    public void parseCommand_clear() throws Exception {
        assertTrue(parser.parseCommand(ClearCommand.COMMAND_WORD) instanceof ClearCommand);
        assertTrue(parser.parseCommand(ClearCommand.COMMAND_WORD + " 3") instanceof ClearCommand);
    }


    @Test
    public void parseCommand_delete() throws Exception {
        ExactPersonMatchDescriptor descriptor = new ExactPersonMatchDescriptor();
        descriptor.setName(new Name("John Doe"));

        DeleteCommand expectedCommand =
                new DeleteCommand(new PersonMatchesExactFieldsPredicate(descriptor));

        assertEquals(expectedCommand,
                parser.parseCommand(DeleteCommand.COMMAND_WORD + " n/John Doe"));
    }

    @Test
    public void parseCommand_edit() throws Exception {
        ExactPersonMatchDescriptor searchDescriptor = new ExactPersonMatchDescriptor();
        searchDescriptor.setName(new Name(VALID_NAME_AMY));

        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder()
                .withName(VALID_NAME_BOB)
                .build();

        EditCommand expectedCommand = new EditCommand(
                new PersonMatchesExactFieldsPredicate(searchDescriptor), descriptor);

        String userInput = EditCommand.COMMAND_WORD + NAME_DESC_AMY
                + " " + EDIT_SEPARATOR + NAME_DESC_BOB;

        assertEquals(expectedCommand, parser.parseCommand(userInput));
    }

    @Test
    public void parseCommand_exit() throws Exception {
        assertTrue(parser.parseCommand(ExitCommand.COMMAND_WORD) instanceof ExitCommand);
        assertTrue(parser.parseCommand(ExitCommand.COMMAND_WORD + " 3") instanceof ExitCommand);
    }

    @Test
    public void parseCommand_find() throws Exception {
        FindCommand command = (FindCommand) parser.parseCommand(FindCommand.COMMAND_WORD + " n/Alice");
        FindPersonDescriptor descriptor = new FindPersonDescriptor();
        descriptor.setName("Alice");
        assertEquals(new FindCommand(new PersonMatchesFlagsPredicate(descriptor), List.of(PREFIX_NAME)), command);
    }

    @Test
    public void parseCommand_help() throws Exception {
        assertTrue(parser.parseCommand(HelpCommand.COMMAND_WORD) instanceof HelpCommand);
        assertTrue(parser.parseCommand(HelpCommand.COMMAND_WORD + " 3") instanceof HelpCommand);
    }

    @Test
    public void parseCommand_list() throws Exception {
        assertTrue(parser.parseCommand(ListCommand.COMMAND_WORD) instanceof ListCommand);
        assertTrue(parser.parseCommand(ListCommand.COMMAND_WORD + " 3") instanceof ListCommand);
    }

    @Test
    public void parseCommand_export() throws Exception {
        Path directory = Paths.get("exports");
        AddressBookParser exportParser = new AddressBookParser(directory);

        assertEquals(new ExportCommand(directory), exportParser.parseCommand(ExportCommand.COMMAND_WORD));
        assertEquals(new ExportCommand(directory), exportParser.parseCommand(ExportCommand.COMMAND_WORD + "   "));
    }

    @Test
    public void parseCommand_exportDefaultParser_usesWorkingDirectory() throws Exception {
        assertEquals(new ExportCommand(Paths.get("")), parser.parseCommand(ExportCommand.COMMAND_WORD));
    }

    @Test
    public void parseCommand_exportWithArguments_throwsParseException() {
        assertThrows(ParseException.class,
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, ExportCommand.MESSAGE_USAGE), ()
                    -> parser.parseCommand(ExportCommand.COMMAND_WORD + " contacts.csv"));
    }

    @Test
    public void constructor_nullExportDirectory_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new AddressBookParser(null));
    }

    @Test
    public void parseCommand_undo() throws Exception {
        assertTrue(parser.parseCommand(UndoCommand.COMMAND_WORD) instanceof UndoCommand);
        assertTrue(parser.parseCommand(UndoCommand.COMMAND_WORD + "   ") instanceof UndoCommand);
    }

    @Test
    public void parseCommand_undoWithArguments_throwsParseException() {
        assertThrows(ParseException.class,
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, UndoCommand.MESSAGE_USAGE), ()
                    -> parser.parseCommand(UndoCommand.COMMAND_WORD + " 1"));
    }

    @Test
    public void parseCommand_unrecognisedInput_throwsParseException() {
        assertThrows(ParseException.class, String.format(MESSAGE_INVALID_COMMAND_FORMAT, HelpCommand.MESSAGE_USAGE), ()
            -> parser.parseCommand(""));
    }

    @Test
    public void parseCommand_unknownCommand_throwsParseException() {
        assertThrows(ParseException.class, MESSAGE_UNKNOWN_COMMAND, () -> parser.parseCommand("unknownCommand"));
    }
}
