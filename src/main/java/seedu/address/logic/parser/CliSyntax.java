package seedu.address.logic.parser;

import java.util.List;

/**
 * Contains Command Line Interface (CLI) syntax definitions common to multiple commands
 */
public class CliSyntax {

    /* Prefix definitions */
    public static final Prefix PREFIX_NAME = new Prefix("n/");
    public static final Prefix PREFIX_PHONE = new Prefix("p/");
    public static final Prefix PREFIX_EMAIL = new Prefix("e/");
    public static final Prefix PREFIX_ADDRESS = new Prefix("a/");
    public static final Prefix PREFIX_TAG = new Prefix("t/");
    public static final Prefix PREFIX_GITHUB = new Prefix("gh/");
    public static final Prefix PREFIX_LINKEDIN = new Prefix("li/");
    public static final Prefix PREFIX_TELEGRAM = new Prefix("tg/");
    public static final Prefix PREFIX_GROUP = new Prefix("g/");

    /** Every prefix that identifies a contact field, in the order they are listed in error messages. */
    public static final List<Prefix> FIELD_PREFIXES = List.of(PREFIX_NAME, PREFIX_PHONE, PREFIX_ADDRESS,
            PREFIX_EMAIL, PREFIX_GITHUB, PREFIX_LINKEDIN, PREFIX_TELEGRAM, PREFIX_GROUP, PREFIX_TAG);

}
