package seedu.address.ui;

import java.util.stream.Stream;

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
    private VBox groupsSection;
    @FXML
    private FlowPane groups;
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
        showBadges(groups, groupsSection, person.getGroups().stream().map(group -> group.value), "team-badge");
        setDetail(phone, person.getPhone().value);
        setDetail(email, person.getEmail().value);
        setDetail(address, person.getAddress().value);
        setDetail(github, person.getGithub().value);
        setDetail(linkedin, person.getLinkedin().value);
        setDetail(telegram, person.getTelegram().value);

        showBadges(tags, tagsSection, person.getTags().stream().map(tag -> tag.tagName), "tag-badge");
        detailsScroll.setVvalue(0);
    }

    private void showBadges(FlowPane pane, VBox section, Stream<String> values, String styleClass) {
        pane.getChildren().clear();
        values.sorted().forEach(value -> {
            Label badge = new Label(value);
            badge.getStyleClass().add(styleClass);
            pane.getChildren().add(badge);
        });
        boolean hasBadges = !pane.getChildren().isEmpty();
        section.setVisible(hasBadges);
        section.setManaged(hasBadges);
    }

    private void setDetail(Label label, String value) {
        label.setText(value.isBlank() ? "\u2014" : value);
        label.setTooltip(value.isBlank() ? null : new Tooltip(value));
    }
}
