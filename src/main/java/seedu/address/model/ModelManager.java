package seedu.address.model;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.ArrayDeque;
import java.util.Comparator;
import java.util.Deque;
import java.util.function.Predicate;
import java.util.logging.Logger;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import seedu.address.commons.core.GuiSettings;
import seedu.address.commons.core.LogsCenter;
import seedu.address.model.person.Person;

/**
 * Represents the in-memory model of the address book data.
 */
public class ModelManager implements Model {
    private static final Logger logger = LogsCenter.getLogger(ModelManager.class);

    private final AddressBook addressBook;
    private final Deque<AddressBook> undoHistory = new ArrayDeque<>();
    private final UserPrefs userPrefs;
    private final FilteredList<Person> filteredPersons;
    private final SortedList<Person> sortedPersons;
    private final ObservableList<Person> displayedPersons;
    private Predicate<Person> currentPredicate = PREDICATE_SHOW_ALL_PERSONS;

    /**
     * Initializes a ModelManager with the given addressBook and userPrefs.
     */
    public ModelManager(ReadOnlyAddressBook addressBook, ReadOnlyUserPrefs userPrefs) {
        requireAllNonNull(addressBook, userPrefs);

        logger.fine("Initializing with address book: " + addressBook + " and user prefs " + userPrefs);

        this.addressBook = new AddressBook(addressBook);
        this.userPrefs = new UserPrefs(userPrefs);
        filteredPersons = new FilteredList<>(this.addressBook.getPersonList());
        // SortedList can reorder equal keys on insertion, so use the stored position to break ties.
        Comparator<Person> displayOrder = Comparator.comparing(Person::isPinned).reversed()
                .thenComparingInt(this.addressBook.getPersonList()::indexOf);
        sortedPersons = new SortedList<>(filteredPersons, displayOrder);
        displayedPersons = FXCollections.unmodifiableObservableList(sortedPersons);
        SortSettings savedSortSettings = this.userPrefs.getSavedSortSettings();
        if (savedSortSettings != null) {
            applySortSettings(savedSortSettings);
        }
    }

    public ModelManager() {
        this(new AddressBook(), new UserPrefs());
    }

    //=========== UserPrefs ==================================================================================

    @Override
    public ReadOnlyUserPrefs getUserPrefs() {
        return userPrefs;
    }

    @Override
    public GuiSettings getGuiSettings() {
        return userPrefs.getGuiSettings();
    }

    @Override
    public void setGuiSettings(GuiSettings guiSettings) {
        requireNonNull(guiSettings);
        userPrefs.setGuiSettings(guiSettings);
    }

    //=========== AddressBook ================================================================================

    @Override
    public void setAddressBook(ReadOnlyAddressBook addressBook) {
        this.addressBook.resetData(addressBook);
    }

    @Override
    public ReadOnlyAddressBook getAddressBook() {
        return addressBook;
    }

    @Override
    public boolean hasPerson(Person person) {
        requireNonNull(person);
        return addressBook.hasPerson(person);
    }

    @Override
    public void deletePerson(Person target) {
        addressBook.removePerson(target);
    }

    @Override
    public void addPerson(Person person) {
        addressBook.addPerson(person);
    }

    @Override
    public void setPerson(Person target, Person editedPerson) {
        requireAllNonNull(target, editedPerson);

        addressBook.setPerson(target, editedPerson);
    }

    //=========== Filtered Person List Accessors =============================================================

    /**
     * Returns matching contacts with pinned contacts first and the selected sort order within each section.
     * Before sorting, addition order is preserved within each section.
     */
    @Override
    public ObservableList<Person> getFilteredPersonList() {
        return displayedPersons;
    }

    @Override
    public void sortFilteredPersonList(SortSettings settings, boolean isContinuous) {
        requireNonNull(settings);
        applySortSettings(settings);
        userPrefs.setSavedSortSettings(isContinuous ? settings : null);
    }

    /** Applies the field order while retaining pin priority and stable tie-breaking. */
    private void applySortSettings(SortSettings settings) {
        sortedPersons.setComparator(Comparator.comparing(Person::isPinned).reversed()
                .thenComparing(settings.createComparator())
                .thenComparingInt(addressBook.getPersonList()::indexOf));
    }

    @Override
    public void updateFilteredPersonList(Predicate<Person> predicate) {
        requireNonNull(predicate);
        currentPredicate = predicate;
        filteredPersons.setPredicate(predicate);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof ModelManager otherModelManager)) {
            return false;
        }

        return addressBook.equals(otherModelManager.addressBook)
                && userPrefs.equals(otherModelManager.userPrefs)
                && displayedPersons.equals(otherModelManager.displayedPersons);
    }

    //=========== Undo ======================================================================================

    @Override
    public void saveStateForUndo(ReadOnlyAddressBook state) {
        requireNonNull(state);
        undoHistory.push(new AddressBook(state));
    }

    @Override
    public boolean undo() {
        if (undoHistory.isEmpty()) {
            return false;
        }

        addressBook.resetData(undoHistory.pop());
        updateFilteredPersonList(currentPredicate);
        return true;
    }

}
