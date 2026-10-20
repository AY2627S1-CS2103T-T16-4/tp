package seedu.address.model.person;

import static java.util.Objects.requireNonNull;

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
        return descriptor.getName().map(search -> startsWithIgnoreCase(person.getName().fullName, search))
                        .orElse(true)
                && descriptor.getPhone().map(search -> startsWithIgnoreCase(person.getPhone().value, search))
                        .orElse(true)
                && descriptor.getEmail().map(search -> startsWithIgnoreCase(person.getEmail().value, search))
                        .orElse(true)
                && descriptor.getAddress().map(search -> startsWithIgnoreCase(person.getAddress().value, search))
                        .orElse(true)
                && descriptor.getGithub().map(search -> person.getGithub().value.startsWith(search))
                        .orElse(true)
                && descriptor.getLinkedin().map(search -> startsWithIgnoreCase(person.getLinkedin().value, search))
                        .orElse(true)
                && descriptor.getTelegram().map(search -> startsWithIgnoreCase(person.getTelegram().value, search))
                        .orElse(true)
                && descriptor.getGroups().stream().allMatch(search -> person.getGroups().stream()
                        .anyMatch(group -> startsWithIgnoreCase(group.value, search)))
                && descriptor.getTags().stream().allMatch(search -> person.getTags().stream()
                        .anyMatch(tag -> startsWithIgnoreCase(tag.tagName, search)));
    }

    private static boolean startsWithIgnoreCase(String value, String search) {
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
