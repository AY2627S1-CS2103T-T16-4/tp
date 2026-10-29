package seedu.address.storage;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import seedu.address.model.person.Person;

/**
 * Writes contacts to a CSV file that opens correctly in spreadsheet applications.
 */
public class CsvExporter {

    public static final String HEADER =
            "name,phone,email,address,groups,github,linkedin,telegram,tags,pinned";

    private static final String UTF8_BOM = "﻿";
    private static final String RECORD_SEPARATOR = "\r\n";
    private static final String MULTI_VALUE_SEPARATOR = ";";
    static final String FORMULA_TRIGGERS = "=+-@\t\r";

    private CsvExporter() {}

    /**
     * Writes {@code persons} in the given order to a new file at {@code file}, creating missing parent folders.
     *
     * @throws IOException if {@code file} already exists or cannot be written.
     */
    public static void write(List<Person> persons, Path file) throws IOException {
        requireNonNull(persons);
        requireNonNull(file);

        Path parent = file.getParent();
        // A bare filename has no parent, so it is written to the working directory, which always exists.
        if (parent != null && !Files.isDirectory(parent)) {
            Files.createDirectories(parent);
        }

        try (Writer writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE)) {
            writer.write(toCsv(persons));
        }
    }

    /**
     * Returns the CSV text for {@code persons}: a byte order mark, a header row, then one row per person.
     */
    static String toCsv(List<Person> persons) {
        requireNonNull(persons);
        return UTF8_BOM + HEADER + RECORD_SEPARATOR
                + persons.stream()
                        .map(CsvExporter::toRow)
                        .map(row -> row + RECORD_SEPARATOR)
                        .collect(Collectors.joining());
    }

    private static String toRow(Person person) {
        return Stream.of(
                person.getName().fullName,
                person.getPhone().value,
                person.getEmail().value,
                person.getAddress().value,
                joinSorted(person.getGroups().stream().map(group -> group.value)),
                person.getGithub().value,
                person.getLinkedin().value,
                person.getTelegram().value,
                joinSorted(person.getTags().stream().map(tag -> tag.tagName)),
                String.valueOf(person.isPinned()))
                .map(CsvExporter::escape)
                .collect(Collectors.joining(","));
    }

    /** Joins values in alphabetical order so the output does not depend on set iteration order. */
    private static String joinSorted(Stream<String> values) {
        return values.sorted().collect(Collectors.joining(MULTI_VALUE_SEPARATOR));
    }

    /**
     * Neutralises spreadsheet formulas, then quotes the cell if it contains a comma, quote or line break.
     */
    private static String escape(String cell) {
        String safe = !cell.isEmpty() && FORMULA_TRIGGERS.indexOf(cell.charAt(0)) >= 0 ? "'" + cell : cell;
        if (safe.contains(",") || safe.contains("\"") || safe.contains("\n") || safe.contains("\r")) {
            return "\"" + safe.replace("\"", "\"\"") + "\"";
        }
        return safe;
    }
}
