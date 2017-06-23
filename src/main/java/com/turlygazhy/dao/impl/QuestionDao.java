package com.turlygazhy.dao.impl;

import com.turlygazhy.dao.AbstractDao;
import com.turlygazhy.dao.DaoFactory;
import com.turlygazhy.entity.Answer;
import com.turlygazhy.entity.Question;
import com.turlygazhy.entity.QuestionButton;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by daniyar on 23.06.17.
 */
public class QuestionDao extends AbstractDao {
    Connection connection;

    public QuestionDao(Connection connection) {
        this.connection = connection;
    }

    public List<Question> getQuestion(int surveyId) throws SQLException {
        List<Question> questions = new ArrayList<>();
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM QUESTION WHERE SURVEY_ID = ?");
        ps.setInt(1, surveyId);
        ps.execute();
        ResultSet rs = ps.getResultSet();
        while (rs.next()){
            questions.add(parseQuestion(rs));
        }
        return questions;
    }

    public void insertQuestion(Question question) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("INSERT INTO QUESTION (TEXT, SURVEY_ID) VALUES (?, ?)");
        ps.setString(1, question.getText());
        ps.setInt(2, question.getSurveyId());
        ps.executeUpdate();
        ResultSet rs = ps.getGeneratedKeys();
        if (rs.next()){
            question.setId(rs.getInt(1));
        }
        for (QuestionButton button : question.getQuestionButtonList()){
            button.setQuestionId(question.getId());
        }
        DaoFactory.getFactory().getQuestionButtonDao().insertButtons(question.getQuestionButtonList());
    }



    private Question parseQuestion(ResultSet rs) throws SQLException {
        Question question = new Question();
        question.setId(rs.getInt("ID"));
        question.setText(rs.getString("TEXT"));
        List<QuestionButton> buttons = DaoFactory.getFactory().getQuestionButtonDao().getButtons(question.getId());
        question.setQuestionButtonList(buttons);
        List<Answer> answer = DaoFactory.getFactory().getAnswerDao().getAnswers(question.getId());
        question.setAnswer(answer);
        return question;
    }

    public void insertQuestions(List<Question> questions) throws SQLException {
        for (Question question : questions){
            insertQuestion(question);
        }
    }
}
