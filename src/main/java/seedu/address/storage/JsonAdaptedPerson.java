package seedu.address.storage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.person.Address;
import seedu.address.model.person.Email;
import seedu.address.model.person.Github;
import seedu.address.model.person.Group;
import seedu.address.model.person.Linkedin;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.model.person.Telegram;
import seedu.address.model.tag.Tag;

/**
 * Jackson-friendly version of {@link Person}.
 */
class JsonAdaptedPerson {

    public static final String MISSING_FIELD_MESSAGE_FORMAT = "Person's %s field is missing!";

    private final String name;
    private final String phone;
    private final String email;
    private final String address;
    private final String group;
    private final String github;
    private final String linkedin;
    private final String telegram;
    private final List<JsonAdaptedTag> tags = new ArrayList<>();

    /**
     * Constructs a {@code JsonAdaptedPerson} with the given person details.
     */
    @JsonCreator
    public JsonAdaptedPerson(@JsonProperty("name") String name, @JsonProperty("phone") String phone,
            @JsonProperty("email") String email, @JsonProperty("address") String address,
            @JsonProperty("group") String group, @JsonProperty("github") String github,
            @JsonProperty("linkedin") String linkedin, @JsonProperty("telegram") String telegram,
            @JsonProperty("tags") List<JsonAdaptedTag> tags) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.group = group;
        this.github = github;
        this.linkedin = linkedin;
        this.telegram = telegram;
        if (tags != null) {
            this.tags.addAll(tags);
        }
    }

    /** Compatibility constructor for JSON data that predates the optional contact fields. */
    public JsonAdaptedPerson(String name, String phone, String email, String address,
            List<JsonAdaptedTag> tags) {
        this(name, phone, email, address, null, null, null, null, tags);
    }

    /**
     * Converts a given {@code Person} into this class for Jackson use.
     */
    public JsonAdaptedPerson(Person source) {
        name = source.getName().fullName;
        phone = source.getPhone().value;
        email = source.getEmail().value;
        address = source.getAddress().value;
        group = source.getGroup().value;
        github = source.getGithub().value;
        linkedin = source.getLinkedin().value;
        telegram = source.getTelegram().value;
        tags.addAll(source.getTags().stream()
                .map(JsonAdaptedTag::new)
                .collect(Collectors.toList()));
    }

    /**
     * Converts this Jackson-friendly adapted person object into the model's {@code Person} object.
     *
     * @throws IllegalValueException if there were any data constraints violated in the adapted person.
     */
    public Person toModelType() throws IllegalValueException {
        final List<Tag> personTags = new ArrayList<>();
        for (JsonAdaptedTag tag : tags) {
            personTags.add(tag.toModelType());
        }

        if (name == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, Name.class.getSimpleName()));
        }

        if (!Name.isValidName(name)) {
            throw new IllegalValueException(Name.MESSAGE_CONSTRAINTS);
        }

        final Name modelName = new Name(name);

        if (phone == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, Phone.class.getSimpleName()));
        }

        if (!Phone.isValidPhone(phone)) {
            throw new IllegalValueException(Phone.MESSAGE_CONSTRAINTS);
        }

        final Phone modelPhone = new Phone(phone);

        String emailValue = email;
        if (emailValue == null) {
            emailValue = "";
        }

        if (!Email.isValidEmail(emailValue)) {
            throw new IllegalValueException(Email.MESSAGE_CONSTRAINTS);
        }

        final Email modelEmail = new Email(emailValue);

        String addressValue = address;
        if (addressValue == null) {
            addressValue = "";
        }

        if (!Address.isValidAddress(addressValue)) {
            throw new IllegalValueException(Address.MESSAGE_CONSTRAINTS);
        }

        final Address modelAddress = new Address(addressValue);

        String groupValue = group;
        if (groupValue == null) {
            groupValue = "";
        }

        final Group modelGroup = new Group(groupValue);

        String githubValue = github;
        if (githubValue == null) {
            githubValue = "";
        }

        final Github modelGithub = new Github(githubValue);

        String linkedinValue = linkedin;
        if (linkedinValue == null) {
            linkedinValue = "";
        }

        final Linkedin modelLinkedin = new Linkedin(linkedinValue);

        String telegramValue = telegram;
        if (telegramValue == null) {
            telegramValue = "";
        }

        final Telegram modelTelegram = new Telegram(telegramValue);

        final Set<Tag> modelTags = new HashSet<>(personTags);

        return new Person(modelName, modelPhone, modelEmail, modelAddress,
                modelGroup, modelGithub, modelLinkedin, modelTelegram, modelTags);
    }

}
