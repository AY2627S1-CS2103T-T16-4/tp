package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BENSON;
import static seedu.address.testutil.TypicalPersons.DANIEL;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.commons.exceptions.DataLoadingException;
import seedu.address.logic.commands.EditCommand;
import seedu.address.logic.parser.AddressBookParser;
import seedu.address.model.ModelManager;
import seedu.address.model.SortSettings;
import seedu.address.model.SortSettings.Field;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

/** Exercises sort preferences through the real JSON storage and startup model. */
public class SortPreferencesStorageTest {
    @TempDir
    public Path testFolder;

    @Test
    public void savedSort_reload_restoresOrderAndRemainsLive() throws Exception {
        ModelManager model = new ModelManager();
        model.addPerson(DANIEL);
        model.addPerson(BENSON);
        AddressBookParser parser = new AddressBookParser();
        parser.parseCommand("sort name asc cont").execute(model);
        JsonUserPrefsStorage storage = new JsonUserPrefsStorage(testFolder.resolve("preferences.json"));
        storage.saveUserPrefs(model.getUserPrefs());

        ModelManager restarted = new ModelManager(model.getAddressBook(), storage.readUserPrefs().orElseThrow());
        assertEquals(List.of(BENSON, DANIEL), restarted.getFilteredPersonList());
        assertEquals(List.of(DANIEL, BENSON), restarted.getAddressBook().getPersonList());
        assertEquals(new SortSettings(Field.NAME, false), restarted.getUserPrefs().getSavedSortSettings());
        restarted.addPerson(ALICE);
        assertEquals(List.of(ALICE, BENSON, DANIEL), restarted.getFilteredPersonList());
        parser.parseCommand("edit n/Alice Pauline " + EditCommand.EDIT_SEPARATOR + " n/Zoe").execute(restarted);
        Person zoe = new PersonBuilder(ALICE).withName("Zoe").build();
        assertEquals(List.of(BENSON, DANIEL, zoe), restarted.getFilteredPersonList());
    }

    @Test
    public void sessionOnlySort_reload_clearsEarlierPreference() throws Exception {
        for (String command : List.of("sort name asc", "sort name asc none")) {
            ModelManager model = new ModelManager();
            model.addPerson(DANIEL);
            model.addPerson(ALICE);
            AddressBookParser parser = new AddressBookParser();
            parser.parseCommand("sort tag desc cont").execute(model);
            parser.parseCommand(command).execute(model);
            assertEquals(List.of(ALICE, DANIEL), model.getFilteredPersonList());
            assertNull(model.getUserPrefs().getSavedSortSettings());

            JsonUserPrefsStorage storage = new JsonUserPrefsStorage(testFolder.resolve("preferences.json"));
            storage.saveUserPrefs(model.getUserPrefs());
            ModelManager restarted = new ModelManager(model.getAddressBook(), storage.readUserPrefs().orElseThrow());
            assertEquals(List.of(DANIEL, ALICE), restarted.getFilteredPersonList());
        }
    }

    @Test
    public void saveAndLoad_eachFieldAndDirection_preservesSettingsAndGui() throws Exception {
        JsonUserPrefsStorage storage = new JsonUserPrefsStorage(testFolder.resolve("preferences.json"));
        for (Field field : Field.values()) {
            for (boolean isDescending : List.of(false, true)) {
                UserPrefs prefs = new UserPrefs();
                prefs.setSavedSortSettings(new SortSettings(field, isDescending));
                storage.saveUserPrefs(prefs);
                UserPrefs loaded = storage.readUserPrefs().orElseThrow();
                assertEquals(prefs, loaded);
                assertEquals(prefs, new UserPrefs(loaded));
                assertEquals(prefs.hashCode(), loaded.hashCode());
            }
        }
    }

    @Test
    public void read_missingOrNullSortSettings_usesDefault() throws Exception {
        Path path = testFolder.resolve("preferences.json");
        for (String json : List.of("{}", "{\"savedSortSettings\":null}")) {
            Files.writeString(path, json);
            assertEquals(new UserPrefs(), new JsonUserPrefsStorage(path).readUserPrefs().orElseThrow());
        }
    }

    @Test
    public void read_invalidSortField_throwsDataLoadingException() throws Exception {
        Path path = testFolder.resolve("preferences.json");
        for (String settings : List.of("{}", "{\"field\":null}", "{\"field\":\"PHONE\"}")) {
            Files.writeString(path, "{\"savedSortSettings\":" + settings + "}");
            assertThrows(DataLoadingException.class, () -> new JsonUserPrefsStorage(path).readUserPrefs());
        }
    }
}
