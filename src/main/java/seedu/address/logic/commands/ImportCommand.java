package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.person.Person;
import seedu.address.storage.CsvImporter;

/** Imports a fully validated CSV batch without overwriting existing contacts. */
public class ImportCommand extends Command {
    public static final String COMMAND_WORD = "import";
    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Imports contacts from a GitMate CSV file as one undoable batch.\n"
            + "Parameters: FILE_PATH (spaces allowed; do not surround the path with quotes)\n"
            + "Relative paths start from the application's working directory.\n"
            + "Example: " + COMMAND_WORD + " data/contacts.csv";
    public static final String MESSAGE_SUCCESS = "Imported %1$d contact(s) from %2$s. "
            + "The current filter is preserved; use " + ListCommand.COMMAND_WORD + " to show all contacts.";
    public static final String MESSAGE_FAILURE = "Could not import contacts: %1$s No contacts were imported.";
    public static final String MESSAGE_EMPTY = "The CSV file contains no contacts. No contacts were imported.";
    public static final String MESSAGE_DUPLICATE = "Could not import record %1$d: phone number %2$s "
            + "is already in use in the address book or an earlier CSV record. No contacts were imported.";
    public static final String MESSAGE_INVALID_PATH = "Invalid import file path: %1$s";

    private final Path file;

    /** Creates a command importing the specified file. */
    public ImportCommand(Path file) {
        this.file = requireNonNull(file);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Person> persons;
        try {
            persons = CsvImporter.read(file);
        } catch (IOException | IllegalValueException e) {
            throw new CommandException(String.format(MESSAGE_FAILURE, e.getMessage()), e);
        }
        if (persons.isEmpty()) {
            throw new CommandException(MESSAGE_EMPTY);
        }
        AddressBook updatedAddressBook = new AddressBook(model.getAddressBook());
        for (int index = 0; index < persons.size(); index++) {
            Person person = persons.get(index);
            if (updatedAddressBook.hasPerson(person)) {
                throw new CommandException(String.format(MESSAGE_DUPLICATE,
                        index + CsvImporter.FIRST_DATA_RECORD, person.getPhone().value));
            }
            updatedAddressBook.addPerson(person);
        }
        model.setAddressBook(updatedAddressBook);
        return new CommandResult(String.format(MESSAGE_SUCCESS, persons.size(), file.toAbsolutePath().normalize()));
    }

    @Override
    public boolean isUndoable() {
        return true;
    }

    @Override
    public boolean equals(Object other) {
        return other == this || other instanceof ImportCommand otherCommand && file.equals(otherCommand.file);
    }

    @Override
    public int hashCode() {
        return file.hashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("file", file).toString();
    }
}
