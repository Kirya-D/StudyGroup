package kirya.view;

import java.io.IOException;
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
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;
import kirya.utils.DisplayText;
import kirya.utils.DisplayableStudyGuide;
import kirya.utils.SessionData;
import kirya.view.enums.Page;
import kirya.view.events.PageRequestEvent;
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

    private static final String HEADER_STRING = "{0} Dashboard";
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
            var username = SessionData.getLoggedInUsername();
            var possessiveUsername = username.endsWith("s") ? username + "'" : username + "'s";
            var header = MessageFormat.format(HEADER_STRING, possessiveUsername);
            this.headerLabel.setText(header);
            this.updateDashboardDisplay();
            this.updateSearchedDisplay();
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
        this.searchScrollPane.addEventFilter(ScrollEvent.ANY, scrollEvent -> {
            this.getMoreGuidesAtBottomOfDisplay();
        });
        this.searchScrollPane.vvalueProperty().addListener((_, _, _) -> {
            this.getMoreGuidesAtBottomOfDisplay();
        });

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
                this.updateSearchedDisplay();
            });
        }
    }

    private void getMoreGuidesAtBottomOfDisplay() {
        var scrolledToBottom = this.searchScrollPane.getVvalue() == this.searchScrollPane.getVmax();
        var haveNowhereToScroll = this.searchScrollPane.getViewportBounds().getHeight() <= this.searchScrollPane
                .getHeight();
        if (scrolledToBottom || haveNowhereToScroll) {
            try {
                this.viewmodel.attemptGetMoreResults();
            } catch (IOException | InterruptedException err) {
                this.alertUserOfError(err);
            }
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
            this.updateDashboardDisplay();
        });
        this.addEventHandler(StudyGuideEvent.FAVORITE, handler -> {
            this.favoriteStudyGuideHandler(handler);
            this.updateDashboardDisplay();
        });
        this.addEventHandler(StudyGuideEvent.UPLOAD, handler -> {
            this.uploadStudyGuideHandler(handler);
            this.updateDashboardDisplay();
        });
    }

    private void downloadStudyGuideHandler(StudyGuideEvent handler) {
        var guide = handler.getStudyGuide();
        var downloading = !guide.getDownloaded();
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
        var oppositeIsFavorited = !guide.getFavorited();
        this.viewmodel.toggleFavoriteStudyGuide(guide, oppositeIsFavorited);
    }

    private void uploadStudyGuideHandler(StudyGuideEvent handler) {
        var guide = handler.getStudyGuide();
        var uploading = !guide.getUploaded();
        Runnable resultingAction = () -> {
            try {
                this.viewmodel.toggleUploadStudyGuide(guide, uploading);
            } catch (IOException | InterruptedException err) {
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

    private void updateDashboardDisplay() {
        this.currentlyToggled.setSelected(false);
        this.currentlyToggled.setSelected(true);
    }

    private void updateSearchedDisplay() {
        var updatedList = this.viewmodel.getSearchedStudyGuidesProperty().get();
        this.refreshTilePane(this.searchedStudyguideTilePane, updatedList);
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
    private void onLogOutClick() {
        try {
            this.viewmodel.logOut();
            var pageRequestEvent = new PageRequestEvent(Page.LOGIN);
            this.fireEvent(pageRequestEvent);
        } catch (IOException | InterruptedException err) {
            this.alertUserOfError(err);
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
        } catch (IOException | InterruptedException err) {
            this.alertUserOfError(err);
        }
    }

    @FXML
    private void onBackButtonClick() {
        this.updateDashboardDisplay();
        this.homeBorderPane.setVisible(true);
    }

    private void alertUserOfError(Exception err) {
        var alert = new Alert(AlertType.ERROR);
        alert.setTitle(err.getClass().getName());
        alert.setContentText(err.getMessage());

        alert.showAndWait();
    }
}
