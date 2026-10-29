package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class TelegramTest {
    @Test
    public void isValidTelegram() {
        assertThrows(NullPointerException.class, () -> Telegram.isValidTelegram(null));

        assertTrue(Telegram.isValidTelegram("john_doe123"));
        assertTrue(Telegram.isValidTelegram(""));
        assertFalse(Telegram.isValidTelegram("john-doe"));
        assertFalse(Telegram.isValidTelegram("john.doe"));
        assertFalse(Telegram.isValidTelegram("john doe"));
        assertFalse(Telegram.isValidTelegram("@john"));
    }
}
