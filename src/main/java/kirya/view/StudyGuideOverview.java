package kirya.view;

import static kirya.view.StudyGuideEvent.DELETE;
import static kirya.view.StudyGuideEvent.DOWNLOAD;
import static kirya.view.StudyGuideEvent.FAVORITE;
import static kirya.view.StudyGuideEvent.START_EDIT;
import static kirya.view.StudyGuideEvent.UPLOAD;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import kirya.utils.DisplayableStudyGuide;

/**
 * Code-behind for studyguideoverview.fxml
 */
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

    private DisplayableStudyGuide displayableStudyGuide;
    
    /**
     * Initializes a new StudyGuideOverview component.
     */
    public StudyGuideOverview() {
        var loader = new FXMLLoader(this.getClass().getResource("studyguideoverview.fxml"));
        loader.setController(this);
        loader.setRoot(this);
        try {
            loader.load();
            this.bindToSelf();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void bindToSelf() {
        this.setOnMouseClicked(handler -> {
            this.fireEvent(new StudyGuideEvent(this.displayableStudyGuide, StudyGuideEvent.VIEW));
        });
        this.extendedUsernameAndTitleLabel.managedProperty().bind(this.extendedUsernameAndTitleLabel.visibleProperty());
        this.extendedUsernameBottomSeparator.managedProperty()
                .bind(this.extendedUsernameBottomSeparator.visibleProperty());
    }

    /**
     * Sets the study guide to overview.
     * @param studyGuide The new study guide to overview
    */
    public void setStudyGuide(DisplayableStudyGuide studyGuide) {
        this.displayableStudyGuide = studyGuide;
        this.refreshDisplay();
    }
    
    private void refreshDisplay() {
        this.titleByUsernameLabel.setText(this.displayableStudyGuide.getTitle());
        this.extendedUsernameAndTitleLabel.setText("");
        this.questionCountLabel.setText(this.displayableStudyGuide.getQuestions().size() + " questions");
        this.descriptionLabel.setText(this.displayableStudyGuide.getDescription());

        var extendedTextIsEmpty = this.extendedUsernameAndTitleLabel.getText().isBlank();
        this.extendedUsernameAndTitleLabel.setVisible(!extendedTextIsEmpty);
        this.extendedUsernameBottomSeparator.setVisible(!extendedTextIsEmpty);
    }

    @FXML
    private void onDownloadButtonClick() {
        this.fireEvent(new StudyGuideEvent(this.displayableStudyGuide, DOWNLOAD));
    }

    @FXML
    private void onEditButtonClick() {
        this.fireEvent(new StudyGuideEvent(this.displayableStudyGuide, START_EDIT));
    }

    @FXML
    private void onFavoriteButtonClick() {
        this.fireEvent(new StudyGuideEvent(this.displayableStudyGuide, FAVORITE));
    }
    
    @FXML
    private void onUploadButtonClick() {
        this.fireEvent(new StudyGuideEvent(this.displayableStudyGuide, UPLOAD));
    }

    @FXML
    private void onDeleteButtonClick() {
        this.fireEvent(new StudyGuideEvent(this.displayableStudyGuide, DELETE));
    }

}
