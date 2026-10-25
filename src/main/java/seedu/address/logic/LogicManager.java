package seedu.address.logic;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Logger;

import javafx.collections.ObservableList;
import seedu.address.commons.core.GuiSettings;
import seedu.address.commons.core.LogsCenter;
import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.commands.ClearCommand;
import seedu.address.logic.commands.Command;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.DeleteCommand;
import seedu.address.logic.commands.EditCommand;
import seedu.address.logic.commands.FindCommand;
import seedu.address.logic.commands.ListCommand;
import seedu.address.logic.commands.PinCommand;
import seedu.address.logic.commands.SortCommand;
import seedu.address.logic.commands.UndoCommand;
import seedu.address.logic.commands.UnpinCommand;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.AddressBookParser;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.person.Person;
import seedu.address.storage.Storage;

/**
 * The main LogicManager of the app.
 */
public class LogicManager implements Logic {
    public static final String FILE_OPS_ERROR_FORMAT = "Could not save data due to the following error: %s";

    public static final String FILE_OPS_PERMISSION_ERROR_FORMAT =
            "Could not save data to file %s due to insufficient permissions to write to the file or the folder.";

    private final Logger logger = LogsCenter.getLogger(LogicManager.class);

    private final Model model;
    private final Storage storage;
    private final AddressBookParser addressBookParser;

    /**
     * Constructs a {@code LogicManager} with the given {@code Model} and {@code Storage}.
     */
    public LogicManager(Model model, Storage storage) {
        this.model = model;
        this.storage = storage;
        addressBookParser = new AddressBookParser(getExportDirectory(storage.getAddressBookFilePath()));
    }

    /**
     * Returns the folder holding {@code dataFilePath}, so exports are saved next to the data file.
     * A bare filename has no folder part, in which case the working directory is used.
     */
    static Path getExportDirectory(Path dataFilePath) {
        Path parent = dataFilePath.getParent();
        return parent != null ? parent : Paths.get("");
    }

    @Override
    public CommandResult execute(String commandText) throws CommandException, ParseException {
        logger.info("----------------[USER COMMAND][" + commandText + "]");

        Command command = addressBookParser.parseCommand(commandText);

        ReadOnlyAddressBook previousState = null;

        if (command.isUndoable()) {
            previousState = new AddressBook(model.getAddressBook());
        }

        CommandResult commandResult = command.execute(model);

        if (previousState != null) {
            model.saveStateForUndo(previousState);
        }

        try {
            storage.saveAddressBook(model.getAddressBook());
        } catch (AccessDeniedException e) {
            throw new CommandException(String.format(FILE_OPS_PERMISSION_ERROR_FORMAT, e.getMessage()), e);
        } catch (IOException ioe) {
            throw new CommandException(String.format(FILE_OPS_ERROR_FORMAT, ioe.getMessage()), ioe);
        }

        return new CommandResult(commandResult.getFeedbackToUser(), commandResult.isShowHelp(), commandResult.isExit(),
                shouldResetContactSelection(command));
    }

    /**
     * Returns whether executing {@code command} should select the first visible contact.
     * Contact-view commands belong here so their selection policy is kept in one place.
     */
    private static boolean shouldResetContactSelection(Command command) {
        return command instanceof AddCommand
                || command instanceof ClearCommand
                || command instanceof DeleteCommand
                || command instanceof EditCommand
                || command instanceof FindCommand
                || command instanceof ListCommand
                || command instanceof PinCommand
                || command instanceof UnpinCommand
                || command instanceof SortCommand
                || command instanceof UndoCommand;
    }

    @Override
    public ObservableList<Person> getFilteredPersonList() {
        return model.getFilteredPersonList();
    }

    @Override
    public GuiSettings getGuiSettings() {
        return model.getGuiSettings();
    }

    @Override
    public void setGuiSettings(GuiSettings guiSettings) {
        model.setGuiSettings(guiSettings);
    }
}
