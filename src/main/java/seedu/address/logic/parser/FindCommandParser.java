package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
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

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import seedu.address.logic.commands.FindCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.FindPersonDescriptor;
import seedu.address.model.person.Github;
import seedu.address.model.person.Group;
import seedu.address.model.person.Linkedin;
import seedu.address.model.person.PersonMatchesFlagsPredicate;

/**
 * Parses input arguments and creates a new FindCommand object
 */
public class FindCommandParser implements Parser<FindCommand> {

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
        ArgumentMultimap argMultimap =
                ArgumentTokenizer.tokenize(args, SEARCHABLE_PREFIXES.toArray(new Prefix[0]));

        List<Prefix> usedPrefixes = SEARCHABLE_PREFIXES.stream()
                .filter(prefix -> !argMultimap.getAllValues(prefix).isEmpty())
                .toList();

        if (!argMultimap.getPreamble().isEmpty() || usedPrefixes.isEmpty() || containsUnknownFlag(args)) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
        }

        // Must run before validation, as some value types (e.g. Email) accept an empty string.
        for (Prefix prefix : usedPrefixes) {
            if (argMultimap.getAllValues(prefix).stream().anyMatch(String::isEmpty)) {
                throw new ParseException(String.format(MESSAGE_EMPTY_FLAG_VALUE, prefix));
            }
        }

        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_NAME, PREFIX_PHONE, PREFIX_ADDRESS, PREFIX_EMAIL,
                PREFIX_GITHUB, PREFIX_LINKEDIN, PREFIX_TELEGRAM, PREFIX_GROUP);

        FindPersonDescriptor descriptor = new FindPersonDescriptor();
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
            descriptor.setGithub(new Github(argMultimap.getValue(PREFIX_GITHUB).get()));
        }
        if (argMultimap.getValue(PREFIX_LINKEDIN).isPresent()) {
            descriptor.setLinkedin(new Linkedin(argMultimap.getValue(PREFIX_LINKEDIN).get()));
        }
        if (argMultimap.getValue(PREFIX_TELEGRAM).isPresent()) {
            descriptor.setTelegram(ParserUtil.parseTelegram(argMultimap.getValue(PREFIX_TELEGRAM).get()));
        }
        if (argMultimap.getValue(PREFIX_GROUP).isPresent()) {
            descriptor.setGroup(new Group(argMultimap.getValue(PREFIX_GROUP).get()));
        }
        descriptor.setTags(ParserUtil.parseTags(argMultimap.getAllValues(PREFIX_TAG)));

        return new FindCommand(new PersonMatchesFlagsPredicate(descriptor), usedPrefixes);
    }

    /**
     * Returns true if {@code args} contains a flag-like token (e.g. {@code x/}) that is not one of the flags
     * accepted by {@code find}. The tokenizer does not split on such tokens, so they would otherwise be
     * absorbed into the value of the preceding flag.
     */
    private static boolean containsUnknownFlag(String args) {
        Matcher matcher = FLAG_LIKE_TOKEN.matcher(args);
        while (matcher.find()) {
            String token = matcher.group();
            if (SEARCHABLE_PREFIXES.stream().noneMatch(prefix -> prefix.getPrefix().equals(token))) {
                return true;
            }
        }
        return false;
    }

}
