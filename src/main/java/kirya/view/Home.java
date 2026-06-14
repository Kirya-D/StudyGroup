package kirya.view;

import java.io.IOException;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import javafx.beans.property.ListProperty;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;
import kirya.utils.DisplayText;
import kirya.utils.DisplayableStudyGuide;
import kirya.utils.SessionData;
import kirya.view.events.StudyGuideEvent;
import kirya.viewmodel.HomeViewmodel;

/**
 * Code-behind for home.fxml
 */
public class Home extends StackPane {

    @FXML
    private BorderPane homeBorderPane;
    @FXML
    private ToggleButton allToggleButton;
    @FXML
    private ToggleButton favoritedToggleButton;
    @FXML
    private ToggleButton downloadedToggleButton;
    @FXML
    private ToggleButton uploadedToggleButton;
    @FXML
    private Label headerLabel;
    @FXML
    private TilePane quickAccessStudyguideTilePane;
    @FXML
    private BorderPane searchingBorderPane;
    @FXML
    private TextField searchTextField;
    @FXML
    private ScrollPane searchScrollPane;
    @FXML
    private TilePane searchedStudyguideTilePane;

    private static final String HEADER_STRING = "{0}''s Quick Access Study Guides";
    private static final String DELETION_HEADER = "You are about to delete the study guide \"{0}\"";
    private static final String DELETION_CONTENT = "This action is irreversible, are you sure you want to PERMANENTLY DELETE \"{0}\"?";
    private static final String DELIST_HEADER = "You are about to remove the study guide \"{0}\" from the cloud";
    private static final String DELIST_CONTENT = "This will remove your study guide from search results, are you sure you want to delist \"{0}\"?";
    private final NodeGroup nodeGroup = new NodeGroup();
    private HomeViewmodel viewmodel;
    private ToggleButton currentlyToggled;

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
        this.nodeGroup.addNodes(List.of(this.homeBorderPane, this.searchingBorderPane));

        this.visibleProperty().addListener((_, _, visible) -> {
            if (!visible) {
                return;
            }
            var header = MessageFormat.format(HEADER_STRING, SessionData.getLoggedInUsername());
            this.headerLabel.setText(header);
        });

        var prefColumnWidth = 480;
        for (var tilepane : new TilePane[] { this.quickAccessStudyguideTilePane, this.searchedStudyguideTilePane }) {
            tilepane.widthProperty().addListener((_, _, newWidth) -> {
                var numChildren = tilepane.getChildren().size();
                var desiredColumns = Math.max(1, (newWidth.intValue() / prefColumnWidth));
                var actualColumns = Math.min(numChildren, desiredColumns);
                actualColumns = actualColumns == 0 ? 1 : actualColumns;
                var newPrefWidth = newWidth.intValue() / actualColumns;

                tilepane.setPrefTileWidth(newPrefWidth);
            });
        }
    }

    /**
     * Propogates any changes made to the studyguide to the underlying data source
     * 
     * @param studyGuide the studyguide to save changes for
     */
    public void saveChangesToStudyGuide(DisplayableStudyGuide studyGuide) {
        this.viewmodel.saveChangesToStudyGuide(studyGuide);
    }

    public void setViewmodel(HomeViewmodel viewmodel) {
        this.viewmodel = viewmodel;
        this.addEventListeners();
        this.bindToViewmodel();
    }

    private void bindToViewmodel() {
        this.searchTextField.textProperty().bindBidirectional(this.viewmodel.getSearchProperty());

        var buttonMapping = new HashMap<ToggleButton, ListProperty<DisplayableStudyGuide>>();

        var favoritedGuidesProperty = this.viewmodel.getFavoritedStudyGuidesProperty();
        var downloadedGuidesProperty = this.viewmodel.getDownloadedStudyGuidesProperty();
        var uploadedGuidesProperty = this.viewmodel.getUploadedStudyGuidesProperty();

        buttonMapping.put(this.favoritedToggleButton, favoritedGuidesProperty);
        buttonMapping.put(this.downloadedToggleButton, downloadedGuidesProperty);
        buttonMapping.put(this.uploadedToggleButton, uploadedGuidesProperty);

        var buttons = new ToggleButton[] { this.allToggleButton, this.favoritedToggleButton,
                this.downloadedToggleButton, this.uploadedToggleButton };

        for (var toggleButton : buttons) {
            toggleButton.selectedProperty().addListener((_, _, toggled) -> {
                if (!toggled) {
                    return;
                }

                this.currentlyToggled = toggleButton;
                var associatedList = buttonMapping.get(toggleButton);
                if (associatedList != null) {
                    this.refreshTilePane(this.quickAccessStudyguideTilePane, associatedList);
                } else {
                    var allContent = new HashSet<DisplayableStudyGuide>();

                    allContent.addAll(this.viewmodel.getFavoritedStudyGuidesProperty());
                    allContent.addAll(this.viewmodel.getDownloadedStudyGuidesProperty());
                    allContent.addAll(this.viewmodel.getUploadedStudyGuidesProperty());

                    this.refreshTilePane(this.quickAccessStudyguideTilePane, List.copyOf(allContent));
                }
            });
        }

        this.allToggleButton.setSelected(false);
        this.allToggleButton.setSelected(true);

        var allProps = new ArrayList<ListProperty<DisplayableStudyGuide>>();
        allProps.addAll(buttonMapping.values());
        allProps.add(this.viewmodel.getSearchedStudyGuidesProperty());
        for (var prop : allProps) {
            prop.addListener((_, _, list) -> {
                var updatedList = this.viewmodel.getSearchedStudyGuidesProperty().get();
                this.refreshTilePane(this.searchedStudyguideTilePane, updatedList);
            });
        }
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

    private void addEventListeners() {
        this.addEventHandler(StudyGuideEvent.DOWNLOAD, handler -> {
            this.downloadStudyGuideHandler(handler);
            this.updateCurrentTilePaneDisplay();
        });
        this.addEventHandler(StudyGuideEvent.FAVORITE, handler -> {
            this.favoriteStudyGuideHandler(handler);
            this.updateCurrentTilePaneDisplay();
        });
        this.addEventHandler(StudyGuideEvent.UPLOAD, handler -> {
            this.uploadStudyGuideHandler(handler);
            this.updateCurrentTilePaneDisplay();
        });
    }

    private void downloadStudyGuideHandler(StudyGuideEvent handler) {
        var guide = handler.getStudyGuide();
        var downloading = !guide.getIsDownloaded();
        Runnable resultingAction = () -> this.viewmodel.toggleDownloadStudyGuide(guide, downloading);

        if (!downloading) {
            var header = MessageFormat.format(DELETION_HEADER, guide);
            var content = MessageFormat.format(DELETION_CONTENT, guide);
            var delete = ConfirmationDialog.show(DisplayText.ARE_YOU_SURE, header, content);
            if (!delete) {
                resultingAction = null;
            }
        }

        if (resultingAction != null) {
            resultingAction.run();
        }
    }

    private void favoriteStudyGuideHandler(StudyGuideEvent handler) {
        var guide = handler.getStudyGuide();
        var oppositeIsFavorited = !guide.getIsFavorited();
        this.viewmodel.toggleFavoriteStudyGuide(guide, oppositeIsFavorited);
    }

    private void uploadStudyGuideHandler(StudyGuideEvent handler) {
        var guide = handler.getStudyGuide();
        var uploading = !guide.getIsUploaded();
        Runnable resultingAction = () -> {
            try {
                this.viewmodel.toggleUploadStudyGuide(guide, uploading);
            } catch (SQLException err) {
                this.alertUserOfError(err);
            }
        };

        if (!uploading) {
            var header = MessageFormat.format(DELIST_HEADER, guide);
            var content = MessageFormat.format(DELIST_CONTENT, guide);
            var delist = ConfirmationDialog.show(DisplayText.ARE_YOU_SURE, header, content);
            if (!delist) {
                resultingAction = null;
            }
        }

        if (resultingAction != null) {
            resultingAction.run();
        }
    }

    private void updateCurrentTilePaneDisplay() {
        this.currentlyToggled.setSelected(false);
        this.currentlyToggled.setSelected(true);
    }

    private void alertUserOfError(Exception err) {
        var alert = new Alert(AlertType.ERROR);
        alert.setTitle(err.getClass().getName());
        alert.setContentText(err.getMessage());

        alert.showAndWait();
    }

    @FXML
    private void onInteractableElementEntered(Event handler) {
        var source = handler.getSource();
        if (source instanceof Label label) {
            if (label.getGraphic() instanceof Shape shape) {
                shape.setStroke(Color.BLACK);
            }
        }
    }

    @FXML
    private void onInteractableElementExited(Event handler) {
        var source = handler.getSource();
        if (source instanceof Label label) {
            if (label.getGraphic() instanceof Shape shape) {
                shape.setStroke(Color.TRANSPARENT);
            }
        }
    }

    @FXML
    private void onCreateNewStudyGuideClick() {
        var studyGuide = this.viewmodel.createNewStudyGuide();
        this.fireEvent(new StudyGuideEvent(studyGuide, StudyGuideEvent.START_EDIT));
    }

    @FXML
    private void onSearchButtonClick() {
        this.searchingBorderPane.setVisible(true);
    }

    @FXML
    private void onSearchEntered() {
        try {
            this.viewmodel.searchForStudyguides();
        } catch (SQLException err) {
            var alert = new Alert(AlertType.ERROR);
            alert.setHeaderText("Database error");
            alert.setContentText(err.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    private void onBackButtonClick() {
        this.updateCurrentTilePaneDisplay();
        this.homeBorderPane.setVisible(true);
    }
}
