package kirya.view;

import java.io.IOException;
import java.util.List;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import kirya.utils.AnswerChoice;
import kirya.utils.DisplayText;
import kirya.utils.DisplayableQuestion;
import kirya.utils.QuestionType;
import kirya.utils.Utils;
import kirya.viewmodel.QuestionEditorViewmodel;

/**
 * Code-behind for questioneditor.fxml
 */
public class QuestionEditor extends VBox {

    @FXML
    private Label headerLabel;
    @FXML
    private Label dropdownLabel;
    @FXML
    private VBox configurationVBox;
    @FXML
    private ComboBox<QuestionType> questionTypeComboBox;
    @FXML
    private TextField questionTextField;
    @FXML
    private TextArea freeResponseAnswerTextArea;
    @FXML
    private TableView<AnswerChoice> multipleChoiceTableView;
    @FXML
    private TableColumn<AnswerChoice, Boolean> correctnessTableColumn;
    @FXML
    private TableColumn<AnswerChoice, String> textTableColumn;
    @FXML
    private TableColumn<AnswerChoice, String> deletionTableColumn;
    @FXML
    private Button multipleChoiceNewChoiceButton;

    private QuestionEditorViewmodel viewmodel = new QuestionEditorViewmodel();

    /**
     * Initializes a new QuestionEditor component.
     */
    public QuestionEditor() {
        var loader = new FXMLLoader(this.getClass().getResource("questioneditor.fxml"));
        loader.setController(this);
        loader.setRoot(this);
        try {
            loader.load();
            this.setupTableView();
            this.bindToViewmodel();
            this.bindToSelf();
            this.setupComboBox();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setupTableView() {
        this.correctnessTableColumn.setCellValueFactory(data -> data.getValue().isCorrectProperty());
        this.textTableColumn.setCellValueFactory(data -> data.getValue().textProperty());
        this.deletionTableColumn.setCellValueFactory(data -> data.getValue().textProperty());

        this.correctnessTableColumn.setCellFactory(CheckBoxTableCell.forTableColumn(index -> {
            var prop = this.multipleChoiceTableView.getItems().get(index).isCorrectProperty();
            prop.addListener((_, _, _) -> this.applyQuestionChanges());
            return prop;
        }));
        this.textTableColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        this.deletionTableColumn.setCellFactory(listview -> new TableCell<AnswerChoice, String>() {
            private Button deleteButton = new Button("🗑");
            {
                deleteButton.prefWidthProperty().bind(widthProperty());
                deleteButton.maxWidth(Double.MAX_VALUE);
                deleteButton.setOnAction(handler -> {
                    startEdit();
                    commitEdit("Delete");
                });
            }

            @Override
            public void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(null);
                    setGraphic(deleteButton);
                }
            }
        });

        this.textTableColumn.setOnEditCommit(handler -> {
            var newestValue = handler.getNewValue();
            handler.getRowValue().setText(newestValue);
            this.applyQuestionChanges();
        });
        this.deletionTableColumn.setOnEditCommit(handler -> {
            var value = handler.getRowValue();
            this.multipleChoiceTableView.getItems().remove(value);
            this.applyQuestionChanges();
        });
    }

    private void bindToSelf() {
        Node[] collapsable = { this.configurationVBox, this.freeResponseAnswerTextArea, this.multipleChoiceTableView,
                this.multipleChoiceNewChoiceButton };
        for (var node : collapsable) {
            node.managedProperty().bind(node.visibleProperty());
        }

        this.questionTypeComboBox.valueProperty().addListener((_, _, newType) -> changeElementVisibility());

        var listenedFocusNodes = List.of(this.questionTextField, this.freeResponseAnswerTextArea);
        for (var focusNode : listenedFocusNodes) {
            focusNode.focusedProperty().addListener((_, _, focused) -> {
                if (!focused) {
                    this.applyQuestionChanges();
                }
            });
        }
    }

    private void changeElementVisibility() {
        var questionType = this.questionTypeComboBox.valueProperty().get();
        switch (questionType) {
            case FREE_RESPONSE -> {
                this.freeResponseAnswerTextArea.setVisible(true);
                this.multipleChoiceTableView.setVisible(false);
                this.multipleChoiceNewChoiceButton.setVisible(false);
            }
            case MULTIPLE_CHOICE -> {
                this.freeResponseAnswerTextArea.setVisible(false);
                this.multipleChoiceTableView.setVisible(true);
                this.multipleChoiceNewChoiceButton.setVisible(true);
            }
        }
    }

    private void bindToViewmodel() {
        this.headerLabel.textProperty().bind(this.viewmodel.getQuestionProperty());
        this.questionTypeComboBox.valueProperty().bindBidirectional(this.viewmodel.getQuestionTypeProperty());
        this.questionTextField.textProperty().bindBidirectional(this.viewmodel.getQuestionProperty());

        this.freeResponseAnswerTextArea.textProperty().bindBidirectional(this.viewmodel.getAnswerProperty());
        this.multipleChoiceTableView.setItems(this.viewmodel.getMultChoiceOptionsObservableList());
    }

    private void setupComboBox() {
        this.questionTypeComboBox.getItems().setAll(QuestionType.values());
        this.questionTypeComboBox.setConverter(new StringConverter<QuestionType>() {
            @Override
            public QuestionType fromString(String string) {
                return QuestionType.getTypeFromName(string);
            }

            @Override
            public String toString(QuestionType questionType) {
                return QuestionType.getNameFromType(questionType);
            }

        });
    }

    /**
     * Sets the question to edit.
     *
     * @param question The new question to edit
     */
    public void setQuestion(DisplayableQuestion question) {
        this.viewmodel.getQuestionObjectProperty().set(question);
    }

    private void applyQuestionChanges() {
        Alert alert = null;

        try {
            this.viewmodel.applyQuestionChanges();
        } catch (IllegalArgumentException e) {
            var warningMessage = Utils.capitalizeString(e.getMessage());
            alert = new Alert(Alert.AlertType.WARNING);
            alert.setHeaderText(warningMessage);
        } catch (NullPointerException e) {
            var errorMessage = Utils.capitalizeString(e.getMessage());
            alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(errorMessage);
        }

        var finalAlert = alert;
        if (finalAlert != null) {
            this.viewmodel.syncPropertiesToQuestionState();
            Runnable modalAlert = () -> finalAlert.showAndWait();
            Platform.runLater(modalAlert);
        }
    }

    @FXML
    private void onNewMultipleChoiceOptionButtonClick() {
        var choiceNum = this.multipleChoiceTableView.getItems().size() + 1;
        var text = "Choice " + choiceNum;
        var newChoice = new AnswerChoice(text, true);

        this.multipleChoiceTableView.getItems().add(newChoice);
        this.applyQuestionChanges();
    }

    @FXML
    private void onTrashButtonClick() {
        var parent = this.getParent();
        var success = false;
        while (parent != null && !success) {
            if (parent instanceof ListCell listCell) {
                var associatedQuestion = this.viewmodel.getQuestionObjectProperty().get();
                listCell.getListView().getItems().remove(associatedQuestion);
                success = true;
            }
            parent = parent.getParent();
        }
    }

    @FXML
    private void onDropdownLabelClick() {
        this.configurationVBox.setVisible(!this.configurationVBox.isVisible());
        var dropdownText = this.configurationVBox.isVisible() ? DisplayText.TRIANGLE_UP : DisplayText.TRIANGLE_DOWN;
        this.dropdownLabel.setText(dropdownText);
    }

}
