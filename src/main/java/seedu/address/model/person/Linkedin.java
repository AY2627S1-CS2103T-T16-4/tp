package seedu.address.model.person;

import static java.util.Objects.requireNonNull;

/** Represents a contact's LinkedIn profile. */
public class Linkedin {
    public final String value;

    public Linkedin(String linkedin) {
        requireNonNull(linkedin);
        value = linkedin.toLowerCase();
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof Linkedin otherLinkedin)) {
            return false;
        }
        return value.equals(otherLinkedin.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
