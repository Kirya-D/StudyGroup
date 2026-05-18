package kirya.viewmodel;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;

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
    public class TestApplyChanges {

        StudyGuideEditorViewmodel viewmodel;

        @BeforeEach
        public void setup() {
            this.viewmodel = new StudyGuideEditorViewmodel();
        }

        @Test
        public void testWhenNullStudyGuide() {
            var beforeAdd = new ArrayList<>(this.viewmodel.getQuestionsObservableList());

            this.viewmodel.applyChanges();

            var afterAdd = new ArrayList<>(this.viewmodel.getQuestionsObservableList());

            assertEquals(beforeAdd, afterAdd);
        }
    }
}
