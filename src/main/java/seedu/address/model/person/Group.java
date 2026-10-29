package seedu.address.model.person;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

/** Represents a contact's group label. */
public class Group {
    public static final String MESSAGE_CONSTRAINTS = "Group names should be alphanumeric without spaces";
    public static final String VALIDATION_REGEX = "\\p{Alnum}+";
    public final String value;

    /**
     * Constructs an {@code Group}.
     *
     * @param group A valid group.
     */
    public Group(String group) {
        requireNonNull(group);
        checkArgument(isValidGroup(group), MESSAGE_CONSTRAINTS);
        value = group.toLowerCase();
    }

    /** Returns true if a given string is a valid group name. */
    public static boolean isValidGroup(String test) {
        return test.matches(VALIDATION_REGEX);
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
        if (!(other instanceof Group otherGroup)) {
            return false;
        }
        return value.equals(otherGroup.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
