package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import seedu.address.model.AddressBook;
import seedu.address.model.ModelManager;
import seedu.address.testutil.TypicalPersons;

public class UndoCommandTest {

    @Test
    public void execute_previousStateExists_restoresState() {
        ModelManager model = new ModelManager();
        UndoCommand undoCommand = new UndoCommand();

        model.addPerson(TypicalPersons.ALICE);
        model.saveStateForUndo(new AddressBook(model.getAddressBook()));
        model.deletePerson(TypicalPersons.ALICE);

        CommandResult result = undoCommand.execute(model);

        assertEquals(UndoCommand.MESSAGE_SUCCESS, result.getFeedbackToUser());
        assertEquals(1, model.getFilteredPersonList().size());
        assertEquals(TypicalPersons.ALICE, model.getFilteredPersonList().get(0));
    }

    @Test
    public void execute_noPreviousState_returnsNoUndoMessage() {
        ModelManager model = new ModelManager();
        UndoCommand undoCommand = new UndoCommand();

        CommandResult result = undoCommand.execute(model);

        assertEquals(UndoCommand.MESSAGE_NO_UNDO, result.getFeedbackToUser());
    }

    @Test
    public void execute_nullModel_throwsNullPointerException() {
        UndoCommand undoCommand = new UndoCommand();

        assertThrows(NullPointerException.class, () -> undoCommand.execute(null));
    }
}
