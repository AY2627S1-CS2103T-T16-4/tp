package seedu.address.model.person;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

/** Represents a contact's GitHub username. */
public class Github {
    public static final String MESSAGE_CONSTRAINTS = "GitHub usernames should not contain whitespace or apostrophes";
    public static final String VALIDATION_REGEX = "[^\\s']*";
    public final String value;

    /**
     * Constructs an {@code Github}.
     *
     * @param github A valid Github username.
     */
    public Github(String github) {
        requireNonNull(github);
        checkArgument(isValidGithub(github), MESSAGE_CONSTRAINTS);
        value = github;
    }

    /** Returns true if the GitHub username contains no whitespace or apostrophes. */
    public static boolean isValidGithub(String github) {
        return github.matches(VALIDATION_REGEX);
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
        if (!(other instanceof Github otherGithub)) {
            return false;
        }
        return value.equals(otherGithub.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
