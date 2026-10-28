package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_GITHUB;
import static seedu.address.logic.parser.CliSyntax.PREFIX_GROUP;
import static seedu.address.logic.parser.CliSyntax.PREFIX_LINKEDIN;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TELEGRAM;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.Address;
import seedu.address.model.person.Email;
import seedu.address.model.person.Github;
import seedu.address.model.person.Group;
import seedu.address.model.person.Linkedin;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.model.person.Telegram;
import seedu.address.model.tag.Tag;

/**
 * Parses input arguments and creates a new AddCommand object
 */
public class AddCommandParser implements Parser<AddCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the AddCommand
     * and returns an AddCommand object for execution.
     * @throws ParseException if the user input does not conform to the expected format
     */
    public AddCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap =
                ArgumentTokenizer.tokenize(args, PREFIX_NAME, PREFIX_PHONE, PREFIX_EMAIL, PREFIX_ADDRESS,
                        PREFIX_GROUP, PREFIX_GITHUB, PREFIX_LINKEDIN, PREFIX_TELEGRAM, PREFIX_TAG);

        List<String> unknownPrefixes = findUnknownPrefixes(args);

        // Reject prefixes that are not supported by the add command.
        if (!unknownPrefixes.isEmpty()) {
            throw new ParseException(
                    String.format(AddCommand.MESSAGE_UNKNOWN_PREFIXES,
                            String.join(", ", unknownPrefixes)));
        }

        // Reject empty input and text that does not belong to a recognised prefix.
        if (args.isBlank() || !argMultimap.getPreamble().isEmpty()) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommand.MESSAGE_USAGE));
        }

        List<Prefix> allPrefixes = List.of(
                PREFIX_NAME,
                PREFIX_PHONE,
                PREFIX_EMAIL,
                PREFIX_ADDRESS,
                PREFIX_GROUP,
                PREFIX_GITHUB,
                PREFIX_LINKEDIN,
                PREFIX_TELEGRAM,
                PREFIX_TAG);

        List<String> emptyPrefixes = new ArrayList<>();

        for (Prefix prefix : allPrefixes) {
            if (argMultimap.getAllValues(prefix).stream().anyMatch(String::isBlank)) {
                emptyPrefixes.add(prefix.getPrefix());
            }
        }

        // Reject prefixes that were provided without a value.
        if (!emptyPrefixes.isEmpty()) {
            throw new ParseException(
                    String.format(AddCommand.MESSAGE_EMPTY_FLAG_VALUE,
                            String.join(", ", emptyPrefixes)));
        }

        // A contact must have a name.
        if (!argMultimap.getValue(PREFIX_NAME).isPresent()) {
            throw new ParseException(AddCommand.MESSAGE_MISSING_NAME);
        }

        // A contact must have a phone number.
        if (!argMultimap.getValue(PREFIX_PHONE).isPresent()) {
            throw new ParseException(Phone.MESSAGE_CONSTRAINTS);
        }

        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_NAME, PREFIX_PHONE, PREFIX_EMAIL,
                PREFIX_ADDRESS, PREFIX_GITHUB, PREFIX_LINKEDIN, PREFIX_TELEGRAM);
        Name name = ParserUtil.parseName(argMultimap.getValue(PREFIX_NAME).get().toUpperCase());
        Phone phone = ParserUtil.parsePhone(argMultimap.getValue(PREFIX_PHONE).get());
        Email email = ParserUtil.parseEmail(argMultimap.getValue(PREFIX_EMAIL).orElse(""));
        Address address = ParserUtil.parseAddress(argMultimap.getValue(PREFIX_ADDRESS).orElse(""));
        Set<Tag> tagList = ParserUtil.parseTags(argMultimap.getAllValues(PREFIX_TAG));
        Set<Group> groupList = ParserUtil.parseGroups(argMultimap.getAllValues(PREFIX_GROUP));
        Github github = ParserUtil.parseGithub(argMultimap.getValue(PREFIX_GITHUB).orElse(""));
        Linkedin linkedin = ParserUtil.parseLinkedin(argMultimap.getValue(PREFIX_LINKEDIN).orElse(""));
        Telegram telegram = ParserUtil.parseTelegram(argMultimap.getValue(PREFIX_TELEGRAM).orElse(""));

        Person person = new Person(name, phone, email, address, groupList, github, linkedin, telegram, tagList);

        return new AddCommand(person);
    }

    /**
     * Finds distinct flag-like tokens that are not supported by the add command.
     *
     * @param args The arguments supplied to the add command.
     * @return A list of unsupported prefixes, in the order they appear.
     */
    private static List<String> findUnknownPrefixes(String args) {
        Pattern flagPattern = Pattern.compile("(?<=\\s)[A-Za-z]+/");
        Matcher matcher = flagPattern.matcher(args);

        List<Prefix> supportedPrefixes = List.of(
                PREFIX_NAME,
                PREFIX_PHONE,
                PREFIX_EMAIL,
                PREFIX_ADDRESS,
                PREFIX_GROUP,
                PREFIX_GITHUB,
                PREFIX_LINKEDIN,
                PREFIX_TELEGRAM,
                PREFIX_TAG);

        List<String> unknownPrefixes = new ArrayList<>();

        while (matcher.find()) {
            String token = matcher.group();

            boolean isSupported = supportedPrefixes.stream()
                    .anyMatch(prefix -> prefix.getPrefix().equals(token));

            if (!isSupported && !unknownPrefixes.contains(token)) {
                unknownPrefixes.add(token);
            }
        }

        return unknownPrefixes;
    }

}
