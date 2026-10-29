package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static seedu.address.logic.commands.CommandTestUtil.showPersonAtIndex;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.SortSettings;
import seedu.address.model.SortSettings.Field;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.storage.CsvExporter;

public class ExportCommandTest {

    private static final Clock FIXED_CLOCK = Clock.fixed(Instant.parse("2026-10-25T14:30:12Z"), ZoneOffset.UTC);
    private static final String EXPECTED_FILENAME = "contacts-20261025-143012.csv";
    private static final String BOM = "﻿";

    @TempDir
    public Path testFolder;

    private Model model;

    @BeforeEach
    public void setUp() {
        model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
    }

    @Test
    public void execute_typicalAddressBook_writesAllContactsAndReportsPath() throws Exception {
        CommandResult result = new ExportCommand(testFolder, FIXED_CLOCK).execute(model);

        Path expectedFile = testFolder.resolve(EXPECTED_FILENAME).toAbsolutePath().normalize();
        assertTrue(Files.exists(expectedFile));
        assertEquals(String.format(ExportCommand.MESSAGE_SUCCESS, model.getAddressBook().getPersonList().size(),
                expectedFile), result.getFeedbackToUser());
        assertEquals(model.getAddressBook().getPersonList().size() + 1, readLines(expectedFile).size());
        assertEquals(BOM + CsvExporter.HEADER, readLines(expectedFile).get(0));
    }

    @Test
    public void execute_filterActive_exportsAllContacts() throws Exception {
        int totalContacts = model.getAddressBook().getPersonList().size();
        showPersonAtIndex(model, INDEX_FIRST_PERSON);
        assertEquals(1, model.getFilteredPersonList().size());

        new ExportCommand(testFolder, FIXED_CLOCK).execute(model);

        assertEquals(totalContacts + 1, readLines(testFolder.resolve(EXPECTED_FILENAME)).size());
    }

    @Test
    public void execute_pinnedAndSorted_exportsInDisplayOrder() throws Exception {
        Person first = model.getAddressBook().getPersonList().get(0);
        Person last = model.getAddressBook().getPersonList().get(model.getAddressBook().getPersonList().size() - 1);
        model.setPerson(last, new Person(last, true));
        model.sortFilteredPersonList(new SortSettings(Field.NAME, true), false);
        List<Person> displayOrder = model.getAllPersonsInDisplayOrder();

        new ExportCommand(testFolder, FIXED_CLOCK).execute(model);

        List<String> lines = readLines(testFolder.resolve(EXPECTED_FILENAME));
        for (int i = 0; i < displayOrder.size(); i++) {
            assertTrue(lines.get(i + 1).startsWith(displayOrder.get(i).getName().fullName + ","),
                    "Row " + i + " should be " + displayOrder.get(i).getName());
        }
        assertTrue(lines.get(1).endsWith(",true"), "Pinned contact should be listed first");
        assertNotEquals(first, displayOrder.get(0));
    }

    @Test
    public void execute_emptyAddressBook_writesHeaderOnly() throws Exception {
        Model emptyModel = new ModelManager();

        CommandResult result = new ExportCommand(testFolder, FIXED_CLOCK).execute(emptyModel);

        assertEquals(List.of(BOM + CsvExporter.HEADER), readLines(testFolder.resolve(EXPECTED_FILENAME)));
        assertTrue(result.getFeedbackToUser().startsWith("Exported 0 contact(s)"));
    }

    @Test
    public void execute_missingDirectory_createsIt() throws Exception {
        Path missing = testFolder.resolve("data");

        new ExportCommand(missing, FIXED_CLOCK).execute(model);

        assertTrue(Files.exists(missing.resolve(EXPECTED_FILENAME)));
    }

    @Test
    public void execute_modelUnchangedAndNotUndoable() throws Exception {
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        ExportCommand command = new ExportCommand(testFolder, FIXED_CLOCK);

        command.execute(model);

        assertEquals(expectedModel, model);
        assertFalse(command.isUndoable());
    }

    @Test
    public void execute_fileAlreadyExists_throwsCommandExceptionAndKeepsFile() throws Exception {
        Path existing = testFolder.resolve(EXPECTED_FILENAME);
        Files.writeString(existing, "original");

        CommandException exception = assertThrows(CommandException.class, () ->
                new ExportCommand(testFolder, FIXED_CLOCK).execute(model));

        assertEquals(String.format(ExportCommand.MESSAGE_FILE_EXISTS, existing.toAbsolutePath().normalize()),
                exception.getMessage());
        assertEquals("original", Files.readString(existing));
    }

    @Test
    public void execute_directoryIsRegularFile_throwsCommandException() throws Exception {
        Path notADirectory = testFolder.resolve("data");
        Files.writeString(notADirectory, "not a folder");

        CommandException exception = assertThrows(CommandException.class, () ->
                new ExportCommand(notADirectory, FIXED_CLOCK).execute(model));

        assertTrue(exception.getMessage().startsWith("Could not export contacts due to the following error:"));
    }

    @Test
    public void execute_unwritableDirectory_throwsCommandExceptionWithPermissionMessage() throws Exception {
        Path readOnly = Files.createDirectory(testFolder.resolve("readonly"));
        // Skipped where folder permissions are not enforced, such as on Windows or when running as root.
        assumeTrue(readOnly.toFile().setWritable(false) && !Files.isWritable(readOnly),
                "Folder permissions cannot be restricted on this system");
        try {
            CommandException exception = assertThrows(CommandException.class, () ->
                    new ExportCommand(readOnly, FIXED_CLOCK).execute(model));

            assertEquals(String.format(ExportCommand.MESSAGE_PERMISSION_DENIED,
                    readOnly.resolve(EXPECTED_FILENAME).toAbsolutePath().normalize()), exception.getMessage());
        } finally {
            readOnly.toFile().setWritable(true);
        }
    }

    @Test
    public void execute_nullModel_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new ExportCommand(testFolder).execute(null));
    }

    @Test
    public void constructor_nullArguments_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new ExportCommand(null));
        assertThrows(NullPointerException.class, () -> new ExportCommand(testFolder, null));
    }

    @Test
    public void equals() {
        ExportCommand command = new ExportCommand(testFolder, FIXED_CLOCK);

        assertTrue(command.equals(command));
        assertTrue(command.equals(new ExportCommand(testFolder, FIXED_CLOCK)));
        assertEquals(command.hashCode(), new ExportCommand(testFolder, FIXED_CLOCK).hashCode());
        assertFalse(command.equals(null));
        assertFalse(command.equals(1));
        assertFalse(command.equals(new ExportCommand(testFolder.resolve("other"), FIXED_CLOCK)));
        assertFalse(command.equals(new ExportCommand(testFolder, Clock.fixed(Instant.EPOCH, ZoneOffset.UTC))));
    }

    @Test
    public void toStringMethod() {
        ExportCommand command = new ExportCommand(testFolder, FIXED_CLOCK);

        assertEquals(ExportCommand.class.getCanonicalName() + "{directory=" + testFolder + "}", command.toString());
    }

    private static List<String> readLines(Path file) throws IOException {
        return Files.readAllLines(file, StandardCharsets.UTF_8);
    }
}
