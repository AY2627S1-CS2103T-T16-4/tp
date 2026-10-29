package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class LinkedinTest {
    @Test
    public void isValidLinkedin() {
        assertThrows(NullPointerException.class, () -> Linkedin.isValidLinkedin(null));

        assertTrue(Linkedin.isValidLinkedin("john-doe123"));
        assertTrue(Linkedin.isValidLinkedin(""));
        assertFalse(Linkedin.isValidLinkedin("john_doe"));
        assertFalse(Linkedin.isValidLinkedin("john.doe"));
        assertFalse(Linkedin.isValidLinkedin("john doe"));
        assertFalse(Linkedin.isValidLinkedin("john/doe"));
    }
}
