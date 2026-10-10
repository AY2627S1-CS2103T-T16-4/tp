package seedu.address.model.person;

import static java.util.Objects.requireNonNull;

/** Represents a contact's GitHub username. */
public class Github {
    public final String value;

    /**
     * Constructs an {@code Github}.
     *
     * @param github A valid Github username.
     */
    public Github(String github) {
        requireNonNull(github);
        value = github;
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
