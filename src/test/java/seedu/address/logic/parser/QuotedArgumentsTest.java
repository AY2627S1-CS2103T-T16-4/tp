package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.Messages.MESSAGE_INVALID_QUOTES;
import static seedu.address.logic.Messages.MESSAGE_QUOTED_VALUE_HINT;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.PinCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.ModelManager;

/**
 * Tests quoted values across commands that create, search, and modify contacts.
 */
public class QuotedArgumentsTest {
    private static final String ADDRESS = "10 Road c/o Bob a/Annex";
    private static final String ADDRESS_ARGUMENT = " a/\"" + ADDRESS + "\"";

    private final AddressBookParser parser = new AddressBookParser();
    private final ModelManager model = new ModelManager();

    @Test
    public void execute_quotedAddress_supportsAddPinUnpinAndDelete() throws Exception {
        execute("add n/\"Alice\" p/81110001" + ADDRESS_ARGUMENT);
        assertEquals(ADDRESS.toLowerCase(), model.getAddressBook().getPersonList().getFirst().getAddress().value);

        execute("pin" + ADDRESS_ARGUMENT);
        assertTrue(model.getAddressBook().getPersonList().getFirst().isPinned());
        execute("unpin" + ADDRESS_ARGUMENT);
        assertFalse(model.getAddressBook().getPersonList().getFirst().isPinned());
        execute("delete" + ADDRESS_ARGUMENT);
        assertTrue(model.getAddressBook().getPersonList().isEmpty());
    }

    @Test
    public void execute_quotedAddress_supportsFindAndBothSidesOfEdit() throws Exception {
        execute("add n/Alice p/81110001" + ADDRESS_ARGUMENT);
        execute("add n/Bob p/81110002");
        execute("find" + ADDRESS_ARGUMENT);
        assertEquals(1, model.getFilteredPersonList().size());

        execute("edit" + ADDRESS_ARGUMENT + " >> a/\"Office >> c/o Carol p/Annex\"");
        execute("edit a/\"Office >> c/o Carol p/Annex\" >> a/\"\"");
        assertEquals("", model.getAddressBook().getPersonList().getFirst().getAddress().value);
    }

    @Test
    public void execute_escapedCharacters_canTargetStoredValue() throws Exception {
        String address = " a/\"Bob's \\\"office\\\" C:\\\\Road\"";
        execute("add n/Alice p/81110001" + address);
        assertEquals("bob's \"office\" c:\\road",
                model.getAddressBook().getPersonList().getFirst().getAddress().value);
        execute("delete" + address);
        assertTrue(model.getAddressBook().getPersonList().isEmpty());
    }

    @Test
    public void parse_unknownFlagOutsideQuotes_rejectsWithHint() {
        for (String command : List.of("add n/Alice p/81110001", "pin", "unpin", "delete", "find")) {
            ParseException exception = assertThrows(ParseException.class, () ->
                    parser.parseCommand(command + ADDRESS_ARGUMENT + " x/value"));
            assertTrue(exception.getMessage().contains(MESSAGE_QUOTED_VALUE_HINT));
        }
        for (String args : List.of(ADDRESS_ARGUMENT + " x/value >> n/Alice",
                ADDRESS_ARGUMENT + " >>" + ADDRESS_ARGUMENT + " x/value")) {
            ParseException exception = assertThrows(ParseException.class, () ->
                    parser.parseCommand("edit" + args));
            assertTrue(exception.getMessage().contains(MESSAGE_QUOTED_VALUE_HINT));
        }
    }

    @Test
    public void parse_unquotedFlagLikeValue_stillRejectsWithHint() {
        ParseException exception = assertThrows(ParseException.class, () ->
                parser.parseCommand("pin a/10 Road c/o Bob"));
        assertTrue(exception.getMessage().contains(MESSAGE_QUOTED_VALUE_HINT));
    }

    @Test
    public void parse_partiallyQuotedValue_rejectsWithQuoteError() {
        String address = " a/10 \"Road c/o Bob\"";
        for (String command : List.of("add n/Alice p/81110001" + address, "pin" + address,
                "unpin" + address, "delete" + address, "find" + address,
                "edit" + address + " >> n/Alice", "edit p/81110001 >>" + address)) {
            ParseException exception = assertThrows(ParseException.class, () -> parser.parseCommand(command));
            assertEquals(MESSAGE_INVALID_QUOTES, exception.getMessage());
        }
    }

    @Test
    public void parse_emptyQuotedSearchValue_stillRejectsEmptyValue() {
        ParseException exception = assertThrows(ParseException.class, () ->
                parser.parseCommand("pin a/\"\""));
        assertEquals(String.format(PinCommand.MESSAGE_EMPTY_FLAG_VALUE, CliSyntax.PREFIX_ADDRESS),
                exception.getMessage());
    }

    private void execute(String command) throws Exception {
        parser.parseCommand(command).execute(model);
    }
}
