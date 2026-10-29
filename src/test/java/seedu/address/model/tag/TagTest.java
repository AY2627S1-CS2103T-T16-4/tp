package seedu.address.model.tag;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class TagTest {

    @Test
    public void fieldValues_validAndInvalidInputs() {
        assertTrue(Tag.isValidTagName("friends"));
        assertTrue(Tag.isValidTagName("a"));
        assertTrue(Tag.isValidTagName("Project123"));
        assertFalse(Tag.isValidTagName(""));
        assertFalse(Tag.isValidTagName(" "));
        assertFalse(Tag.isValidTagName("friends team"));
        assertFalse(Tag.isValidTagName("friends-team"));
        assertFalse(Tag.isValidTagName("friends_team"));
        assertFalse(Tag.isValidTagName("friends;school"));
        assertFalse(Tag.isValidTagName("friends/school"));
        assertFalse(Tag.isValidTagName("friends\tteam"));
        assertFalse(Tag.isValidTagName("friends\nteam"));
    }

    @Test
    public void constructor_invalidFieldValue_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> new Tag("friends team"));
    }


    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Tag(null));
    }

    @Test
    public void constructor_invalidTagName_throwsIllegalArgumentException() {
        String invalidTagName = "";
        assertThrows(IllegalArgumentException.class, () -> new Tag(invalidTagName));
    }

    @Test
    public void isValidTagName() {
        // null tag name
        assertThrows(NullPointerException.class, () -> Tag.isValidTagName(null));
    }

}
