package seedu.address.storage;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

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

/** Reads the CSV format written by {@link CsvExporter}, without changing live contact data. */
public final class CsvImporter {
    public static final int FIRST_DATA_RECORD = 2;
    public static final String MESSAGE_HEADER = "Expected CSV header: " + CsvExporter.HEADER;
    public static final String MESSAGE_RECORD = "Invalid CSV record %1$d: %2$s";
    public static final String MESSAGE_COLUMN_COUNT = "Expected %1$d columns, found %2$d.";
    public static final String MESSAGE_QUOTES = "Malformed quoted field.";
    public static final String MESSAGE_PINNED = "pinned must be true or false.";
    public static final String MESSAGE_EMPTY_MEMBER = "Group and tag lists must not contain empty entries.";

    private static final char BOM = '\ufeff';
    private static final char COMMA = ',';
    private static final char QUOTE = '"';
    private static final char CR = '\r';
    private static final char LF = '\n';
    private static final String MULTI_VALUE_SEPARATOR = ";";
    private static final String PINNED_TRUE = "true";
    private static final String PINNED_FALSE = "false";

    /** Column order is the export format's fixed schema. */
    private enum Column {
        NAME, PHONE, EMAIL, ADDRESS, GROUPS, GITHUB, LINKEDIN, TELEGRAM, TAGS, PINNED
    }

    private enum State {
        START, UNQUOTED, QUOTED, AFTER_QUOTE
    }

    private CsvImporter() {}

    /**
     * Reads and validates every CSV record. A header-only file returns an empty list.
     * Leading and trailing whitespace is trimmed from fields and individual group/tag entries.
     * Leading spreadsheet-protection apostrophes are preserved because their origin is ambiguous.
     *
     * @throws IOException if the file cannot be read as UTF-8.
     * @throws IllegalValueException if the CSV structure or a contact field is invalid.
     */
    public static List<Person> read(Path file) throws IOException, IllegalValueException {
        requireNonNull(file);
        String text = Files.readString(file, StandardCharsets.UTF_8);
        if (!text.isEmpty() && text.charAt(0) == BOM) {
            text = text.substring(1);
        }
        List<List<String>> records = parseRecords(text);
        if (records.isEmpty() || !records.getFirst().equals(List.of(CsvExporter.HEADER.split(String.valueOf(COMMA))))) {
            throw new IllegalValueException(MESSAGE_HEADER);
        }
        List<Person> persons = new ArrayList<>();
        for (int index = 1; index < records.size(); index++) {
            List<String> row = records.get(index);
            try {
                if (row.size() != Column.values().length) {
                    throw new IllegalArgumentException(String.format(MESSAGE_COLUMN_COUNT,
                            Column.values().length, row.size()));
                }
                persons.add(toPerson(row));
            } catch (IllegalArgumentException e) {
                throw new IllegalValueException(String.format(MESSAGE_RECORD, index + 1, e.getMessage()), e);
            }
        }
        return List.copyOf(persons);
    }

    private static Person toPerson(List<String> row) {
        return new Person(value(row, Column.NAME, Name::new), value(row, Column.PHONE, Phone::new),
                value(row, Column.EMAIL, Email::new), value(row, Column.ADDRESS, Address::new),
                value(row, Column.GROUPS, cell -> members(cell, Group::new)),
                value(row, Column.GITHUB, Github::new), value(row, Column.LINKEDIN, Linkedin::new),
                value(row, Column.TELEGRAM, Telegram::new),
                value(row, Column.TAGS, cell -> members(cell, Tag::new)),
                value(row, Column.PINNED, CsvImporter::parsePinned));
    }

    private static <T> T value(List<String> row, Column column, Function<String, T> constructor) {
        try {
            return constructor.apply(row.get(column.ordinal()).trim());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(column + ": " + e.getMessage(), e);
        }
    }

    private static <T> Set<T> members(String cell, Function<String, T> constructor) {
        Set<T> result = new HashSet<>();
        if (cell.isEmpty()) {
            return result;
        }
        for (String member : cell.split(MULTI_VALUE_SEPARATOR, -1)) {
            if (member.isBlank()) {
                throw new IllegalArgumentException(MESSAGE_EMPTY_MEMBER);
            }
            result.add(constructor.apply(member.trim()));
        }
        return result;
    }

    private static boolean parsePinned(String cell) {
        return switch (cell) {
            case PINNED_TRUE -> true;
            case PINNED_FALSE -> false;
            default -> throw new IllegalArgumentException(MESSAGE_PINNED);
        };
    }

    /** Reads logical records, retaining commas and line breaks inside quoted cells. */
    private static List<List<String>> parseRecords(String text) throws IllegalValueException {
        List<List<String>> records = new ArrayList<>();
        List<String> row = new ArrayList<>();
        StringBuilder cell = new StringBuilder();
        State state = State.START;
        for (int index = 0; index < text.length(); index++) {
            char character = text.charAt(index);
            if (state == State.QUOTED) {
                if (character == QUOTE) {
                    state = State.AFTER_QUOTE;
                } else {
                    cell.append(character);
                }
            } else if (character == QUOTE) {
                if (state == State.AFTER_QUOTE) {
                    cell.append(QUOTE);
                    state = State.QUOTED;
                } else if (state == State.START) {
                    state = State.QUOTED;
                } else {
                    throw invalidQuotes(records.size() + 1);
                }
            } else if (character == COMMA || character == CR || character == LF) {
                row.add(cell.toString());
                cell.setLength(0);
                state = State.START;
                if (character != COMMA) {
                    records.add(row);
                    row = new ArrayList<>();
                    if (character == CR && index + 1 < text.length() && text.charAt(index + 1) == LF) {
                        index++;
                    }
                }
            } else if (state == State.AFTER_QUOTE) {
                throw invalidQuotes(records.size() + 1);
            } else {
                cell.append(character);
                state = State.UNQUOTED;
            }
        }
        if (state == State.QUOTED) {
            throw invalidQuotes(records.size() + 1);
        }
        if (!row.isEmpty() || state != State.START) {
            row.add(cell.toString());
            records.add(row);
        }
        return records;
    }

    private static IllegalValueException invalidQuotes(int recordNumber) {
        return new IllegalValueException(String.format(MESSAGE_RECORD, recordNumber, MESSAGE_QUOTES));
    }
}
