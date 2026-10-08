package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import seedu.address.testutil.PersonBuilder;

public class RemarkTest {
    @Test
    public void constructor_acceptsEmptyAndFreeTextButRejectsNull() {
        assertEquals("", new Remark("").value);
        assertEquals(" Notes / ! ", new Remark(" Notes / ! ").value);
        assertThrows(NullPointerException.class, () -> new Remark(null));
    }

    @Test
    public void equals_comparesRemarksOnly() {
        Remark remark = new Remark("Note");
        assertEquals(remark, new Remark("Note"));
        assertEquals(remark.hashCode(), new Remark("Note").hashCode());
        assertNotEquals(remark, new Remark("Other"));
        assertNotEquals(remark, new Address("Note"));
        assertNotEquals(remark, null);
    }

    @Test
    public void person_remarkAffectsEqualityButNotIdentity() {
        Person original = new PersonBuilder().build();
        Person updated = new PersonBuilder(original).withRemark("Note").build();
        assertNotEquals(original, updated);
        assertTrue(original.isSamePerson(updated));
    }
}
