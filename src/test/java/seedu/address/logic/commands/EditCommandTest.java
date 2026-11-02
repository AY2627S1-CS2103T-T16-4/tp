package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.DESC_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_GROUP_AMY;
import static seedu.address.logic.commands.CommandTestUtil.VALID_GROUP_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_NAME_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_PHONE_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TAG_FRIEND;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TAG_HUSBAND;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.AMY;
import static seedu.address.testutil.TypicalPersons.BOB;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.EditCommand.EditPersonDescriptor;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.ExactPersonMatchDescriptor;
import seedu.address.model.person.Person;
import seedu.address.model.person.PersonMatchesExactFieldsPredicate;
import seedu.address.testutil.EditPersonDescriptorBuilder;
import seedu.address.testutil.PersonBuilder;

/**
 * Contains integration tests and unit tests for EditCommand.
 */
public class EditCommandTest {

    private Model model;

    @BeforeEach
    public void setUp() {
        model = new ModelManager(new AddressBook(), new UserPrefs());
        model.addPerson(AMY);
    }

    // EP: all editable fields supplied; exactly one matching contact; replacement phone is unique.
    // Boundary value: 1 match, the only count allowing an edit.
    @Test
    public void execute_allFieldsSpecified_success() {
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder(BOB).build();

        // AMY already has friend and VALID_GROUP_AMY.
        // friend is toggled off; husband and VALID_GROUP_BOB are toggled on.
        Person editedPerson = new PersonBuilder(BOB)
                .withTags(VALID_TAG_HUSBAND)
                .withGroups(VALID_GROUP_AMY, VALID_GROUP_BOB)
                .build();

        assertEditSuccess(descriptor, editedPerson);
    }

    // EP: a nonempty proper subset of fields supplied; omitted fields retain their existing values.
    @Test
    public void execute_someFieldsSpecified_success() {
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder()
                .withName(VALID_NAME_BOB)
                .withPhone(VALID_PHONE_BOB)
                .build();

        Person editedPerson = new PersonBuilder(AMY)
                .withName(VALID_NAME_BOB)
                .withPhone(VALID_PHONE_BOB)
                .build();

        assertEditSuccess(descriptor, editedPerson);
    }

    // EP: one matching contact and another nonmatching contact; only the matching contact is edited.
    @Test
    public void execute_otherPersonPresent_success() {
        model.addPerson(BOB);
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withName(VALID_NAME_BOB).build();
        Person editedPerson = new PersonBuilder(AMY).withName(VALID_NAME_BOB).build();

        assertEditSuccess(descriptor, editedPerson);
    }

    // EP: supplied phone equals the target's current phone; this is not a duplicate of another contact.
    @Test
    public void execute_unchangedPhone_success() {
        model.addPerson(BOB);
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder()
                .withPhone(AMY.getPhone().value).build();

        assertEditSuccess(descriptor, AMY);
    }

    // EP: target contact is pinned; editing a field preserves the pin state.
    @Test
    public void execute_pinnedPerson_preservesPin() {
        Person pinnedAmy = new Person(AMY, true);
        model.setPerson(AMY, pinnedAmy);
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withName(VALID_NAME_BOB).build();
        Person editedPerson = new Person(new PersonBuilder(AMY).withName(VALID_NAME_BOB).build(), true);

        assertEditSuccess(pinnedAmy, descriptor, editedPerson);
    }

    // EP: the unique matching contact exists in the address book but is hidden by the display filter.
    @Test
    public void execute_personHiddenByFilter_success() {
        model.addPerson(BOB);
        PersonMatchesExactFieldsPredicate displayPredicate = createPredicateFor(BOB);
        model.updateFilteredPersonList(displayPredicate);
        assertEquals(List.of(BOB), model.getFilteredPersonList());

        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withName(VALID_NAME_BOB).build();
        Person editedPerson = new PersonBuilder(AMY).withName(VALID_NAME_BOB).build();
        EditCommand command = new EditCommand(createPredicateFor(AMY), descriptor);
        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(AMY, editedPerson);
        expectedModel.updateFilteredPersonList(displayPredicate);

        assertCommandSuccess(command, model,
                String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(editedPerson)), expectedModel);
    }

    // EP: only tags supplied; requested tags include an existing and an absent member.
    // EP: unmentioned tags and the omitted group field retain their existing values.
    @Test
    public void execute_onlyTagsSpecified_preservesOtherMemberships() {
        Person personToEdit = new PersonBuilder(AMY).withTags(VALID_TAG_FRIEND, "colleague").build();
        model.setPerson(AMY, personToEdit);
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder()
                .withTags(VALID_TAG_FRIEND, VALID_TAG_HUSBAND).build();
        Person editedPerson = new PersonBuilder(personToEdit).withTags("colleague", VALID_TAG_HUSBAND).build();

        assertEditSuccess(personToEdit, descriptor, editedPerson);
    }

    // EP: only groups supplied; requested groups include an existing and an absent member.
    // EP: unmentioned groups and the omitted tag field retain their existing values.
    @Test
    public void execute_onlyGroupsSpecified_preservesOtherMemberships() {
        Person personToEdit = new PersonBuilder(AMY).withGroups(VALID_GROUP_AMY, "CS2100").build();
        model.setPerson(AMY, personToEdit);
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder()
                .withGroups(VALID_GROUP_AMY, VALID_GROUP_BOB).build();
        Person editedPerson = new PersonBuilder(personToEdit).withGroups("CS2100", VALID_GROUP_BOB).build();

        assertEditSuccess(personToEdit, descriptor, editedPerson);
    }

    // EP: existing tag and group sets are empty; nonempty updates add memberships.
    // Boundary value: 0 existing memberships in each set, at the empty/nonempty boundary.
    @Test
    public void execute_emptyMemberships_addsTagsAndGroups() {
        Person personToEdit = new PersonBuilder(AMY).withTags().withGroups().build();
        model.setPerson(AMY, personToEdit);
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder()
                .withTags(VALID_TAG_FRIEND).withGroups(VALID_GROUP_AMY).build();

        assertEditSuccess(personToEdit, descriptor, AMY);
    }

    // EP: existing optional scalar fields are blank; supplied nonempty values populate them.
    // Boundary value: existing optional values have length 0.
    @Test
    public void execute_blankOptionalFields_populatesFields() {
        Person personToEdit = new PersonBuilder(AMY)
                .withEmail("").withAddress("").withGithub("").withLinkedin("").withTelegram("").build();
        model.setPerson(AMY, personToEdit);
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder()
                .withEmail(AMY.getEmail().value)
                .withAddress(AMY.getAddress().value)
                .withGithub(AMY.getGithub().value)
                .withLinkedin(AMY.getLinkedin().value)
                .withTelegram(AMY.getTelegram().value)
                .build();

        assertEditSuccess(personToEdit, descriptor, AMY);
    }

    // EP: both collection updates are nonempty; existing members are removed and absent members are added.
    @Test
    public void execute_toggleTagsAndGroups_success() {
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder()
                .withTags(VALID_TAG_FRIEND, VALID_TAG_HUSBAND)
                .withGroups(VALID_GROUP_AMY, VALID_GROUP_BOB)
                .build();

        Person editedPerson = new PersonBuilder(AMY)
                .withTags(VALID_TAG_HUSBAND)
                .withGroups(VALID_GROUP_BOB)
                .build();

        assertEditSuccess(descriptor, editedPerson);
    }

    // EP: explicitly empty collection updates clear existing tags and groups rather than preserve them.
    // Boundary value: 0 supplied members in each update set, at the clear/toggle boundary.
    @Test
    public void execute_clearTagsAndGroups_success() {
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder()
                .withTags()
                .withGroups()
                .build();

        Person editedPerson = new PersonBuilder(AMY)
                .withTags()
                .withGroups()
                .build();

        assertEditSuccess(descriptor, editedPerson);
    }

    // EP: explicitly blank optional scalar updates clear previously populated fields.
    // Boundary value: supplied optional values have length 0.
    @Test
    public void execute_clearOptionalFields_success() {
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder()
                .withEmail("")
                .withAddress("")
                .withGithub("")
                .withLinkedin("")
                .withTelegram("")
                .build();

        Person editedPerson = new PersonBuilder(AMY)
                .withEmail("")
                .withAddress("")
                .withGithub("")
                .withLinkedin("")
                .withTelegram("")
                .build();

        assertEditSuccess(descriptor, editedPerson);
    }

    // EP: no contact matches the supplied search criteria.
    // Boundary value: 0 matches, immediately below the required count of 1.
    @Test
    public void execute_noMatchingPerson_failure() {
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder()
                .withName(VALID_NAME_BOB)
                .build();
        EditCommand editCommand = new EditCommand(createPredicateFor(BOB), descriptor);

        assertCommandFailure(editCommand, model, EditCommand.MESSAGE_PERSON_NOT_FOUND);
    }

    // EP: multiple contacts match; failure displays exactly the matches without editing any contact.
    // Boundary value: 2 matches, the smallest ambiguous count immediately above the required count of 1.
    @Test
    public void execute_multipleMatchingPersons_failure() {
        Person anotherAmy = new PersonBuilder(AMY)
                .withPhone("93333333")
                .build();
        model.addPerson(anotherAmy);
        model.addPerson(BOB);
        model.updateFilteredPersonList(createPredicateFor(BOB));
        assertEquals(List.of(BOB), model.getFilteredPersonList());

        ExactPersonMatchDescriptor searchDescriptor = new ExactPersonMatchDescriptor();
        searchDescriptor.setName(AMY.getName());

        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder()
                .withName(VALID_NAME_BOB)
                .build();
        EditCommand editCommand = new EditCommand(
                new PersonMatchesExactFieldsPredicate(searchDescriptor), descriptor);

        AddressBook expectedAddressBook = new AddressBook(model.getAddressBook());

        // Ambiguity intentionally updates the displayed list.
        assertThrows(
                CommandException.class, EditCommand.MESSAGE_AMBIGUOUS_PERSON, () -> editCommand.execute(model));

        assertEquals(expectedAddressBook, model.getAddressBook());
        assertEquals(List.of(AMY, anotherAmy), model.getFilteredPersonList());
    }

    // EP: replacement phone belongs to another contact in the address book; edit is rejected.
    @Test
    public void execute_duplicatePhone_failure() {
        model.addPerson(BOB);

        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder()
                .withPhone(VALID_PHONE_BOB)
                .build();
        EditCommand editCommand = new EditCommand(createPredicateFor(AMY), descriptor);

        assertCommandFailure(editCommand, model, EditCommand.MESSAGE_DUPLICATE_PERSON);
    }

    // EP: replacement phone belongs to another contact hidden by the display filter; edit is rejected.
    @Test
    public void execute_duplicatePhoneHiddenByFilter_failure() {
        model.addPerson(BOB);
        model.updateFilteredPersonList(createPredicateFor(AMY));
        assertEquals(List.of(AMY), model.getFilteredPersonList());
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withPhone(VALID_PHONE_BOB).build();
        EditCommand command = new EditCommand(createPredicateFor(AMY), descriptor);

        assertCommandFailure(command, model, EditCommand.MESSAGE_DUPLICATE_PERSON);
    }

    // EP: multiple search fields jointly identify one contact although the name alone is ambiguous.
    // Boundary value: 1 contact satisfies all criteria, the only valid match count.
    @Test
    public void execute_multipleSearchFields_identifiesUniquePerson() {
        Person anotherAmy = new PersonBuilder(AMY).withPhone(VALID_PHONE_BOB).build();
        model.addPerson(anotherAmy);
        ExactPersonMatchDescriptor searchDescriptor = new ExactPersonMatchDescriptor();
        searchDescriptor.setName(AMY.getName());
        searchDescriptor.setPhone(AMY.getPhone());
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withName(VALID_NAME_BOB).build();
        Person editedPerson = new PersonBuilder(AMY).withName(VALID_NAME_BOB).build();
        EditCommand command = new EditCommand(new PersonMatchesExactFieldsPredicate(searchDescriptor), descriptor);
        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(AMY, editedPerson);

        assertCommandSuccess(command, model,
                String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(editedPerson)), expectedModel);
    }

    // EP: search fields match different contacts individually but no contact satisfies all fields together.
    // Boundary value: 0 contacts satisfy the combined criteria, immediately below the required count of 1.
    @Test
    public void execute_searchFieldsMatchDifferentPersons_failure() {
        model.addPerson(BOB);
        ExactPersonMatchDescriptor searchDescriptor = new ExactPersonMatchDescriptor();
        searchDescriptor.setName(AMY.getName());
        searchDescriptor.setPhone(BOB.getPhone());
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withName(VALID_NAME_BOB).build();
        EditCommand command = new EditCommand(new PersonMatchesExactFieldsPredicate(searchDescriptor), descriptor);

        assertCommandFailure(command, model, EditCommand.MESSAGE_PERSON_NOT_FOUND);
    }

    // EPs for equality are labelled separately before each comparison below.
    @Test
    public void equals() {
        EditCommand standardCommand = new EditCommand(createPredicateFor(AMY), DESC_AMY);

        // EP: distinct commands with equal search criteria and updates -> true.
        EditPersonDescriptor copyDescriptor = new EditPersonDescriptor(DESC_AMY);
        EditCommand commandWithSameValues =
                new EditCommand(createPredicateFor(AMY), copyDescriptor);
        assertTrue(standardCommand.equals(commandWithSameValues));

        // EP: same command instance -> true.
        assertTrue(standardCommand.equals(standardCommand));

        // EP: comparison with null -> false.
        assertFalse(standardCommand.equals(null));

        // EP: comparison with an object of another type -> false.
        assertFalse(standardCommand.equals(new ClearCommand()));

        // EP: same updates but different search criteria -> false.
        assertFalse(standardCommand.equals(new EditCommand(createPredicateFor(BOB), DESC_AMY)));

        // EP: same search criteria but different updates -> false.
        assertFalse(standardCommand.equals(new EditCommand(createPredicateFor(AMY), DESC_BOB)));
    }

    // EP: command with populated search criteria and updates; text includes both stored values.
    @Test
    public void toStringMethod() {
        PersonMatchesExactFieldsPredicate predicate = createPredicateFor(AMY);
        EditCommand editCommand = new EditCommand(predicate, DESC_BOB);

        String expected = EditCommand.class.getCanonicalName()
                + "{predicate=" + predicate
                + ", editPersonDescriptor=" + DESC_BOB + "}";

        assertEquals(expected, editCommand.toString());
    }

    /**
     * Asserts that editing AMY produces the expected person and model.
     */
    private void assertEditSuccess(EditPersonDescriptor descriptor, Person editedPerson) {
        assertEditSuccess(AMY, descriptor, editedPerson);
    }

    /**
     * Asserts that editing the supplied contact produces the expected person and model.
     */
    private void assertEditSuccess(Person personToEdit, EditPersonDescriptor descriptor, Person editedPerson) {
        EditCommand editCommand = new EditCommand(createPredicateFor(personToEdit), descriptor);

        String expectedMessage = String.format(
                EditCommand.MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(editedPerson));

        Model expectedModel = new ModelManager(
                new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(personToEdit, editedPerson);

        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    /**
     * Returns exact search criteria identifying the person by phone.
     */
    private static PersonMatchesExactFieldsPredicate createPredicateFor(Person person) {
        ExactPersonMatchDescriptor descriptor = new ExactPersonMatchDescriptor();
        descriptor.setPhone(person.getPhone());
        return new PersonMatchesExactFieldsPredicate(descriptor);
    }
}

