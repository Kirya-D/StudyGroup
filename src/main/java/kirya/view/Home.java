package kirya.view;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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
import kirya.viewmodel.HomeViewmodel;

/**
 * Code-behind for home.fxml
 */
public class Home extends ScrollPane {

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

    private final String deletionHeader = "You''re about to delete the study guide \"{0}\"";
    private final String deletionContent = "This action is irreversible, are you sure you want to PERMANENTLY DELETE \"{0}\"?";
    private final String cancelEditHeader = "You're about to discard your changes";
    private final String cancelEditContent = "You have unsaved changes that you will lose if you continue!";
    private final NodeGroup nodeGroup = new NodeGroup();
    private final HomeViewmodel viewmodel = new HomeViewmodel();

    /**
     * Initializes a new Home component.
     */
    public Home() {
        var loader = new FXMLLoader(this.getClass().getResource("home.fxml"));
        loader.setController(this);
        loader.setRoot(this);
        try {
            loader.load();
            this.initialize();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initialize() {
        this.nodeGroup.addNodes(List.of(this.homeVBox, this.studyGuideEditor, this.studyGuideViewer));
        this.bindToViewmodel();
        this.addEventListeners();
        this.viewmodel.load();
    }

    private void addEventListeners() {
        this.addEventHandler(StudyGuideEvent.VIEW, handler -> this.viewStudyGuideHandler(handler));
        this.addEventHandler(StudyGuideEvent.CLOSE, handler -> this.homeVBox.setVisible(true));
        this.addEventHandler(StudyGuideEvent.DOWNLOAD, handler -> this.downloadStudyGuideHandler(handler));
        this.addEventHandler(StudyGuideEvent.FAVORITE, handler -> this.favoriteStudyGuideHandler(handler));
        this.addEventHandler(StudyGuideEvent.UPLOAD, handler -> {
            var studyGuide = handler.getStudyGuide();
            this.viewmodel.toggleUploadStudyGuide(studyGuide, true);
        });
        this.addEventHandler(StudyGuideEvent.START_EDIT, handler -> {
            var studyGuide = handler.getStudyGuide();
            this.startEditingStudyGuide(studyGuide);
        });
        this.addEventHandler(StudyGuideEvent.FINISH_EDIT, handler -> {
            this.finishEditStudyGuideHandler(handler);
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

    private void viewStudyGuideHandler(StudyGuideEvent handler) {
        var guide = handler.getStudyGuide();
        this.studyGuideViewer.setStudyGuide(guide);
        this.studyGuideViewer.setVisible(true);
    }

    private void downloadStudyGuideHandler(StudyGuideEvent handler) {
        var guide = handler.getStudyGuide();
        var downloading = !guide.getIsDownloaded();
        Runnable resultingAction = () -> this.viewmodel.toggleDownloadStudyGuide(guide, true);
        if (!downloading) {
            var header = MessageFormat.format(this.deletionHeader, guide);
            var content = MessageFormat.format(this.deletionContent, guide);
            var delete = this.confirmUserOfAction(guide, header, content);
            if (delete) {
                resultingAction = () -> this.viewmodel.toggleDownloadStudyGuide(guide, false);
            }
        }
        resultingAction.run();
    }

    private void favoriteStudyGuideHandler(StudyGuideEvent handler) {
        var guide = handler.getStudyGuide();
        var oppositeIsFavorited = !guide.getIsFavorited();
        this.viewmodel.toggleFavoriteStudyGuide(guide, oppositeIsFavorited);
    }

    private void finishEditStudyGuideHandler(StudyGuideEvent handler) {
        var studyGuide = handler.getStudyGuide();
        if (handler.getSavedChanges() && studyGuide != null) {
            this.viewmodel.saveChangesToStudyGuide(studyGuide);
            this.refreshBothStudyGuidePanes();
            this.homeVBox.setVisible(true);
        } else {
            var confirmed = this.confirmUserOfAction(studyGuide, this.cancelEditHeader, this.cancelEditContent);
            this.homeVBox.setVisible(confirmed);
        }
    }

    private boolean confirmUserOfAction(DisplayableStudyGuide studyGuide, String header, String content) {
        var proceedWithAction = false;

        var alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Are you sure?");
        alert.setHeaderText(header);
        alert.setContentText(content);
        var optional = alert.showAndWait();

        if (optional.isPresent() && optional.get() == ButtonType.OK) {
            proceedWithAction = true;
        }

        return proceedWithAction;
    }

    private void bindToViewmodel() {
        this.viewmodel.getFavoritedStudyGuidesProperty().addListener((_, _, newList) -> {
            this.refreshBothStudyGuidePanes();
        });
        this.viewmodel.getDownloadedStudyGuidesProperty().addListener((_, _, newList) -> {
            this.refreshBothStudyGuidePanes();
        });
    }

    private void refreshBothStudyGuidePanes() {
        var favoritesPane = this.favoritedStudyGuidesTilePane;
        var favoritesContent = this.viewmodel.getFavoritedStudyGuidesProperty().get();
        var downloadPane = this.downloadedStudyGuidesTilePane;
        var downloadContent = this.viewmodel.getDownloadedStudyGuidesProperty().get();
        this.refreshTilePane(downloadPane, downloadContent);
        this.refreshTilePane(favoritesPane, favoritesContent);
    }

    private void refreshTilePane(TilePane pane, List<DisplayableStudyGuide> content) {
        for (var child : pane.getChildren()) {
            child.setVisible(false);
        }

        var paneChildren = new ArrayList<>(pane.getChildren());

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
            this.viewmodel.save(); // TODO Move this behaviour App.stop()
        });
    }

}
