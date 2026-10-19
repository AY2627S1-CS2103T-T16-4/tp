package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.model.tag.Tag;

public class FindPersonDescriptorTest {

    @Test
    public void isAnyFieldSet() {
        // no fields set -> returns false
        assertFalse(new FindPersonDescriptor().isAnyFieldSet());

        // each field on its own -> returns true
        FindPersonDescriptor descriptor = new FindPersonDescriptor();
        descriptor.setName(new Name("Alice"));
        assertTrue(descriptor.isAnyFieldSet());

        descriptor = new FindPersonDescriptor();
        descriptor.setPhone(new Phone("91234567"));
        assertTrue(descriptor.isAnyFieldSet());

        descriptor = new FindPersonDescriptor();
        descriptor.setEmail(new Email("alice@example.com"));
        assertTrue(descriptor.isAnyFieldSet());

        descriptor = new FindPersonDescriptor();
        descriptor.setAddress(new Address("Clementi"));
        assertTrue(descriptor.isAnyFieldSet());

        descriptor = new FindPersonDescriptor();
        descriptor.setGroups(Set.of(new Group("T09")));
        assertTrue(descriptor.isAnyFieldSet());

        descriptor = new FindPersonDescriptor();
        descriptor.setGithub(new Github("alice"));
        assertTrue(descriptor.isAnyFieldSet());

        descriptor = new FindPersonDescriptor();
        descriptor.setLinkedin(new Linkedin("alice"));
        assertTrue(descriptor.isAnyFieldSet());

        descriptor = new FindPersonDescriptor();
        descriptor.setTelegram(new Telegram("alice"));
        assertTrue(descriptor.isAnyFieldSet());

        descriptor = new FindPersonDescriptor();
        descriptor.setTags(Set.of(new Tag("friend")));
        assertTrue(descriptor.isAnyFieldSet());
    }

    @Test
    public void getters_unsetFieldsAreEmpty() {
        FindPersonDescriptor descriptor = new FindPersonDescriptor();
        assertEquals(Optional.empty(), descriptor.getName());
        assertEquals(Optional.empty(), descriptor.getPhone());
        assertEquals(Optional.empty(), descriptor.getEmail());
        assertEquals(Optional.empty(), descriptor.getAddress());
        assertTrue(descriptor.getGroups().isEmpty());
        assertEquals(Optional.empty(), descriptor.getGithub());
        assertEquals(Optional.empty(), descriptor.getLinkedin());
        assertEquals(Optional.empty(), descriptor.getTelegram());
        assertTrue(descriptor.getTags().isEmpty());
    }

    @Test
    public void getTags_modifyTags_throwsUnsupportedOperationException() {
        FindPersonDescriptor descriptor = new FindPersonDescriptor();
        assertThrows(UnsupportedOperationException.class, () -> descriptor.getTags().add(new Tag("friend")));
    }

    @Test
    public void setTags_defensiveCopy() {
        FindPersonDescriptor descriptor = new FindPersonDescriptor();
        HashSet<Tag> tags = new HashSet<>(Set.of(new Tag("friend")));
        descriptor.setTags(tags);
        tags.add(new Tag("family"));
        assertEquals(Set.of(new Tag("friend")), descriptor.getTags());
    }

    @Test
    public void equals() {
        FindPersonDescriptor descriptor = new FindPersonDescriptor();
        descriptor.setName(new Name("Alice"));
        descriptor.setGithub(new Github("alice"));
        descriptor.setTags(Set.of(new Tag("friend")));

        // same values -> returns true (copy constructor)
        assertEquals(descriptor, new FindPersonDescriptor(descriptor));

        // same object -> returns true
        assertEquals(descriptor, descriptor);

        // null -> returns false
        assertNotEquals(null, descriptor);

        // different types -> returns false
        assertNotEquals(1, descriptor);

        // different name -> returns false
        FindPersonDescriptor other = new FindPersonDescriptor(descriptor);
        other.setName(new Name("Bob"));
        assertNotEquals(descriptor, other);

        // different phone -> returns false
        other = new FindPersonDescriptor(descriptor);
        other.setPhone(new Phone("91234567"));
        assertNotEquals(descriptor, other);

        // different email -> returns false
        other = new FindPersonDescriptor(descriptor);
        other.setEmail(new Email("alice@example.com"));
        assertNotEquals(descriptor, other);

        // different address -> returns false
        other = new FindPersonDescriptor(descriptor);
        other.setAddress(new Address("Clementi"));
        assertNotEquals(descriptor, other);

        // different group -> returns false
        other = new FindPersonDescriptor(descriptor);
        other.setGroups(Set.of(new Group("T09")));
        assertNotEquals(descriptor, other);

        // different github -> returns false
        other = new FindPersonDescriptor(descriptor);
        other.setGithub(new Github("bob"));
        assertNotEquals(descriptor, other);

        // different linkedin -> returns false
        other = new FindPersonDescriptor(descriptor);
        other.setLinkedin(new Linkedin("alice"));
        assertNotEquals(descriptor, other);

        // different telegram -> returns false
        other = new FindPersonDescriptor(descriptor);
        other.setTelegram(new Telegram("alice"));
        assertNotEquals(descriptor, other);

        // different tags -> returns false
        other = new FindPersonDescriptor(descriptor);
        other.setTags(Set.of(new Tag("family")));
        assertNotEquals(descriptor, other);
    }

    @Test
    public void toStringMethod() {
        FindPersonDescriptor descriptor = new FindPersonDescriptor();
        String expected = FindPersonDescriptor.class.getCanonicalName() + "{name=" + null
                + ", phone=" + null
                + ", email=" + null
                + ", address=" + null
                + ", groups=" + descriptor.getGroups()
                + ", github=" + null
                + ", linkedin=" + null
                + ", telegram=" + null
                + ", tags=" + descriptor.getTags() + "}";
        assertEquals(expected, descriptor.toString());
    }
}
