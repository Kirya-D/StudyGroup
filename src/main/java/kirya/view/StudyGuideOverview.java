package kirya.view;

import java.io.IOException;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import kirya.utils.DisplayableStudyGuide;

public class StudyGuideOverview extends GridPane {

    @FXML
    private ImageView creatorProfileImageView;
    @FXML
    private Label titleByUsernameLabel;
    @FXML
    private Label extendedUsernameAndTitleLabel;
    @FXML
    private Separator extendedUsernameBottomSeparator;
    @FXML
    private Label questionCountLabel;
    @FXML
    private Label descriptionLabel;

    @FXML
    public Button editButton;
    @FXML
    public Button favoriteButton;
    @FXML
    public Button uploadButton;
    @FXML
    public Button downloadButton;
    public ObjectProperty<DisplayableStudyGuide> studyGuideProperty = new SimpleObjectProperty<>(null);
    
    public StudyGuideOverview() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("studyguideoverview.fxml"));
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
        this.extendedUsernameBottomSeparator.managedProperty()
                .bind(this.extendedUsernameBottomSeparator.visibleProperty());
        this.extendedUsernameBottomSeparator.visibleProperty().bind(this.extendedUsernameAndTitleLabel.visibleProperty());
        this.extendedUsernameAndTitleLabel.managedProperty().bind(this.extendedUsernameAndTitleLabel.visibleProperty());
        this.extendedUsernameAndTitleLabel.visibleProperty().bind(this.extendedUsernameAndTitleLabel.textProperty().isEmpty().not());
        this.studyGuideProperty.addListener((_, _, studyGuide) -> {
            this.onStudyGuideChanged(studyGuide);
        });
    }

    private void onStudyGuideChanged(DisplayableStudyGuide studyGuide) {
        var titleLabelText = studyGuide.getTitle() + " By Username (not implemented yet)";
        var extendedByText = "Extension not implemented yet";
        var questionCountText = studyGuide.getQuestions().size() + " Questions";
        var descriptionText = studyGuide.getDescription();
        descriptionText = descriptionText.isEmpty() ? "No description." : descriptionText;

        this.titleByUsernameLabel.setText(titleLabelText);
        this.extendedUsernameAndTitleLabel.setText("");
        this.questionCountLabel.setText(questionCountText);
        this.descriptionLabel.setText(descriptionText);
    }
}
