package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javafx.application.Platform;
import javafx.scene.control.Label;
import seedu.address.testutil.PersonBuilder;

public class PersonCardTest {
    @BeforeAll
    public static void setUpToolkit() {
        Platform.startup(() -> Platform.setImplicitExit(false));
    }

    @Test
    public void constructor_loadsAndDisplaysRemark() throws Exception {
        FutureTask<Void> checkCard = new FutureTask<>(() -> {
            PersonCard card = new PersonCard(new PersonBuilder().withRemark("Likes swimming").build(), 1);
            Label remark = (Label) card.getRoot().lookup("#remark");
            assertEquals("Likes swimming", remark.getText());

            PersonCard emptyCard = new PersonCard(new PersonBuilder().build(), 2);
            assertEquals("", ((Label) emptyCard.getRoot().lookup("#remark")).getText());
            return null;
        });
        Platform.runLater(checkCard);
        checkCard.get(10, TimeUnit.SECONDS);
    }
}
