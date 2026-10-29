package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class GroupTest {

    @Test
    public void fieldValues_validAndInvalidInputs() {
        assertTrue(Group.isValidGroup("CS2103"));
        assertTrue(Group.isValidGroup("a"));
        assertTrue(Group.isValidGroup("123"));
        assertFalse(Group.isValidGroup(""));
        assertFalse(Group.isValidGroup(" "));
        assertFalse(Group.isValidGroup("alpha beta"));
        assertFalse(Group.isValidGroup("alpha-beta"));
        assertFalse(Group.isValidGroup("alpha_beta"));
        assertFalse(Group.isValidGroup("alpha;beta"));
        assertFalse(Group.isValidGroup("alpha/beta"));
        assertFalse(Group.isValidGroup("alpha\tbeta"));
        assertFalse(Group.isValidGroup("alpha\nbeta"));
    }

    @Test
    public void constructor_invalidFieldValue_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> new Group("alpha beta"));
    }

    @Test
    public void isValidGroup() {
        assertThrows(NullPointerException.class, () -> Group.isValidGroup(null));

        assertFalse(Group.isValidGroup(""));
        assertFalse(Group.isValidGroup("alpha beta"));
        assertFalse(Group.isValidGroup("alpha-beta"));
        assertFalse(Group.isValidGroup("alpha_beta"));
        assertFalse(Group.isValidGroup("alpha;beta"));
        assertTrue(Group.isValidGroup("CS2103"));
    }
}
