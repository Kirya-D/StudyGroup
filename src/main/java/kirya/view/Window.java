package kirya.view;

import java.util.ArrayList;
import java.util.List;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.control.ToggleButton;
import kirya.utils.DisplayableStudyGuide;
import kirya.viewmodel.StudyGuideEditorViewmodel;
import kirya.viewmodel.WindowViewmodel;

public class Window {

    @FXML
    private ToggleButton favoritesToggleButton;
    @FXML
    private ListView<DisplayableStudyGuide> favoriteStudyGuidesListView;
    @FXML
    private HomePane homePane;
    @FXML
    private StudyGuideEditor studyGuideEditor;
    @FXML
    private StudyGuideViewer studyGuideViewer;

    private final NodeGroup viewGroup;
    private final WindowViewmodel viewmodel;

    public Window() {
        this.viewGroup = new NodeGroup();
        this.viewmodel = new WindowViewmodel();
        this.viewmodel.load();
    }

    @FXML
    private void initialize() {
        this.bindProperties();
        this.addListeners();
    }

    private void bindProperties() {
        viewGroup.add(this.homePane);
        viewGroup.add(this.studyGuideEditor);
        viewGroup.add(this.studyGuideViewer);

        this.homePane.downloadedStudyGuidesListView.setItems((ObservableList<DisplayableStudyGuide>) this.viewmodel.getDownloadedStudyGuidesProperty());
        this.favoriteStudyGuidesListView
                .setItems((ObservableList<DisplayableStudyGuide>) this.viewmodel.getFavoritedStudyGuidesProperty());
        this.favoriteStudyGuidesListView.visibleProperty().bind(this.favoritesToggleButton.selectedProperty());
        this.favoriteStudyGuidesListView.managedProperty().bind(this.favoriteStudyGuidesListView.visibleProperty());

        this.studyGuideViewer.studyGuideProperty.bind(this.homePane.downloadedStudyGuidesListView.getSelectionModel().selectedItemProperty());

        this.homePane.createNewStudyGuideButton.setOnAction(handler -> {
            this.onCreateNewStudyGuideButtonClick();
        });
        this.homePane.setStudyGuideEditAction(this::onEditStudyGuideButtonClick);
    }

    private void addListeners() {
        this.homePane.downloadedStudyGuidesListView.getSelectionModel().selectedItemProperty()
                .addListener((_, _, newVal) -> {
                    this.studyGuideViewer.setVisible(newVal != null);
                });
        this.homePane.sceneProperty().addListener((_, _, scene) -> {
            if (scene != null) {
                scene.windowProperty().addListener((_, _, window) -> {
                    if (window != null) {
                        this.bindOnApplicationExit(window);
                    }
                });
            }
        });
        this.studyGuideEditor.cancelledEdits.addListener((_, _, newVal) -> {
            if (newVal) {
                this.onHomeButtonClick();
            }
        });
        this.studyGuideEditor.confirmedEdits.addListener((_, _, newVal) -> {
            if (newVal) {
                this.studyGuideEditor.confirmedEdits.set(false);
                this.viewmodel.getCurrentlyEditingStudyGuide().set(false);
                this.onHomeButtonClick();
            }
        });
        this.studyGuideViewer.finishButton.setOnAction(handler -> {
            this.onHomeButtonClick();
            handler.consume();
        });
    }
    
    private void bindOnApplicationExit(javafx.stage.Window window) {
        window.setOnCloseRequest(handler -> {
            this.viewmodel.save();
        });
    }
    
    @FXML
    private void onHomeButtonClick() {
        this.homePane.downloadedStudyGuidesListView.getSelectionModel().select(null);
        this.homePane.setVisible(true);
    }
    
    @FXML
    private void onCreateNewStudyGuideButtonClick() {
        var newEditorViewmodel = this.viewmodel.createNewStudyGuide();
        this.editStudyGuide(newEditorViewmodel);
    }

    private void onEditStudyGuideButtonClick(DisplayableStudyGuide studyGuide) {
        var newEditorViewmodel = this.viewmodel.editStudyGuide(studyGuide);
        this.editStudyGuide(newEditorViewmodel);
    }

    private void editStudyGuide(StudyGuideEditorViewmodel viewmodel) {
        this.viewmodel.getCurrentlyEditingStudyGuide().set(true);
        this.studyGuideEditor.setViewmodel(viewmodel);
        this.studyGuideEditor.setVisible(true);
    }

    private class NodeGroup {

        private final List<Node> nodes;

        public NodeGroup() {
            this.nodes = new ArrayList<>();
        }

        public void add(Node node) {
            if (this.nodes.contains(node)) {
                return;
            }

            this.nodes.add(node);

            node.visibleProperty().addListener((_, _, newVal) -> {
                if (newVal) {
                    this.onNodeMadeVisible(node);
                }
            });
        }
        
        private void onNodeMadeVisible(Node node) {
            for (var iNode : this.nodes) {
                if (iNode != node) {
                    iNode.setVisible(false);
                }
            }
        }
    }
}
