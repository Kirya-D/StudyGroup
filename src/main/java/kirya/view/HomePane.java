package kirya.view;

import java.io.IOException;
import java.util.function.Consumer;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;
import kirya.utils.DisplayableStudyGuide;

public class HomePane extends GridPane {

    @FXML
    public Button createNewStudyGuideButton;

    @FXML
    public ListView<DisplayableStudyGuide> downloadedStudyGuidesListView;

    private Consumer<DisplayableStudyGuide> editAction;

    public HomePane() {
        var loader = new FXMLLoader(this.getClass().getResource("homepane.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void setStudyGuideEditAction(Consumer<DisplayableStudyGuide> eventHandler) {
        this.editAction = eventHandler;
    }

    @FXML
    private void initialize() {
        this.visibleProperty().addListener((_, _, newVal) -> {
            if (newVal) {
                this.downloadedStudyGuidesListView.refresh();
            }
        });
        this.setDownloadedStudyGuidesCellFactory();
        this.addEventHandler(ActionEvent.ACTION, event -> {
            var sender = event.getTarget();
            if (sender instanceof Button button) {
                if (button.getText().equals("📝")) {
                    this.fireEditAction(button);
                }
            }
        });
    }

    private void fireEditAction(Button sender) {
        Parent parent = sender.getParent();
        while (parent != null) {
            if (parent instanceof ListCell<?> cell) {
                if (cell.getItem() instanceof DisplayableStudyGuide item) {
                    this.editAction.accept(item);
                    break;
                }
            }
            parent = parent.getParent();
        }
    }

    private void setDownloadedStudyGuidesCellFactory() {
        this.downloadedStudyGuidesListView.setCellFactory(listview -> new ListCell<DisplayableStudyGuide>() {
            private final StudyGuideOverview overview = new StudyGuideOverview();

            @Override
            protected void updateItem(DisplayableStudyGuide studyGuide, boolean empty) {
                super.updateItem(studyGuide, empty);
                if (empty || studyGuide == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    overview.studyGuideProperty.set(studyGuide);
                    overview.deleteButton.setOnAction(handler -> {
                        getListView().getItems().remove(studyGuide);
                        handler.consume();
                    });
                    overview.prefWidthProperty().bind(widthProperty());
                    setMaxWidth(Double.MAX_VALUE);
                    setPadding(Insets.EMPTY);
                    setText(null);
                    setGraphic(overview);
                }
            }
        });
    }
}
