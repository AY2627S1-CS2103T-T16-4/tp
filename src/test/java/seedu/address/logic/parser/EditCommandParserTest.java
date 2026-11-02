package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.commands.CommandTestUtil.ADDRESS_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.EMAIL_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.GITHUB_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.GROUP_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_EMAIL_DESC;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_NAME_DESC;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_PHONE_DESC;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_TAG_DESC;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_TELEGRAM_DESC;
import static seedu.address.logic.commands.CommandTestUtil.LINKEDIN_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.NAME_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.PHONE_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.PHONE_DESC_BOB;
import static seedu.address.logic.commands.CommandTestUtil.PREAMBLE_NON_EMPTY;
import static seedu.address.logic.commands.CommandTestUtil.TAG_DESC_FRIEND;
import static seedu.address.logic.commands.CommandTestUtil.TAG_DESC_HUSBAND;
import static seedu.address.logic.commands.CommandTestUtil.TELEGRAM_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.VALID_ADDRESS_AMY;
import static seedu.address.logic.commands.CommandTestUtil.VALID_EMAIL_AMY;
import static seedu.address.logic.commands.CommandTestUtil.VALID_GITHUB_AMY;
import static seedu.address.logic.commands.CommandTestUtil.VALID_GROUP_AMY;
import static seedu.address.logic.commands.CommandTestUtil.VALID_LINKEDIN_AMY;
import static seedu.address.logic.commands.CommandTestUtil.VALID_NAME_AMY;
import static seedu.address.logic.commands.CommandTestUtil.VALID_PHONE_AMY;
import static seedu.address.logic.commands.CommandTestUtil.VALID_PHONE_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TAG_FRIEND;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TAG_HUSBAND;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TELEGRAM_AMY;
import static seedu.address.logic.commands.EditCommand.EDIT_SEPARATOR;
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
import java.util.Map;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.EditCommand;
import seedu.address.logic.commands.EditCommand.EditPersonDescriptor;
import seedu.address.model.person.Email;
import seedu.address.model.person.ExactPersonMatchDescriptor;
import seedu.address.model.person.Name;
import seedu.address.model.person.PersonMatchesExactFieldsPredicate;
import seedu.address.model.person.Phone;
import seedu.address.model.person.Telegram;
import seedu.address.model.tag.Tag;
import seedu.address.testutil.EditPersonDescriptorBuilder;

public class EditCommandParserTest {

    private static final String SEPARATOR = " " + EDIT_SEPARATOR;
    private static final String SEARCH_AND_SEPARATOR = PHONE_DESC_AMY + SEPARATOR;
    private static final String GROUP_EMPTY = " " + PREFIX_GROUP;
    private static final String TAG_EMPTY = " " + PREFIX_TAG;

    private static final String MESSAGE_INVALID_FORMAT =
            String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditCommand.MESSAGE_USAGE);

    private final EditCommandParser parser = new EditCommandParser();

    // EPs: missing command sections or incorrect separator count (individual cases labelled below).
    @Test
    public void parse_missingParts_failure() {
        // EP: empty input. Boundary value: 0 search flags and 0 update flags.
        assertParseFailure(parser, "", MESSAGE_INVALID_FORMAT);

        // EP: missing separator. Boundary value: 0 separators, immediately below the required count of 1.
        assertParseFailure(parser, PHONE_DESC_AMY + NAME_DESC_AMY,
                MESSAGE_INVALID_FORMAT);

        // EP: empty search section. Boundary value: 0 search flags, below the required minimum of 1.
        assertParseFailure(parser, SEPARATOR + NAME_DESC_AMY,
                MESSAGE_INVALID_FORMAT);

        // EP: empty update section. Boundary value: 0 update flags, below the required minimum of 1.
        assertParseFailure(parser, SEARCH_AND_SEPARATOR,
                EditCommand.MESSAGE_NOT_EDITED);

        // EP: excess separators. Boundary value: 2 separators, immediately above the required count of 1.
        assertParseFailure(parser,
                SEARCH_AND_SEPARATOR + NAME_DESC_AMY + SEPARATOR + EMAIL_DESC_AMY,
                MESSAGE_INVALID_FORMAT);
    }

    // EPs: unexpected text before search flags or before update flags; neither section accepts a preamble.
    @Test
    public void parse_invalidPreamble_failure() {
        // EP: nonempty preamble in the search section.
        assertParseFailure(parser,
                PREAMBLE_NON_EMPTY + SEARCH_AND_SEPARATOR + NAME_DESC_AMY,
                MESSAGE_INVALID_FORMAT);

        // EP: nonempty preamble in the update section.
        assertParseFailure(parser,
                SEARCH_AND_SEPARATOR + " " + PREAMBLE_NON_EMPTY + NAME_DESC_AMY,
                MESSAGE_INVALID_FORMAT);
    }

    // EP: a search flag has no value; even optional fields require nonempty search criteria.
    // Boundary value: search value length 0.
    @Test
    public void parse_emptySearchValue_failure() {
        // Unlike updates, every search field requires a nonempty value.
        for (Prefix prefix : List.of(PREFIX_NAME, PREFIX_PHONE, PREFIX_EMAIL, PREFIX_ADDRESS,
                PREFIX_GITHUB, PREFIX_LINKEDIN, PREFIX_TELEGRAM, PREFIX_GROUP, PREFIX_TAG)) {
            assertParseFailure(parser,
                    " " + prefix + SEPARATOR + NAME_DESC_AMY,
                    String.format(EditCommand.MESSAGE_EMPTY_FLAG_VALUE, prefix));
        }
    }

    // EPs: unsupported flag after a known search or update flag; it must not be absorbed into the value.
    @Test
    public void parse_unknownFlag_failure() {
        assertParseFailure(parser,
                PHONE_DESC_AMY + " x/value" + SEPARATOR + NAME_DESC_AMY, MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser,
                SEARCH_AND_SEPARATOR + NAME_DESC_AMY + " x/value", MESSAGE_INVALID_FORMAT);
    }

    // EPs: invalid search phone or invalid update value; all other arguments are valid in each case.
    @Test
    public void parse_invalidValue_failure() {
        // EP: search phone violates the phone format constraint.
        assertParseFailure(parser,
                INVALID_PHONE_DESC + SEPARATOR + NAME_DESC_AMY,
                Phone.MESSAGE_CONSTRAINTS);

        // EP: update name contains a forbidden character.
        assertParseFailure(parser,
                SEARCH_AND_SEPARATOR + INVALID_NAME_DESC,
                Name.MESSAGE_CONSTRAINTS);
        // EP: update phone violates the phone format constraint.
        assertParseFailure(parser,
                SEARCH_AND_SEPARATOR + INVALID_PHONE_DESC,
                Phone.MESSAGE_CONSTRAINTS);
        // EP: update email has an invalid format.
        assertParseFailure(parser,
                SEARCH_AND_SEPARATOR + INVALID_EMAIL_DESC,
                Email.MESSAGE_CONSTRAINTS);
        // EP: update Telegram handle contains a forbidden character.
        assertParseFailure(parser,
                SEARCH_AND_SEPARATOR + INVALID_TELEGRAM_DESC,
                Telegram.MESSAGE_CONSTRAINTS);
        // EP: update tag contains a forbidden character.
        assertParseFailure(parser,
                SEARCH_AND_SEPARATOR + INVALID_TAG_DESC,
                Tag.MESSAGE_CONSTRAINTS);
    }

    // EP: all supported update fields supplied with valid values, including multiple distinct tags.
    @Test
    public void parse_allFieldsSpecified_success() {
        String userInput = SEARCH_AND_SEPARATOR
                + NAME_DESC_AMY + PHONE_DESC_BOB
                + EMAIL_DESC_AMY + ADDRESS_DESC_AMY
                + GITHUB_DESC_AMY + LINKEDIN_DESC_AMY + TELEGRAM_DESC_AMY
                + GROUP_DESC_AMY + TAG_DESC_FRIEND + TAG_DESC_HUSBAND;

        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder()
                .withName(VALID_NAME_AMY)
                .withPhone(VALID_PHONE_BOB)
                .withEmail(VALID_EMAIL_AMY)
                .withAddress(VALID_ADDRESS_AMY)
                .withGithub(VALID_GITHUB_AMY)
                .withLinkedin(VALID_LINKEDIN_AMY)
                .withTelegram(VALID_TELEGRAM_AMY)
                .withGroups(VALID_GROUP_AMY)
                .withTags(VALID_TAG_FRIEND, VALID_TAG_HUSBAND)
                .build();

        assertParseSuccess(parser, userInput, createExpectedCommand(descriptor));
    }

    // EP: a nonempty proper subset of update fields supplied; omitted fields remain absent in the descriptor.
    @Test
    public void parse_someFieldsSpecified_success() {
        String userInput = SEARCH_AND_SEPARATOR + PHONE_DESC_BOB + EMAIL_DESC_AMY;

        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder()
                .withPhone(VALID_PHONE_BOB)
                .withEmail(VALID_EMAIL_AMY)
                .build();

        assertParseSuccess(parser, userInput, createExpectedCommand(descriptor));
    }

    // EP: each supported update field supplied alone; parsing must not depend on another update.
    // Boundary value: 1 update field, the minimum required after the separator.
    @Test
    public void parse_singleUpdateField_success() {
        assertParseSuccess(parser, SEARCH_AND_SEPARATOR + NAME_DESC_AMY,
                createExpectedCommand(new EditPersonDescriptorBuilder().withName(VALID_NAME_AMY).build()));
        assertParseSuccess(parser, SEARCH_AND_SEPARATOR + PHONE_DESC_BOB,
                createExpectedCommand(new EditPersonDescriptorBuilder().withPhone(VALID_PHONE_BOB).build()));
        assertParseSuccess(parser, SEARCH_AND_SEPARATOR + EMAIL_DESC_AMY,
                createExpectedCommand(new EditPersonDescriptorBuilder().withEmail(VALID_EMAIL_AMY).build()));
        assertParseSuccess(parser, SEARCH_AND_SEPARATOR + ADDRESS_DESC_AMY,
                createExpectedCommand(new EditPersonDescriptorBuilder().withAddress(VALID_ADDRESS_AMY).build()));
        assertParseSuccess(parser, SEARCH_AND_SEPARATOR + GITHUB_DESC_AMY,
                createExpectedCommand(new EditPersonDescriptorBuilder().withGithub(VALID_GITHUB_AMY).build()));
        assertParseSuccess(parser, SEARCH_AND_SEPARATOR + LINKEDIN_DESC_AMY,
                createExpectedCommand(new EditPersonDescriptorBuilder().withLinkedin(VALID_LINKEDIN_AMY).build()));
        assertParseSuccess(parser, SEARCH_AND_SEPARATOR + TELEGRAM_DESC_AMY,
                createExpectedCommand(new EditPersonDescriptorBuilder().withTelegram(VALID_TELEGRAM_AMY).build()));
        assertParseSuccess(parser, SEARCH_AND_SEPARATOR + GROUP_DESC_AMY,
                createExpectedCommand(new EditPersonDescriptorBuilder().withGroups(VALID_GROUP_AMY).build()));
        assertParseSuccess(parser, SEARCH_AND_SEPARATOR + TAG_DESC_FRIEND,
                createExpectedCommand(new EditPersonDescriptorBuilder().withTags(VALID_TAG_FRIEND).build()));
    }

    // EP: valid separator with no surrounding spaces; whitespace is not required for splitting sections.
    // Boundary value: 0 spaces on either side of the separator.
    @Test
    public void parse_separatorWithoutSurroundingSpaces_success() {
        String userInput = PHONE_DESC_AMY.trim() + EDIT_SEPARATOR + NAME_DESC_AMY.trim();
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withName(VALID_NAME_AMY).build();

        assertParseSuccess(parser, userInput, createExpectedCommand(descriptor));
    }

    // EP: multiple search fields and an update to a searched field; search and replacement values stay separate.
    @Test
    public void parse_multipleSearchFields_success() {
        String userInput = NAME_DESC_AMY + PHONE_DESC_AMY
                + SEPARATOR + PHONE_DESC_BOB;

        ExactPersonMatchDescriptor searchDescriptor = new ExactPersonMatchDescriptor();
        searchDescriptor.setName(new Name(VALID_NAME_AMY));
        searchDescriptor.setPhone(new Phone(VALID_PHONE_AMY));

        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder()
                .withPhone(VALID_PHONE_BOB)
                .build();

        EditCommand expectedCommand = new EditCommand(
                new PersonMatchesExactFieldsPredicate(searchDescriptor), descriptor);

        assertParseSuccess(parser, userInput, expectedCommand);
    }

    // EP: explicit empty update for a required field (name or phone); clearing must be rejected.
    // Boundary value: required update value length 0.
    @Test
    public void parse_clearRequiredField_failure() {
        assertParseFailure(parser,
                SEARCH_AND_SEPARATOR + " " + PREFIX_NAME,
                String.format(EditCommand.MESSAGE_CANNOT_CLEAR_REQUIRED_FIELD,
                        PREFIX_NAME));

        assertParseFailure(parser,
                SEARCH_AND_SEPARATOR + " " + PREFIX_PHONE,
                String.format(EditCommand.MESSAGE_CANNOT_CLEAR_REQUIRED_FIELD,
                        PREFIX_PHONE));
    }

    // EP: explicit empty optional scalar updates; empty values request clearing rather than omission.
    // Boundary value: optional update value length 0.
    @Test
    public void parse_clearOptionalFields_success() {
        String userInput = SEARCH_AND_SEPARATOR
                + " " + PREFIX_EMAIL
                + " " + PREFIX_ADDRESS
                + " " + PREFIX_GITHUB
                + " " + PREFIX_LINKEDIN
                + " " + PREFIX_TELEGRAM;

        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder()
                .withEmail("")
                .withAddress("")
                .withGithub("")
                .withLinkedin("")
                .withTelegram("")
                .build();

        assertParseSuccess(parser, userInput, createExpectedCommand(descriptor));
    }

    // EP: one empty occurrence of each collection flag requests clearing rather than toggling.
    // Boundary value: 0 members supplied in each collection update.
    @Test
    public void parse_clearTagsAndGroups_success() {
        String userInput = SEARCH_AND_SEPARATOR + GROUP_EMPTY + TAG_EMPTY;

        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder()
                .withGroups()
                .withTags()
                .build();

        assertParseSuccess(parser, userInput, createExpectedCommand(descriptor));
    }

    // EP: an empty and a nonempty occurrence of the same collection flag, tested in both orders.
    @Test
    public void parse_mixedClearAndToggle_failure() {
        assertParseFailure(parser,
                SEARCH_AND_SEPARATOR + TAG_DESC_FRIEND + TAG_EMPTY,
                String.format(EditCommand.MESSAGE_MIXED_CLEAR_AND_TOGGLE, PREFIX_TAG));

        assertParseFailure(parser,
                SEARCH_AND_SEPARATOR + GROUP_DESC_AMY + GROUP_EMPTY,
                String.format(EditCommand.MESSAGE_MIXED_CLEAR_AND_TOGGLE, PREFIX_GROUP));

        // Empty values are invalid in either order when another occurrence is present.
        assertParseFailure(parser,
                SEARCH_AND_SEPARATOR + TAG_EMPTY + TAG_DESC_FRIEND,
                String.format(EditCommand.MESSAGE_MIXED_CLEAR_AND_TOGGLE, PREFIX_TAG));
        assertParseFailure(parser,
                SEARCH_AND_SEPARATOR + GROUP_EMPTY + GROUP_DESC_AMY,
                String.format(EditCommand.MESSAGE_MIXED_CLEAR_AND_TOGGLE, PREFIX_GROUP));
    }

    // EP: the same empty collection flag repeated; clearing is only valid when that flag occurs alone.
    // Boundary value: 2 empty occurrences, the first count exceeding the permitted single clear flag.
    @Test
    public void parse_repeatedEmptySetFields_failure() {
        assertParseFailure(parser,
                SEARCH_AND_SEPARATOR + TAG_EMPTY + TAG_EMPTY,
                String.format(EditCommand.MESSAGE_MIXED_CLEAR_AND_TOGGLE, PREFIX_TAG));
        assertParseFailure(parser,
                SEARCH_AND_SEPARATOR + GROUP_EMPTY + GROUP_EMPTY,
                String.format(EditCommand.MESSAGE_MIXED_CLEAR_AND_TOGGLE, PREFIX_GROUP));
    }

    // EP: identical nonempty collection flags repeated; duplicate values collapse to one requested member.
    @Test
    public void parse_repeatedNonemptySetFields_success() {
        String userInput = SEARCH_AND_SEPARATOR + TAG_DESC_FRIEND + TAG_DESC_FRIEND
                + GROUP_DESC_AMY + GROUP_DESC_AMY;
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder()
                .withTags(VALID_TAG_FRIEND).withGroups(VALID_GROUP_AMY).build();

        assertParseSuccess(parser, userInput, createExpectedCommand(descriptor));
    }

    // EP: identical scalar flag repeated in search or updates; equal values do not make repetition valid.
    // Boundary value: 2 occurrences, the first count exceeding the permitted single scalar flag per section.
    @Test
    public void parse_repeatedScalarFields_failure() {
        Map<Prefix, String> scalarFields = Map.of(
                PREFIX_NAME, NAME_DESC_AMY,
                PREFIX_PHONE, PHONE_DESC_AMY,
                PREFIX_EMAIL, EMAIL_DESC_AMY,
                PREFIX_ADDRESS, ADDRESS_DESC_AMY,
                PREFIX_GITHUB, GITHUB_DESC_AMY,
                PREFIX_LINKEDIN, LINKEDIN_DESC_AMY,
                PREFIX_TELEGRAM, TELEGRAM_DESC_AMY);

        scalarFields.forEach((prefix, field) -> {
            assertParseFailure(parser,
                    field + field + SEPARATOR + NAME_DESC_AMY,
                    Messages.getErrorMessageForDuplicatePrefixes(prefix));
            assertParseFailure(parser,
                    SEARCH_AND_SEPARATOR + field + field,
                    Messages.getErrorMessageForDuplicatePrefixes(prefix));
        });
    }

    // EP: scalar flag repeated with conflicting values in search or updates; neither value silently wins.
    // Boundary value: 2 occurrences, the first count exceeding the permitted single scalar flag per section.
    @Test
    public void parse_repeatedScalarFieldWithDifferentValues_failure() {
        assertParseFailure(parser,
                PHONE_DESC_AMY + PHONE_DESC_BOB + SEPARATOR + NAME_DESC_AMY,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_PHONE));
        assertParseFailure(parser,
                SEARCH_AND_SEPARATOR + PHONE_DESC_AMY + PHONE_DESC_BOB,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_PHONE));
    }

    /**
     * Returns an expected command identifying AMY by her original phone.
     */
    private static EditCommand createExpectedCommand(EditPersonDescriptor descriptor) {
        ExactPersonMatchDescriptor searchDescriptor = new ExactPersonMatchDescriptor();
        searchDescriptor.setPhone(new Phone(VALID_PHONE_AMY));

        return new EditCommand(
                new PersonMatchesExactFieldsPredicate(searchDescriptor), descriptor);
    }
}
