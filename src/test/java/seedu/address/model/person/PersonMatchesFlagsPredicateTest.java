package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BENSON;
import static seedu.address.testutil.TypicalPersons.CARL;
import static seedu.address.testutil.TypicalPersons.GEORGE;
import static seedu.address.testutil.TypicalPersons.JACK;
import static seedu.address.testutil.TypicalPersons.KATE;

import java.util.Set;
import java.util.function.Consumer;

import org.junit.jupiter.api.Test;

import seedu.address.model.tag.Tag;

public class PersonMatchesFlagsPredicateTest {

    @Test
    public void constructor_nullDescriptor_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new PersonMatchesFlagsPredicate(null));
    }

    @Test
    public void test_noFieldsSet_returnsTrue() {
        assertTrue(new PersonMatchesFlagsPredicate(new FindPersonDescriptor()).test(ALICE));
    }

    @Test
    public void test_name_matchesExactNameIgnoringCase() {
        // exact name -> returns true
        assertTrue(predicateWith(d -> d.setName(ALICE.getName())).test(ALICE));

        // different case -> returns true (names are stored as block letters)
        assertTrue(predicateWith(d -> d.setName(new Name(ALICE.getName().fullName.toLowerCase()))).test(ALICE));

        // partial name -> returns false
        assertFalse(predicateWith(d -> d.setName(new Name(ALICE.getName().fullName.split(" ")[0]))).test(ALICE));

        // different name -> returns false
        assertFalse(predicateWith(d -> d.setName(BENSON.getName())).test(ALICE));
    }

    @Test
    public void test_phone() {
        assertTrue(predicateWith(d -> d.setPhone(ALICE.getPhone())).test(ALICE));
        assertFalse(predicateWith(d -> d.setPhone(BENSON.getPhone())).test(ALICE));
    }

    @Test
    public void test_email_caseInsensitive() {
        assertTrue(predicateWith(d -> d.setEmail(ALICE.getEmail())).test(ALICE));
        assertTrue(predicateWith(d -> d.setEmail(new Email(ALICE.getEmail().value.toUpperCase()))).test(ALICE));
        assertFalse(predicateWith(d -> d.setEmail(BENSON.getEmail())).test(ALICE));
    }

    @Test
    public void test_address_caseInsensitive() {
        assertTrue(predicateWith(d -> d.setAddress(ALICE.getAddress())).test(ALICE));
        assertTrue(predicateWith(d -> d.setAddress(new Address(ALICE.getAddress().value.toUpperCase())))
                .test(ALICE));

        // partial address -> returns false
        String address = ALICE.getAddress().value;
        assertFalse(predicateWith(d -> d.setAddress(new Address(address.substring(0, address.length() / 2))))
                .test(ALICE));
    }

    @Test
    public void test_group_caseInsensitive() {
        assertTrue(predicateWith(d -> d.setGroup(ALICE.getGroup())).test(ALICE));
        assertTrue(predicateWith(d -> d.setGroup(new Group(ALICE.getGroup().value.toUpperCase()))).test(ALICE));
        assertFalse(predicateWith(d -> d.setGroup(CARL.getGroup())).test(ALICE));
    }

    @Test
    public void test_github_caseSensitive() {
        assertTrue(predicateWith(d -> d.setGithub(ALICE.getGithub())).test(ALICE));

        // differs from ALICE's github only by case -> returns false
        assertFalse(predicateWith(d -> d.setGithub(JACK.getGithub())).test(ALICE));

        assertFalse(predicateWith(d -> d.setGithub(BENSON.getGithub())).test(ALICE));
    }

    @Test
    public void test_linkedin_caseInsensitive() {
        assertTrue(predicateWith(d -> d.setLinkedin(ALICE.getLinkedin())).test(ALICE));
        assertTrue(predicateWith(d -> d.setLinkedin(new Linkedin(ALICE.getLinkedin().value.toUpperCase())))
                .test(ALICE));
        assertFalse(predicateWith(d -> d.setLinkedin(BENSON.getLinkedin())).test(ALICE));
    }

    @Test
    public void test_telegram_caseInsensitive() {
        assertTrue(predicateWith(d -> d.setTelegram(ALICE.getTelegram())).test(ALICE));
        assertTrue(predicateWith(d -> d.setTelegram(new Telegram(ALICE.getTelegram().value.toUpperCase())))
                .test(ALICE));
        assertFalse(predicateWith(d -> d.setTelegram(CARL.getTelegram())).test(ALICE));
    }

    @Test
    public void test_tags_allSearchedTagsMustBePresent() {
        // single tag the person has -> returns true (ALICE's only tag is also one of KATE's tags)
        assertTrue(predicateWith(d -> d.setTags(ALICE.getTags())).test(KATE));

        // different case -> returns true (tags are stored in lowercase)
        String aliceTagName = ALICE.getTags().iterator().next().tagName;
        assertTrue(predicateWith(d -> d.setTags(Set.of(new Tag(aliceTagName.toUpperCase())))).test(KATE));

        // all of several tags -> returns true
        assertTrue(predicateWith(d -> d.setTags(KATE.getTags())).test(KATE));

        // tag the person does not have -> returns false
        assertFalse(predicateWith(d -> d.setTags(JACK.getTags())).test(ALICE));

        // one matching and one missing tag -> returns false (AND)
        assertFalse(predicateWith(d -> d.setTags(KATE.getTags())).test(ALICE));
    }

    @Test
    public void test_multipleFields_allMustMatch() {
        // all fields match -> returns true
        assertTrue(predicateWith(d -> {
            d.setName(ALICE.getName());
            d.setGroup(ALICE.getGroup());
            d.setTags(ALICE.getTags());
        }).test(ALICE));

        // one field does not match -> returns false
        assertFalse(predicateWith(d -> {
            d.setName(ALICE.getName());
            d.setGroup(CARL.getGroup());
        }).test(ALICE));
    }

    @Test
    public void test_unsetFieldOnPerson_doesNotMatchSearchedValue() {
        // GEORGE has no group, github or tags
        assertFalse(predicateWith(d -> d.setGithub(ALICE.getGithub())).test(GEORGE));
        assertFalse(predicateWith(d -> d.setGroup(ALICE.getGroup())).test(GEORGE));
        assertFalse(predicateWith(d -> d.setTags(ALICE.getTags())).test(GEORGE));
    }

    @Test
    public void constructor_defensiveCopyOfDescriptor() {
        FindPersonDescriptor descriptor = new FindPersonDescriptor();
        descriptor.setGroup(ALICE.getGroup());
        PersonMatchesFlagsPredicate predicate = new PersonMatchesFlagsPredicate(descriptor);

        descriptor.setGroup(CARL.getGroup());
        assertTrue(predicate.test(ALICE));
    }

    @Test
    public void equals() {
        FindPersonDescriptor firstDescriptor = new FindPersonDescriptor();
        firstDescriptor.setName(ALICE.getName());
        FindPersonDescriptor secondDescriptor = new FindPersonDescriptor();
        secondDescriptor.setGroup(ALICE.getGroup());

        PersonMatchesFlagsPredicate firstPredicate = new PersonMatchesFlagsPredicate(firstDescriptor);
        PersonMatchesFlagsPredicate secondPredicate = new PersonMatchesFlagsPredicate(secondDescriptor);

        // same object -> returns true
        assertEquals(firstPredicate, firstPredicate);

        // same values -> returns true
        assertEquals(firstPredicate, new PersonMatchesFlagsPredicate(firstDescriptor));

        // different types -> returns false
        assertNotEquals(1, firstPredicate);

        // null -> returns false
        assertNotEquals(null, firstPredicate);

        // different descriptor -> returns false
        assertNotEquals(firstPredicate, secondPredicate);
    }

    @Test
    public void toStringMethod() {
        FindPersonDescriptor descriptor = new FindPersonDescriptor();
        descriptor.setGroup(ALICE.getGroup());
        PersonMatchesFlagsPredicate predicate = new PersonMatchesFlagsPredicate(descriptor);

        String expected = PersonMatchesFlagsPredicate.class.getCanonicalName()
                + "{descriptor=" + descriptor + "}";
        assertEquals(expected, predicate.toString());
    }

    private PersonMatchesFlagsPredicate predicateWith(Consumer<FindPersonDescriptor> setup) {
        FindPersonDescriptor descriptor = new FindPersonDescriptor();
        setup.accept(descriptor);
        return new PersonMatchesFlagsPredicate(descriptor);
    }
}
