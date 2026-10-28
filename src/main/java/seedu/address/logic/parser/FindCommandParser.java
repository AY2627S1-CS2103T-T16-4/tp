package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.Messages.MESSAGE_QUOTED_VALUE_HINT;
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

import java.util.HashSet;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import seedu.address.commons.core.LogsCenter;
import seedu.address.logic.commands.FindCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.FindPersonDescriptor;
import seedu.address.model.person.PersonMatchesFlagsPredicate;

/**
 * Parses input arguments and creates a new FindCommand object
 */
public class FindCommandParser implements Parser<FindCommand> {

    private static final Logger logger = LogsCenter.getLogger(FindCommandParser.class);

    /** Every flag that {@code find} accepts, in the order they are listed in error messages. */
    private static final List<Prefix> SEARCHABLE_PREFIXES = List.of(PREFIX_NAME, PREFIX_PHONE, PREFIX_ADDRESS,
            PREFIX_EMAIL, PREFIX_GITHUB, PREFIX_LINKEDIN, PREFIX_TELEGRAM, PREFIX_GROUP, PREFIX_TAG);

    /** Matches a word followed by '/' that starts after whitespace, i.e. something that looks like a flag. */
    private static final Pattern FLAG_LIKE_TOKEN = Pattern.compile("(?<=\\s)[A-Za-z]+/");

    /**
     * Parses the given {@code String} of arguments in the context of the FindCommand
     * and returns a FindCommand object for execution.
     * @throws ParseException if the user input does not conform to the expected format
     */
    public FindCommand parse(String args) throws ParseException {
        logger.fine("Parsing find arguments: " + args);
        if (containsUnknownFlag(args)) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE)
                    + "\n" + MESSAGE_QUOTED_VALUE_HINT);
        }
        ArgumentMultimap argMultimap =
                ArgumentTokenizer.tokenize(args, SEARCHABLE_PREFIXES.toArray(new Prefix[0]));

        List<Prefix> usedPrefixes = SEARCHABLE_PREFIXES.stream()
                .filter(prefix -> !argMultimap.getAllValues(prefix).isEmpty())
                .toList();

        if (!argMultimap.getPreamble().isEmpty() || usedPrefixes.isEmpty()) {
            logger.info("Rejected find input: missing, unknown or non-flag arguments");
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
        }

        // Must run before validation, as some value types (e.g. Email) accept an empty string.
        for (Prefix prefix : usedPrefixes) {
            if (argMultimap.getAllValues(prefix).stream().anyMatch(String::isEmpty)) {
                logger.info("Rejected find input: empty value for " + prefix);
                throw new ParseException(String.format(MESSAGE_EMPTY_FLAG_VALUE, prefix));
            }
        }

        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_NAME, PREFIX_PHONE, PREFIX_ADDRESS, PREFIX_EMAIL,
                PREFIX_GITHUB, PREFIX_LINKEDIN, PREFIX_TELEGRAM);

        // The values are kept as raw search text to allow partial matching (e.g. p/98 or e/tom), so they are
        // not validated as full field values.
        FindPersonDescriptor descriptor = new FindPersonDescriptor();
        argMultimap.getValue(PREFIX_NAME).ifPresent(descriptor::setName);
        argMultimap.getValue(PREFIX_PHONE).ifPresent(descriptor::setPhone);
        argMultimap.getValue(PREFIX_ADDRESS).ifPresent(descriptor::setAddress);
        argMultimap.getValue(PREFIX_EMAIL).ifPresent(descriptor::setEmail);
        argMultimap.getValue(PREFIX_GITHUB).ifPresent(descriptor::setGithub);
        argMultimap.getValue(PREFIX_LINKEDIN).ifPresent(descriptor::setLinkedin);
        argMultimap.getValue(PREFIX_TELEGRAM).ifPresent(descriptor::setTelegram);
        descriptor.setGroups(new HashSet<>(argMultimap.getAllValues(PREFIX_GROUP)));
        descriptor.setTags(new HashSet<>(argMultimap.getAllValues(PREFIX_TAG)));

        assert !usedPrefixes.isEmpty();
        logger.fine("Parsed find with flags: " + usedPrefixes);
        return new FindCommand(new PersonMatchesFlagsPredicate(descriptor), usedPrefixes);
    }

    /**
     * Returns true if {@code args} contains a flag-like token (e.g. {@code x/}) that is not one of the flags
     * accepted by {@code find}. The tokenizer does not split on such tokens, so they would otherwise be
     * absorbed into the value of the preceding flag.
     */
    private static boolean containsUnknownFlag(String args) throws ParseException {
        assert args != null;
        Matcher matcher = FLAG_LIKE_TOKEN.matcher(ArgumentTokenizer.maskQuotedValues(args));
        while (matcher.find()) {
            String token = matcher.group();
            if (SEARCHABLE_PREFIXES.stream().noneMatch(prefix -> prefix.getPrefix().equals(token))) {
                return true;
            }
        }
        return false;
    }

}
