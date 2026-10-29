package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class GithubTest {
    @Test
    public void isValidGithub() {
        assertThrows(NullPointerException.class, () -> Github.isValidGithub(null));
        assertTrue(Github.isValidGithub("john-doe_123"));
        assertTrue(Github.isValidGithub("john;doe"));
        assertTrue(Github.isValidGithub(""));
        assertFalse(Github.isValidGithub("john doe"));
        assertFalse(Github.isValidGithub("john\tdoe"));
    }
}
