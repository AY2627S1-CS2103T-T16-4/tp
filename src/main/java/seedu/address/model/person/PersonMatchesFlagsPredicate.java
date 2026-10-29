package seedu.address.model.person;

import static java.util.Objects.requireNonNull;

import java.util.Optional;
import java.util.function.Predicate;

import seedu.address.commons.util.ToStringBuilder;

/**
 * Tests that a {@code Person} matches every field set in the given {@code FindPersonDescriptor}.
 * Each searched text must be a prefix of the corresponding field, ignoring case except for GitHub usernames,
 * which are case-sensitive. For groups and tags, every searched text must be a prefix of at least one
 * of the person's groups or tags respectively.
 */
public class PersonMatchesFlagsPredicate implements Predicate<Person> {
    private final FindPersonDescriptor descriptor;

    /**
     * A defensive copy of {@code descriptor} is used internally.
     */
    public PersonMatchesFlagsPredicate(FindPersonDescriptor descriptor) {
        requireNonNull(descriptor);
        this.descriptor = new FindPersonDescriptor(descriptor);
    }

    @Override
    public boolean test(Person person) {
        assert person != null;
        return matchesIgnoringCase(descriptor.getName(), person.getName().fullName)
                && matchesIgnoringCase(descriptor.getPhone(), person.getPhone().value)
                && matchesIgnoringCase(descriptor.getEmail(), person.getEmail().value)
                && matchesIgnoringCase(descriptor.getAddress(), person.getAddress().value)
                && descriptor.getGithub().map(search -> person.getGithub().value.startsWith(search))
                        .orElse(true)
                && matchesIgnoringCase(descriptor.getLinkedin(), person.getLinkedin().value)
                && matchesIgnoringCase(descriptor.getTelegram(), person.getTelegram().value)
                && descriptor.getGroups().stream().allMatch(search -> person.getGroups().stream()
                        .anyMatch(group -> startsWithIgnoreCase(group.value, search)))
                && descriptor.getTags().stream().allMatch(search -> person.getTags().stream()
                        .anyMatch(tag -> startsWithIgnoreCase(tag.tagName, search)));
    }

    /**
     * Returns true if nothing is searched for, or if the searched text is a prefix of {@code value}, ignoring case.
     */
    private static boolean matchesIgnoringCase(Optional<String> search, String value) {
        return search.map(text -> startsWithIgnoreCase(value, text)).orElse(true);
    }

    private static boolean startsWithIgnoreCase(String value, String search) {
        assert value != null && search != null;
        return value.toLowerCase().startsWith(search.toLowerCase());
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof PersonMatchesFlagsPredicate otherPredicate)) {
            return false;
        }

        return descriptor.equals(otherPredicate.descriptor);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("descriptor", descriptor).toString();
    }
}
