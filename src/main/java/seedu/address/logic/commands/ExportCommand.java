package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Person;
import seedu.address.storage.CsvExporter;

/**
 * Exports every contact, in the order shown on screen, to a new timestamped CSV file.
 */
public class ExportCommand extends Command {

    public static final String COMMAND_WORD = "export";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Exports all contacts, in the order shown, to a CSV file next to the data file.\n"
            + "Example: " + COMMAND_WORD;

    public static final String MESSAGE_SUCCESS = "Exported %1$d contact(s) to %2$s";
    public static final String MESSAGE_FILE_EXISTS = "Could not export because %1$s already exists. Please try again.";
    public static final String MESSAGE_FAILURE = "Could not export contacts due to the following error: %1$s";

    private static final DateTimeFormatter FILENAME_FORMAT =
            DateTimeFormatter.ofPattern("'contacts-'yyyyMMdd-HHmmss'.csv'");

    private final Path directory;
    private final Clock clock;

    /**
     * Creates a command that exports into {@code directory}, named after the current time.
     */
    public ExportCommand(Path directory) {
        this(directory, Clock.systemDefaultZone());
    }

    /**
     * Creates a command that exports into {@code directory}, named after the time given by {@code clock}.
     */
    public ExportCommand(Path directory, Clock clock) {
        this.directory = requireNonNull(directory);
        this.clock = requireNonNull(clock);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        List<Person> persons = model.getAllPersonsInDisplayOrder();
        Path file = directory.resolve(LocalDateTime.now(clock).format(FILENAME_FORMAT)).toAbsolutePath().normalize();

        try {
            CsvExporter.write(persons, file);
        } catch (FileAlreadyExistsException e) {
            // The same exception is thrown when the export folder is actually a regular file.
            if (Files.exists(file)) {
                throw new CommandException(String.format(MESSAGE_FILE_EXISTS, file), e);
            }
            throw new CommandException(String.format(MESSAGE_FAILURE, e), e);
        } catch (IOException e) {
            throw new CommandException(String.format(MESSAGE_FAILURE, e), e);
        }

        return new CommandResult(String.format(MESSAGE_SUCCESS, persons.size(), file));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        return other instanceof ExportCommand otherExportCommand
                && directory.equals(otherExportCommand.directory)
                && clock.equals(otherExportCommand.clock);
    }

    @Override
    public int hashCode() {
        return Objects.hash(directory, clock);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("directory", directory)
                .toString();
    }
}
