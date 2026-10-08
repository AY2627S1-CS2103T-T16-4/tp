package seedu.address.model.person;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

/** Represents a contact's Telegram username. */
public class Telegram {
    public static final String MESSAGE_CONSTRAINTS =
            "Telegram usernames must not contain @";

    public final String value;

    public Telegram(String telegram) {
        requireNonNull(telegram);
        checkArgument(isValidTelegram(telegram), MESSAGE_CONSTRAINTS);
        value = telegram.toLowerCase();
    }

    public static boolean isValidTelegram(String telegram) {
        return !telegram.contains("@");
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
