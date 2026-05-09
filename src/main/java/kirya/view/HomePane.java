package kirya.view;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;
import kirya.utils.DisplayableStudyGuide;

public class HomePane extends GridPane {

    @FXML
    public Button createNewStudyGuideButton;

    @FXML
    public ListView<DisplayableStudyGuide> savedStudyGuidesListView;

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

    @FXML
    private void initialize() {
        this.setSavedStudyGuidesCellFactory();
    }

    private void setSavedStudyGuidesCellFactory() {
        this.savedStudyGuidesListView.setCellFactory(listview -> new ListCell<DisplayableStudyGuide>() {
            private final StudyGuideOverview overview = new StudyGuideOverview();

            @Override
            protected void updateItem(DisplayableStudyGuide studyGuide, boolean empty) {
                super.updateItem(studyGuide, empty);
                if (empty || studyGuide == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    overview.studyGuideProperty.set(studyGuide);
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
