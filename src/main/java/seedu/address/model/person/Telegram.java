package seedu.address.model.person;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

/** Represents a contact's Telegram username. */
public class Telegram {
    public static final String MESSAGE_CONSTRAINTS =
            "Telegram usernames should contain only letters, numbers, and underscores";
    public static final String VALIDATION_REGEX = "[A-Za-z0-9_]*";

    public final String value;

    /**
     * Constructs an {@code Telegram}.
     *
     * @param telegram A valid Telegram handle.
     */
    public Telegram(String telegram) {
        requireNonNull(telegram);
        checkArgument(isValidTelegram(telegram), MESSAGE_CONSTRAINTS);
        value = telegram.toLowerCase();
    }

    public static boolean isValidTelegram(String telegram) {
        return telegram.matches(VALIDATION_REGEX);
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
        if (!(other instanceof Telegram otherTelegram)) {
            return false;
        }
        return value.equals(otherTelegram.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
