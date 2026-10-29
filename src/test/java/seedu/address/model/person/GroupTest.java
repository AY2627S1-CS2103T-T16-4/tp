package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class GroupTest {
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
