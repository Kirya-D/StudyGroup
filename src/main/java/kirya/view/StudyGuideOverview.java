package kirya.view;

import static kirya.view.StudyGuideEvent.DOWNLOAD;
import static kirya.view.StudyGuideEvent.FAVORITE;
import static kirya.view.StudyGuideEvent.START_EDIT;
import static kirya.view.StudyGuideEvent.UPLOAD;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Border;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
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
    @FXML
    private Button downloadButton;
    @FXML
    private Button editButton;
    @FXML
    private Button favoriteButton;
    @FXML
    private Button uploadButton;

    private final String downloadTooltipText = "Download this study guide to your device.";
    private final String deleteTooltipText = "Delete this study guide from your device.";
    private final String editTooltipText = "Edit this study guide's title, description, and questions.";
    private final String favoriteTooltipText = "Favorite this study guide to pin for quick access.";
    private final String unfavoriteTooltipText = "Unfavorite this study guide.";
    private final String uploadTooltipText = "Upload this study guide online to be publically available for others to see.";
    private final String unuploadTooltipText = "Remove this study guide from online";
    private final Border defaultBorder = Border.stroke(Color.LIGHTGRAY);
    private final Border hoveredBorder = Border.stroke(Color.BLACK);
    private final int tooltipFontSize = 12;

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
            this.setTooltips();
            this.bindToSelf();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void bindToSelf() {
        this.setBorder(this.defaultBorder);
        this.setOnMouseClicked(handler -> {
            this.fireEvent(new StudyGuideEvent(this.displayableStudyGuide, StudyGuideEvent.VIEW));
        });
        this.extendedUsernameAndTitleLabel.managedProperty().bind(this.extendedUsernameAndTitleLabel.visibleProperty());
        this.extendedUsernameBottomSeparator.managedProperty()
                .bind(this.extendedUsernameBottomSeparator.visibleProperty());

        this.editButton.managedProperty().bind(this.editButton.visibleProperty());
    }

    private void setTooltips() {
        var downloadTooltip = new Tooltip();
        var editTooltip = new Tooltip(this.editTooltipText);
        var favoriteTooltip = new Tooltip();
        var uploadTooltip = new Tooltip();

        for (var tooltip : new Tooltip[] { downloadTooltip, editTooltip, favoriteTooltip, uploadTooltip }) {
            tooltip.setFont(Font.font(tooltipFontSize));
        }

        this.downloadButton.setTooltip(downloadTooltip);
        this.editButton.setTooltip(editTooltip);
        this.favoriteButton.setTooltip(favoriteTooltip);
        this.uploadButton.setTooltip(uploadTooltip);
    }

    /**
     * Sets the study guide to overview.
     * 
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

        this.updateButtonsDisplay();
    }

    private void updateButtonsDisplay() {
        var downloaded = this.displayableStudyGuide.getIsDownloaded();
        var favorited = this.displayableStudyGuide.getIsFavorited();
        var uploaded = this.displayableStudyGuide.getIsUploaded();
        var isUserCreated = true;

        var downloadButtonTextFill = downloaded ? Color.BLUE : Color.BLACK;
        var favoriteButtonTextFill = favorited ? Color.YELLOW : Color.BLACK;
        var uploadButtonTextFill = uploaded ? Color.BLUE : Color.BLACK;

        this.downloadButton.setTextFill(downloadButtonTextFill);
        this.editButton.setVisible(isUserCreated);
        this.favoriteButton.setTextFill(favoriteButtonTextFill);
        this.uploadButton.setTextFill(uploadButtonTextFill);

        this.downloadButton.getTooltip().setText(downloaded ? this.deleteTooltipText : this.downloadTooltipText);
        this.favoriteButton.getTooltip().setText(downloaded ? this.unfavoriteTooltipText : this.favoriteTooltipText);
        this.uploadButton.getTooltip().setText(downloaded ? this.unuploadTooltipText : this.uploadTooltipText);
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
    private void onMouseEnter() {
        this.setBorder(this.hoveredBorder);
    }

    @FXML
    private void onMouseExit() {
        this.setBorder(this.defaultBorder);
    }
}
