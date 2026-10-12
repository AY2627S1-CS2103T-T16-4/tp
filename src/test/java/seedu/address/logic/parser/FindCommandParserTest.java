package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.Messages.getErrorMessageForDuplicatePrefixes;
import static seedu.address.logic.commands.FindCommand.MESSAGE_EMPTY_FLAG_VALUE;
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

import seedu.address.logic.commands.FindCommand;
import seedu.address.model.person.Address;
import seedu.address.model.person.Email;
import seedu.address.model.person.FindPersonDescriptor;
import seedu.address.model.person.Github;
import seedu.address.model.person.Group;
import seedu.address.model.person.Linkedin;
import seedu.address.model.person.Name;
import seedu.address.model.person.PersonMatchesFlagsPredicate;
import seedu.address.model.person.Phone;
import seedu.address.model.person.Telegram;
import seedu.address.model.tag.Tag;

public class FindCommandParserTest {

    private static final String MESSAGE_INVALID_FORMAT =
            String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE);

    private FindCommandParser parser = new FindCommandParser();

    @Test
    public void parse_noFlag_throwsParseException() {
        assertParseFailure(parser, "", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, "     ", MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_preambleOnly_throwsParseException() {
        // old keyword-style input is no longer supported
        assertParseFailure(parser, " alice", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, " alice bob", MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_preambleBeforeValidFlag_throwsParseException() {
        assertParseFailure(parser, " alice n/Bob", MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_unknownFlagFirst_throwsParseException() {
        assertParseFailure(parser, " x/foo", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, " x/foo n/Alice", MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_blankValue_throwsParseException() {
        assertParseFailure(parser, " n/", String.format(MESSAGE_EMPTY_FLAG_VALUE, PREFIX_NAME));
        assertParseFailure(parser, " p/", String.format(MESSAGE_EMPTY_FLAG_VALUE, PREFIX_PHONE));
        assertParseFailure(parser, " a/", String.format(MESSAGE_EMPTY_FLAG_VALUE, PREFIX_ADDRESS));
        assertParseFailure(parser, " e/", String.format(MESSAGE_EMPTY_FLAG_VALUE, PREFIX_EMAIL));
        assertParseFailure(parser, " gh/", String.format(MESSAGE_EMPTY_FLAG_VALUE, PREFIX_GITHUB));
        assertParseFailure(parser, " li/", String.format(MESSAGE_EMPTY_FLAG_VALUE, PREFIX_LINKEDIN));
        assertParseFailure(parser, " tg/", String.format(MESSAGE_EMPTY_FLAG_VALUE, PREFIX_TELEGRAM));
        assertParseFailure(parser, " g/", String.format(MESSAGE_EMPTY_FLAG_VALUE, PREFIX_GROUP));
        assertParseFailure(parser, " t/", String.format(MESSAGE_EMPTY_FLAG_VALUE, PREFIX_TAG));

        // whitespace only
        assertParseFailure(parser, " n/    ", String.format(MESSAGE_EMPTY_FLAG_VALUE, PREFIX_NAME));

        // blank flag alongside a valid flag
        assertParseFailure(parser, " n/Alice g/", String.format(MESSAGE_EMPTY_FLAG_VALUE, PREFIX_GROUP));

        // blank repeated tag
        assertParseFailure(parser, " t/friend t/", String.format(MESSAGE_EMPTY_FLAG_VALUE, PREFIX_TAG));
    }

    @Test
    public void parse_invalidValue_throwsParseException() {
        assertParseFailure(parser, " n/James&", Name.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, " p/1234", Phone.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, " p/71234567", Phone.MESSAGE_CONSTRAINTS); // must begin with 8 or 9
        assertParseFailure(parser, " e/not-an-email", Email.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, " tg/@john", Telegram.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, " t/hubby*", Tag.MESSAGE_CONSTRAINTS);

        // invalid value alongside valid flags
        assertParseFailure(parser, " n/Alice p/1234", Phone.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_duplicateSingleValuedFlag_throwsParseException() {
        assertParseFailure(parser, " n/Alice n/Bob", getErrorMessageForDuplicatePrefixes(PREFIX_NAME));
        assertParseFailure(parser, " g/T09 g/T10", getErrorMessageForDuplicatePrefixes(PREFIX_GROUP));
        assertParseFailure(parser, " gh/alice gh/bob", getErrorMessageForDuplicatePrefixes(PREFIX_GITHUB));
    }

    @Test
    public void parse_singleFlag_returnsFindCommand() {
        assertParseSuccess(parser, " n/Alice Pauline",
                commandWith(List.of(PREFIX_NAME), d -> d.setName(new Name("Alice Pauline"))));
        assertParseSuccess(parser, " p/91234567",
                commandWith(List.of(PREFIX_PHONE), d -> d.setPhone(new Phone("91234567"))));
        assertParseSuccess(parser, " a/123 Clementi Ave",
                commandWith(List.of(PREFIX_ADDRESS), d -> d.setAddress(new Address("123 Clementi Ave"))));
        assertParseSuccess(parser, " e/alice@example.com",
                commandWith(List.of(PREFIX_EMAIL), d -> d.setEmail(new Email("alice@example.com"))));
        assertParseSuccess(parser, " gh/AliceGh",
                commandWith(List.of(PREFIX_GITHUB), d -> d.setGithub(new Github("AliceGh"))));
        assertParseSuccess(parser, " li/alice-li",
                commandWith(List.of(PREFIX_LINKEDIN), d -> d.setLinkedin(new Linkedin("alice-li"))));
        assertParseSuccess(parser, " tg/alicetg",
                commandWith(List.of(PREFIX_TELEGRAM), d -> d.setTelegram(new Telegram("alicetg"))));
        assertParseSuccess(parser, " g/T09",
                commandWith(List.of(PREFIX_GROUP), d -> d.setGroup(new Group("T09"))));
        assertParseSuccess(parser, " t/friend",
                commandWith(List.of(PREFIX_TAG), d -> d.setTags(Set.of(new Tag("friend")))));
    }

    @Test
    public void parse_valuesAreNormalised_returnsFindCommand() {
        // name is stored as block letters, so the case of the input does not matter
        assertParseSuccess(parser, " n/alice pauline",
                commandWith(List.of(PREFIX_NAME), d -> d.setName(new Name("ALICE PAULINE"))));

        // email, group and tag are stored in lowercase
        assertParseSuccess(parser, " e/Alice@Example.COM",
                commandWith(List.of(PREFIX_EMAIL), d -> d.setEmail(new Email("alice@example.com"))));
        assertParseSuccess(parser, " g/t09",
                commandWith(List.of(PREFIX_GROUP), d -> d.setGroup(new Group("T09"))));
        assertParseSuccess(parser, " t/FRIEND",
                commandWith(List.of(PREFIX_TAG), d -> d.setTags(Set.of(new Tag("friend")))));
    }

    @Test
    public void parse_whitespaceAroundValues_returnsFindCommand() {
        assertParseSuccess(parser, " \n n/  Alice Pauline  \t g/  T09 \n",
                commandWith(List.of(PREFIX_NAME, PREFIX_GROUP), d -> {
                    d.setName(new Name("Alice Pauline"));
                    d.setGroup(new Group("T09"));
                }));
    }

    @Test
    public void parse_multipleFlags_returnsFindCommand() {
        FindCommand expectedCommand = commandWith(List.of(PREFIX_NAME, PREFIX_GROUP), d -> {
            d.setName(new Name("Lim Jun Han"));
            d.setGroup(new Group("T09"));
        });
        assertParseSuccess(parser, " n/Lim Jun Han g/T09", expectedCommand);
    }

    @Test
    public void parse_flagsInDifferentOrder_returnsSameFindCommand() {
        FindCommand expectedCommand = commandWith(List.of(PREFIX_NAME, PREFIX_GROUP), d -> {
            d.setName(new Name("Lim Jun Han"));
            d.setGroup(new Group("T09"));
        });
        assertParseSuccess(parser, " g/T09 n/Lim Jun Han", expectedCommand);
    }

    @Test
    public void parse_repeatedTagFlag_returnsFindCommandWithAllTags() {
        FindCommand expectedCommand = commandWith(List.of(PREFIX_TAG),
                d -> d.setTags(Set.of(new Tag("friend"), new Tag("classmate"))));
        assertParseSuccess(parser, " t/friend t/classmate", expectedCommand);
    }

    @Test
    public void parse_allFlags_returnsFindCommand() {
        FindCommand expectedCommand = commandWith(
                List.of(PREFIX_NAME, PREFIX_PHONE, PREFIX_ADDRESS, PREFIX_EMAIL, PREFIX_GITHUB, PREFIX_LINKEDIN,
                        PREFIX_TELEGRAM, PREFIX_GROUP, PREFIX_TAG),
                d -> {
                    d.setName(new Name("Alice Pauline"));
                    d.setPhone(new Phone("91234567"));
                    d.setAddress(new Address("123 Clementi Ave"));
                    d.setEmail(new Email("alice@example.com"));
                    d.setGithub(new Github("AliceGh"));
                    d.setLinkedin(new Linkedin("alice-li"));
                    d.setTelegram(new Telegram("alicetg"));
                    d.setGroup(new Group("T09"));
                    d.setTags(Set.of(new Tag("friend")));
                });
        assertParseSuccess(parser, " t/friend g/T09 tg/alicetg li/alice-li gh/AliceGh e/alice@example.com"
                + " a/123 Clementi Ave p/91234567 n/Alice Pauline", expectedCommand);
    }

    private static FindCommand commandWith(List<Prefix> usedPrefixes, Consumer<FindPersonDescriptor> setup) {
        FindPersonDescriptor descriptor = new FindPersonDescriptor();
        setup.accept(descriptor);
        return new FindCommand(new PersonMatchesFlagsPredicate(descriptor), usedPrefixes);
    }

}
