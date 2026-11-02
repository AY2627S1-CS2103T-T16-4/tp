package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.Messages.getErrorMessageForDuplicatePrefixes;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_GITHUB;
import static seedu.address.logic.parser.CliSyntax.PREFIX_GROUP;
import static seedu.address.logic.parser.CliSyntax.PREFIX_LINKEDIN;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TELEGRAM;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.DeleteCommand;
import seedu.address.model.person.Address;
import seedu.address.model.person.Email;
import seedu.address.model.person.ExactPersonMatchDescriptor;
import seedu.address.model.person.Github;
import seedu.address.model.person.Group;
import seedu.address.model.person.Linkedin;
import seedu.address.model.person.Name;
import seedu.address.model.person.PersonMatchesExactFieldsPredicate;
import seedu.address.model.person.Phone;
import seedu.address.model.person.Telegram;
import seedu.address.model.tag.Tag;

/**
 * Tests parsing of delete criteria using the shared exact-match parser and field parsers.
 * Covers argument validation and construction of the expected command; detailed field-value
 * boundaries are covered by ParserUtilTest and the corresponding value-class tests.
 */
public class DeleteCommandParserTest {

    private static final String MESSAGE_INVALID_FORMAT =
            String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteCommand.MESSAGE_USAGE);

    private DeleteCommandParser parser = new DeleteCommandParser();

    // EPs: empty or whitespace-only arguments contain no search flags.
    // Boundary value: 0 search flags, immediately below the required minimum of 1.
    @Test
    public void parse_noPrefix_throwsParseException() {
        assertParseFailure(parser, "", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, "     ", MESSAGE_INVALID_FORMAT);
    }

    // EPs: unprefixed text alone or before valid criteria; positional search values are unsupported.
    @Test
    public void parse_nonEmptyPreamble_throwsParseException() {
        assertParseFailure(parser, " Alice", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, " unexpected n/Alice", MESSAGE_INVALID_FORMAT);
    }

    // EPs: unsupported flags alone, before, between, or after known flags; aliases and wrong case are rejected.
    @Test
    public void parse_unknownPrefix_throwsParseException() {
        assertParseFailure(parser, " x/foo", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, " x/foo n/Alice", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, " n/Alice x/foo g/T09", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, " n/Alice g/T09 x/foo", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, " n/Alice ph/91234567", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, " g/T09 N/Alice", MESSAGE_INVALID_FORMAT);
    }

    // Error precedence: unknown flag combined with empty or invalid known value reports invalid command format.
    @Test
    public void parse_unknownPrefixWithInvalidValue_reportsInvalidFormat() {
        // Unknown prefixes are checked before empty or invalid field values.
        assertParseFailure(parser, " n/ x/foo", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, " p/1234 x/foo", MESSAGE_INVALID_FORMAT);
    }

    // EPs: malformed field values; each failure isolates the invalid field (details labelled below).
    @Test
    public void parse_invalidValue_throwsParseException() {
        // EP: name contains a forbidden character.
        assertParseFailure(parser, " n/James&", Name.MESSAGE_CONSTRAINTS);
        // EP: phone contains letters.
        assertParseFailure(parser, " p/abc", Phone.MESSAGE_CONSTRAINTS);
        // EP: phone contains digits only but has an invalid length.
        assertParseFailure(parser, " p/1234", Phone.MESSAGE_CONSTRAINTS);
        // EP: phone has the required length but an invalid leading digit.
        // Boundary value: leading digit 7, immediately below the permitted leading digits 8 and 9.
        assertParseFailure(parser, " p/71234567", Phone.MESSAGE_CONSTRAINTS);
        // EP: email has an invalid format.
        assertParseFailure(parser, " e/not-an-email", Email.MESSAGE_CONSTRAINTS);
        // EP: Telegram handle contains a forbidden character.
        assertParseFailure(parser, " tg/@john", Telegram.MESSAGE_CONSTRAINTS);
        // EP: tag contains a forbidden character.
        assertParseFailure(parser, " t/hubby*", Tag.MESSAGE_CONSTRAINTS);

        // EP: invalid phone alongside a valid name; another valid criterion cannot mask the error.
        assertParseFailure(parser, " n/Alice p/1234", Phone.MESSAGE_CONSTRAINTS);
    }

    // EP: a scalar search flag repeated with different values; each scalar prefix is checked independently.
    // Boundary value: 2 occurrences, the first count exceeding the permitted single scalar flag.
    @Test
    public void parse_duplicateScalarPrefix_throwsParseException() {
        assertParseFailure(parser, " n/Alice n/Bob",
                getErrorMessageForDuplicatePrefixes(PREFIX_NAME));
        assertParseFailure(parser, " p/91234567 p/81234567",
                getErrorMessageForDuplicatePrefixes(PREFIX_PHONE));
        assertParseFailure(parser, " a/First Street a/Second Street",
                getErrorMessageForDuplicatePrefixes(PREFIX_ADDRESS));
        assertParseFailure(parser, " e/alice@example.com e/bob@example.com",
                getErrorMessageForDuplicatePrefixes(PREFIX_EMAIL));
        assertParseFailure(parser, " gh/alice gh/bob",
                getErrorMessageForDuplicatePrefixes(PREFIX_GITHUB));
        assertParseFailure(parser, " li/alice li/bob",
                getErrorMessageForDuplicatePrefixes(PREFIX_LINKEDIN));
        assertParseFailure(parser, " tg/alice tg/bob",
                getErrorMessageForDuplicatePrefixes(PREFIX_TELEGRAM));
    }

    // EP: each supported search field supplied alone; the expected predicate contains exactly that criterion.
    // Boundary value: 1 search flag, the minimum required.
    @Test
    public void parse_singlePrefix_returnsDeleteCommand() {
        assertParseSuccess(parser, " n/Alice Pauline",
                createCommandWith(d -> d.setName(new Name("Alice Pauline"))));
        assertParseSuccess(parser, " p/91234567",
                createCommandWith(d -> d.setPhone(new Phone("91234567"))));
        assertParseSuccess(parser, " a/123 Clementi Ave",
                createCommandWith(d -> d.setAddress(new Address("123 Clementi Ave"))));
        assertParseSuccess(parser, " e/alice@example.com",
                createCommandWith(d -> d.setEmail(new Email("alice@example.com"))));
        assertParseSuccess(parser, " gh/AliceGh",
                createCommandWith(d -> d.setGithub(new Github("AliceGh"))));
        assertParseSuccess(parser, " li/alice-li",
                createCommandWith(d -> d.setLinkedin(new Linkedin("alice-li"))));
        assertParseSuccess(parser, " tg/alicetg",
                createCommandWith(d -> d.setTelegram(new Telegram("alicetg"))));
        assertParseSuccess(parser, " g/T09",
                createCommandWith(d -> d.setGroups(Set.of(new Group("T09")))));
        assertParseSuccess(parser, " t/friend",
                createCommandWith(d -> d.setTags(Set.of(new Tag("friend")))));
    }

    // EP: a recognised search flag with no value, checked independently for every supported field.
    // Boundary value: search value length 0.
    @Test
    public void parse_emptyFlagValue_throwsParseException() {
        List<Prefix> prefixes = List.of(
                PREFIX_NAME, PREFIX_PHONE, PREFIX_EMAIL, PREFIX_ADDRESS,
                PREFIX_GITHUB, PREFIX_LINKEDIN, PREFIX_TELEGRAM,
                PREFIX_GROUP, PREFIX_TAG);
        for (int i = 0; i < prefixes.size(); i++) {
            Prefix prefix = prefixes.get(i);
            assertParseFailure(parser, " " + prefix,
                    String.format(DeleteCommand.MESSAGE_EMPTY_FLAG_VALUE, prefix));
        }
    }

    // EP: search value contains whitespace only; trimming makes it empty.
    // Boundary value: trimmed search value length 0.
    @Test
    public void parse_whitespaceOnlyFlagValue_throwsParseException() {
        assertParseFailure(parser, " n/   ",
                String.format(DeleteCommand.MESSAGE_EMPTY_FLAG_VALUE, PREFIX_NAME));
    }

    // EP: one empty search value alongside a valid criterion; the valid field must not mask the missing value.
    @Test
    public void parse_emptyFlagValueWithValidField_throwsParseException() {
        assertParseFailure(parser, " n/Alice p/",
                String.format(DeleteCommand.MESSAGE_EMPTY_FLAG_VALUE, PREFIX_PHONE));
    }

    // EP: repeated group flags include an empty value, in either order; all search values must be nonempty.
    @Test
    public void parse_repeatedGroupWithEmptyValue_throwsParseException() {
        String expectedMessage = String.format(DeleteCommand.MESSAGE_EMPTY_FLAG_VALUE, PREFIX_GROUP);

        assertParseFailure(parser, " g/ g/T09", expectedMessage);
        assertParseFailure(parser, " g/T09 g/", expectedMessage);
    }

    // EP: repeated tag flags include an empty value, in either order; all search values must be nonempty.
    @Test
    public void parse_repeatedTagWithEmptyValue_throwsParseException() {
        String expectedMessage = String.format(DeleteCommand.MESSAGE_EMPTY_FLAG_VALUE, PREFIX_TAG);

        assertParseFailure(parser, " t/ t/friend", expectedMessage);
        assertParseFailure(parser, " t/friend t/", expectedMessage);
    }

    // EP: all supported search fields supplied together; every criterion is retained in the predicate.
    @Test
    public void parse_allPrefixes_returnsDeleteCommand() {
        String args = " n/Alice p/91234567 a/123 Clementi Ave e/alice@example.com"
                + " gh/AliceGh li/alice-li tg/alicetg g/T09 t/friend";
        DeleteCommand expectedCommand = createCommandWith(d -> {
            d.setName(new Name("Alice"));
            d.setPhone(new Phone("91234567"));
            d.setAddress(new Address("123 Clementi Ave"));
            d.setEmail(new Email("alice@example.com"));
            d.setGithub(new Github("AliceGh"));
            d.setLinkedin(new Linkedin("alice-li"));
            d.setTelegram(new Telegram("alicetg"));
            d.setGroups(Set.of(new Group("T09")));
            d.setTags(Set.of(new Tag("friend")));
        });

        assertParseSuccess(parser, args, expectedCommand);
    }

    // EP: repeated group flags with distinct nonempty values; all requested groups must be retained.
    @Test
    public void parse_multipleGroups_returnsDeleteCommandWithAllGroups() {
        assertParseSuccess(parser, " g/T09 g/T10",
                createCommandWith(d -> d.setGroups(Set.of(new Group("T09"), new Group("T10")))));
    }

    // EP: repeated tag flags with distinct nonempty values; all requested tags must be retained.
    @Test
    public void parse_multipleTags_returnsDeleteCommandWithAllTags() {
        assertParseSuccess(parser, " t/friend t/colleague",
                createCommandWith(d -> d.setTags(Set.of(new Tag("friend"), new Tag("colleague")))));
    }

    // EP: repeated group flags with identical values; duplicate membership criteria collapse into one.
    @Test
    public void parse_repeatedIdenticalGroup_returnsDeleteCommandWithOneGroup() {
        assertParseSuccess(parser, " g/T09 g/T09",
                createCommandWith(d -> d.setGroups(Set.of(new Group("T09")))));
    }

    // EP: repeated tag flags with identical values; duplicate membership criteria collapse into one.
    @Test
    public void parse_repeatedIdenticalTag_returnsDeleteCommandWithOneTag() {
        assertParseSuccess(parser, " t/friend t/friend",
                createCommandWith(d -> d.setTags(Set.of(new Tag("friend")))));
    }

    // EP: nonempty value with leading and trailing whitespace; trim its edges while preserving the full name.
    @Test
    public void parse_valueWithSurroundingWhitespace_returnsDeleteCommandWithTrimmedValue() {
        assertParseSuccess(parser, " n/  Alice Pauline   ",
                createCommandWith(d -> d.setName(new Name("Alice Pauline"))));
    }

    /**
     * Builds the expected command from exact-match criteria.
     */
    private static DeleteCommand createCommandWith(
            Consumer<ExactPersonMatchDescriptor> setup) {
        ExactPersonMatchDescriptor descriptor = new ExactPersonMatchDescriptor();
        setup.accept(descriptor);
        return new DeleteCommand(new PersonMatchesExactFieldsPredicate(descriptor));
    }
}
