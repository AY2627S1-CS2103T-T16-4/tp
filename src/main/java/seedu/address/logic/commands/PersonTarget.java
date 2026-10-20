package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.person.ExactPersonMatchDescriptor;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.PersonMatchesExactFieldsPredicate;
import seedu.address.model.person.Phone;

/**
 * Identifies one contact by full name and optional complete phone number, independently of the current filter.
 */
public record PersonTarget(Name name, Optional<Phone> phone) {

    public static final String MESSAGE_PERSON_NOT_FOUND =
            "No contact matches the supplied name and phone number.";
    public static final String MESSAGE_AMBIGUOUS_PERSON =
            "Multiple contacts match:\n%1$s\n"
                    + "Please specify a phone number using p/PHONE_NUMBER.";

    public PersonTarget {
        requireAllNonNull(name, phone);
    }

    /**
     * Resolves the target against all contacts.
     *
     * @throws CommandException if no contact matches or the target is ambiguous.
     */
    public Person resolve(ReadOnlyAddressBook addressBook) throws CommandException {
        requireNonNull(addressBook);
        List<Person> matches = findMatches(addressBook);

        if (matches.isEmpty()) {
            throw new CommandException(MESSAGE_PERSON_NOT_FOUND);
        }
        if (matches.size() > 1) {
            throw new CommandException(String.format(MESSAGE_AMBIGUOUS_PERSON, formatMatches(matches)));
        }

        return matches.getFirst();
    }

    private List<Person> findMatches(ReadOnlyAddressBook addressBook) {
        ExactPersonMatchDescriptor descriptor = new ExactPersonMatchDescriptor();
        descriptor.setName(name);
        phone.ifPresent(descriptor::setPhone);

        return addressBook.getPersonList().stream()
                .filter(new PersonMatchesExactFieldsPredicate(descriptor))
                .toList();
    }

    private static String formatMatches(List<Person> matches) {
        return matches.stream()
                .map(person -> String.format("%s, Phone: %s", person.getName(), person.getPhone()))
                .collect(Collectors.joining("\n"));
    }
}
