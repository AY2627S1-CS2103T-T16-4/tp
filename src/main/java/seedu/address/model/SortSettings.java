package seedu.address.model;

import static java.util.Objects.requireNonNull;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.person.Person;

/** Immutable description of a contact sort order. */
public final class SortSettings {
    /** Supported fields and their command keywords. */
    public enum Field {
        NAME("name"), GROUP("group"), TAG("tag");

        private final String keyword;

        Field(String keyword) {
            this.keyword = keyword;
        }

        public String getKeyword() {
            return keyword;
        }

        /** Returns the field matching the exact command keyword. */
        public static Optional<Field> fromKeyword(String keyword) {
            return Arrays.stream(values()).filter(field -> field.keyword.equals(keyword)).findFirst();
        }

        /** Returns the supported keywords for command usage text. */
        public static String getUsage() {
            return Arrays.stream(values()).map(Field::getKeyword).collect(Collectors.joining("|"));
        }
    }

    private final Field field;
    private final boolean isDescending;

    /** Creates settings, including when loading them from preferences JSON. */
    @JsonCreator
    public SortSettings(@JsonProperty("field") Field field,
            @JsonProperty("isDescending") boolean isDescending) {
        this.field = requireNonNull(field);
        this.isDescending = isDescending;
    }

    public Field getField() {
        return field;
    }

    public boolean isDescending() {
        return isDescending;
    }

    /** Creates the field comparator; the model supplies pin priority and stable tie-breaking. */
    public Comparator<Person> createComparator() {
        Comparator<Person> byName = Comparator.comparing(person -> person.getName().fullName,
                String.CASE_INSENSITIVE_ORDER);
        Comparator<String> valueOrder = isDescending
                ? String.CASE_INSENSITIVE_ORDER.reversed() : String.CASE_INSENSITIVE_ORDER;
        return switch (field) {
            case NAME -> isDescending ? byName.reversed() : byName;
            case GROUP -> Comparator.comparing(SortSettings::getAlphabeticallyFirstGroup,
                    Comparator.nullsLast(valueOrder)).thenComparing(byName);
            case TAG -> Comparator.comparing(SortSettings::getAlphabeticallyFirstTag,
                    Comparator.nullsLast(valueOrder)).thenComparing(byName);
        };
    }

    /** Uses the alphabetically first group as the sorting key for multiple groups; null if ungrouped. */
    private static String getAlphabeticallyFirstGroup(Person person) {
        return person.getGroups().stream().map(group -> group.value)
                .min(String.CASE_INSENSITIVE_ORDER).orElse(null);
    }

    /** Uses the alphabetically first tag as the sorting key for multiple tags; null if untagged. */
    private static String getAlphabeticallyFirstTag(Person person) {
        return person.getTags().stream().map(tag -> tag.tagName)
                .min(String.CASE_INSENSITIVE_ORDER).orElse(null);
    }

    @Override
    public boolean equals(Object other) {
        return other == this || other instanceof SortSettings otherSettings
                && field == otherSettings.field && isDescending == otherSettings.isDescending;
    }

    @Override
    public int hashCode() {
        return Objects.hash(field, isDescending);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("field", field).add("isDescending", isDescending).toString();
    }
}
