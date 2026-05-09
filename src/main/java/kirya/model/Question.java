package kirya.model;

import java.util.ArrayList;
import java.util.List;

import kirya.utils.DisplayableQuestion;
import kirya.utils.QuestionType;

public class Question extends DisplayableQuestion {

        private QuestionType questionType;
        private String question;
        private List<String> answerChoices;
        private List<String> correctChoices;

        public Question() {
            this.questionType = QuestionType.FREE_RESPONSE;
            this.question = "";
            this.answerChoices = new ArrayList<>();
            this.correctChoices = new ArrayList<>();
        }


        public void setQuestionType(QuestionType questionType) {
            this.questionType = questionType;
        }

        public void setQuestion(String question) {
            this.question = question;
        }

        public void setAnswerChoices(List<String> answerChoices) {
            this.answerChoices = answerChoices;
        }

        public void setCorrectAnswers(List<String> correctAnswers) {
            this.correctChoices = correctAnswers;
        }

        @Override
        public QuestionType getQuestionType() {
            return this.questionType;
        }

        @Override
        public String getQuestion() {
            return this.question;
        }

        @Override
        public List<String> getChoices() {
            return this.answerChoices;
        }

        @Override
        public List<String> getAnswers() {
            return this.correctChoices;
        }

    }
