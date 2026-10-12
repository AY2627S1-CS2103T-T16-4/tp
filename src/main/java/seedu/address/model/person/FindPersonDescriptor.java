package seedu.address.model.person;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import seedu.address.commons.util.CollectionUtil;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.tag.Tag;

/**
 * Stores the values to search for when finding persons. Each non-null field must match.
 * An empty tag set places no restriction on tags.
 */
public class FindPersonDescriptor {
    private Name name;
    private Phone phone;
    private Email email;
    private Address address;
    private Group group;
    private Github github;
    private Linkedin linkedin;
    private Telegram telegram;
    private final Set<Tag> tags = new HashSet<>();

    public FindPersonDescriptor() {}

    /**
     * Copy constructor.
     * A defensive copy of {@code tags} is used internally.
     */
    public FindPersonDescriptor(FindPersonDescriptor toCopy) {
        setName(toCopy.name);
        setPhone(toCopy.phone);
        setEmail(toCopy.email);
        setAddress(toCopy.address);
        setGroup(toCopy.group);
        setGithub(toCopy.github);
        setLinkedin(toCopy.linkedin);
        setTelegram(toCopy.telegram);
        setTags(toCopy.tags);
    }

    /**
     * Returns true if at least one field to search for is set.
     */
    public boolean isAnyFieldSet() {
        return CollectionUtil.isAnyNonNull(name, phone, email, address, group, github, linkedin, telegram)
                || !tags.isEmpty();
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

    public void setGroup(Group group) {
        this.group = group;
    }

    public Optional<Group> getGroup() {
        return Optional.ofNullable(group);
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
     * Replaces the tags to search for with {@code tags}.
     * A defensive copy of {@code tags} is used internally.
     */
    public void setTags(Set<Tag> tags) {
        this.tags.clear();
        this.tags.addAll(tags);
    }

    /**
     * Returns an unmodifiable tag set, which throws {@code UnsupportedOperationException}
     * if modification is attempted.
     */
    public Set<Tag> getTags() {
        return Collections.unmodifiableSet(tags);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof FindPersonDescriptor otherDescriptor)) {
            return false;
        }

        return getName().equals(otherDescriptor.getName())
                && getPhone().equals(otherDescriptor.getPhone())
                && getEmail().equals(otherDescriptor.getEmail())
                && getAddress().equals(otherDescriptor.getAddress())
                && getGroup().equals(otherDescriptor.getGroup())
                && getGithub().equals(otherDescriptor.getGithub())
                && getLinkedin().equals(otherDescriptor.getLinkedin())
                && getTelegram().equals(otherDescriptor.getTelegram())
                && tags.equals(otherDescriptor.tags);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("name", name)
                .add("phone", phone)
                .add("email", email)
                .add("address", address)
                .add("group", group)
                .add("github", github)
                .add("linkedin", linkedin)
                .add("telegram", telegram)
                .add("tags", tags)
                .toString();
    }
}
