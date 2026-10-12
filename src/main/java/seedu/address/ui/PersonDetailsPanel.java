package seedu.address.ui;

import java.util.Comparator;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import seedu.address.model.person.Person;

/**
 * Displays the full details of the selected contact, or an empty state.
 */
public class PersonDetailsPanel extends UiPart<Region> {

    private static final String FXML = "PersonDetailsPanel.fxml";

    @FXML
    private ScrollPane detailsScroll;
    @FXML
    private VBox emptyState;
    @FXML
    private Label initials;
    @FXML
    private Label name;
    @FXML
    private Label team;
    @FXML
    private VBox teamSection;
    @FXML
    private Label phone;
    @FXML
    private Label email;
    @FXML
    private Label address;
    @FXML
    private Label github;
    @FXML
    private Label linkedin;
    @FXML
    private Label telegram;
    @FXML
    private VBox tagsSection;
    @FXML
    private FlowPane tags;

    /**
     * Creates a details panel with no contact selected.
     */
    public PersonDetailsPanel() {
        super(FXML);
        showPerson(null);
    }

    /**
     * Displays {@code person}; a null person clears the panel.
     */
    public void showPerson(Person person) {
        boolean hasPerson = person != null;
        detailsScroll.setVisible(hasPerson);
        detailsScroll.setManaged(hasPerson);
        emptyState.setVisible(!hasPerson);
        emptyState.setManaged(!hasPerson);
        if (!hasPerson) {
            return;
        }

        initials.setText(PersonCard.getInitials(person));
        name.setText(person.getName().fullName);
        team.setText(person.getGroup().value);
        team.setVisible(!person.getGroup().value.isBlank());
        team.setManaged(team.isVisible());
        teamSection.setVisible(team.isVisible());
        teamSection.setManaged(team.isVisible());
        setDetail(phone, person.getPhone().value);
        setDetail(email, person.getEmail().value);
        setDetail(address, person.getAddress().value);
        setDetail(github, person.getGithub().value);
        setDetail(linkedin, person.getLinkedin().value);
        setDetail(telegram, person.getTelegram().value);

        tags.getChildren().clear();
        person.getTags().stream()
                .sorted(Comparator.comparing(tag -> tag.tagName))
                .forEach(tag -> {
                    Label badge = new Label(tag.tagName);
                    badge.getStyleClass().add("tag-badge");
                    tags.getChildren().add(badge);
                });
        tagsSection.setVisible(!person.getTags().isEmpty());
        tagsSection.setManaged(tagsSection.isVisible());
        detailsScroll.setVvalue(0);
    }

    private void setDetail(Label label, String value) {
        label.setText(value.isBlank() ? "\u2014" : value);
        label.setTooltip(value.isBlank() ? null : new Tooltip(value));
    }
}
