package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.GEORGE;
import static seedu.address.testutil.TypicalPersons.JACK;
import static seedu.address.testutil.TypicalPersons.KATE;

import java.util.Set;
import java.util.function.Consumer;

import org.junit.jupiter.api.Test;

import seedu.address.testutil.PersonBuilder;

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
    public void test_name_startsWithIgnoringCase() {
        // full name -> returns true
        assertTrue(predicateWith(d -> d.setName("Alice Pauline")).test(ALICE));

        // start of the name -> returns true
        assertTrue(predicateWith(d -> d.setName("Alice")).test(ALICE));
        assertTrue(predicateWith(d -> d.setName("Alice Pau")).test(ALICE));
        assertTrue(predicateWith(d -> d.setName("A")).test(ALICE));

        // middle or end of the name -> returns false
        assertFalse(predicateWith(d -> d.setName("ce Pau")).test(ALICE));
        assertFalse(predicateWith(d -> d.setName("Pauline")).test(ALICE));
        assertFalse(predicateWith(d -> d.setName("line")).test(ALICE));

        // different case -> returns true (names are stored as block letters)
        assertTrue(predicateWith(d -> d.setName("aLiCe pAuLiNe")).test(ALICE));

        // another person's name -> returns false
        assertFalse(predicateWith(d -> d.setName("Benson")).test(ALICE));

        // search longer than the name -> returns false
        assertFalse(predicateWith(d -> d.setName("Alice Pauline Tan")).test(ALICE));
    }

    @Test
    public void test_phone_startsWith() {
        assertTrue(predicateWith(d -> d.setPhone("94351253")).test(ALICE));
        assertTrue(predicateWith(d -> d.setPhone("9435")).test(ALICE));
        assertFalse(predicateWith(d -> d.setPhone("1253")).test(ALICE));
        assertFalse(predicateWith(d -> d.setPhone("4")).test(ALICE));
        assertFalse(predicateWith(d -> d.setPhone("98765432")).test(ALICE));
        assertFalse(predicateWith(d -> d.setPhone("abc")).test(ALICE));
    }

    @Test
    public void test_email_startsWithIgnoringCase() {
        assertTrue(predicateWith(d -> d.setEmail("alice@example.com")).test(ALICE));
        assertTrue(predicateWith(d -> d.setEmail("ALICE")).test(ALICE));
        assertFalse(predicateWith(d -> d.setEmail("@Example")).test(ALICE));
        assertFalse(predicateWith(d -> d.setEmail("johnd")).test(ALICE));
    }

    @Test
    public void test_address_startsWithIgnoringCase() {
        assertTrue(predicateWith(d -> d.setAddress("123, Jurong West Ave 6, #08-111")).test(ALICE));
        assertTrue(predicateWith(d -> d.setAddress("123, JURONG")).test(ALICE));
        assertFalse(predicateWith(d -> d.setAddress("JURONG")).test(ALICE));
        assertFalse(predicateWith(d -> d.setAddress("west ave")).test(ALICE));
        assertFalse(predicateWith(d -> d.setAddress("Clementi")).test(ALICE));
    }

    @Test
    public void test_group_everySearchedGroupIsPrefixOfSomeGroup() {
        // full group -> returns true
        assertTrue(predicateWith(d -> d.setGroups(Set.of("t09"))).test(ALICE));

        // partial group, different case -> returns true
        assertTrue(predicateWith(d -> d.setGroups(Set.of("T0"))).test(ALICE));

        // group the person does not have -> returns false
        assertFalse(predicateWith(d -> d.setGroups(Set.of("t10"))).test(ALICE));

        // person with several groups: each searched group may match a different group
        PersonMatchesFlagsPredicate predicate = predicateWith(d -> d.setGroups(Set.of("tut", "LAB")));
        Person tutorialAndLab = new PersonBuilder().withGroups("tutorial1", "lab3").build();
        assertTrue(predicate.test(tutorialAndLab));

        // one searched group matches and another does not -> returns false
        assertFalse(predicateWith(d -> d.setGroups(Set.of("tut", "exam"))).test(tutorialAndLab));
    }

    @Test
    public void test_github_startsWithCaseSensitive() {
        assertTrue(predicateWith(d -> d.setGithub("AliceGh")).test(ALICE));
        assertTrue(predicateWith(d -> d.setGithub("Alice")).test(ALICE));
        assertFalse(predicateWith(d -> d.setGithub("ceG")).test(ALICE));

        // differs from the github only by case -> returns false
        assertFalse(predicateWith(d -> d.setGithub("alicegh")).test(ALICE));
        assertFalse(predicateWith(d -> d.setGithub("alice")).test(ALICE));
        assertTrue(predicateWith(d -> d.setGithub("alicegh")).test(JACK));

        assertFalse(predicateWith(d -> d.setGithub("bensonm")).test(ALICE));
    }

    @Test
    public void test_linkedin_startsWithIgnoringCase() {
        assertTrue(predicateWith(d -> d.setLinkedin("alice-pauline")).test(ALICE));
        assertTrue(predicateWith(d -> d.setLinkedin("ALICE-P")).test(ALICE));
        assertFalse(predicateWith(d -> d.setLinkedin("PAULINE")).test(ALICE));
        assertFalse(predicateWith(d -> d.setLinkedin("benson")).test(ALICE));
    }

    @Test
    public void test_telegram_startsWithIgnoringCase() {
        assertTrue(predicateWith(d -> d.setTelegram("alicepauline")).test(ALICE));
        assertTrue(predicateWith(d -> d.setTelegram("ALICEP")).test(ALICE));
        assertFalse(predicateWith(d -> d.setTelegram("PAUL")).test(ALICE));
        assertFalse(predicateWith(d -> d.setTelegram("carlk")).test(ALICE));
    }

    @Test
    public void test_tags_everySearchedTagIsPrefixOfSomeTag() {
        // full tag the person has -> returns true
        assertTrue(predicateWith(d -> d.setTags(Set.of("friends"))).test(ALICE));

        // partial tag, different case -> returns true (tags are stored in lowercase)
        assertTrue(predicateWith(d -> d.setTags(Set.of("FRI"))).test(ALICE));

        // all of several tags -> returns true
        assertTrue(predicateWith(d -> d.setTags(Set.of("friends", "colleagues"))).test(KATE));

        // tag the person does not have -> returns false
        assertFalse(predicateWith(d -> d.setTags(Set.of("colleagues"))).test(ALICE));

        // one matching and one missing tag -> returns false (AND)
        assertFalse(predicateWith(d -> d.setTags(Set.of("friends", "colleagues"))).test(ALICE));
    }

    @Test
    public void test_multipleFields_allMustMatch() {
        // all fields match -> returns true
        assertTrue(predicateWith(d -> {
            d.setName("alice");
            d.setGroups(Set.of("t0"));
            d.setTags(Set.of("fri"));
        }).test(ALICE));

        // one field does not match -> returns false
        assertFalse(predicateWith(d -> {
            d.setName("alice");
            d.setGroups(Set.of("t10"));
        }).test(ALICE));
    }

    @Test
    public void test_unsetFieldOnPerson_doesNotMatchSearchedValue() {
        // GEORGE has no group, github or tags
        assertFalse(predicateWith(d -> d.setGithub("Alice")).test(GEORGE));
        assertFalse(predicateWith(d -> d.setGroups(Set.of("t09"))).test(GEORGE));
        assertFalse(predicateWith(d -> d.setTags(Set.of("friends"))).test(GEORGE));
    }

    @Test
    public void constructor_defensiveCopyOfDescriptor() {
        FindPersonDescriptor descriptor = new FindPersonDescriptor();
        descriptor.setGroups(Set.of("t09"));
        PersonMatchesFlagsPredicate predicate = new PersonMatchesFlagsPredicate(descriptor);

        descriptor.setGroups(Set.of("t10"));
        assertTrue(predicate.test(ALICE));
    }

    @Test
    public void equals() {
        FindPersonDescriptor firstDescriptor = new FindPersonDescriptor();
        firstDescriptor.setName("Alice");
        FindPersonDescriptor secondDescriptor = new FindPersonDescriptor();
        secondDescriptor.setGroups(Set.of("t09"));

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
        descriptor.setGroups(Set.of("t09"));
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
