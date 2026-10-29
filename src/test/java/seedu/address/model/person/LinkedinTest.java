package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class LinkedinTest {

    @Test
    public void fieldValues_validAndInvalidInputs() {
        assertTrue(Linkedin.isValidLinkedin(""));
        assertTrue(Linkedin.isValidLinkedin("John123"));
        assertTrue(Linkedin.isValidLinkedin("john-doe"));
        assertFalse(Linkedin.isValidLinkedin(" "));
        assertFalse(Linkedin.isValidLinkedin("john_doe"));
        assertFalse(Linkedin.isValidLinkedin("john.doe"));
        assertFalse(Linkedin.isValidLinkedin("john doe"));
        assertFalse(Linkedin.isValidLinkedin("john/doe"));
        assertFalse(Linkedin.isValidLinkedin("john;doe"));
        assertFalse(Linkedin.isValidLinkedin("john\tdoe"));
        assertFalse(Linkedin.isValidLinkedin("john\ndoe"));
    }

    @Test
    public void constructor_invalidFieldValue_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> new Linkedin("john_doe"));
    }

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
