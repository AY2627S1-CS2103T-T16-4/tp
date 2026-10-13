package seedu.address.ui;

import java.util.ArrayList;
import java.util.List;

/** Stores commands entered during the current application session. */
public class CommandHistory {
    private final List<String> commands = new ArrayList<>();
    private int index;

    /** Adds a successfully executed command. */
    public void add(String command) {
        commands.add(command);
        index = commands.size();
    }

    /** Returns the previous command, or an empty string if there is no history. */
    public String previous() {
        if (commands.isEmpty()) {
            return "";
        }

        if (index > 0) {
            index--;
        }

        return commands.get(index);
    }

    /** Returns the next command, or an empty string at the newest position. */
    public String next() {
        if (commands.isEmpty()) {
            return "";
        }

        if (index < commands.size() - 1) {
            index++;
            return commands.get(index);
        }

        index = commands.size();
        return "";
    }
}
