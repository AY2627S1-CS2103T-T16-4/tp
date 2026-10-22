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

    @Test
    public void parse_missingParts_failure() {
        // no search criteria or updates
        assertParseFailure(parser, "", MESSAGE_INVALID_FORMAT);

        // no separator
        assertParseFailure(parser, PHONE_DESC_AMY + NAME_DESC_AMY,
                MESSAGE_INVALID_FORMAT);

        // no search criteria
        assertParseFailure(parser, SEPARATOR + NAME_DESC_AMY,
                MESSAGE_INVALID_FORMAT);

        // no updates
        assertParseFailure(parser, SEARCH_AND_SEPARATOR,
                EditCommand.MESSAGE_NOT_EDITED);

        // multiple separators
        assertParseFailure(parser,
                SEARCH_AND_SEPARATOR + NAME_DESC_AMY + SEPARATOR + EMAIL_DESC_AMY,
                MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_invalidPreamble_failure() {
        // preamble before search criteria
        assertParseFailure(parser,
                PREAMBLE_NON_EMPTY + SEARCH_AND_SEPARATOR + NAME_DESC_AMY,
                MESSAGE_INVALID_FORMAT);

        // preamble before update fields
        assertParseFailure(parser,
                SEARCH_AND_SEPARATOR + " " + PREAMBLE_NON_EMPTY + NAME_DESC_AMY,
                MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_emptySearchValue_failure() {
        assertParseFailure(parser,
                " " + PREFIX_NAME + SEPARATOR + PHONE_DESC_BOB,
                String.format(EditCommand.MESSAGE_EMPTY_FLAG_VALUE, PREFIX_NAME));

        // Optional fields must also have a value when used as search criteria.
        assertParseFailure(parser,
                " " + PREFIX_EMAIL + SEPARATOR + NAME_DESC_AMY,
                String.format(EditCommand.MESSAGE_EMPTY_FLAG_VALUE, PREFIX_EMAIL));
    }

    @Test
    public void parse_invalidValue_failure() {
        // invalid search value
        assertParseFailure(parser,
                INVALID_PHONE_DESC + SEPARATOR + NAME_DESC_AMY,
                Phone.MESSAGE_CONSTRAINTS);

        // invalid update values
        assertParseFailure(parser,
                SEARCH_AND_SEPARATOR + INVALID_NAME_DESC,
                Name.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser,
                SEARCH_AND_SEPARATOR + INVALID_PHONE_DESC,
                Phone.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser,
                SEARCH_AND_SEPARATOR + INVALID_EMAIL_DESC,
                Email.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser,
                SEARCH_AND_SEPARATOR + INVALID_TELEGRAM_DESC,
                Telegram.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser,
                SEARCH_AND_SEPARATOR + INVALID_TAG_DESC,
                Tag.MESSAGE_CONSTRAINTS);
    }

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

        assertParseSuccess(parser, userInput, expectedCommand(descriptor));
    }

    @Test
    public void parse_someFieldsSpecified_success() {
        String userInput = SEARCH_AND_SEPARATOR + PHONE_DESC_BOB + EMAIL_DESC_AMY;

        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder()
                .withPhone(VALID_PHONE_BOB)
                .withEmail(VALID_EMAIL_AMY)
                .build();

        assertParseSuccess(parser, userInput, expectedCommand(descriptor));
    }

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

        assertParseSuccess(parser, userInput, expectedCommand(descriptor));
    }

    @Test
    public void parse_clearTagsAndGroups_success() {
        String userInput = SEARCH_AND_SEPARATOR + GROUP_EMPTY + TAG_EMPTY;

        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder()
                .withGroups()
                .withTags()
                .build();

        assertParseSuccess(parser, userInput, expectedCommand(descriptor));
    }

    @Test
    public void parse_mixedClearAndToggle_failure() {
        assertParseFailure(parser,
                SEARCH_AND_SEPARATOR + TAG_DESC_FRIEND + TAG_EMPTY,
                String.format(EditCommand.MESSAGE_MIXED_CLEAR_AND_TOGGLE, PREFIX_TAG));

        assertParseFailure(parser,
                SEARCH_AND_SEPARATOR + GROUP_DESC_AMY + GROUP_EMPTY,
                String.format(EditCommand.MESSAGE_MIXED_CLEAR_AND_TOGGLE, PREFIX_GROUP));
    }

    @Test
    public void parse_repeatedScalarFields_failure() {
        // repeated search prefix
        assertParseFailure(parser,
                PHONE_DESC_AMY + PHONE_DESC_BOB + SEPARATOR + NAME_DESC_AMY,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_PHONE));

        // repeated update prefix
        assertParseFailure(parser,
                SEARCH_AND_SEPARATOR + PHONE_DESC_AMY + PHONE_DESC_BOB,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_PHONE));
    }

    /**
     * Returns an expected command identifying AMY by her original phone.
     */
    private static EditCommand expectedCommand(EditPersonDescriptor descriptor) {
        ExactPersonMatchDescriptor searchDescriptor = new ExactPersonMatchDescriptor();
        searchDescriptor.setPhone(new Phone(VALID_PHONE_AMY));

        return new EditCommand(
                new PersonMatchesExactFieldsPredicate(searchDescriptor), descriptor);
    }
}
