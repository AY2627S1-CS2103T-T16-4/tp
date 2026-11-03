---
  layout: default.md
  title: "Testing guide"
  pageNav: 3
---

# Testing guide

<!-- * Table of Contents -->
<page-nav-print />

<!-- -------------------------------------------------------------------------------------------------------------------- -->

## Running tests

You can run tests in two ways.

* **Method 1: Using IntelliJ JUnit test runner**
  * To run all tests, right-click on the `src/test/java` folder and choose `Run 'All Tests'`
  * To run a subset of tests, you can right-click on a test package,
    test class, or a test and choose `Run 'ABC'`
* **Method 2: Using Gradle**
  * Open a console and run the command `gradlew clean test` (Mac/Linux: `./gradlew clean test`)

<box type="info" seamless>

**Link**: Read [this Gradle Tutorial from the se-edu/guides](https://se-education.org/guides/tutorials/gradle.html) to learn more about using Gradle.
</box>

--------------------------------------------------------------------------------------------------------------------

## Types of tests

This project has three types of tests:

1. *Unit tests* target the lowest-level methods and classes.<br>
   For example: `seedu.address.commons.StringUtilTest`
1. *Integration tests* check how multiple code units work together; the individual units are assumed to work.<br>
   For example: `seedu.address.storage.StorageManagerTest`
1. *Hybrid tests* combine unit and integration testing. These tests check both the individual units and how they work together.<br>
   For example: `seedu.address.logic.LogicManagerTest`

## Command interaction coverage

Command tests check individual outcomes, while the following integration tests exercise sequences through the real `LogicManager`, command parser and model. Storage-backed tests use JUnit `@TempDir` directories and real JSON files, so they do not depend on or overwrite the application's normal data files.

| Test class | Main responsibility |
| --- | --- |
| `ImportCommandIntegrationTest` | Checks whole-batch undo, saved contacts, and preservation of data, filtering, sort preferences and existing undo history after rejected imports. |
| `UnpinCommandIntegrationTest` | Checks hidden-match ambiguity, rejected requests with existing undo history, persisted unpinned details and display order after reloading. |
| `ViewCommandIntegrationTest` | Checks help, list and sort together with filters, saved contacts and previous undo entries. Repeated help/list calls must preserve the relevant state, and invalid sort syntax must leave the active ordering and preference intact. |
| `SortPreferencesStorageTest` | Saves and reloads preferences, checks startup ordering and live updates, and verifies that a session-only sort clears an earlier persistent preference. |

`ViewCommandIntegrationTest` groups commands that interact with the current view; there is no production `ViewCommand` class. Its preference assertions inspect the model's saved-sort setting. Actual preference serialization and startup reconstruction are covered separately by `SortPreferencesStorageTest`.

The command-level tests complement these workflows. `HelpCommandTest` checks individual usage sections rather than relying only on the same aggregate constant as the implementation. `ImportCommandTest` checks duplicate phone numbers even when other details differ. `SortCommandTest` checks that equal names retain stored order within each pin section in both sort directions. Comments explain significant partitions and fixture choices, such as an empty address book versus an empty filtered view, or a valid CSV row preceding an invalid row.

To run the tests for import, help, list, unpin and sort together with Checkstyle on Windows:

```powershell
.\gradlew.bat test --tests '*Import*Test' --tests '*HelpCommandTest' --tests '*ListCommandTest' --tests '*Unpin*Test' --tests '*Sort*Test' --tests '*ViewCommandIntegrationTest' checkstyleTest
```

On macOS/Linux, use `./gradlew` in place of `.\gradlew.bat`. These are focused checks; use the full test command above when broader regression testing is needed.
