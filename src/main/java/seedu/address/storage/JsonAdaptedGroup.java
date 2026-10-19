package seedu.address.storage;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import seedu.address.model.person.Group;

/**
 * Jackson-friendly version of {@link Group}.
 */
class JsonAdaptedGroup {

    private final String groupName;

    /**
     * Constructs a {@code JsonAdaptedGroup}.
     */
    @JsonCreator
    public JsonAdaptedGroup(String groupName) {
        this.groupName = groupName;
    }

    /**
     * Converts a {@code Group} into this Jackson-friendly format.
     */
    public JsonAdaptedGroup(Group source) {
        groupName = source.value;
    }

    @JsonValue
    public String getGroupName() {
        return groupName;
    }

    /**
     * Converts this object back into a {@code Group}.
     */
    public Group toModelType() {
        return new Group(groupName);
    }
}
