package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.Messages.MESSAGE_QUOTED_VALUE_HINT;
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
import seedu.address.model.person.FindPersonDescriptor;
import seedu.address.model.person.PersonMatchesFlagsPredicate;

public class FindCommandParserTest {

    private static final String MESSAGE_INVALID_FORMAT =
            String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE);
    private static final String MESSAGE_UNKNOWN_FLAG = MESSAGE_INVALID_FORMAT + "\n" + MESSAGE_QUOTED_VALUE_HINT;

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
        assertParseFailure(parser, " x/foo", MESSAGE_UNKNOWN_FLAG);
        assertParseFailure(parser, " x/foo n/Alice", MESSAGE_UNKNOWN_FLAG);
    }

    @Test
    public void parse_unknownFlagAfterValidFlag_throwsParseException() {
        // in the middle
        assertParseFailure(parser, " n/Alice x/foo g/T09", MESSAGE_UNKNOWN_FLAG);

        // at the end
        assertParseFailure(parser, " n/Alice g/T09 x/foo", MESSAGE_UNKNOWN_FLAG);

        // longer flag
        assertParseFailure(parser, " n/Alice ph/91234567", MESSAGE_UNKNOWN_FLAG);

        // flags are case-sensitive
        assertParseFailure(parser, " g/T09 N/Alice", MESSAGE_UNKNOWN_FLAG);

        // takes priority over a blank value and other value errors
        assertParseFailure(parser, " n/ x/foo", MESSAGE_UNKNOWN_FLAG);
        assertParseFailure(parser, " p/1234 x/foo", MESSAGE_UNKNOWN_FLAG);
    }

    @Test
    public void parse_slashInsideValue_returnsFindCommand() {
        // a slash that does not follow a word, so it is not mistaken for a flag
        assertParseSuccess(parser, " a/123/4 Clementi Ave",
                commandWith(List.of(PREFIX_ADDRESS), d -> d.setAddress("123/4 Clementi Ave")));

        // a slash that is not preceded by whitespace
        assertParseSuccess(parser, " li/in/alice",
                commandWith(List.of(PREFIX_LINKEDIN), d -> d.setLinkedin("in/alice")));
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
    public void parse_partialOrInvalidFormatValue_returnsFindCommand() {
        // values are raw search text, so they need not be valid full field values
        assertParseSuccess(parser, " n/ali",
                commandWith(List.of(PREFIX_NAME), d -> d.setName("ali")));
        assertParseSuccess(parser, " n/James&",
                commandWith(List.of(PREFIX_NAME), d -> d.setName("James&")));
        assertParseSuccess(parser, " p/98",
                commandWith(List.of(PREFIX_PHONE), d -> d.setPhone("98")));
        assertParseSuccess(parser, " p/71234567",
                commandWith(List.of(PREFIX_PHONE), d -> d.setPhone("71234567")));
        assertParseSuccess(parser, " e/alice",
                commandWith(List.of(PREFIX_EMAIL), d -> d.setEmail("alice")));
        assertParseSuccess(parser, " tg/@john",
                commandWith(List.of(PREFIX_TELEGRAM), d -> d.setTelegram("@john")));
        assertParseSuccess(parser, " t/fri",
                commandWith(List.of(PREFIX_TAG), d -> d.setTags(Set.of("fri"))));
    }

    @Test
    public void parse_duplicateSingleValuedFlag_throwsParseException() {
        assertParseFailure(parser, " n/Alice n/Bob", getErrorMessageForDuplicatePrefixes(PREFIX_NAME));
        assertParseFailure(parser, " gh/alice gh/bob", getErrorMessageForDuplicatePrefixes(PREFIX_GITHUB));
    }

    @Test
    public void parse_singleFlag_returnsFindCommand() {
        assertParseSuccess(parser, " n/Alice Pauline",
                commandWith(List.of(PREFIX_NAME), d -> d.setName("Alice Pauline")));
        assertParseSuccess(parser, " p/91234567",
                commandWith(List.of(PREFIX_PHONE), d -> d.setPhone("91234567")));
        assertParseSuccess(parser, " a/123 Clementi Ave",
                commandWith(List.of(PREFIX_ADDRESS), d -> d.setAddress("123 Clementi Ave")));
        assertParseSuccess(parser, " e/alice@example.com",
                commandWith(List.of(PREFIX_EMAIL), d -> d.setEmail("alice@example.com")));
        assertParseSuccess(parser, " gh/AliceGh",
                commandWith(List.of(PREFIX_GITHUB), d -> d.setGithub("AliceGh")));
        assertParseSuccess(parser, " li/alice-li",
                commandWith(List.of(PREFIX_LINKEDIN), d -> d.setLinkedin("alice-li")));
        assertParseSuccess(parser, " tg/alicetg",
                commandWith(List.of(PREFIX_TELEGRAM), d -> d.setTelegram("alicetg")));
        assertParseSuccess(parser, " g/T09",
                commandWith(List.of(PREFIX_GROUP), d -> d.setGroups(Set.of("T09"))));
        assertParseSuccess(parser, " t/friend",
                commandWith(List.of(PREFIX_TAG), d -> d.setTags(Set.of("friend"))));
    }

    @Test
    public void parse_valuesKeepTheirCase_returnsFindCommand() {
        // case is handled when matching, so the search text is kept as typed
        assertParseSuccess(parser, " n/alice PAULINE",
                commandWith(List.of(PREFIX_NAME), d -> d.setName("alice PAULINE")));
        assertParseSuccess(parser, " gh/AliceGh",
                commandWith(List.of(PREFIX_GITHUB), d -> d.setGithub("AliceGh")));
        assertParseSuccess(parser, " t/FRIEND",
                commandWith(List.of(PREFIX_TAG), d -> d.setTags(Set.of("FRIEND"))));
    }

    @Test
    public void parse_repeatedGroupFlag_returnsFindCommandWithAllGroups() {
        FindCommand expectedCommand = commandWith(List.of(PREFIX_GROUP),
                d -> d.setGroups(Set.of("T09", "lab3")));
        assertParseSuccess(parser, " g/T09 g/lab3", expectedCommand);
    }

    @Test
    public void parse_whitespaceAroundValues_returnsFindCommand() {
        assertParseSuccess(parser, " \n n/  Alice Pauline  \t g/  T09 \n",
                commandWith(List.of(PREFIX_NAME, PREFIX_GROUP), d -> {
                    d.setName("Alice Pauline");
                    d.setGroups(Set.of("T09"));
                }));
    }

    @Test
    public void parse_multipleFlags_returnsFindCommand() {
        FindCommand expectedCommand = commandWith(List.of(PREFIX_NAME, PREFIX_GROUP), d -> {
            d.setName("Lim Jun Han");
            d.setGroups(Set.of("T09"));
        });
        assertParseSuccess(parser, " n/Lim Jun Han g/T09", expectedCommand);
    }

    @Test
    public void parse_flagsInDifferentOrder_returnsSameFindCommand() {
        FindCommand expectedCommand = commandWith(List.of(PREFIX_NAME, PREFIX_GROUP), d -> {
            d.setName("Lim Jun Han");
            d.setGroups(Set.of("T09"));
        });
        assertParseSuccess(parser, " g/T09 n/Lim Jun Han", expectedCommand);
    }

    @Test
    public void parse_repeatedTagFlag_returnsFindCommandWithAllTags() {
        FindCommand expectedCommand = commandWith(List.of(PREFIX_TAG),
                d -> d.setTags(Set.of("friend", "classmate")));
        assertParseSuccess(parser, " t/friend t/classmate", expectedCommand);
    }

    @Test
    public void parse_allFlags_returnsFindCommand() {
        FindCommand expectedCommand = commandWith(
                List.of(PREFIX_NAME, PREFIX_PHONE, PREFIX_ADDRESS, PREFIX_EMAIL, PREFIX_GITHUB, PREFIX_LINKEDIN,
                        PREFIX_TELEGRAM, PREFIX_GROUP, PREFIX_TAG),
                d -> {
                    d.setName("Alice Pauline");
                    d.setPhone("91234567");
                    d.setAddress("123 Clementi Ave");
                    d.setEmail("alice@example.com");
                    d.setGithub("AliceGh");
                    d.setLinkedin("alice-li");
                    d.setTelegram("alicetg");
                    d.setGroups(Set.of("T09"));
                    d.setTags(Set.of("friend"));
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
