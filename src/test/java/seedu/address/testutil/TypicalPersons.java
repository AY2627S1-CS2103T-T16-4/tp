package seedu.address.testutil;

import static seedu.address.logic.commands.CommandTestUtil.VALID_ADDRESS_AMY;
import static seedu.address.logic.commands.CommandTestUtil.VALID_ADDRESS_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_EMAIL_AMY;
import static seedu.address.logic.commands.CommandTestUtil.VALID_EMAIL_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_GITHUB_AMY;
import static seedu.address.logic.commands.CommandTestUtil.VALID_GITHUB_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_GROUP_AMY;
import static seedu.address.logic.commands.CommandTestUtil.VALID_GROUP_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_LINKEDIN_AMY;
import static seedu.address.logic.commands.CommandTestUtil.VALID_LINKEDIN_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_NAME_AMY;
import static seedu.address.logic.commands.CommandTestUtil.VALID_NAME_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_PHONE_AMY;
import static seedu.address.logic.commands.CommandTestUtil.VALID_PHONE_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TAG_FRIEND;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TAG_HUSBAND;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TELEGRAM_AMY;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TELEGRAM_BOB;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import seedu.address.model.AddressBook;
import seedu.address.model.person.Person;

/**
 * A utility class containing a list of {@code Person} objects to be used in tests.
 *
 * <p>The persons in {@link #getTypicalPersons()} (the "typical" persons) make up the typical address book, in the
 * order listed. The persons below it (HOON, IDA, AMY, BOB) are deliberately <b>not</b> in the typical address book,
 * so that tests can use them as persons that do not exist yet (e.g. to add, or as replacement values).
 *
 * <p>The data is designed so that, for every optional field, the typical persons contain:
 * <ul>
 *     <li>a value that is unique to one person,</li>
 *     <li>a value shared by two or more persons,</li>
 *     <li>at least one person with the field left blank.</li>
 * </ul>
 * Specifically:
 * <ul>
 *     <li>Group: t09 (ALICE, BENSON, JACK), t10 (CARL, DANIEL), t11 (FIONA, KATE); blank for ELLE, GEORGE and
 *     DANIEL_TWIN.</li>
 *     <li>Github: ALICE ("AliceGh") and JACK ("alicegh") differ only by case, as github usernames are
 *     case-sensitive. Blank for CARL, FIONA, GEORGE and DANIEL_TWIN.</li>
 *     <li>Linkedin and telegram: unique per person, and blank for some persons. The blanks do not coincide, e.g.
 *     BENSON has a linkedin but no telegram while CARL has a telegram but no linkedin.</li>
 *     <li>Tags: none (CARL, ELLE, FIONA, GEORGE, DANIEL_TWIN), one ("friends": ALICE, DANIEL; "colleagues": JACK),
 *     or several (BENSON, KATE). "friends" is shared by ALICE, BENSON, DANIEL and KATE; "colleagues" by JACK and
 *     KATE.</li>
 *     <li>ALICE, DANIEL, JACK and KATE have every field filled in; GEORGE and DANIEL_TWIN have every optional
 *     field blank.</li>
 *     <li>DANIEL and DANIEL_TWIN have the same name but different phones. Persons are identified by phone, so
 *     both can be in the address book. Note that {@code CommandTestUtil#showPersonAtIndex} filters by exact name and
 *     expects exactly one match, so it cannot be used on either of them.</li>
 * </ul>
 *
 * <p>When adding persons, avoid phones already used here, by {@code PersonBuilder}'s defaults, and by the
 * {@code VALID_PHONE_*} constants in {@code CommandTestUtil}. Mutating existing values may break tests that rely on
 * the relationships above.
 */
public class TypicalPersons {

    public static final Person ALICE = new PersonBuilder().withName("Alice Pauline")
            .withAddress("123, Jurong West Ave 6, #08-111").withEmail("alice@example.com")
            .withPhone("94351253").withGroups("t09").withGithub("AliceGh").withLinkedin("alice-pauline")
            .withTelegram("alicepauline").withTags("friends").build();
    public static final Person BENSON = new PersonBuilder().withName("Benson Meier")
            .withAddress("311, Clementi Ave 2, #02-25")
            .withEmail("johnd@example.com").withPhone("98765432").withGroups("t09").withGithub("bensonm")
            .withLinkedin("benson-meier").withTags("owesMoney", "friends").build();
    public static final Person CARL = new PersonBuilder().withName("Carl Kurz").withPhone("95352563")
            .withEmail("heinz@example.com").withAddress("wall street").withGroups("t10")
            .withTelegram("carlk").build();
    public static final Person DANIEL = new PersonBuilder().withName("Daniel Meier").withPhone("87652533")
            .withEmail("cornelia@example.com").withAddress("10th street").withGroups("t10").withGithub("danielm")
            .withLinkedin("daniel-meier").withTelegram("danielm").withTags("friends").build();
    public static final Person ELLE = new PersonBuilder().withName("Elle Meyer").withPhone("94822244")
            .withEmail("werner@example.com").withAddress("michegan ave").withGithub("ellem")
            .withTelegram("ellem").build();
    public static final Person FIONA = new PersonBuilder().withName("Fiona Kunz").withPhone("94824277")
            .withEmail("lydia@example.com").withAddress("little tokyo").withGroups("t11")
            .withLinkedin("fiona-kunz").build();
    public static final Person GEORGE = new PersonBuilder().withName("George Best").withPhone("94824422")
            .withEmail("anna@example.com").withAddress("4th street").build();
    public static final Person JACK = new PersonBuilder().withName("Jack Lim").withPhone("81111111")
            .withEmail("jack@example.com").withAddress("20 Bishan Street 12").withGroups("t09")
            .withGithub("alicegh").withLinkedin("jack-lim").withTelegram("jacklim").withTags("colleagues").build();
    public static final Person KATE = new PersonBuilder().withName("Kate Ong").withPhone("82222222")
            .withEmail("kate@example.com").withAddress("7 Orchard Road").withGroups("t11").withGithub("kateong")
            .withLinkedin("kate-ong").withTelegram("kateong").withTags("friends", "colleagues").build();
    // Same name as DANIEL but a different phone
    public static final Person DANIEL_TWIN = new PersonBuilder().withName("Daniel Meier").withPhone("83333333")
            .withEmail("twin@example.com").withAddress("5 Pasir Ris Drive").build();

    // Manually added
    public static final Person HOON = new PersonBuilder().withName("Hoon Meier").withPhone("84824244")
            .withEmail("stefan@example.com").withAddress("little india").withGroups("t12").withGithub("hoonm")
            .withLinkedin("hoon-meier").withTelegram("hoonm").build();
    public static final Person IDA = new PersonBuilder().withName("Ida Mueller").withPhone("84821311")
            .withEmail("hans@example.com").withAddress("chicago ave").withGroups("t12").withGithub("idam")
            .withLinkedin("ida-mueller").withTelegram("idam").build();

    // Manually added - Person's details found in {@code CommandTestUtil}
    public static final Person AMY = new PersonBuilder().withName(VALID_NAME_AMY).withPhone(VALID_PHONE_AMY)
            .withEmail(VALID_EMAIL_AMY).withAddress(VALID_ADDRESS_AMY).withGroups(VALID_GROUP_AMY)
            .withGithub(VALID_GITHUB_AMY).withLinkedin(VALID_LINKEDIN_AMY).withTelegram(VALID_TELEGRAM_AMY)
            .withTags(VALID_TAG_FRIEND).build();
    public static final Person BOB = new PersonBuilder().withName(VALID_NAME_BOB).withPhone(VALID_PHONE_BOB)
            .withEmail(VALID_EMAIL_BOB).withAddress(VALID_ADDRESS_BOB).withGroups(VALID_GROUP_BOB)
            .withGithub(VALID_GITHUB_BOB).withLinkedin(VALID_LINKEDIN_BOB).withTelegram(VALID_TELEGRAM_BOB)
            .withTags(VALID_TAG_HUSBAND, VALID_TAG_FRIEND).build();

    public static final String KEYWORD_MATCHING_MEIER = "Meier"; // A keyword that matches MEIER

    private TypicalPersons() {} // prevents instantiation

    /**
     * Returns an {@code AddressBook} with all the typical persons.
     */
    public static AddressBook getTypicalAddressBook() {
        AddressBook ab = new AddressBook();
        for (Person person : getTypicalPersons()) {
            ab.addPerson(person);
        }
        return ab;
    }

    public static List<Person> getTypicalPersons() {
        return new ArrayList<>(Arrays.asList(ALICE, BENSON, CARL, DANIEL, ELLE, FIONA, GEORGE, JACK, KATE,
                DANIEL_TWIN));
    }
}
