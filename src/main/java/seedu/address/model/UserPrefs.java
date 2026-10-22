package seedu.address.model;

import static java.util.Objects.requireNonNull;

import java.util.Objects;

import seedu.address.commons.core.GuiSettings;

/**
 * Represents User's preferences.
 */
public class UserPrefs implements ReadOnlyUserPrefs {

    private GuiSettings guiSettings = new GuiSettings();
    private SortSettings savedSortSettings;

    /**
     * Creates a {@code UserPrefs} with default values.
     */
    public UserPrefs() {}

    /**
     * Creates a {@code UserPrefs} with the prefs in {@code userPrefs}.
     */
    public UserPrefs(ReadOnlyUserPrefs userPrefs) {
        requireNonNull(userPrefs);
        setGuiSettings(userPrefs.getGuiSettings());
        setSavedSortSettings(userPrefs.getSavedSortSettings());
    }

    public GuiSettings getGuiSettings() {
        return guiSettings;
    }

    public void setGuiSettings(GuiSettings guiSettings) {
        requireNonNull(guiSettings);
        this.guiSettings = guiSettings;
    }

    @Override
    public SortSettings getSavedSortSettings() {
        return savedSortSettings;
    }

    /** Sets the startup sort preference; null disables restoration. */
    public void setSavedSortSettings(SortSettings savedSortSettings) {
        this.savedSortSettings = savedSortSettings;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof UserPrefs otherUserPrefs)) {
            return false;
        }

        return guiSettings.equals(otherUserPrefs.guiSettings)
                && Objects.equals(savedSortSettings, otherUserPrefs.savedSortSettings);
    }

    @Override
    public int hashCode() {
        return Objects.hash(guiSettings, savedSortSettings);
    }

    @Override
    public String toString() {
        return "Gui Settings : " + guiSettings + ", Saved Sort Settings : " + savedSortSettings;
    }

}
