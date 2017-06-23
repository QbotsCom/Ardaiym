package com.turlygazhy.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by daniyar on 23.06.17.
 */
public class Question {
    int id;
    int surveyId;
    String text;
    List<QuestionButton> questionButtonList;
    List<Answer> answer;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSurveyId() {
        return surveyId;
    }

    public void setSurveyId(int surveyId) {
        this.surveyId = surveyId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<QuestionButton> getQuestionButtonList() {
        return questionButtonList;
    }

    public void setQuestionButtonList(List<QuestionButton> questionButtonList) {
        this.questionButtonList = questionButtonList;
    }

    public List<Answer> getAnswers() {
        return answer;
    }

    public void setAnswer(List<Answer>  answer) {
        this.answer = answer;
    }

    public void addAnswer(Answer answer){
        this.answer.add(answer);
    }

    public void addQuestionButton(QuestionButton button) {
        if (questionButtonList == null){
            questionButtonList = new ArrayList<>();
        }
        questionButtonList.add(button);
    }
}
