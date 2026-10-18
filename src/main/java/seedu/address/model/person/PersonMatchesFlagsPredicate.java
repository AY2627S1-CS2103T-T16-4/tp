package seedu.address.model.person;

import static java.util.Objects.requireNonNull;

import java.util.function.Predicate;

import seedu.address.commons.util.ToStringBuilder;

/**
 * Tests that a {@code Person} matches every field set in the given {@code FindPersonDescriptor}.
 * Each field must be equal, and the person must have all of the searched tags.
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
        return descriptor.getName().map(person.getName()::equals).orElse(true)
                && descriptor.getPhone().map(person.getPhone()::equals).orElse(true)
                && descriptor.getEmail().map(person.getEmail()::equals).orElse(true)
                && descriptor.getAddress().map(person.getAddress()::equals).orElse(true)
                && descriptor.getGroup().map(person.getGroups()::contains).orElse(true)
                && descriptor.getGithub().map(person.getGithub()::equals).orElse(true)
                && descriptor.getLinkedin().map(person.getLinkedin()::equals).orElse(true)
                && descriptor.getTelegram().map(person.getTelegram()::equals).orElse(true)
                && person.getTags().containsAll(descriptor.getTags());
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
