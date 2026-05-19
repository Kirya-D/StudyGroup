package kirya.view;

import static javafx.scene.layout.Region.USE_COMPUTED_SIZE;

import java.util.ArrayList;
import java.util.List;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.stage.WindowEvent;
import kirya.utils.DisplayText;
import kirya.utils.DisplayableStudyGuide;
import kirya.viewmodel.RootDisplayViewmodel;

/**
 * Code-behind for rootdisplay.fxml
 */
public class RootDisplay {

    @FXML
    private ScrollPane rootPane;
    @FXML
    private VBox homeVBox;
    @FXML
    private Label favoritedStudyGuidesToggleLabel;
    @FXML
    private TilePane favoritedStudyGuidesTilePane;
    @FXML
    private Label downloadedStudyGuidesToggleLabel;
    @FXML
    private TilePane downloadedStudyGuidesTilePane;
    @FXML
    private StudyGuideEditor studyGuideEditor;
    @FXML
    private StudyGuideViewer studyGuideViewer;

    private final NodeGroup nodeGroup = new NodeGroup();
    private final RootDisplayViewmodel viewmodel = new RootDisplayViewmodel();

    /**
     * Initializes a new RootDisplay component.
     */
    public RootDisplay() {
    }

    @FXML
    private void initialize() {
        this.nodeGroup.addNodes(List.of(this.homeVBox, this.studyGuideEditor, this.studyGuideViewer));
        this.addEventListeners();
        this.bindToViewmodel();
        this.viewmodel.load();
    }

    private void addEventListeners() {
        this.rootPane.addEventHandler(StudyGuideEvent.VIEW, handler -> {
            var guide = handler.getStudyGuide();
            if (guide != null) {
                this.studyGuideViewer.setStudyGuide(guide);
                this.studyGuideViewer.setVisible(true);
            }
        });
        this.rootPane.addEventHandler(StudyGuideEvent.CLOSE, handler -> {
            this.homeVBox.setVisible(true);
        });
        this.rootPane.addEventHandler(StudyGuideEvent.DOWNLOAD, handler -> {
            var guide = handler.getStudyGuide();
            this.viewmodel.downloadStudyGuide(guide);
            this.refreshBothStudyGuidePanes();
        });
        this.rootPane.addEventHandler(StudyGuideEvent.FAVORITE, handler -> {
            var guide = handler.getStudyGuide();
            this.viewmodel.toggleFavoriteStudyGuide(guide);
            this.refreshBothStudyGuidePanes();
        });
        this.rootPane.addEventHandler(StudyGuideEvent.UPLOAD, handler -> {
            // TODO implement uploading (when DB is implemented)
        });
        this.rootPane.addEventHandler(StudyGuideEvent.DELETE, handler -> {
            var studyGuide = handler.getStudyGuide();
            this.alertUserOfDeletion(studyGuide);
        });
        this.rootPane.addEventHandler(StudyGuideEvent.START_EDIT, handler -> {
            var studyGuide = handler.getStudyGuide();
            this.startEditingStudyGuide(studyGuide);
        });
        this.rootPane.addEventHandler(StudyGuideEvent.FINISH_EDIT, handler -> {
            if (handler.getSavedChanges()) {
                var studyGuide = handler.getStudyGuide();
                this.viewmodel.downloadStudyGuide(studyGuide);
                this.homeVBox.setVisible(true);
            } else {
                this.alertUserOfCancellation();
            }
        });
        this.studyGuideEditor.sceneProperty().addListener((_, _, scene) -> {
            if (scene != null) {
                if (scene.getWindow() != null) {
                    this.bindOnApplicationExit(scene.getWindow());
                }
                scene.windowProperty().addListener((_, _, window) -> {
                    if (window != null) {
                        this.bindOnApplicationExit(scene.getWindow());
                    }
                });
            }
        });
    }

    private void alertUserOfDeletion(DisplayableStudyGuide studyGuide) {
        var alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Are you sure?");
        alert.setHeaderText("You're about to delete the study guide \"" + studyGuide.getTitle() + "\"");
        alert.setContentText("This action is irreversible, are you sure you want to PERMANENTLY DELETE \""
                + studyGuide.getTitle() + "\"?");
        var optional = alert.showAndWait();
        if (optional.isPresent() && optional.get() == ButtonType.OK) {
            this.viewmodel.deleteStudyGuide(studyGuide);
        }
    }

    private void alertUserOfCancellation() {
        var alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Are you sure?");
        alert.setHeaderText("You're about to discard your changes");
        alert.setContentText("You have unsaved changes that you will lose if you continue!");
        var optional = alert.showAndWait();
        if (optional.isPresent() && optional.get() == ButtonType.OK) {
            this.homeVBox.setVisible(true);
        }
    }

    private void bindToViewmodel() {
        this.viewmodel.getFavoritedStudyGuidesProperty().addListener((_, _, newList) -> {
            this.refreshFavoritedTilePane();
        });
        this.viewmodel.getDownloadedStudyGuidesProperty().addListener((_, _, newList) -> {
            this.refreshDownloadedTilePane();
        });
    }

    private void refreshBothStudyGuidePanes() {
        this.refreshDownloadedTilePane();
        this.refreshFavoritedTilePane();
    }

    private void refreshFavoritedTilePane() {
        this.refreshTilePane(this.favoritedStudyGuidesTilePane, this.viewmodel.getFavoritedStudyGuidesProperty());
    }

    private void refreshDownloadedTilePane() {
        this.refreshTilePane(this.downloadedStudyGuidesTilePane, this.viewmodel.getDownloadedStudyGuidesProperty());
    }

    private void refreshTilePane(TilePane pane, List<DisplayableStudyGuide> content) {
        var paneChildren = new ArrayList<>(pane.getChildren());
        paneChildren.stream().map(child -> {
            child.setVisible(false);
            return child;
        });
        for (int i = 0; i < content.size(); i++) {
            var studyGuide = content.get(i);
            if (i < paneChildren.size()) {
                var child = paneChildren.get(i);
                if (child instanceof StudyGuideOverview overview) {
                    overview.setStudyGuide(studyGuide);
                    overview.setVisible(true);
                }
            } else {
                var overview = new StudyGuideOverview();
                overview.managedProperty().bind(overview.visibleProperty());
                overview.setStudyGuide(studyGuide);
                pane.getChildren().add(overview);
            }
        }
    }

    @FXML
    private void onCreateNewStudyGuideClick() {
        var studyGuide = this.viewmodel.createNewStudyGuide();
        this.startEditingStudyGuide(studyGuide);
    }

    private void startEditingStudyGuide(DisplayableStudyGuide studyGuide) {
        this.studyGuideEditor.setStudyGuide(studyGuide);
        this.studyGuideEditor.setVisible(true);
    }

    @FXML
    private void onFavoritedStudyGuidesDropdownClick() {
        this.toggleDropdown(this.favoritedStudyGuidesTilePane, this.favoritedStudyGuidesToggleLabel);
    }

    @FXML
    private void onDownloadedStudyGuidesDropdownClick() {
        this.toggleDropdown(this.downloadedStudyGuidesTilePane, this.downloadedStudyGuidesToggleLabel);
    }

    private void toggleDropdown(TilePane toToggle, Label label) {
        var currentlyVisible = toToggle.isVisible();
        var newLabelText = currentlyVisible ? DisplayText.TRIANGLE_DOWN : DisplayText.TRIANGLE_UP;
        var newPrefHeight = currentlyVisible ? 0 : USE_COMPUTED_SIZE;

        label.setText(newLabelText);
        toToggle.setPrefHeight(newPrefHeight);
        toToggle.setVisible(!currentlyVisible);
    }

    private void bindOnApplicationExit(javafx.stage.Window window) {
        window.addEventHandler(WindowEvent.WINDOW_CLOSE_REQUEST, handler -> {
            this.viewmodel.save();
        });
    }

}
