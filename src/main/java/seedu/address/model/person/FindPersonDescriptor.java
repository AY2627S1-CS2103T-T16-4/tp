package seedu.address.model.person;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import seedu.address.commons.util.CollectionUtil;
import seedu.address.commons.util.ToStringBuilder;

/**
 * Stores the text to search for when finding persons. Each non-null field must be a prefix of the
 * corresponding field of a matching person. The values are raw search text, so that they can match the start
 * of a field (partial matching). Unlike the person fields, they are therefore not validated against the format of
 * the field they are searched in.
 * An empty group or tag set places no restriction on groups or tags respectively.
 */
public class FindPersonDescriptor {
    private String name;
    private String phone;
    private String email;
    private String address;
    private final Set<String> groups = new HashSet<>();
    private String github;
    private String linkedin;
    private String telegram;
    private final Set<String> tags = new HashSet<>();

    public FindPersonDescriptor() {}

    /**
     * Copy constructor.
     * A defensive copy of {@code groups} and {@code tags} is used internally.
     */
    public FindPersonDescriptor(FindPersonDescriptor toCopy) {
        setName(toCopy.name);
        setPhone(toCopy.phone);
        setEmail(toCopy.email);
        setAddress(toCopy.address);
        setGroups(toCopy.groups);
        setGithub(toCopy.github);
        setLinkedin(toCopy.linkedin);
        setTelegram(toCopy.telegram);
        setTags(toCopy.tags);
    }

    /**
     * Returns true if at least one field to search for is set.
     */
    public boolean isAnyFieldSet() {
        return CollectionUtil.isAnyNonNull(name, phone, email, address, github, linkedin, telegram)
                || !tags.isEmpty() || !groups.isEmpty();
    }

    public void setName(String name) {
        this.name = name;
    }

    public Optional<String> getName() {
        return Optional.ofNullable(name);
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Optional<String> getPhone() {
        return Optional.ofNullable(phone);
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Optional<String> getEmail() {
        return Optional.ofNullable(email);
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Optional<String> getAddress() {
        return Optional.ofNullable(address);
    }

    /**
     * Replaces the groups to search for with {@code groups}.
     * A defensive copy of {@code groups} is used internally.
     */
    public void setGroups(Set<String> groups) {
        this.groups.clear();
        this.groups.addAll(groups);
    }

    /**
     * Returns an unmodifiable group set, which throws {@code UnsupportedOperationException}
     * if modification is attempted.
     */
    public Set<String> getGroups() {
        return Collections.unmodifiableSet(groups);
    }

    public void setGithub(String github) {
        this.github = github;
    }

    public Optional<String> getGithub() {
        return Optional.ofNullable(github);
    }

    public void setLinkedin(String linkedin) {
        this.linkedin = linkedin;
    }

    public Optional<String> getLinkedin() {
        return Optional.ofNullable(linkedin);
    }

    public void setTelegram(String telegram) {
        this.telegram = telegram;
    }

    public Optional<String> getTelegram() {
        return Optional.ofNullable(telegram);
    }

    /**
     * Replaces the tags to search for with {@code tags}.
     * A defensive copy of {@code tags} is used internally.
     */
    public void setTags(Set<String> tags) {
        this.tags.clear();
        this.tags.addAll(tags);
    }

    /**
     * Returns an unmodifiable tag set, which throws {@code UnsupportedOperationException}
     * if modification is attempted.
     */
    public Set<String> getTags() {
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
                && getGroups().equals(otherDescriptor.getGroups())
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
                .add("groups", groups)
                .add("github", github)
                .add("linkedin", linkedin)
                .add("telegram", telegram)
                .add("tags", tags)
                .toString();
    }
}
