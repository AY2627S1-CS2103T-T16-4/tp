package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import seedu.address.commons.util.CollectionUtil;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Address;
import seedu.address.model.person.Email;
import seedu.address.model.person.Github;
import seedu.address.model.person.Group;
import seedu.address.model.person.Linkedin;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.PersonMatchesExactFieldsPredicate;
import seedu.address.model.person.Phone;
import seedu.address.model.person.Telegram;
import seedu.address.model.tag.Tag;

/**
 * Edits the unique contact matching the supplied exact field criteria.
 */
public class EditCommand extends Command {

    public static final String COMMAND_WORD = "edit";

    public static final String EDIT_SEPARATOR = ">>";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Edits exactly one contact matching all supplied search values.\n"
            + "Format: edit SEARCH_FLAGS "
            + EDIT_SEPARATOR
            + " UPDATE_FLAGS\n"
            + PersonTarget.MESSAGE_PARAMETERS
            + "Use the supported prefixes on either side of "
            + EDIT_SEPARATOR
            + ".\n"
            + "Provide at least one search flag and one update flag.\n"
            + "Search flags require nonempty values.\n"
            + "Name and phone cannot be cleared.\n"
            + "An empty update value clears an optional field.\n"
            + "Tag and group values toggle membership; t/ and g/ clear all.\n"
            + "Example: edit n/charlotte "
            + EDIT_SEPARATOR
            + " n/charlotte tan t/tag1 t/tag2 g/grp1 g/grp2";

    public static final String MESSAGE_PERSON_NOT_FOUND =
            PersonTarget.MESSAGE_PERSON_NOT_FOUND;

    public static final String MESSAGE_AMBIGUOUS_PERSON =
            String.format(PersonTarget.MESSAGE_AMBIGUOUS_PERSON, "edited");

    public static final String MESSAGE_EMPTY_FLAG_VALUE =
            "Please provide a value for %1$s to identify the contact to edit.";

    public static final String MESSAGE_CANNOT_CLEAR_REQUIRED_FIELD =
            "Please provide a value for %1$s. Name and phone cannot be cleared.";

    public static final String MESSAGE_EDIT_PERSON_SUCCESS =
            "Edited person: %1$s";

    public static final String MESSAGE_NOT_EDITED =
            "At least one field to edit must be provided after >>.";

    public static final String MESSAGE_DUPLICATE_PERSON =
            "This person already exists in the address book. No duplicate phone numbers!";

    public static final String MESSAGE_MIXED_CLEAR_AND_TOGGLE =
            "Use %1$s alone to clear the field or provide only nonempty values to toggle.";

    private final PersonTarget target;
    private final EditPersonDescriptor editPersonDescriptor;

    /**
     * Creates a command that edits the unique contact satisfying {@code predicate}.
     *
     * @param predicate Exact field criteria used to identify the contact to edit.
     * @param editPersonDescriptor Updates to apply to the matching contact.
     */
    public EditCommand(
            PersonMatchesExactFieldsPredicate predicate,
            EditPersonDescriptor editPersonDescriptor) {
        this.target = new PersonTarget(predicate);
        this.editPersonDescriptor = new EditPersonDescriptor(requireNonNull(editPersonDescriptor));
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        Person personToEdit = target.resolve(model, MESSAGE_PERSON_NOT_FOUND, MESSAGE_AMBIGUOUS_PERSON);

        Person editedPerson = createEditedPerson(personToEdit, editPersonDescriptor);

        if (!personToEdit.isSamePerson(editedPerson) && model.hasPerson(editedPerson)) {
            throw new CommandException(MESSAGE_DUPLICATE_PERSON);
        }

        model.setPerson(personToEdit, editedPerson);

        return new CommandResult(
                String.format(MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(editedPerson)));
    }

    /**
     * Creates an updated contact, preserving unspecified fields and pin state.
     *
     * @param personToEdit Contact whose current values provide the defaults.
     * @param descriptor Field replacements and membership updates to apply.
     */
    private static Person createEditedPerson(
            Person personToEdit, EditPersonDescriptor descriptor) {
        requireNonNull(personToEdit);

        Name updatedName = descriptor.getName().orElse(personToEdit.getName());
        Phone updatedPhone = descriptor.getPhone().orElse(personToEdit.getPhone());
        Email updatedEmail = descriptor.getEmail().orElse(personToEdit.getEmail());
        Address updatedAddress = descriptor.getAddress().orElse(personToEdit.getAddress());
        Github updatedGithub = descriptor.getGithub().orElse(personToEdit.getGithub());
        Linkedin updatedLinkedin = descriptor.getLinkedin().orElse(personToEdit.getLinkedin());
        Telegram updatedTelegram = descriptor.getTelegram().orElse(personToEdit.getTelegram());

        Set<Group> updatedGroups = applySetEdit(personToEdit.getGroups(), descriptor.getGroups());
        Set<Tag> updatedTags = applySetEdit(personToEdit.getTags(), descriptor.getTags());

        return new Person(
                updatedName,
                updatedPhone,
                updatedEmail,
                updatedAddress,
                updatedGroups,
                updatedGithub,
                updatedLinkedin,
                updatedTelegram,
                updatedTags,
                personToEdit.isPinned());
    }

    /**
     * Returns a copy of the current set with the requested membership updates applied.
     * An absent update preserves the set; a present but empty set clears it.
     * Otherwise, each supplied value toggles membership once.
     *
     * @param <T> Type of element in the set.
     * @param current Existing set whose contents are copied before editing.
     * @param update Optional set of values to toggle, or an empty set to clear all values.
     */
    private static <T> Set<T> applySetEdit(
            Set<T> current, Optional<Set<T>> update) {
        Set<T> result = new HashSet<>(current);

        if (update.isEmpty()) {
            return result;
        }

        Set<T> values = update.get();
        if (values.isEmpty()) {
            result.clear();
            return result;
        }

        for (T value : values) {
            if (!result.remove(value)) {
                result.add(value);
            }
        }

        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof EditCommand otherEditCommand)) {
            return false;
        }

        return target.equals(otherEditCommand.target)
                && editPersonDescriptor.equals(otherEditCommand.editPersonDescriptor);
    }

    @Override
    public boolean isUndoable() {
        return true;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("predicate", target.predicate())
                .add("editPersonDescriptor", editPersonDescriptor)
                .toString();
    }

    /**
     * Stores requested updates.
     * Null fields are unchanged.
     * Supplied scalar values replace existing values.
     * Empty sets clear tags/groups; nonempty sets toggle their members.
     */
    public static class EditPersonDescriptor {
        private Name name;
        private Phone phone;
        private Email email;
        private Address address;
        private Github github;
        private Linkedin linkedin;
        private Telegram telegram;
        private Set<Group> groups;
        private Set<Tag> tags;

        /**
         * Creates a descriptor with no field updates.
         */
        public EditPersonDescriptor() {}

        /**
         * Creates a copy of the given descriptor, defensively copying collection fields.
         *
         * @param toCopy Descriptor whose field updates are copied.
         */
        public EditPersonDescriptor(EditPersonDescriptor toCopy) {
            requireNonNull(toCopy);
            setName(toCopy.name);
            setPhone(toCopy.phone);
            setEmail(toCopy.email);
            setAddress(toCopy.address);
            setGithub(toCopy.github);
            setLinkedin(toCopy.linkedin);
            setTelegram(toCopy.telegram);
            setGroups(toCopy.groups);
            setTags(toCopy.tags);
        }

        /**
         * Returns true if at least one field update is supplied.
         */
        public boolean isAnyFieldEdited() {
            return CollectionUtil.isAnyNonNull(
                    name, phone, email, address, github, linkedin, telegram, groups, tags);
        }

        public void setName(Name name) {
            this.name = name;
        }

        public Optional<Name> getName() {
            return Optional.ofNullable(name);
        }

        public void setPhone(Phone phone) {
            this.phone = phone;
        }

        public Optional<Phone> getPhone() {
            return Optional.ofNullable(phone);
        }

        public void setEmail(Email email) {
            this.email = email;
        }

        public Optional<Email> getEmail() {
            return Optional.ofNullable(email);
        }

        public void setAddress(Address address) {
            this.address = address;
        }

        public Optional<Address> getAddress() {
            return Optional.ofNullable(address);
        }

        public void setGithub(Github github) {
            this.github = github;
        }

        public Optional<Github> getGithub() {
            return Optional.ofNullable(github);
        }

        public void setLinkedin(Linkedin linkedin) {
            this.linkedin = linkedin;
        }

        public Optional<Linkedin> getLinkedin() {
            return Optional.ofNullable(linkedin);
        }

        public void setTelegram(Telegram telegram) {
            this.telegram = telegram;
        }

        public Optional<Telegram> getTelegram() {
            return Optional.ofNullable(telegram);
        }

        /**
         * Stores a defensive copy of the requested group update.
         * A {@code null} value leaves groups unchanged; an empty set clears all groups.
         * A nonempty set toggles membership for each supplied group.
         */
        public void setGroups(Set<Group> groups) {
            this.groups = groups == null ? null : new HashSet<>(groups);
        }

        /**
         * Returns an optional containing an unmodifiable group update set, or an empty optional if unchanged.
         * A present but empty set requests clearing all groups.
         */
        public Optional<Set<Group>> getGroups() {
            return groups == null
                    ? Optional.empty()
                    : Optional.of(Collections.unmodifiableSet(groups));
        }

        /**
         * Stores a defensive copy of the requested tag update.
         * A {@code null} value leaves tags unchanged; an empty set clears all tags.
         * A nonempty set toggles membership for each supplied tag.
         */
        public void setTags(Set<Tag> tags) {
            this.tags = tags == null ? null : new HashSet<>(tags);
        }

        /**
         * Returns an optional containing an unmodifiable tag update set, or an empty optional if unchanged.
         * A present but empty set requests clearing all tags.
         */
        public Optional<Set<Tag>> getTags() {
            return tags == null
                    ? Optional.empty()
                    : Optional.of(Collections.unmodifiableSet(tags));
        }

        @Override
        public boolean equals(Object other) {
            if (other == this) {
                return true;
            }

            if (!(other instanceof EditPersonDescriptor otherDescriptor)) {
                return false;
            }

            return Objects.equals(name, otherDescriptor.name)
                    && Objects.equals(phone, otherDescriptor.phone)
                    && Objects.equals(email, otherDescriptor.email)
                    && Objects.equals(address, otherDescriptor.address)
                    && Objects.equals(github, otherDescriptor.github)
                    && Objects.equals(linkedin, otherDescriptor.linkedin)
                    && Objects.equals(telegram, otherDescriptor.telegram)
                    && Objects.equals(groups, otherDescriptor.groups)
                    && Objects.equals(tags, otherDescriptor.tags);
        }

        @Override
        public int hashCode() {
            return Objects.hash(
                    name, phone, email, address, github, linkedin, telegram, groups, tags);
        }

        @Override
        public String toString() {
            return new ToStringBuilder(this)
                    .add("name", name)
                    .add("phone", phone)
                    .add("email", email)
                    .add("address", address)
                    .add("github", github)
                    .add("linkedin", linkedin)
                    .add("telegram", telegram)
                    .add("groups", groups)
                    .add("tags", tags)
                    .toString();
        }
    }
}
