package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.DESC_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_ADDRESS_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_EMAIL_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_GITHUB_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_GROUP_AMY;
import static seedu.address.logic.commands.CommandTestUtil.VALID_GROUP_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_LINKEDIN_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_NAME_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_PHONE_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TAG_FRIEND;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TAG_HUSBAND;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TELEGRAM_BOB;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.AMY;
import static seedu.address.testutil.TypicalPersons.BOB;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.EditCommand.EditPersonDescriptor;
import seedu.address.model.person.Address;
import seedu.address.model.person.Email;
import seedu.address.model.person.Github;
import seedu.address.model.person.Group;
import seedu.address.model.person.Linkedin;
import seedu.address.model.person.Telegram;
import seedu.address.model.tag.Tag;
import seedu.address.testutil.EditPersonDescriptorBuilder;

public class EditPersonDescriptorTest {

    private static final String EMPTY_FIELD_VALUE = "";

    @Test
    public void constructor_noUpdates_allFieldsAbsent() {
        EditPersonDescriptor descriptor = new EditPersonDescriptor();

        assertFalse(descriptor.isAnyFieldEdited());
        assertTrue(descriptor.getName().isEmpty());
        assertTrue(descriptor.getPhone().isEmpty());
        assertTrue(descriptor.getEmail().isEmpty());
        assertTrue(descriptor.getAddress().isEmpty());
        assertTrue(descriptor.getGithub().isEmpty());
        assertTrue(descriptor.getLinkedin().isEmpty());
        assertTrue(descriptor.getTelegram().isEmpty());
        assertTrue(descriptor.getGroups().isEmpty());
        assertTrue(descriptor.getTags().isEmpty());
    }

    @Test
    public void setters_singleFieldEdited_detectsUpdateAndAllowsReset() {
        assertFieldUpdate(EditPersonDescriptor::setName, EditPersonDescriptor::getName, AMY.getName());
        assertFieldUpdate(EditPersonDescriptor::setPhone, EditPersonDescriptor::getPhone, AMY.getPhone());
        assertFieldUpdate(EditPersonDescriptor::setEmail, EditPersonDescriptor::getEmail, AMY.getEmail());
        assertFieldUpdate(EditPersonDescriptor::setAddress, EditPersonDescriptor::getAddress, AMY.getAddress());
        assertFieldUpdate(EditPersonDescriptor::setGithub, EditPersonDescriptor::getGithub, AMY.getGithub());
        assertFieldUpdate(EditPersonDescriptor::setLinkedin, EditPersonDescriptor::getLinkedin, AMY.getLinkedin());
        assertFieldUpdate(EditPersonDescriptor::setTelegram, EditPersonDescriptor::getTelegram, AMY.getTelegram());
        assertFieldUpdate(EditPersonDescriptor::setGroups, EditPersonDescriptor::getGroups, AMY.getGroups());
        assertFieldUpdate(EditPersonDescriptor::setTags, EditPersonDescriptor::getTags, AMY.getTags());
    }

    @Test
    public void setters_explicitClear_detectsUpdateAndDistinguishesAbsentField() {
        assertClearUpdate(EditPersonDescriptor::setEmail, EditPersonDescriptor::getEmail, new Email(EMPTY_FIELD_VALUE));
        assertClearUpdate(EditPersonDescriptor::setAddress, EditPersonDescriptor::getAddress,
                new Address(EMPTY_FIELD_VALUE));
        assertClearUpdate(EditPersonDescriptor::setGithub, EditPersonDescriptor::getGithub,
                new Github(EMPTY_FIELD_VALUE));
        assertClearUpdate(EditPersonDescriptor::setLinkedin, EditPersonDescriptor::getLinkedin,
                new Linkedin(EMPTY_FIELD_VALUE));
        assertClearUpdate(EditPersonDescriptor::setTelegram, EditPersonDescriptor::getTelegram,
                new Telegram(EMPTY_FIELD_VALUE));
        assertClearUpdate(EditPersonDescriptor::setGroups, EditPersonDescriptor::getGroups, Set.<Group>of());
        assertClearUpdate(EditPersonDescriptor::setTags, EditPersonDescriptor::getTags, Set.<Tag>of());
    }

    @Test
    public void setTags_mutableInput_defensivelyCopied() {
        Set<Tag> suppliedTags = new HashSet<>(AMY.getTags());
        EditPersonDescriptor descriptor = new EditPersonDescriptor();
        descriptor.setTags(suppliedTags);

        suppliedTags.clear();
        assertEquals(Optional.of(AMY.getTags()), descriptor.getTags());
        suppliedTags.add(new Tag(VALID_TAG_HUSBAND));
        assertEquals(Optional.of(AMY.getTags()), descriptor.getTags());
    }

    @Test
    public void setGroups_mutableInput_defensivelyCopied() {
        Set<Group> suppliedGroups = new HashSet<>(AMY.getGroups());
        EditPersonDescriptor descriptor = new EditPersonDescriptor();
        descriptor.setGroups(suppliedGroups);

        suppliedGroups.clear();
        assertEquals(Optional.of(AMY.getGroups()), descriptor.getGroups());
        suppliedGroups.add(new Group(VALID_GROUP_BOB));
        assertEquals(Optional.of(AMY.getGroups()), descriptor.getGroups());
    }

    @Test
    public void getTags_nonemptySet_unmodifiable() {
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder(AMY).build();
        Set<Tag> tags = descriptor.getTags().orElseThrow();

        assertThrows(UnsupportedOperationException.class, () -> tags.add(new Tag(VALID_TAG_HUSBAND)));
        assertThrows(UnsupportedOperationException.class, () -> tags.removeAll(AMY.getTags()));
        assertEquals(Optional.of(AMY.getTags()), descriptor.getTags());
    }

    @Test
    public void getGroups_nonemptySet_unmodifiable() {
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder(AMY).build();
        Set<Group> groups = descriptor.getGroups().orElseThrow();

        assertThrows(UnsupportedOperationException.class, () -> groups.add(new Group(VALID_GROUP_BOB)));
        assertThrows(UnsupportedOperationException.class, () -> groups.removeAll(AMY.getGroups()));
        assertEquals(Optional.of(AMY.getGroups()), descriptor.getGroups());
    }

    @Test
    public void copyConstructor_differentUpdateStates_preservesAllFields() {
        for (EditPersonDescriptor original : descriptorStates()) {
            EditPersonDescriptor copy = new EditPersonDescriptor(original);

            assertDescriptorFieldsEqual(original, copy);
            assertEquals(original.isAnyFieldEdited(), copy.isAnyFieldEdited());
            assertEquals(original, copy);
            assertEquals(copy, original);
            assertEquals(original.hashCode(), copy.hashCode());
        }
    }

    @Test
    public void copyConstructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new EditPersonDescriptor(null));
    }

    @Test
    public void copyConstructor_subsequentChanges_areIndependent() {
        for (Consumer<EditPersonDescriptor> update : bobFieldUpdates()) {
            EditPersonDescriptor original = new EditPersonDescriptorBuilder(AMY).build();
            EditPersonDescriptor copy = new EditPersonDescriptor(original);
            EditPersonDescriptor expected = new EditPersonDescriptorBuilder(AMY).build();

            update.accept(original);
            assertDescriptorFieldsEqual(expected, copy);

            // Use a fresh pair to check mutation in the opposite direction.
            original = new EditPersonDescriptorBuilder(AMY).build();
            copy = new EditPersonDescriptor(original);
            update.accept(copy);
            assertDescriptorFieldsEqual(expected, original);
        }
    }

    @Test
    public void equals_differentPopulatedField_returnsFalse() {
        for (Consumer<EditPersonDescriptor> update : bobFieldUpdates()) {
            EditPersonDescriptor original = new EditPersonDescriptorBuilder(AMY).build();
            EditPersonDescriptor changed = new EditPersonDescriptorBuilder(AMY).build();
            update.accept(changed);

            assertNotEquals(original, changed);
            assertNotEquals(changed, original);
        }
    }

    @Test
    public void equalsAndHashCode_independentlyConstructedDescriptors_match() {
        assertEquals(new EditPersonDescriptor(), new EditPersonDescriptor());
        assertEquals(new EditPersonDescriptor().hashCode(), new EditPersonDescriptor().hashCode());
        EditPersonDescriptor first = new EditPersonDescriptorBuilder(AMY).build();
        EditPersonDescriptor second = new EditPersonDescriptorBuilder(AMY).build();

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }

    @Test
    public void equalsAndHashCode_differentSetInsertionOrders_match() {
        Tag friend = new Tag(VALID_TAG_FRIEND);
        Tag husband = new Tag(VALID_TAG_HUSBAND);
        Group amyGroup = new Group(VALID_GROUP_AMY);
        Group bobGroup = new Group(VALID_GROUP_BOB);
        EditPersonDescriptor first = new EditPersonDescriptor();
        first.setTags(new LinkedHashSet<>(List.of(friend, husband)));
        first.setGroups(new LinkedHashSet<>(List.of(amyGroup, bobGroup)));
        EditPersonDescriptor second = new EditPersonDescriptor();
        second.setTags(new LinkedHashSet<>(List.of(husband, friend)));
        second.setGroups(new LinkedHashSet<>(List.of(bobGroup, amyGroup)));

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }

    @Test
    public void equals() {
        // same values -> returns true
        EditPersonDescriptor descriptorWithSameValues = new EditPersonDescriptor(DESC_AMY);
        assertTrue(DESC_AMY.equals(descriptorWithSameValues));

        // same object -> returns true
        assertTrue(DESC_AMY.equals(DESC_AMY));

        // null -> returns false
        assertFalse(DESC_AMY.equals(null));

        // different types -> returns false
        assertFalse(DESC_AMY.equals(5));

        // different values -> returns false
        assertFalse(DESC_AMY.equals(DESC_BOB));

        // different name -> returns false
        EditPersonDescriptor editedAmy = new EditPersonDescriptorBuilder(DESC_AMY).withName(VALID_NAME_BOB).build();
        assertFalse(DESC_AMY.equals(editedAmy));

        // different phone -> returns false
        editedAmy = new EditPersonDescriptorBuilder(DESC_AMY).withPhone(VALID_PHONE_BOB).build();
        assertFalse(DESC_AMY.equals(editedAmy));

        // different email -> returns false
        editedAmy = new EditPersonDescriptorBuilder(DESC_AMY).withEmail(VALID_EMAIL_BOB).build();
        assertFalse(DESC_AMY.equals(editedAmy));

        // different address -> returns false
        editedAmy = new EditPersonDescriptorBuilder(DESC_AMY).withAddress(VALID_ADDRESS_BOB).build();
        assertFalse(DESC_AMY.equals(editedAmy));

        // different github -> returns false
        editedAmy = new EditPersonDescriptorBuilder(DESC_AMY).withGithub(VALID_GITHUB_BOB).build();
        assertFalse(DESC_AMY.equals(editedAmy));

        // different linkedin -> returns false
        editedAmy = new EditPersonDescriptorBuilder(DESC_AMY).withLinkedin(VALID_LINKEDIN_BOB).build();
        assertFalse(DESC_AMY.equals(editedAmy));

        // different telegram -> returns false
        editedAmy = new EditPersonDescriptorBuilder(DESC_AMY).withTelegram(VALID_TELEGRAM_BOB).build();
        assertFalse(DESC_AMY.equals(editedAmy));

        // different group -> returns false
        editedAmy = new EditPersonDescriptorBuilder(DESC_AMY).withGroups(VALID_GROUP_BOB).build();
        assertFalse(DESC_AMY.equals(editedAmy));

        // different tags -> returns false
        editedAmy = new EditPersonDescriptorBuilder(DESC_AMY).withTags(VALID_TAG_HUSBAND).build();
        assertFalse(DESC_AMY.equals(editedAmy));
    }

    @Test
    public void toStringMethod() {
        EditPersonDescriptor descriptor = DESC_AMY;
        String expected = EditPersonDescriptor.class.getCanonicalName() + "{name="
                + descriptor.getName().orElse(null) + ", phone="
                + descriptor.getPhone().orElse(null) + ", email="
                + descriptor.getEmail().orElse(null) + ", address="
                + descriptor.getAddress().orElse(null) + ", github="
                + descriptor.getGithub().orElse(null) + ", linkedin="
                + descriptor.getLinkedin().orElse(null) + ", telegram="
                + descriptor.getTelegram().orElse(null) + ", groups="
                + descriptor.getGroups().orElse(null) + ", tags="
                + descriptor.getTags().orElse(null) + "}";
        assertEquals(expected, descriptor.toString());
    }

    /**
     * Checks a single field's supplied and reset states without other updates masking edit detection.
     */
    private static <T> void assertFieldUpdate(BiConsumer<EditPersonDescriptor, T> setter,
            Function<EditPersonDescriptor, Optional<T>> getter, T value) {
        EditPersonDescriptor descriptor = new EditPersonDescriptor();
        setter.accept(descriptor, value);
        assertEquals(Optional.of(value), getter.apply(descriptor));
        assertTrue(descriptor.isAnyFieldEdited());

        setter.accept(descriptor, null);
        assertTrue(getter.apply(descriptor).isEmpty());
        assertFalse(descriptor.isAnyFieldEdited());
    }

    /**
     * Checks that an explicit clearing request remains distinct from an omitted update.
     */
    private static <T> void assertClearUpdate(BiConsumer<EditPersonDescriptor, T> setter,
            Function<EditPersonDescriptor, Optional<T>> getter, T emptyValue) {
        EditPersonDescriptor descriptor = new EditPersonDescriptor();
        EditPersonDescriptor identicalClear = new EditPersonDescriptor();
        setter.accept(descriptor, emptyValue);
        setter.accept(identicalClear, emptyValue);

        assertEquals(Optional.of(emptyValue), getter.apply(descriptor));
        assertTrue(descriptor.isAnyFieldEdited());
        assertNotEquals(new EditPersonDescriptor(), descriptor);
        assertNotEquals(descriptor, new EditPersonDescriptor());
        assertEquals(descriptor, identicalClear);
        assertEquals(descriptor.hashCode(), identicalClear.hashCode());
    }

    /**
     * Compares each getter independently so a defect in equals cannot mask a copying defect.
     */
    private static void assertDescriptorFieldsEqual(EditPersonDescriptor expected, EditPersonDescriptor actual) {
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getPhone(), actual.getPhone());
        assertEquals(expected.getEmail(), actual.getEmail());
        assertEquals(expected.getAddress(), actual.getAddress());
        assertEquals(expected.getGithub(), actual.getGithub());
        assertEquals(expected.getLinkedin(), actual.getLinkedin());
        assertEquals(expected.getTelegram(), actual.getTelegram());
        assertEquals(expected.getGroups(), actual.getGroups());
        assertEquals(expected.getTags(), actual.getTags());
    }

    /**
     * Returns fresh descriptors covering absent, partial, populated, and explicit-clear updates.
     */
    private static List<EditPersonDescriptor> descriptorStates() {
        return List.of(new EditPersonDescriptor(), new EditPersonDescriptor(DESC_AMY),
                new EditPersonDescriptorBuilder(AMY).build(),
                new EditPersonDescriptorBuilder().withEmail(EMPTY_FIELD_VALUE).withAddress(EMPTY_FIELD_VALUE)
                        .withGithub(EMPTY_FIELD_VALUE).withLinkedin(EMPTY_FIELD_VALUE).withTelegram(EMPTY_FIELD_VALUE)
                        .withGroups().withTags().build());
    }

    /**
     * Returns independent updates that each change exactly one populated AMY field.
     */
    private static List<Consumer<EditPersonDescriptor>> bobFieldUpdates() {
        return List.of(descriptor -> descriptor.setName(BOB.getName()),
                descriptor -> descriptor.setPhone(BOB.getPhone()),
                descriptor -> descriptor.setEmail(BOB.getEmail()),
                descriptor -> descriptor.setAddress(BOB.getAddress()),
                descriptor -> descriptor.setGithub(BOB.getGithub()),
                descriptor -> descriptor.setLinkedin(BOB.getLinkedin()),
                descriptor -> descriptor.setTelegram(BOB.getTelegram()),
                descriptor -> descriptor.setGroups(BOB.getGroups()),
                descriptor -> descriptor.setTags(BOB.getTags()));
    }
}
