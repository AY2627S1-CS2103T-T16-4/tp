---
  layout: default.md
  title: "User Guide"
  pageNav: 3
---

# AB-3 User Guide

AddressBook Level 3 (AB3) is a **desktop application for managing contacts, optimized for use through a Command Line Interface (CLI)** while retaining the benefits of a Graphical User Interface (GUI). If you type quickly, AB3 can help you manage contacts faster than traditional GUI applications.

<!-- * Table of Contents -->
<page-nav-print />

--------------------------------------------------------------------------------------------------------------------

## Quick start

1. Ensure that Java `25` or later is installed on your computer.<br>
   **Mac users:** Ensure you have the precise JDK version prescribed [here](https://se-education.org/guides/tutorials/javaInstallationMac.html).

1. Download the latest `.jar` file from [here](https://github.com/se-edu/addressbook-level3/releases).

1. Copy the file to the folder you want to use as the _home folder_ for your AddressBook.

1. Open a terminal, `cd` to the folder containing the JAR file, and run `java -jar addressbook.jar`.<br>
   A GUI similar to the one below should appear in a few seconds. Note how the app contains some sample data.<br>
   ![Ui](images/Ui.png)

1. Type a command in the command box and press Enter to execute it. For example, type **`help`** and press Enter to open the help window.<br>
   Some example commands you can try:

   * `list` : Lists all contacts.

   * `add n/John Doe p/98765432 e/johnd@example.com a/John street, block 123, #01-01` : Adds a contact named `John Doe` to the Address Book.

   * `delete p/98765432` : Deletes the contact added above, identified by its phone number.

   * `clear` : Deletes all contacts.

   * `exit` : Exits the app.

1. Refer to the [Features](#features) section below for details of each command.

--------------------------------------------------------------------------------------------------------------------

## Features

<box type="info" seamless>

**Notes about the command format:**<br>

* Words in `UPPER_CASE` are the parameters to be supplied by the user.<br>
  For example, in `add n/NAME`, replace `NAME` with a value such as `John Doe`.

* Items in square brackets are optional.<br>
  For example, `n/NAME [t/TAG]` can be used as `n/John Doe t/friend` or as `n/John Doe`.

* Items followed by `...` can appear zero or more times.<br>
  For example, `[t/TAG]... ` may be omitted, or written as `t/friend` or `t/friend t/family`.

* Parameters can be in any order.<br>
  For example, if the command specifies `n/NAME p/PHONE_NUMBER`, `p/PHONE_NUMBER n/NAME` is also acceptable.
  For `edit`, flags may be reordered within each side of `>>`, but search flags must come before it and update flags after it.

* Extraneous parameters for commands that take no parameters, such as `help`, `list`, `exit`, and `clear`, are ignored.<br>
  For example, `help 123` is interpreted as `help`.

* If you are using a PDF version of this document, be careful when copying and pasting commands that span multiple lines as space characters surrounding line-breaks may be omitted when copied over to the application.
</box>

### Viewing help: `help`

Shows a message explaining how to access the help page.

![help message](images/helpMessage.png)

Format: `help`


### Adding a person: `add`

Adds a person to the address book.

Format: `add n/NAME p/PHONE_NUMBER e/EMAIL a/ADDRESS [t/TAG]... `

<box type="tip" seamless>

**Tip:** A person can have any number of tags, including zero.
</box>

Examples:
* `add n/John Doe p/98765432 e/johnd@example.com a/John street, block 123, #01-01`
* `add n/Betsy Crowe t/friend e/betsycrowe@example.com a/Newgate Prison p/1234567 t/criminal`

### Listing all persons: `list`

Shows a list of all persons in the address book.
* Empty list while using list command outputs "Your contact list is empty."; 

Format: `list`

### Editing a person: `edit`

Edits exactly one contact matching the supplied field values.

Format: `edit SEARCH_FLAGS >> UPDATE_FLAGS`

Use any of the following flags on either side of `>>`:

`[n/NAME] [p/PHONE_NUMBER] [e/EMAIL] [a/ADDRESS] [gh/GITHUB] [li/LINKEDIN] [tg/TELEGRAM] [g/GROUP]... [t/TAG]...`

**Identifying the contact:**

* Provide at least one search flag before `>>` and at least one update flag after it. Include exactly one `>>` separator. List indices are not accepted.
* Every search value must be nonempty and match in full. For example, `n/John` does not match `John Doe`. Matching ignores letter case except for GitHub usernames (`gh/`).
* All supplied search criteria must match the same contact. Repeated `g/` and `t/` search flags require membership in every supplied group and tag.
* Searches all contacts, including those hidden by the current search filter. The filter remains active after a successful edit; use `list` if the updated contact is hidden.
* If no contact matches, an error is displayed. If several match, GitMate displays them and prompts the user to enter more search criteria. Neither case changes contact data.
* Each flag other than `g/` and `t/` may appear at most once on each side of `>>`. For example, using `p/` once to identify a contact and once to change its phone number is allowed. Unsupported flags and invalid field values are rejected.

**Applying updates:**

* Supplied name, phone, email, address, GitHub, LinkedIn, and Telegram values replace the existing values. Unspecified fields and pin status remain unchanged.
* An empty optional update value clears that field, for example `e/` clears the email. Name and phone are compulsory fields and cannot be cleared.
* A phone number already belonging to another contact is rejected without changing contact data.
* Tag and group values toggle membership: an existing value is removed, and an absent value is added. Unmentioned tags and groups remain unchanged. Repeating the same value toggles it only once.
* To clear all tags or groups, use a bare `t/` or `g/` after `>>`. Do not combine a clearing flag with another occurrence of the same prefix, such as `t/ t/backend`.
* Successful edits are saved automatically and can be reversed with `undo`.

Examples:

Each example is independent and assumes the specified contact exists. The name-based example assumes exactly one contact has that name.

* `edit p/98765432 >> p/91234567 e/johndoe@example.com` changes that contact's phone and email, provided the new phone number is unused.
* `edit n/Betsy Crowe >> n/Betsy Crower t/` changes Betsy's name and clears all her tags.
* `edit p/98765432 >> t/backend g/cs2103` removes the `backend` tag and `cs2103` group if present, or adds them if absent. Other tags and groups are preserved.
* `edit p/98765432 >> e/` clears that contact's email.

### Locating persons by name: `find`

Finds persons whose names contain any of the given keywords.

Format: `find KEYWORD [MORE_KEYWORDS]`

* The search is case-insensitive; for example, `hans` matches `Hans`.
* Keyword order does not matter; for example, `Hans Bo` matches `Bo Hans`.
* The search considers only names.
* Only full words match; for example, `Han` does not match `Hans`.
* Persons matching at least one keyword are returned (an `OR` search); for example, `Hans Bo` returns `Hans Gruber` and `Bo Yang`.

Examples:
* `find John` returns `john` and `John Doe`
* `find alex david` returns `Alex Yeoh`, `David Li`<br>
  ![result for 'find alex david'](images/findAlexDavidResult.png)

### Pinning a contact: `pin`

Keeps an important contact above unpinned contacts, with a star beside its name.

Format: `pin [n/NAME] [p/PHONE_NUMBER] [a/ADDRESS] [e/EMAIL] [gh/GITHUB] [li/LINKEDIN] [tg/TELEGRAM] [g/GROUP]... [t/TAG]...`

* Provide at least one field. All supplied values must match in full, ignoring letter case except for GitHub usernames (`gh/`).
* If several contacts match, GitMate displays them and requests more details. No contact is pinned unless exactly one matches. An already-pinned contact produces an error.
* Pinned contacts stay in the order they were added, above unpinned contacts.
* Searches all contacts without clearing the current search after success. Use `list` to see a pinned contact hidden by the search.
* Pins are saved automatically and retained after editing or restarting. There is no `unpin` command.

Examples:

* `pin p/98765432` pins the contact with phone number `98765432`.
* `pin n/John Doe p/98765432` pins the contact matching both the full name and phone number.

### Deleting a person: `delete`

Deletes exactly one contact matching the supplied field values.

Format: `delete [n/NAME] [p/PHONE_NUMBER] [e/EMAIL] [a/ADDRESS] [gh/GITHUB] [li/LINKEDIN] [tg/TELEGRAM] [g/GROUP]... [t/TAG]...`

* Provide at least one search flag. Every supplied search value must be nonempty.
* All supplied criteria must match the same contact in full. Partial values do not match. Matching ignores letter case except for GitHub usernames (`gh/`).
* A contact must belong to every supplied group and have every supplied tag. Repeating `g/` or `t/` is allowed; other prefixes may appear at most once. Unsupported flags and invalid field values are rejected.
* Searches the entire address book, including contacts hidden by the current search filter. Running `find` first does not restrict which contacts can be deleted.
* If no contact matches, an error is displayed. If several match, GitMate displays them and prompts the user to enter more search criteria. No contact is deleted in either case; matching several contacts never triggers a bulk deletion.
* Successful deletions are saved automatically and can be reversed with `undo`.

Examples:

Each example is independent and assumes the specified contact exists.

* `delete p/98765432` deletes the contact with phone number `98765432`.
* `delete n/John Doe p/98765432` deletes the contact matching both the full name and phone number. A contact matching only one criterion is not deleted.

### Undoing previous ccommand: `undo`

Restores the address book to the state before the most recent data-changing
command.

Format: `undo`

* The undo command can undo data-changing commands such as add, delete, edit, and clear.
* It does not undo view-only commands such as find, list, or sort.

### Clearing all entries: `clear`

Clears all entries from the address book.

Format: `clear`

### Exporting contacts: `export`

Saves all contacts to a new CSV file, for backup or use in a spreadsheet.

Format: `export`

* Takes no parameters. `export` does not ignore extra text: it is rejected as an invalid command format and no file is created.
* Exports every contact, including contacts hidden by the current search filter, in the order shown on screen: pinned contacts first, then the active sort, then the order added.
* Creates a file named `contacts-yyyyMMdd-HHmmss.csv` in the same folder as the data file, which is `data` by default. The folder is created if it is missing.
* Never overwrites an existing file. If a file with the same name already exists, such as after two exports in the same second, an error is displayed. Run `export` again.
* The success message shows the number of contacts exported and the full path of the file. An empty address book produces a file with only the header row.
* Does not change any contact data, so it cannot be reversed with `undo`.

**File contents:**

* The file is a UTF-8 CSV with a header row, so spreadsheet applications open it correctly. The columns are:

```csv
name,phone,email,address,groups,github,linkedin,telegram,tags,pinned
```

* Names appear in capital letters, GitHub usernames keep their letter case, and all other text appears in lowercase, as GitMate stores them. Multiple groups or tags share one cell, separated by semicolons in alphabetical order. Pinned status is `true` or `false`.
* A cell that starts with `=`, `+`, `-`, `@`, a tab, or a carriage return gets a leading apostrophe, so that spreadsheet applications do not treat it as a formula.
* The file can be loaded again with [`import`](#importing-contacts-import).

Examples:

* `export` creates a file such as `data/contacts-20261025-042535.csv` containing all contacts.

### Exiting the program: `exit`

Exits the program.

Format: `exit`

### Saving the data

AddressBook automatically saves data after every command. You do not need to save manually.

### Editing the data file

AddressBook data is saved automatically as a JSON file `[JAR file location]/data/addressbook.json`. Advanced users are welcome to update data directly by editing that data file.

<box type="warning" seamless>

**Caution:**
If your changes make the data file invalid, AddressBook starts with an empty address book at the next run. The invalid file remains on disk until you run a command (AddressBook saves after every command). Still, we recommend backing up the file before editing it.<br>
Furthermore, certain edits can cause the AddressBook to behave in unexpected ways (e.g., if a value entered is outside of the acceptable range). Therefore, edit the data file only if you are confident that you can update it correctly.
</box>

### Archiving data files `[coming in v2.0]`

_Details coming soon ..._

--------------------------------------------------------------------------------------------------------------------

## FAQ

**Q**: How do I transfer my data to another computer?<br>
**A**: Install the app on the other computer and overwrite the data file it creates with the data file from your previous AddressBook home folder.<br>
Alternatively, run [`export`](#exporting-contacts-export) to save your contacts to a CSV file, copy that file to the other computer, and load it there with [`import`](#importing-contacts-import). This transfers only your contacts.

--------------------------------------------------------------------------------------------------------------------

## Known issues

1. **When using multiple screens**, if you move the application to a secondary screen, and later switch to using only the primary screen, the GUI will open off-screen. The remedy is to delete the `preferences.json` file created by the application before running the application again.
2. **If you minimize the Help Window** and then run the `help` command (or use the `Help` menu, or the keyboard shortcut `F1`) again, the original Help Window will remain minimized, and no new Help Window will appear. The remedy is to manually restore the minimized Help Window.

--------------------------------------------------------------------------------------------------------------------

## Sorting the current list: `sort`

Format: `sort name|group|tag [asc|desc] [cont|none]`. Defaults to `asc` and `none`.

* `sort name asc` sorts names alphabetically, ignoring case; `sort name desc` reverses the order.
* `sort group` sorts by each contact's alphabetically first group, breaking ties by name.
  `sort group desc` reverses group order while keeping names within a group alphabetical.
* `sort tag` uses each contact's alphabetically first tag, ignoring case and breaking ties by name.
  `sort tag desc` reverses tag order while keeping names within a tag alphabetical.
* Pinned contacts stay first. Sorting applies separately within the pinned and unpinned sections;
  contacts without groups or tags appear last within their section when sorting by that field, in either direction.
* Sorting preserves the current search filter. For example, run `find g/t09`, then `sort name`.
* Sorting remains active for subsequent searches, additions, edits, and `list` in the current session.
  Edited contacts move to their new sorted position. Sorting does not change saved contact order or create an undo step.
* Add `cont` to save the sort preference on normal application exit and restore it on the next launch.
  For example, `sort name asc cont` or `sort tag cont`. The current search filter is not saved.
* Omitting `cont`, or specifying `none`, clears any previously saved sort preference on normal exit.
  Sorting still stays active during the current session; the next launch uses pinned-first stored order.
  For example, `sort group desc none`. Forced termination before saving may lose preference changes.

## Importing contacts: `import`

Format: `import FILE_PATH`, for example `import data/contacts-20261025-042535.csv`.
Use a UTF-8 CSV file with the header exported by GitMate, in the same order:

```csv
name,phone,email,address,groups,github,linkedin,telegram,tags,pinned
```

* The entire argument is the path. Spaces are allowed without surrounding quotes.
  Relative paths start from the application's working directory; absolute paths are also accepted.
* Import adds contacts without overwriting existing contacts. A phone number already in the address book,
  or repeated in the file, rejects the whole batch. Invalid records also reject the whole batch.
  Error record numbers count the header as record 1; quoted line breaks do not start new records.
* Empty optional fields are allowed. Use semicolons between groups or tags and `true` or `false` for pinned status.
  An empty file or a header-only file imports nothing and reports an error.
* Pinned status is preserved. The current filter, active sort, and saved sort preference remain active.
  Use `list` if imported contacts are hidden by the current filter.
* One `undo` reverses the entire successful import. Contacts are saved through the normal address-book save path.
  As with other data-changing commands, a disk-save failure can leave changes in memory; validation failures do not.
* Export compatibility limitations: leading spreadsheet-protection apostrophes are preserved, because they cannot
  be distinguished from literal apostrophes. Semicolons in the groups column always separate groups, so a group
  name containing a semicolon cannot be restored unambiguously from the current export format.

## Command summary

Action     | Format, Examples
-----------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------
**Add**    | `add n/NAME p/PHONE_NUMBER e/EMAIL a/ADDRESS [t/TAG]... ` <br> e.g., `add n/James Ho p/22224444 e/jamesho@example.com a/123, Clementi Rd, 1234665 t/friend t/colleague`
**Clear**  | `clear`
**Delete** | `delete [n/NAME] [p/PHONE_NUMBER] [e/EMAIL] [a/ADDRESS] [gh/GITHUB] [li/LINKEDIN] [tg/TELEGRAM] [g/GROUP]... [t/TAG]...` (at least one field)<br> e.g., `delete p/98765432`
**Edit**   | `edit SEARCH_FLAGS >> UPDATE_FLAGS` (at least one flag on each side)<br>Supported flags: `[n/NAME] [p/PHONE_NUMBER] [e/EMAIL] [a/ADDRESS] [gh/GITHUB] [li/LINKEDIN] [tg/TELEGRAM] [g/GROUP]... [t/TAG]...`<br> e.g., `edit p/98765432 >> n/James Lee e/jameslee@example.com`
**Find**   | `find KEYWORD [MORE_KEYWORDS]`<br> e.g., `find James Jake`
**List**   | `list`
**Import** | `import FILE_PATH`<br> e.g., `import data/contacts.csv`
**Export** | `export`
**Sort**   | `sort name\|group\|tag [asc\|desc] [cont\|none]`<br> e.g., `sort name asc cont`, `sort tag`
**Pin**    | `pin [n/NAME] [p/PHONE_NUMBER] [a/ADDRESS] [e/EMAIL] [gh/GITHUB] [li/LINKEDIN] [tg/TELEGRAM] [g/GROUP]... [t/TAG]...` (at least one field)<br> e.g., `pin p/98765432`
**Help**   | `help`
