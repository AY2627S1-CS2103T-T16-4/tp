package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class AddressTest {

    @Test
    public void fieldValues_validAndInvalidInputs() {
        assertTrue(Address.isValidAddress(""));
        assertTrue(Address.isValidAddress("123 Main Street"));
        assertTrue(Address.isValidAddress("Block 12, #03-45 (North)"));
        assertFalse(Address.isValidAddress(" "));
        assertFalse(Address.isValidAddress(" 123 Street"));
        assertFalse(Address.isValidAddress("123/45 Street"));
        assertFalse(Address.isValidAddress("Street; Block"));
        assertFalse(Address.isValidAddress("Street_1"));
        assertFalse(Address.isValidAddress("123\tStreet"));
        assertFalse(Address.isValidAddress("123\nStreet"));
        assertFalse(Address.isValidAddress("街道"));
    }

    @Test
    public void constructor_invalidFieldValue_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> new Address(" 123 Street"));
    }

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Address(null));
    }

    @Test
    public void isValidAddress() {
        // null address
        assertThrows(NullPointerException.class, () -> Address.isValidAddress(null));

        // invalid addresses
        assertFalse(Address.isValidAddress(" ")); // spaces only
        assertFalse(Address.isValidAddress("123/45 Main Street"));

        // valid addresses
        assertTrue(Address.isValidAddress("Leng Inc, 1234 Market St, San Francisco CA 2349879, USA")); // long address
    }

    @Test
    public void equals() {
        Address address = new Address("Valid Address");

        // same values -> returns true
        assertTrue(address.equals(new Address("Valid Address")));

        // same object -> returns true
        assertTrue(address.equals(address));

        // null -> returns false
        assertFalse(address.equals(null));

        // different types -> returns false
        assertFalse(address.equals(5.0f));

        // different values -> returns false
        assertFalse(address.equals(new Address("Other Valid Address")));
    }
}
