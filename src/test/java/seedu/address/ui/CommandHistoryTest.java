package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CommandHistoryTest {
    private CommandHistory history;

    @BeforeEach
    public void setUp() {
        history = new CommandHistory();
    }

    @Test
    public void previous_emptyHistory_returnsEmptyString() {
        assertEquals("", history.previous());
    }

    @Test
    public void next_emptyHistory_returnsEmptyString() {
        assertEquals("", history.next());
    }

    @Test
    public void previous_returnsCommandsInReverseOrder() {
        history.add("list");
        history.add("find Alice");
        history.add("clear");

        assertEquals("clear", history.previous());
        assertEquals("find Alice", history.previous());
        assertEquals("list", history.previous());
        assertEquals("list", history.previous());
    }

    @Test
    public void next_returnsCommandsInForwardOrder() {
        history.add("list");
        history.add("find Alice");
        history.add("clear");

        history.previous();
        history.previous();
        history.previous();

        assertEquals("find Alice", history.next());
        assertEquals("clear", history.next());
        assertEquals("", history.next());
    }

    @Test
    public void addingCommand_resetsToNewestCommand() {
        history.add("list");
        history.add("find Alice");

        history.previous();
        history.add("clear");

        assertEquals("clear", history.previous());
    }

    @Test
    public void duplicateCommands_areStoredSeparately() {
        history.add("list");
        history.add("list");

        assertEquals("list", history.previous());
        assertEquals("list", history.previous());
    }
}
