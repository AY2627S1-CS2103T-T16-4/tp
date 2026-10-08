package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.CommandTestUtil.showPersonAtIndex;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.model.person.Remark;
import seedu.address.testutil.PersonBuilder;

public class RemarkCommandTest {
    private final Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_addReplaceAndClearRemark_success() {
        assertRemarkSuccess("Likes swimming");
        assertRemarkSuccess("Prefers running");
        assertRemarkSuccess("");
    }

    @Test
    public void execute_filteredList_updatesDisplayedPerson() {
        Person firstPerson = model.getFilteredPersonList().get(0);
        showPersonAtIndex(model, INDEX_SECOND_PERSON);
        assertRemarkSuccess("Filtered person");
        assertEquals(firstPerson, model.getAddressBook().getPersonList().get(0));
    }

    @Test
    public void execute_invalidIndex_failureWithoutChanges() {
        Index invalidIndex = Index.fromOneBased(model.getFilteredPersonList().size() + 1);
        assertCommandFailure(new RemarkCommand(invalidIndex, new Remark("Note")), model,
                Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        showPersonAtIndex(model, INDEX_FIRST_PERSON);
        assertCommandFailure(new RemarkCommand(INDEX_SECOND_PERSON, new Remark("Note")), model,
                Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void execute_editOtherFields_preservesRemark() throws Exception {
        new RemarkCommand(INDEX_FIRST_PERSON, new Remark("Keep this note")).execute(model);
        EditCommand.EditPersonDescriptor descriptor = new EditCommand.EditPersonDescriptor();
        descriptor.setPhone(new Phone("87654321"));
        new EditCommand(INDEX_FIRST_PERSON, descriptor).execute(model);
        assertEquals(new Remark("Keep this note"), model.getFilteredPersonList().get(0).getRemark());
    }

    @Test
    public void equals_comparesIndexAndRemark() {
        RemarkCommand command = new RemarkCommand(INDEX_FIRST_PERSON, new Remark("Note"));
        assertEquals(command, new RemarkCommand(INDEX_FIRST_PERSON, new Remark("Note")));
        assertNotEquals(command, new RemarkCommand(INDEX_SECOND_PERSON, new Remark("Note")));
        assertNotEquals(command, new RemarkCommand(INDEX_FIRST_PERSON, new Remark("Other")));
    }

    private void assertRemarkSuccess(String text) {
        Person original = model.getFilteredPersonList().get(0);
        Person updated = new PersonBuilder(original).withRemark(text).build();
        Model expected = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expected.setPerson(original, updated);
        String message = text.isEmpty() ? RemarkCommand.MESSAGE_DELETE_REMARK_SUCCESS
                : RemarkCommand.MESSAGE_ADD_REMARK_SUCCESS;
        assertCommandSuccess(new RemarkCommand(INDEX_FIRST_PERSON, new Remark(text)), model,
                String.format(message, Messages.format(updated)), expected);
    }
}
