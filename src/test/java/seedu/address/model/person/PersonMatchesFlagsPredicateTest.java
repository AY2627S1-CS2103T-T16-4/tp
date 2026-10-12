package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;
import java.util.function.Consumer;

import org.junit.jupiter.api.Test;

import seedu.address.model.tag.Tag;
import seedu.address.testutil.PersonBuilder;

public class PersonMatchesFlagsPredicateTest {

    private final Person alice = new PersonBuilder().withName("Alice Pauline").withPhone("91234567")
            .withEmail("alice@example.com").withAddress("123 Clementi Ave").withGroup("T09")
            .withGithub("AliceGh").withLinkedin("alice-li").withTelegram("alicetg")
            .withTags("friend", "classmate").build();

    @Test
    public void constructor_nullDescriptor_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new PersonMatchesFlagsPredicate(null));
    }

    @Test
    public void test_noFieldsSet_returnsTrue() {
        assertTrue(new PersonMatchesFlagsPredicate(new FindPersonDescriptor()).test(alice));
    }

    @Test
    public void test_name_matchesExactNameIgnoringCase() {
        // exact name -> returns true
        assertTrue(predicateWith(d -> d.setName(new Name("Alice Pauline"))).test(alice));

        // different case -> returns true (names are stored as block letters)
        assertTrue(predicateWith(d -> d.setName(new Name("alice pauline"))).test(alice));

        // partial name -> returns false
        assertFalse(predicateWith(d -> d.setName(new Name("Alice"))).test(alice));

        // different name -> returns false
        assertFalse(predicateWith(d -> d.setName(new Name("Bob Lee"))).test(alice));
    }

    @Test
    public void test_phone() {
        assertTrue(predicateWith(d -> d.setPhone(new Phone("91234567"))).test(alice));
        assertFalse(predicateWith(d -> d.setPhone(new Phone("81234567"))).test(alice));
    }

    @Test
    public void test_email_caseInsensitive() {
        assertTrue(predicateWith(d -> d.setEmail(new Email("alice@example.com"))).test(alice));
        assertTrue(predicateWith(d -> d.setEmail(new Email("ALICE@Example.COM"))).test(alice));
        assertFalse(predicateWith(d -> d.setEmail(new Email("bob@example.com"))).test(alice));
    }

    @Test
    public void test_address_caseInsensitive() {
        assertTrue(predicateWith(d -> d.setAddress(new Address("123 Clementi Ave"))).test(alice));
        assertTrue(predicateWith(d -> d.setAddress(new Address("123 CLEMENTI AVE"))).test(alice));

        // partial address -> returns false
        assertFalse(predicateWith(d -> d.setAddress(new Address("Clementi"))).test(alice));
    }

    @Test
    public void test_group_caseInsensitive() {
        assertTrue(predicateWith(d -> d.setGroup(new Group("T09"))).test(alice));
        assertTrue(predicateWith(d -> d.setGroup(new Group("t09"))).test(alice));
        assertFalse(predicateWith(d -> d.setGroup(new Group("T10"))).test(alice));
    }

    @Test
    public void test_github_caseSensitive() {
        assertTrue(predicateWith(d -> d.setGithub(new Github("AliceGh"))).test(alice));
        assertFalse(predicateWith(d -> d.setGithub(new Github("alicegh"))).test(alice));
        assertFalse(predicateWith(d -> d.setGithub(new Github("bobgh"))).test(alice));
    }

    @Test
    public void test_linkedin_caseInsensitive() {
        assertTrue(predicateWith(d -> d.setLinkedin(new Linkedin("alice-li"))).test(alice));
        assertTrue(predicateWith(d -> d.setLinkedin(new Linkedin("ALICE-LI"))).test(alice));
        assertFalse(predicateWith(d -> d.setLinkedin(new Linkedin("bob-li"))).test(alice));
    }

    @Test
    public void test_telegram_caseInsensitive() {
        assertTrue(predicateWith(d -> d.setTelegram(new Telegram("alicetg"))).test(alice));
        assertTrue(predicateWith(d -> d.setTelegram(new Telegram("AliceTG"))).test(alice));
        assertFalse(predicateWith(d -> d.setTelegram(new Telegram("bobtg"))).test(alice));
    }

    @Test
    public void test_tags_allSearchedTagsMustBePresent() {
        // single tag the person has -> returns true
        assertTrue(predicateWith(d -> d.setTags(Set.of(new Tag("friend")))).test(alice));

        // different case -> returns true (tags are stored in lowercase)
        assertTrue(predicateWith(d -> d.setTags(Set.of(new Tag("FRIEND")))).test(alice));

        // all of several tags -> returns true
        assertTrue(predicateWith(d -> d.setTags(Set.of(new Tag("friend"), new Tag("classmate")))).test(alice));

        // tag the person does not have -> returns false
        assertFalse(predicateWith(d -> d.setTags(Set.of(new Tag("colleague")))).test(alice));

        // one matching and one missing tag -> returns false (AND)
        assertFalse(predicateWith(d -> d.setTags(Set.of(new Tag("friend"), new Tag("colleague")))).test(alice));
    }

    @Test
    public void test_multipleFields_allMustMatch() {
        // all fields match -> returns true
        assertTrue(predicateWith(d -> {
            d.setName(new Name("Alice Pauline"));
            d.setGroup(new Group("T09"));
            d.setTags(Set.of(new Tag("friend")));
        }).test(alice));

        // one field does not match -> returns false
        assertFalse(predicateWith(d -> {
            d.setName(new Name("Alice Pauline"));
            d.setGroup(new Group("T10"));
        }).test(alice));
    }

    @Test
    public void test_unsetFieldOnPerson_doesNotMatchSearchedValue() {
        Person noGithub = new PersonBuilder().build();
        assertFalse(predicateWith(d -> d.setGithub(new Github("AliceGh"))).test(noGithub));
        assertFalse(predicateWith(d -> d.setGroup(new Group("T09"))).test(noGithub));
        assertFalse(predicateWith(d -> d.setTags(Set.of(new Tag("friend")))).test(noGithub));
    }

    @Test
    public void constructor_defensiveCopyOfDescriptor() {
        FindPersonDescriptor descriptor = new FindPersonDescriptor();
        descriptor.setGroup(new Group("T09"));
        PersonMatchesFlagsPredicate predicate = new PersonMatchesFlagsPredicate(descriptor);

        descriptor.setGroup(new Group("T10"));
        assertTrue(predicate.test(alice));
    }

    @Test
    public void equals() {
        FindPersonDescriptor firstDescriptor = new FindPersonDescriptor();
        firstDescriptor.setName(new Name("Alice"));
        FindPersonDescriptor secondDescriptor = new FindPersonDescriptor();
        secondDescriptor.setGroup(new Group("T09"));

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
        descriptor.setGroup(new Group("T09"));
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
