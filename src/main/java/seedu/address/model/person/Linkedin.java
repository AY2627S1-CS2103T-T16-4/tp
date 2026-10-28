package seedu.address.model.person;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

/** Represents a contact's LinkedIn profile. */
public class Linkedin {
    public static final String MESSAGE_CONSTRAINTS =
            "LinkedIn usernames should contain only letters, numbers, and hyphens";
    public static final String VALIDATION_REGEX = "[A-Za-z0-9-]*";
    public final String value;

    /**
     * Constructs an {@code Linkedin}.
     *
     * @param linkedin A valid Linkedin profile name.
     */
    public Linkedin(String linkedin) {
        requireNonNull(linkedin);
        checkArgument(isValidLinkedin(linkedin), MESSAGE_CONSTRAINTS);
        value = linkedin.toLowerCase();
    }

    /** Returns true if the LinkedIn username contains only letters, numbers, and hyphens. */
    public static boolean isValidLinkedin(String linkedin) {
        return linkedin.matches(VALIDATION_REGEX);
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
