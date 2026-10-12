package seedu.address.ui;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.Region;
import seedu.address.model.person.Person;

/**
 * Panel containing the list of persons.
 */
public class PersonListPanel extends UiPart<Region> {
    private static final String FXML = "PersonListPanel.fxml";

    @FXML
    private Label contactCount;

    @FXML
    private ListView<Person> personListView;

    /**
     * Creates a {@code PersonListPanel} with the given {@code ObservableList}.
     */
    public PersonListPanel(ObservableList<Person> personList) {
        super(FXML);
        personListView.setItems(personList);
        personListView.setCellFactory(listView -> new PersonListViewCell());
        contactCount.textProperty().bind(Bindings.size(personList).asString());
        Label emptyList = new Label("No contacts to display.");
        emptyList.getStyleClass().add("empty-list-message");
        personListView.setPlaceholder(emptyList);
    }

    /**
     * Returns the contact currently selected by mouse, keyboard, or a command.
     */
    public ReadOnlyObjectProperty<Person> selectedPersonProperty() {
        return personListView.getSelectionModel().selectedItemProperty();
    }

    /**
     * Selects the first visible contact, or clears selection when the list is empty.
     */
    public void selectFirstContact() {
        personListView.getSelectionModel().clearSelection();
        if (!personListView.getItems().isEmpty()) {
            personListView.getSelectionModel().selectFirst();
            personListView.scrollTo(0);
        }
    }

    /**
     * Custom {@code ListCell} that displays the graphics of a {@code Person} using a {@code PersonCard}.
     */
    class PersonListViewCell extends ListCell<Person> {
        @Override
        protected void updateItem(Person person, boolean empty) {
            super.updateItem(person, empty);

            if (empty || person == null) {
                setGraphic(null);
                setText(null);
            } else {
                PersonCard card = new PersonCard(person, getIndex() + 1);
                card.getRoot().prefWidthProperty().bind(widthProperty().subtract(24));
                setGraphic(card.getRoot());
            }
        }
    }

}
