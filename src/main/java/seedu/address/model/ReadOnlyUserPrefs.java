package seedu.address.model;

import seedu.address.commons.core.GuiSettings;

/**
 * Unmodifiable view of user prefs.
 */
public interface ReadOnlyUserPrefs {

    GuiSettings getGuiSettings();

    /** Returns the sort order to restore on startup, or null for no saved sort. */
    SortSettings getSavedSortSettings();

}
