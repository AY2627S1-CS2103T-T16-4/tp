package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
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

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import seedu.address.logic.commands.EditCommand;
import seedu.address.logic.commands.EditCommand.EditPersonDescriptor;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.Group;
import seedu.address.model.person.PersonMatchesExactFieldsPredicate;
import seedu.address.model.tag.Tag;

/**
 * Parses exact search criteria and contact updates separated by {@code >>}.
 */
public class EditCommandParser implements Parser<EditCommand> {

    private static final List<Prefix> EDITABLE_PREFIXES = List.of(
            PREFIX_NAME, PREFIX_PHONE, PREFIX_EMAIL, PREFIX_ADDRESS,
            PREFIX_GITHUB, PREFIX_LINKEDIN, PREFIX_TELEGRAM,
            PREFIX_GROUP, PREFIX_TAG);

    private static final List<Prefix> UNCLEARABLE_PREFIXES = List.of(PREFIX_NAME, PREFIX_PHONE);

    private static final Pattern FLAG_LIKE_TOKEN = Pattern.compile("(?<=\\s)[A-Za-z]+/");

    private final ExactPersonMatchParser targetParser =
            new ExactPersonMatchParser(EditCommand.MESSAGE_USAGE, EditCommand.MESSAGE_EMPTY_FLAG_VALUE);

    @Override
    public EditCommand parse(String args) throws ParseException {
        requireNonNull(args);

        String[] sections = args.split(EDIT_SEPARATOR, -1);

        if (sections.length != 2 || sections[0].isBlank()) {
            throw invalidFormat();
        }

        // Prefix recognition requires a preceding space.
        String searchArgs = " " + sections[0].trim();
        String updateArgs = " " + sections[1].trim();

        PersonMatchesExactFieldsPredicate predicate = targetParser.parse(searchArgs);

        ArgumentMultimap updates = ArgumentTokenizer.tokenize(
                updateArgs, EDITABLE_PREFIXES.toArray(new Prefix[0]));

        validateUpdates(updateArgs, updates);

        EditPersonDescriptor descriptor = parseUpdates(updates);

        if (!descriptor.isAnyFieldEdited()) {
            throw new ParseException(EditCommand.MESSAGE_NOT_EDITED);
        }

        return new EditCommand(predicate, descriptor);
    }

    /**
     * Validates update syntax while allowing empty optional fields.
     *
     * @param args Update arguments to check for unsupported flags.
     * @param updates Tokenized updates to check for preamble text, duplicate flags, and required field values.
     * @throws ParseException If the updates contain invalid syntax, duplicate scalar flags, or empty required fields.
     */
    private static void validateUpdates(
            String args, ArgumentMultimap updates) throws ParseException {
        if (!updates.getPreamble().isEmpty() || containsUnknownFlag(args)) {
            throw invalidFormat();
        }

        updates.verifyNoDuplicatePrefixesFor(
                PREFIX_NAME, PREFIX_PHONE, PREFIX_EMAIL, PREFIX_ADDRESS,
                PREFIX_GITHUB, PREFIX_LINKEDIN, PREFIX_TELEGRAM);

        for (int i = 0; i < UNCLEARABLE_PREFIXES.size(); i++) {
            Prefix prefix = UNCLEARABLE_PREFIXES.get(i);
            if (updates.getValue(prefix).filter(String::isEmpty).isPresent()) {
                throw new ParseException(String.format(
                        EditCommand.MESSAGE_CANNOT_CLEAR_REQUIRED_FIELD, prefix));
            }
        }
    }

    /**
     * Converts supplied updates into a descriptor.
     *
     * @param updates Tokenized field values to parse into replacements and membership updates.
     * @throws ParseException If a field value is invalid or a set update mixes clearing and toggling.
     */
    private static EditPersonDescriptor parseUpdates(
            ArgumentMultimap updates) throws ParseException {
        EditPersonDescriptor descriptor = new EditPersonDescriptor();

        if (updates.getValue(PREFIX_NAME).isPresent()) {
            descriptor.setName(ParserUtil.parseName(
                    updates.getValue(PREFIX_NAME).get()));
        }

        if (updates.getValue(PREFIX_PHONE).isPresent()) {
            descriptor.setPhone(ParserUtil.parsePhone(
                    updates.getValue(PREFIX_PHONE).get()));
        }

        if (updates.getValue(PREFIX_EMAIL).isPresent()) {
            descriptor.setEmail(ParserUtil.parseEmail(
                    updates.getValue(PREFIX_EMAIL).get()));
        }

        if (updates.getValue(PREFIX_ADDRESS).isPresent()) {
            descriptor.setAddress(ParserUtil.parseAddress(
                    updates.getValue(PREFIX_ADDRESS).get()));
        }

        if (updates.getValue(PREFIX_GITHUB).isPresent()) {
            descriptor.setGithub(ParserUtil.parseGithub(
                    updates.getValue(PREFIX_GITHUB).get()));
        }

        if (updates.getValue(PREFIX_LINKEDIN).isPresent()) {
            descriptor.setLinkedin(ParserUtil.parseLinkedin(
                    updates.getValue(PREFIX_LINKEDIN).get()));
        }

        if (updates.getValue(PREFIX_TELEGRAM).isPresent()) {
            descriptor.setTelegram(ParserUtil.parseTelegram(
                    updates.getValue(PREFIX_TELEGRAM).get()));
        }

        parseGroupsForEdit(updates.getAllValues(PREFIX_GROUP)).ifPresent(descriptor::setGroups);

        parseTagsForEdit(updates.getAllValues(PREFIX_TAG)).ifPresent(descriptor::setTags);

        return descriptor;
    }

    /**
     * Returns the requested tag membership update.
     * Returns an empty optional if no tag flag is supplied, an optional containing an empty set for {@code t/},
     * or an optional containing the tags whose membership should be toggled.
     *
     * @param values Values supplied with tag flags.
     * @throws ParseException If a tag value is invalid or an empty value appears with another tag flag.
     */
    private static Optional<Set<Tag>> parseTagsForEdit(
            Collection<String> values) throws ParseException {
        if (values.isEmpty()) {
            return Optional.empty();
        }

        validateSetValues(values, PREFIX_TAG);

        if (values.contains("")) {
            return Optional.of(Collections.emptySet());
        }

        return Optional.of(ParserUtil.parseTags(values));
    }

    /**
     * Returns the requested group membership update.
     * Returns an empty optional if no group flag is supplied, an optional containing an empty set for {@code g/},
     * or an optional containing the groups whose membership should be toggled.
     *
     * @param values Values supplied with group flags.
     * @throws ParseException If a group value is invalid or an empty value appears with another group flag.
     */
    private static Optional<Set<Group>> parseGroupsForEdit(
            Collection<String> values) throws ParseException {
        if (values.isEmpty()) {
            return Optional.empty();
        }

        validateSetValues(values, PREFIX_GROUP);

        if (values.contains("")) {
            return Optional.of(Collections.emptySet());
        }

        return Optional.of(ParserUtil.parseGroups(values));
    }

    /**
     * Rejects repeated set flags when any occurrence has an empty value.
     *
     * @param values Values supplied for the set field.
     * @param prefix Prefix identifying the set field in the error message.
     * @throws ParseException If an empty value appears with another occurrence of the flag.
     */
    private static void validateSetValues(
            Collection<String> values, Prefix prefix) throws ParseException {
        if (values.size() > 1 && values.contains("")) {
            throw new ParseException(
                    String.format(EditCommand.MESSAGE_MIXED_CLEAR_AND_TOGGLE, prefix));
        }
    }

    /**
     * Returns true if the arguments contain an unsupported flag-like token absorbed into a field value.
     * Uses the same detection rule as {@link ExactPersonMatchParser}.
     */
    private static boolean containsUnknownFlag(String args) {
        Matcher matcher = FLAG_LIKE_TOKEN.matcher(args);

        while (matcher.find()) {
            String token = matcher.group();
            if (EDITABLE_PREFIXES.stream()
                    .noneMatch(prefix -> prefix.getPrefix().equals(token))) {
                return true;
            }
        }

        return false;
    }

    /**
     * Creates a parse exception containing the invalid format message and edit command usage.
     */
    private static ParseException invalidFormat() {
        return new ParseException(
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditCommand.MESSAGE_USAGE));
    }
}
