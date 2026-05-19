package kirya.viewmodel;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import kirya.model.Question;
import kirya.model.StudyGuide;

public class TestStudyGuideEditorViewmodel {

    @Nested
    public class TestConstructor {

        @Test
        public void testParameterlessDefaultMemberValues() {
            var viewmodel = new StudyGuideEditorViewmodel();

            var expectedStudyGuideObjectType = ObjectProperty.class;
            var actualStudyGuideObject = viewmodel.getStudyGuideProperty();
            var expectedTitleType = StringProperty.class;
            var actualTitleProperty = viewmodel.getTitleProperty();
            var expectedDescriptionType = StringProperty.class;
            var actualDescription = viewmodel.getDescriptionProperty();
            var expectedQuestionsType = ObservableList.class;
            var actualQuestions = viewmodel.getQuestionsObservableList();

            assertAll("Members",
                    () -> assertInstanceOf(expectedStudyGuideObjectType, actualStudyGuideObject),
                    () -> assertInstanceOf(expectedTitleType, actualTitleProperty),
                    () -> assertInstanceOf(expectedDescriptionType, actualDescription),
                    () -> assertInstanceOf(expectedQuestionsType, actualQuestions));
        }
    }

    @Nested
    public class TestSyncPropertiesToStudyGuideState {
        
        @Test
        public void testWhenStudyGuideIsNotNull() {
            var studyGuide = new StudyGuide();
            studyGuide.setTitle("Unique Study Guide Title");
            studyGuide.setDescription("Even more unique description");
            studyGuide.setQuestions(List.of(new Question(), new Question()));
            var viewmodel = new StudyGuideEditorViewmodel();
            viewmodel.getStudyGuideProperty().set(studyGuide);

            var expectedTitle = studyGuide.getTitle();
            var expectedDescription = studyGuide.getDescription();
            var expectedQuestions = studyGuide.getQuestions();

            viewmodel.syncPropertiesToStudyGuideState();

            var actualTitle = viewmodel.getTitleProperty().get();
            var actualDescription = viewmodel.getDescriptionProperty().get();
            var actualQuestions = new ArrayList<>(viewmodel.getQuestionsObservableList());

            assertAll("Member checks", 
                () -> assertEquals(expectedTitle, actualTitle),
                () -> assertEquals(expectedDescription, actualDescription),
                () -> assertEquals(expectedQuestions, actualQuestions)
            );
        }

        @Test
        public void testWhenStudyGuideIsNull() {
            var viewmodel = new StudyGuideEditorViewmodel();

            var expectedTitle = "";
            var expectedDescription = "";
            var expectedQuestions = Collections.emptyList();

            viewmodel.syncPropertiesToStudyGuideState();

            var actualTitle = viewmodel.getTitleProperty().get();
            var actualDescription = viewmodel.getDescriptionProperty().get();
            var actualQuestions = new ArrayList<>(viewmodel.getQuestionsObservableList());

            assertAll("Member checks", 
                () -> assertEquals(expectedTitle, actualTitle),
                () -> assertEquals(expectedDescription, actualDescription),
                () -> assertEquals(expectedQuestions, actualQuestions)
            );
        }
    }

    @Nested
    public class TestAddNewQuestion {

        StudyGuideEditorViewmodel viewmodel;

        @BeforeEach
        public void setup() {
            this.viewmodel = new StudyGuideEditorViewmodel();
        }

        @Test
        public void testWhenQuestionsEmpty() {
            this.viewmodel.addNewQuestion();
            var questionsList = this.viewmodel.getQuestionsObservableList();

            var expectedSize = 1;
            var actualSize = questionsList.size();
            var expectedFirstItemQuestion = "Question 1";
            var actualFirstItemQuestion = questionsList.getFirst().getQuestion();
            var expectedLastItemQuestion = "Question 1";
            var actualLastItemQuestion = questionsList.getLast().getQuestion();

            assertAll("Collection check",
                    () -> assertEquals(expectedSize, actualSize),
                    () -> assertEquals(expectedFirstItemQuestion, actualFirstItemQuestion),
                    () -> assertEquals(expectedLastItemQuestion, actualLastItemQuestion));
        }

        @Test
        public void testWhenOneQuestionAlreadyAdded() {
            this.viewmodel.addNewQuestion();
            this.viewmodel.addNewQuestion();
            var questionsList = this.viewmodel.getQuestionsObservableList();

            var expectedSize = 2;
            var actualSize = questionsList.size();
            var expectedFirstItemQuestion = "Question 1";
            var actualFirstItemQuestion = questionsList.getFirst().getQuestion();
            var expectedLastItemQuestion = "Question 2";
            var actualLastItemQuestion = questionsList.getLast().getQuestion();

            assertAll("Collection check",
                    () -> assertEquals(expectedSize, actualSize),
                    () -> assertEquals(expectedFirstItemQuestion, actualFirstItemQuestion),
                    () -> assertEquals(expectedLastItemQuestion, actualLastItemQuestion));
        }

        @Test
        public void testWhenSomeQuestionsAlreadyAdded() {
            this.viewmodel.addNewQuestion();
            this.viewmodel.addNewQuestion();
            this.viewmodel.addNewQuestion();
            var questionsList = this.viewmodel.getQuestionsObservableList();

            var expectedSize = 3;
            var actualSize = questionsList.size();
            var expectedFirstItemQuestion = "Question 1";
            var actualFirstItemQuestion = questionsList.getFirst().getQuestion();
            var expectedLastItemQuestion = "Question 3";
            var actualLastItemQuestion = questionsList.getLast().getQuestion();

            assertAll("Collection check",
                    () -> assertEquals(expectedSize, actualSize),
                    () -> assertEquals(expectedFirstItemQuestion, actualFirstItemQuestion),
                    () -> assertEquals(expectedLastItemQuestion, actualLastItemQuestion)
            );
        }
    }

    @Nested
    public class TestApplyStudyGuideChanges {

        StudyGuideEditorViewmodel viewmodel;

        @BeforeEach
        public void setup() {
            this.viewmodel = new StudyGuideEditorViewmodel();
            this.viewmodel.getStudyGuideProperty().set(new StudyGuide());
        }

        @Test
        public void testWhenSuccessful() {
            var expectedTitle = "Valid Title";
            var expectedDescription = "Meh";
            var expectedQuestions = List.of(new Question(), new Question());

            this.viewmodel.getTitleProperty().set(expectedTitle);
            this.viewmodel.getDescriptionProperty().set(expectedDescription);
            this.viewmodel.getQuestionsObservableList().setAll(expectedQuestions);
            this.viewmodel.applyStudyGuideChanges();

            var studyGuide = this.viewmodel.getStudyGuideProperty().get();
            var actualTitle = studyGuide.getTitle();
            var actualDescription = studyGuide.getDescription();
            var actualQuestions = studyGuide.getQuestions();

            assertAll("Member checks", 
                () -> assertEquals(expectedTitle, actualTitle),
                () -> assertEquals(expectedDescription, actualDescription),
                () -> assertEquals(expectedQuestions, actualQuestions)
            );
        }

        @Test
        public void throwsWhenNullStudyGuide() {
            this.viewmodel.getStudyGuideProperty().set(null);

            assertThrows(NullPointerException.class, () -> {
                this.viewmodel.applyStudyGuideChanges();
            });
        }

        @Test
        public void throwsWhenTitleIsBlank() {
            this.viewmodel.getTitleProperty().set("");

            assertThrows(IllegalArgumentException.class, () -> {
                this.viewmodel.applyStudyGuideChanges();
            });
        }

        @Test
        public void throwsWhenQuestionsIsEmpty() {
            this.viewmodel.getTitleProperty().set("Valid Title");
            this.viewmodel.getQuestionsObservableList().clear();

            assertThrows(IllegalArgumentException.class, () -> {
                this.viewmodel.applyStudyGuideChanges();
            });
        }
    }
}
