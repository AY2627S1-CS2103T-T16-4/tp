package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.Messages.MESSAGE_QUOTED_VALUE_HINT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_GITHUB;
import static seedu.address.logic.parser.CliSyntax.PREFIX_GROUP;
import static seedu.address.logic.parser.CliSyntax.PREFIX_LINKEDIN;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TELEGRAM;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.ExactPersonMatchDescriptor;
import seedu.address.model.person.PersonMatchesExactFieldsPredicate;

/**
 * Parses exact field criteria shared by commands that require one matching contact.
 */
public class ExactPersonMatchParser {

    private static final List<Prefix> SEARCHABLE_PREFIXES = List.of(
            PREFIX_NAME, PREFIX_PHONE, PREFIX_ADDRESS, PREFIX_EMAIL,
            PREFIX_GITHUB, PREFIX_LINKEDIN, PREFIX_TELEGRAM,
            PREFIX_GROUP, PREFIX_TAG);

    private static final Pattern FLAG_LIKE_TOKEN =
            Pattern.compile("(?<=\\s)[A-Za-z]+/");

    private final String messageUsage;
    private final String messageEmptyFlagValue;

    /**
     * Creates a parser with command-specific usage and empty-value messages.
     */
    public ExactPersonMatchParser(String messageUsage, String messageEmptyFlagValue) {
        requireAllNonNull(messageUsage, messageEmptyFlagValue);
        this.messageUsage = messageUsage;
        this.messageEmptyFlagValue = messageEmptyFlagValue;
    }

    /**
     * Parses nonempty, valid exact-match criteria.
     *
     * @throws ParseException if the arguments are missing, unsupported, or invalid.
     */
    public PersonMatchesExactFieldsPredicate parse(String args) throws ParseException {
        requireNonNull(args);
        if (containsUnknownFlag(args)) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, messageUsage)
                    + "\n" + MESSAGE_QUOTED_VALUE_HINT);
        }
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, SEARCHABLE_PREFIXES.toArray(new Prefix[0]));
        validateArguments(argMultimap);
        ExactPersonMatchDescriptor descriptor = parseDescriptor(argMultimap);
        assert descriptor.isAnyFieldSet() : "Accepted criteria must constrain at least one contact field";
        return new PersonMatchesExactFieldsPredicate(descriptor);
    }

    private void validateArguments(ArgumentMultimap argMultimap) throws ParseException {
        List<Prefix> usedPrefixes = SEARCHABLE_PREFIXES.stream()
                .filter(prefix -> !argMultimap.getAllValues(prefix).isEmpty())
                .toList();
        if (!argMultimap.getPreamble().isEmpty() || usedPrefixes.isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, messageUsage));
        }

        // Validate before parsing, since optional field types can accept an empty string.
        for (Prefix prefix : usedPrefixes) {
            if (argMultimap.getAllValues(prefix).stream().anyMatch(String::isEmpty)) {
                throw new ParseException(String.format(messageEmptyFlagValue, prefix));
            }
        }
        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_NAME, PREFIX_PHONE, PREFIX_ADDRESS, PREFIX_EMAIL,
                PREFIX_GITHUB, PREFIX_LINKEDIN, PREFIX_TELEGRAM);
    }

    private static ExactPersonMatchDescriptor parseDescriptor(ArgumentMultimap argMultimap) throws ParseException {
        ExactPersonMatchDescriptor descriptor = new ExactPersonMatchDescriptor();
        if (argMultimap.getValue(PREFIX_NAME).isPresent()) {
            descriptor.setName(ParserUtil.parseName(argMultimap.getValue(PREFIX_NAME).get()));
        }
        if (argMultimap.getValue(PREFIX_PHONE).isPresent()) {
            descriptor.setPhone(ParserUtil.parsePhone(argMultimap.getValue(PREFIX_PHONE).get()));
        }
        if (argMultimap.getValue(PREFIX_ADDRESS).isPresent()) {
            descriptor.setAddress(ParserUtil.parseAddress(argMultimap.getValue(PREFIX_ADDRESS).get()));
        }
        if (argMultimap.getValue(PREFIX_EMAIL).isPresent()) {
            descriptor.setEmail(ParserUtil.parseEmail(argMultimap.getValue(PREFIX_EMAIL).get()));
        }
        if (argMultimap.getValue(PREFIX_GITHUB).isPresent()) {
            descriptor.setGithub(ParserUtil.parseGithub(argMultimap.getValue(PREFIX_GITHUB).get()));
        }
        if (argMultimap.getValue(PREFIX_LINKEDIN).isPresent()) {
            descriptor.setLinkedin(ParserUtil.parseLinkedin(argMultimap.getValue(PREFIX_LINKEDIN).get()));
        }
        if (argMultimap.getValue(PREFIX_TELEGRAM).isPresent()) {
            descriptor.setTelegram(ParserUtil.parseTelegram(argMultimap.getValue(PREFIX_TELEGRAM).get()));
        }
        descriptor.setGroups(ParserUtil.parseGroups(argMultimap.getAllValues(PREFIX_GROUP)));
        descriptor.setTags(ParserUtil.parseTags(argMultimap.getAllValues(PREFIX_TAG)));
        return descriptor;
    }

    /**
     * Detects unsupported flag-like tokens that the tokenizer would otherwise
     * absorb into the preceding value.
     */
    private static boolean containsUnknownFlag(String args) throws ParseException {
        Matcher matcher = FLAG_LIKE_TOKEN.matcher(ArgumentTokenizer.maskQuotedValues(args));
        while (matcher.find()) {
            String token = matcher.group();
            if (SEARCHABLE_PREFIXES.stream()
                    .noneMatch(prefix -> prefix.getPrefix().equals(token))) {
                return true;
            }
        }
        return false;
    }
}
