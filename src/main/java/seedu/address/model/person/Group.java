package seedu.address.model.person;

import static java.util.Objects.requireNonNull;

/** Represents a contact's group label. */
public class Group {
    public final String value;

    public Group(String group) {
        requireNonNull(group);
        value = group.toLowerCase();
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
