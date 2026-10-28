---
  layout: default.md
  title: "Developer Guide"
  pageNav: 3
---

# AB-3 Developer Guide

<!-- * Table of Contents -->
<page-nav-print />

--------------------------------------------------------------------------------------------------------------------

## **Acknowledgements**

* _{List the sources of reused or adapted ideas, code, documentation, and third-party libraries here, with links to the originals.}_

--------------------------------------------------------------------------------------------------------------------

## **Setting up, getting started**

Refer to the guide [_Setting up and getting started_](SettingUp.md).

--------------------------------------------------------------------------------------------------------------------

## **Design**

### Architecture

<puml src="diagrams/ArchitectureDiagram.puml" width="280" />

The ***Architecture Diagram*** given above explains the high-level design of the App.

The following provides a quick overview of the main components and their interactions.

**Main components of the architecture**

**`Main`** (consisting of classes [`Main`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/Main.java) and [`MainApp`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/MainApp.java)) is in charge of the app launch and shut down.
* At app launch, it initializes the other components in the correct sequence, and connects them up with each other.
* At shut down, it shuts down the other components and invokes cleanup methods where necessary.

The bulk of the app's work is done by the following four components:

* [**`UI`**](#ui-component): The UI of the App.
* [**`Logic`**](#logic-component): The command executor.
* [**`Model`**](#model-component): Holds the data of the App in memory.
* [**`Storage`**](#storage-component): Reads data from, and writes data to, the hard disk.

[**`Commons`**](#common-classes) represents a collection of classes used by multiple other components.

**How the architecture components interact with each other**

The *Sequence Diagram* below shows how the components interact with each other for the scenario where the user issues the command `delete n/John Doe p/81231234` and exactly one contact matches. Contact resolution and undo bookkeeping are omitted for simplicity.

<puml src="diagrams/ArchitectureSequenceDiagram.puml" width="574" />

Each of the four main components (also shown in the diagram above),

* defines its *API* in an `interface` with the same name as the Component.
* provides its functionality through a concrete `{Component Name}Manager` class that implements the corresponding API interface.

For example, the `Logic` component defines its API in `Logic.java` and implements it in `LogicManager.java`. Other components interact with a component through its interface rather than its concrete class, preventing them from coupling to that component's implementation, as illustrated in the following partial class diagram.

<puml src="diagrams/ComponentManagers.puml" width="300" />

The sections below give more details of each component.

### UI component

The **API** of this component is specified in [`Ui.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/ui/Ui.java)

<puml src="diagrams/UiClassDiagram.puml" alt="Structure of the UI Component"/>

The UI consists of a `MainWindow` and its parts, such as `CommandBox`, `ResultDisplay`, `PersonListPanel`, and `StatusBarFooter`. All of these, including `MainWindow`, inherit from the abstract `UiPart` class, which captures common behavior among classes that represent visible GUI parts.

The `UI` component uses the JavaFX UI framework. The layouts of these UI parts are defined in matching `.fxml` files in `src/main/resources/view`. For example, [`MainWindow.fxml`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/resources/view/MainWindow.fxml) specifies the layout of [`MainWindow`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/ui/MainWindow.java).

The `UI` component,

* executes user commands using the `Logic` component.
* listens for changes to `Model` data so that the UI can be updated with the modified data.
* keeps a reference to the `Logic` component, because the `UI` relies on the `Logic` to execute commands.
* depends on some classes in the `Model` component because it displays `Person` objects from the model.

### Logic component

**API** : [`Logic.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/logic/Logic.java)

Here's a (partial) class diagram of the `Logic` component:

<puml src="diagrams/LogicClassDiagram.puml" width="550"/>

The sequence diagram below illustrates the high-level interactions within the `Logic` component, taking `execute("delete n/John Doe p/81231234")` as an example where exactly one contact matches all supplied criteria. The search includes contacts hidden by the current filter; undo bookkeeping, storage, and UI updates are omitted.

<puml src="diagrams/DeleteSequenceDiagram.puml" alt="Interactions Inside the Logic Component for Deleting a Contact by Exact Field Criteria" />

<box type="info" seamless>

**Note:** The lifeline for `DeleteCommandParser` should end at the destroy marker (X), but due to a limitation of PlantUML, the lifeline continues till the end of diagram.
</box>


How the `Logic` component works:

1. When `Logic` is called upon to execute a command, the command is passed to an `AddressBookParser` object, which in turn creates a parser that matches the command (e.g., `DeleteCommandParser`) and uses it to parse the command.
1. This results in a `Command` object (more precisely, an object of one of its subclasses e.g., `DeleteCommand`) which is executed by the `LogicManager`.
1. The command can communicate with the `Model` when it is executed (e.g. to delete a person).<br>
   Note that although this is shown as a single step in the diagram above for simplicity, the code can require several interactions between the command object and the `Model` to complete the operation.
1. The result of the command execution is encapsulated as a `CommandResult` object which is returned from `Logic`.

Here are the other classes in `Logic` (omitted from the class diagram above) that are used for parsing a user command:

<puml src="diagrams/ParserClasses.puml" width="600"/>

How the parsing works:
* When called upon to parse a user command, the `AddressBookParser` class creates an `XYZCommandParser` (`XYZ` is a placeholder for the specific command name, e.g., `AddCommandParser`). The parser uses the other classes shown above to parse the user command and create an `XYZCommand` object (e.g., `AddCommand`). The `AddressBookParser` returns that object as a `Command` object.
* All `XYZCommandParser` classes, such as `AddCommandParser` and `DeleteCommandParser`, implement the `Parser` interface so they can be treated similarly where appropriate, for example during testing.

### Model component
**API** : [`Model.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/model/Model.java)

<puml src="diagrams/ModelClassDiagram.puml" width="450" />


The `Model` component,

* stores the address book data i.e., all `Person` objects (which are contained in a `UniquePersonList` object).
* stores the `Person` objects selected by the current filter, such as search results, in a separate _filtered_ list. It exposes this list as an unmodifiable `ObservableList<Person>` that the UI can observe and bind to, so the UI updates when the list changes.
* stores a `UserPrefs` object that represents the user’s preferences (currently, just the GUI settings). This is exposed to the outside as a `ReadOnlyUserPrefs` object.
* does not depend on any of the other three components (as the `Model` represents data entities of the domain, they should make sense on their own without depending on other components)


<box type="info" seamless>

**Note:** The alternative, arguably more object-oriented, design below keeps a unique list of tags in `AddressBook`, and each `Person` references tags from that list. This lets `AddressBook` maintain one `Tag` object per unique tag instead of each `Person` holding its own `Tag` objects.<br>

<puml src="diagrams/BetterModelClassDiagram.puml" width="450" />
</box>


### Storage component

**API** : [`Storage.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/storage/Storage.java)

<puml src="diagrams/StorageClassDiagram.puml" width="550" />

The `Storage` component,
* can save both address book data and user preference data in JSON format, and read them back into corresponding objects.
* is implemented by `StorageManager`, which delegates the actual JSON file access to `JsonAddressBookStorage` and `JsonUserPrefsStorage` (one class per data file).
* depends on some classes in the `Model` component (because the `Storage` component's job is to save/retrieve objects that belong to the `Model`)

### Common classes

Classes used by multiple components are in the `seedu.address.commons` package.

--------------------------------------------------------------------------------------------------------------------

## **Implementation**

This section describes some noteworthy details on how certain features are implemented.

### Add command

The `add` command creates a new contact from the details provided by the user. For example:

```text
add n/John Doe p/81231234 a/123 Clementi Ave 3 e/john.doe@example.com gh/johndoe li/john-doe tg/johndoe g/ProjectTeam t/active t/colleague
```
Its implementation has five main parts:

* **Parsing:** `AddCommandParser` tokenizes the input using the supported prefixes: name `n/`, phone `p/`, address `a/`, email `e/`, GitHub `gh/`, LinkedIn `li/`, Telegram `tg/`, group `g/`, and tag `t/`. The name and phone prefixes are required. Unsupported text, missing required fields, invalid values, and repeated single-value prefixes are rejected.
* **Field validation:** `ParserUtil` validates each field according to its corresponding model class. Names must contain only alphanumeric characters and spaces. Phone numbers must contain exactly eight digits and begin with `8` or `9`. Emails and tags must follow their respective validation rules, while optional fields may be omitted.
* **Repeated fields:** Group and tag prefixes may be repeated to assign multiple groups or tags to a contact. For example, `g/Friends g/Project` assigns two groups, while `t/active t/colleague` assigns two tags. The values are stored as sets.
* **Contact creation:** After parsing and validating the input, `AddCommandParser` creates a Person object containing the supplied details. Fields that were not provided are represented using their default empty values.
* **Duplicate prevention and persistence:** When executed, `AddCommand` checks whether the phone number is already associated with an existing contact. If it is, the command is rejected with a duplicate-phone error. Otherwise, the new contact is added to the model. Since `AddCommand` is undoable, the previous address book state is saved before the change and the updated address book is saved through Storage.

### Edit command

The `edit` command updates exactly one contact identified by exact field criteria. The `>>` separator distinguishes the search criteria from the requested updates, for example:

```text
edit n/John Doe p/81231234 >> p/91234567 e/john@example.com
```

Its implementation has five main parts:

* **Parsing:** `EditCommandParser` requires exactly one `>>`, at least one search flag, and at least one update flag. It delegates search parsing to `ExactPersonMatchParser` and stores updates in an `EditPersonDescriptor`. Supported fields are name (`n/`), phone (`p/`), email (`e/`), address (`a/`), GitHub (`gh/`), LinkedIn (`li/`), Telegram (`tg/`), groups (`g/`), and tags (`t/`). Unsupported flags, unexpected preamble text, invalid values, and repeated scalar prefixes are rejected.
* **Shared targeting:** `PersonTarget` applies `PersonMatchesExactFieldsPredicate` to the entire address book, including contacts hidden by the current filter. All supplied scalar fields must equal the stored values; contacts must belong to every supplied group and have every supplied tag. Search values cannot be empty. If no contact matches, the command reports an error. If multiple contacts match, it displays those contacts and requests more criteria without changing contact data.
* **Field updates:** `EditCommand` creates a replacement `Person`, preserving unspecified fields and the existing pin state. Scalar updates replace their existing values. Empty optional field values clear those fields, while empty name or phone updates are rejected. A nonempty tag or group update toggles each supplied value's membership once; a bare `t/` or `g/` clears the corresponding set. Mixing an empty value with another occurrence of the same set prefix is rejected. The descriptor defensively copies tag and group sets.
* **Duplicate prevention:** Before calling `Model#setPerson`, the command checks whether a changed phone number already identifies another contact. Such an update is rejected without changing contact data, since phone numbers define contact identity.
* **Persistence and undo:** `EditCommand#isUndoable` returns `true`. `LogicManager` copies the address book before execution, saves that snapshot to undo history after successful execution, and saves the updated address book through `Storage`. The command returns a success message containing the updated contact's details.

Exact matching uses the field types' normalized values: names are converted to uppercase, while tags and groups are converted to lowercase, for both stored contacts and parsed criteria. Matching these fields is therefore case-insensitive, but still requires the full value rather than a partial match. The same normalization applies to `delete` targeting.

### Delete command

The `delete` command removes exactly one contact identified by exact field criteria, for example:

```text
delete n/John Doe p/81231234
```

Its implementation has four main parts:

* **Parsing:** `DeleteCommandParser` delegates to `ExactPersonMatchParser`, using delete-specific usage and error messages. It accepts the same search fields as `edit` and requires at least one nonempty search flag. Unsupported flags, unexpected preamble text, invalid values, and repeated scalar prefixes are rejected; group and tag prefixes may be repeated.
* **Shared targeting:** `DeleteCommand` uses `PersonTarget` and `PersonMatchesExactFieldsPredicate` to resolve the criteria against all contacts, including those hidden by the current filter. Every supplied criterion must match. No match produces an error; multiple matches are displayed for refinement. Neither failure changes contact data.
* **Deletion:** Once exactly one contact is resolved, `DeleteCommand` calls `Model#deletePerson` and returns a success message containing the deleted contact's details. Criteria matching multiple contacts never trigger a bulk deletion.
* **Persistence and undo:** `DeleteCommand#isUndoable` returns `true`. `LogicManager` saves the pre-execution address book snapshot to undo history after successful execution and persists the updated address book through `Storage`. Undo can therefore restore the deleted contact with its previous details and pin state.

### Find command

The `find` command lists the contacts whose fields start with the supplied values, for example:

```text
find n/Alex g/T09
```

Its implementation has four main parts:

* **Parsing:** `FindCommandParser` accepts the name (`n/`), phone (`p/`), address (`a/`), email (`e/`), GitHub (`gh/`), LinkedIn (`li/`), Telegram (`tg/`), group (`g/`), and tag (`t/`) flags, and requires at least one. It rejects unexpected preamble text, unsupported flag-like tokens such as `x/`, empty values, and repeated scalar prefixes; group and tag prefixes may be repeated. The unsupported-flag check is needed because `ArgumentTokenizer` only splits on known prefixes, so an unknown flag would otherwise be absorbed into the value of the preceding flag. The values are stored as raw search text in a `FindPersonDescriptor`. They are not validated as full field values, so that partial values such as `p/98` are accepted.
* **Matching:** `PersonMatchesFlagsPredicate` tests a `Person` against the descriptor. Every supplied flag must match (an `AND` search), and each value must be a prefix of the corresponding field. Matching ignores letter case except for GitHub usernames, which are case-sensitive. For groups and tags, each supplied value must be a prefix of at least one of the person's groups or tags. This differs from `PersonMatchesExactFieldsPredicate`, which `edit`, `delete`, and `pin` use to match full values, so `find` does not share `PersonTarget` or `ExactPersonMatchParser` with them.
* **Display:** `FindCommand` calls `Model#updateFilteredPersonList` with the predicate. This replaces any existing filter instead of narrowing it, and `ModelManager` keeps the predicate as the current filter, so it stays active until `list` is run and is reapplied after `undo`. The displayed list still shows pinned contacts first. If no contact matches, `FindCommand` returns an error message naming the flags that were searched.
* **No persistence or undo:** `FindCommand` does not override `Command#isUndoable`, which returns `false`, so `LogicManager` saves no undo snapshot. `find` changes only the filtered view, not the contact data.

### Pinned contacts

The `pin` command marks one matching contact as pinned.

Its implementation has four main parts:
* **Shared targeting:** `PinCommandParser` uses `ExactPersonMatchParser` to validate criteria, and `PersonTarget` resolves them against all contacts using `PersonMatchesExactFieldsPredicate`. These classes are shared with `edit` and `delete`. All supplied fields must match. Ambiguous matches are displayed for refinement; unsuccessful requests leave contact data unchanged.
* **Pin state:** `PinCommand` replaces the contact with an immutable copy whose `isPinned` flag is `true`, preserving its other details. Editing retains this flag.
* **Display:** `ModelManager` sorts the filtered view with pinned contacts first, preserving addition order within each section. The stored list stays in addition order. `PersonCard` shows a star for pinned contacts. 
* **Persistence and undo:** `JsonAdaptedPerson` stores `isPinned`, defaulting to `false` for older files. Successful pins save an undo snapshot through `LogicManager`, allowing the existing undo mechanism to restore the previous state.

The activity diagram summarizes the flow, grouping unsuccessful requests into one path.

<puml src="diagrams/PinActivityDiagram.puml" alt="Pin activity flow showing validation, contact replacement, display update, saving, and feedback" />

### Undo command

The `undo` command restores the address book to the state before the most recent undoable command.

```text
undo
```

Its implementation has four main parts:

* **Command parsing:** When the user enters `undo`, `AddressBookParser` creates an `UndoCommand` object. The command does not require any prefixes or additional arguments.
* **State history:** Before an undoable command is executed, `LogicManager` creates a defensive copy of the current address book. After the command executes successfully, the copy is stored in `ModelManager`'s `undoHistory`. The history is implemented as a stack, so the most recent state is restored first.
* **State restoration:** `UndoCommand` calls `Model#undo()`. `ModelManager` checks whether `undoHistory` contains a saved state. If a state exists, it is removed from the stack and restored as the current address book. The active filtered contact list is then updated to reflect the restored data.
* **Feedback and persistence:** If a state is successfully restored, the command returns `The previous command was undone.` If no state is available, the address book remains unchanged and the command returns `There is nothing to undo.` The restored address book is saved through `Storage`.

#### Undoable commands

Commands that modify address-book data save a state for undo. These include:

* `add`
* `delete`
* `edit`
* `clear`
* `import`
* `pin`
* `unpin`

Commands that only change the current view, such as `find`, `list`, and `sort`, do not create an undo state.

### Export command

The `export` command saves all contacts to a new timestamped CSV file next to the data file, for example:

```text
export
```

Its implementation has four main parts:

* **Parsing:** `ExportCommandParser` takes no arguments and rejects any non-empty arguments with an invalid command format error. Unlike the other parsers, it is created with the folder to export into. `LogicManager` derives this folder from the parent of the data file path, or the working directory if the path has no folder part, and passes it to `AddressBookParser`, which gives it to `ExportCommandParser`. The folder is supplied this way because `Command#execute(Model)` has no access to `Storage`, so the `Command` API is unchanged.
* **Selecting contacts:** `ExportCommand` calls `Model#getAllPersonsInDisplayOrder`. This copies every contact in the address book, including those hidden by the current filter, and sorts the copy with the same comparator as the displayed list: pinned contacts first, then the active sort, then the order added. The file therefore matches what the user sees on screen, without the filter.
* **Writing:** `CsvExporter#write` creates any missing parent folder, then opens the file with `CREATE_NEW`, so an existing file is never overwritten. `ExportCommand` names the file `contacts-yyyyMMdd-HHmmss.csv` from a `Clock`, which tests replace with a fixed one. `CsvExporter` writes a byte order mark, the header row, and one CRLF-terminated row per contact. Groups and tags are sorted alphabetically and joined with semicolons. A cell starting with `=`, `+`, `-`, `@`, a tab, or a carriage return gets a leading apostrophe to prevent spreadsheet formula injection, and a cell containing a comma, quote, or line break is quoted. `CsvImporter` checks its header against `CsvExporter.HEADER`, so the two stay in step.
* **Errors and no undo:** `CsvExporter#write` throws `FileAlreadyExistsException` both when the file exists (from `CREATE_NEW`) and when the export folder is actually a regular file (from creating the folder), so `ExportCommand` checks `Files.exists` to choose between the file-exists message and the general failure message. Other `IOException`s also give the failure message. `ExportCommand` does not override `Command#isUndoable`, which returns `false`, and leaves the model unchanged. The new file is its only effect.

### Command history

The command history feature allows users to reuse successfully executed commands with the `↑` and `↓` arrow keys.

Its implementation has three main parts:

* **Command storage:** `CommandHistory` stores successfully executed commands in an ordered list. Commands are added only after `CommandExecutor#execute` completes successfully. Empty commands and commands that throw an exception are not stored.
* **History navigation:** `CommandHistory` maintains an index representing the current position in the history. `previous()` moves backward through the list and returns the previous command. `next()` moves forward through the list and returns the next command. Navigating beyond the oldest command keeps returning the oldest command, while navigating beyond the newest command returns an empty string.
* **User interface handling:** `CommandBox` listens for `KeyCode.UP` and `KeyCode.DOWN` events. The `↑` key calls `CommandHistory#previous`, while the `↓` key calls `CommandHistory#next`. The returned command is placed in the input field and the caret is moved to the end of the text. The key event is consumed so that the text field does not perform its default cursor movement.

Command history is stored only in memory and is cleared when the application is restarted.

--------------------------------------------------------------------------------------------------------------------

## **Documentation, logging, testing, dev-ops**

* [Documentation guide](Documentation.md)
* [Testing guide](Testing.md)
* [Logging guide](Logging.md)
* [DevOps guide](DevOps.md)

--------------------------------------------------------------------------------------------------------------------

## **Appendix: Requirements**

### Product scope

**Target user profile**:

* has a need to manage a significant number of teammate and peer contacts across multiple concurrent group projects and school activities
* prefers fast, command-driven interfaces over mouse interactions and visual dashboards
* can type fast and prefers typing commands for quick lookups and updates
* is reasonably comfortable using command-line interfaces to manage contacts efficiently

**Value proposition**: Helps NUS students rapidly track and access group project groupmates details through an intuitive command interface tailored for fast typists.


### User stories

Priorities: High (must have) - `* * *`, Medium (nice to have) - `* *`, Low (unlikely to have) - `*`

| Priority | Scope      | As a … | I want to …                                                            | So that I can …                                                                     |
|:---------|:-----------| :- |:-----------------------------------------------------------------------|:------------------------------------------------------------------------------------|
| `***`    | MVP        | New user | See sample data upon launching the app                                 | Immediately understand how a populated contact list looks and functions             |
| `***`    | MVP        | New user | Clear the sample data                                                  | Start populating the app with my own data                                           |
| `***`    | MVP        | New user | See instructions for commands                                          | Quickly learn how to use the app and know what command formats are accepted         |
| `***`    | MVP        | User | Add my groupmate’s contacts                                            | Keep track of their contact information for project collaboration                   |
| `***`    | MVP        | User | Delete my groupmate’s contact                                          | Remove groupmates who I do not keep in contact with anymore                         |
| `***`    | MVP        | User | List all my contacts                                                   | See an overview of all my current group project contacts                            |
| `***`    | MVP        | User | Find my contacts                                                       | Quickly retrieve specific contact details without searching through the entire list |
| `**`     | MVP        | Second-time user | Receive clear, non-destructive error messages when I mistype a command | Correct my input without losing my progress                                         |
| `**`     | MVP        | 10th time user | To see my previous commands autofilled when I click the up arrow key   | Quickly repeat or correct recent commands without having to retype them             |
| `***`    | Beyond MVP | User | Edit my groupmate's contact details                                    | Update outdated contact information without deleting and re-adding them             |
| `*`      | Beyond MVP | Experienced User | Pin contacts to appear at the top                                      | Access important contacts quickly                                                   |
| `*`      | Beyond MVP | Experienced User | Sort my contacts                                                       | Change the view of my contact list to my preferred choice                           |
| `*`      | Beyond MVP | User             | Export my contacts to a CSV file                                       | Back up my contacts or open them in a spreadsheet                                   |

### Use cases

**System:** GitMate (GM)  
**Use case:** U1. Add a contact  
**Actor:** User

**MSS**

1.  User enters the add command with contact details. 
2.  GM adds the contact to the list. 
3.  GM displays a confirmation message.

    Use case ends.

**Extensions**

* 1a. GM detects missing mandatory fields or invalid format in the entered data.

    * 1a1. GM displays an error message with the correct format.

    * Use case ends.

* 1b. GM detects that contact already exists.

    * 1b1. GM informs user of the duplicate contact.

    * Use case ends.

**System:** GitMate (GM)  
**Use case:** U2. Delete a contact  
**Actor:** User

**MSS**

1.  User enters the delete command with contact details. 
2.  GM deletes the contact. 
3.  GM displays a confirmation message.

    Use case ends.

**Extensions**

* 1a. GM detects an invalid command format, missing search criteria, or invalid field values.

    * 1a1. GM displays an error explaining the accepted input. 

    * Use case ends.

* 1b. GM detects that the contact does not exist.

    * 1b1. GM informs the user that the contact does not exist.

    * Use case ends.

* 1c. GM detects multiple contacts matching the identifier.

    * 1c1. GM displays ambiguous contacts and prompts the user to specify further details to resolve ambiguity.

    * Use case ends.
 
**System:** GitMate (GM)  
**Use case:** U3. Find a contact  
**Actor:** User

**MSS**

1.  User enters a find command with search details. 
2.  GM displays all matching contacts.

    Use case ends.

**Extensions**

* 1a. GM detects an invalid command format, missing search criteria, an unsupported flag, a repeated flag that may appear only once, or an empty search value.

    * 1a1. GM displays an error message explaining the accepted input.

    * Use case ends.

* 1b. GM detects that there are no matching contacts.

    * 1b1. GM displays an empty list and informs the user that no contact matches the flags searched.

    * Use case ends.

**System:** GitMate (GM)  
**Use case:** U4. View help  
**Actor:** User

**MSS**

1.  User enters help command. 
2.  GM displays a list of available commands, flags and examples.

    Use case ends.

**Extensions**

* 1a. User selects the help button instead.

    * 1a1. GM opens the help window, displaying the available commands, flags and examples.

    * Use case ends.

**System:** GitMate (GM)  
**Use case:** U5. List all contacts  
**Actor:** User

**MSS**

1.  User enters the list command.
2.  GM displays a list of all stored contacts.

    Use case ends.

**Extensions**

* 1a. GM detects that stored contact list in GM is empty.

    * 1a1. GM displays an empty list and informs user that contact list is empty.

    * Use case ends.

**System:** GitMate (GM)<br>
**Use case:** U6. Pin a contact<br>
**Actor:** User<br>

**MSS**

1. User requests to pin a contact, supplying details that identify it uniquely.
2. GM pins the contact and displays it above unpinned contacts whenever it is included in the current view.
3. GM displays a confirmation message.

   Use case ends.

**Extensions**

* 1a. GM detects missing or invalid contact criteria.

    * 1a1. GM displays an error explaining the accepted input.
    
    * Use case ends.

* 1b. GM finds no contact matching the supplied criteria.

    * 1b1. GM informs the user that no contact matches.
    
    * Use case ends.

* 1c. GM finds multiple matching contacts.

    * 1c1. GM displays the matching contacts and requests additional details. No contact is pinned.
    
    * 1c2. User supplies further details to identify one contact.
    
    * Use case resumes at step 1.

* 1d. GM finds that the contact is already pinned.

    * 1d1. GM informs the user that the contact is already pinned.
    
    * Use case ends.

**System:** GitMate (GM)  
**Use case:** U7. Sort Address Book
**Actor:** User

**MSS**

1.  User enters sort command with sorting details.
2.  GM reorganises current contacts according to details.
3.  GM repeats to user details to confirm sort.

    Use case ends.

**Extensions**

* 1a. User enters an unsupported field

    * 1a1. GM displays an error explaining the accepted input.

    * Use case ends.

**System:** GitMate (GM)  
**Use case:** U8. Edit a contact  
**Actor:** User

**MSS**

1.  User enters edit command with contact details to identify the contact along with the desired updates.
2.  GM updates the contact.
3.  GM displays a confirmation message.

    Use case ends.

**Extensions**

* 1a. GM detects an invalid command format, missing search criteria, invalid field values, or no fields to update.

    * 1a1. GM displays an error message explaining the accepted input.

    * Use case ends.

* 1b. GM detects that the contact does not exist.

    * 1b1. GM informs the user that no contact matches the supplied details.

    * Use case ends.

* 1c. GM detects multiple contacts matching the identifier.

    * 1c1. GM displays ambiguous contacts and prompts the user to specify further details to resolve ambiguity.

    * Use case ends.

* 1d. GM detects an attempt to clear the contact's name or phone number.

    * 1d1. GM informs the user that name and phone number cannot be cleared.

    * Use case ends.

* 1e. GM detects that the supplied phone number belongs to another contact.

    * 1e1. GM informs the user of the duplicate phone number.

    * Use case ends.
    
**System:** GitMate (GM)  
**Use case:** U9. Undo most recent command  
**Actor:** User  
**Precondition:** GM is running and ready to receive commands

**MSS**

1.  User enters the undo command.
2.  GM restores most recent saved state.

    Use case ends.

**Extensions**

* 1a. There is no previous state to restore.

    * 1a1. The system displays an error message.

    * Use case ends.

**System:** GitMate (GM)  
**Use case:** U10. Export contacts  
**Actor:** User

**MSS**

1.  User enters the export command.
2.  GM saves all contacts to a new timestamped CSV file.
3.  GM displays the number of contacts exported and the location of the file.

    Use case ends.

**Extensions**

* 1a. GM detects an invalid command format, such as extra text after the command word.

    * 1a1. GM displays an error message explaining the accepted input.

    * Use case ends.

* 2a. GM detects that a file with the same name already exists.

    * 2a1. GM displays an error asking the user to try again. The existing file is kept.

    * Use case ends.

* 2b. GM cannot write the file, for example because the folder cannot be written to.

    * 2b1. GM displays an error message with the reason.

    * Use case ends.

**System:** GitMate (GM)  
**Use case:** U11. Reuse a previous command  
**Actor:** User

**MSS**

1. User presses the `↑` key in the command box.
2. GM displays the most recent successfully executed command.
3. User presses `↑` or `↓` to navigate through the command history.
4. GM displays the corresponding command in the command box.

   Use case ends.

**Extensions**

* 1a. There are no previous commands.

    * 1a1. GM leaves the command box empty.

    * Use case ends.

* 1b. User presses `↓` while viewing the newest command.

    * 1b1. GM clears the command box.

    * Use case ends.

* 1c. The previous command failed.

    * 1c1. GM does not save the failed command in the history.

    * Use case ends.

### Non-Functional Requirements

1.  Should work on any _mainstream OS_ as long as it has Java `25` or above installed.
2.  The system should respond to any valid user command within two seconds when storing a contact list of up to 1,000 entries.
3.  A user who can type at least 60 words per minute should be able to complete routine contact management tasks (adding, searching, deleting) faster using keyboard commands than performing the equivalent operations via GUI mouse interactions.
4.  The application should be fully functional for all core contact management features without requiring an active internet connection after initial installation.
5.  The user interface should have a clear, consistent layout with readable text and sufficient contrast between text and backgrounds.
6.  Error messages and help descriptions should use clear, simple language that users can understand without technical knowledge.
7.  All contact data should persist automatically across application sessions, saving to local storage upon the completion of any add, edit, or delete action.
8.  A first-time user should be able to add, find, edit, and delete contacts within 15 minutes of completing setup, using only the application’s help messages and user guide for guidance.

### Glossary

* **MVP (Minimum Viable Product)**: The smallest set of features that makes GitMate usable, i.e. the features marked MVP in the user stories.
* **Valid command**: A command that follows an accepted command format, with all required parameters present and correctly formatted.
* **Core contact management features**: Our MVP commands of 'add', 'delete', 'find', 'help' and the sample data.
* **Duplicate contact**: A contact whose phone number is already associated with another contact. Two contacts may share the same name, so a matching name alone does not make a duplicate.
* **Mandatory field**: A contact detail that must be provided when adding a contact (name and phone number); the command is rejected if either is missing.
* **CCA (Co-Curricular Activity)**: A student club, committee or organisation at NUS that a user may belong to outside their modules.
* **Group**: A named set of groupmates, such as a module project team or a CCA committee (such contacts can be organised under the `g/` field).
* **Groupmate**: A student collaborator assigned to the same project team or CCA as the user, whose contact details are managed within the application.
* **Tag**: A label describing a contact's trait or project role (e.g. "frontend"), added with the `t/` field.
* **Event**: A scheduled meeting or deadline linked to a group (e.g. a project meeting).
* **Fast typist**: A user who types at least 60 words per minute.
* **Local storage**: The data file on the user's own computer where GitMate saves contacts, so no internet connection or server is needed.
* **Mainstream OS**: Windows, Linux, and macOS.
--------------------------------------------------------------------------------------------------------------------

## **Appendix: Instructions for manual testing**

Given below are instructions to test the app manually.

<box type="info" seamless>

**Note:** These instructions only provide a starting point for testers to work on;
testers are expected to do more *exploratory* testing.
</box>

### Launch and shutdown

1. Initial launch

   1. Download the JAR file and copy it into an empty folder.

   1. Double-click the JAR file.<br>
      Expected: The GUI opens with a set of sample contacts. The window size may not be optimal.

1. Saving window preferences

   1. Resize the window to an optimal size. Move the window to a different location. Close the window.

   1. Relaunch the app by double-clicking the JAR file.<br>
       Expected: The most recent window size and location are retained.

1. _{ more test cases … }_

### Deleting a person

Run these tests in a separate test copy of the app. Before starting, ensure that the following names and phone numbers are unused, then add these contacts:

```text
add n/Manual Alex p/81110001 e/alex@example.com a/10 Test Road gh/manualalex li/manualalex tg/manualalex g/manualteam t/backend t/testing
add n/Manual Alex p/81110002 e/alexother@example.com g/manualteam t/backend
add n/Manual Beth p/81110003 g/otherteam t/design
```

Run the cases in order. After each successful deletion, run `undo` immediately, then `list`, to restore the fixtures for the next case. Do not run `undo` after a rejected command, as it would undo an earlier successful change.

1. Deleting exactly one matching contact

   1. Test case: `delete p/81110001`<br>
      Expected: Only the contact with phone `81110001` is deleted. The status message begins with `Deleted person:` and shows that contact's details. `undo` restores all of those details.

   1. Test case: `delete n/Manual Alex p/81110002`<br>
      Expected: Only the second Manual Alex is deleted, because both supplied fields must match the same contact.

   1. Test cases (restore after each): `delete e/alex@example.com`, `delete a/10 Test Road`, `delete gh/manualalex`, `delete li/manualalex`, and `delete tg/manualalex`.<br>
      Expected: Each command deletes only the contact with phone `81110001`.

   1. Test case: `delete g/manualteam t/backend t/testing`<br>
      Expected: Only the contact with phone `81110001` is deleted. A contact must have the supplied group and every supplied tag.

1. Resolving ambiguous and unmatched criteria

   1. Test case: `delete n/Manual Alex`<br>
      Expected: Both Manual Alex contacts are displayed. The status message reports multiple matches and asks for additional prefixes. Neither contact is deleted.

   1. Test case: `delete n/Manual Alex p/81110001`<br>
      Expected: The additional phone criterion resolves the ambiguity and only the intended contact is deleted. Restore it with `undo`, then run `list`.

   1. Test cases: `delete n/Manual`, `delete n/Manual Missing`, and `delete n/Manual Alex p/81110003`.<br>
      Expected: Each reports `No contact matches the supplied criteria.` No contact is deleted. Partial names do not match, and criteria belonging to different contacts cannot be combined to select a contact.

1. Deleting a contact hidden by the current filter

   1. Run `find p/81110003`, then `delete p/81110001`, then `list`.<br>
      Expected: The hidden contact with phone `81110001` is deleted; Manual Beth remains. Targeting searches the entire address book. Run `undo`, then `list`, to restore the fixtures.

1. Rejecting invalid input

   1. Test cases: `delete`, `delete 1`, and `delete n/Manual Alex x/value`.<br>
      Expected: Each reports an invalid command format and displays usage guidance. Index-based deletion is rejected.

   1. Test case: `delete p/`<br>
      Expected: An error requests a nonempty phone search value.

   1. Test cases: `delete p/abc` and `delete p/81110001 p/81110002`.<br>
      Expected: The first reports an invalid phone value; the second reports a repeated phone prefix. Neither deletes a contact.

### Editing a person

Use the three contacts created for the delete tests, with all deletions undone. If testing this section independently, add those contacts first. Run the cases in order. After each successful edit, run `undo` immediately, then `list`, unless the case specifies otherwise. Rejected commands must leave contact data unchanged and should not be followed by `undo`.

1. Updating scalar fields while preserving other details

   1. Test case: `edit n/Manual Alex p/81110001 >> n/Manual Alex Updated p/81110004 e/updated@example.com a/20 Test Road gh/manualalexnew li/manualalexnew tg/manualalexnew`<br>
      Expected: Only the contact with phone `81110001` is updated with all supplied values. Its tags and groups remain unchanged. The status message begins with `Edited person:` and shows the updated details. `undo` restores the original contact and all original values.

   1. Test case: `edit p/81110001 >> e/updated@example.com`<br>
      Expected: Only the email changes; all other fields remain unchanged.

1. Clearing optional fields and protecting mandatory fields

   1. Test case: `edit p/81110001 >> e/ a/ gh/ li/ tg/`<br>
      Expected: Email, address, GitHub, LinkedIn, and Telegram are cleared. Name, phone, tags, and groups remain unchanged.

   1. Test cases: `edit p/81110001 >> n/` and `edit p/81110001 >> p/`.<br>
      Expected: Each reports that name and phone cannot be cleared. No contact details change.

1. Toggling and clearing tags and groups

   1. Test case: `edit p/81110001 >> t/backend t/frontend g/manualteam g/newteam`<br>
      Expected: The existing `backend` tag and `manualteam` group are removed, while `frontend` and `newteam` are added. The `testing` tag remains.

   1. Test case: `edit p/81110001 >> t/ g/`<br>
      Expected: All tags and groups are cleared, while scalar fields remain unchanged.

   1. Test cases: `edit p/81110001 >> t/ t/backend` and `edit p/81110001 >> g/ g/manualteam`.<br>
      Expected: Each reports that clearing and toggling cannot be mixed for the same field. No contact details change.

1. Resolving ambiguous and unmatched criteria

   1. Test case: `edit n/Manual Alex >> e/updated@example.com`<br>
      Expected: Both Manual Alex contacts are displayed with a multiple-match error. Neither is edited.

   1. Test case: `edit n/Manual Alex p/81110001 >> e/updated@example.com`<br>
      Expected: Adding the phone criterion identifies exactly one contact, whose email is updated.

   1. Test cases: `edit n/Manual >> e/updated@example.com` and `edit n/Manual Alex p/81110003 >> e/updated@example.com`.<br>
      Expected: Each reports `No contact matches the supplied criteria.` No contact details change.

1. Editing a contact hidden by the current filter

   1. Run `find p/81110003`, then `edit p/81110001 >> e/updated@example.com`, then `list`.<br>
      Expected: The hidden contact's email is updated. Manual Beth remains unchanged. Run `undo`, then `list`, to restore the original email.

1. Preventing duplicate phone numbers

   1. Test case: `edit p/81110001 >> p/81110002`<br>
      Expected: A duplicate-phone error is displayed. Both contacts retain their original phone numbers and details.

1. Rejecting invalid syntax and values

   1. Test cases: `edit p/81110001 e/updated@example.com`, `edit >> e/updated@example.com`, and `edit p/81110001 >> e/updated@example.com >> t/testing`.<br>
      Expected: Each reports an invalid command format because exactly one separator and nonempty search criteria are required.

   1. Test case: `edit p/81110001 >>`<br>
      Expected: An error requests at least one update field after `>>`.

   1. Test cases: `edit p/ >> e/updated@example.com`, `edit p/81110001 >> e/invalid`, `edit p/81110001 >> x/value`, and `edit p/81110001 >> p/81110004 p/81110005`.<br>
      Expected: Respectively, an empty search value, invalid email, unsupported flag, and repeated scalar prefix are rejected.

1. Retaining pin state and saving changes

   1. Run `pin p/81110001`, then `edit p/81110001 >> e/updated@example.com`.<br>
      Expected: The contact retains its pinned indicator and has the new email.

   1. Close and relaunch the app, then run `list`.<br>
      Expected: The updated email and pin state are retained. Undo history is not needed to verify persistence; this case finishes with the updated, pinned test contact saved.

### Finding a person

Run these tests in a separate test copy of the app. The edit tests change the Manual contacts, so remove them and add them again. Run `delete p/81110001`, `delete p/81110002`, and `delete p/81110003` (each reports `No contact matches the supplied criteria.` if the contact is absent), then add these contacts:

```text
add n/Manual Alex p/81110001 e/alex@example.com a/10 Test Road gh/manualalex li/manualalex tg/manualalex g/manualteam t/backend t/testing
add n/Manual Alex p/81110002 e/alexother@example.com g/manualteam t/backend
add n/Manual Beth p/81110003 g/otherteam t/design
```

The sample contacts may still be present, so the cases below use values that only the Manual contacts have. Each `find` replaces the previous filter, so no cleanup is needed between cases. Rejected commands leave the displayed list unchanged. Run `list` at the end.

1. Matching the start of a single field

   1. Test case: `find n/Manual`<br>
      Expected: All three Manual contacts are listed.

   1. Test case: `find n/manual al`<br>
      Expected: Only the two Manual Alex contacts are listed. The match is case-insensitive and may continue past a space.

   1. Test case: `find n/Alex`<br>
      Expected: No contact is listed, because no name starts with `Alex`. The status message reads `No contact was found. Please check the n/ provided.`

   1. Test cases: `find p/8111000` and `find p/81110002`.<br>
      Expected: The first lists all three Manual contacts. The second lists only the contact with phone `81110002`.

   1. Test cases: `find e/alex@` and `find e/alexo`.<br>
      Expected: The first lists only the contact with phone `81110001`. The second lists only the contact with phone `81110002`.

   1. Test case: `find a/10`<br>
      Expected: Only the contact with phone `81110001` is listed.

   1. Test cases: `find gh/manual`, `find li/manual`, and `find tg/manual`.<br>
      Expected: Each lists only the contact with phone `81110001`.

1. Letter case

   1. Test cases: `find n/MANUAL ALEX`, `find li/MANUAL`, and `find tg/MANUAL`.<br>
      Expected: The first lists both Manual Alex contacts. The second and third list only the contact with phone `81110001`.

   1. Test case: `find gh/MANUAL`<br>
      Expected: No contact is listed, because GitHub usernames are case-sensitive. The status message names `gh/`. `find gh/manual` lists the contact with phone `81110001`.

1. Matching groups and tags

   1. Test cases: `find g/manual` and `find g/OTHER`.<br>
      Expected: The first lists the contacts with phones `81110001` and `81110002`. The second lists only the contact with phone `81110003`.

   1. Test case: `find t/back`<br>
      Expected: The contacts with phones `81110001` and `81110002` are listed.

   1. Test case: `find t/back t/test`<br>
      Expected: Only the contact with phone `81110001` is listed. Each supplied value must be a prefix of at least one of the contact's tags.

   1. Test case: `find t/back t/design`<br>
      Expected: No contact is listed, because no contact has a tag starting with each value.

1. Combining flags

   1. Test case: `find n/Manual Alex p/81110002`<br>
      Expected: Only the contact with phone `81110002` is listed.

   1. Test case: `find g/manualteam t/testing`<br>
      Expected: Only the contact with phone `81110001` is listed.

   1. Test case: `find n/Manual Beth p/81110001`<br>
      Expected: No contact is listed. Flags that match different contacts cannot be combined to select a contact. The status message names `n/ p/`.

1. Viewing and clearing the results

   1. Test case: `find n/Manual`, then `list`.<br>
      Expected: After `find`, only the Manual contacts are shown. After `list`, all contacts are shown again.

   1. Test case: `find n/Manual`, then `sort name`.<br>
      Expected: The list is still limited to the Manual contacts and is sorted by name.

   1. Run `pin p/81110002`, then `find n/Manual`.<br>
      Expected: The pinned Manual Alex with phone `81110002` is listed first, with a star. Run `undo`, then `list`, to unpin it.

1. Rejecting invalid input

   1. Test cases: `find`, `find Manual`, and `find n/Manual x/value`.<br>
      Expected: Each reports an invalid command format and displays usage guidance. Keyword search and unsupported flags are rejected.

   1. Test cases: `find n/` and `find n/Manual p/`.<br>
      Expected: Each reports `Please provide a value for` the empty flag (`n/` and `p/` respectively) `to search for.`

   1. Test case: `find n/Manual n/Alex`<br>
      Expected: An error reports the repeated name prefix. `find t/back t/test` is allowed, because tag prefixes may be repeated.

   1. Test case: `find p/abc`<br>
      Expected: No contact is listed and the status message names `p/`. The value is searched as text and is not validated as a phone number.

### Saving data

1. Dealing with missing/corrupted data files

   1. _{Explain how to simulate missing or corrupted data files and state the expected behavior.}_

1. _{ more test cases … }_

### Exporting contacts

Run these tests in a separate test copy of the app. Exported files are saved in the `data` folder next to `addressbook.json`; open them in a text editor to check them. Remove any earlier Manual contacts, then add the fixtures. Run `delete p/81110001`, `delete p/81110002`, `delete p/81110003`, and `delete p/81110006` (each reports `No contact matches the supplied criteria.` if the contact is absent), then add these contacts:

```text
add n/Manual Alex p/81110001 e/alex@example.com a/10 Test Road gh/manualalex li/manualalex tg/manualalex g/manualteam t/backend t/testing
add n/Manual Alex p/81110002 e/alexother@example.com g/manualteam t/backend
add n/Manual Beth p/81110003 g/otherteam t/design
```

Run the cases in order. Exported files are not removed by the app, so delete them from the `data` folder when you have finished.

1. Exporting all contacts

   1. Test case: `export`<br>
      Expected: The status message reads `Exported N contact(s) to ` followed by the full path of `data/contacts-<yyyyMMdd-HHmmss>.csv`, where `N` is the number of contacts in the app. The file exists at that path.

   1. Open the exported file.<br>
      Expected: The first row is `name,phone,email,address,groups,github,linkedin,telegram,tags,pinned`. It contains these rows for the Manual contacts, with names in capital letters and all other text, except the GitHub username, in lowercase:

      ```csv
      MANUAL ALEX,81110001,alex@example.com,10 test road,manualteam,manualalex,manualalex,manualalex,backend;testing,false
      MANUAL ALEX,81110002,alexother@example.com,,manualteam,,,,backend,false
      MANUAL BETH,81110003,,,otherteam,,,,design,false
      ```

   1. Wait at least one second, then run `export` again.<br>
      Expected: A second file with a later timestamp is created. The first file is unchanged.

1. Exporting special characters

   1. Run `add n/Manual Quote p/81110006 a/1 Test Road, #02-01 gh/=cmd`, then `export`.<br>
      Expected: The new file contains the row `MANUAL QUOTE,81110006,,"1 test road, #02-01",,'=cmd,,,,false`. The address is quoted because it contains a comma, and the GitHub value starts with an apostrophe because it starts with `=`. Run `undo` to remove the contact.

1. Exporting while the list is filtered, pinned, or sorted

   1. Run `find n/Manual`, then `export`.<br>
      Expected: The status message gives the number of all contacts in the app, not 3, and the file contains the contacts hidden by the filter. Run `list` afterwards.

   1. Run `pin p/81110003`, then `export`.<br>
      Expected: The `MANUAL BETH` row is the first row after the header, and its `pinned` value is `true`. Run `undo`, then `list`.

   1. Run `sort name desc`, then `export`.<br>
      Expected: The rows are in the same order as the contact list on screen, so the `MANUAL BETH` row comes before both `MANUAL ALEX` rows. The sort stays active for the rest of the session.

1. Exporting an empty address book and importing the export

   1. Run `export` and note the name of the new file. Then run `clear` and `export` again.<br>
      Expected: The second status message reads `Exported 0 contact(s) to ` followed by the path. The second file contains only the header row.

   1. Run `import data/FILE_NAME`, where `FILE_NAME` is the file from the first step.<br>
      Expected: All contacts from the first export are restored, with the same details and pin status.

1. Rejecting invalid input

   1. Test cases: `export x` and `export 1`.<br>
      Expected: Each reports an invalid command format and displays usage guidance. No new file appears in the `data` folder.

The errors for a file name that already exists and for a folder that cannot be written to are hard to trigger by hand. `ExportCommandTest` covers them.
